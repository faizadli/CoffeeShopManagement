package DAO;

import org.hibernate.Session;
import org.hibernate.Transaction;
import pojo.CoffeShopUtil;
import pojo.Users;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DAOUser {

    private static final Logger logger = Logger.getLogger(DAOUser.class.getName());

    public boolean save(Users user) {
        Session session = CoffeShopUtil.getSessionFactory().openSession();
        Transaction tx = null;

        try {
            tx = session.beginTransaction();
            session.save(user);
            tx.commit();
            logger.info("User saved successfully: " + user.getEmail());
            return true;
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            logger.log(Level.SEVERE, "Error saving user: " + user.getEmail(), e);
            return false;
        } finally {
            session.close();
        }
    }

    public Users findByEmail(String email) {
        Session session = CoffeShopUtil.getSessionFactory().openSession();
        try {
            Users user = (Users) session.createQuery("FROM Users WHERE email = :email")
                    .setParameter("email", email)
                    .uniqueResult();
            if (user != null) {
                logger.info("User found with email: " + email);
            } else {
                logger.info("No user found with email: " + email);
            }
            return user;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error finding user by email: " + email, e);
            return null;
        } finally {
            session.close();
        }
    }

    // Add other CRUD operations as needed
}
