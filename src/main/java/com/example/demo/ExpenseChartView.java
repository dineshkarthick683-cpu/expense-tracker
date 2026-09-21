package com.example.demo;
import com.example.demo.AppUser.AppUserModel;
import com.example.demo.AppUser.LoginViewImpl;
import com.example.demo.financeview.AppIncomeMainViewImpl;
import com.example.demo.financeview.AppIncomeMainViewModel;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.html.H2;
//import com.vaadin.flow.component.charts.Chart;
//import com.vaadin.flow.component.charts.model.*;
import java.util.List;
import com.vaadin.flow.component.button.Button;
//import com.vaadin.flow.component.charts.Chart;
//import com.vaadin.flow.component.charts.model.*;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
//import com.vaadin.flow.component.charts.Chart;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Route("charts")
public class ExpenseChartView extends VerticalLayout {

    private final ExpenseService expenseService;

    private final DatePicker startDate = new DatePicker("Start Date");
    private final DatePicker endDate = new DatePicker("End Date");

    private final VerticalLayout chartContainer = new VerticalLayout();

    AppUserModel user = VaadinSession.getCurrent().getAttribute(AppUserModel.class);

    public ExpenseChartView(ExpenseService expenseService) {

        this.expenseService = expenseService;

        if(user==null){
            UI.getCurrent().navigate(LoginViewImpl.class);
            return;
        }

        setSizeFull();
        setPadding(true);
        setSpacing(true);
        chartContainer.setWidthFull();
        chartContainer.setSpacing(true);
        chartContainer.setPadding(false);

        H2 title = new H2("Expense Dashboard");

        startDate.setValue(LocalDate.now().withDayOfMonth(1));
        endDate.setValue(LocalDate.now());

        Button loadButton = new Button(VaadinIcon.DATABASE.create());

        HorizontalLayout emptyLayout2 = new HorizontalLayout(startDate);
        emptyLayout2.setWidthFull();

        HorizontalLayout emptyLayout3 = new HorizontalLayout(endDate,loadButton);
        emptyLayout3.setAlignItems(FlexComponent.Alignment.END);
        emptyLayout2.setWidthFull();


        loadButton.addClickListener(event -> loadCharts());

        VerticalLayout filterLayout =
                new VerticalLayout(emptyLayout2, emptyLayout3);
        filterLayout.setWidthFull();


        // Action buttons
        Button backBtn = new Button("Back", VaadinIcon.ARROW_LEFT.create());
        backBtn.getStyle().set("background", "linear-gradient(to right, #4facfe, #00f2fe)");
        backBtn.getStyle().set("color", "white");

        add(title, filterLayout, chartContainer,backBtn);

        loadCharts();

        backBtn.addClickListener(event -> {


            UI.getCurrent().navigate(AppIncomeMainViewImpl.class);

        });
    }

    private void loadCharts() {

        List<AppExpenseMainViewModel> allExpenses =
                expenseService.getAllExpenses();

        LocalDate start = startDate.getValue();
        LocalDate end = endDate.getValue();

        List<AppExpenseMainViewModel> filteredList =
                allExpenses.stream()
                        .filter(exp -> {

                            LocalDate expDate =
                                    exp.getExpenseDate().toLocalDate();

                            return !expDate.isBefore(start)
                                    && !expDate.isAfter(end);
                        })
                        .toList();

        chartContainer.removeAll();

        List<AppExpenseMainViewModel> lstOfDetails = filteredList.stream().filter(p -> p.getUsername().equals(user.getUsername())).collect(Collectors.toList());

        VerticalLayout pieChart = createPieChart(lstOfDetails);
        VerticalLayout lineChart = createLineChart(lstOfDetails);
        VerticalLayout barChart = createBarChart(lstOfDetails);

        pieChart.setWidthFull();
        barChart.setWidthFull();
        lineChart.setWidthFull();
//        pieChart.setWidth("50%");
//        barChart.setWidth("50%");

        VerticalLayout topRow =
                new VerticalLayout(pieChart, barChart,lineChart);

        topRow.setWidthFull();

        chartContainer.add(
                topRow,
                lineChart
        );
    }

