package DAO;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.Query;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import pojo.CoffeShopUtil;
import pojo.Users;

@RunWith(PowerMockRunner.class)
@PrepareForTest(CoffeShopUtil.class)
public class DAOUserTest {

    @Mock
    private Session session;

    @Mock
    private SessionFactory sessionFactory;

    @Mock
    private Transaction transaction;

    @Mock
    private Query query;

    private DAOUser daoUser;

    @Before
    public void setUp() {
        PowerMockito.mockStatic(CoffeShopUtil.class);
        when(CoffeShopUtil.getSessionFactory()).thenReturn(sessionFactory);
        when(sessionFactory.openSession()).thenReturn(session);
        when(session.beginTransaction()).thenReturn(transaction);

        daoUser = new DAOUser();
    }

    @After
    public void tearDown() {
        daoUser = null;
    }

    @Test
    public void testSaveSuccess() {
        // Arrange
        Users user = new Users("Test User", "test@test.com", "password", "123456789", "customer");
        when(session.save(any(Users.class))).thenReturn(1L);

        // Act
        boolean result = daoUser.save(user);

        // Assert
        assertTrue(result);
        verify(session).save(user);
        verify(transaction).commit();
        verify(session).close();
    }

    @Test
    public void testSaveFailure() {
        // Arrange
        Users user = new Users("Test User", "test@test.com", "password", "123456789", "customer");
        when(session.save(any(Users.class))).thenThrow(new RuntimeException("Save failed"));

        // Act
        boolean result = daoUser.save(user);

        // Assert
        assertFalse(result);
        verify(transaction).rollback();
        verify(session).close();
    }

    @Test
    public void testFindByEmailSuccess() {
        // Arrange
        String email = "test@test.com";
        Users expectedUser = new Users("Test User", email, "password", "123456789", "customer");

        when(session.createQuery(anyString())).thenReturn(query);
        when(query.setParameter("email", email)).thenReturn(query);
        when(query.uniqueResult()).thenReturn(expectedUser);

        // Act
        Users result = daoUser.findByEmail(email);

        // Assert
        assertNotNull(result);
        assertEquals(email, result.getEmail());
        verify(session).close();
    }

    @Test
    public void testFindByEmailNotFound() {
        // Arrange
        String email = "notfound@test.com";

        when(session.createQuery(anyString())).thenReturn(query);
        when(query.setParameter("email", email)).thenReturn(query);
        when(query.uniqueResult()).thenReturn(null);

        // Act
        Users result = daoUser.findByEmail(email);

        // Assert
        assertNull(result);
        verify(session).close();
    }

    @Test
    public void testFindByEmailException() {
        // Arrange
        String email = "test@test.com";
        when(session.createQuery(anyString())).thenThrow(new RuntimeException("Query failed"));

        // Act
        Users result = daoUser.findByEmail(email);

        // Assert
        assertNull(result);
        verify(session).close();
    }
}
