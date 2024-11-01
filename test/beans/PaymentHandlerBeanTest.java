package beans;

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
import pojo.Orders;
import pojo.Payment;

import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({CoffeShopUtil.class, FacesContext.class})
public class PaymentHandlerBeanTest {

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
    private CartBean cartBean;
    @Mock
    private HttpServletResponse response;

    private PaymentHandlerBean paymentHandlerBean;

    @Before
    public void setUp() {
        PowerMockito.mockStatic(CoffeShopUtil.class);
        PowerMockito.mockStatic(FacesContext.class);

        when(CoffeShopUtil.getSessionFactory()).thenReturn(sessionFactory);
        when(sessionFactory.openSession()).thenReturn(session);
        when(session.beginTransaction()).thenReturn(transaction);

        when(FacesContext.getCurrentInstance()).thenReturn(facesContext);
        when(facesContext.getExternalContext()).thenReturn(externalContext);

        cartBean = mock(CartBean.class);
        paymentHandlerBean = new PaymentHandlerBean();
        paymentHandlerBean.setCartBean(cartBean);
        paymentHandlerBean.setPaymentMethod("Credit Card");
    }

    @Test
    public void testProcessPayment() {
        // Arrange
        List<Orders> cartItems = new ArrayList<>();
        Orders order = new Orders();
        order.setTotalPrice(new BigDecimal("10.00"));
        cartItems.add(order);
        when(cartBean.getCartItems()).thenReturn(cartItems);
        when(cartBean.isCartEmpty()).thenReturn(false);

        // Act
        String result = paymentHandlerBean.processPayment();

        // Assert
        assertEquals("paymentSuccess?faces-redirect=true", result);
        verify(session).update(order);
        verify(session).save(any(Payment.class));
        verify(transaction).commit();
        verify(cartBean).clearCart();
    }

    @Test
    public void testProcessPaymentEmptyCart() {
        // Arrange
        when(cartBean.isCartEmpty()).thenReturn(true);

        // Act
        String result = paymentHandlerBean.processPayment();

        // Assert
        assertNull(result);
        verify(facesContext).addMessage(eq(null), any(FacesMessage.class));
    }

    @Test
    public void testDownloadReceipt() throws Exception {
        // Arrange
        PaymentHandlerBean paymentHandlerBean = new PaymentHandlerBean();
        paymentHandlerBean.setCartBean(cartBean);
        paymentHandlerBean.setPaymentMethod("Credit Card");

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(outputStream);

        HttpServletResponse response = mock(HttpServletResponse.class);
        ExternalContext externalContext = mock(ExternalContext.class);

        when(facesContext.getExternalContext()).thenReturn(externalContext);
        when(externalContext.getResponse()).thenReturn(response);
        when(response.getWriter()).thenReturn(writer);

        List<CartBean.OrderInfo> lastOrderInfo = new ArrayList<>();
        lastOrderInfo.add(new CartBean.OrderInfo("Test Item", 1, new BigDecimal("10.00")));
        when(cartBean.getLastOrderInfo()).thenReturn(lastOrderInfo);
        when(cartBean.getLastOrderTotal()).thenReturn(new BigDecimal("10.00"));

        // Act
        paymentHandlerBean.downloadReceipt();

        // Assert
        verify(response).setContentType("text/plain");
        verify(response).setHeader("Content-Disposition", "attachment;filename=receipt.txt");
    }

    @Test
    public void testGetSetPaymentMethod() {
        // Arrange
        String newPaymentMethod = "PayPal";

        // Act
        paymentHandlerBean.setPaymentMethod(newPaymentMethod);
        String result = paymentHandlerBean.getPaymentMethod();

        // Assert
        assertEquals(newPaymentMethod, result);
    }
    
    @Test
    public void testProcessPaymentWithException() {
        // Arrange
        List<Orders> cartItems = new ArrayList<>();
        Orders order = new Orders();
        order.setTotalPrice(new BigDecimal("10.00"));
        cartItems.add(order);

        when(cartBean.getCartItems()).thenReturn(cartItems);
        when(cartBean.isCartEmpty()).thenReturn(false);
        when(session.save(any(Payment.class))).thenThrow(new RuntimeException("Database error"));

        // Act
        String result = paymentHandlerBean.processPayment();

        // Assert
        assertNull(result);
        verify(transaction).rollback();
        verify(facesContext).addMessage(eq(null), any(FacesMessage.class));
    }

    @Test
    public void testDownloadReceiptWithIOException() throws Exception {
        // Arrange
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(outputStream);

        when(facesContext.getExternalContext()).thenReturn(externalContext);
        when(externalContext.getResponse()).thenReturn(response);
        when(response.getOutputStream()).thenThrow(new IOException("Write error"));

        List<CartBean.OrderInfo> lastOrderInfo = new ArrayList<>();
        lastOrderInfo.add(new CartBean.OrderInfo("Test Item", 1, new BigDecimal("10.00")));
        when(cartBean.getLastOrderInfo()).thenReturn(lastOrderInfo);
        when(cartBean.getLastOrderTotal()).thenReturn(new BigDecimal("10.00"));

        // Act
        paymentHandlerBean.downloadReceipt();

        // Assert
        verify(response).setContentType("text/plain");
        verify(response).setHeader("Content-Disposition", "attachment;filename=receipt.txt");
    }

    @Test
    public void testProcessPaymentWithNullPaymentMethod() {
        // Arrange
        List<Orders> cartItems = new ArrayList<>();
        Orders order = new Orders();
        order.setTotalPrice(new BigDecimal("10.00"));
        cartItems.add(order);

        when(cartBean.getCartItems()).thenReturn(cartItems);
        when(cartBean.isCartEmpty()).thenReturn(false);
        paymentHandlerBean.setPaymentMethod(null);

        // Act
        String result = paymentHandlerBean.processPayment();

        // Assert
        assertNull(result);
        verify(session, never()).save(any(Payment.class));
        verify(transaction, never()).commit();
    }
}