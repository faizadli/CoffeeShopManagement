package DAO;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import pojo.CoffeShopUtil;
import pojo.Reservation;
import pojo.Users;

@RunWith(PowerMockRunner.class)
@PrepareForTest(CoffeShopUtil.class)
public class DAOReservationTest {

    @Mock
    private SessionFactory sessionFactory;
    @Mock
    private Session session;
    @Mock
    private Transaction transaction;
    @Mock
    private Query query;

    private DAOReservation daoReservation;
    private Reservation testReservation;

    @Before
    public void setUp() {
        PowerMockito.mockStatic(CoffeShopUtil.class);
        when(CoffeShopUtil.getSessionFactory()).thenReturn(sessionFactory);
        when(sessionFactory.openSession()).thenReturn(session);
        when(session.beginTransaction()).thenReturn(transaction);

        daoReservation = new DAOReservation();

        Users testUser = new Users();
        testUser.setName("Test User");

        testReservation = new Reservation();
        testReservation.setReservationId(1);
        testReservation.setUsers(testUser);
        testReservation.setTableNumber(1);
        testReservation.setReservationDate(new Date());
        testReservation.setReservationTime(new Date());
        testReservation.setStatus("pending");
        testReservation.setCreatedAt(new Date());
    }

    @Test
    public void testSave() {
        // Remove doNothing() as it's not needed for void methods
        daoReservation.save(testReservation);

        verify(session).save(testReservation);
        verify(transaction).commit();
        verify(session).close();
    }

    @Test
    public void testFindById() {
        String hql = "FROM Reservation r LEFT JOIN FETCH r.users WHERE r.id = :id";
        when(session.createQuery(hql)).thenReturn(query);
        when(query.setParameter("id", 1)).thenReturn(query);
        when(query.uniqueResult()).thenReturn(testReservation);

        Reservation result = daoReservation.findById(1);

        assertNotNull(result);
        assertEquals(testReservation.getReservationId(), result.getReservationId());
        verify(session).close();
    }

    @Test
    public void testUpdate() {
        // Remove doNothing() for void methods
        daoReservation.update(testReservation);

        verify(session).update(testReservation);
        verify(transaction).commit();
        verify(session).close();
    }

    @Test
    public void testDelete() {
        // Remove doNothing() for void methods
        daoReservation.delete(testReservation);

        verify(session).delete(testReservation);
        verify(transaction).commit();
        verify(session).close();
    }

    @Test
    public void testFindAll() {
        List<Reservation> testList = new ArrayList<>();
        testList.add(testReservation);

        String hql = "SELECT DISTINCT r FROM Reservation r LEFT JOIN FETCH r.users";
        when(session.createQuery(hql)).thenReturn(query);
        when(query.list()).thenReturn(testList);

        List<Reservation> result = daoReservation.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testReservation.getReservationId(), result.get(0).getReservationId());
        verify(session).close();
    }

    @Test
    public void testSaveWithException() {
        doThrow(new RuntimeException("Test exception")).when(session).save(any(Reservation.class));

        daoReservation.save(testReservation);

        verify(transaction).rollback();
        verify(session).close();
    }

    @Test
    public void testUpdateWithException() {
        doThrow(new RuntimeException("Test exception")).when(session).update(any(Reservation.class));

        daoReservation.update(testReservation);

        verify(transaction).rollback();
        verify(session).close();
    }

    @Test
    public void testDeleteWithException() {
        doThrow(new RuntimeException("Test exception")).when(session).delete(any(Reservation.class));

        daoReservation.delete(testReservation);

        verify(transaction).rollback();
        verify(session).close();
    }
}
