package com.example.demo;
import com.example.demo.AppExpenseMainViewModel;
import com.example.demo.AppUser.AppUserModel;
import com.example.demo.AppUser.LoginViewImpl;
import com.example.demo.ExpenseService;
import com.example.demo.financeview.AppIncomeAddMainDialogImpl;
import com.example.demo.financeview.AppIncomeMainViewModel;
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
import com.vaadin.flow.component.html.Anchor;
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
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Route("records")
@PageTitle("Records")
public class AppExpenseRecordsView extends VerticalLayout {

    private final ExpenseService expenseService;

    @Autowired
    CommonUtil commonUtil;

    private ComboBox<String> categoryFilter;
    private DatePicker startDate;
    private DatePicker endDate;
    private TextField searchField;
    Button backButton = new Button("Back", VaadinIcon.ARROW_LEFT.create());
    private Grid<AppExpenseMainViewModel> grid;
    Span totalLabel = new Span();

    @Value("${app.base-url}")
    private String baseUrl;

    @Value("${app.upload-dir}")
    private String uploadDir;

    AppUserModel user = VaadinSession.getCurrent().getAttribute(AppUserModel.class);

    @Autowired
    public AppExpenseRecordsView(ExpenseService expenseService) {

        this.expenseService = expenseService;


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
//
//
//        header.getStyle()
//                .set("background-color", "#4a4a4a")
//                .set("color", "white")
//                .set("padding", "10px")
//                .set("text-align", "center");
//                //.set("width", "100%");
//        header.setWidthFull();

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
                "Gift",
                "Shopping",
                "Home",
                "Vegetables",
                "Pharmacy",
                "Food");

        startDate = new DatePicker("Start Date");
        endDate = new DatePicker("End Date");

        searchField = new TextField("");
        searchField.setPlaceholder(
                "Expense/User/Category");

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
        //resetBtn.setWidthFull();

        //rightLayout.setJustifyContentMode(JustifyContentMode.END);

//        categoryFilter.setWidth("180px");
//        startDate.setWidth("180px");
//        endDate.setWidth("180px");
//        searchField.setWidth("250px");



        VerticalLayout filterLayout = new VerticalLayout(topLayout1,topLayout2,topLayout3,topLayout4);
        //filterLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        //filterLayout.setMaxWidth("400px");
        //filterLayout.setWidthFull();
        //filterLayout.setAlignItems(Alignment.END);
        //filterLayout.expand(leftLayout);


//        filterLayout.setAlignItems(
//                Alignment.END);

        totalLabel.getStyle()
                .set("font-size", "15px")
                .set("font-weight", "600")
                .set("color", "#2E7D32");

//        HorizontalLayout rightSide = new HorizontalLayout();
//        rightSide.setWidth("100px"); // optional fixed width
//        rightSide.setJustifyContentMode(JustifyContentMode.END);



//        Button downloadPdf = new Button(VaadinIcon.DOWNLOAD.create());
//
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
//                for (AppExpenseMainViewModel item : grid.getListDataView().getItems().toList()) {
//
//                    table.addCell(String.valueOf(item.getId()));
//                    table.addCell(item.getExpenseDate().format(formatter));
//                    table.addCell(item.getCategory());
//                    table.addCell(item.getExpenseName());
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
//
//
//        HorizontalLayout downloadLayout = new HorizontalLayout(downloadPdf);
//        downloadLayout.setWidthFull();

        // generate pdf into outputStream




        Button pdfButton = new Button("Export PDF");

        HorizontalLayout downloadLayout =
                new HorizontalLayout();

        downloadLayout.add(pdfButton);

        pdfButton.addClickListener(event -> {

            List<AppExpenseMainViewModel> gridListData =
                    grid.getListDataView()
                            .getItems()
                            .toList();

            Anchor downloadPdf =
                    createPdfDownload(gridListData);

            downloadLayout.removeAll();

            downloadLayout.add(downloadPdf);

        });

        add(downloadLayout);




        HorizontalLayout footer =
                new HorizontalLayout(backButton, downloadLayout, totalLabel);

