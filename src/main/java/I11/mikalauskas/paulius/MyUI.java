package I11.mikalauskas.paulius;

import javax.servlet.annotation.WebServlet;

import I11.mikalauskas.paulius.controller.CustomerForm;
import I11.mikalauskas.paulius.controller.CustomerService;
import I11.mikalauskas.paulius.model.Customer;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Theme("mytheme")
public class MyUI extends UI {
    
    private CustomerService service = CustomerService.getInstance();
    private Grid<Customer> grid = new Grid<>(Customer.class);
    private TextField filterText = new TextField();
    private CustomerForm form = new CustomerForm(this);
    private MenuBar menuBar = new MenuBar();
    Window helpWindow = new Window("Help");
    Window aboutWindow = new Window("About");
    private final static Logger logger = Logger.getLogger(MyUI.class.getName());

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        final VerticalLayout layout = new VerticalLayout();

        VerticalLayout subContentHelp = new VerticalLayout();
        helpWindow.setContent(subContentHelp);
        subContentHelp.addComponents(
                new Label("Norint prideti naują vartotoją, reikia paspausti 'Add new Customer' mygtuką."),
                new Label("Dešinėje atsiras forma, kurioje galėsite įvesti vardą, pavardę ir kitus duomenis."),
                new Label("Surašę duomenis spauskite 'Save' ir jūsų suvestus vartotojas bus įkeltas į lentelę."),
                new Label("Paspaudus ant lentelės stulpelių kaip 'email' duomenis surušiuojami pagal abecelę."),
                new Label("Norint ištrinti vartotoja, reikia paspausti ant vartotojo eilutės, toliau dešineje"),
                new Label("formoje spauskite 'delete'")
        );
        helpWindow.center();

        VerticalLayout subContentAbout = new VerticalLayout();
        subContentAbout.setWidth("300");
        // subContentAbout.setHeight("300");
        aboutWindow.setContent(subContentAbout);
        subContentAbout.addComponents(
                new Label("Paulius Mikalauskas"),
                new Label("I11-2"),
                new Label("Versija 8.05")
        );
        aboutWindow.center();

        menuBar.addItem("About", (MenuBar.Command) menuItem -> {
            addWindow(aboutWindow);
        });

        menuBar.addItem("Help", (MenuBar.Command) menuItem -> {
            addWindow(helpWindow);
        });

        filterText.setPlaceholder("filter by name...");
        filterText.addValueChangeListener(e -> updateList());
        filterText.setValueChangeMode(ValueChangeMode.LAZY);

        Button clearFilterTextBtn = new Button(FontAwesome.TIMES);
        clearFilterTextBtn.setDescription("Clear the current filter");
        clearFilterTextBtn.addClickListener(e -> filterText.clear());

        CssLayout filtering = new CssLayout();
        filtering.addComponents(filterText, clearFilterTextBtn);
        filtering.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);

        Button addCustomerBtn = new Button("Add new customer");
        addCustomerBtn.addClickListener(e -> {
            grid.asSingleSelect().clear();
            form.setCustomer(new Customer());
        });

        MenuBar.Command mycommand = (MenuBar.Command) selectedItem -> {
            addWindow(helpWindow);
            logger.log(Level.ALL, "LUL");
        };

        HorizontalLayout toolbar = new HorizontalLayout(menuBar, filtering, addCustomerBtn);

        grid.setColumns("firstName", "lastName", "email");

        HorizontalLayout main = new HorizontalLayout(grid, form);
        main.setSizeFull();
        grid.setSizeFull();
        main.setExpandRatio(grid, 1);

        layout.addComponents(toolbar, main);

        updateList();

        setContent(layout);

        form.setVisible(false);

        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() == null) {
                form.setVisible(false);
            } else {
                form.setCustomer(event.getValue());
            }
        });
    }

    public void updateList() {
        List<Customer> customers = service.findAll(filterText.getValue());
        grid.setItems(customers);
    }

    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {
    }
}
