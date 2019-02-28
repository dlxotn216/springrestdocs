package app.springrestful.member.application.service;

import app.springrestful.member.domain.repository.MemberRepository;
import app.springrestful.member.interfaces.model.MemberDto;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * Created by taesu at : 2019-02-21
 *
 * @author taesu
 * @version 1.0
 * @since 1.0
 */
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
