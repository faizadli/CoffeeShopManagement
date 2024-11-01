package pojo;

import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class OrdersTest {

    private Orders order;
    private Menu menu;
    private Users user;
    private Date orderDate;
    private Set payments;

    @Before
    public void setUp() {
        menu = new Menu();
        user = new Users();
        orderDate = new Date();
        payments = new HashSet();
        order = new Orders(menu, user, 2, new BigDecimal("20.00"), orderDate, "pending", payments);
    }

    @Test
    public void testConstructorAndGetters() {
        assertEquals(2, order.getQuantity());
        assertEquals(new BigDecimal("20.00"), order.getTotalPrice());
        assertEquals(orderDate, order.getOrderDate());
        assertEquals("pending", order.getStatus());
        assertSame(menu, order.getMenu());
        assertSame(user, order.getUsers());
        assertSame(payments, order.getPayments());
    }

    @Test
    public void testSetters() {
        // Arrange
        Integer newOrderId = 1;
        Menu newMenu = new Menu();
        Users newUser = new Users();
        int newQuantity = 3;
        BigDecimal newTotalPrice = new BigDecimal("30.00");
        Date newOrderDate = new Date();
        String newStatus = "completed";
        Set newPayments = new HashSet();

        // Act
        order.setOrderId(newOrderId);
        order.setMenu(newMenu);
        order.setUsers(newUser);
        order.setQuantity(newQuantity);
        order.setTotalPrice(newTotalPrice);
        order.setOrderDate(newOrderDate);
        order.setStatus(newStatus);
        order.setPayments(newPayments);

        // Assert
        assertEquals(newOrderId, order.getOrderId());
        assertSame(newMenu, order.getMenu());
        assertSame(newUser, order.getUsers());
        assertEquals(newQuantity, order.getQuantity());
        assertEquals(newTotalPrice, order.getTotalPrice());
        assertEquals(newOrderDate, order.getOrderDate());
        assertEquals(newStatus, order.getStatus());
        assertSame(newPayments, order.getPayments());
    }

    @Test
    public void testDefaultConstructor() {
        Orders defaultOrder = new Orders();
        assertNull(defaultOrder.getOrderId());
        assertNull(defaultOrder.getMenu());
        assertNull(defaultOrder.getUsers());
        assertEquals(0, defaultOrder.getQuantity());
        assertNull(defaultOrder.getTotalPrice());
        assertNull(defaultOrder.getOrderDate());
        assertNull(defaultOrder.getStatus());
        assertNotNull(defaultOrder.getPayments());
        assertTrue(defaultOrder.getPayments().isEmpty());
    }

    @Test
    public void testPartialConstructor() {
        int quantity = 1;
        BigDecimal totalPrice = new BigDecimal("10.00");
        Date orderDate = new Date();

        Orders partialOrder = new Orders(quantity, totalPrice, orderDate);

        assertEquals(quantity, partialOrder.getQuantity());
        assertEquals(totalPrice, partialOrder.getTotalPrice());
        assertEquals(orderDate, partialOrder.getOrderDate());
        assertNull(partialOrder.getOrderId());
        assertNull(partialOrder.getMenu());
        assertNull(partialOrder.getUsers());
        assertNull(partialOrder.getStatus());
        assertNotNull(partialOrder.getPayments());
        assertTrue(partialOrder.getPayments().isEmpty());
    }
}
