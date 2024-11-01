package beans;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.ArgumentCaptor;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import DAO.DAOReservation;
import java.io.IOException;
import pojo.Reservation;
import pojo.Users;

@RunWith(PowerMockRunner.class)
@PrepareForTest({FacesContext.class})
public class ReservationBeanTest {

    @Mock
    private FacesContext facesContext;
    @Mock
    private ExternalContext externalContext;
    @Mock
    private HttpSession httpSession;
    @Mock
    private DAOReservation daoReservation;

    private ReservationBean reservationBean;
    private Users testUser;
    private Reservation testReservation;
    private List<Reservation> testReservations;

    @Before
    public void setUp() throws Exception {
        // Mock FacesContext
        PowerMockito.mockStatic(FacesContext.class);
        when(FacesContext.getCurrentInstance()).thenReturn(facesContext);
        when(facesContext.getExternalContext()).thenReturn(externalContext);
        when(externalContext.getSession(false)).thenReturn(httpSession);

        // Initialize test data
        testUser = new Users();
        testUser.setName("Test User");

        testReservation = new Reservation();
        testReservation.setReservationId(1);
        testReservation.setUsers(testUser);
        testReservation.setTableNumber(1);
        testReservation.setReservationDate(new Date());
        testReservation.setReservationTime(new Date());
        testReservation.setStatus("pending");
        testReservation.setCreatedAt(new Date());

        testReservations = new ArrayList<>();
        testReservations.add(testReservation);

        // Initialize ReservationBean
        reservationBean = PowerMockito.spy(new ReservationBean());
        PowerMockito.doNothing().when(reservationBean).checkLogin();
        PowerMockito.doNothing().when(reservationBean).loadReservations();

        // Set the mock DAO
        PowerMockito.field(ReservationBean.class, "reservationDAO").set(reservationBean, daoReservation);

        // Set initial data
        reservationBean.setReservation(testReservation);
        PowerMockito.field(ReservationBean.class, "reservations").set(reservationBean, testReservations);
    }

    @Test
    public void testGetReservations() throws Exception {
        // Set initial reservations to null to force loading
        PowerMockito.field(ReservationBean.class, "reservations").set(reservationBean, null);

        // Setup mock DAO response
        when(daoReservation.findAll()).thenReturn(testReservations);

        // Call the method allowing real loadReservations execution
        PowerMockito.doCallRealMethod().when(reservationBean).loadReservations();

        // Get reservations
        List<Reservation> result = reservationBean.getReservations();

        // Verify
        assertNotNull("Result should not be null", result);
        assertEquals("Should have exactly 1 reservation", 1, result.size());
        assertEquals("Reservation ID should match", testReservation.getReservationId(), result.get(0).getReservationId());
        verify(daoReservation, times(1)).findAll();
    }

    @Test
    public void testConfirmReservation() throws Exception {
        when(daoReservation.findById(1)).thenReturn(testReservation);

        String result = reservationBean.confirmReservation(1);

        assertNull(result);
        assertEquals("confirmed", testReservation.getStatus());
        verify(daoReservation).update(testReservation);

        ArgumentCaptor<FacesMessage> messageCaptor = ArgumentCaptor.forClass(FacesMessage.class);
        verify(facesContext).addMessage((String) isNull(), messageCaptor.capture());
        assertEquals(FacesMessage.SEVERITY_INFO, messageCaptor.getValue().getSeverity());
    }

