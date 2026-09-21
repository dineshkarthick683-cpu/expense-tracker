package com.example.demo.financeview;

import com.example.demo.*;
import com.example.demo.AppUser.AppUserModel;
import com.example.demo.AppUser.LoginViewImpl;
import com.example.demo.AppUser.UserDetailsView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@Route("IncomeMain")
public class AppIncomeMainViewImpl extends VerticalLayout {


    private IncomeService incomeService;

    @Autowired
    private CommonUtil commonUtil;

    Dialog sideDrawer = new Dialog();
    Span username = new Span();
    Span todayIncome = new Span();
    Span MonthIncome = new Span();
    VerticalLayout cardContent = new VerticalLayout();
    Span title = new Span();
    Div card = new Div();
    AppUserModel user = VaadinSession.getCurrent().getAttribute(AppUserModel.class);

    public AppIncomeMainViewImpl(IncomeService incomeService) {

        this.incomeService = incomeService;



        VaadinSession.getCurrent().setAttribute("financeValue", "Income");

        if(user==null){
            UI.getCurrent().navigate(LoginViewImpl.class);
            return;
        }


        String total = populatedIncomeAmount();
        setSizeFull();
        setPadding(false);
        setSpacing(false);

        // =========================
        // Header
        // =========================
        Span header = new Span("Smart Income");

//        header.getStyle()
//                .set("font-size", "32px")
//                //.set("font-weight", "bold")
//                 .set("background", "linear-gradient(to right, #2a5298, #1e3c72)")
//                .set("font-weight", "600")
//                .set("color", "white");;
//
//        header.getStyle().set("height", "60px");   // increase height
//        header.getStyle().set("padding", "20px");  // more breathing space;

        header.getStyle()
                .set("font-size", "1.5rem !important")   // force override
                .set("font-weight", "600")
                .set("background", "linear-gradient(to right, #2a5298, #1e3c72)")
                .set("color", "white")
                .set("padding", "0.5rem");
        header.setWidthFull();

        Button menuBtn = new Button(VaadinIcon.MENU.create());
        menuBtn.getStyle().set("background-color", "transparent");
        menuBtn.getStyle().set("color", "white");
        menuBtn.getStyle().set("border", "none");
        menuBtn.getStyle().set("font-size", "24px");

        HorizontalLayout rightSide = new HorizontalLayout();
        //rightSide.setWidth("100px"); // optional fixed width
        rightSide.setJustifyContentMode(JustifyContentMode.END);

        HorizontalLayout headerLayout = new HorizontalLayout(header,menuBtn,rightSide);
        headerLayout.setWidthFull();
        headerLayout.setAlignItems(Alignment.CENTER);
        headerLayout.setJustifyContentMode(JustifyContentMode.BETWEEN);
        headerLayout.getStyle().set("background", "linear-gradient(135deg, #1e3c72, #2a5298)");
        headerLayout.expand(header);

        menuBtn.addClickListener(e -> {
            initLoad();          // call your initialization logic
            sideDrawer.open();   // then open the drawer
        });




        // =========================
        // Income List
        // =========================

        // Example category tile
        Button salaryBtnTile = new Button("Salary", VaadinIcon.MONEY.create());
        Button investmentBtnTile = new Button("Invest", VaadinIcon.TRENDING_UP.create());
        Button goldBtnTile = new Button("Gold", VaadinIcon.COIN_PILES.create());
        Button LoanBtnTile = new Button("Loan", VaadinIcon.CREDIT_CARD.create());
        Button returnsBtnTile = new Button("Returns", VaadinIcon.TRENDING_UP.create());
        Button commonBtnTile = new Button("Common", VaadinIcon.MONEY.create());

        HorizontalLayout row1 = new HorizontalLayout(
                createIncomeRow(salaryBtnTile,"Salary"),
                createIncomeRow(investmentBtnTile,"Invest")


        );


        row1.setWidthFull();

        HorizontalLayout row2 = new HorizontalLayout(
                createIncomeRow(LoanBtnTile,"Loan"),
                createIncomeRow(returnsBtnTile,"Returns")

        );
        row2.setWidthFull();


        row1.setWidthFull();

        HorizontalLayout row3 = new HorizontalLayout(
                createIncomeRow(goldBtnTile,"Gold"),
                createIncomeRow(commonBtnTile,"Common")

        );
        row3.setWidthFull();

        VerticalLayout incomeList = new VerticalLayout();
        incomeList.add(row1,row2,row3);
        incomeList.setWidthFull();


        // =========================
        // Bottom Navigation
        // =========================
        Button records =
                new Button("",
                        VaadinIcon.HOME.create());
        setBtnComponentStyle(records);

        Button chart =
                new Button("",
                        VaadinIcon.CHART.create());
        setBtnComponentStyle(chart);


        Button me =
                new Button("",
                        VaadinIcon.USER.create());
        setBtnComponentStyle(me);

        HorizontalLayout nav = new HorizontalLayout(
                records,
                chart,
                me
        );


        nav.expand(records, chart, me);
        nav.setWidthFull();

        headerLayout.setWidthFull();
        incomeList.setWidthFull();


        add(headerLayout, incomeList, nav);

        expand(incomeList);

        /**
         * Records Navigation Button
         *
         * Purpose : Navigate user to Records screen.
         *
         * Author : Karthick S
         * Created : 03-Sep-2026 09:22 PM IST
         *
         * Version : 1.0
         */
        records.addClickListener(event ->

                UI.getCurrent().navigate(AppIncomeRecordsView.class));





        /**
         * 
         *
         * Purpose : Navigate user to Chart screen.
         *
         * Author : Karthick S
         * Created : 03-Sep-2026 09:22 PM IST
         *
         * Version : 1.0
         */
        chart.addClickListener(event -> UI.getCurrent().navigate(IncomeChartView.class));

        /**
         * User Details Navigation Button
         *
         * Purpose : Navigate user to Records screen.
         *
         * Author : Karthick S
         * Created : 03-Sep-2026 09:22 PM IST
         *
         * Version : 1.0
         */

        me.addClickListener(event ->

                UI.getCurrent().navigate(UserDetailsView.class));



//        nav.setFlexGrow(1, records);
//        nav.setFlexGrow(1, chart);
//        nav.setFlexGrow(1, me);

        // =========================
        // Main Layout
        // =========================

    }


