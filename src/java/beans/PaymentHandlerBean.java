package beans;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.Session;
import org.hibernate.Transaction;
import pojo.CoffeShopUtil;
import pojo.Orders;
import pojo.Payment;

@ManagedBean
@ViewScoped
public class PaymentHandlerBean implements Serializable {

    @ManagedProperty(value = "#{cartBean}")
    private CartBean cartBean;

    private String paymentMethod;

    public String processPayment() {
        if (cartBean.isCartEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Your cart is empty. Cannot process payment."));
            return null;
        }
        if (paymentMethod == null || paymentMethod.isEmpty()) {
            // Handle error: payment method not selected
            return null;
        }

        Session session = CoffeShopUtil.getSessionFactory().openSession();
        Transaction tx = null;

        try {
            tx = session.beginTransaction();

            for (Orders order : cartBean.getCartItems()) {
                order.setStatus("pending");
                session.update(order);

                Payment payment = new Payment();
                payment.setOrders(order);
                payment.setPaymentMethod(paymentMethod);
                payment.setPaymentDate(new Date());
                payment.setAmountPaid(order.getTotalPrice());
                session.save(payment);
            }

            tx.commit();
            
            // Store the payment method in the session
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("lastPaymentMethod", paymentMethod);

            // Clear the cart after successful payment
            cartBean.clearCart();

            return "paymentSuccess?faces-redirect=true";
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "An error occurred during payment processing."));
            return null;
        } finally {
            session.close();
        }
    }

    public void downloadReceipt() {
        // Generate receipt content (you may want to use a template engine in a real application)
        String receiptContent = generateReceiptContent();

        FacesContext facesContext = FacesContext.getCurrentInstance();
        HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();

        response.setContentType("text/plain");
        response.setHeader("Content-Disposition", "attachment;filename=receipt.txt");

        try {
            response.getOutputStream().write(receiptContent.getBytes());
            response.getOutputStream().flush();
            response.getOutputStream().close();
            facesContext.responseComplete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String generateReceiptContent() {
        StringBuilder receipt = new StringBuilder();
        receipt.append("Payment Receipt\n");
        receipt.append("Date: ").append(new Date()).append("\n");
        receipt.append("Payment Method: ").append(paymentMethod != null ? paymentMethod : "Unknown").append("\n");
        receipt.append("Items:\n");

        for (CartBean.OrderInfo orderInfo : cartBean.getLastOrderInfo()) {
            receipt.append(orderInfo.getMenuName())
                    .append(" x ").append(orderInfo.getQuantity())
                    .append(" = ").append(orderInfo.getTotalPrice()).append("\n");
        }

        receipt.append("Total: ").append(cartBean.getLastOrderTotal()).append("\n");

        return receipt.toString();
    }
    
    @PostConstruct
    public void init() {
        // Retrieve the payment method from the session when the bean is created
        this.paymentMethod = (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("lastPaymentMethod");
    }

    // Getters and setters
    public void setCartBean(CartBean cartBean) {
        this.cartBean = cartBean;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}
