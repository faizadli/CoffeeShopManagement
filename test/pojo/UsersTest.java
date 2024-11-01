package pojo;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class UsersTest {

    private Users user;

    public UsersTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @Before
    public void setUp() {
        user = new Users();
    }

    @After
    public void tearDown() {
        user = null;
    }

    @Test
    public void testGetUserId() {
        Integer userId = 1;
        user.setUserId(userId);
        assertEquals(userId, user.getUserId());
    }

    @Test
    public void testSetUserId() {
        Integer userId = 1;
        user.setUserId(userId);
        assertEquals(userId, user.getUserId());
    }

    @Test
    public void testGetName() {
        String name = "Test User";
        user.setName(name);
        assertEquals(name, user.getName());
    }

    @Test
    public void testSetName() {
        String name = "Test User";
        user.setName(name);
        assertEquals(name, user.getName());
    }

    @Test
    public void testGetEmail() {
        String email = "test@test.com";
        user.setEmail(email);
        assertEquals(email, user.getEmail());
    }

    @Test
    public void testSetEmail() {
        String email = "test@test.com";
        user.setEmail(email);
        assertEquals(email, user.getEmail());
    }

    @Test
    public void testGetPassword() {
        String password = "password123";
        user.setPassword(password);
        assertEquals(password, user.getPassword());
    }

    @Test
    public void testSetPassword() {
        String password = "password123";
        user.setPassword(password);
        assertEquals(password, user.getPassword());
    }

    @Test
    public void testGetPhoneNumber() {
        String phoneNumber = "123456789";
        user.setPhoneNumber(phoneNumber);
        assertEquals(phoneNumber, user.getPhoneNumber());
    }

    @Test
    public void testSetPhoneNumber() {
        String phoneNumber = "123456789";
        user.setPhoneNumber(phoneNumber);
        assertEquals(phoneNumber, user.getPhoneNumber());
    }

    @Test
    public void testGetRole() {
        String role = "customer";
        user.setRole(role);
        assertEquals(role, user.getRole());
    }

    @Test
    public void testSetRole() {
        String role = "customer";
        user.setRole(role);
        assertEquals(role, user.getRole());
    }

    @Test
    public void testGetCreatedAt() {
        Date createdAt = new Date();
        user.setCreatedAt(createdAt);
        assertEquals(createdAt, user.getCreatedAt());
    }

    @Test
    public void testSetCreatedAt() {
        Date createdAt = new Date();
        user.setCreatedAt(createdAt);
        assertEquals(createdAt, user.getCreatedAt());
    }

    @Test
    public void testGetOrderses() {
        Set orderses = new HashSet();
        user.setOrderses(orderses);
        assertEquals(orderses, user.getOrderses());
    }

    @Test
    public void testSetOrderses() {
        Set orderses = new HashSet();
        user.setOrderses(orderses);
        assertEquals(orderses, user.getOrderses());
    }

    @Test
    public void testGetReservations() {
        Set reservations = new HashSet();
        user.setReservations(reservations);
        assertEquals(reservations, user.getReservations());
    }

    @Test
    public void testSetReservations() {
        Set reservations = new HashSet();
        user.setReservations(reservations);
        assertEquals(reservations, user.getReservations());
    }
}
