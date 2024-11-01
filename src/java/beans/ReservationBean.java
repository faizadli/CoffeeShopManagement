package beans;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import pojo.Reservation;
import pojo.Users;
import DAO.DAOReservation;
import java.io.Serializable;
import java.io.IOException;
import java.util.Date;
import java.util.List;

@ManagedBean
@ViewScoped
public class ReservationBean implements Serializable {

    private Reservation reservation;
    private DAOReservation reservationDAO;
    private Users loggedInUser;
    private List<Reservation> reservations;

    public ReservationBean() {
        reservation = new Reservation();
        reservationDAO = new DAOReservation();
        checkLogin();
        loadReservations();
    }

    protected FacesContext getFacesContext() {
        return FacesContext.getCurrentInstance();
    }

    public void checkLogin() {
        FacesContext context = getFacesContext();
        HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
        loggedInUser = (Users) session.getAttribute("loggedInUser");

        if (loggedInUser == null) {
            try {
                context.getExternalContext().redirect("login.xhtml");
            } catch (IOException e) {
                e.printStackTrace();
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Navigation failed"));
            }
        } else {
            reservation.setUsers(loggedInUser);
        }
    }

    public Reservation getReservation() {
        return reservation;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    public String getLoggedInUserName() {
        return loggedInUser != null ? loggedInUser.getName() : "";
    }

    public String bookReservation() {
        if (loggedInUser == null) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "You must be logged in to make a reservation"));
            return "login?faces-redirect=true";
        }

        try {
            reservation.setCreatedAt(new Date());
            reservation.setStatus("pending");
            reservationDAO.save(reservation);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Reservation booked successfully"));
            return null;
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Failed to book reservation"));
            return null;
        }
    }

    public List<Reservation> getReservations() {
        if (reservations == null) {
            loadReservations();
        }
        return reservations;
    }

    protected void loadReservations() {
        try {
            reservations = reservationDAO.findAll();
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Failed to load reservations"));
            throw new RuntimeException("Failed to load reservations", e);
        }
    }

    public String confirmReservation(Integer reservationId) {
        try {
            Reservation res = reservationDAO.findById(reservationId);
            if (res != null) {
                res.setStatus("confirmed");
                reservationDAO.update(res);
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Reservation confirmed successfully"));
                loadReservations();
                return null;
            } else {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Reservation not found"));
                return null;
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Failed to confirm reservation"));
            return null;
        }
    }
}
