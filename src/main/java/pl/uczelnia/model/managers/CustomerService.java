package pl.uczelnia.model.managers;

import pl.uczelnia.model.Customer;

import javax.persistence.EntityManager;
import java.time.LocalDate;

public class CustomerService {
    private EntityManager em;

    public CustomerService(EntityManager em) {
        this.em = em;
    }

    public void addCustomer(Customer customer) {
        em.getTransaction().begin();
        em.persist(customer);
        em.getTransaction().commit();
    }

    public Customer findCustomer(Long id) {
        return em.find(Customer.class, id);
    }

    public void updateFirstName(Long customerId, String newFirstName) {
        em.getTransaction().begin();
        Customer customer = em.find(Customer.class, customerId);
        if (customer != null) {
            customer.setFirstName(newFirstName);
        }
        em.getTransaction().commit();
    }

    public void updateLastName(Long customerId, String newLastName) {
        em.getTransaction().begin();
        Customer customer = em.find(Customer.class, customerId);
        if (customer != null) {
            customer.setLastName(newLastName);
        }
        em.getTransaction().commit();
    }

    public void updateEmail(Long customerId, String newEmail) {
        em.getTransaction().begin();
        Customer customer = em.find(Customer.class, customerId);
        if (customer != null) {
            customer.setEmail(newEmail);
        }
        em.getTransaction().commit();
    }

    public void updateBirthday(Long customerId, LocalDate newBirthday) {
        em.getTransaction().begin();
        Customer customer = em.find(Customer.class, customerId);
        if (customer != null) {
            customer.setBirthday(newBirthday);
        }
        em.getTransaction().commit();
    }

    public void deleteCustomer(Long id) {
        em.getTransaction().begin();
        Customer customer = em.find(Customer.class, id);
        if (customer != null) {
            em.remove(customer);
        }
        em.getTransaction().commit();
    }
}
