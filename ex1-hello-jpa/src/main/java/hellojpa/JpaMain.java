package hellojpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

public class JpaMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();
        EntityTransaction ex = em.getTransaction();
        ex.begin();
        try {
            /* INSERT
            Member member = new Member();
            member.setId(2L);
            member.setName("HelloB");

            em.persist(member);
            */

            // SELECT
            /*Member findMember = em.find(Member.class, 1L);
            System.out.println("findMember.id: " + findMember.getId());
            System.out.println("findMember.name: " + findMember.getName());*/

            // UPDATE
            //findMember.setName("HelloA");

            // JPQL
            /*List<Member> result = em.createQuery("select m from Member as m", Member.class)
                    .setFirstResult(5)
                    .setMaxResults(8)
                    .getResultList();
            for (Member member : result) {
                System.out.println("member.name: " + member.getName());
            }*/

            // 비영속
            Member member = new Member();
            member.setId(100L);
            member.setName("HelloJPA");
            // 영속
            em.persist(member);
            // 준영속
            em.detach(member);
            // 삭제
            em.remove(member);

            ex.commit();
        } catch (Exception e) {
            ex.rollback();
        } finally {
            em.clear();
        }
        emf.close();
    }
}
