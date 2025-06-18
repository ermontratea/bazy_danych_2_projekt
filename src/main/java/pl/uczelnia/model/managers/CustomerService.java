package pl.uczelnia.model.managers;

import pl.uczelnia.model.Customer;
import pl.uczelnia.model.Rental;
import pl.uczelnia.model.Reservation;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import java.time.LocalDate;
import java.util.List;

public class CustomerService {
    private EntityManager em;
    private final RentalService rentalService;
    private final ReservationService reservationService;

    public CustomerService(EntityManager em,RentalService rentalService, ReservationService reservationService) {
        this.em = em;
        this.rentalService = rentalService;
        this.reservationService = reservationService;
    }
    public List<Customer> findAllCustomers() {
        return em.createQuery("SELECT c FROM Customer c", Customer.class).getResultList();
    }

    public void addCustomer(Customer customer) {
        if (!isAtLeast13YearsOld(customer.getBirthday())) {
            throw new IllegalArgumentException("Klient musi mieć co najmniej 13 lat.");
        }
        em.persist(customer);
    }


    public Customer findCustomer(Long id) {
        return em.find(Customer.class, id);
    }

    public void updateFirstName(Long customerId, String newFirstName) {

        Customer customer = em.find(Customer.class, customerId, LockModeType.PESSIMISTIC_WRITE);

        if (customer != null) {
            customer.setFirstName(newFirstName);
        }

    }

    public void updateLastName(Long customerId, String newLastName) {

        Customer customer = em.find(Customer.class, customerId, LockModeType.PESSIMISTIC_WRITE);

        if (customer != null) {
            customer.setLastName(newLastName);
        }

    }

    public void updateEmail(Long customerId, String newEmail) {

        Customer customer = em.find(Customer.class, customerId, LockModeType.PESSIMISTIC_WRITE);

        if (customer != null) {
            customer.setEmail(newEmail);
        }

    }

    public void updateBirthday(Long customerId, LocalDate newBirthday) {
        if (!isAtLeast13YearsOld(newBirthday)) {
            throw new IllegalArgumentException("Klient musi mieć co najmniej 13 lat.");
        }

        Customer customer = em.find(Customer.class, customerId, LockModeType.PESSIMISTIC_WRITE);

        if (customer != null) {
            customer.setBirthday(newBirthday);
        }
    }

    public void deleteCustomerWithRelations(Long id) {
        Customer customer = em.find(Customer.class, id, LockModeType.PESSIMISTIC_WRITE);

        if (customer != null) {
            // Usuń wszystkie wypożyczenia klienta używając RentalService
            List<Rental> rentals = rentalService.findRentalsByCustomer(customer);
            for (Rental rental : rentals) {
                rentalService.deleteRental(rental.getId());
            }

            // Usuń wszystkie rezerwacje klienta używając ReservationService
            List<Reservation> reservations = reservationService.getReservationsByCustomer(customer);
            for (Reservation reservation : reservations) {
                reservationService.deleteReservation(reservation.getId());
            }

            // Usuń klienta
            em.remove(customer);
        }
    }

    public boolean deleteCustomerIfNoRelations(Long id) {
        Customer customer = em.find(Customer.class, id, LockModeType.PESSIMISTIC_WRITE);

        if (customer != null) {
            boolean hasRentals = rentalService.hasRentals(customer);
            boolean hasReservations = reservationService.hasReservations(customer);

            if (!hasRentals && !hasReservations) {
                em.remove(customer);
                return true;
            }
        }
        return false;
    }
    private boolean isAtLeast13YearsOld(LocalDate birthday) {
        return birthday != null && birthday.plusYears(13).isBefore(LocalDate.now());
    }
}
