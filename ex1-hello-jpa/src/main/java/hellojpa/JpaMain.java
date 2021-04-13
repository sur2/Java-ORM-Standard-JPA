package hellojpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;
import java.util.concurrent.locks.Lock;

public class JpaMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("test");
        EntityManager em = emf.createEntityManager();
        EntityTransaction ex = em.getTransaction();
        ex.begin();
        try {

            Locker locker = new Locker();
            locker.setName("lockerA");
            em.persist(locker);

            Member member = new Member();
            member.setUserName("memberA");
            member.setLocker(locker);
            locker.setMember(member);
            em.persist(member);

            Locker findLocker = em.find(Locker.class, member.getLocker().getId());
            System.out.println(findLocker.getMember().getUserName());

            ex.commit();
        } catch (Exception e) {
            ex.rollback();
        } finally {
            em.clear();
        }
        emf.close();
    }
}