    public List<String> getLastFiveTransactions() {
        //List<AppIncomeMainViewModel> lstAllIncome = incomeService.getAllIncome();

        List<AppIncomeMainViewModel> lstAllIncome = incomeService.getAllIncome().stream().
                filter(p -> p.getUsername().equals(user.getUsername())).collect(Collectors.toList());

        if (lstAllIncome == null || lstAllIncome.isEmpty()) {
            return Collections.emptyList();
        }

        // Sort by IncomeDate descending (latest first)
        List<AppIncomeMainViewModel> sortedList = lstAllIncome.stream()
                .filter(exp -> exp.getIncomeDate() != null)
                .sorted((a, b) -> b.getIncomeDate().compareTo(a.getIncomeDate()))
                .toList();

        // Take last 5 and format as "Type Remarks Amount"
        return sortedList.stream()
                .limit(3)
                .map(exp -> exp.getCategory() + ":-" +
                        exp.getRemarks() + ":" +
                        exp.getAmount() + " Rs")
                .toList();
    }

    private HorizontalLayout createIncomeRow(Button btn,String Category) {

//        btn.getStyle().set("width", "160px");
//        btn.getStyle().set("height", "120px");
        btn.getStyle().set("border-radius", "8px"); // square with rounded corners
        btn.getStyle().set("box-shadow", "0 4px 10px rgba(0,0,0,0.2)");
        btn.getStyle().set("background-color", "#FFD700"); // golden color
        btn.getStyle().set("color", "black");
        btn.getStyle().set("flex-direction", "column"); // icon above text
        btn.getStyle().set("align-items", "center");
        btn.getStyle().set("justify-content", "center");
        btn.setWidthFull();
        btn.setWidth("130px");
        btn.setHeight("120px");




        btn.addClickListener(event -> {
            AppIncomeAddMainDialogImpl dialog =
                    new AppIncomeAddMainDialogImpl(Category,incomeService,commonUtil);

            dialog.open();
        });

        HorizontalLayout row =
                new HorizontalLayout(btn);

        row.setWidthFull();
        row.setAlignItems(Alignment.CENTER);
        row.setJustifyContentMode(JustifyContentMode.CENTER);

        return row;
    }

    public String populatedIncomeAmount(){

        double totalAmount = 0.0;
        //List<AppIncomeMainViewModel> lstAllIncome = incomeService.getAllIncome();
        List<AppIncomeMainViewModel> lstAllIncome = incomeService.getAllIncome().stream().
                filter(p -> p.getUsername().equals(user.getUsername())).collect(Collectors.toList());
        if(!lstAllIncome.isEmpty()){

             totalAmount = lstAllIncome.stream().filter(p->p.getAmount()!=null && p.getCategory()!=null && p.getCategory().equalsIgnoreCase("Salary")).mapToDouble(AppIncomeMainViewModel::getAmount).sum();
        }


        return "" + totalAmount;
    }


    public String populatedTodayEIncomeAmount() {
        double totalAmount = 0.0;

        // Get all income
        List<AppIncomeMainViewModel> lstAllIncome = incomeService.getAllIncome().stream().
                filter(p -> p.getUsername().equals(user.getUsername())).collect(Collectors.toList());

        if (!lstAllIncome.isEmpty()) {
            LocalDate today = LocalDate.now();

            // Filter only today's Income and sum their amounts
            totalAmount = lstAllIncome.stream()
                    .filter(exp -> exp.getIncomeDate() != null && exp.getIncomeDate().toLocalDate().isEqual(today))
                    .filter(exp -> exp.getAmount() != null)
                    .filter(exp -> exp.getCategory() != null)
                    .filter(exp -> exp.getCategory().equalsIgnoreCase("Salary"))
                    .mapToDouble(AppIncomeMainViewModel::getAmount)
                    .sum();
        }

        return String.valueOf(totalAmount);
    }

