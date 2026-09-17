package com.example.demo;

import com.example.demo.AppUser.AppUserModel;
import com.example.demo.AppUser.LoginViewImpl;
import com.vaadin.flow.component.ClientCallable;
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
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.client.RestTemplate;

import java.time.LocalTime;

@Route("editxpense")
public class AppExpenseEditMainDialogImpl extends Dialog {


    private ExpenseService expenseService;

    private CommonUtil commonUtil;

    private String category;

    private  AppExpenseMainViewModel expenseMainViewModel;

    private Runnable onSaveCallback;  // callback reference

    private DatePicker expenseDate;
    private String currentLocation;
    AppUserModel user = VaadinSession.getCurrent().getAttribute(AppUserModel.class);
    NumberField amountField = new NumberField();
    TextField remarksField = new TextField();
    public AppExpenseEditMainDialogImpl(String category,ExpenseService expenseService,CommonUtil commonUtil,AppExpenseMainViewModel expenseMainViewModel,Runnable onSaveCallback) {

        this.category = category;
        this.expenseService = expenseService;
        this.commonUtil = commonUtil;
        this.expenseMainViewModel = expenseMainViewModel;
        this.onSaveCallback = onSaveCallback;
        // ... rest of your constructor logic



        if(user==null){
            UI.getCurrent().navigate(LoginViewImpl.class);
            return;
        }

        setWidth("400px");
//        setPadding(true);
//        setSpacing(true);


        // Amount
        amountField = new NumberField("Amount");
        amountField.setRequired(true);
        amountField.setWidthFull();

        expenseDate = new DatePicker("Expense Date");
        expenseDate.setRequired(true);
        expenseDate.setWidthFull();

        //date field


        remarksField = new TextField("Enter Remarks");
        remarksField.setPlaceholder("Type here...");
        remarksField.setWidthFull();

        // Buttons

        Button saveBtn = new Button("Save",
                VaadinIcon.CHECK.create());

        Button resetBtn = new Button("Reset",
                VaadinIcon.REFRESH.create());

        Button cancelBtn = new Button("Cancel",
                VaadinIcon.CLOSE.create());



        //set Obj value
        remarksField.setValue(expenseMainViewModel.getExpenseName());
        amountField.setValue(expenseMainViewModel.getAmount());
        expenseDate.setValue(expenseMainViewModel.getExpenseDate().toLocalDate());


        saveBtn.addClickListener(event -> {

            saveBtn.setEnabled(false);

            if (CommonUtil.isNullOrEmpty(category)) {

                errorNotification("required field missing");

            }
            else if (amountField.getValue()==null || CommonUtil.isValidDouble(amountField.getValue()) ) {

                errorNotification("Amount must be greater than 0");
            }else {


                //AppExpenseMainViewModel expenseMainViewModel = new AppExpenseMainViewModel();

                // 👉 Trigger point: Save button click
                UI.getCurrent().getPage().executeJs(
                        "navigator.geolocation.getCurrentPosition("
                                + "pos => { console.log('Got coords', pos); "
                                + "          $0.$server.saveExpenseWithLocation(pos.coords.latitude, pos.coords.longitude); }, "
                                + "err => { console.error(err); "
                                + "          $0.$server.handleLocationError(err.message); });",
                        this // bind to this dialog instance
                );






            }
            saveBtn.setEnabled(true);

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

    @ClientCallable
    public void saveExpenseWithLocation(double lat, double lon) {

        String location = getAddress(lat, lon); // reverse‑geocode
        this.currentLocation = location;
        Notification.show("Location captured: " + location);

        // Save expense with location
        expenseMainViewModel.setCategory(expenseMainViewModel.getCategory()); // pass from image click
        expenseMainViewModel.setExpenseName(remarksField.getValue());
        expenseMainViewModel.setAmount(amountField.getValue());
        expenseMainViewModel.setUsername(user.getUsername());
        expenseMainViewModel.setExpenseDate(expenseDate.getValue().atTime(LocalTime.now()));
        expenseMainViewModel.setLocation(location);

        expenseService.save(expenseMainViewModel);

        // 👉 Call the callback after save
        if (onSaveCallback != null) {
            onSaveCallback.run();
        }

        SuccessNotification();
        close();
    }

    @ClientCallable
    public void handleLocationError(String errorMessage) {
        Notification.show("Location error: " + errorMessage);
    }

    public String getAddress(double lat, double lon) {
        String apiKey = "YOUR_GOOGLE_API_KEY";
        String url = "https://maps.googleapis.com/maps/api/geocode/json?latlng="
                + lat + "," + lon + "&key=" + apiKey;

        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(url, String.class);

        JSONObject json = new JSONObject(response);
        JSONArray results = json.getJSONArray("results");
        if (results.length() > 0) {
            return results.getJSONObject(0).getString("formatted_address");
        }
        return lat + "," + lon; // fallback
    }
}
