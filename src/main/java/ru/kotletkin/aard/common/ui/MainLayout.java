package ru.kotletkin.aard.common.ui;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.Layout;
import com.vaadin.flow.theme.lumo.LumoUtility;

@Layout
public final class MainLayout extends AppLayout {

    MainLayout() {

        DrawerToggle toggle = new DrawerToggle();

        H1 title = new H1("Aard Platform");
        title.getStyle().set("font-size", "var(--lumo-font-size-l)").set("margin", "0");

        SideNav nav = getSideNav();

        Scroller scroller = new Scroller(nav);
        scroller.setClassName(LumoUtility.Padding.SMALL);

        addToDrawer(scroller);
        addToNavbar(toggle, title);
    }

    private SideNav getSideNav() {

        SideNav nav = new SideNav();

        nav.setLabel("Основная навигация");

        SideNavItem mainPage = new SideNavItem("Главная страница", "");
        mainPage.setPrefixComponent(VaadinIcon.SHIELD.create());

        SideNavItem registrations = new SideNavItem("Модули");
        registrations.setExpanded(false);
        registrations.setPrefixComponent(VaadinIcon.DATABASE.create());

        SideNavItem getAllModules = new SideNavItem("Зарегистрированные", "/registrations");
        SideNavItem registerModule = new SideNavItem("Регистрация", "/registrations/register");

        registrations.addItem(getAllModules, registerModule);

        nav.addItem(mainPage, registrations);

        return nav;
    }

}
