package beans;

import DAO.DAOOrders;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import pojo.Orders;
import pojo.Menu;
import pojo.Users;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({FacesContext.class})
public class OrderBeanTest {

    @Mock
    private DAOOrders daoOrders;

    @Mock
    private FacesContext facesContext;

    private OrderBean orderBean;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        PowerMockito.mockStatic(FacesContext.class);
        when(FacesContext.getCurrentInstance()).thenReturn(facesContext);

        orderBean = spy(new OrderBean());
        orderBean.setDaoOrders(daoOrders);
    }

    @Test
    public void testGetOrderList() {
        // Setup dummy data
        Orders order1 = new Orders();
        order1.setOrderId(1);
        order1.setQuantity(2);
        order1.setStatus("pending");
        order1.setOrderDate(new Date());
        order1.setTotalPrice(new BigDecimal("50000.00"));

        Menu menu1 = new Menu();
        menu1.setMenuId(1);
        menu1.setName("Espresso");
        order1.setMenu(menu1);

        Users user1 = new Users();
        user1.setUserId(1);
        user1.setName("John Doe");
        order1.setUsers(user1);

        List<Orders> testOrders = new ArrayList<>();
        testOrders.add(order1);

        // Setup mock
        when(daoOrders.findAll()).thenReturn(testOrders);
        doReturn(testOrders).when(orderBean).getOrderList();

        // Execute
        List<Orders> result = orderBean.getOrderList();

        // Verify
        assertNotNull(result); //Result should not be null
        assertEquals(1, result.size()); //Should have correct size

        Orders returnedOrder = result.get(0);
        assertEquals(1, returnedOrder.getOrderId().intValue()); //Order ID should match
        assertEquals(2, returnedOrder.getQuantity()); //Quantity should match
        assertEquals("pending", returnedOrder.getStatus()); //Status should match
        assertEquals(new BigDecimal("50000.00"), returnedOrder.getTotalPrice()); //Price should match
    }

    @Test
    public void testGetPendingOrders() {
        // Setup dummy data
        Orders pendingOrder = new Orders();
        pendingOrder.setOrderId(1);
        pendingOrder.setQuantity(2);
        pendingOrder.setStatus("pending");
        pendingOrder.setOrderDate(new Date());
        pendingOrder.setTotalPrice(new BigDecimal("50000.00"));

        Menu menu = new Menu();
        menu.setMenuId(1);
        menu.setName("Latte");
        pendingOrder.setMenu(menu);

        Users user = new Users();
        user.setUserId(1);
        user.setName("Jane Doe");
        pendingOrder.setUsers(user);

        List<Orders> pendingOrders = new ArrayList<>();
        pendingOrders.add(pendingOrder);

        // Setup mock
        when(daoOrders.findPendingOrders()).thenReturn(pendingOrders);
        doReturn(pendingOrders).when(orderBean).getPendingOrders();

        // Execute
        List<Orders> result = orderBean.getPendingOrders();

        // Verify
        assertNotNull(result); //Result should not be null
        assertEquals(1, result.size()); //Should have correct size

        Orders returnedOrder = result.get(0);
        assertEquals(1, returnedOrder.getOrderId().intValue()); //Order ID should match
        assertEquals("pending", returnedOrder.getStatus()); //Status should be pending
        assertEquals("Latte", returnedOrder.getMenu().getName()); //Menu name should match
        assertEquals("Jane Doe", returnedOrder.getUsers().getName()); //User name should match
    }

    @Test
    public void testGetCompletedOrders() {
        // Setup dummy data
        Orders completedOrder = new Orders();
        completedOrder.setOrderId(1);
        completedOrder.setQuantity(3);
        completedOrder.setStatus("completed");
        completedOrder.setOrderDate(new Date());
        completedOrder.setTotalPrice(new BigDecimal("75000.00"));

        Menu menu = new Menu();
        menu.setMenuId(1);
        menu.setName("Cappuccino");
        completedOrder.setMenu(menu);

        Users user = new Users();
        user.setUserId(1);
        user.setName("Alice Smith");
        completedOrder.setUsers(user);

        List<Orders> completedOrders = new ArrayList<>();
        completedOrders.add(completedOrder);

        // Setup mock
        when(daoOrders.findCompletedOrders()).thenReturn(completedOrders);
        doReturn(completedOrders).when(orderBean).getCompletedOrders();

        // Execute
        List<Orders> result = orderBean.getCompletedOrders();

        // Verify
        assertNotNull(result); //Result should not be null
        assertEquals(1, result.size()); //Should have correct size

        Orders returnedOrder = result.get(0);
        assertEquals(1, returnedOrder.getOrderId().intValue()); //Order ID should match
        assertEquals("completed", returnedOrder.getStatus()); //Status should be completed
        assertEquals("Cappuccino", returnedOrder.getMenu().getName()); //Menu name should match
        assertEquals("Alice Smith", returnedOrder.getUsers().getName()); //User name should match
    }

    @Test
    public void testSave() {
        // Setup dummy data
        Orders orderToSave = new Orders();
        orderToSave.setOrderId(1);
        orderToSave.setQuantity(2);
        orderToSave.setStatus("pending");
        orderToSave.setOrderDate(new Date());
        orderToSave.setTotalPrice(new BigDecimal("50000.00"));

        Menu menu = new Menu();
        menu.setMenuId(1);
        menu.setName("Espresso");
        orderToSave.setMenu(menu);

        orderBean.setOrder(orderToSave);

        List<Orders> updatedList = new ArrayList<>();
        updatedList.add(orderToSave);

        when(daoOrders.findAll()).thenReturn(updatedList);
        doNothing().when(daoOrders).save(any(Orders.class));

        // Execute
        orderBean.save();

        // Verify operations
        verify(daoOrders).save(orderToSave); //Should call save with correct order
        verify(daoOrders).findAll(); //Should refresh order list

        // Verify success message
        ArgumentCaptor<FacesMessage> messageCaptor = ArgumentCaptor.forClass(FacesMessage.class);
        verify(facesContext).addMessage(eq(null), messageCaptor.capture());
        FacesMessage capturedMessage = messageCaptor.getValue();

        assertEquals("Order saved successfully.", capturedMessage.getSummary()); //Message should match
    }

    @Test
    public void testProcessOrder() {
        // Setup dummy data
        Integer orderId = 1;
        Orders pendingOrder = new Orders();
        pendingOrder.setOrderId(orderId);
        pendingOrder.setStatus("pending");
        pendingOrder.setQuantity(2);
        pendingOrder.setTotalPrice(new BigDecimal("50000.00"));

        List<Orders> updatedPendingOrders = new ArrayList<>();
        updatedPendingOrders.add(pendingOrder);

        when(daoOrders.findPendingOrders()).thenReturn(updatedPendingOrders);
        doNothing().when(daoOrders).updateOrderStatusToCompleted(any(Integer.class));

        // Execute
        orderBean.processOrder(orderId);

        // Verify
        verify(daoOrders).updateOrderStatusToCompleted(orderId); //Should call update with correct ID
        verify(daoOrders).findPendingOrders(); //Should refresh pending orders list
    }

    @Test
    public void testDelete() {
        // Setup dummy data
        Long orderId = 1L;
        Orders orderToDelete = new Orders();
        orderToDelete.setOrderId(1);
        orderToDelete.setStatus("pending");
        orderToDelete.setQuantity(2);
        orderToDelete.setTotalPrice(new BigDecimal("50000.00"));

        List<Orders> updatedList = new ArrayList<>();
        updatedList.add(orderToDelete);

        when(daoOrders.findAll()).thenReturn(updatedList);
        doNothing().when(daoOrders).delete(any(Long.class));

        // Execute
        orderBean.delete(orderId);

        // Verify operations
        verify(daoOrders).delete(orderId); //Should call delete with correct ID
        verify(daoOrders).findAll(); //Should refresh order list

        // Verify success message
        ArgumentCaptor<FacesMessage> messageCaptor = ArgumentCaptor.forClass(FacesMessage.class);
        verify(facesContext).addMessage(eq(null), messageCaptor.capture());
        FacesMessage capturedMessage = messageCaptor.getValue();

        assertEquals("Order deleted successfully.", capturedMessage.getSummary()); //Message should match
    }

    @Test
    public void testSetterGetter() {
        // Setup dummy data
        Orders order = new Orders();
        order.setOrderId(1);
        order.setQuantity(2);
        order.setStatus("pending");
        order.setTotalPrice(new BigDecimal("50000.00"));

        // Execute
        orderBean.setOrder(order);
        Orders result = orderBean.getOrder();

        // Verify
        assertNotNull(result); //Result should not be null
        assertEquals(1, result.getOrderId().intValue()); //Order ID should match
        assertEquals(2, result.getQuantity()); //Quantity should match
        assertEquals("pending", result.getStatus()); //Status should match
        assertEquals(new BigDecimal("50000.00"), result.getTotalPrice()); //Price should match
    }
}
