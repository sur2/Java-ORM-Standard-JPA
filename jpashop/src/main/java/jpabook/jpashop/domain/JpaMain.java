package jpabook.jpashop.domain;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

public class JpaMain {

    //private static EntityManagerFactory emf = null;

    public static void main(String[] args) {

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpashop");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        try {
            Team team = new Team();
            team.setName("TeamA");
            em.persist(team);

            MemberT member = new MemberT();
            member.setUsername("member01");
            member.setTeam(team);

            em.persist(member);

            //em.flush();
            //em.clear();

            MemberT findMemberT = em.find(MemberT.class, member.getId());
            List<MemberT> memberTs = findMemberT.getTeam().getMembers();
//            Team findTeam = findMemberT.getTeam();
//            System.out.println("findTeam = " + findTeam.getName());
            for (MemberT m : memberTs) {
                System.out.println("m = " + m.getUsername());
            }

            tx.commit();

        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.clear();
        }
        emf.close();
    }
}
