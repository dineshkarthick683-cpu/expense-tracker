package com.example.demo;

import com.example.demo.AppUser.AppUserModel;
import com.example.demo.AppUser.AppUserSerice;
import com.example.demo.AppUser.LoginViewImpl;
import com.example.demo.AppUser.UserDetailsView;
import com.example.demo.financeview.FinanceView;
import com.example.demo.financeview.IncomeService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Route("Main")
public class AppExpenseMainViewImpl extends VerticalLayout {


    private ExpenseService expenseService;

    private IncomeService incomeService;


    Dialog sideDrawer = new Dialog();
    Div card = new Div();
    Span username = new Span();
    Span budgetLeft = new Span();
    Span todayExpense = new Span();
    Span Monthexpense = new Span();
    Span title = new Span();
    VerticalLayout cardContent = new VerticalLayout();
    AppUserModel user = VaadinSession.getCurrent().getAttribute(AppUserModel.class);

    @Autowired
    private CommonUtil commonUtil;

    public AppExpenseMainViewImpl(ExpenseService expenseService,IncomeService incomeService)  {

        this.expenseService = expenseService;

        this.incomeService = incomeService;

        VaadinSession.getCurrent().setAttribute("financeValue", "Expense");

        if(user==null){
            UI.getCurrent().navigate(LoginViewImpl.class);
            return;
        }


        String total = populatedExpenseAmount();
        setSizeFull();
        setPadding(false);
        setSpacing(false);

        // =========================
        // Header
        // =========================
        Span header = new Span("Smart Expense");


        header.getStyle()
                .set("font-size", "1.5rem !important")   // force override
                .set("font-weight", "600")
                .set("background", "linear-gradient(to right, #2a5298, #1e3c72)")
                .set("color", "white")
                .set("padding", "0.5rem");
        header.setWidthFull();             // stretch across screen


        Button menuBtn = new Button(VaadinIcon.MENU.create());
        menuBtn.getStyle().set("background-color", "transparent");
        menuBtn.getStyle().set("color", "white");
        menuBtn.getStyle().set("border", "none");
        menuBtn.getStyle().set("font-size", "24px");

        HorizontalLayout rightSide = new HorizontalLayout();
        //rightSide.setWidthFull();  // optional fixed width
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
        // Expense List
        // =========================

        // Example category tile
        Button giftBtnTile = new Button("Gift", VaadinIcon.GIFT.create());
        Button ShoppingBtnTile = new Button("Shopping", VaadinIcon.CART.create());
        Button HomeBtnTile = new Button("Home", VaadinIcon.HOME.create());
        Button VegetableBtnTile = new Button("Vegetable", VaadinIcon.CUTLERY.create());
        Button PharmacyBtnTile = new Button("Pharmacy", VaadinIcon.HOSPITAL.create());
        Button FoodBtnTile = new Button("Food", VaadinIcon.COFFEE.create());

        HorizontalLayout row1 = new HorizontalLayout(
                createExpenseRow(giftBtnTile,"Gift"),
                createExpenseRow(ShoppingBtnTile,"Shopping")


        );


        row1.setWidthFull();

        HorizontalLayout row2 = new HorizontalLayout(
                createExpenseRow(VegetableBtnTile,"Vegetable"),
                createExpenseRow(PharmacyBtnTile,"Pharmacy")
        );
        row2.setWidthFull();

        HorizontalLayout row3 = new HorizontalLayout(
                createExpenseRow(HomeBtnTile,"Home"),
                createExpenseRow(FoodBtnTile,"Food")

        );
        row3.setWidthFull();

        VerticalLayout expenseList = new VerticalLayout();
        expenseList.add(row1,row2,row3);
        expenseList.setWidthFull();


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
        add(
                headerLayout,
                expenseList,
                nav
        );

        expand(expenseList);

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

                UI.getCurrent().navigate(AppExpenseRecordsView.class));





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
        chart.addClickListener(event -> UI.getCurrent().navigate(ExpenseChartView.class));

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



    }


    public List<String> getLastFiveTransactions() {
        //List<AppExpenseMainViewModel> lstAllExpense = expenseService.getAllExpenses();

        List<AppExpenseMainViewModel> lstAllExpense = expenseService.getAllExpenses().stream().
                filter(p -> p.getUsername().equals(user.getUsername())).collect(Collectors.toList());
        if (lstAllExpense == null || lstAllExpense.isEmpty()) {
            return Collections.emptyList();
        }

        // Sort by expenseDate descending (latest first)
        List<AppExpenseMainViewModel> sortedList = lstAllExpense.stream()
                .filter(exp -> exp.getExpenseDate() != null)
                .sorted((a, b) -> b.getExpenseDate().compareTo(a.getExpenseDate()))
                .toList();

        // Take last 5 and format as "Type Remarks Amount"
        return sortedList.stream()
                .limit(3)
                .map(exp -> exp.getCategory() + ":-" +
                        exp.getExpenseName() + ":" +
                        exp.getAmount() + " Rs")
                .toList();
    }

    public List<Double> getBalanceIncomeSalaryForMonthAmount() {

        double thisMonthSalary = incomeService.getAllIncome().stream()
                // only Salary category
                .filter(incomeObj -> "Salary".equalsIgnoreCase(incomeObj.getCategory()))

                .filter(incomeObj -> incomeObj.getUsername().equals(user.getUsername()))
                // only current month
                .filter(incomeObj -> {
                    LocalDate date = incomeObj.getIncomeDate().toLocalDate();
                    YearMonth currentMonth = YearMonth.now();
                    return YearMonth.from(date).equals(currentMonth);
                })
                // sum amounts
                .mapToDouble(incomeObj -> incomeObj.getAmount().doubleValue())
                .sum();

        // Wrap the result in a list since your method returns List<Double>
        return Collections.singletonList(thisMonthSalary);
    }

    private HorizontalLayout createExpenseRow(Button btn,String Category) {

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
            AppExpenseAddMainDialogImpl dialog =
                    new AppExpenseAddMainDialogImpl(Category,expenseService,commonUtil);

            dialog.open();
        });

        HorizontalLayout row =
                new HorizontalLayout(btn);

        row.setWidthFull();
        row.setAlignItems(Alignment.CENTER);
        row.setJustifyContentMode(JustifyContentMode.CENTER);

        return row;
    }

    public String populatedExpenseAmount(){

        double totalAmount = 0.0;
        //List<AppExpenseMainViewModel> lstAllExpense = expenseService.getAllExpenses();
        List<AppExpenseMainViewModel> lstAllExpense = expenseService.getAllExpenses().stream().
                filter(p -> p.getUsername().equals(user.getUsername())).collect(Collectors.toList());
        if(!lstAllExpense.isEmpty()){

             totalAmount = lstAllExpense.stream().filter(p->p.getAmount()!=null).mapToDouble(AppExpenseMainViewModel::getAmount).sum();
        }


        return "" + totalAmount;
    }


    public String populatedTodayExpenseAmount() {
        double totalAmount = 0.0;

        // Get all expenses
        //List<AppExpenseMainViewModel> lstAllExpense = expenseService.getAllExpenses();
        List<AppExpenseMainViewModel> lstAllExpense = expenseService.getAllExpenses().stream().
                filter(p -> p.getUsername().equals(user.getUsername())).collect(Collectors.toList());
        if (!lstAllExpense.isEmpty()) {
            LocalDate today = LocalDate.now();

            // Filter only today's expenses and sum their amounts
            totalAmount = lstAllExpense.stream()
                    .filter(exp -> exp.getExpenseDate() != null && exp.getExpenseDate().toLocalDate().isEqual(today))
                    .filter(exp -> exp.getAmount() != null)
                    .mapToDouble(AppExpenseMainViewModel::getAmount)
                    .sum();
        }

        return String.valueOf(totalAmount);
    }

    public String populatedMonthlyExpenseAmount() {
        double totalAmount = 0.0;

        // Get all expenses
        //List<AppExpenseMainViewModel> lstAllExpense = expenseService.getAllExpenses();
        List<AppExpenseMainViewModel> lstAllExpense = expenseService.getAllExpenses().stream().
                filter(p -> p.getUsername().equals(user.getUsername())).collect(Collectors.toList());

        if (!lstAllExpense.isEmpty()) {
            LocalDate today = LocalDate.now();
            int currentMonth = today.getMonthValue();
            int currentYear = today.getYear();

            // Filter only current month's expenses and sum their amounts
            totalAmount = lstAllExpense.stream()
                    .filter(exp -> exp.getExpenseDate() != null)
                    .filter(exp -> {
                        LocalDate expenseDate = exp.getExpenseDate().toLocalDate();
                        return expenseDate.getMonthValue() == currentMonth &&
                                expenseDate.getYear() == currentYear;
                    })
                    .filter(exp -> exp.getAmount() != null)
                    .mapToDouble(AppExpenseMainViewModel::getAmount)
                    .sum();
        }

        return String.valueOf(totalAmount);
    }



    public void setBtnComponentStyle(Button btnCommon){
        btnCommon.getStyle().set("border-radius", "8px"); // square with rounded corners
        btnCommon.getStyle().set("box-shadow", "0 4px 10px rgba(0,0,0,0.2)");
        btnCommon.getStyle().set("background-color", "#FFD700"); // golden color
        btnCommon.getStyle().set("color", "black");
        btnCommon.getStyle().set("flex-direction", "column"); // icon above text
        btnCommon.getStyle().set("align-items", "center");
        btnCommon.getStyle().set("justify-content", "center");
    }

    public void initLoad(){
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
                .set("padding", "20px")
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

        double balance = getBalanceIncomeSalaryForMonthAmount().get(0) - Double.valueOf(populatedMonthlyExpenseAmount());
        // Clamp negative values to 0
        if (balance < 0) {
            balance = 0;
        }
        budgetLeft = new Span("💰 Budget Left: ₹ " +balance);
        budgetLeft.getStyle()
                .set("font-size", "18px")
                .set("font-weight", "600")
                .set("color", "#2E7D32"); // Green


        todayExpense = new Span("📉 Today Expense: ₹" + populatedTodayExpenseAmount());
        todayExpense.getStyle().set("font-size", "15px").set("color", "#c62828").set("font-weight", "700");

        Monthexpense = new Span("📉 Month Expense: ₹" + populatedMonthlyExpenseAmount());
        Monthexpense.getStyle().set("font-size", "15px").set("color", "#c62828").set("font-weight", "700");

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
                budgetLeft,
                todayExpense,
                Monthexpense,
                title
        );

        for (String lastExpense : lastFiveTransactions) {
            Span txn = new Span("• " + lastExpense);
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
                //.set("padding", "10px 20px")
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


}