        footer.setWidthFull();
        footer.setAlignItems(Alignment.CENTER);

        footer.expand(downloadLayout);




        grid = new Grid<>(AppExpenseMainViewModel.class, false);

        grid.addColumn(AppExpenseMainViewModel::getId)
                .setHeader("ID")
                .setAutoWidth(true);

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm:ss");

        grid.addColumn(expense ->
                        expense.getExpenseDate().format(formatter))
                .setHeader("Date")
                .setAutoWidth(false)
                .setWidth("180px")
                .setFlexGrow(0);

        grid.addColumn(AppExpenseMainViewModel::getCategory)
                .setHeader("Category")
                .setAutoWidth(true);

        grid.addColumn(AppExpenseMainViewModel::getExpenseName)
                .setHeader("Ex-Name")
                .setAutoWidth(true);

        grid.addColumn(AppExpenseMainViewModel::getAmount)
                .setHeader("₹")
                .setAutoWidth(true);

        grid.addColumn(AppExpenseMainViewModel::getUsername)
                .setHeader("Me")
                .setAutoWidth(true);

        grid.addComponentColumn(expense -> {
            String latLon = expense.getLocation(); // e.g. "12.935518532896975,80.24253790683441"
            String mapsUrl = "https://www.google.com/maps?q=" + latLon;
            Anchor link = new Anchor(mapsUrl, "Open in Maps");
            link.setTarget("_blank"); // open in new tab
            return link;
        }).setHeader("Location")
                .setAutoWidth(true)
                .setFlexGrow(0);

        // Action column
        grid.addComponentColumn(item -> {
            Button editBtn = new Button("", VaadinIcon.EDIT.create());
            editBtn.addClickListener(e -> {
                // 👉 Your edit logic here
                editExpense(item);

            });

            Button deleteBtn = new Button("", VaadinIcon.TRASH.create());
            deleteBtn.addClickListener(e -> {
                // 👉 Your delete logic here
                expenseService.deleteExpense(item.getId());
                grid.getListDataView().removeItem(item);
                getSumOfAmountBasedOnGrid();
            });

            Button shareBtn = new Button("", VaadinIcon.SHARE.create());
            shareBtn.addClickListener(e -> {
                // 👉 Your share logic here
                shareToWhatsApp(item);
                Notification.show("Shared record: " + item.getExpenseName());
            });

            HorizontalLayout actions = new HorizontalLayout(editBtn, deleteBtn, shareBtn);
                    actions.setWidthFull();
            return actions;
        }).setHeader("Actions")
                .setAutoWidth(true)
                .setFlexGrow(0);;


        grid.setWidthFull();

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

        backButton.addClickListener(event -> UI.getCurrent().navigate(AppExpenseMainViewImpl.class));

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

