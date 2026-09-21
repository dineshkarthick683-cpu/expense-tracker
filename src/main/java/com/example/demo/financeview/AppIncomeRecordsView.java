package com.example.demo.financeview;

import com.example.demo.AppExpenseEditMainDialogImpl;
import com.example.demo.AppExpenseMainViewModel;
import com.example.demo.AppUser.AppUserModel;
import com.example.demo.AppUser.LoginViewImpl;
import com.example.demo.CommonUtil;
import com.example.demo.financeview.AppIncomeMainViewModel;
import com.example.demo.financeview.IncomeService;
import com.vaadin.flow.component.html.Anchor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import com.vaadin.flow.server.VaadinSession;
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
import com.vaadin.flow.component.html.Span;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

@Route("incomerecords")
@PageTitle("Records")
public class AppIncomeRecordsView extends VerticalLayout {

    private final IncomeService incomeService;

    @Autowired
    CommonUtil commonUtil;

    private ComboBox<String> categoryFilter;
    private DatePicker startDate;
    private DatePicker endDate;
    private TextField searchField;
    Button backButton = new Button("Back", VaadinIcon.ARROW_LEFT.create());
    private Grid<AppIncomeMainViewModel> grid;
    Span totalLabel = new Span();

    @Value("${app.base-url}")
    private String baseUrl;

    @Value("${app.upload-dir}")
    private String uploadDir;

    AppUserModel user = VaadinSession.getCurrent().getAttribute(AppUserModel.class);

    @Autowired
    public AppIncomeRecordsView(IncomeService incomeService) {

        this.incomeService = incomeService;

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

        Span header = new Span("🏠 Records");
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

        categoryFilter = new ComboBox<>("Category");
        //categoryFilter.setWidth("180px");
        categoryFilter.setItems(
                "Salary",
                "Investment",
                "Gold",
                "Loan",
                "Returns",
                "Common");

        startDate = new DatePicker("Start Date");
        endDate = new DatePicker("End Date");

        searchField = new TextField("");
        searchField.setPlaceholder(
                "Income/User/Category");

        searchField.setPrefixComponent(
                VaadinIcon.SEARCH.create());

        Button searchBtn = new Button("", VaadinIcon.SEARCH.create());
        Button resetBtn = new Button("", VaadinIcon.REFRESH.create());

        searchField.setPlaceholder("Search...");
        searchField.setClearButtonVisible(true);

        HorizontalLayout topLayout1 =
                new HorizontalLayout(
                        startDate);
        //topLayout1.setWidthFull();

        HorizontalLayout topLayout2 =
                new HorizontalLayout(
                        endDate);
        //topLayout2.setWidthFull();

        HorizontalLayout topLayout3 =
                new HorizontalLayout(
                        categoryFilter,
                        searchBtn);

        HorizontalLayout topLayout4 =
                new HorizontalLayout(
                        searchField,resetBtn);

        //topLayout3.setWidthFull();

        topLayout1.setWidthFull();
        topLayout2.setWidthFull();
        topLayout3.setWidthFull();
        topLayout3.setAlignItems(FlexComponent.Alignment.END);
        topLayout4.setWidthFull();
        topLayout4.setAlignItems(FlexComponent.Alignment.END);

        categoryFilter.setWidthFull();
        startDate.setWidthFull();
        endDate.setWidthFull();
        searchField.setWidthFull();

        VerticalLayout filterLayout = new VerticalLayout(topLayout1,topLayout2,topLayout3,topLayout4);

        totalLabel.getStyle()
                .set("font-size", "15px")
                .set("font-weight", "600")
                .set("color", "#2E7D32");


        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // generate pdf into outputStream

        StreamResource resource = new StreamResource(
                "Expense_Report.pdf",
                () -> new ByteArrayInputStream(outputStream.toByteArray()));
        Anchor downloadPdf = new Anchor(resource, "Download PDF");
        downloadPdf.getElement().setAttribute("download", true);

        HorizontalLayout downloadLayout = new HorizontalLayout(downloadPdf);
        downloadLayout.setWidthFull();

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
                new HorizontalLayout(backButton, downloadLayout, totalLabel);

        footer.setWidthFull();
        footer.setAlignItems(Alignment.CENTER);

        footer.expand(downloadLayout);

        grid = new Grid<>(AppIncomeMainViewModel.class, false);

        grid.addColumn(AppIncomeMainViewModel::getId)
                .setHeader("ID")
                .setAutoWidth(true);

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

        grid.addColumn(Income ->
                        Income.getIncomeDate().format(formatter))
                .setHeader("Date")
                .setAutoWidth(false)
                .setWidth("180px")
                .setFlexGrow(0);

        grid.addColumn(AppIncomeMainViewModel::getCategory)
                .setHeader("Category")
                .setAutoWidth(true);

        grid.addColumn(AppIncomeMainViewModel::getRemarks)
                .setHeader("Inc-Name")
                .setAutoWidth(true);

        grid.addColumn(AppIncomeMainViewModel::getAmount)
                .setHeader("₹")
                .setAutoWidth(true);

        grid.addColumn(AppIncomeMainViewModel::getUsername)
                .setHeader("Me")
                .setAutoWidth(true);

        // Action column
        grid.addComponentColumn(item -> {
            Button editBtn = new Button("", VaadinIcon.EDIT.create());
            editBtn.addClickListener(e -> {
                // 👉 Your edit logic here
                editIncome(item);

            });

            Button deleteBtn = new Button("", VaadinIcon.TRASH.create());
            deleteBtn.addClickListener(e -> {
                // 👉 Your delete logic here
                incomeService.deleteIncome(item.getId());
                grid.getListDataView().removeItem(item);
                getSumOfAmountBasedOnGrid();
            });

            Button shareBtn = new Button("", VaadinIcon.SHARE.create());
            shareBtn.addClickListener(e -> {
                // 👉 Your share logic here
                shareToWhatsApp(item);
                Notification.show("Shared record: " + item.getRemarks());
            });

            HorizontalLayout actions = new HorizontalLayout(editBtn, deleteBtn, shareBtn);
            actions.setWidthFull();
            return actions;
        }).setHeader("Ops")
                .setAutoWidth(true)
                .setFlexGrow(0);;;

        grid.setSizeFull();



        getSumOfAmountBasedOnGrid();

        add(
                headerLayout,
                filterLayout,
                grid,footer);

        expand(grid);


        searchBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        searchBtn.addClickListener(event -> {
                    searchRecords();
        });

        searchBtn.addClickListener(event -> {
            searchRecords();
        });

        backButton.addClickListener(event -> UI.getCurrent().navigate(AppIncomeMainViewImpl.class));

        resetBtn.addClickListener(event -> {

            categoryFilter.clear();
            startDate.clear();
            endDate.clear();
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

            List<AppIncomeMainViewModel> result =
                    incomeService.getAllIncome()
                            .stream()
                            .filter(e ->
                                    searchText == null ||
                                            searchText.trim().isEmpty() ||

                                            e.getRemarks()
                                                    .toLowerCase()
                                                    .contains(searchText.toLowerCase())

                                            ||

                                            e.getCategory()
                                                    .toLowerCase()
                                                    .contains(searchText.toLowerCase())

                                            ||

                                            e.getUsername()
                                                    .toLowerCase()
                                                    .contains(searchText.toLowerCase()))
                            .toList();

            grid.setItems(result);
            getSumOfAmountBasedOnGrid();
        });



    }

