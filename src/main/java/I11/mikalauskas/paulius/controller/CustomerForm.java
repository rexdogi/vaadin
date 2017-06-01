package I11.mikalauskas.paulius.controller;

import I11.mikalauskas.paulius.MyUI;
import I11.mikalauskas.paulius.model.Customer;
import com.vaadin.data.Binder;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.Page;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;

public class CustomerForm extends FormLayout {

    private TextField firstName = new TextField("First name");
    private TextField lastName = new TextField("Last name");
    private TextField email = new TextField("Email");
    private NativeSelect<CustomerStatus> status = new NativeSelect<>("Status");
    private DateField birthdate = new DateField("Birthday");
    private Button save = new Button("Save");
    private Button delete = new Button("Delete");

    private CustomerService service = CustomerService.getInstance();
    private Customer customer;
    private MyUI myUI;
    private Binder<Customer> binder = new Binder<>(Customer.class);

    public CustomerForm(MyUI myUI) {
        this.myUI = myUI;

        setSizeUndefined();
        HorizontalLayout buttons = new HorizontalLayout(save, delete);
        addComponents(firstName, lastName, email, status, birthdate, buttons);

        status.setItems(CustomerStatus.values());
        save.setStyleName(ValoTheme.BUTTON_PRIMARY);
        save.setClickShortcut(KeyCode.ENTER);

        binder.bindInstanceFields(this);

        save.addClickListener(e -> {
            if(firstName.getValue().length() == 0) {
                new Notification("Privalomas vartotojo vardas", Notification.Type.ERROR_MESSAGE).show(Page.getCurrent());
            } else {
                this.save();
                new Notification("Pridėtas vartotojas", Notification.Type.HUMANIZED_MESSAGE).show(Page.getCurrent());
            }
        });
        delete.addClickListener(e -> {
            this.delete();
            new Notification("Ištrintas vartotojas", Notification.Type.WARNING_MESSAGE).show(Page.getCurrent());
        });
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
        binder.setBean(customer);

        delete.setVisible(customer.isPersisted());
        setVisible(true);
        firstName.selectAll();
    }

    private void delete() {
        service.delete(customer);
        myUI.updateList();
        setVisible(false);
    }

    private void save() {
        service.save(customer);
        myUI.updateList();
        setVisible(false);
    }

}
