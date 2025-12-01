package ru.kotletkin.aard.registration.ui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import ru.kotletkin.aard.registration.RegistrationService;
import ru.kotletkin.aard.registration.dto.RegistrationDTO;
import ru.kotletkin.aard.registration.dto.RegistrationSort;

import java.util.List;

@Route("/registrations")
@PageTitle("Aard Registrations")
public class RegistrationView extends VerticalLayout {

    private final RegistrationService registrationService;
    private final Dialog dialog = new Dialog();

    public RegistrationView(RegistrationService registrationServiceBean) {

        createHeader();

        this.registrationService = registrationServiceBean;

        Grid<RegistrationDTO> grid = new Grid<>(RegistrationDTO.class, false);
        grid.setWidthFull();
        grid.setAllRowsVisible(true);

        grid.addColumn(RegistrationDTO::id).setHeader("ID").setAutoWidth(true).setSortable(true);
        grid.addColumn(RegistrationDTO::name).setHeader("Имя").setAutoWidth(true).setSortable(true);
        grid.addColumn(RegistrationDTO::version).setHeader("Версия").setAutoWidth(true).setSortable(true);
        grid.addColumn(RegistrationDTO::image).setHeader("Образ").setAutoWidth(true).setSortable(true);
        grid.addColumn(RegistrationDTO::author).setHeader("Автор").setAutoWidth(true).setSortable(true);
        grid.addColumn(RegistrationDTO::gitlabLink).setHeader("Ссылка на Gitlab").setAutoWidth(true);

        List<RegistrationDTO> registrationDTOs = registrationService.findAll(0, 10, RegistrationSort.ID_ASC)
                .getRegistrations();

        GridListDataView<RegistrationDTO> dataView = grid.setItems(registrationDTOs);


        HorizontalLayout searchContainer = new HorizontalLayout();
        searchContainer.setWidthFull();
        searchContainer.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        searchContainer.setPadding(false);
        searchContainer.setSpacing(false);

        TextField searchField = new TextField();
        searchField.setWidth("50%"); // или задайте фиксированную ширину, например "300px"
        searchField.setPlaceholder("Поиск");
        searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        searchField.setValueChangeMode(ValueChangeMode.EAGER);
        searchField.addValueChangeListener(e -> dataView.refreshAll());

        searchContainer.add(searchField);

        dataView.addFilter(registrationDTO -> {
            String searchTerm = searchField.getValue().trim();

            if (searchTerm.isEmpty())
                return true;

            boolean matchesName = matchesTerm(registrationDTO.name(),
                    searchTerm);
            boolean matchesGitlab = matchesTerm(registrationDTO.gitlabLink(), searchTerm);
            boolean matchesDocker = matchesTerm(registrationDTO.image(), searchTerm);

            return matchesName || matchesGitlab || matchesDocker;
        });

        grid.getStyle()
                .set("border", "1px solid var(--lumo-contrast-20pct)")
                .set("border-radius", "8px");

        dialog.setHeaderTitle("reg description");
        Button closeButton = new Button("Close");
        closeButton.addClickListener(e -> dialog.close());

        grid.addItemClickListener(event -> {
            if (event.getClickCount() == 1) { // Одинарный клик
                dialog.open();
            }
        });

        add(dialog);
        add(searchContainer);
        add(grid);
        setPadding(true);

    }


    private void createHeader() {
        H2 header = new H2("Зарегистрированные модули");
        header.getStyle()
                .set("margin-top", "auto")
                .set("margin-bottom", "auto")
                .set("margin-left", "auto")   // Центрирование через margin
                .set("margin-right", "auto")
                .set("display", "block")      // Для работы margin auto
                .set("text-align", "center"); // Центрирование текста внутри

        this.add(header);
    }

    private boolean matchesTerm(String value, String searchTerm) {
        return searchTerm == null || searchTerm.isEmpty()
                || value.toLowerCase().contains(searchTerm.toLowerCase());
    }
}
