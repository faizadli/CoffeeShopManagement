package DAO;

import org.hibernate.Session;
import org.hibernate.Transaction;
import java.util.List;
import java.util.logging.Level;
import org.hibernate.Query;
import pojo.CoffeShopUtil;
import pojo.Menu;
import javax.faces.context.FacesContext;
import javax.faces.application.FacesMessage;
import java.util.logging.Logger;

public class DAOMenu {

    private static final Logger LOGGER = Logger.getLogger(DAOMenu.class.getName());

    protected Session getSession() {
        return CoffeShopUtil.getSessionFactory().openSession();
    }
    
    public void save(Menu menu) {
        Transaction transaction = null;
        Session session = null;
        try {
            LOGGER.log(Level.INFO, "Attempting to save menu: {0}", menu.getName());
            session = getSession();
            transaction = session.beginTransaction();

            LOGGER.log(Level.INFO, "Saving menu to database");
            session.save(menu);

            LOGGER.log(Level.INFO, "Committing transaction");
            transaction.commit();

            LOGGER.log(Level.INFO, "Menu saved successfully: {0}", menu.getName());
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Data berhasil disimpan"));

            FacesContext.getCurrentInstance().getExternalContext().redirect("MenuData.xhtml");

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error saving menu: " + menu.getName(), e);
            if (transaction != null) {
                LOGGER.log(Level.INFO, "Rolling back transaction");
                transaction.rollback();
            }
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Gagal menyimpan data: " + e.getMessage()));
        } finally {
            if (session != null) {
                LOGGER.log(Level.INFO, "Closing session");
                session.close();
            }
        }
    }

    public void update(Menu menu) {
        Transaction transaction = null;
        Session session = null;
        try {
            LOGGER.log(Level.INFO, "Attempting to update menu: {0}", menu.getName());
            session = CoffeShopUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();

            LOGGER.log(Level.INFO, "Updating menu in database");
            session.update(menu);

            LOGGER.log(Level.INFO, "Committing transaction");
            transaction.commit();

            LOGGER.log(Level.INFO, "Menu updated successfully: {0}", menu.getName());
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Menu updated successfully"));
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error updating menu: " + menu.getName(), e);
            if (transaction != null) {
                LOGGER.log(Level.INFO, "Rolling back transaction");
                transaction.rollback();
            }
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Failed to update menu: " + e.getMessage()));
        } finally {
            if (session != null) {
                LOGGER.log(Level.INFO, "Closing session");
                session.close();
            }
        }
    }

    public List<Menu> findAll() {
        Session session = null;
        try {
            session = CoffeShopUtil.getSessionFactory().openSession();
            String hql = "FROM Menu";
            Query query = session.createQuery(hql);

            // Casting manual dari Object ke List<Menu>
            List<Menu> menuList = query.list();
            return menuList;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (session != null) {
                session.close(); // Menutup session secara manual
            }
        }
    }

    public Menu findById(Integer menuId) {
        Session session = null;
        try {
            session = CoffeShopUtil.getSessionFactory().openSession();

            // Lakukan casting manual jika diperlukan
            Menu menu = (Menu) session.get(Menu.class, menuId);
            return menu;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (session != null) {
                session.close(); // Menutup session secara manual
            }
        }
    }

    public void delete(Integer menuId) {
        Transaction transaction = null;
        Session session = null;
        try {
            session = CoffeShopUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();
            Menu menu = (Menu) session.get(Menu.class, menuId);
            if (menu != null) {
                session.delete(menu);
            }
            transaction.commit();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Menu deleted successfully"));
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Failed to delete menu"));
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    public List<Menu> findByCategory(String category) {
        Session session = null;
        try {
            session = CoffeShopUtil.getSessionFactory().openSession();
            String hql = "FROM Menu WHERE category = :category";
            Query query = session.createQuery(hql);
            query.setParameter("category", category);

            List<Menu> menuList = query.list();
            return menuList;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }
}
