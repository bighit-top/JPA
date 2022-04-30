package jpabook.jpashop.domain2;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Member {

    @Id
    @GeneratedValue
    @Column(name = "MEMBER_ID")
    private Long id;

    @Column(name = "USERNAME")
    private String username;

    //객체지향스럽지 않음. 기존의 테이블 방식.
//    @Column(name = "TEAM_ID")
//    private Long teamId;

    //일대다 연관관계 매핑 : 단방향
//    @ManyToOne
//    @JoinColumn(name = "TEAM_ID")
    //다대일
    @ManyToOne
    private Team team;

    //일대일
    @OneToOne
    @JoinColumn(name = "name")
    private Locker locker;

    //다대다 : 실무에서 X
//    @ManyToMany
//    @JoinTable(name = "MEMBER_PRODUCT")
//    private List<Product> products = new ArrayList<>();
    @OneToMany(mappedBy = "member")
    private List<MemberProduct> memberProducts = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public void changeTeam(Team team) {
        this.team = team;
        team.getMembers().add(this);
    }


}
