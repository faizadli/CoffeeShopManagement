package DAO;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import pojo.CoffeShopUtil;
import pojo.Menu;

import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({CoffeShopUtil.class, FacesContext.class})
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DAOMenuTest {

    @Mock
    private SessionFactory sessionFactory;
    @Mock
    private Session session;
    @Mock
    private Transaction transaction;
    @Mock
    private Query query;
    @Mock
    private FacesContext facesContext;
    @Mock
    private ExternalContext externalContext;

    private DAOMenu daoMenu;
    private RuntimeException testException;
    private static Menu menu;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        daoMenu = new DAOMenu();
        testException = new RuntimeException("Test exception");

        mockStatic(CoffeShopUtil.class);
        mockStatic(FacesContext.class);

        when(CoffeShopUtil.getSessionFactory()).thenReturn(sessionFactory);
        when(sessionFactory.openSession()).thenReturn(session);
        when(session.beginTransaction()).thenReturn(transaction);
        when(FacesContext.getCurrentInstance()).thenReturn(facesContext);
        when(facesContext.getExternalContext()).thenReturn(externalContext);
    }

    @Test
    public void test1Save() {
        // Inisialisasi data
        menu = new Menu();
        menu.setMenuId(1);
        menu.setName("Espresso");
        menu.setPrice(new BigDecimal("25000.00"));
        menu.setCategory("Coffee");
        menu.setCreatedAt(new Date());

        // Setup mock
        when(session.save(any(Menu.class))).thenReturn(1);
        when(session.get(Menu.class, 1)).thenReturn(menu);
        doNothing().when(transaction).commit();

        // Execute
        daoMenu.save(menu);

        // Verify basic operations
        verify(session).beginTransaction();
        verify(session).save(menu);
        verify(transaction).commit();
        verify(session).close();

        // Direct assertions for saved menu
        ArgumentCaptor<Menu> menuCaptor = ArgumentCaptor.forClass(Menu.class);
        verify(session).save(menuCaptor.capture());
        Menu savedMenu = menuCaptor.getValue();

        assertNotNull(savedMenu);
        assertEquals(Integer.valueOf(1), savedMenu.getMenuId());
        assertEquals("Espresso", savedMenu.getName());
        assertEquals(new BigDecimal("25000.00"), savedMenu.getPrice());
        assertEquals("Coffee", savedMenu.getCategory());
        assertNotNull(savedMenu.getCreatedAt());

        // Verify FacesMessage
        ArgumentCaptor<FacesMessage> messageCaptor = ArgumentCaptor.forClass(FacesMessage.class);
        verify(facesContext).addMessage(eq(null), messageCaptor.capture());
        FacesMessage capturedMessage = messageCaptor.getValue();

        assertEquals(FacesMessage.SEVERITY_INFO, capturedMessage.getSeverity());
        assertEquals("Success", capturedMessage.getSummary());
        assertEquals("Data berhasil disimpan", capturedMessage.getDetail());
    }

    @Test
    public void test2Update() {
        // Pastikan menu sudah ada
        assertNotNull("Menu should not be null before update", menu);

        // Update data menu yang sudah ada
        menu.setName("Updated Espresso");
        menu.setPrice(new BigDecimal("27000.00"));

        // Setup mock
        when(session.get(Menu.class, 1)).thenReturn(menu);
        doNothing().when(transaction).commit();

        // Execute
        daoMenu.update(menu);

        // Verify basic operations
        verify(session).beginTransaction();
        verify(session).update(menu);
        verify(transaction).commit();
        verify(session).close();

        // Direct assertions for updated menu
        ArgumentCaptor<Menu> menuCaptor = ArgumentCaptor.forClass(Menu.class);
        verify(session).update(menuCaptor.capture());
        Menu updatedMenu = menuCaptor.getValue();

        assertEquals(Integer.valueOf(1), updatedMenu.getMenuId());
        assertEquals("Updated Espresso", updatedMenu.getName());
        assertEquals(new BigDecimal("27000.00"), updatedMenu.getPrice());
        assertEquals("Coffee", updatedMenu.getCategory());
        assertNotNull(updatedMenu.getCreatedAt());

        // Verify FacesMessage
        ArgumentCaptor<FacesMessage> messageCaptor = ArgumentCaptor.forClass(FacesMessage.class);
        verify(facesContext).addMessage(eq(null), messageCaptor.capture());
        FacesMessage capturedMessage = messageCaptor.getValue();

        assertEquals(FacesMessage.SEVERITY_INFO, capturedMessage.getSeverity());
        assertEquals("Success", capturedMessage.getSummary());
        assertEquals("Menu updated successfully", capturedMessage.getDetail());
    }

    @Test
    public void test3Delete() {
        // Pastikan menu sudah ada
        assertNotNull("Menu should not be null before delete", menu);

        // Setup mock
        when(session.get(Menu.class, 1)).thenReturn(menu);
        doNothing().when(transaction).commit();

        // Execute
        daoMenu.delete(1);

        // Verify basic operations
        verify(session).beginTransaction();
        verify(session).get(Menu.class, 1);
        verify(session).delete(menu);
        verify(transaction).commit();
        verify(session).close();

        // Direct assertions for deleted menu
        ArgumentCaptor<Menu> menuCaptor = ArgumentCaptor.forClass(Menu.class);
        verify(session).delete(menuCaptor.capture());
        Menu deletedMenu = menuCaptor.getValue();

        assertEquals(Integer.valueOf(1), deletedMenu.getMenuId());
        assertEquals("Updated Espresso", deletedMenu.getName());
        assertEquals(new BigDecimal("27000.00"), deletedMenu.getPrice());
        assertEquals("Coffee", deletedMenu.getCategory());

        // Verify FacesMessage
        ArgumentCaptor<FacesMessage> messageCaptor = ArgumentCaptor.forClass(FacesMessage.class);
        verify(facesContext).addMessage(eq(null), messageCaptor.capture());
        FacesMessage capturedMessage = messageCaptor.getValue();

        assertEquals(FacesMessage.SEVERITY_INFO, capturedMessage.getSeverity());
        assertEquals("Success", capturedMessage.getSummary());
        assertEquals("Menu deleted successfully", capturedMessage.getDetail());
    }

    @Test
    public void test4FindAll() {
        // Pastikan menu sudah ada
        assertNotNull("Menu should not be null before findAll", menu);

        // Setup list menu yang akan dikembalikan
        List<Menu> expectedMenus = Arrays.asList(menu);

        when(session.createQuery(anyString())).thenReturn(query);
        when(query.list()).thenReturn(expectedMenus);

        // Execute
        List<Menu> result = daoMenu.findAll();

        // Verify basic operations
        verify(session).createQuery("FROM Menu");
        verify(session).close();

        // Direct assertions for result list
        assertNotNull(result);
        assertEquals(1, result.size());

        // Verify menu in list
        Menu firstMenu = result.get(0);
        assertEquals(Integer.valueOf(1), firstMenu.getMenuId());
        assertEquals("Updated Espresso", firstMenu.getName());
        assertEquals(new BigDecimal("27000.00"), firstMenu.getPrice());
        assertEquals("Coffee", firstMenu.getCategory());
    }

    @Test
    public void test5FindById() {
        // Pastikan menu sudah ada
        assertNotNull("Menu should not be null before findById", menu);

        when(session.get(Menu.class, 1)).thenReturn(menu);

        // Execute
        Menu result = daoMenu.findById(1);

        // Verify basic operations
        verify(session).get(Menu.class, 1);
        verify(session).close();

        // Direct assertions for found menu
        assertNotNull(result);
        assertEquals(Integer.valueOf(1), result.getMenuId());
        assertEquals("Updated Espresso", result.getName());
        assertEquals(new BigDecimal("27000.00"), result.getPrice());
        assertEquals("Coffee", result.getCategory());
        assertNotNull(result.getCreatedAt());
    }

    @Test
    public void test6FindByCategory() {
        // Pastikan menu sudah ada
        assertNotNull("Menu should not be null before findByCategory", menu);

        // Setup list menu yang akan dikembalikan
        List<Menu> expectedMenus = Arrays.asList(menu);

        when(session.createQuery(anyString())).thenReturn(query);
        when(query.setParameter(eq("category"), anyString())).thenReturn(query);
        when(query.list()).thenReturn(expectedMenus);

        // Execute
        List<Menu> result = daoMenu.findByCategory("Coffee");

        // Verify basic operations
        verify(session).createQuery("FROM Menu WHERE category = :category");
        verify(query).setParameter("category", "Coffee");
        verify(session).close();

        // Direct assertions for result list
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.stream().allMatch(m -> "Coffee".equals(m.getCategory())));

        // Verify menu in list
        Menu firstMenu = result.get(0);
        assertEquals(Integer.valueOf(1), firstMenu.getMenuId());
        assertEquals("Updated Espresso", firstMenu.getName());
        assertEquals(new BigDecimal("27000.00"), firstMenu.getPrice());
    }

    @Test
    public void test7SaveException() {
        // Pastikan menu sudah ada
        assertNotNull("Menu should not be null before save exception", menu);

        // Setup mock untuk exception
        doThrow(testException).when(session).save(any(Menu.class));

        // Execute
        daoMenu.save(menu);

        // Verify basic operations and rollback
        verify(session).save(menu);
        verify(transaction).rollback();
        verify(session).close();

        // Verify FacesMessage
        ArgumentCaptor<FacesMessage> messageCaptor = ArgumentCaptor.forClass(FacesMessage.class);
        verify(facesContext).addMessage(eq(null), messageCaptor.capture());
        FacesMessage capturedMessage = messageCaptor.getValue();

        assertEquals(FacesMessage.SEVERITY_ERROR, capturedMessage.getSeverity());
        assertEquals("Error", capturedMessage.getSummary());
        assertTrue(capturedMessage.getDetail().contains("Gagal menyimpan data"));
    }

    @Test
    public void test8UpdateException() {
        // Pastikan menu sudah ada
        assertNotNull("Menu should not be null before update exception", menu);

        // Setup mock untuk exception
        doThrow(testException).when(session).update(any(Menu.class));

        // Execute
        daoMenu.update(menu);

        // Verify basic operations and rollback
        verify(session).update(menu);
        verify(transaction).rollback();
        verify(session).close();

        // Verify FacesMessage
        ArgumentCaptor<FacesMessage> messageCaptor = ArgumentCaptor.forClass(FacesMessage.class);
        verify(facesContext).addMessage(eq(null), messageCaptor.capture());
        FacesMessage capturedMessage = messageCaptor.getValue();

        assertEquals(FacesMessage.SEVERITY_ERROR, capturedMessage.getSeverity());
        assertEquals("Error", capturedMessage.getSummary());
        assertTrue(capturedMessage.getDetail().contains("Failed to update menu"));
    }

    @Test
    public void test9DeleteException() {
        // Pastikan menu sudah ada
        assertNotNull("Menu should not be null before delete exception", menu);

        // Setup mock untuk exception
        when(session.get(Menu.class, 1)).thenReturn(menu);
        doThrow(testException).when(session).delete(any(Menu.class));

        // Execute
        daoMenu.delete(1);

        // Verify basic operations and rollback
        verify(session).get(Menu.class, 1);
        verify(session).delete(menu);
        verify(transaction).rollback();
        verify(session).close();

        // Verify FacesMessage
        ArgumentCaptor<FacesMessage> messageCaptor = ArgumentCaptor.forClass(FacesMessage.class);
        verify(facesContext).addMessage(eq(null), messageCaptor.capture());
        FacesMessage capturedMessage = messageCaptor.getValue();

        assertEquals(FacesMessage.SEVERITY_ERROR, capturedMessage.getSeverity());
        assertEquals("Error", capturedMessage.getSummary());
        assertEquals("Failed to delete menu", capturedMessage.getDetail());
    }

    @Test
    public void testAFindByIdNotFound() {
        // Setup untuk ID yang tidak ada
        when(session.get(Menu.class, 999)).thenReturn(null);

        // Execute
        Menu result = daoMenu.findById(999);

        // Verify basic operations
        verify(session).get(Menu.class, 999);
        verify(session).close();

        // Direct assertions
        assertNull(result);
    }

    @Test
    public void testBDeleteNonExistentMenu() {
        // Setup untuk menu yang tidak ada
        when(session.get(eq(Menu.class), anyInt())).thenReturn(null);

        // Execute
        daoMenu.delete(999);

        // Verify basic operations
        verify(session).beginTransaction();
        verify(session).get(Menu.class, 999);
        verify(session, never()).delete(any(Menu.class));
        verify(transaction).commit();
        verify(session).close();

        // Verify FacesMessage
        ArgumentCaptor<FacesMessage> messageCaptor = ArgumentCaptor.forClass(FacesMessage.class);
        verify(facesContext).addMessage(eq(null), messageCaptor.capture());
        FacesMessage capturedMessage = messageCaptor.getValue();

        assertEquals(FacesMessage.SEVERITY_INFO, capturedMessage.getSeverity());
        assertEquals("Success", capturedMessage.getSummary());
        assertEquals("Menu deleted successfully", capturedMessage.getDetail());
    }

    @Test
    public void testCFindByCategoryEmptyResult() {
        // Setup
        when(session.createQuery(anyString())).thenReturn(query);
        when(query.setParameter(eq("category"), anyString())).thenReturn(query);
        when(query.list()).thenReturn(Arrays.asList());

        // Execute
        List<Menu> result = daoMenu.findByCategory("NonExistentCategory");

        // Verify basic operations
        verify(session).createQuery("FROM Menu WHERE category = :category");
        verify(query).setParameter("category", "NonExistentCategory");
        verify(session).close();

        // Direct assertions
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testDUpdateWithEmptyMenu() {
        // Pastikan menu sudah ada dan buat salinan kosong
        assertNotNull("Menu should not be null before empty update test", menu);
        Menu emptyMenu = new Menu();
        emptyMenu.setMenuId(menu.getMenuId());

        doNothing().when(transaction).commit();

        // Execute
        daoMenu.update(emptyMenu);

        // Verify basic operations
        verify(session).beginTransaction();
        verify(session).update(emptyMenu);
        verify(transaction).commit();
        verify(session).close();

        // Direct assertions for empty menu
        ArgumentCaptor<Menu> menuCaptor = ArgumentCaptor.forClass(Menu.class);
        verify(session).update(menuCaptor.capture());
        Menu updatedMenu = menuCaptor.getValue();

        assertEquals(menu.getMenuId(), updatedMenu.getMenuId());
        assertNull(updatedMenu.getName());
        assertNull(updatedMenu.getPrice());
        assertNull(updatedMenu.getCategory());

        // Verify FacesMessage
        ArgumentCaptor<FacesMessage> messageCaptor = ArgumentCaptor.forClass(FacesMessage.class);
        verify(facesContext).addMessage(eq(null), messageCaptor.capture());
        FacesMessage capturedMessage = messageCaptor.getValue();

        assertEquals(FacesMessage.SEVERITY_INFO, capturedMessage.getSeverity());
        assertEquals("Success", capturedMessage.getSummary());
        assertEquals("Menu updated successfully", capturedMessage.getDetail());
    }

    @Test
    public void testESessionNullInSave() {
        // Pastikan menu sudah ada
        assertNotNull("Menu should not be null before session null save test", menu);

        // Setup untuk session null
        when(CoffeShopUtil.getSessionFactory()).thenReturn(null);

        // Execute
        daoMenu.save(menu);

        // Verify FacesMessage
        ArgumentCaptor<FacesMessage> messageCaptor = ArgumentCaptor.forClass(FacesMessage.class);
        verify(facesContext).addMessage(eq(null), messageCaptor.capture());
        FacesMessage capturedMessage = messageCaptor.getValue();

        assertEquals(FacesMessage.SEVERITY_ERROR, capturedMessage.getSeverity());
        assertEquals("Error", capturedMessage.getSummary());
        assertTrue(capturedMessage.getDetail().contains("Gagal menyimpan data"));
    }

    @Test
    public void testFSessionNullInUpdate() {
        // Pastikan menu sudah ada
        assertNotNull("Menu should not be null before session null update test", menu);

        // Setup untuk session null
        when(CoffeShopUtil.getSessionFactory()).thenReturn(null);

        // Execute
        daoMenu.update(menu);

        // Verify FacesMessage
        ArgumentCaptor<FacesMessage> messageCaptor = ArgumentCaptor.forClass(FacesMessage.class);
        verify(facesContext).addMessage(eq(null), messageCaptor.capture());
        FacesMessage capturedMessage = messageCaptor.getValue();

        assertEquals(FacesMessage.SEVERITY_ERROR, capturedMessage.getSeverity());
        assertEquals("Error", capturedMessage.getSummary());
        assertTrue(capturedMessage.getDetail().contains("Failed to update menu"));
    }
}
