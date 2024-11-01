package pojo;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class MenuTest {
    
    private Menu menu;
    
    @Before
    public void setUp() {
        menu = new Menu();
    }

    @Test
    public void testGetSetMenuId() {
        Integer menuId = 1;
        menu.setMenuId(menuId);
        assertEquals(menuId, menu.getMenuId());
    }

    @Test
    public void testGetSetName() {
        String name = "Espresso";
        menu.setName(name);
        assertEquals(name, menu.getName());
    }

    @Test
    public void testGetSetDescription() {
        String description = "Strong black coffee";
        menu.setDescription(description);
        assertEquals(description, menu.getDescription());
    }

    @Test
    public void testGetSetPrice() {
        BigDecimal price = new BigDecimal("25000.00");
        menu.setPrice(price);
        assertEquals(price, menu.getPrice());
    }

    @Test
    public void testGetSetCategory() {
        String category = "Drinks";
        menu.setCategory(category);
        assertEquals(category, menu.getCategory());
    }

    @Test
    public void testGetSetAvailable() {
        Boolean available = true;
        menu.setAvailable(available);
        assertEquals(available, menu.getAvailable());
    }

    @Test
    public void testGetSetCreatedAt() {
        Date createdAt = new Date();
        menu.setCreatedAt(createdAt);
        assertEquals(createdAt, menu.getCreatedAt());
    }

    @Test
    public void testGetSetOrderses() {
        Set orderses = new HashSet(0);
        menu.setOrderses(orderses);
        assertEquals(orderses, menu.getOrderses());
    }

    @Test
    public void testConstructorWithRequiredFields() {
        String name = "Latte";
        BigDecimal price = new BigDecimal("30000.00");
        Date createdAt = new Date();
        
        Menu newMenu = new Menu(name, price, createdAt);
        
        assertEquals(name, newMenu.getName());
        assertEquals(price, newMenu.getPrice());
        assertEquals(createdAt, newMenu.getCreatedAt());
    }

    @Test
    public void testConstructorWithAllFields() {
        String name = "Cappuccino";
        String description = "Italian coffee drink";
        BigDecimal price = new BigDecimal("28000.00");
        String category = "Drinks";
        Boolean available = true;
        Date createdAt = new Date();
        Set orderses = new HashSet(0);
        
        Menu newMenu = new Menu(name, description, price, category, available, createdAt, orderses);
        
        assertEquals(name, newMenu.getName());
        assertEquals(description, newMenu.getDescription());
        assertEquals(price, newMenu.getPrice());
        assertEquals(category, newMenu.getCategory());
        assertEquals(available, newMenu.getAvailable());
        assertEquals(createdAt, newMenu.getCreatedAt());
        assertEquals(orderses, newMenu.getOrderses());
    }
}