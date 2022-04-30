package jpabook.jpashop1.api;

import jpabook.jpashop1.domain.Member;
import jpabook.jpashop1.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController //@Controller @ResponseBody
public class MemberApiController {

    private final MemberService memberService;

    //Member entity를 직접 사용하는 방법.
    //@Valid Member member를 검증한다.(화면에 대한)
    @PostMapping("/api/v1/members")
    public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member) {
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    //Member entity를 직접 사용하는 방법.
    //해당 필요없는 데이터까지 반환한다.
    //엔티티에서 @JsonIgnore 등 로직으로 노출을 막을 수 있지만
    //엔티티는 다른 기능에서도 사용되기 때문에 특정 기능에 종속시켜 맞출 수 없다. = api 스팩이 변경되버린다.
    @GetMapping("/api/v1/members")
    public List<Member> membersV1() {
        return memberService.findMembers();
    }

    //화면에서 사용할 객체를 따로 이용한다. CreateMemberRequest. dto같은 존재.
    //엔티티 변경시에도 화면단 수정이 필요없다. = api 스팩이 변경되지 않는다
    @PostMapping("/api/v2/members")
    public CreateMemberResponse saverMemberV2(@RequestBody @Valid CreateMemberRequest request) {

        Member member = new Member();
        member.setName(request.getName());

        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    @PutMapping("/api/v2/members/{id}")
    public UpdateMemberResponse updateMemberV2(
            @PathVariable("id") Long id,
            @RequestBody @Valid UpdateMemberRequest request) {

        memberService.update(id, request.getName());
//        Member member = memberService.update(id, request.getName()); //query 와 command 분리
        Member findMember = memberService.findOne(id);
        return new UpdateMemberResponse(findMember.getId(), findMember.getName());
    }

    @GetMapping("/api/v2/members")
    public Result memberV2() {
        List<Member> findMembers = memberService.findMembers();
        List<MemberDto> collect = findMembers.stream()
                .map(m -> new MemberDto(m.getName()))
                .collect(Collectors.toList());
        return new Result(collect);
    }


    //api 스팩 = DTO
    //해당 페이지에서만 사용 할 경우, 외부에 따로 뺄 필요는 없다. inner class로 구현해도 무방.
    @Data
    static class CreateMemberResponse {
        private Long id;
        public CreateMemberResponse(Long id) {
            this.id = id;
        }
    }

    @Data
    static class CreateMemberRequest {
        @NotEmpty
        private String name;
    }

    @Data
    @AllArgsConstructor
    static class UpdateMemberResponse {
        private Long id;
        private String name;
    }

    @Data
    static class UpdateMemberRequest {
        private String name;
    }

    @Data
    @AllArgsConstructor
    static class Result<T> {
        private T data;
    }

    @Data
    @AllArgsConstructor
    static class MemberDto {
        private String name;
    }

}
