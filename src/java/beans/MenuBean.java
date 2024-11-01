package beans;

import DAO.DAOMenu;
import java.io.Serializable;
import pojo.Menu;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;
import javax.faces.bean.ViewScoped;
import java.util.logging.Logger;

@ManagedBean(name = "menuBean")
@ViewScoped
public class MenuBean implements Serializable {
    
    private static final Logger LOGGER = Logger.getLogger(MenuBean.class.getName());


    private Menu menu = new Menu();
    private DAOMenu daoMenu = new DAOMenu();
    private List<Menu> menuList;
    private Integer selectedMenuId;

    // Constructor untuk menginisialisasi daftar menu
    public MenuBean() {
        LOGGER.log(Level.INFO, "Initializing MenuBean");
        menuList = daoMenu.findAll(); // Ambil semua data menu saat bean diinisialisasi
    }

    // Getters and Setters
    public Menu getMenu() {
        return menu;
    }

    public void setMenu(Menu menu) {
        this.menu = menu;
    }

    public List<Menu> getMenuList() {
        return menuList;
    }

    public Integer getSelectedMenuId() {
        return selectedMenuId;
    }

    public void setSelectedMenuId(Integer selectedMenuId) {
        this.selectedMenuId = selectedMenuId;
    }

    public List<Menu> getMainDishes() {
        return menuList.stream()
                .filter(m -> "Main Dish".equals(m.getCategory()))
                .collect(Collectors.toList());
    }

    public List<Menu> getDrinks() {
        return menuList.stream()
                .filter(m -> "Drinks".equals(m.getCategory()))
                .collect(Collectors.toList());
    }

    public List<Menu> getDesserts() {
        return menuList.stream()
                .filter(m -> "Desserts".equals(m.getCategory()))
                .collect(Collectors.toList());
    }

    // Fungsi untuk menyimpan data menu
    public String save() {
        try {
            LOGGER.log(Level.INFO, "Attempting to save menu: {0}", menu.getName());
            daoMenu.save(menu);
            LOGGER.log(Level.INFO, "Menu saved successfully: {0}", menu.getName());
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Menu saved successfully."));
            refreshMenuList();
            return "MenuData?faces-redirect=true";
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error saving menu: " + menu.getName(), e);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Failed to save menu: " + e.getMessage()));
            return null;
        }
    }

    // Fungsi untuk memulai proses edit menu
    public String editMenu(Integer menuId) {
        this.selectedMenuId = menuId;
        return "EditMenu?faces-redirect=true&amp;includeViewParams=true&amp;menuId=" + menuId;
    }

    // Fungsi untuk mengupdate menu yang sudah diedit
    public String updateMenu() {
        try {
            LOGGER.log(Level.INFO, "Attempting to update menu: {0}", menu.getName());
            if (this.menu.getMenuId() == null || !this.menu.getMenuId().equals(this.selectedMenuId)) {
                LOGGER.log(Level.WARNING, "Invalid menu ID. Expected: {0}, Actual: {1}", new Object[]{this.selectedMenuId, this.menu.getMenuId()});
                throw new IllegalStateException("Invalid menu ID");
            }
            daoMenu.update(menu);
            LOGGER.log(Level.INFO, "Menu updated successfully: {0}", menu.getName());
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Menu updated successfully"));
            refreshMenuList();
            return "MenuData?faces-redirect=true";
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error updating menu: " + menu.getName(), e);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Failed to update menu: " + e.getMessage()));
            return null;
        }
    }

    // Fungsi untuk menghapus menu
    public void deleteMenu(Integer menuId) {
        try {
            daoMenu.delete(menuId);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Menu deleted successfully"));
            refreshMenuList();
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Failed to delete menu"));
        }
    }

    // Fungsi untuk memuat menu berdasarkan ID (dipanggil saat halaman edit dimuat)
    public void loadMenu() {
        if (selectedMenuId != null) {
            LOGGER.log(Level.INFO, "Loading menu with ID: {0}", selectedMenuId);
            menu = daoMenu.findById(selectedMenuId);
            if (menu == null) {
                LOGGER.log(Level.WARNING, "Menu not found for ID: {0}", selectedMenuId);
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Menu not found"));
            } else {
                LOGGER.log(Level.INFO, "Menu loaded successfully: {0}", menu.getName());
            }
        }
    }

    // Helper method untuk merefresh daftar menu
    private void refreshMenuList() {
        LOGGER.log(Level.INFO, "Refreshing menu list");
        menuList = daoMenu.findAll();
    }
}