    @Test
    public void testCheckLoginWithIOException() throws Exception {
        // Create a new spy specifically for this test
        ReservationBean localBean = PowerMockito.spy(new ReservationBean());
        PowerMockito.field(ReservationBean.class, "reservationDAO").set(localBean, daoReservation);

        // Reset and setup mocks
        reset(facesContext, externalContext, httpSession);
        PowerMockito.mockStatic(FacesContext.class);
        when(FacesContext.getCurrentInstance()).thenReturn(facesContext);
        when(facesContext.getExternalContext()).thenReturn(externalContext);
        when(externalContext.getSession(false)).thenReturn(httpSession);
        when(httpSession.getAttribute("loggedInUser")).thenReturn(null);

        // Setup exception
        doThrow(new IOException("Redirect failed"))
                .when(externalContext)
                .redirect("login.xhtml");

        // Skip loadReservations in constructor
        PowerMockito.doNothing().when(localBean).loadReservations();

        // Act
        localBean.checkLogin();

        // Verify
        verify(externalContext, times(1)).redirect("login.xhtml");
        ArgumentCaptor<FacesMessage> messageCaptor = ArgumentCaptor.forClass(FacesMessage.class);
        verify(facesContext).addMessage((String) isNull(), messageCaptor.capture());
        FacesMessage message = messageCaptor.getValue();
        assertEquals(FacesMessage.SEVERITY_ERROR, message.getSeverity());
        assertEquals("Error", message.getSummary());
        assertEquals("Navigation failed", message.getDetail());
    }

    @Test
    public void testBookReservation() throws Exception {
        PowerMockito.field(ReservationBean.class, "loggedInUser").set(reservationBean, testUser);

        String result = reservationBean.bookReservation();

        assertNull(result);
        verify(daoReservation).save(any(Reservation.class));

        ArgumentCaptor<FacesMessage> messageCaptor = ArgumentCaptor.forClass(FacesMessage.class);
        verify(facesContext).addMessage((String) isNull(), messageCaptor.capture());
        assertEquals(FacesMessage.SEVERITY_INFO, messageCaptor.getValue().getSeverity());
    }

    @Test
    public void testBookReservationWithException() throws Exception {
        PowerMockito.field(ReservationBean.class, "loggedInUser").set(reservationBean, testUser);

        doThrow(new RuntimeException("Database error"))
                .when(daoReservation).save(any(Reservation.class));

        String result = reservationBean.bookReservation();

        assertNull(result);
        ArgumentCaptor<FacesMessage> messageCaptor = ArgumentCaptor.forClass(FacesMessage.class);
        verify(facesContext).addMessage((String) isNull(), messageCaptor.capture());
        assertEquals(FacesMessage.SEVERITY_ERROR, messageCaptor.getValue().getSeverity());
    }

    @Test
    public void testLoadReservationsWithException() {
        when(daoReservation.findAll()).thenThrow(new RuntimeException("Failed to load reservations"));
        PowerMockito.doCallRealMethod().when(reservationBean).loadReservations();

        try {
            reservationBean.loadReservations();
            fail("Expected RuntimeException");
        } catch (RuntimeException e) {
            assertEquals("Failed to load reservations", e.getMessage());
            ArgumentCaptor<FacesMessage> messageCaptor = ArgumentCaptor.forClass(FacesMessage.class);
            verify(facesContext).addMessage((String) isNull(), messageCaptor.capture());
            assertEquals(FacesMessage.SEVERITY_ERROR, messageCaptor.getValue().getSeverity());
        }
    }
    
    @Test
    public void testConfirmReservationWithGeneralException() throws Exception {
        // Arrange
        when(daoReservation.findById(1)).thenReturn(testReservation);
        // Menggunakan RuntimeException sebagai gantinya
        doThrow(new RuntimeException("Database update failed"))
                .when(daoReservation).update(any(Reservation.class));

        // Act
        String result = reservationBean.confirmReservation(1);

        // Assert
        assertNull("Result should be null", result);
        verify(daoReservation).findById(1);
        verify(daoReservation).update(testReservation);

        // Verify error message
        ArgumentCaptor<FacesMessage> messageCaptor = ArgumentCaptor.forClass(FacesMessage.class);
        verify(facesContext).addMessage((String) isNull(), messageCaptor.capture());

        FacesMessage capturedMessage = messageCaptor.getValue();
        assertEquals(FacesMessage.SEVERITY_ERROR, capturedMessage.getSeverity());
        assertEquals("Error", capturedMessage.getSummary());
        assertEquals("Failed to confirm reservation", capturedMessage.getDetail());
    }
}
