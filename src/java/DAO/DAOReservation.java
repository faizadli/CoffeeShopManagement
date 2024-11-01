package DAO;

import java.util.List;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import pojo.CoffeShopUtil;
import pojo.Reservation;

public class DAOReservation {

    public void save(Reservation reservation) {
        Session session = CoffeShopUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.save(reservation);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    public Reservation findById(Integer id) {
        Session session = CoffeShopUtil.getSessionFactory().openSession();
        Reservation reservation = null;
        try {
            // Menggunakan HQL dengan JOIN FETCH
            String hql = "FROM Reservation r LEFT JOIN FETCH r.users WHERE r.id = :id";
            Query query = session.createQuery(hql);
            query.setParameter("id", id);
            reservation = (Reservation) query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
        return reservation;
    }

    public void update(Reservation reservation) {
        Session session = CoffeShopUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.update(reservation);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    public void delete(Reservation reservation) {
        Session session = CoffeShopUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.delete(reservation);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    @SuppressWarnings("unchecked")
    public List<Reservation> findAll() {
        Session session = CoffeShopUtil.getSessionFactory().openSession();
        List<Reservation> reservations = null;
        try {
            // Menggunakan HQL dengan JOIN FETCH
            String hql = "SELECT DISTINCT r FROM Reservation r LEFT JOIN FETCH r.users";
            Query query = session.createQuery(hql);
            reservations = query.list();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
        return reservations;
    }
}
