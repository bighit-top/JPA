package jpabook.jpashop.domain2;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Team {

    @Id
    @GeneratedValue
    @Column(name = "TEAM_ID")
    private Long id;
    private String name;

    // 양방향 매핑 : 사실 단방향 2개 - 연관관계 주인. 외래키가 있는 곳이 주인.
    // 반대편 Member.team 과 연관되어 있다. 연관관계 주인 = Member.team.
    // 값의 세팅은 양방향 모두한다. = 양방향 하지 않으면, 중간중간 flush(), clear() 필요하다.
    // 양방향으로 세팅하지 않으면서 flush(), clear()하지 않으면 1차캐시에만 머무는 상태이기 때문에 한쪽 값이 없는 상태다.
    // 코딩을 하다 보면 위 내용을 자주 잊어버리기 때문에 단방향만 세팅할 수 잇도록 '연관관계 편의 메서드'를 넣어준다.
//    @OneToMany(mappedBy = "team")
    // 다대일
    @OneToMany
    @JoinColumn(name = "TEAM_ID")
    private List<Member> members = new ArrayList<>(); // ArrayList 초기화 관례: add()할 때 null 방지함

    public void addMember(Member member) {
        member.setTeam(this);
        members.add(member);
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Member> getMembers() {
        return members;
    }

    public void setMembers(List<Member> members) {
        this.members = members;
    }
}
