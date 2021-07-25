package hellojpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.locks.Lock;

public class JpaMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("test");
        EntityManager em = emf.createEntityManager();
        EntityTransaction ex = em.getTransaction();
        ex.begin();
        try {

            Member member1 = new Member();
            member1.setUserName("hello1");
            em.persist(member1);

            Member member2 = new Member();
            member2.setUserName("hello2");
            em.persist(member2);

            em.flush();
            em.clear();

            Member m1 = em.find(Member.class, member1.getId());
            Member m2 = em.find(Member.class, member2.getId());

            System.out.println("m1 == m2: " + (m1.getClass() == m2.getClass()));

            ex.commit();
        } catch (Exception e) {
            ex.rollback();
        } finally {
            em.clear();
        }
        emf.close();
    }
}