            List<AppExpenseMainViewModel> result =
                    expenseService.getAllExpenses()
                            .stream()
                            .filter(e ->
                                    searchText == null ||
                                            searchText.trim().isEmpty() ||

                                            e.getExpenseName()
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

            List<AppExpenseMainViewModel> lstOfDetails = result.stream().filter(p -> p.getUsername().equals(user.getUsername())).collect(Collectors.toList());
            grid.setItems(lstOfDetails);


            //result.stream().filter(p-> p.getExpenseName().startsWith("Salary")).forEach(System.out::println);

            //Predicate<Integer> predicate = n -> n%2 ==0;

//            String chartAtValue = "";
//            Function<String,Integer> length = str -> str.length();
//            length.apply(chartAtValue);

            //List<Integer> number = Arrays.asList(1, 2, 3, 4, 5, 6);

            //number.stream().filter(p-> p % 2==0).map(n->n*n).forEach(System.out::println);


            //HashSet<Integer> uniqe = new HashSet<Integer>();

            //number.stream().distinct().forEach(System.out::println);

            //remove duplicate;
            //umber.stream().filter(p-> !uniqe.add(p));

            //Map<Character,Long> result =
            //String test = "java developer";

            //Map<Character, Long> resultSet = test.chars().mapToObj(c -> (char) c).collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

            //new StringBuilder(test).reverse().toString();

//            LinkedHashMap<Character, Long> resultSet = test.chars().mapToObj(c -> (char) c).collect(Collectors.groupingBy(Function.identity(), LinkedHashMap::new, Collectors.counting()));
//            Character finalresult = resultSet.entrySet().stream().filter(p -> p.getValue() == 1).map(Map.Entry::getKey).findFirst().orElse(null);


             //List<Integer> listNumber = Arrays.asList(1,3,4,5);

             //listNumber.stream().distinct().sorted(Comparator.reverseOrder()).skip(1).findFirst().get();

            // Map<Boolean,List<Integer>> lstElements = listNumber.stream().collect(Collectors.partitioningBy(n-> n%2==0));

            //lstElements.entrySet().stream().filter(p -> p.getKey()).forEach(System.out::println);

            //boolean ispolindrome = test.equals(new StringBuilder(test).reverse().toString());


            //find seq missing number
            //int[] arr = {1,2,3,5};

            //int  n = 5;

            //int expected = n * (n+1)/2;

            //Arrays.stream(arr).sum();

            //List<Integer> lstList = Arrays.stream(arr).boxed().collect(Collectors.toList());

            //IntStream.rangeClosed(1,n).filter(p-> !lstList.contains(p));


           // String words = "java spring java sql spring";

           // Arrays.stream(words.split("\\ ")).collect(Collectors.groupingBy(Function.identity(),Collectors.counting()));

            //find large number
//            int[] intArray = {2, 9, 29, 23, 29};
//
//            int largeNumber =  intArray[0];
//
//            for(int i=0;i<intArray.length;i++){
//
//                if(intArray[i]>largeNumber){
//
//                    largeNumber =  intArray[i];
//                }
//            }



            // find second largest number

//            int fisrtLargeNumber = Integer.MIN_VALUE;
//            int secondLargeNumber = Integer.MIN_VALUE;
//
//            int n = 29;
//
//            int expSum = n * (n+1)/2;
//
//            int actualSum = 0;
//
//            for(int num: intArray){
//
//                if(num > fisrtLargeNumber){  // 2 > -0099 , 9 > 2 , 29 > 9 , 23  > 29
//
//                    secondLargeNumber = fisrtLargeNumber; // -0099 = -0099 , 2 , 9
//
//                    fisrtLargeNumber = num; // 2 , 9 , 29
//
//                }else if(num > secondLargeNumber && num!=fisrtLargeNumber){
//                    secondLargeNumber = num;
//                }
//
//            }

//            int[] arr = {1, 2, 3, 2, 1, 2, 4};
//
//            for (int i = 0; i < arr.length; i++) {
//                int count = 1;
//
//                for (int j = i + 1; j < arr.length; j++) {
//                    if (arr[i] == arr[j]) {
//                        count++;
//                    }
//                }
//
//                boolean alreadyCounted = false;
//
//                for (int k = 0; k < i; k++) {
//                    if (arr[k] == arr[i]) {
//                        alreadyCounted = true;
//                        break;
//                    }
//                }
//
//                if (!alreadyCounted) {
//                    System.out.println(arr[i] + " occurs " + count + " times");
//                }
//            }


            getSumOfAmountBasedOnGrid();
        });




        getSumOfAmountBasedOnGrid();

    }

    private void editExpense(AppExpenseMainViewModel item) {

        AppExpenseEditMainDialogImpl dialog =
                new AppExpenseEditMainDialogImpl(item.getCategory(), expenseService, commonUtil, item,
                        () -> loadAllRecords()); // callback

        dialog.open();
    }

    private void loadAllRecords() {

        List<AppExpenseMainViewModel> lstAllExpense = expenseService.getAllExpenses();
        List<AppExpenseMainViewModel> lstOfDetails = lstAllExpense.stream().filter(p -> p.getUsername().equals(user.getUsername())).collect(Collectors.toList());

        grid.setItems(lstOfDetails);
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

        List<AppExpenseMainViewModel> result =
                expenseService.searchExpenses(
                        category,
                        fromDateTime,
                        toDateTime);



        List<AppExpenseMainViewModel> lstOfDetails = result.stream().filter(p -> p.getUsername().equals(user.getUsername())).collect(Collectors.toList());

        grid.setItems(lstOfDetails);
        getSumOfAmountBasedOnGrid();
    }

    public void getSumOfAmountBasedOnGrid() {

        // --- Calculate total from grid items ---
        double totalAmount = grid.getListDataView().getItems()
                .mapToDouble(AppExpenseMainViewModel::getAmount)
                .sum();

        totalLabel.setText("💰 Total: ₹ " + totalAmount);

    }




    private void createRowImage(AppExpenseMainViewModel item, File file) throws IOException {
//        BufferedImage img = new BufferedImage(400, 200, BufferedImage.TYPE_INT_RGB);
//        Graphics2D g = img.createGraphics();
//        g.setColor(Color.WHITE);
//        g.fillRect(0, 0, 400, 200);
//        g.setColor(Color.BLACK);
//        g.drawString("Expense Record", 20, 30);
//        g.drawString("Category: " + item.getCategory(), 20, 60);
//        g.drawString("Name: " + item.getExpenseName(), 20, 90);
//        g.drawString("Amount: ₹" + item.getAmount(), 20, 120);
//        g.dispose();
//        File dir = new File("uploads");
//        if (!dir.exists()) {
//            dir.mkdirs();
//        }
//
//        file = new File(dir, "record-" + item.getExpenseName() + ".png");
//        ImageIO.write(img, "png", file);


    }



    private void shareToWhatsApp(AppExpenseMainViewModel item) {
        try {
            // Sanitize filename (replace spaces with underscores)
            String safeName = item.getExpenseName().replaceAll("\\s+", "_");
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
            g.drawString("Remarks: " + item.getExpenseName(), 20, 50);
            g.drawString("Amount: ₹" + item.getAmount(), 20, 80);
            g.drawString("Expense Date: ₹" + item.getExpenseDate(), 20, 80);
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

    private Anchor createPdfDownload(List<AppExpenseMainViewModel> expenseList) {

        try {

            ByteArrayOutputStream outputStream =
                    new ByteArrayOutputStream();

            PDDocument document = new PDDocument();

            PDPage page = new PDPage();

            document.addPage(page);

            PDPageContentStream content =
                    new PDPageContentStream(document, page);

            content.beginText();

            content.setFont(
                    new PDType1Font(
                            Standard14Fonts.FontName.HELVETICA_BOLD),
                    14);

            content.newLineAtOffset(50, 750);

            content.showText("Expense Report");

            content.setFont(
                    new PDType1Font(
                            Standard14Fonts.FontName.HELVETICA),
                    10);

            int y = 730;

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm:ss");

            Double totalAmount = expenseList.stream().map(AppExpenseMainViewModel::getAmount).filter(Objects::nonNull)
                    .reduce(0.0, Double::sum);
            for (AppExpenseMainViewModel t : expenseList) {

                content.newLineAtOffset(0, -20);

                String formattedDate = t.getExpenseDate().format(formatter);



                content.showText(

                        "ID : " + t.getId()
                                + " | Date : " + formattedDate
                                + " | Category : " + t.getCategory()
                                + " | Expense Name : " + t.getExpenseName()
                                + " | Amount : " + t.getAmount());

                y -= 20;

                if (y < 50) {
                    break;
                }
            }

            content.newLineAtOffset(0, -30);
            content.showText("Total Amount : " + totalAmount);

            content.endText();

            content.close();

            document.save(outputStream);

            document.close();

            StreamResource resource =
                    new StreamResource(
                            "Expense_Report.pdf",
                            () -> new ByteArrayInputStream(
                                    outputStream.toByteArray()));

            Anchor downloadPdf =
                    new Anchor(resource, "Download PDF");

            downloadPdf.getElement()
                    .setAttribute("download", true);

            System.out.println("Successfully created");

            return downloadPdf;

        } catch (Exception e) {

            e.printStackTrace();

            return new Anchor();
        }
    }

}