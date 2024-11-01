package beans;

import DAO.DAOOrders;
import pojo.Orders;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import java.util.List;

@ManagedBean
@ViewScoped
public class OrderBean {

    private Orders order = new Orders();
    private DAOOrders daoOrders = new DAOOrders();
    private List<Orders> orderList;
    private List<Orders> pendingOrders;
    private List<Orders> completedOrders;

    public OrderBean() {
        orderList = daoOrders.findAll();
        loadPendingOrders();
        loadCompletedOrders();
    }
    
    public void setDaoOrders(DAOOrders daoOrders) {
        this.daoOrders = daoOrders;
    }

    // Getters and Setters
    public Orders getOrder() {
        return order;
    }

    public void setOrder(Orders order) {
        this.order = order;
    }

    public List<Orders> getOrderList() {
        return orderList;
    }

    private void loadPendingOrders() {
        pendingOrders = daoOrders.findPendingOrders();
    }

    public List<Orders> getPendingOrders() {
        return pendingOrders;
    }
    
    private void loadCompletedOrders() {
        completedOrders = daoOrders.findCompletedOrders();
    }

    public List<Orders> getCompletedOrders() {
        return completedOrders;
    }

    public void save() {
        daoOrders.save(order);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Order saved successfully."));
        orderList = daoOrders.findAll();
    }

    public void processOrder(Integer orderId) {
        daoOrders.updateOrderStatusToCompleted(orderId);
        loadPendingOrders();
    }

    public void delete(Long orderId) {
        daoOrders.delete(orderId);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Order deleted successfully."));
        orderList = daoOrders.findAll();
    }
}
