package com.example.demo.financeview;

import com.example.demo.AppExpenseMainViewImpl;
import com.example.demo.AppUser.AppUserModel;
import com.example.demo.AppUser.AppUserNotesModel;
import com.example.demo.AppUser.LoginViewImpl;
import com.example.demo.AppUser.NotesService;
import com.example.demo.CommonUtil;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.VaadinSession;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Route("notesview")
@PageTitle("Notes")
public class NotesRecordsView extends VerticalLayout {

    private NotesService noteservice;

    @Autowired
    CommonUtil commonUtil;

    private TextField searchField;
    Button backButton = new Button("Back", VaadinIcon.ARROW_LEFT.create());
    private Grid<AppUserNotesModel> grid;

    @Value("${app.base-url}")
    private String baseUrl;

    @Value("${app.upload-dir}")
    private String uploadDir;

    AppUserModel user = VaadinSession.getCurrent().getAttribute(AppUserModel.class);

    @Autowired
    public NotesRecordsView(NotesService noteservice) {

        this.noteservice = noteservice;

        if(user==null){
            UI.getCurrent().navigate(LoginViewImpl.class);
            return;
        }

        setSizeFull();
        setPadding(true);

        createUI();

        loadAllRecords();
    }

    private void createUI() {

//        H3 header = new H3("🏠 Records");
//        header.getStyle()
//                .set("background-color", "#4a4a4a")
//                .set("color", "white")
//                .set("padding", "10px")
//                .set("text-align", "center")
//                .set("width", "100%");

        Span header = new Span("🏠 Notes");
        header.getStyle()
                .set("font-size", "1.5rem !important")   // force override
                .set("font-weight", "600")
                .set("background", "linear-gradient(to right, #2a5298, #1e3c72)")
                .set("color", "white")
                .set("padding", "0.5rem");
        header.setWidthFull();             // stretch across screen

        HorizontalLayout headerLayout = new HorizontalLayout(header);
        headerLayout.setWidthFull();
        headerLayout.setAlignItems(Alignment.CENTER);
        headerLayout.setJustifyContentMode(JustifyContentMode.BETWEEN);
        headerLayout.expand(header);

        Button resetBtn = new Button("", VaadinIcon.REFRESH.create());

        searchField = new TextField();
        searchField.setPlaceholder("Search...");
        searchField.setClearButtonVisible(true);


        HorizontalLayout topLayout3 =
                new HorizontalLayout();

        HorizontalLayout topLayout4 =
                new HorizontalLayout(
                        searchField,resetBtn);

        //topLayout3.setWidthFull();


        topLayout3.setWidthFull();
        topLayout3.setAlignItems(Alignment.END);
        topLayout4.setWidthFull();
        topLayout4.setAlignItems(Alignment.END);


        searchField.setWidthFull();

        VerticalLayout filterLayout = new VerticalLayout(topLayout3,topLayout4);





        // generate pdf into outputStream

        //Button downloadPdf = new Button(VaadinIcon.DOWNLOAD.create());

//        downloadPdf.addClickListener(e -> {
//
//            try {
//
//                String fileName = System.getProperty("user.home")
//                        + "/Downloads/IncomeRecords.pdf";
//
//                Document document = new Document();
//
//                PdfWriter.getInstance(document, new FileOutputStream(fileName));
//
//                document.open();
//                document.add(new Paragraph("Income Records"));
//                document.add(new Paragraph(" "));
//
//                PdfPTable table = new PdfPTable(5);
//
//                table.addCell("ID");
//                table.addCell("Date");
//                table.addCell("Category");
//                table.addCell("Income Name");
//                table.addCell("Amount");
//
//                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
//                for (AppIncomeMainViewModel item : grid.getListDataView().getItems().toList()) {
//
//                    table.addCell(String.valueOf(item.getId()));
//                    table.addCell(item.getIncomeDate().format(formatter));
//                    table.addCell(item.getCategory());
//                    table.addCell(item.getRemarks());
//                    table.addCell(String.valueOf(item.getAmount()));
//                }
//
//                document.add(table);
//                document.close();
//
//                Notification.show("PDF Created");
//
//            } catch (Exception ex) {
//                ex.printStackTrace();
//                Notification.show(ex.getMessage());
//            }
//
//        });


//        HorizontalLayout downloadLayout = new HorizontalLayout(downloadPdf);
//        downloadLayout.setWidthFull();

        HorizontalLayout footer =
                new HorizontalLayout(backButton);

        footer.setWidthFull();
        footer.setAlignItems(Alignment.CENTER);

        grid = new Grid<>(AppUserNotesModel.class, false);

        grid.addColumn(AppUserNotesModel::getNoteId)
                .setHeader("ID")
                .setAutoWidth(true);

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm:ss");

        grid.addColumn(Notes ->
                        Notes.getCreateddate().format(formatter))
                .setHeader("Date")
                .setAutoWidth(false)
                .setSortable(true)
                .setWidth("210px")
                .setFlexGrow(0);

        grid.addColumn(AppUserNotesModel::getFilename)
                .setHeader("File Name")
                .setAutoWidth(true);

        grid.addColumn(AppUserNotesModel::getNotename)
                .setHeader("Note Name")
                .setAutoWidth(true);


        // Action column
        grid.addComponentColumn(item -> {
            Button deleteBtn = new Button("", VaadinIcon.TRASH.create());
            deleteBtn.addClickListener(e -> {
                // 👉 Your delete logic here
                noteservice.deleteNotes(item.getNoteId());
                grid.getListDataView().removeItem(item);

            });

                    Button viewBtn = new Button("", VaadinIcon.EYE.create());

                    StreamResource resource = new StreamResource(
                            item.getFilename(),
                            () -> new ByteArrayInputStream(item.getContent())
                    );

                    Anchor viewLink = new Anchor(resource, "");
                    viewLink.setTarget("_blank");
                    viewLink.add(viewBtn);

                    HorizontalLayout actions =
                            new HorizontalLayout(viewLink, deleteBtn);
            actions.setWidthFull();
            return actions;
        }).setHeader("Ops")
                .setAutoWidth(true)
                .setFlexGrow(0);;;

        grid.setSizeFull();

        add(
                headerLayout,
                filterLayout,
                grid,footer);

        expand(grid);

        backButton.addClickListener(event -> UI.getCurrent().navigate(AppExpenseMainViewImpl.class));

        resetBtn.addClickListener(event -> {
            searchField.clear();
            loadAllRecords();
        });

        /**
         * Search Field
         *
         * Purpose : Global Search.
         *
         * Author : Karthick S
         *
         * Created : 03-Sep-2026 09:22 PM IST
         *
         * Version : 1.0
         */


        searchField.addValueChangeListener(event -> {

            String searchText = event.getValue();

            List<AppUserNotesModel> result =
                    noteservice.searchNotes(searchText);

            List<AppUserNotesModel> lstOfDetails = result.stream().filter(p -> p.getUsername().equals(user.getUsername())).collect(Collectors.toList());
//            List<AppIncomeMainViewModel> sortOrderList = lstOfDetails.stream()
//                    .sorted(Comparator.comparing(AppIncomeMainViewModel::getCreated_time)
//                            .reversed())
//                    .collect(Collectors.toList());
            grid.setItems(lstOfDetails);
        });



    }

    private void loadAllRecords() {


        List<AppUserNotesModel> lstOfDetails = noteservice.getAllNotes().stream().filter(p -> p.getUsername().equals(user.getUsername())).collect(Collectors.toList());
//        List<AppIncomeMainViewModel> sortOrderList = lstOfDetails.stream()
//                .sorted(Comparator.comparing(AppIncomeMainViewModel::getCreated_time)
//                        .reversed())
//                .collect(Collectors.toList());
        grid.setItems(
                lstOfDetails);

    }
}