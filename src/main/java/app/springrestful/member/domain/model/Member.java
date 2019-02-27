package app.springrestful.member.domain.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

import static app.springrestful.member.domain.model.Member.GENERATOR_NAME;


/**
 * Created by taesu at : 2019-02-21
 *
 * @author taesu
 * @version 1.0
 * @since 1.0
 */
@Entity
@Getter
@NoArgsConstructor
@SequenceGenerator(name = GENERATOR_NAME, sequenceName = GENERATOR_NAME)
public class Member {
    static final String GENERATOR_NAME = "MEMBER_SEQ";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = GENERATOR_NAME)
    private Long memberKey;

    @Column
    private String id;

    @Column
    private String name;

    @Column
    private LocalDate joinedAt;

    public Member(String id, String name) {
        this.id = id;
        this.name = name;
        this.joinedAt = LocalDate.now();
    }
}
