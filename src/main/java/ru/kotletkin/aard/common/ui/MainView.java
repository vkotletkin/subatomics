package ru.kotletkin.aard.common.ui;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route("")
@PageTitle("Aard Platform")
public class MainView extends Main {

    public MainView() {
        H1 title = new H1("Aard Platform");
        title.getStyle().set("margin", "0");
        H2 description = new H2("Однажды здесь будет описание, но пока что я плохо умею в фронтенд");


        add(title);
        add(description);

        getStyle().set("padding", "1em").setTextAlign(Style.TextAlign.CENTER);

    }
}
