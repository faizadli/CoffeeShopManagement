package beans;

import DAO.DAOMenu;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.modules.junit4.PowerMockRunner;
import pojo.Menu;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.HashSet;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.mockito.ArgumentCaptor;

@RunWith(PowerMockRunner.class)
@PrepareForTest({FacesContext.class})
public class MenuBeanTest {

    @Mock
    private DAOMenu daoMenu;

    @Mock
    private FacesContext facesContext;

    @Mock
    private ExternalContext externalContext;

    private MenuBean menuBean;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        menuBean = new MenuBean();

        // Mock FacesContext
        PowerMockito.mockStatic(FacesContext.class);
        when(FacesContext.getCurrentInstance()).thenReturn(facesContext);
        when(facesContext.getExternalContext()).thenReturn(externalContext);

        // Set DAOMenu using reflection
        try {
            java.lang.reflect.Field daoMenuField = MenuBean.class.getDeclaredField("daoMenu");
            daoMenuField.setAccessible(true);
            daoMenuField.set(menuBean, daoMenu);
        } catch (Exception e) {
            fail("Failed to set up test data: " + e.getMessage());
        }
    }

    @Test
    public void testGetSetMenu() {
        // Setup dummy data
        Menu menu = new Menu();
        menu.setMenuId(1);
        menu.setName("Test Menu");
        menu.setPrice(new BigDecimal("25000.00"));
        menu.setCategory("Drinks");
        menu.setCreatedAt(new Date());

        menuBean.setMenu(menu);
        assertEquals(menu, menuBean.getMenu()); //Menu object should match the set menu
    }

    @Test
    public void testGetMenuList() {
        // Setup dummy data
        Menu menu1 = new Menu();
        menu1.setMenuId(1);
        menu1.setName("Espresso");
        menu1.setPrice(new BigDecimal("25000.00"));
        menu1.setCategory("Drinks");
        menu1.setCreatedAt(new Date());

        Menu menu2 = new Menu();
        menu2.setMenuId(2);
        menu2.setName("Sandwich");
        menu2.setPrice(new BigDecimal("35000.00"));
        menu2.setCategory("Main Dish");
        menu2.setCreatedAt(new Date());

        List<Menu> expectedList = Arrays.asList(menu1, menu2);

        // Set menuList using reflection
        try {
            java.lang.reflect.Field menuListField = MenuBean.class.getDeclaredField("menuList");
            menuListField.setAccessible(true);
            menuListField.set(menuBean, expectedList);
        } catch (Exception e) {
            fail("Failed to set test data: " + e.getMessage());
        }

        assertEquals(expectedList, menuBean.getMenuList()); //Menu list should match expected list
    }

    @Test
    public void testGetMainDishes() {
        // Setup dummy data
        Menu mainDish1 = new Menu();
        mainDish1.setMenuId(1);
        mainDish1.setName("Sandwich");
        mainDish1.setPrice(new BigDecimal("35000.00"));
        mainDish1.setCategory("Main Dish");

        Menu mainDish2 = new Menu();
        mainDish2.setMenuId(2);
        mainDish2.setName("Pasta");
        mainDish2.setPrice(new BigDecimal("40000.00"));
        mainDish2.setCategory("Main Dish");

        Menu drink = new Menu();
        drink.setMenuId(3);
        drink.setName("Coffee");
        drink.setCategory("Drinks");

        List<Menu> menuList = Arrays.asList(mainDish1, mainDish2, drink);

        // Set menuList using reflection
        try {
            java.lang.reflect.Field menuListField = MenuBean.class.getDeclaredField("menuList");
            menuListField.setAccessible(true);
            menuListField.set(menuBean, menuList);
        } catch (Exception e) {
            fail("Failed to set test data: " + e.getMessage());
        }

        List<Menu> mainDishes = menuBean.getMainDishes();
        assertEquals(2, mainDishes.size()); //Should return correct number of main dishes
        assertTrue(mainDishes.stream().allMatch(m -> "Main Dish".equals(m.getCategory()))); //All items should be Main Dish
    }

    @Test
    public void testGetDrinks() {
        // Setup dummy data
        Menu drink1 = new Menu();
        drink1.setMenuId(1);
        drink1.setName("Coffee");
        drink1.setPrice(new BigDecimal("25000.00"));
        drink1.setCategory("Drinks");

        Menu drink2 = new Menu();
        drink2.setMenuId(2);
        drink2.setName("Tea");
        drink2.setPrice(new BigDecimal("20000.00"));
        drink2.setCategory("Drinks");

        Menu mainDish = new Menu();
        mainDish.setMenuId(3);
        mainDish.setName("Sandwich");
        mainDish.setCategory("Main Dish");

        List<Menu> menuList = Arrays.asList(drink1, drink2, mainDish);

        // Set menuList using reflection
        try {
            java.lang.reflect.Field menuListField = MenuBean.class.getDeclaredField("menuList");
            menuListField.setAccessible(true);
            menuListField.set(menuBean, menuList);
        } catch (Exception e) {
            fail("Failed to set test data: " + e.getMessage());
        }

        List<Menu> drinks = menuBean.getDrinks();
        assertEquals(2, drinks.size()); //Should return correct number of drinks
        assertTrue(drinks.stream().allMatch(m -> "Drinks".equals(m.getCategory()))); //All items should be Drinks
    }

    @Test
    public void testGetDesserts() {
        // Setup dummy data
        Menu dessert1 = new Menu();
        dessert1.setMenuId(1);
        dessert1.setName("Cake");
        dessert1.setPrice(new BigDecimal("30000.00"));
        dessert1.setCategory("Desserts");

        Menu dessert2 = new Menu();
        dessert2.setMenuId(2);
        dessert2.setName("Ice Cream");
        dessert2.setPrice(new BigDecimal("25000.00"));
        dessert2.setCategory("Desserts");

        Menu drink = new Menu();
        drink.setMenuId(3);
        drink.setName("Coffee");
        drink.setCategory("Drinks");

        List<Menu> menuList = Arrays.asList(dessert1, dessert2, drink);

        // Set menuList using reflection
        try {
            java.lang.reflect.Field menuListField = MenuBean.class.getDeclaredField("menuList");
            menuListField.setAccessible(true);
            menuListField.set(menuBean, menuList);
        } catch (Exception e) {
            fail("Failed to set test data: " + e.getMessage());
        }

        List<Menu> desserts = menuBean.getDesserts();
        assertEquals(2, desserts.size()); //Should return correct number of desserts
        assertTrue(desserts.stream().allMatch(m -> "Desserts".equals(m.getCategory()))); //All items should be Desserts
    }

    @Test
    public void testSaveSuccess() {
        // Setup dummy data
        Menu menuToSave = new Menu();
        menuToSave.setMenuId(1);
        menuToSave.setName("New Coffee");
        menuToSave.setPrice(new BigDecimal("25000.00"));
        menuToSave.setCategory("Drinks");
        menuToSave.setCreatedAt(new Date());
        menuToSave.setDescription("Fresh brewed coffee");
        menuToSave.setAvailable(true);

        menuBean.setMenu(menuToSave);

        // Execute
        String result = menuBean.save();

        // Verify basic operations
        verify(daoMenu).save(any(Menu.class));
        assertEquals("MenuData?faces-redirect=true", result); //Should redirect to menu data page

        // Verify saved menu
        ArgumentCaptor<Menu> menuCaptor = ArgumentCaptor.forClass(Menu.class);
        verify(daoMenu).save(menuCaptor.capture());
        Menu savedMenu = menuCaptor.getValue();

        assertEquals(Integer.valueOf(1), savedMenu.getMenuId()); //Menu ID should match
        assertEquals("New Coffee", savedMenu.getName()); //Menu name should match
        assertEquals(new BigDecimal("25000.00"), savedMenu.getPrice()); //Menu price should match
        assertEquals("Drinks", savedMenu.getCategory()); //Menu category should match
        assertNotNull(savedMenu.getCreatedAt()); //Created date should not be null

        // Verify FacesMessage
        ArgumentCaptor<FacesMessage> messageCaptor = ArgumentCaptor.forClass(FacesMessage.class);
        verify(facesContext).addMessage(eq(null), messageCaptor.capture());
        FacesMessage capturedMessage = messageCaptor.getValue();

        assertEquals(FacesMessage.SEVERITY_INFO, capturedMessage.getSeverity()); //Message severity should be INFO
        assertEquals("Success", capturedMessage.getSummary()); //Success summary should match
        assertEquals("Menu saved successfully.", capturedMessage.getDetail()); //Success detail should match
    }

    @Test
    public void testSaveFailure() {
        // Setup dummy data
        Menu menuToSave = new Menu();
        menuToSave.setMenuId(1);
        menuToSave.setName("Failed Coffee");
        menuToSave.setPrice(new BigDecimal("25000.00"));
        menuToSave.setCategory("Drinks");
        menuToSave.setCreatedAt(new Date());

        menuBean.setMenu(menuToSave);
        doThrow(new RuntimeException("Save failed")).when(daoMenu).save(any(Menu.class));

        // Execute
        String result = menuBean.save();

        // Verify
        assertNull(result); //Should return null on failure
        verify(daoMenu).save(menuToSave); //Should attempt to save

        // Verify FacesMessage
        ArgumentCaptor<FacesMessage> messageCaptor = ArgumentCaptor.forClass(FacesMessage.class);
        verify(facesContext).addMessage(eq(null), messageCaptor.capture());
        FacesMessage capturedMessage = messageCaptor.getValue();

        assertEquals(FacesMessage.SEVERITY_ERROR, capturedMessage.getSeverity()); //Message severity should be ERROR
        assertEquals("Error", capturedMessage.getSummary()); //Error summary should match
        assertTrue(capturedMessage.getDetail().contains("Failed to save menu")); //Error detail should contain failure message
    }
    
    @Test
    public void testEditMenu() {
        // Setup dummy data
        Integer menuId = 1;
        menuBean.setSelectedMenuId(null); // Memastikan ID awal kosong

        // Execute
        String result = menuBean.editMenu(menuId);

        // Verify
        assertEquals("EditMenu?faces-redirect=true&amp;includeViewParams=true&amp;menuId=1", result); //Should return correct edit URL
        assertEquals(Integer.valueOf(1), menuBean.getSelectedMenuId()); //Selected menu ID should be updated
    }

    @Test
    public void testUpdateMenuSuccess() {
        // Setup dummy data
        Menu menuToUpdate = new Menu();
        menuToUpdate.setMenuId(1);
        menuToUpdate.setName("Updated Coffee");
        menuToUpdate.setPrice(new BigDecimal("27000.00"));
        menuToUpdate.setCategory("Drinks");
        menuToUpdate.setCreatedAt(new Date());
        menuToUpdate.setDescription("Updated description");
        menuToUpdate.setAvailable(true);

        menuBean.setMenu(menuToUpdate);
        menuBean.setSelectedMenuId(1);

        // Execute
        String result = menuBean.updateMenu();

        // Verify basic operations
        verify(daoMenu).update(any(Menu.class));
        assertEquals("MenuData?faces-redirect=true", result); //Should redirect to menu data page

        // Verify updated menu
        ArgumentCaptor<Menu> menuCaptor = ArgumentCaptor.forClass(Menu.class);
        verify(daoMenu).update(menuCaptor.capture());
        Menu updatedMenu = menuCaptor.getValue();

        assertEquals(Integer.valueOf(1), updatedMenu.getMenuId()); //Menu ID should match
        assertEquals("Updated Coffee", updatedMenu.getName()); //Menu name should match
        assertEquals(new BigDecimal("27000.00"), updatedMenu.getPrice()); //Menu price should match
        assertEquals("Drinks", updatedMenu.getCategory()); //Menu category should match
        assertNotNull(updatedMenu.getCreatedAt()); //Created date should not be null

        // Verify FacesMessage
        ArgumentCaptor<FacesMessage> messageCaptor = ArgumentCaptor.forClass(FacesMessage.class);
        verify(facesContext).addMessage(eq(null), messageCaptor.capture());
        FacesMessage capturedMessage = messageCaptor.getValue();

        assertEquals(FacesMessage.SEVERITY_INFO, capturedMessage.getSeverity()); //Message severity should be INFO
        assertEquals("Success", capturedMessage.getSummary()); //Success summary should match
        assertEquals("Menu updated successfully", capturedMessage.getDetail()); //Success detail should match
    }

    @Test
    public void testUpdateMenuFailure() {
        // Setup dummy data
        Menu menuToUpdate = new Menu();
        menuToUpdate.setMenuId(1);
        menuToUpdate.setName("Update Failed Coffee");
        menuToUpdate.setPrice(new BigDecimal("27000.00"));
        menuToUpdate.setCategory("Drinks");
        menuToUpdate.setCreatedAt(new Date());

        menuBean.setMenu(menuToUpdate);
        menuBean.setSelectedMenuId(1);
        doThrow(new RuntimeException("Update failed")).when(daoMenu).update(any(Menu.class));

        // Execute
        String result = menuBean.updateMenu();

        // Verify
        assertNull(result); //Should return null on failure
        verify(daoMenu).update(menuToUpdate); //Should attempt to update

        // Verify FacesMessage
        ArgumentCaptor<FacesMessage> messageCaptor = ArgumentCaptor.forClass(FacesMessage.class);
        verify(facesContext).addMessage(eq(null), messageCaptor.capture());
        FacesMessage capturedMessage = messageCaptor.getValue();

        assertEquals(FacesMessage.SEVERITY_ERROR, capturedMessage.getSeverity()); //Message severity should be ERROR
        assertEquals("Error", capturedMessage.getSummary()); //Error summary should match
        assertTrue(capturedMessage.getDetail().contains("Failed to update menu")); //Error detail should contain failure message
    }

    @Test
    public void testUpdateMenuInvalidId() {
        // Setup dummy data
        Menu menuToUpdate = new Menu();
        menuToUpdate.setMenuId(2); // Different from selectedMenuId
        menuToUpdate.setName("Invalid ID Coffee");
        menuToUpdate.setPrice(new BigDecimal("27000.00"));
        menuToUpdate.setCategory("Drinks");
        menuToUpdate.setCreatedAt(new Date());

        menuBean.setMenu(menuToUpdate);
        menuBean.setSelectedMenuId(1);

        // Execute
        String result = menuBean.updateMenu();

        // Verify
        assertNull(result); //Should return null on invalid ID
        verify(daoMenu, never()).update(any(Menu.class)); //Should not attempt to update

        // Verify FacesMessage
        ArgumentCaptor<FacesMessage> messageCaptor = ArgumentCaptor.forClass(FacesMessage.class);
        verify(facesContext).addMessage(eq(null), messageCaptor.capture());
        FacesMessage capturedMessage = messageCaptor.getValue();

        assertEquals(FacesMessage.SEVERITY_ERROR, capturedMessage.getSeverity()); //Message severity should be ERROR
        assertEquals("Error", capturedMessage.getSummary()); //Error summary should match
        assertTrue(capturedMessage.getDetail().contains("Failed to update menu")); //Error detail should contain failure message
    }

    @Test
    public void testDeleteMenu() {
        // Setup dummy data
        Menu menuToDelete = new Menu();
        menuToDelete.setMenuId(1);
        menuToDelete.setName("Delete Coffee");
        menuToDelete.setPrice(new BigDecimal("22000.00"));
        menuToDelete.setCategory("Drinks");

        when(daoMenu.findById(1)).thenReturn(menuToDelete);

        // Execute
        menuBean.deleteMenu(1);

        // Verify operations
        verify(daoMenu).delete(1); //Should call delete with correct ID

        // Verify FacesMessage
        ArgumentCaptor<FacesMessage> messageCaptor = ArgumentCaptor.forClass(FacesMessage.class);
        verify(facesContext).addMessage(eq(null), messageCaptor.capture());
        FacesMessage capturedMessage = messageCaptor.getValue();

        assertEquals(FacesMessage.SEVERITY_INFO, capturedMessage.getSeverity()); //Message severity should be INFO
        assertEquals("Success", capturedMessage.getSummary()); //Success summary should match
        assertEquals("Menu deleted successfully", capturedMessage.getDetail()); //Success detail should match
    }

    @Test
    public void testDeleteMenuFailure() {
        // Setup dummy data
        doThrow(new RuntimeException("Delete failed")).when(daoMenu).delete(anyInt());

        // Execute
        menuBean.deleteMenu(1);

        // Verify FacesMessage
        ArgumentCaptor<FacesMessage> messageCaptor = ArgumentCaptor.forClass(FacesMessage.class);
        verify(facesContext).addMessage(eq(null), messageCaptor.capture());
        FacesMessage capturedMessage = messageCaptor.getValue();

        assertEquals(FacesMessage.SEVERITY_ERROR, capturedMessage.getSeverity()); //Message severity should be ERROR
        assertEquals("Error", capturedMessage.getSummary()); //Error summary should match
        assertTrue(capturedMessage.getDetail().contains("Failed to delete menu")); //Error detail should contain failure message
    }

    @Test
    public void testLoadMenu() {
        // Setup dummy data
        Menu expectedMenu = new Menu();
        expectedMenu.setMenuId(1);
        expectedMenu.setName("Load Test Coffee");
        expectedMenu.setPrice(new BigDecimal("26000.00"));
        expectedMenu.setCategory("Drinks");
        expectedMenu.setCreatedAt(new Date());

        when(daoMenu.findById(1)).thenReturn(expectedMenu);

        // Execute
        menuBean.setSelectedMenuId(1);
        menuBean.loadMenu();

        // Verify
        Menu loadedMenu = menuBean.getMenu();
        assertNotNull(loadedMenu); //Loaded menu should not be null
        assertEquals(expectedMenu.getMenuId(), loadedMenu.getMenuId()); //Menu ID should match
        assertEquals(expectedMenu.getName(), loadedMenu.getName()); //Menu name should match
        assertEquals(expectedMenu.getPrice(), loadedMenu.getPrice()); //Menu price should match
        assertEquals(expectedMenu.getCategory(), loadedMenu.getCategory()); //Menu category should match

        verify(daoMenu).findById(1); //Should call findById with correct ID
    }

    @Test
    public void testLoadMenuNotFound() {
        // Setup
        when(daoMenu.findById(999)).thenReturn(null);

        // Execute
        menuBean.setSelectedMenuId(999);
        menuBean.loadMenu();

        // Verify FacesMessage
        ArgumentCaptor<FacesMessage> messageCaptor = ArgumentCaptor.forClass(FacesMessage.class);
        verify(facesContext).addMessage(eq(null), messageCaptor.capture());
        FacesMessage capturedMessage = messageCaptor.getValue();

        assertEquals(FacesMessage.SEVERITY_ERROR, capturedMessage.getSeverity()); //Message severity should be ERROR
        assertEquals("Error", capturedMessage.getSummary()); //Error summary should match
        assertEquals("Menu not found", capturedMessage.getDetail()); //Error detail should match
    }
}
