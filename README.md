# Spring Rest Docs

### Swagger vs Spring Rest Docs
코드 레벨의 문서화는 javadoc 표준을 따라 개발자가 잘 작성하면 된다.  
그렇다면 API에 대한 문서화는 어떻게 진행해야하나?   

이전까지는 Swagger를 통해 문서화를 진행했으나 아래와 같이  
몇 가지 단점을 발견되어 실제 적용이 힘들었다.  
(1) Command Object에 객체 중첩 레벨이 많으면 생각보다 build에 시간이 오래걸린다  
(2) 오랜 build 끝에 생성 된 json 객체를 swaager-ui를 통해 import 하는 도중에 웹 페이지가 죽는 현상이 잦다  
(3) Swagger를 위해 코드의 변경이 필요하다. Annotation을 통해 작성하므로 개발자 입장에선 가독성이 떨어지는 문제가 있다.   
(4) 해당 API가 제대로 동작하는지 알 수 없다. (테스트를 기반으로 하지 않으므로)  
  
 여기서 소개하는 Spring Rest Docs는 위에 단점들을 충분히 커버할 수 있다

### 프로젝트 설정

String Boot 기반으로 아래와 같이 설정
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-hateoas</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-devtools</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.restdocs</groupId>
            <artifactId>spring-restdocs-mockmvc</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <artifactId>maven-resources-plugin</artifactId>
                <version>2.7</version>
                <executions>
                    <execution>
                        <id>copy-resources</id>
                        <phase>prepare-package</phase>
                        <goals>
                            <goal>copy-resources</goal>
                        </goals>
                        <configuration>
                            <outputDirectory>
                                ${project.build.outputDirectory}/static/docs
                            </outputDirectory>
                            <resources>
                                <resource>
                                    <directory>
                                        ${project.build.directory}/generated-docs
                                    </directory>
                                </resource>
                            </resources>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
            <plugin>
                <groupId>org.asciidoctor</groupId>
                <artifactId>asciidoctor-maven-plugin</artifactId>
                <version>1.5.3</version>
                <executions>
                    <execution>
                        <id>generate-docs</id>
                        <phase>prepare-package</phase>
                        <goals>
                            <goal>process-asciidoc</goal>
                        </goals>
                        <configuration>
                            <backend>html</backend>
                            <doctype>book</doctype>
                        </configuration>
                    </execution>
                </executions>
                <dependencies>
                    <dependency>
                        <groupId>org.springframework.restdocs</groupId>
                        <artifactId>spring-restdocs-asciidoctor</artifactId>
                        <version>${spring-restdocs.version}</version>
                    </dependency>
                </dependencies>
            </plugin>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>

</project>

```

### MockMVC를 이용한 테스트 환경
Spring Rest Docs에선 MockMVC와 Rest Assured를 통해서 문서화할 방법을 선택할 수 있다.
여기에선 MockMVC를 이용하는데 MockMVC를 통한 컨트롤러 단위 테스트가 아닌 
컨트롤러 통합 테스트를 통해서 진행한다.

컨트롤러 단위 테스트는 Controller가 참조하는 Service를 Mocking하여 주입 후 진행하는 방식이다.
반면 컨트롤러 통합 테스트는 컨트롤러부터 시작하는 전체 통합테스트라고 생각하면 된다.  

여기에선 API의 모든 테스트가 검증된 이후 Stable 한 상태의 API에 대해서만 문서화를  
진행한다고 가정하여 진행할 것이므로 MockMVC를 이용한 컨트롤러 통합 테스트를 이용한  
방법을 선택한다.


### AsciiDoc vs Markdown
문서화하는 템플릿엔진에 대해 두 방식을 지원하는데 AsciiDoc이 조금 더 나에게 친숙하였다.  
또한 <a href="https://jojoldu.tistory.com/289">Markdown을 이용한 Spring Rest Docs</a>  글을 보면 너무 불편하단다...  


### 문서화를 위한 Restful API 구현
(1) 사용자 생성

아래와 같은 RestController와 그에 따른 Service를 구현한다

```java
@RestController
public class MemberCreateController {
    
    private MemberCreateService memberCreateService;

    public MemberCreateController(MemberCreateService memberCreateService) {
        this.memberCreateService = memberCreateService;
    }

    @PostMapping("/members")
    public ResponseEntity<MemberDto.MemberCreateResponse> createMember(
            @RequestBody MemberDto.MemberCreateRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(this.memberCreateService.create(request));
    }
}

```

MemberCreateService의 Input은 DTO이며 마찬가지로 Output도 DTO이다.
(Domain service가 아닌 Application service임을 주의)
```java
@Service
public class MemberCreateService {
    private MemberRepository repository;

    public MemberCreateService(MemberRepository repository) {
        this.repository = repository;
    }

    public MemberDto.MemberCreateResponse create(MemberDto.MemberCreateRequest request) {
        return MemberDto.asCreateResponse(
                request, 
                this.repository.save(MemberDto.asMember(request)));
    }
}

```

그 후 아래와 같이 MemberCreateController를 테스트하는 클래스를 작성한다
 ```java
