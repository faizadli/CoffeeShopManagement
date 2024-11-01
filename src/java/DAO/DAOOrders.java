package DAO;

import org.hibernate.Session;
import org.hibernate.Transaction;
import java.util.List;
import org.hibernate.Query;
import pojo.CoffeShopUtil;
import pojo.Orders;
import javax.faces.context.FacesContext;
import javax.faces.application.FacesMessage;

public class DAOOrders {

    public void save(Orders order) {
        Transaction transaction = null;
        Session session = null;
        try {
            session = CoffeShopUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();
            session.save(order);
            transaction.commit();

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Order berhasil disimpan"));
            FacesContext.getCurrentInstance().getExternalContext().redirect("OrderData.xhtml");
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Gagal menyimpan order"));
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    public List<Orders> findAll() {
        Session session = null;
        try {
            session = CoffeShopUtil.getSessionFactory().openSession();
            String hql = "FROM Orders";
            Query query = session.createQuery(hql);
            List<Orders> orderList = query.list();
            return orderList;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    public List<Orders> findPendingOrders() {
        Session session = null;
        try {
            session = CoffeShopUtil.getSessionFactory().openSession();
            String hql = "SELECT DISTINCT o FROM Orders o "
                    + "LEFT JOIN FETCH o.users "
                    + "LEFT JOIN FETCH o.menu "
                    + "WHERE o.status = 'pending'";
            Query query = session.createQuery(hql);
            List<Orders> pendingOrderList = query.list();
            return pendingOrderList;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }
    
    public List<Orders> findCompletedOrders() {
        Session session = null;
        try {
            session = CoffeShopUtil.getSessionFactory().openSession();
            String hql = "SELECT DISTINCT o FROM Orders o "
                    + "LEFT JOIN FETCH o.users "
                    + "LEFT JOIN FETCH o.menu "
                    + "WHERE o.status = 'completed'";
            Query query = session.createQuery(hql);
            List<Orders> completedOrderList = query.list();
            return completedOrderList;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    public Orders findById(Long orderId) {
        Session session = null;
        try {
            session = CoffeShopUtil.getSessionFactory().openSession();
            Orders order = (Orders) session.get(Orders.class, orderId);
            return order;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    public void update(Orders order) {
        Transaction transaction = null;
        Session session = null;
        try {
            session = CoffeShopUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();
            session.update(order);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    public void delete(Long orderId) {
        Transaction transaction = null;
        Session session = null;
        try {
            session = CoffeShopUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();
            Orders order = (Orders) session.get(Orders.class, orderId);
            if (order != null) {
                session.delete(order);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    public void updateOrderStatusToCompleted(Integer orderId) {
        Transaction transaction = null;
        Session session = null;
        try {
            session = CoffeShopUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();
            Orders order = (Orders) session.get(Orders.class, orderId);
            if (order != null) {
                order.setStatus("completed");
                session.update(order);
                transaction.commit();
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Order status updated to completed"));
            } else {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Order not found"));
            }
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Failed to update order status"));
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }
}
