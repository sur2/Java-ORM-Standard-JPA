package hellojpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

public class JpaMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("test");
        EntityManager em = emf.createEntityManager();
        EntityTransaction ex = em.getTransaction();
        ex.begin();
        try {

            Team team = new Team();
            team.setName("teamA");
            em.persist(team);

            Member member = new Member();
            member.setUserName("memberA");
            //member.setTeam(team);
            member.changeTeam(team);
            em.persist(member);

            Member findMember = em.find(Member.class, member.getId());
            List<Member> members = findMember.getTeam().getMembers();

            for (Member m : members) {
                System.out.println("m = " + m.getUserName());
            }

            ex.commit();
        } catch (Exception e) {
            ex.rollback();
        } finally {
            em.clear();
        }
        emf.close();
    }
}
