package pojo;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Test;
import static org.junit.Assert.*;

public class CoffeShopUtilTest {

    @Test
    public void testGetSessionFactory() {
        SessionFactory sessionFactory = CoffeShopUtil.getSessionFactory();
        assertNotNull("SessionFactory should not be null", sessionFactory);

        // Fix: Open a session and check if it's open
        Session session = sessionFactory.openSession();
        assertTrue("Session should be open", session.isOpen());
        session.close();
    }

    @Test
    public void testMultipleCallsReturnSameInstance() {
        SessionFactory firstCall = CoffeShopUtil.getSessionFactory();
        SessionFactory secondCall = CoffeShopUtil.getSessionFactory();
        assertSame("Multiple calls should return the same SessionFactory instance",
                firstCall, secondCall);
    }

    @Test
    public void testShutdown() {
        SessionFactory sessionFactory = CoffeShopUtil.getSessionFactory();
        CoffeShopUtil.shutdown();
        assertTrue("SessionFactory should be closed after shutdown",
                sessionFactory.isClosed());
    }
}
