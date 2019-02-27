package app.springrestful.member.interfaces.controller;

import app.springrestful.config.WebConfig;
import app.springrestful.member.interfaces.model.MemberDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.snippet.Attributes;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by Lee Tae Su on 2019-02-21.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class MemberCreateControllerTest {

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
    public void 생성_테스트() throws Exception {
        //given
        MemberDto.MemberCreateRequest request
                = new MemberDto.MemberCreateRequest("taesu", "LeeTaeSu");

        //when
        ResultActions perform = this.mockMvc.perform(
                post("/members")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON_VALUE)
        );

        //then
        perform.andExpect(status().isCreated())
                .andDo(print())
                .andDo(this.handler.document(
                        requestFields(
                                fieldWithPath("memberId").type(JsonFieldType.STRING)
                                        .attributes(getFormatAttribute("아이디 포맷")).description("아이디"),
                                fieldWithPath("name").type(JsonFieldType.STRING).description("이름")
                        ),
                        links(
                                halLinks(),
                                linkWithRel("self").description("Self description")
                        ),
                        responseFields(
                                fieldWithPath("memberKey").type(JsonFieldType.NUMBER).description("키"),
                                fieldWithPath("memberId").type(JsonFieldType.STRING).description("아이디"),
                                fieldWithPath("name").type(JsonFieldType.STRING).description("이름"),
                                fieldWithPath("joinedAt").type(JsonFieldType.STRING).description("가입일"),
                                subsectionWithPath("_links").ignored()
                        ))
                )
                .andReturn()
                .getResponse();


    }
    
    private Attributes.Attribute getFormatAttribute(String value){
        return new Attributes.Attribute("format", value);
    }

}