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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalTime;

@Route("editincome")
public class AppIncomeEditMainDialogImpl extends Dialog {


    private IncomeService incomeService;

    private CommonUtil commonUtil;

    private String category;

    private AppIncomeMainViewModel incomeMainViewModel;

    private Runnable onSaveCallback;  // callback reference

    private DatePicker expenseDate;

    public AppIncomeEditMainDialogImpl(String category, IncomeService incomeService, CommonUtil commonUtil, AppIncomeMainViewModel incomeMainViewModel, Runnable onSaveCallback) {

        this.category = category;
        this.incomeService = incomeService;
        this.commonUtil = commonUtil;
        this.incomeMainViewModel = incomeMainViewModel;
        this.onSaveCallback = onSaveCallback;
        // ... rest of your constructor logic

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

        expenseDate = new DatePicker("Income Date");
        expenseDate.setRequired(true);
        expenseDate.setWidthFull();

        //date field


        TextField remarksField = new TextField("Enter Remarks");
        remarksField.setPlaceholder("Type here...");
        remarksField.setWidthFull();

        // Buttons

        Button saveBtn = new Button("Save",
                VaadinIcon.CHECK.create());

        Button resetBtn = new Button("Reset",
                VaadinIcon.REFRESH.create());

        Button cancelBtn = new Button("Cancel",
                VaadinIcon.CLOSE.create());

        setBtnComponentStyle(saveBtn);
        setBtnComponentStyle(resetBtn);
        setBtnComponentStyle(cancelBtn);

        //set Obj value
        remarksField.setValue(incomeMainViewModel.getRemarks());
        amountField.setValue(incomeMainViewModel.getAmount());
        expenseDate.setValue(incomeMainViewModel.getIncomeDate().toLocalDate());


        saveBtn.addClickListener(event -> {
            saveBtn.setEnabled(false);
            if (CommonUtil.isNullOrEmpty(category)) {

                errorNotification("required field missing");
                saveBtn.setEnabled(false);
            }
            else if (amountField.getValue()==null || CommonUtil.isValidDouble(amountField.getValue()) ) {

                errorNotification("Amount must be greater than 0");
                saveBtn.setEnabled(false);

            }else {
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();

                //AppExpenseMainViewModel expenseMainViewModel = new AppExpenseMainViewModel();

                incomeMainViewModel.setCategory(incomeMainViewModel.getCategory()); // pass from image click
                incomeMainViewModel.setRemarks(remarksField.getValue());
                incomeMainViewModel.setAmount(amountField.getValue());
                incomeMainViewModel.setUsername(user.getUsername());
                incomeMainViewModel.setIncomeDate(expenseDate.getValue().atTime(LocalTime.now()));

                incomeService.save(incomeMainViewModel);

                // 👉 Call the callback after save
                if (onSaveCallback != null) {
                    onSaveCallback.run();
                }

                SuccessNotification();
                saveBtn.setEnabled(false);

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

        buttonLayout.setWidthFull();
        buttonLayout.setJustifyContentMode(
                FlexComponent.JustifyContentMode.CENTER);

        add(
                remarksField,
                amountField,
                expenseDate,
                buttonLayout
        );
    }

   public void  SuccessNotification(){

       Notification notification = new Notification();

       notification.setPosition(Notification.Position.TOP_CENTER);

       Span message = new Span("✅ Expense saved successfully!");

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
        btnCommon.getStyle().set("flex-direction", "column"); // icon above text
        btnCommon.getStyle().set("align-items", "center");
        btnCommon.getStyle().set("justify-content", "center");
    }
}
