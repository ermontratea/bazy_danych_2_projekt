package pl.uczelnia.model.managers;

import pl.uczelnia.model.Customer;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;

public class CustomerService {
    private EntityManager em;

    public CustomerService(EntityManager em) {
        this.em = em;
    }
    public List<Customer> findAllCustomers() {
        return em.createQuery("SELECT c FROM Customer c", Customer.class).getResultList();
    }

    public void addCustomer(Customer customer) {

        em.persist(customer);

    }

    public Customer findCustomer(Long id) {
        return em.find(Customer.class, id);
    }

    public void updateFirstName(Long customerId, String newFirstName) {

        Customer customer = em.find(Customer.class, customerId);
        if (customer != null) {
            customer.setFirstName(newFirstName);
        }

    }

    public void updateLastName(Long customerId, String newLastName) {

        Customer customer = em.find(Customer.class, customerId);
        if (customer != null) {
            customer.setLastName(newLastName);
        }

    }

    public void updateEmail(Long customerId, String newEmail) {

        Customer customer = em.find(Customer.class, customerId);
        if (customer != null) {
            customer.setEmail(newEmail);
        }

    }

    public void updateBirthday(Long customerId, LocalDate newBirthday) {

        Customer customer = em.find(Customer.class, customerId);
        if (customer != null) {
            customer.setBirthday(newBirthday);
        }

    }

    public void deleteCustomer(Long id) {

        Customer customer = em.find(Customer.class, id);
        if (customer != null) {
            em.remove(customer);
        }

    }
}
