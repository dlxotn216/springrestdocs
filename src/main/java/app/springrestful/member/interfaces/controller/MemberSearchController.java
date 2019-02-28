package app.springrestful.member.interfaces.controller;

import app.springrestful.base.CustomPageMetadata;
import app.springrestful.member.application.service.MemberSearchService;
import app.springrestful.member.interfaces.model.MemberDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author taesu
 * @version 1.0
 * @since 1.0
 */
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
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaTypes.HAL_JSON)
                .body(body);
    }

    @GetMapping("/members/{memberKey}")
    public ResponseEntity<MemberDto.MemberSearchResponse> searchMember(@PathVariable("memberKey") Long memberKey) {
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaTypes.HAL_JSON)
                .body(this.memberSearchService.searchMember(memberKey));
    }
}