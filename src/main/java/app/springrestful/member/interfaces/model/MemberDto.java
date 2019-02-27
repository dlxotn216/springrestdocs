package app.springrestful.member.interfaces.model;

import app.springrestful.member.domain.model.Member;
import app.springrestful.member.interfaces.controller.MemberCreateController;
import app.springrestful.member.interfaces.controller.MemberSearchController;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.core.Relation;

import java.time.LocalDate;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

/**
 * Created by taesu at : 2019-02-21
 *
 * 여기에 MemberDto 클래스에 대한 설명을 기술해주세요
 *
 * @author taesu
 * @version 1.0
 * @since 1.0
 */
public final class MemberDto {

    private MemberDto() {
    }

    public static Member asMember(MemberCreateRequest request) {
        return new Member(request.getMemberId(), request.getName());
    }

    public static MemberCreateResponse asCreateResponse(MemberCreateRequest request, Member member) {
        MemberCreateResponse memberCreateResponse
                = new MemberCreateResponse(member.getMemberKey(), member.getId(), member.getName(), member.getJoinedAt());
        memberCreateResponse.add(linkTo(methodOn(MemberCreateController.class).createMember(request)).withSelfRel());

        return memberCreateResponse;
    }
    
    public static MemberSearchResponse asSearchResponse(Member member){
        MemberSearchResponse memberSearchResponse 
                = new MemberSearchResponse(member.getMemberKey(), member.getId(), member.getName(), member.getJoinedAt());
        memberSearchResponse.add(linkTo(methodOn(MemberSearchController.class).searchMember(member.getMemberKey())).withSelfRel());
        return memberSearchResponse;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemberCreateRequest {
        private String memberId;
        private String name;
    }


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemberCreateResponse extends ResourceSupport {
        private Long memberKey;
        private String memberId;
        private String name;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        private LocalDate joinedAt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Relation(collectionRelation = "members")
    public static class MemberSearchResponse extends ResourceSupport {
        private Long memberKey;
        private String memberId;
        private String name;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        private LocalDate joinedAt;
    }
}
