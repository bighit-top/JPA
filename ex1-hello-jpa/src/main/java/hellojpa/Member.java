package hellojpa;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
//@SequenceGenerator(name = "member_seq_generator", sequenceName = "member_seq") //@GeneratedValue SEQUENCE 전략에서 시퀀스 지정해서 사용
/*
@TableGenerator(
        name = "MEMBER_SEQ_GENERATOR",
        table = "MY_SEQUENCES",
        pkColumnValue = "MEMBER_SEQ", allocationSize = 1
) //@GeneratedValue TABLE 전략에서 시퀀스 테이블을 만들어서 사용. 성능X
*/
@SequenceGenerator(
        name = "MEMBER_SEQ_GENERATOR",
        sequenceName = "MEMBER_SEQ", //매핑할 데이터베이스 시퀀스 이름
        initialValue = 1, allocationSize = 50 //1-50까지 시퀀스를 땡겨놓고 - 성능 문제로 50개를 먼저 땡겨놓고 사용
)
public class Member {

    @Id //직접할당
    //@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "member_seq_genertor") //자동할당. DB방언 다르게 설정. AUTO, IDENTITY(mysql), SEQUENCE(oracle), TABLE
    //@GeneratedValue(strategy = GenerationType.TABLE, generator = "MEMBER_SEQ_GENERATOR")
    //@GeneratedValue(strategy = GenerationType.IDENTITY) //PK가 필요하기 때문에 JPA가 commit전에 insert한다. 버퍼링 기능X
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "MEMBER_SEQ_GENERATOR") //PK가 필요하기 때문에 JPA가 commit전에 PK값을 가져오고 commit할 때 insert한다. 버퍼링 기능O
    private Long id;

    @Column(name = "name", nullable = false)
    private String username;

    private Integer age;

    @Enumerated(EnumType.STRING) //Enum 타입의 경우. EnumType.ORDINAL 은 사용하지 말것
    private RoleType roleType;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createDate;

    @Temporal(TemporalType.TIMESTAMP) //시간 지정. DATE/TIME/TIMESTAMP. 최신 버전은 localdate(time)을 인식하기 때문에 사용할 필요 없음.
    private Date lastModifiedDate;

    private LocalDate testLocalDate;
    private LocalDateTime testLocalDateTime;

    @Lob //varchar 이상의 크기를 원할때. 문자=CLOB매핑(String, char[], java.sql.CLOB), 나머지=BLOB매핑(byte[], java.sql.BLOB)
    private String description;

    @Transient //mapping 재외
    private int temp;
    public Member() {
    }

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

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public RoleType getRoleType() {
        return roleType;
    }

    public void setRoleType(RoleType roleType) {
        this.roleType = roleType;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
