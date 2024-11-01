package DAO;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import pojo.CoffeShopUtil;
import pojo.Orders;
import pojo.Menu;
import pojo.Users;

import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({CoffeShopUtil.class, FacesContext.class})
public class DAOOrdersTest {

    @Mock
    private SessionFactory sessionFactory;
    @Mock
    private Session session;
    @Mock
    private Transaction transaction;
    @Mock
    private Query query;
    @Mock
    private FacesContext facesContext;
    @Mock
    private ExternalContext externalContext;

    private DAOOrders daoOrders;
    private RuntimeException testException;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        daoOrders = new DAOOrders();
        testException = new RuntimeException("Test exception");

        mockStatic(CoffeShopUtil.class);
        mockStatic(FacesContext.class);

        when(CoffeShopUtil.getSessionFactory()).thenReturn(sessionFactory);
        when(sessionFactory.openSession()).thenReturn(session);
        when(session.beginTransaction()).thenReturn(transaction);
        when(FacesContext.getCurrentInstance()).thenReturn(facesContext);
        when(facesContext.getExternalContext()).thenReturn(externalContext);
    }

    @Test
    public void testSave() throws IOException {
        // Setup dummy data
        Orders orderToSave = new Orders();
        orderToSave.setOrderId(1);
        orderToSave.setQuantity(2);
        orderToSave.setStatus("pending");
        orderToSave.setOrderDate(new Date());
        orderToSave.setTotalPrice(new BigDecimal("50000.00"));

        Menu menu = new Menu();
        menu.setMenuId(1);
        orderToSave.setMenu(menu);

        Users user = new Users();
        user.setUserId(1);
        orderToSave.setUsers(user);

        // Execute
        daoOrders.save(orderToSave);

        // Verify basic operations
        verify(session).beginTransaction();
        verify(session).save(orderToSave);
        verify(transaction).commit();
        verify(session).close();

        // Verify saved order
        ArgumentCaptor<Orders> orderCaptor = ArgumentCaptor.forClass(Orders.class);
        verify(session).save(orderCaptor.capture());
        Orders savedOrder = orderCaptor.getValue();

        assertEquals(Integer.valueOf(1), savedOrder.getOrderId()); //Order ID should match
        assertEquals(2, savedOrder.getQuantity()); //Quantity should match
        assertEquals("pending", savedOrder.getStatus()); //Status should match
        assertEquals(new BigDecimal("50000.00"), savedOrder.getTotalPrice()); //Total price should match
        assertNotNull(savedOrder.getOrderDate()); //Order date should not be null
        assertEquals(Integer.valueOf(1), savedOrder.getMenu().getMenuId()); //Menu ID should match
        assertEquals(Integer.valueOf(1), savedOrder.getUsers().getUserId()); //User ID should match

        // Verify FacesMessage
        ArgumentCaptor<FacesMessage> messageCaptor = ArgumentCaptor.forClass(FacesMessage.class);
        verify(facesContext).addMessage(eq(null), messageCaptor.capture());
        FacesMessage capturedMessage = messageCaptor.getValue();

        assertEquals(FacesMessage.SEVERITY_INFO, capturedMessage.getSeverity()); //Message severity should be INFO
        assertEquals("Success", capturedMessage.getSummary()); //Success summary should match
        assertEquals("Order berhasil disimpan", capturedMessage.getDetail()); //Success detail should match

        verify(externalContext).redirect("OrderData.xhtml");
    }

    @Test
    public void testSaveException() throws IOException {
        // Setup dummy data
        Orders orderToSave = new Orders();
        orderToSave.setOrderId(1);
        orderToSave.setQuantity(2);
        orderToSave.setStatus("pending");
        orderToSave.setOrderDate(new Date());
        orderToSave.setTotalPrice(new BigDecimal("50000.00"));

        Menu menu = new Menu();
        menu.setMenuId(1);
        orderToSave.setMenu(menu);

        Users user = new Users();
        user.setUserId(1);
        orderToSave.setUsers(user);

        doThrow(testException).when(session).save(any(Orders.class));

        // Execute
        daoOrders.save(orderToSave);

        // Verify operations and rollback
        verify(session).save(orderToSave);
        verify(transaction).rollback();
        verify(session).close();

        // Verify error message
        ArgumentCaptor<FacesMessage> messageCaptor = ArgumentCaptor.forClass(FacesMessage.class);
        verify(facesContext).addMessage(eq(null), messageCaptor.capture());
        FacesMessage capturedMessage = messageCaptor.getValue();

        assertEquals("Message severity should be ERROR", FacesMessage.SEVERITY_ERROR, capturedMessage.getSeverity());
        assertEquals("Error summary should match", "Error", capturedMessage.getSummary());
        assertEquals("Error detail should match", "Gagal menyimpan order", capturedMessage.getDetail());
    }

    @Test
    public void testFindAll() {
        // Setup dummy data
        Orders order1 = new Orders();
        order1.setOrderId(1);
        order1.setStatus("pending");
        order1.setQuantity(2);
        order1.setOrderDate(new Date());
        order1.setTotalPrice(new BigDecimal("50000.00"));

        Menu menu1 = new Menu();
        menu1.setMenuId(1);
        menu1.setName("Espresso");
        order1.setMenu(menu1);

        Users user1 = new Users();
        user1.setUserId(1);
        user1.setName("john");
        order1.setUsers(user1);

        Orders order2 = new Orders();
        order2.setOrderId(2);
        order2.setStatus("completed");
        order2.setQuantity(1);
        order2.setOrderDate(new Date());
        order2.setTotalPrice(new BigDecimal("25000.00"));

        Menu menu2 = new Menu();
        menu2.setMenuId(2);
        menu2.setName("Latte");
        order2.setMenu(menu2);

        Users user2 = new Users();
        user2.setUserId(2);
        user2.setName("jane");
        order2.setUsers(user2);

        List<Orders> expectedOrders = Arrays.asList(order1, order2);

        when(session.createQuery("FROM Orders")).thenReturn(query);
        when(query.list()).thenReturn(expectedOrders);

        // Execute
        List<Orders> result = daoOrders.findAll();

        // Verify basic operations
        verify(session).createQuery("FROM Orders");
        verify(session).close();

        // Verify result
        assertNotNull("Result should not be null", result);
        assertEquals("Should return correct number of orders", 2, result.size());

        // Verify first order
        Orders firstOrder = result.get(0);
        assertEquals(1, firstOrder.getOrderId().intValue()); //First order ID should match
        assertEquals("pending", firstOrder.getStatus()); //First order status should match
        assertEquals(2, firstOrder.getQuantity()); //First order quantity should match
        assertEquals(new BigDecimal("50000.00"), firstOrder.getTotalPrice()); //First order price should match
        assertEquals(1, firstOrder.getMenu().getMenuId().intValue()); //First order menu ID should match
        assertEquals("Espresso", firstOrder.getMenu().getName()); //First order menu name should match
        assertEquals(1, firstOrder.getUsers().getUserId().intValue()); //First order user ID should match
        assertEquals("john", firstOrder.getUsers().getName()); //First order username should match

        // Verify second order
        Orders secondOrder = result.get(1);
        assertEquals(2, secondOrder.getOrderId().intValue()); //Second order ID should match
        assertEquals("completed", secondOrder.getStatus()); //Second order status should match
        assertEquals(1, secondOrder.getQuantity()); //Second order quantity should match
        assertEquals(new BigDecimal("25000.00"), secondOrder.getTotalPrice()); //Second order price should match
        assertEquals(2, secondOrder.getMenu().getMenuId().intValue()); //Second order menu ID should match
        assertEquals("Latte", secondOrder.getMenu().getName()); //Second order menu ID should match
        assertEquals( 2, secondOrder.getUsers().getUserId().intValue()); //Second order user ID should match
        assertEquals("jane", secondOrder.getUsers().getName()); //Second order username should match
    } 
    
    @Test
    public void testFindAllException() {
        // Setup
        when(session.createQuery(anyString())).thenThrow(testException);

        // Execute
        List<Orders> result = daoOrders.findAll();

        // Verify
        assertNull("Result should be null on exception", result);
        verify(session).close();
    }

    @Test
    public void testFindPendingOrders() {
        // Setup dummy data
        Orders pendingOrder1 = new Orders();
        pendingOrder1.setOrderId(1);
        pendingOrder1.setStatus("pending");
        pendingOrder1.setQuantity(2);
        pendingOrder1.setTotalPrice(new BigDecimal("50000.00"));

        Menu menu1 = new Menu();
        menu1.setMenuId(1);
        menu1.setName("Espresso");
        pendingOrder1.setMenu(menu1);

        Users user1 = new Users();
        user1.setUserId(1);
        user1.setName("john");
        pendingOrder1.setUsers(user1);

        Orders pendingOrder2 = new Orders();
        pendingOrder2.setOrderId(2);
        pendingOrder2.setStatus("pending");
        pendingOrder2.setQuantity(1);
        pendingOrder2.setTotalPrice(new BigDecimal("25000.00"));

        Menu menu2 = new Menu();
        menu2.setMenuId(2);
        menu2.setName("Latte");
        pendingOrder2.setMenu(menu2);

        Users user2 = new Users();
        user2.setUserId(2);
        user2.setName("jane");
        pendingOrder2.setUsers(user2);

        List<Orders> expectedOrders = Arrays.asList(pendingOrder1, pendingOrder2);

        String expectedHql = "SELECT DISTINCT o FROM Orders o LEFT JOIN FETCH o.users LEFT JOIN FETCH o.menu WHERE o.status = 'pending'";
        when(session.createQuery(expectedHql)).thenReturn(query);
        when(query.list()).thenReturn(expectedOrders);

        // Execute
        List<Orders> result = daoOrders.findPendingOrders();

        // Verify basic operations
        verify(session).createQuery(expectedHql);
        verify(session).close();

        // Verify result
        assertNotNull(result); //Result should not be null
        assertEquals(2, result.size()); //Should return correct number of pending orders
        assertTrue(result.stream().allMatch(o -> "pending".equals(o.getStatus()))); //All orders should be pending

        // Verify first order details
        Orders firstOrder = result.get(0);
        assertEquals(Integer.valueOf(1), firstOrder.getOrderId()); //First order ID should match
        assertEquals(2, firstOrder.getQuantity()); //First order quantity should match
        assertEquals(new BigDecimal("50000.00"), firstOrder.getTotalPrice()); //First order total price should match
        assertEquals("Espresso", firstOrder.getMenu().getName()); //First order menu name should match
        assertEquals("john", firstOrder.getUsers().getName()); //First order username should match

        // Verify second order details
        Orders secondOrder = result.get(1);
        assertEquals(Integer.valueOf(2), secondOrder.getOrderId()); //Second order ID should match
        assertEquals(1, secondOrder.getQuantity()); //Second order quantity should match
        assertEquals(new BigDecimal("25000.00"), secondOrder.getTotalPrice()); //Second order total price should match
        assertEquals("Latte", secondOrder.getMenu().getName()); //Second order menu name should match
        assertEquals("jane", secondOrder.getUsers().getName()); //Second order username should match
    }
    
    @Test
    public void testFindPendingOrdersException() {
        // Setup
        when(session.createQuery(anyString())).thenThrow(testException);

        // Execute
        List<Orders> result = daoOrders.findPendingOrders();

        // Verify
        assertNull("Result should be null on exception", result);
        verify(session).close();
    }

    @Test
    public void testFindCompletedOrders() {
        // Setup dummy data
        Orders completedOrder1 = new Orders();
        completedOrder1.setOrderId(1);
        completedOrder1.setStatus("completed");
        completedOrder1.setQuantity(3);
        completedOrder1.setOrderDate(new Date());
        completedOrder1.setTotalPrice(new BigDecimal("75000.00"));

        Menu menu1 = new Menu();
        menu1.setMenuId(1);
        completedOrder1.setMenu(menu1);

        Users user1 = new Users();
        user1.setUserId(1);
        completedOrder1.setUsers(user1);

        Orders completedOrder2 = new Orders();
        completedOrder2.setOrderId(2);
        completedOrder2.setStatus("completed");
        completedOrder2.setQuantity(2);
        completedOrder2.setOrderDate(new Date());
        completedOrder2.setTotalPrice(new BigDecimal("50000.00"));

        Menu menu2 = new Menu();
        menu2.setMenuId(2);
        completedOrder2.setMenu(menu2);

        Users user2 = new Users();
        user2.setUserId(2);
        completedOrder2.setUsers(user2);

        List<Orders> expectedOrders = Arrays.asList(completedOrder1, completedOrder2);

        String expectedHql = "SELECT DISTINCT o FROM Orders o LEFT JOIN FETCH o.users LEFT JOIN FETCH o.menu WHERE o.status = 'completed'";
        when(session.createQuery(expectedHql)).thenReturn(query);
        when(query.list()).thenReturn(expectedOrders);

        // Execute
        List<Orders> result = daoOrders.findCompletedOrders();

        // Verify basic operations
        verify(session).createQuery(expectedHql);
        verify(session).close();

        // Verify result
        assertNotNull(result); //Result should not be null
        assertEquals(2, result.size()); //Should return correct number of completed orders
        assertTrue(result.stream().allMatch(o -> "completed".equals(o.getStatus()))); //All orders should be completed

        // Verify orders details
        Orders firstOrder = result.get(0);
        assertEquals(Integer.valueOf(1), firstOrder.getOrderId()); //First order ID should match
        assertEquals(3, firstOrder.getQuantity()); //First order quantity should match
        assertEquals(new BigDecimal("75000.00"), firstOrder.getTotalPrice()); //First order total price should match
        assertEquals(Integer.valueOf(1), firstOrder.getMenu().getMenuId()); //First order menu ID should match
        assertEquals(Integer.valueOf(1), firstOrder.getUsers().getUserId()); //First order user ID should match

        Orders secondOrder = result.get(1);
        assertEquals(Integer.valueOf(2), secondOrder.getOrderId()); //Second order ID should match
        assertEquals(2, secondOrder.getQuantity()); //Second order quantity should match
        assertEquals(new BigDecimal("50000.00"), secondOrder.getTotalPrice()); //Second order total price should match
        assertEquals(Integer.valueOf(2), secondOrder.getMenu().getMenuId()); //Second order menu ID should match
        assertEquals(Integer.valueOf(2), secondOrder.getUsers().getUserId()); //Second order user ID should match
    }

    @Test
    public void testFindCompletedOrdersException() {
        // Setup
        when(session.createQuery(anyString())).thenThrow(testException);

        // Execute
        List<Orders> result = daoOrders.findCompletedOrders();

        // Verify
        assertNull("Result should be null on exception", result);
        verify(session).close();
    }

    @Test
    public void testFindById() {
        // Setup dummy data
        Orders expectedOrder = new Orders();
        expectedOrder.setOrderId(1);
        expectedOrder.setStatus("pending");
        expectedOrder.setQuantity(2);
        expectedOrder.setOrderDate(new Date());
        expectedOrder.setTotalPrice(new BigDecimal("50000.00"));

        Menu menu = new Menu();
        menu.setMenuId(1);
        menu.setName("Espresso");
        expectedOrder.setMenu(menu);

        Users user = new Users();
        user.setUserId(1);
        user.setName("john_doe");
        expectedOrder.setUsers(user);

        when(session.get(Orders.class, 1L)).thenReturn(expectedOrder);

        // Execute
        Orders result = daoOrders.findById(1L);

        // Verify basic operations
        verify(session).get(Orders.class, 1L);
        verify(session).close();

        // Verify result
        assertNotNull(result); //Result should not be null
        assertEquals(Integer.valueOf(1), result.getOrderId()); //Order ID should match
        assertEquals("pending", result.getStatus()); //Order status should match
        assertEquals(2, result.getQuantity()); //Order quantity should match
        assertEquals(new BigDecimal("50000.00"), result.getTotalPrice()); //Order total price should match
        assertNotNull(result.getOrderDate()); //Order date should not be null
        assertEquals(Integer.valueOf(1), result.getMenu().getMenuId()); //Menu ID should match
        assertEquals(Integer.valueOf(1), result.getUsers().getUserId()); //User ID should match
    }

    @Test
    public void testFindByIdException() {
        // Setup
        Long orderId = 1L;
        when(session.get(Orders.class, orderId)).thenThrow(testException);

        // Execute
        Orders result = daoOrders.findById(orderId);

        // Verify
        assertNull("Result should be null on exception", result);
        verify(session).close();
    }

    @Test
    public void testUpdate() {
        // Setup dummy data
        Orders orderToUpdate = new Orders();
        orderToUpdate.setOrderId(1);
        orderToUpdate.setStatus("completed");
        orderToUpdate.setQuantity(3);
        orderToUpdate.setOrderDate(new Date());
        orderToUpdate.setTotalPrice(new BigDecimal("75000.00"));

        Menu menu = new Menu();
        menu.setMenuId(1);
        orderToUpdate.setMenu(menu);

        Users user = new Users();
        user.setUserId(1);
        orderToUpdate.setUsers(user);

        // Execute
        daoOrders.update(orderToUpdate);

        // Verify basic operations
        verify(session).beginTransaction();
        verify(session).update(orderToUpdate);
        verify(transaction).commit();
        verify(session).close();

        // Verify updated order
        ArgumentCaptor<Orders> orderCaptor = ArgumentCaptor.forClass(Orders.class);
        verify(session).update(orderCaptor.capture());
        Orders updatedOrder = orderCaptor.getValue();

        assertEquals(Integer.valueOf(1), updatedOrder.getOrderId()); //Order ID should match
        assertEquals("completed", updatedOrder.getStatus()); //Updated status should match
        assertEquals(3, updatedOrder.getQuantity()); //Updated quantity should match
        assertEquals(new BigDecimal("75000.00"), updatedOrder.getTotalPrice()); //Updated total price should match
        assertEquals(Integer.valueOf(1), updatedOrder.getMenu().getMenuId()); //Menu ID should match
        assertEquals(Integer.valueOf(1), updatedOrder.getUsers().getUserId()); //User ID should match
    }

    @Test
    public void testUpdateException() {
        // Setup dummy data
        Orders orderToUpdate = new Orders();
        orderToUpdate.setOrderId(1);
        orderToUpdate.setStatus("pending");
        orderToUpdate.setQuantity(2);
        orderToUpdate.setTotalPrice(new BigDecimal("50000.00"));

        Menu menu = new Menu();
        menu.setMenuId(1);
        orderToUpdate.setMenu(menu);

        Users user = new Users();
        user.setUserId(1);
        orderToUpdate.setUsers(user);

        doThrow(testException).when(session).update(any(Orders.class));

        // Execute
        daoOrders.update(orderToUpdate);

        // Verify operations and rollback
        verify(session).update(orderToUpdate);
        verify(transaction).rollback();
        verify(session).close();
    }

    @Test
    public void testDelete() {
        // Setup dummy data
        Orders orderToDelete = new Orders();
        orderToDelete.setOrderId(1);
        orderToDelete.setStatus("completed");
        orderToDelete.setQuantity(2);
        orderToDelete.setTotalPrice(new BigDecimal("50000.00"));

        Menu menu = new Menu();
        menu.setMenuId(1);
        orderToDelete.setMenu(menu);

        Users user = new Users();
        user.setUserId(1);
        orderToDelete.setUsers(user);

        when(session.get(Orders.class, 1L)).thenReturn(orderToDelete);

        // Execute
        daoOrders.delete(1L);

        // Verify basic operations
        verify(session).beginTransaction();
        verify(session).get(Orders.class, 1L);
        verify(session).delete(orderToDelete);
        verify(transaction).commit();
        verify(session).close();

        // Verify deleted order
        ArgumentCaptor<Orders> orderCaptor = ArgumentCaptor.forClass(Orders.class);
        verify(session).delete(orderCaptor.capture());
        Orders deletedOrder = orderCaptor.getValue();

        assertEquals(Integer.valueOf(1), deletedOrder.getOrderId()); //Deleted order ID should match
        assertEquals("completed", deletedOrder.getStatus()); //Deleted order status should match
        assertEquals(2, deletedOrder.getQuantity()); //Deleted order quantity should match
        assertEquals(new BigDecimal("50000.00"), deletedOrder.getTotalPrice()); //Deleted order total price should match
    }
    
    @Test
    public void testDeleteException() {
        // Setup dummy data
        Orders orderToDelete = new Orders();
        orderToDelete.setOrderId(1);
        orderToDelete.setStatus("pending");
        orderToDelete.setQuantity(2);
        orderToDelete.setTotalPrice(new BigDecimal("50000.00"));

        when(session.get(Orders.class, 1L)).thenReturn(orderToDelete);
        doThrow(testException).when(session).delete(any(Orders.class));

        // Execute
        daoOrders.delete(1L);

        // Verify operations and rollback
        verify(session).get(Orders.class, 1L);
        verify(session).delete(orderToDelete);
        verify(transaction).rollback();
        verify(session).close();
    }

    @Test
    public void testUpdateOrderStatusToCompleted() {
        // Setup dummy data
        Orders orderToUpdate = new Orders();
        orderToUpdate.setOrderId(1);
        orderToUpdate.setStatus("pending");
        orderToUpdate.setQuantity(2);
        orderToUpdate.setTotalPrice(new BigDecimal("50000.00"));

        Menu menu = new Menu();
        menu.setMenuId(1);
        orderToUpdate.setMenu(menu);

        Users user = new Users();
        user.setUserId(1);
        orderToUpdate.setUsers(user);

        when(session.get(Orders.class, 1)).thenReturn(orderToUpdate);

        // Execute
        daoOrders.updateOrderStatusToCompleted(1);

        // Verify basic operations
        verify(session).beginTransaction();
        verify(session).get(Orders.class, 1);
        verify(session).update(orderToUpdate);
        verify(transaction).commit();
        verify(session).close();

        // Verify updated status
        assertEquals("completed", orderToUpdate.getStatus()); //Status should be updated to completed

        // Verify success message
        ArgumentCaptor<FacesMessage> messageCaptor = ArgumentCaptor.forClass(FacesMessage.class);
        verify(facesContext).addMessage(eq(null), messageCaptor.capture());
        FacesMessage capturedMessage = messageCaptor.getValue();

        assertEquals(FacesMessage.SEVERITY_INFO, capturedMessage.getSeverity()); //Message severity should be INFO
        assertEquals("Success", capturedMessage.getSummary()); //Success summary should match
        assertEquals("Order status updated to completed", capturedMessage.getDetail()); //Success detail should match
    }

    @Test
    public void testUpdateOrderStatusToCompletedOrderNotFound() {
        // Setup
        when(session.get(Orders.class, 999)).thenReturn(null);

        // Execute
        daoOrders.updateOrderStatusToCompleted(999);

        // Verify basic operations
        verify(session).beginTransaction();
        verify(session).get(Orders.class, 999);
        verify(session, never()).update(any(Orders.class));
        verify(session).close();

        // Verify error message
        ArgumentCaptor<FacesMessage> messageCaptor = ArgumentCaptor.forClass(FacesMessage.class);
        verify(facesContext).addMessage(eq(null), messageCaptor.capture());
        FacesMessage capturedMessage = messageCaptor.getValue();

        assertEquals(FacesMessage.SEVERITY_ERROR, capturedMessage.getSeverity()); //Message severity should be ERROR
        assertEquals("Error", capturedMessage.getSummary()); //Error summary should match
        assertEquals("Order not found", capturedMessage.getDetail()); //Error detail should match
    }

    @Test
    public void testUpdateOrderStatusToCompletedException() {
        // Setup dummy data
        Orders orderToUpdate = new Orders();
        orderToUpdate.setOrderId(1);
        orderToUpdate.setStatus("pending");

        when(session.get(Orders.class, 1)).thenReturn(orderToUpdate);
        doThrow(testException).when(session).update(any(Orders.class));

        // Execute
        daoOrders.updateOrderStatusToCompleted(1);

        // Verify operations and rollback
        verify(session).beginTransaction();
        verify(session).get(Orders.class, 1);
        verify(transaction).rollback();
        verify(session).close();

        // Verify error message
        ArgumentCaptor<FacesMessage> messageCaptor = ArgumentCaptor.forClass(FacesMessage.class);
        verify(facesContext).addMessage(eq(null), messageCaptor.capture());
        FacesMessage capturedMessage = messageCaptor.getValue();

        assertEquals(FacesMessage.SEVERITY_ERROR, capturedMessage.getSeverity()); //Message severity should be ERROR
        assertEquals("Error", capturedMessage.getSummary()); //Error summary should match
        assertEquals("Failed to update order status", capturedMessage.getDetail()); //Error detail should match
    }
}
