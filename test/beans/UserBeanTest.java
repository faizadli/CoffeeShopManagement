package beans;

import DAO.DAOUser;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.application.NavigationHandler;
import javax.servlet.http.HttpSession;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import pojo.CoffeShopUtil;
import pojo.Users;
import java.lang.reflect.Field;
import javax.faces.application.Application;

@RunWith(PowerMockRunner.class)
@PrepareForTest({FacesContext.class, CoffeShopUtil.class})
public class UserBeanTest {

    @Mock
    private FacesContext facesContext;

    @Mock
    private ExternalContext externalContext;

    @Mock
    private HttpSession httpSession;

    @Mock
    private SessionFactory sessionFactory;

    @Mock
    private Session hibernateSession;

    @Mock
    private DAOUser daoUser;

    @Mock
    private Application application;

    @Mock
    private NavigationHandler navigationHandler;

    private UserBean userBean;

    @Before
    public void setUp() throws Exception {
        // Setup PowerMock statics
        PowerMockito.mockStatic(FacesContext.class);
        PowerMockito.mockStatic(CoffeShopUtil.class);

        // Setup mock returns
        when(FacesContext.getCurrentInstance()).thenReturn(facesContext);
        when(facesContext.getExternalContext()).thenReturn(externalContext);
        when(facesContext.getApplication()).thenReturn(application);
        when(application.getNavigationHandler()).thenReturn(navigationHandler);
        when(externalContext.getSession(anyBoolean())).thenReturn(httpSession);
        when(CoffeShopUtil.getSessionFactory()).thenReturn(sessionFactory);
        when(sessionFactory.openSession()).thenReturn(hibernateSession);

        // Initialize UserBean
        userBean = new UserBean();

        // Inject mock DAOUser
        Field daoUserField = UserBean.class.getDeclaredField("daoUser");
        daoUserField.setAccessible(true);
        daoUserField.set(userBean, daoUser);

        // Set test data
        userBean.setEmail("test@test.com");
        userBean.setPassword("password");
        userBean.setName("Test User");
        userBean.setPhoneNumber("123456789");
    }

    @After
    public void tearDown() {
        userBean = null;
    }

    @Test
    public void testLoginSuccess() {
        // Arrange
        Users mockUser = new Users("Test User", "test@test.com", "password", "123456789", "customer");
        when(daoUser.findByEmail("test@test.com")).thenReturn(mockUser);

        // Act
        String result = userBean.login();

        // Assert
        assertEquals("profile?faces-redirect=true", result);
        verify(httpSession).setAttribute("loggedInUser", mockUser);
        verify(httpSession).setAttribute("userId", mockUser.getUserId());
    }

    @Test
    public void testLoginAdmin() {
        // Arrange
        Users mockUser = new Users("Admin", "admin@test.com", "password", "123456789", "admin");
        when(daoUser.findByEmail("test@test.com")).thenReturn(mockUser);

        // Act
        String result = userBean.login();

        // Assert
        assertEquals("dashboard?faces-redirect=true", result);
        verify(httpSession).setAttribute("loggedInUser", mockUser);
    }

    @Test
    public void testLoginFailure() {
        // Arrange
        when(daoUser.findByEmail(anyString())).thenReturn(null);

        // Act
        String result = userBean.login();

        // Assert
        assertEquals("login?faces-redirect=true", result);
    }

    @Test
    public void testLogout() {
        // Act
        String result = userBean.logout();

        // Assert
        assertEquals("login?faces-redirect=true", result);
        verify(externalContext).invalidateSession();
    }

    @Test
    public void testRegisterSuccess() {
        // Arrange
        when(daoUser.findByEmail(anyString())).thenReturn(null);
        when(daoUser.save(any(Users.class))).thenReturn(true);

        // Act
        String result = userBean.register();

        // Assert
        assertEquals("login?faces-redirect=true", result);
        verify(daoUser).save(any(Users.class));
        verify(facesContext).addMessage(eq(null), any(FacesMessage.class));
    }

    @Test
    public void testRegisterFailureEmailExists() {
        // Arrange
        when(daoUser.findByEmail(anyString())).thenReturn(new Users());

        // Act
        String result = userBean.register();

        // Assert
        assertNull(result);
        verify(facesContext).addMessage(eq(null), any(FacesMessage.class));
    }

    @Test
    public void testRegisterFailureInvalidInput() {
        // Arrange
        userBean.setEmail("");

        // Act
        String result = userBean.register();

        // Assert
        assertNull(result);
        verify(facesContext).addMessage(eq(null), any(FacesMessage.class));
    }

    @Test
    public void testCheckIfLoggedIn() {
        // Arrange
        when(httpSession.getAttribute("loggedInUser")).thenReturn(new Users());

        // Act
        userBean.checkIfLoggedIn();

        // Assert
        verify(navigationHandler).handleNavigation(
                eq(facesContext),
                eq(null),
                eq("profile?faces-redirect=true")
        );
    }

    @Test
    public void testCheckIfLoggedInNoUser() {
        // Arrange
        when(httpSession.getAttribute("loggedInUser")).thenReturn(null);

        // Act
        userBean.checkIfLoggedIn();

        // Assert
        verify(navigationHandler, never()).handleNavigation(
                any(FacesContext.class),
                any(String.class),
                any(String.class)
        );
    }

    // Getter and Setter Tests
    @Test
    public void testGetterAndSetters() {
        // Test Name
        String name = "New Name";
        userBean.setName(name);
        assertEquals(name, userBean.getName());

        // Test Email
        String email = "new@test.com";
        userBean.setEmail(email);
        assertEquals(email, userBean.getEmail());

        // Test Password
        String password = "newpassword";
        userBean.setPassword(password);
        assertEquals(password, userBean.getPassword());

        // Test Phone Number
        String phoneNumber = "987654321";
        userBean.setPhoneNumber(phoneNumber);
        assertEquals(phoneNumber, userBean.getPhoneNumber());

        // Test Current User
        Users currentUser = new Users();
        userBean.setCurrentUser(currentUser);
        assertEquals(currentUser, userBean.getCurrentUser());
    }
}
