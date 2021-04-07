package hellojpa;

import javax.annotation.Generated;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Team {

    @Id @GeneratedValue
    @Column(name = "TEAM_ID")
    private Long id;

    @Column(name = "TEAM_NAME")
    private String name;

    // Entity : Column (주인이 아닌 곳에 mappedBy = "연관관계 주인의 외래키")
    @OneToMany(mappedBy = "team")
    private List<Member> members = new ArrayList<Member>();

    // 연관관계 편의 메서드(연관관계에 있는 두 엔티티 모두 값을 설정)
    public void addMembers(Member member) {
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