@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class MemberCreateControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;
    
    @Rule
    public JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation();
    
    private MockMvc mockMvc;

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
```

주의점은 Hateoas 스펙메 맞게 추가한 _links 필드는 response field에서 명시적으로 ignore 선언을 해주어야 한다는 것이다.  
hanLinks()는 HAL 스펙임을 알리는 것인데 Response header의 Content-Type이 HAL json이면 자동으로 활성화 된다. 

또한 addAttributes 메소드를 통해 format이라는 Custom field를 추가했으므로  
이러한 Custom field를 인식할 수 있도록 test resource 디렉토리 하위에 org/springframework/restdocs/templates/asciidoctor   디렉터리를
만들고 아래의 snippet을 추가한다  
```text
|===
|Field name|Type|Required|Format|Description


{{#fields}}
|{{#tableCellContent}}`+{{path}}+`{{/tableCellContent}}
|{{#tableCellContent}}`+{{type}}+`{{/tableCellContent}}
|{{#tableCellContent}}{{^optional}}true{{/optional}}{{/tableCellContent}}
|{{#tableCellContent}}{{#format}}{{.}}{{/format}}{{/tableCellContent}} 
|{{#tableCellContent}}{{description}}{{/tableCellContent}}

{{/fields}}

|===
```


위의 테스트를 실행하면 아래와 같이 target/generated-snippets/member-create-controller-test 디렉토리 하위에 Snippet들이 생성된다.  
<img width="400" src="https://raw.githubusercontent.com/dlxotn216/springrestdocs/master/src/main/resources/images/create-test-generated-snippets.png" >

 
(2) 사용자 조회

아래와 같이 사용자의 목록을 조회하는 api를 작성한다  
여기에 사용된 Spring Data JPA의 Pageable, Spring hateoas의 PagedResources에 대한 설명은 하지 않는다. 

```java
@RestController
public class MemberSearchController {

    private MemberSearchService memberSearchService;

    public MemberSearchController(MemberSearchService memberSearchService) {
        this.memberSearchService = memberSearchService;
    }

    @GetMapping("/members")
    public ResponseEntity<PagedResources<MemberDto.MemberSearchResponse>> searchMembers(Pageable pageable) {
        Page<MemberDto.MemberSearchResponse> page = this.memberSearchService.searchMember(pageable);
        PagedResources<MemberDto.MemberSearchResponse> body
                = new PagedResources<>(page.getContent(),
                                       new CustomPageMetadata(page),
                                       ControllerLinkBuilder.linkTo(ControllerLinkBuilder.methodOn(
                                               this.getClass()).searchMembers(pageable)).withSelfRel());
        return ResponseEntity.ok(body);
    }
}
```

위 컨트롤러에 대한 테스트를 작성한다
```java
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
                                fieldWithPath("_embedded.members[].memberKey")
                                    .type(JsonFieldType.NUMBER).description("사용자 key"),
                                fieldWithPath("_embedded.members[].memberId")
                                    .type(JsonFieldType.STRING).description("사용자 아이디"),
                                fieldWithPath("_embedded.members[].name")
                                    .type(JsonFieldType.STRING).description("사용자 이름"),
                                fieldWithPath("_embedded.members[].joinedAt")
                                    .type(JsonFieldType.STRING).description("사용자 가입일"),
                                fieldWithPath("_embedded.members[]._links")
                                    .type(JsonFieldType.OBJECT).description("Entity links"),
                                fieldWithPath("_embedded.members[]._links.self")
                                    .type(JsonFieldType.OBJECT).description("Self description"),
                                fieldWithPath("_embedded.members[]._links.self.*").ignored(),
                                fieldWithPath("page.totalElements")
                                    .type(JsonFieldType.NUMBER).description("전체 엘리먼트 개수"),
                                fieldWithPath("page.totalPages")
                                    .type(JsonFieldType.NUMBER).description("총 페이지 수"),
                                fieldWithPath("page.first")
                                    .type(JsonFieldType.BOOLEAN).description("첫번째 페이지 인지 여부"),
                                fieldWithPath("page.last")
                                    .type(JsonFieldType.BOOLEAN).description("마지막 페이지 인지 여부"),
                                fieldWithPath("page.empty")
                                    .type(JsonFieldType.BOOLEAN).description("비어있는지 여부"),
                                fieldWithPath("page.number")
                                    .type(JsonFieldType.NUMBER).description("현재 페이지 번호"),
                                fieldWithPath("page.size")
                                    .type(JsonFieldType.NUMBER).description("현재 조회 요청 개수"),
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
```

주의할 점은 생성 API와 마찬가지로 _links, _links.self, _links.self.*에 대한 경로를 명시적으로 ignore 해주려 했으나 실패했고  
field로 처리하여 명시했다는 점이다.  
_embedded.members[]._links, _embedded.members[]._links.self, _embedded.members[]._links.self.* 등을 명시적으로 무시하도록 하면  
response에 있는 필드가 문서화 되지 않았다거나, 명시한 경로는 없다거나 등의 갖가지 에러가 났다.  

`정신건강을 위해 어느정도 타협하기로 했다.`
  
위의 테스트를 실행하면 아래와 같이 target/generated-snippets/member-search-controller-test 디렉토리 하위에 Snippet들이 생성된다.  
<img width="400" src="https://raw.githubusercontent.com/dlxotn216/springrestdocs/master/src/main/resources/images/search-test-generated-snippets.png" >


### Rest docs 생성
(1) AsciiDoc 템플릿 작성  
src/main/asciidoc 디렉터리 하위에 아래와 같은 파일을 위치시킨다  
HTML 파일을 만들기 위한 템플릿이라고 생각하면 된다.  

```text
= RESTful Notes API Guide
Lee Tae Su;
:doctype: book
:icons: font
:source-highlighter: highlightjs
:toc: left
:toclevels: 4
:sectlinks:

[[overview]]
= Overview

[[overview-http-verbs]]
== HTTP verbs

RESTful notes tries to adhere as closely as possible to standard HTTP and REST conventions in its
use of HTTP verbs.

|===
| Verb | Usage

| `GET`
| 조회

| `POST`
| 생성

| `PUT`
| 전체변경

| `PATCH`
| 일부변경

| `DELETE`
| 삭제
|===

[[overview-http-status-codes]]
== HTTP status codes

RESTful notes tries to adhere as closely as possible to standard HTTP and REST conventions in its
use of HTTP status codes.

|===
| Status code | Usage

| `200 OK`
| The request completed successfully

| `201 Created`
| A new resource has been created successfully. The resource's URI is available from the response's
`Location` header

| `204 No Content`
| An update to an existing resource has been applied successfully

| `400 Bad Request`
| The request was malformed. The response body will include an error providing further information

| `404 Not Found`
| The requested resource did not exist
|===

= Member

== Member 생성

Http Response
include::{snippets}/member-create-controller-test/생성_테스트/http-response.adoc[]

Request fields 
include::{snippets}/member-create-controller-test/생성_테스트/request-fields.adoc[]

Response fields
include::{snippets}/member-create-controller-test/생성_테스트/response-fields.adoc[]

Links
include::{snippets}/member-create-controller-test/생성_테스트/links.adoc[]

Curl request
include::{snippets}/member-create-controller-test/생성_테스트/curl-request.adoc[]

Http request
include::{snippets}/member-create-controller-test/생성_테스트/http-request.adoc[]

Httpie request
include::{snippets}/member-create-controller-test/생성_테스트/httpie-request.adoc[]

Request body
include::{snippets}/member-create-controller-test/생성_테스트/request-body.adoc[]

Response body
include::{snippets}/member-create-controller-test/생성_테스트/response-body.adoc[]

== Member 조회

Http Response
include::{snippets}/member-search-controller-test/페이징_테스트/http-response.adoc[]

Request Parameters
include::{snippets}/member-search-controller-test/페이징_테스트/request-parameters.adoc[]

Response fields
include::{snippets}/member-search-controller-test/페이징_테스트/response-fields.adoc[]

Links
include::{snippets}/member-search-controller-test/페이징_테스트/links.adoc[]

Curl request
include::{snippets}/member-search-controller-test/페이징_테스트/curl-request.adoc[]

Http request
include::{snippets}/member-search-controller-test/페이징_테스트/http-request.adoc[]

Httpie request
include::{snippets}/member-search-controller-test/페이징_테스트/httpie-request.adoc[]

Response body
include::{snippets}/member-search-controller-test/페이징_테스트/response-body.adoc[]
```

  
(2) 테스트 실행  
Maven install 라이프사이클을 실행하고 테스트가 정상적으로 완료되는지 확인한다.  
정상적으로 완료되었으면 target/generated-docs 디렉토리 하위에 html파일이 생성된다.  
이것을 일반 css, js와 같이 리소스 폴더에 위치시킨 후 Application을 실행하여 확인해보자  
(Resource handler 설정에 대한 설명은 생략한다)  


  
<img width="600" src="https://raw.githubusercontent.com/dlxotn216/springrestdocs/master/src/main/resources/images/result-create.png" />  
  
<img width="600" src="https://raw.githubusercontent.com/dlxotn216/springrestdocs/master/src/main/resources/images/result-search-body.png" />  
  
<img width="600" src="https://raw.githubusercontent.com/dlxotn216/springrestdocs/master/src/main/resources/images/result-search-res-field.png" />


### 마무리  
생각보다 Spring rest docs를 설정하면서 많은 오류를 마주쳤는데 정리하자니 별것이 아닌 것 같아 기재하기는 힘들었다  
Spring hateoas, Spring Data JPA에 연관된 내용이 많이 관련 내용을 모른다면 위에 정리된 내용을 이해하기는 힘들 것으로  
생각된다. 다음 링크를 통해 <a href="https://github.com/dlxotn216/spring-hateoas">Spring Hateoas</a> Spring Hateaos와 관련된 내용을  
숙지 후 위 내용을 진행하면 좋을 것 같다.  
 
생각보다 API의 문서화
