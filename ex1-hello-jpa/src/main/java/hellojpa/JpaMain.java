package hellojpa;

import org.hibernate.Hibernate;

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

            Member refMember = em.getReference(Member.class, member1.getId());
            System.out.println("refMember = " + refMember.getClass()); // Proxy
            refMember.getUserName();
            System.out.println("isLoad = " + emf.getPersistenceUnitUtil().isLoaded(refMember));
            Hibernate.initialize(refMember);

            ex.commit();
        } catch (Exception e) {
            ex.rollback();
            e.printStackTrace();
        } finally {
            em.clear();
        }
        emf.close();
    }
}
