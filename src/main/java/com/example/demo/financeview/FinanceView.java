package com.example.demo.financeview;

import com.example.demo.AppExpenseAddMainDialogImpl;
import com.example.demo.AppExpenseMainViewImpl;
import com.example.demo.AppUser.AppUserModel;
import com.example.demo.AppUser.LoginViewImpl;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.awt.*;
import java.time.LocalDateTime;

@Route("finance")
public class FinanceView extends VerticalLayout {

    public FinanceView() {
        // Header


        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        VaadinSession.getCurrent().getAttribute(AppUserModel.class);

        if (authentication != null && authentication.getPrincipal() instanceof OAuth2User oauthUser) {

            AppUserModel user = new AppUserModel();

            user.setUsername(oauthUser.getAttribute("name"));

            user.setEmailId(oauthUser.getAttribute("email"));

            user.setCreatedDate(LocalDateTime.now());

            VaadinSession.getCurrent().setAttribute(AppUserModel.class, user);
        }else{
            AppUserModel user = VaadinSession.getCurrent().getAttribute(AppUserModel.class);
            if(user==null){
                UI.getCurrent().navigate(LoginViewImpl.class);
                return;
            }
        }

        H3 header = new H3("🏠 Finance View");
        header.getStyle()
                .set("font-size", "32px")
                //.set("font-weight", "bold")
                .set("background", "linear-gradient(to right, #2a5298, #1e3c72)")
                .set("font-weight", "600")
                .set("color", "white");;

        header.getStyle().set("height", "60px");   // increase height
        header.getStyle().set("padding", "20px");  // more breathing space;

        HorizontalLayout headerLayout = new HorizontalLayout(header);
        headerLayout.setWidthFull();
        headerLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        headerLayout.setJustifyContentMode(JustifyContentMode.BETWEEN);
        headerLayout.getStyle().set("background", "linear-gradient(135deg, #1e3c72, #2a5298)");
        headerLayout.expand(header);

        // Buttons
        Button incomeBtnTile = new Button("Income", VaadinIcon.MONEY.create());
        incomeBtnTile.addClickListener(e -> UI.getCurrent().navigate(AppIncomeMainViewImpl.class));

        Button expenseBtnTile = new Button("Expense", VaadinIcon.EXCHANGE.create());
        expenseBtnTile.addClickListener(e -> UI.getCurrent().navigate(AppExpenseMainViewImpl.class));

        // Layout row
        HorizontalLayout row1 = new HorizontalLayout(
                createTile(incomeBtnTile),
                createTile(expenseBtnTile)
        );
        // Center them in the middle
        row1.setWidthFull();
        row1.setAlignItems(Alignment.CENTER);
        row1.setJustifyContentMode(JustifyContentMode.CENTER);
        row1.setSpacing(true); // adds space between components
        row1.getStyle().set("margin-top", "50px"); // push down from header

        add(headerLayout,row1);
        setFlexGrow(1, row1);
    }

    private HorizontalLayout createTile(Button btn) {
        btn.getStyle().set("width", "160px");
        btn.getStyle().set("height", "120px");
        btn.getStyle().set("border-radius", "8px");
        btn.getStyle().set("box-shadow", "0 4px 10px rgba(0,0,0,0.2)");
        btn.getStyle().set("background-color", "#FFD700"); // golden
        btn.getStyle().set("color", "black");

        HorizontalLayout row = new HorizontalLayout(btn);
        row.setAlignItems(Alignment.CENTER);
        row.setJustifyContentMode(JustifyContentMode.CENTER);
        return row;
    }
}


