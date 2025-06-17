package pl.uczelnia.model.presenter;

import pl.uczelnia.model.Customer;
import pl.uczelnia.model.managers.CustomerService;
import pl.uczelnia.model.view.CustomerView;

import java.util.List;

public class CustomerPresenter {
    private final CustomerView view;
    private final CustomerService service;

    public CustomerPresenter(CustomerView view, CustomerService service) {
        this.view = view;
        this.service = service;
        loadCustomers();
    }

    private void loadCustomers() {
        List<Customer> customers = service.findAllCustomers();
        view.setCustomers(customers);
    }
}

