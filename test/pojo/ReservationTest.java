package pojo;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class ReservationTest {

    private Reservation reservation;
    private Users testUser;
    private Date testDate;

    public ReservationTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @Before
    public void setUp() {
        reservation = new Reservation();
        testUser = new Users();
        testUser.setName("Test User");
        testDate = new Date();
    }
    
    @Test
    public void testConstructorWithRequiredFields() {
        // Arrange
        int tableNumber = 1;
        Date reservationDate = new Date();
        Date reservationTime = new Date();
        Date createdAt = new Date();

        // Act
        Reservation reservation = new Reservation(tableNumber, reservationDate, reservationTime, createdAt);

        // Assert
        assertEquals(tableNumber, reservation.getTableNumber());
        assertEquals(reservationDate, reservation.getReservationDate());
        assertEquals(reservationTime, reservation.getReservationTime());
        assertEquals(createdAt, reservation.getCreatedAt());
        assertNull(reservation.getUsers());
        assertNull(reservation.getStatus());
    }

    @Test
    public void testConstructorWithAllFields() {
        // Arrange
        Users users = new Users();
        users.setName("Test User");
        int tableNumber = 1;
        Date reservationDate = new Date();
        Date reservationTime = new Date();
        String status = "confirmed";
        Date createdAt = new Date();

        // Act
        Reservation reservation = new Reservation(users, tableNumber, reservationDate,
                reservationTime, status, createdAt);

        // Assert
        assertEquals(users, reservation.getUsers());
        assertEquals(tableNumber, reservation.getTableNumber());
        assertEquals(reservationDate, reservation.getReservationDate());
        assertEquals(reservationTime, reservation.getReservationTime());
        assertEquals(status, reservation.getStatus());
        assertEquals(createdAt, reservation.getCreatedAt());
    }

    @Test
    public void testDefaultConstructor() {
        // Act
        Reservation reservation = new Reservation();

        // Assert
        assertNull(reservation.getReservationId());
        assertNull(reservation.getUsers());
        assertEquals(0, reservation.getTableNumber());
        assertNull(reservation.getReservationDate());
        assertNull(reservation.getReservationTime());
        assertNull(reservation.getStatus());
        assertNull(reservation.getCreatedAt());
    }

    @Test
    public void testGetReservationId() {
        Integer id = 1;
        reservation.setReservationId(id);
        assertEquals(id, reservation.getReservationId());
    }

    @Test
    public void testSetReservationId() {
        Integer id = 1;
        reservation.setReservationId(id);
        assertEquals(id, reservation.getReservationId());
    }

    @Test
    public void testGetUsers() {
        reservation.setUsers(testUser);
        assertEquals(testUser, reservation.getUsers());
    }

    @Test
    public void testSetUsers() {
        reservation.setUsers(testUser);
        assertEquals(testUser, reservation.getUsers());
    }

    @Test
    public void testGetTableNumber() {
        int tableNumber = 1;
        reservation.setTableNumber(tableNumber);
        assertEquals(tableNumber, reservation.getTableNumber());
    }

    @Test
    public void testSetTableNumber() {
        int tableNumber = 1;
        reservation.setTableNumber(tableNumber);
        assertEquals(tableNumber, reservation.getTableNumber());
    }

    @Test
    public void testGetReservationDate() {
        reservation.setReservationDate(testDate);
        assertEquals(testDate, reservation.getReservationDate());
    }

    @Test
    public void testSetReservationDate() {
        reservation.setReservationDate(testDate);
        assertEquals(testDate, reservation.getReservationDate());
    }

    @Test
    public void testGetReservationTime() {
        reservation.setReservationTime(testDate);
        assertEquals(testDate, reservation.getReservationTime());
    }

    @Test
    public void testSetReservationTime() {
        reservation.setReservationTime(testDate);
        assertEquals(testDate, reservation.getReservationTime());
    }

    @Test
    public void testGetStatus() {
        String status = "pending";
        reservation.setStatus(status);
        assertEquals(status, reservation.getStatus());
    }

    @Test
    public void testSetStatus() {
        String status = "pending";
        reservation.setStatus(status);
        assertEquals(status, reservation.getStatus());
    }

    @Test
    public void testGetCreatedAt() {
        reservation.setCreatedAt(testDate);
        assertEquals(testDate, reservation.getCreatedAt());
    }

    @Test
    public void testSetCreatedAt() {
        reservation.setCreatedAt(testDate);
        assertEquals(testDate, reservation.getCreatedAt());
    }
}