    private VerticalLayout createPieChart(
            List<AppExpenseMainViewModel> expenses) {

        Map<String, Double> categoryMap =
                expenses.stream()
                        .collect(Collectors.groupingBy(
                                AppExpenseMainViewModel::getCategory,
                                Collectors.summingDouble(
                                        AppExpenseMainViewModel::getAmount)));

        double total = categoryMap.values()
                .stream()
                .mapToDouble(Double::doubleValue)
                .sum();

        String[] colors = {
                "#4285F4",
                "#EA4335",
                "#FBBC05",
                "#34A853",
                "#9C27B0",
                "#FF9800",
                "#795548"
        };

        StringBuilder gradient = new StringBuilder();
        double current = 0;
        int index = 0;

        for (Map.Entry<String, Double> entry : categoryMap.entrySet()) {

            double percent = (entry.getValue() / total) * 100;

            gradient.append(colors[index % colors.length])
                    .append(" ")
                    .append(current)
                    .append("%, ")
                    .append(colors[index % colors.length])
                    .append(" ")
                    .append(current + percent)
                    .append("%");

            current += percent;

            if (index < categoryMap.size() - 1) {
                gradient.append(", ");
            }

            index++;
        }

        Div pie = new Div();

        pie.getStyle()
                .set("height", "200px")
                .set("border-radius", "50%")
                .set("background",
                        "conic-gradient(" + gradient + ")");
        pie.setWidthFull();

        VerticalLayout legend = new VerticalLayout();
        legend.setSpacing(false);
        legend.setPadding(false);

        index = 0;
        for (Map.Entry<String, Double> entry : categoryMap.entrySet()) {

            Span item = new Span(
                    "■ " + entry.getKey()
                            + " ₹"
                            + String.format("%.2f",
                            entry.getValue()));

            item.getStyle().set(
                    "color",
                    colors[index % colors.length]);

            legend.add(item);

            index++;
        }

        HorizontalLayout layout =
                new HorizontalLayout(pie, legend);

        layout.setAlignItems(Alignment.CENTER);

        VerticalLayout wrapper =
                new VerticalLayout(
                        new H2("Expense By Category"),
                        layout);

        wrapper.setWidthFull();

        return wrapper;
    }

    private VerticalLayout createLineChart(
            List<AppExpenseMainViewModel> expenses) {

        Map<LocalDate, Double> dailyMap =
                expenses.stream()
                        .collect(Collectors.groupingBy(
                                exp -> exp.getExpenseDate().toLocalDate(),
                                TreeMap::new,
                                Collectors.summingDouble(
                                        AppExpenseMainViewModel::getAmount
                                )
                        ));

        double maxAmount = dailyMap.values()
                .stream()
                .mapToDouble(Double::doubleValue)
                .max()
                .orElse(1);

        HorizontalLayout graphLayout = new HorizontalLayout();
        graphLayout.setWidthFull();
        graphLayout.setSpacing(true);
        graphLayout.setAlignItems(Alignment.END);

        for (Map.Entry<LocalDate, Double> entry : dailyMap.entrySet()) {

            double amount = entry.getValue();

            int height =
                    (int) ((amount / maxAmount) * 250);

            Div bar = new Div();
            bar.setHeight(height + "px");
            bar.setWidthFull();

            bar.getStyle()
                    .set("background", "#4285F4")
                    .set("border-radius", "4px");

            Span amountLabel =
                    new Span(String.valueOf(
                            Math.round(amount)));

            Span dateLabel =
                    new Span(entry.getKey().toString());

            dateLabel.getStyle()
                    .set("font-size", "10px");

            VerticalLayout col =
                    new VerticalLayout(
                            amountLabel,
                            bar,
                            dateLabel);

            col.setSpacing(false);
            col.setPadding(false);
            col.setAlignItems(Alignment.CENTER);

            graphLayout.add(col);
        }

        VerticalLayout wrapper =
                new VerticalLayout(
                        new H2("Daily Expense Trend"),
                        graphLayout);

        wrapper.setWidthFull();

        return wrapper;
    }

    private VerticalLayout createBarChart(
            List<AppExpenseMainViewModel> expenses) {

        Map<String, Double> categoryMap =
                expenses.stream()
                        .collect(Collectors.groupingBy(
                                AppExpenseMainViewModel::getCategory,
                                Collectors.summingDouble(
                                        AppExpenseMainViewModel::getAmount
                                )
                        ));

        double maxAmount = categoryMap.values()
                .stream()
                .mapToDouble(Double::doubleValue)
                .max()
                .orElse(1);

        VerticalLayout barsLayout = new VerticalLayout();
        barsLayout.setSpacing(false);
        barsLayout.setPadding(false);
        barsLayout.setWidthFull();

        categoryMap.forEach((category, amount) -> {

            int width =
                    (int) ((amount / maxAmount) * 400);

            Div bar = new Div();

            //bar.setWidth(width + "px");
            bar.setHeight("25px");
            bar.setWidthFull();

            bar.getStyle()
                    .set("background", "#34A853")
                    .set("border-radius", "5px");

            Span label = new Span(
                    category + " : ₹" +
                            String.format("%.2f", amount));

            VerticalLayout row = new VerticalLayout(
                    label,
                    bar);

            row.setSpacing(false);
            row.setPadding(false);

            barsLayout.add(row);
        });

        VerticalLayout wrapper =
                new VerticalLayout(
                        new H2("Top Categories"),
                        barsLayout);

        wrapper.setWidthFull();

        return wrapper;
    }
}
