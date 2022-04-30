package Domain;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
public class Member extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    private String username;


    //@ManyToOne(fetch = FetchType.LAZY) //(fetch = FetchType.LAZY) : 프록시 조회 - 지연로딩
    @ManyToOne(fetch = FetchType.EAGER) //(fetch = FetchType.EAGER) :  프록시 조회 - 즉시로딩
    @JoinColumn //(name = "TEAM_ID", insertable = false, updatable = false)
    private Team team;

    //기간: Period
/*
    private LocalDateTime startDate;
    private LocalDateTime endDate;
*/
    @Embedded
    private Period workPeriod;

    //주소: Address
/*
    private String city;
    private String street;
    private String zipcode;
*/
    @Embedded
    private Address homeAddress;

    //주소를 두개 embedded 하고싶을 때
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "city", column = @Column(name = "WOKR_CITY")),
            @AttributeOverride(name = "street", column = @Column(name = "WORK_STREET")),
            @AttributeOverride(name = "zipcode", column = @Column(name = "WORK_ZIPCODE"))
    })
    private Address workAddress;

    //값 타입 컬렉션 : 값 타입을 하나 이상 저장할 때 사용
    @ElementCollection
    @CollectionTable(name = "favorite_food",
            joinColumns = @JoinColumn(name = "member_id")) //테이블 지정
    @Column(name = "food_name")
    private Set<String> favoriteFoods = new HashSet<>();

//    @ElementCollection
//    @CollectionTable(name = "address",
//            joinColumns = @JoinColumn(name = "member_id"))
//    private List<Address> addressHistory = new ArrayList<>();
    // 값 타입 컬렉션 대신 일대다 단방향으로 매핑
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "member_id")
    private List<AddressEntity> addressHistory = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return username;
    }

    public void setUserName(String userName) {
        this.username = userName;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public Period getWorkPeriod() {
        return workPeriod;
    }

    public void setWorkPeriod(Period workPeriod) {
        this.workPeriod = workPeriod;
    }

    public Address getHomeAddress() {
        return homeAddress;
    }

    public void setHomeAddress(Address homeAddress) {
        this.homeAddress = homeAddress;
    }

    public Address getWorkAddress() {
        return workAddress;
    }

    public void setWorkAddress(Address workAddress) {
        this.workAddress = workAddress;
    }

    public Set<String> getFavoriteFoods() {
        return favoriteFoods;
    }

    public void setFavoriteFoods(Set<String> favoriteFoods) {
        this.favoriteFoods = favoriteFoods;
    }

    public List<AddressEntity> getAddressHistory() {
        return addressHistory;
    }

    public void setAddressHistory(List<AddressEntity> addressHistory) {
        this.addressHistory = addressHistory;
    }
}