    private void loadAllRecords() {

        grid.setItems(
                incomeService.getAllIncome());
        getSumOfAmountBasedOnGrid();

    }

    private void searchRecords() {

        String category =
                categoryFilter.getValue();

        LocalDate fromDate =
                startDate.getValue();

        LocalDate toDate =
                endDate.getValue();



        LocalDateTime fromDateTime =
                fromDate == null ? null : fromDate.atStartOfDay();
        LocalDateTime toDateTime =
                toDate == null ? null : toDate.atTime(23, 59, 59);

        List<AppIncomeMainViewModel> result =
                incomeService.searchIncome(
                        category,
                        fromDateTime,
                        toDateTime);

        grid.setItems(result);
        getSumOfAmountBasedOnGrid();

    }

    public void getSumOfAmountBasedOnGrid() {

        // --- Calculate total from grid items ---
        double totalAmount = grid.getListDataView().getItems()
                .mapToDouble(AppIncomeMainViewModel::getAmount)
                .sum();

        totalLabel.setText("💰 Total: ₹ " + totalAmount);

    }

    private void editIncome(AppIncomeMainViewModel item) {

        AppIncomeEditMainDialogImpl dialog =
                new AppIncomeEditMainDialogImpl(item.getCategory(), incomeService, commonUtil, item,
                        () -> loadAllRecords()); // callback

        dialog.open();
    }

    private void shareToWhatsApp(AppIncomeMainViewModel item) {
        try {
            // Sanitize filename (replace spaces with underscores)
            String safeName = item.getRemarks().replaceAll("\\s+", "_");
            String filename = "record-" + safeName + ".png";

            // Ensure uploads folder exists
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            System.out.println("Uploads folder: " + dir.getAbsolutePath());

            // Generate image file reference
            File imageFile = new File(dir, filename);

            // 🔥 Delete all old files inside uploads folder
            File[] oldFiles = dir.listFiles();
            if (oldFiles != null) {
                for (File f : oldFiles) {
                    if (f.isFile()) {
                        boolean deleted = f.delete();
                        System.out.println("Deleted " + f.getName() + ": " + deleted);
                    }
                }
            }

            // Create new image
            BufferedImage img = new BufferedImage(400, 200, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = img.createGraphics();
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, 400, 200);
            g.setColor(Color.BLACK);
            g.drawString("Remarks: " + item.getRemarks(), 20, 50);
            g.drawString("Amount: ₹" + item.getAmount(), 20, 80);
            g.dispose();

            // Write new image file
            ImageIO.write(img, "png", imageFile);
            System.out.println("New file saved: " + imageFile.getAbsolutePath());

            // Build public URL served by ImageController
            String imageUrl = baseUrl + "/uploads/" + filename;

            // WhatsApp link
            String phone = user.getMobileNumber() != null
                    ? "91" + user.getMobileNumber()
                    : "91++++";

            String url = "https://wa.me/" + phone + "?text=" +
                    URLEncoder.encode(imageUrl, StandardCharsets.UTF_8);

            // Open WhatsApp link
            UI.getCurrent().getPage().open(url, "_blank");

        } catch (IOException ex) {
            Notification.show("Error creating image: " + ex.getMessage());
        }
    }


}