    public String populatedMonthlyIncomeAmount() {
        double totalAmount = 0.0;

        // Get all Income
        //List<AppIncomeMainViewModel> lstAllIncome = incomeService.getAllIncome();
        List<AppIncomeMainViewModel> lstAllIncome = incomeService.getAllIncome().stream().
                filter(p -> p.getUsername().equals(user.getUsername())).collect(Collectors.toList());
        if (!lstAllIncome.isEmpty()) {
            LocalDate today = LocalDate.now();
            int currentMonth = today.getMonthValue();
            int currentYear = today.getYear();

            // Filter only current month's income and sum their amounts
            totalAmount = lstAllIncome.stream()
                    .filter(exp -> exp.getIncomeDate() != null)
                    .filter(exp -> {
                        LocalDate incomeDate = exp.getIncomeDate().toLocalDate();
                        return incomeDate.getMonthValue() == currentMonth &&
                                incomeDate.getYear() == currentYear;
                    })
                    .filter(exp -> exp.getAmount() != null)
                    .filter(exp -> exp.getCategory() != null)
                    .filter(exp -> exp.getCategory().equalsIgnoreCase("Salary"))
                    .mapToDouble(AppIncomeMainViewModel::getAmount)
                    .sum();
        }

        return String.valueOf(totalAmount);
    }



    public void  initLoad(){

        // Create sidebar drawer
        sideDrawer = new Dialog();
        //sideDrawer.setWidth("580px");
        sideDrawer.setWidthFull();
        sideDrawer.setHeight(null);
        sideDrawer.getElement().getStyle()
                .set("position", "fixed")
                .set("top", "0")
                .set("right", "0")
                .set("margin", "0")
                //.set("padding", "20px")
                .set("background", "#f9f9f9")
                .set("box-shadow", "-4px 0 12px rgba(0,0,0,0.2)");

        // Sidebar card layout
        card = new Div();

        card.getStyle()
                .set("background", "white")
                .set("border-radius", "12px")
                .set("box-shadow", "0 4px 12px rgba(0,0,0,0.1)")
                .set("padding", "20px")
                .set("margin", "10px");

        username = new Span("👤 User: " + user.getUsername());
        username.getStyle().set("font-size", "18px").set("font-weight", "600");

        todayIncome = new Span("📉 Today Income: ₹" + populatedTodayEIncomeAmount());
        todayIncome.getStyle().set("font-size", "15px").set("color", "green").set("font-weight", "700");

        MonthIncome = new Span("📉 Month Income: ₹" + populatedMonthlyIncomeAmount());
        MonthIncome.getStyle().set("font-size", "15px").set("color", "green").set("font-weight", "700");

        title = new Span("🕒 Recent Txns");
        title.getStyle()
                .set("font-weight", "bold")
                .set("font-size", "16px");

        List<String> lastFiveTransactions = getLastFiveTransactions();

        cardContent = new VerticalLayout();
        //new code------------------
        cardContent.setPadding(false);
        cardContent.setSpacing(true);
        //cardContent.setSizeFull();
        cardContent.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        cardContent.setAlignItems(FlexComponent.Alignment.START);
        //card.setSizeFull();
       //-------------------------------
        cardContent.add(
                username,
                todayIncome,
                MonthIncome,
                title
        );

        for (String lastIncome : lastFiveTransactions) {
            Span txn = new Span("• " + lastIncome);
            txn.getStyle().set("display", "block");
            cardContent.add(txn);
        }

        // 👉 Add navigation button
        Button financeBtn = new Button("Go to Finance View");
        financeBtn.getStyle()
                //.set("margin-top", "15px")
                .set("background-color", "#4CAF50")
                .set("color", "white")
                .set("border-radius", "8px")
                .set("padding", "10px 20px")
                .set("box-shadow", "0 4px 8px rgba(0,0,0,0.2)");

        financeBtn.addClickListener(e -> {
            UI.getCurrent().navigate(FinanceView.class);
            sideDrawer.close(); // optional: close drawer after navigation
        });

        financeBtn.setWidthFull();
        cardContent.add(financeBtn);

        card.add(cardContent);
        sideDrawer.add(card);
    }

    public void setBtnComponentStyle(Button btnCommon){
        //btnCommon.getStyle().set("width", "100px");
        //btnCommon.setWidthFull();
        //btnCommon.getStyle().set("height", "50px");
        btnCommon.getStyle().set("border-radius", "8px"); // square with rounded corners
        btnCommon.getStyle().set("box-shadow", "0 4px 10px rgba(0,0,0,0.2)");
        btnCommon.getStyle().set("background-color", "#FFD700"); // golden color
        btnCommon.getStyle().set("color", "black");
        btnCommon.getStyle().set("flex-direction", "column"); // icon above text
        btnCommon.getStyle().set("align-items", "center");
        btnCommon.getStyle().set("justify-content", "center");
    }


}