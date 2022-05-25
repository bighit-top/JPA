package springdatajpa.datajpa_1.repository;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;
import springdatajpa.datajpa_1.entity.Member;
import springdatajpa.datajpa_1.entity.Team;

import javax.persistence.criteria.*;

public class MemberSpec {

    public static Specification<Member> teamName(final String teamName) {
        return (root, query, criteriaBuilder) -> {
            if (StringUtils.hasText(teamName)) { //isEmpty deprecated
                return null;
            }

            Join<Member, Team> t = root.join("team", JoinType.INNER);//회원과 조인
            return criteriaBuilder.equal(t.get("name"), teamName); //where
        };
    }

    public static Specification<Member> username(final String username) {
        return (Specification<Member>) (root, query, criteriaBuilder) ->
            criteriaBuilder.equal(root.get("username"), username); //where
    }
}
