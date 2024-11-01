package pojo;

import java.math.BigDecimal;
import java.util.Date;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;
import static org.junit.Assert.*;

@RunWith(PowerMockRunner.class)
public class PaymentTest {

    private Payment payment;
    private Orders mockOrders;
    private Date testDate;
    private BigDecimal testAmount;

    @Before
    public void setUp() {
        // Initialize test data
        testDate = new Date();
        testAmount = new BigDecimal("100.50");
        mockOrders = new Orders();
        payment = new Payment();
    }

    @Test
    public void testGetPaymentId() {
        // Arrange
        Integer expectedId = 1;
        payment.setPaymentId(expectedId);

        // Act
        Integer actualId = payment.getPaymentId();

        // Assert
        assertEquals(expectedId, actualId);
    }

    @Test
    public void testGetOrders() {
        // Arrange
        payment.setOrders(mockOrders);

        // Act
        Orders result = payment.getOrders();

        // Assert
        assertEquals(mockOrders, result);
    }

    @Test
    public void testGetPaymentMethod() {
        // Arrange
        String expectedMethod = "Credit Card";
        payment.setPaymentMethod(expectedMethod);

        // Act
        String actualMethod = payment.getPaymentMethod();

        // Assert
        assertEquals(expectedMethod, actualMethod);
    }

    @Test
    public void testGetPaymentDate() {
        // Arrange
        payment.setPaymentDate(testDate);

        // Act
        Date actualDate = payment.getPaymentDate();

        // Assert
        assertEquals(testDate, actualDate);
    }

    @Test
    public void testGetAmountPaid() {
        // Arrange
        payment.setAmountPaid(testAmount);

        // Act
        BigDecimal actualAmount = payment.getAmountPaid();

        // Assert
        assertEquals(testAmount, actualAmount);
    }

    @Test
    public void testPaymentConstructorWithThreeParams() {
        // Arrange
        String paymentMethod = "Credit Card";

        // Act
        Payment newPayment = new Payment(paymentMethod, testDate, testAmount);

        // Assert
        assertEquals(paymentMethod, newPayment.getPaymentMethod());
        assertEquals(testDate, newPayment.getPaymentDate());
        assertEquals(testAmount, newPayment.getAmountPaid());
    }

    @Test
    public void testPaymentConstructorWithFourParams() {
        // Arrange
        String paymentMethod = "Debit Card";

        // Act
        Payment newPayment = new Payment(mockOrders, paymentMethod, testDate, testAmount);

        // Assert
        assertEquals(mockOrders, newPayment.getOrders());
        assertEquals(paymentMethod, newPayment.getPaymentMethod());
        assertEquals(testDate, newPayment.getPaymentDate());
        assertEquals(testAmount, newPayment.getAmountPaid());
    }
}
