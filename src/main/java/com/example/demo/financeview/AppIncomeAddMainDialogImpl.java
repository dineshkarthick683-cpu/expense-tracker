package com.example.demo.financeview;

import com.example.demo.AppExpenseMainViewModel;
import com.example.demo.AppUser.AppUserModel;
import com.example.demo.AppUser.LoginViewImpl;
import com.example.demo.CommonUtil;
import com.example.demo.ExpenseService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalTime;

@Route("addincome")
public class AppIncomeAddMainDialogImpl extends Dialog {


    private IncomeService incomeService;

    private CommonUtil commonUtil;

    private String category;

    private DatePicker incomeDate;

    public AppIncomeAddMainDialogImpl(String category, IncomeService incomeService, CommonUtil commonUtil) {

        this.category = category;
        this.incomeService = incomeService;
        this.commonUtil = commonUtil;

        AppUserModel user = VaadinSession.getCurrent().getAttribute(AppUserModel.class);

        if(user==null){
            UI.getCurrent().navigate(LoginViewImpl.class);
            return;
        }

        setWidth("400px");
//        setPadding(true);
//        setSpacing(true);


        // Amount
        NumberField amountField = new NumberField("Amount");
        amountField.setRequired(true);
        amountField.setWidthFull();

        incomeDate = new DatePicker("Income Date");
        incomeDate.setRequired(true);
        incomeDate.setWidthFull();

        //date field


        TextField remarksField = new TextField("Enter Remarks");
        remarksField.setPlaceholder("Type here...");
        remarksField.setWidthFull();

        // Buttons

        Button saveBtn = new Button("Save",
                VaadinIcon.CHECK.create());

        setBtnComponentStyle(saveBtn);

        Button resetBtn = new Button("Reset",
                VaadinIcon.REFRESH.create());
        setBtnComponentStyle(resetBtn);

        Button cancelBtn = new Button("Cancel",
                VaadinIcon.CLOSE.create());
        setBtnComponentStyle(cancelBtn);




        saveBtn.addClickListener(event -> {
            saveBtn.setEnabled(false);

            if (CommonUtil.isNullOrEmpty(category)) {

                errorNotification("required field missing");
                saveBtn.setEnabled(true);

            }
            else if (amountField.getValue()==null || CommonUtil.isValidDouble(amountField.getValue()) ) {

                errorNotification("Amount must be greater than 0");
                saveBtn.setEnabled(true);

            }else {
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                AppIncomeMainViewModel incomeMainViewModel = new AppIncomeMainViewModel();

                incomeMainViewModel.setCategory(category); // pass from image click
                incomeMainViewModel.setRemarks(remarksField.getValue());
                incomeMainViewModel.setAmount(amountField.getValue());
                incomeMainViewModel.setUsername(user.getUsername());
                incomeMainViewModel.setIncomeDate(incomeDate.getValue().atTime(LocalTime.now()));

                incomeService.save(incomeMainViewModel);
                SuccessNotification();
                saveBtn.setEnabled(true);

            }
            saveBtn.setEnabled(true);
            close();
        });

        resetBtn.addClickListener(event -> {

            remarksField.clear();
            amountField.clear();
            saveBtn.setEnabled(true);
        });

        cancelBtn.addClickListener(event -> {

            close();

        });



        HorizontalLayout buttonLayout =
                new HorizontalLayout(
                        saveBtn,
                        resetBtn,
                        cancelBtn);

        buttonLayout.setSpacing(true);
        buttonLayout.setPadding(true);
        buttonLayout.getStyle().set("margin-top", "20px");

        buttonLayout.setWidthFull();
        buttonLayout.setJustifyContentMode(
                FlexComponent.JustifyContentMode.CENTER);

        add(
                remarksField,
                amountField,
                incomeDate,
                buttonLayout
        );
    }

   public void  SuccessNotification(){

       Notification notification = new Notification();

       notification.setPosition(Notification.Position.TOP_CENTER);

       Span message = new Span("✅ Income saved successfully!");

       notification.add(message);

       notification.setDuration(3000);

       notification.open();
   }

    public void errorNotification(String errorMessage) {

        Notification notification = new Notification();

        notification.setPosition(Notification.Position.TOP_CENTER);

        Span message = new Span("❌ " + errorMessage);

        notification.add(message);

        notification.setDuration(3000);

        notification.open();
    }

    public void setBtnComponentStyle(Button btnCommon){
        btnCommon.getStyle().set("border-radius", "8px"); // square with rounded corners
        btnCommon.getStyle().set("box-shadow", "0 4px 10px rgba(0,0,0,0.2)");
        btnCommon.getStyle().set("background-color", "#FFD700"); // golden color
        btnCommon.getStyle().set("color", "black");
//        btnCommon.getStyle().set("flex-direction", "column"); // icon above text
//        btnCommon.getStyle().set("align-items", "center");
//        btnCommon.getStyle().set("justify-content", "center");
    }
}
