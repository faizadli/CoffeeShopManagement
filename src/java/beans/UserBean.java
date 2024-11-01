package beans;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import pojo.Users;
import DAO.DAOUser;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.NavigationHandler;
import javax.servlet.http.HttpSession;
import org.hibernate.Session;
import pojo.CoffeShopUtil;

@ManagedBean
@SessionScoped
public class UserBean {

    private static final Logger logger = Logger.getLogger(UserBean.class.getName());

    private String name;
    private String email;
    private String password;
    private String phoneNumber;
    private Users currentUser;

    private DAOUser daoUser;

    public UserBean() {
        daoUser = new DAOUser();
    }

    public String login() {
        Session session = CoffeShopUtil.getSessionFactory().openSession();
        logger.info("Attempting to log in user: " + email);
        try {
            Users user = daoUser.findByEmail(email);
            if (user != null && user.getPassword().equals(password)) {
                HttpSession httpSession = (HttpSession) FacesContext.getCurrentInstance()
                        .getExternalContext().getSession(true);
                httpSession.setAttribute("loggedInUser", user);
                httpSession.setAttribute("userId", user.getUserId());

                // Inisialisasi currentUser
                this.currentUser = user;

                logger.info("User logged in successfully: " + email);
                if ("customer".equals(user.getRole())) {
                    return "profile?faces-redirect=true";
                } else if ("admin".equals(user.getRole())) {
                    return "dashboard?faces-redirect=true";
                }
            } else {
                return "login?faces-redirect=true";
            }
        } finally {
            session.close();
        }
        return null;
    }

    public String logout() {
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        logger.info("User logged out: " + (currentUser != null ? currentUser.getEmail() : "unknown"));
        currentUser = null;
        return "login?faces-redirect=true";
    }

    public String register() {
        logger.info("Attempting to register user: " + email);

        if (!isValidInput()) {
            return null;
        }

        if (daoUser.findByEmail(email) != null) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Registration failed", "Email already exists"));
            logger.warning("Registration failed: Email already exists - " + email);
            return null;
        }

        Users user = new Users(name, email, password, phoneNumber, "customer");

        if (daoUser.save(user)) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Registration successful", "You can now login"));
            logger.info("User registered successfully: " + email);
            return "login?faces-redirect=true";
        } else {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Registration failed", "Please try again"));
            logger.severe("Registration failed for user: " + email);
            return null;
        }
    }

    private boolean isValidInput() {
        if (name == null || name.trim().isEmpty()
                || email == null || email.trim().isEmpty()
                || password == null || password.trim().isEmpty()
                || phoneNumber == null || phoneNumber.trim().isEmpty()) {

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Registration failed", "All fields are required"));
            logger.warning("Registration failed: Invalid input");
            return false;
        }
        return true;
    }

    public void checkIfLoggedIn() {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
                .getExternalContext().getSession(false);

        // Cek apakah ada user yang sudah login di session
        if (session != null && session.getAttribute("loggedInUser") != null) {
            // Gunakan NavigationHandler untuk redirect
            FacesContext fc = FacesContext.getCurrentInstance();
            NavigationHandler navHandler = fc.getApplication().getNavigationHandler();
            navHandler.handleNavigation(fc, null, "profile?faces-redirect=true");
        }
    }

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Users getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(Users currentUser) {
        this.currentUser = currentUser;
    }
}
