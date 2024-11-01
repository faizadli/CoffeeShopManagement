package beans;

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
import pojo.Menu;
import pojo.Orders;
import pojo.Users;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({CoffeShopUtil.class, FacesContext.class})
public class CartBeanTest {

    @Mock
    private SessionFactory sessionFactory;

    @Mock
    private Session session;

    @Mock
    private Transaction transaction;

    @Mock
    private FacesContext facesContext;

    @Mock
    private ExternalContext externalContext;

    @Mock
    private HttpSession httpSession;

    private CartBean cartBean;
    private Users testUser;
    private Menu testMenu;
    private RuntimeException testException;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        
        cartBean = new CartBean();
        testException = new RuntimeException("Test exception");

        // Setup test user
        testUser = new Users();
        testUser.setUserId(1);
        testUser.setEmail("test@test.com");

        // Setup test menu
        testMenu = new Menu();
        testMenu.setMenuId(1);
        testMenu.setName("Test Menu");
        testMenu.setPrice(new BigDecimal("10.00"));

        // Mock static methods
        mockStatic(CoffeShopUtil.class);
        mockStatic(FacesContext.class);

        // Setup basic mocks
        when(CoffeShopUtil.getSessionFactory()).thenReturn(sessionFactory);
        when(sessionFactory.openSession()).thenReturn(session);
        when(session.beginTransaction()).thenReturn(transaction);
        when(FacesContext.getCurrentInstance()).thenReturn(facesContext);
        when(facesContext.getExternalContext()).thenReturn(externalContext);
        when(externalContext.getSession(false)).thenReturn(httpSession);
    }

    @Test
    public void testAddToCartNewItem() {
        // Setup
        when(httpSession.getAttribute("loggedInUser")).thenReturn(testUser);
        when(session.save(any(Orders.class))).thenReturn(1L);

        // Execute
        String result = cartBean.addToCart(testMenu);

        // Verify
        assertEquals("cart?faces-redirect=true", result);
        assertEquals(1, cartBean.getCartItems().size());
        
        Orders addedOrder = cartBean.getCartItems().get(0);
        assertEquals(testMenu, addedOrder.getMenu());
        assertEquals(1, addedOrder.getQuantity());
        assertEquals(new BigDecimal("10.00"), addedOrder.getTotalPrice());
        assertEquals(testUser, addedOrder.getUsers());
        assertEquals("pending", addedOrder.getStatus());
        
        verify(session).save(any(Orders.class));
        verify(transaction).commit();
        verify(session).close();
    }

    @Test
    public void testAddToCartExistingItem() {
        // Setup
        when(httpSession.getAttribute("loggedInUser")).thenReturn(testUser);
        
        // Add item first time
        cartBean.addToCart(testMenu);
        
        // Execute - add same item second time
        String result = cartBean.addToCart(testMenu);

        // Verify
        assertEquals("cart?faces-redirect=true", result);
        assertEquals(1, cartBean.getCartItems().size());
        
        Orders updatedOrder = cartBean.getCartItems().get(0);
        assertEquals(2, updatedOrder.getQuantity());
        assertEquals(new BigDecimal("20.00"), updatedOrder.getTotalPrice());
        
        verify(session, times(1)).save(any(Orders.class));
        verify(session, times(1)).update(any(Orders.class));
        verify(transaction, times(2)).commit();
    }

    @Test
    public void testAddToCartUserNotLoggedIn() {
        // Setup
        when(httpSession.getAttribute("loggedInUser")).thenReturn(null);
        when(httpSession.getAttribute("userId")).thenReturn(null);

        // Execute
        String result = cartBean.addToCart(testMenu);

        // Verify
        assertEquals("login?faces-redirect=true", result);
        assertTrue(cartBean.getCartItems().isEmpty());
        verify(session, never()).save(any(Orders.class));
        verify(transaction, never()).commit();
        verify(session).close();
    }

    @Test
    public void testAddToCartException() {
        // Setup
        when(httpSession.getAttribute("loggedInUser")).thenReturn(testUser);
        doThrow(testException).when(session).save(any(Orders.class));

        // Execute
        String result = cartBean.addToCart(testMenu);

        // Verify
        assertEquals("error", result);
        verify(transaction).rollback();
        verify(session).close();
    }

    @Test
    public void testUpdateQuantityIncrease() {
        // Setup
        Orders order = new Orders();
        order.setMenu(testMenu);
        order.setQuantity(1);
        order.setTotalPrice(new BigDecimal("10.00"));
        cartBean.getCartItems().add(order);

        // Execute
        cartBean.updateQuantity(order, 2);

        // Verify
        assertEquals(2, order.getQuantity());
        assertEquals(new BigDecimal("20.00"), order.getTotalPrice());
        verify(session).update(order);
        verify(transaction).commit();
        verify(session).close();
    }

    @Test
    public void testUpdateQuantityRemove() {
        // Setup
        Orders order = new Orders();
        order.setMenu(testMenu);
        order.setQuantity(1);
        order.setTotalPrice(new BigDecimal("10.00"));
        cartBean.getCartItems().add(order);

        // Execute
        cartBean.updateQuantity(order, 0);

        // Verify
        assertTrue(cartBean.getCartItems().isEmpty());
        verify(session).delete(order);
        verify(transaction).commit();
        verify(session).close();
    }

    @Test
    public void testUpdateQuantityException() {
        // Setup
        Orders order = new Orders();
        order.setMenu(testMenu);
        order.setQuantity(1);
        doThrow(testException).when(session).update(any(Orders.class));

        // Execute
        cartBean.updateQuantity(order, 2);

        // Verify
        verify(transaction).rollback();
        verify(session).close();
    }

    @Test
    public void testGetCartTotal() {
        // Setup
        Orders order1 = new Orders();
        order1.setMenu(testMenu);
        order1.setQuantity(1);
        order1.setTotalPrice(new BigDecimal("10.00"));

        Menu menu2 = new Menu();
        menu2.setPrice(new BigDecimal("20.00"));
        Orders order2 = new Orders();
        order2.setMenu(menu2);
        order2.setQuantity(1);
        order2.setTotalPrice(new BigDecimal("20.00"));

        cartBean.getCartItems().add(order1);
        cartBean.getCartItems().add(order2);

        // Execute
        BigDecimal total = cartBean.getCartTotal();

        // Verify
        assertEquals(new BigDecimal("30.00"), total);
    }

    @Test
    public void testClearCart() {
        // Setup
        Orders order = new Orders();
        order.setMenu(testMenu);
        order.setQuantity(1);
        order.setTotalPrice(new BigDecimal("10.00"));
        cartBean.getCartItems().add(order);

        // Execute
        cartBean.clearCart();

        // Verify
        assertTrue(cartBean.getCartItems().isEmpty());
        assertEquals(1, cartBean.getLastOrderInfo().size());
        
        CartBean.OrderInfo lastOrder = cartBean.getLastOrderInfo().get(0);
        assertEquals(testMenu.getName(), lastOrder.getMenuName());
        assertEquals(1, lastOrder.getQuantity());
        assertEquals(new BigDecimal("10.00"), lastOrder.getTotalPrice());
        assertEquals(new BigDecimal("10.00"), cartBean.getLastOrderTotal());
    }

    @Test
    public void testClearLastOrder() {
        // Setup
        Orders order = new Orders();
        order.setMenu(testMenu);
        order.setQuantity(1);
        order.setTotalPrice(new BigDecimal("10.00"));
        cartBean.getCartItems().add(order);
        cartBean.clearCart();

        // Execute
        cartBean.clearLastOrder();

        // Verify
        assertTrue(cartBean.getLastOrderInfo().isEmpty());
        assertEquals(BigDecimal.ZERO, cartBean.getLastOrderTotal());
    }

    @Test
    public void testIsCartEmpty() {
        // Initial state
        assertTrue(cartBean.isCartEmpty());

        // Add item
        Orders order = new Orders();
        cartBean.getCartItems().add(order);
        assertFalse(cartBean.isCartEmpty());

        // Clear cart
        cartBean.getCartItems().clear();
        assertTrue(cartBean.isCartEmpty());
    }

    @Test
    public void testGetCurrentUserFromSession() {
        // Setup
        when(httpSession.getAttribute("loggedInUser")).thenReturn(testUser);

        // Execute
        cartBean.addToCart(testMenu);

        // Verify
        verify(httpSession).getAttribute("loggedInUser");
        verify(httpSession, never()).getAttribute("userId");
    }

    @Test
    public void testGetCurrentUserFromDatabase() {
        // Setup
        when(httpSession.getAttribute("loggedInUser")).thenReturn(null);
        when(httpSession.getAttribute("userId")).thenReturn(1L);
        when(session.get(Users.class, 1L)).thenReturn(testUser);

        // Execute
        cartBean.addToCart(testMenu);

        // Verify
        verify(httpSession).getAttribute("loggedInUser");
        verify(httpSession).getAttribute("userId");
        verify(session).get(Users.class, 1L);
        verify(httpSession).setAttribute("loggedInUser", testUser);
    }
}