package com.example.demo.financeview;

import com.example.demo.AppUser.AppUserModel;
import com.example.demo.AppUser.AppUserNotesModel;
import com.example.demo.AppUser.LoginViewImpl;
import com.example.demo.AppUser.NotesService;
import com.example.demo.CommonUtil;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Route("addnotes")
public class AddNotesMainDialogImpl extends Dialog {

    @Autowired
    private NotesService notesService;

    private String uploadedFileName;


    public AddNotesMainDialogImpl(NotesService notesService) {

        this.notesService = notesService;



        AppUserModel user = VaadinSession.getCurrent().getAttribute(AppUserModel.class);

        if(user==null){
            UI.getCurrent().navigate(LoginViewImpl.class);
            return;
        }

        setWidth("400px");
//        setPadding(true);
//        setSpacing(true);


        MemoryBuffer buffer = new MemoryBuffer();
        Upload upload = new Upload(buffer);
        upload.setUploadButton(
                new Button("Browse File")
        );
        add(upload);

        DatePicker expenseDate = new DatePicker("Notes Date");
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

        upload.addSucceededListener(event -> {
            uploadedFileName = event.getFileName();
            Notification.show("Selected File: " + uploadedFileName);
        });


        saveBtn.addClickListener(event -> {
            //byte[] fileBytes = buffer.getInputStream().readAllBytes();

            try {
                byte[] fileBytes = buffer.getInputStream().readAllBytes();

                if (remarksField.getValue() != null && fileBytes.length != 0) {


                    AppUserNotesModel addNotes = new AppUserNotesModel();
                    addNotes.setFilename(remarksField.getValue());
                    addNotes.setContent(fileBytes);
                    addNotes.setNotename(uploadedFileName);
                    addNotes.setCreateddate(LocalDateTime.now());
                    addNotes.setUsername(user.getUsername());
                    System.out.println("Before Save");
                    notesService.save(addNotes);
                    System.out.println("After Save");
                    SuccessNotification();
                    close();
                } else {
                    errorNotification("Mandatory Fields missing..");
                    return;
                }
            } catch (IOException e) {
                e.printStackTrace();
                errorNotification("Error reading file");
            }
        });

        resetBtn.addClickListener(event -> {

            remarksField.clear();
            //amountField.clear();
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

        VerticalLayout fieldsLayout =
                new VerticalLayout(
                        remarksField,
                        upload,
                        expenseDate);

        fieldsLayout.setSpacing(true);
        fieldsLayout.setPadding(true);
        fieldsLayout.getStyle().set("margin-top", "20px");

        fieldsLayout.setWidthFull();
        fieldsLayout.setJustifyContentMode(
                FlexComponent.JustifyContentMode.CENTER);

        add(
                fieldsLayout,
                buttonLayout
        );
    }

   public void  SuccessNotification(){

       Notification notification = new Notification();

       notification.setPosition(Notification.Position.TOP_CENTER);

       Span message = new Span("✅ Notes saved successfully!");

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
