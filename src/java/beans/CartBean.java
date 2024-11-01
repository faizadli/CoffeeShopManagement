package beans;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import org.hibernate.Session;
import org.hibernate.Transaction;
import pojo.CoffeShopUtil;
import pojo.Menu;
import pojo.Orders;
import pojo.Users;

@ManagedBean
@SessionScoped
public class CartBean implements Serializable {

    private List<Orders> cartItems = new ArrayList<>();
    private List<OrderInfo> lastOrderInfo = new ArrayList<>();
    private BigDecimal lastOrderTotal = BigDecimal.ZERO;
    
    public static class OrderInfo implements Serializable {

        private String menuName;
        private int quantity;
        private BigDecimal totalPrice;

        public OrderInfo(String menuName, int quantity, BigDecimal totalPrice) {
            this.menuName = menuName;
            this.quantity = quantity;
            this.totalPrice = totalPrice;
        }

        // Getters
        public String getMenuName() {
            return menuName;
        }

        public int getQuantity() {
            return quantity;
        }

        public BigDecimal getTotalPrice() {
            return totalPrice;
        }
    }

    public String addToCart(Menu menu) {
        Session session = CoffeShopUtil.getSessionFactory().openSession();
        Transaction tx = null;

        try {
            tx = session.beginTransaction();

            Users user = getCurrentUser();

            if (user == null) {
                // Handle the case where user is not logged in
                return "login?faces-redirect=true"; // Redirect to login page
            }

            Orders existingOrder = findOrderByMenu(menu);
            if (existingOrder != null) {
                existingOrder.setQuantity(existingOrder.getQuantity() + 1);
                updateTotalPrice(existingOrder);
                session.update(existingOrder);
            } else {
                Orders newOrder = new Orders();
                newOrder.setMenu(menu);
                newOrder.setUsers(user);
                newOrder.setQuantity(1);
                newOrder.setOrderDate(new Date());
                newOrder.setStatus("pending");
                updateTotalPrice(newOrder);
                session.save(newOrder);
                cartItems.add(newOrder);
            }

            tx.commit();
            return "cart?faces-redirect=true"; // Navigate to cart page
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
            return "error"; // Navigate to error page
        } finally {
            session.close();
        }
    }

    private Users getCurrentUser() {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpSession httpSession = (HttpSession) context.getExternalContext().getSession(false);
        Users user = (Users) httpSession.getAttribute("loggedInUser");

        if (user == null) {
            // If user is not in session, try to get from database using a stored user ID
            Long userId = (Long) httpSession.getAttribute("userId");
            if (userId != null) {
                Session session = CoffeShopUtil.getSessionFactory().openSession();
                try {
                    user = (Users) session.get(Users.class, userId);
                    if (user != null) {
                        httpSession.setAttribute("loggedInUser", user);
                    }
                } finally {
                    session.close();
                }
            }
        }

        return user;
    }

    public void updateQuantity(Orders order, int newQuantity) {
        Session session = CoffeShopUtil.getSessionFactory().openSession();
        Transaction tx = null;

        try {
            tx = session.beginTransaction();

            if (newQuantity > 0) {
                order.setQuantity(newQuantity);
                updateTotalPrice(order);
                session.update(order);
            } else {
                session.delete(order);
                cartItems.remove(order);
            }

            tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    private void updateTotalPrice(Orders order) {
        BigDecimal price = order.getMenu().getPrice();
        BigDecimal quantity = new BigDecimal(order.getQuantity());
        order.setTotalPrice(price.multiply(quantity));
    }

    private Orders findOrderByMenu(Menu menu) {
        for (Orders order : cartItems) {
            if (order.getMenu().getMenuId().equals(menu.getMenuId())) {
                return order;
            }
        }
        return null;
    }

    public BigDecimal getCartTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (Orders order : cartItems) {
            total = total.add(order.getTotalPrice());
        }
        return total;
    }

    public List<Orders> getCartItems() {
        return cartItems;
    }

    public void clearCart() {
        lastOrderInfo.clear();
        lastOrderTotal = BigDecimal.ZERO;
        for (Orders order : cartItems) {
            lastOrderInfo.add(new OrderInfo(order.getMenu().getName(), order.getQuantity(), order.getTotalPrice()));
            lastOrderTotal = lastOrderTotal.add(order.getTotalPrice());
        }
        cartItems.clear();
    }
    
    public List<OrderInfo> getLastOrderInfo() {
        return lastOrderInfo;
    }

    public BigDecimal getLastOrderTotal() {
        return lastOrderTotal;
    }

    public void clearLastOrder() {
        lastOrderInfo.clear();
        lastOrderTotal = BigDecimal.ZERO;
    }

    public boolean isCartEmpty() {
        return cartItems.isEmpty();
    }
}
