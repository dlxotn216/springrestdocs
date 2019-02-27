package app.springrestful.member.interfaces.controller;

import app.springrestful.member.domain.model.Member;
import app.springrestful.member.domain.repository.MemberRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by Lee Tae Su on 2019-02-26.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class MemberSearchControllerTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Rule
    public JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation();

    private RestDocumentationResultHandler handler;


    @Before
    public void onBeforeTest() {
        this.handler = document(
                "{class-name}/{method-name}",
                preprocessResponse(prettyPrint())
        );


        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext)
                .apply(documentationConfiguration(this.restDocumentation)
                               .snippets()
                               .withEncoding("UTF-8")
                               .and()
                               .uris().withScheme("https")
                               .withHost("docs.example.io")
                               .withPort(443))
                .alwaysDo(this.handler)
                .build();
    }

    @Test
    public void 페이징_테스트() throws Exception {
        //Given
        this.memberRepository.saveAll(IntStream.rangeClosed(1, 100)
                                              .mapToObj(idx -> new Member("taesu" + idx, "Lee Tae Su" + idx))
                                              .collect(Collectors.toList()));


        //When
        MockHttpServletResponse response
                = this.mockMvc.perform(MockMvcRequestBuilders.get("/members?page=1&size=3")
                                               .accept(MediaTypes.HAL_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(this.handler.document(
                        requestParameters(
                                parameterWithName("page").description("페이지 번호(zero based)"),
                                parameterWithName("size").description("조회할 개수")
                        ),
                        responseFields(
                                fieldWithPath("_embedded.members[].memberKey").type(JsonFieldType.NUMBER).description("사용자 key"),
                                fieldWithPath("_embedded.members[].memberId").type(JsonFieldType.STRING).description("사용자 아이디"),
                                fieldWithPath("_embedded.members[].name").type(JsonFieldType.STRING).description("사용자 이름"),
                                fieldWithPath("_embedded.members[].joinedAt").type(JsonFieldType.STRING).description("사용자 가입일"),
                                fieldWithPath("_embedded.members[]._links").type(JsonFieldType.OBJECT).description("Entity links"),
                                fieldWithPath("_embedded.members[]._links.self").type(JsonFieldType.OBJECT).description("Self description"),
                                fieldWithPath("_embedded.members[]._links.self.*").ignored(),
                                fieldWithPath("page.totalElements").type(JsonFieldType.NUMBER).description("전체 엘리먼트 개수"),
                                fieldWithPath("page.totalPages").type(JsonFieldType.NUMBER).description("총 페이지 수"),
                                fieldWithPath("page.first").type(JsonFieldType.BOOLEAN).description("첫번째 페이지 인지 여부"),
                                fieldWithPath("page.last").type(JsonFieldType.BOOLEAN).description("마지막 페이지 인지 여부"),
                                fieldWithPath("page.empty").type(JsonFieldType.BOOLEAN).description("비어있는지 여부"),
                                fieldWithPath("page.number").type(JsonFieldType.NUMBER).description("현재 페이지 번호"),
                                fieldWithPath("page.size").type(JsonFieldType.NUMBER).description("현재 조회 요청 개수"),
                                subsectionWithPath("_links").ignored()
                        ),
                        links(
                                halLinks(),
                                linkWithRel("self").description("목록 조회")
                        ))
                )
                .andReturn()
                .getResponse();
    }
}