package jpa.querydsl_1.repository;

import jpa.querydsl_1.dto.MemberSearchCondition;
import jpa.querydsl_1.entity.MemberTeamDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MemberRepositoryCustom {
    List<MemberTeamDto> search(MemberSearchCondition condition);

    //페이징 - simple
    Page<MemberTeamDto> searchPageSimple(MemberSearchCondition condition, Pageable pageable);

    //페이징 - complex: deprecated
    Page<MemberTeamDto> searchPageComplex(MemberSearchCondition condition, Pageable pageable);

    //페이징 - complex: new
    Page<MemberTeamDto> searchPageComplex_New(MemberSearchCondition condition, Pageable pageable);

}
