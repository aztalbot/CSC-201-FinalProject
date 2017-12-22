package final_project;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.ArrayList;
import java.util.Arrays;

/*
* Main class with start method for initializing and controlling the program
* Algorithm/Pseudocode:
* 1. Declare certain variables, including the BorderPane, RightPane, ObservableList of Filings, CaseData,
*    and the weekly filings query for the scope of the entire class to be used in multiple class methods
* 2. Within the start method, instantiate CaseData, which contains the necessary methods to return the data for filings
*    to populate the ObservableList of items
* 3. Load in and format icons via an ImageView array for use as buttons in the program
* 4. Instantiate buttons (and format them) for this program and give each a graphic from the ImageView array,
*    including a refresh button.
* 5. Instantiate, format, and add buttons to LeftPane (except the refresh button), which will be 100 pixels wide
* 6. Before instantiating RightPane, invoke getFilings within caseData with the proper parameters to load the weekly
*    filings into an ArrayList which in turn is passed to the observable list.
*       a. This step will take a while as the program waits on the URL to load, then parses that JSON data
* 7. With the JSON data now loaded into the observable array list, this list (items) is passed when instantiating
*    RightPane, which uses the list to feed the cell factory and generate each cell
*       a. For each JSON object it loads data from unique cases, this is necessary for links to cases
*          (this is an inefficiency I didn't have time to fix, as it makes so many requests and parses
*          so many objects it takes a long time to load)
* 8. Create a text header using HBox, this will be at the top of the RightPane
*       a. Within this HBox also place the refresh button, along with a Region for spacing
*       b. The Region needs to be HGrow = Always so that the header and the button stay in opposite ends of the box
* 9. Add RightPane and header to a VBox, with VGrow for the ListView as Always, to avoid height gaps
* 10. Create a BorderPane, set the left as the LeftPane, and set the right as the VBox containing the header
*     and list view
* 11. Add BorderPane to Scene and the Scene to a Stage, then show stage.
* 12. Add event handling methods to buttons to change between weekly filings and search results (also to refresh)
*       a. Button1 handles event by setting BorderPane right to hold the ListView with weekly filings
*       b. Button2 handles event by setting BorderPane right to hold a search TextField
*         i. When the user presses enter, the words in the TextField are split into an array
*         ii. Each term is inserted into a standard query, which makes up the URL
*         iii. getFilings is invoked to get filings based on the query
*         iv. The filings are added to a ListView (RightPane), which is added a VBox with the search TextField
*       c. Refresh button handles event by switching out the items in the list view with newly queried results
*/

public class Main extends Application {

    private BorderPane pane;
    private RightPane right;
    private ObservableList<Filing> items;
    private CaseData caseData;

    // query necessary for weekly filings, get everything filed in past 5 days (important
    // since sometimes filings are not posted for multiple days)
    private final String query= "((Year%20eq%202017d)%20and%20(Month%20eq%206d))%20and%20(Day%20eq%2029d)%20or%20((Year%20eq%202017d)" +
            "%20and%20(Month%20eq%206d))%20and%20(Day%20eq%2028d)%20or%20((Year%20eq%202017d)%20and%20(Month%20eq%206d))" +
            "%20and%20(Day%20eq%2027d)%20or%20((Year%20eq%202017d)%20and%20(Month%20eq%206d))%20and%20(Day%20eq%2026d)%20or" +
            "%20((Year%20eq%202017d)%20and%20(Month%20eq%206d))%20and%20(Day%20eq%2025d)&$orderby=Month%2CDay&$select=" +
            "CaseNumber%2CDocName%2CMonth%2CDay%2CYear%2CDocID%2CFileName";

    @Override
    public void start(Stage primaryStage) throws Exception {

        // contains methods for getting JSON data
        caseData = new CaseData();

        // load icons into image views as array
        ImageView[][] icons = new ImageView[3][2];
        icons[0][0] = new ImageView("final_project/icons/WeeklyFilingsIdle.png");
        icons[0][1] = new ImageView("final_project/icons/WeeklyFilingsActive.png");
        icons[1][0] = new ImageView("final_project/icons/SearchIdle.png");
        icons[1][1] = new ImageView("final_project/icons/SearchActive.png");
        icons[2][0] = new ImageView("final_project/icons/RefreshDark.png");
        icons[2][1] = new ImageView("final_project/icons/QuickLookDark.png");
        /* optional image icons for future functionality
        icons[3][0] = new ImageView("final_project/icons/MyCasesIdle.png");
        icons[3][1] = new ImageView("final_project/icons/MyCasesActive.png");
        icons[4][0] = new ImageView("final_project/icons/RefreshActive.png");
        icons[4][1] = new ImageView("final_project/icons/RefreshIdle.png");
        icons[5][0] = new ImageView("final_project/icons/QuickLookIdle.png");
        icons[5][1] = new ImageView("final_project/icons/QuickLookActive.png");
        */

        // format image views in icons array
        for(ImageView iconArray[] : icons) {
            for(ImageView icon : iconArray) {
                icon.setPreserveRatio(true);
                icon.setFitWidth(50);
            }
        }

        // BUTTONS
        Button button1 = new Button("", icons[0][1]);
        Button button2 = new Button("", icons[1][0]);

        icons[2][0].setFitWidth(30);
        Button refreshBtn = new Button("", icons[2][0]);
        // Button button3 = new Button("", icons[2][0]);
        // Button button4 = new Button("", icons[3][0]);

        button1.setStyle("-fx-background-color: #1c355e");
        button2.setStyle("-fx-background-color: #1c355e");
        refreshBtn.setStyle("-fx-background-color: transparent");
        // button3.setStyle("-fx-background-color: #1c355e");
        // button4.setStyle("-fx-background-color: #1c355e");

        // ADD BUTTONS TO LEFT PANE
        LeftPane left = new LeftPane();
        left.getChildren().addAll(button1, button2/* , button3, button4 */);
        left.setPrefWidth(100);

        // Load JSON data via caseData
        System.out.println("Loading weekly filings...");
        ArrayList<Filing> weeklyFilings = caseData.getFilings("DailyFilings/GetAllDailyFilings", query);
        items = FXCollections.observableArrayList(weeklyFilings);

        // Create a list view (RightPane) with the JSON items (type: Filing)
        right = new RightPane(items);
        right.setStyle("-fx-background-color: #f5f5f5; -fx-border-radius: 0 0 5 0; -fx-background-radius: 0 0 5 0");
        System.out.println("Finished loading!");

        // Create a header using HBox, add to VBox with RightPane
        Text header = new Text("Weekly Filings");
        Font headerFont = Font.font("Avenir", FontWeight.findByWeight(900), FontPosture.REGULAR, 18);
        header.setFont(headerFont);
        Region space = new Region();
        HBox hBox = new HBox(header, space, refreshBtn);
        hBox.setPadding(new Insets(5, 5, 5, 5));
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.setMaxHeight(40);
        hBox.setStyle("-fx-background-color: whitesmoke; -fx-border-width: 0 0 1 0; -fx-border-color: darkgrey");
        VBox rightPlus = new VBox(hBox, right);
        rightPlus.alignmentProperty().setValue(Pos.TOP_CENTER);
        hBox.setHgrow(space, Priority.ALWAYS);
        rightPlus.setVgrow(right, Priority.ALWAYS);

        // BORDER PANE
        pane = new BorderPane();
        pane.setStyle("-fx-border-radius: 0 0 5 5; -fx-background-radius: 0 0 5 5");
        pane.setLeft(left);
        right.prefWidthProperty().bind(pane.widthProperty().subtract(115));
        pane.setRight(rightPlus);

        // CREATE SCENE
        Scene scene = new Scene(pane, 500, 700);
        scene.setFill(Color.TRANSPARENT);
        scene.getStylesheets().add(getClass().getResource("StyleSheet.css").toString());
        pane.prefWidthProperty().bind(scene.widthProperty());

        // CREATE AND SET STAGE
        primaryStage.initStyle(StageStyle.UNIFIED);
        primaryStage.setMinWidth(400);
        primaryStage.setMinHeight(300);
        primaryStage.setTitle("DocketSearch");
        primaryStage.setScene(scene);
        primaryStage.show();

        // BUTTON ACTION HANDLERS
        button1.setOnAction(e -> { // weekly filings
            button1.setGraphic(icons[0][1]);
            button2.setGraphic(icons[1][0]);
            // button3.setGraphic(icons[2][0]);
            // button4.setGraphic(icons[3][0]);

            goToWeeklyFilings(rightPlus);
        });
        button2.setOnAction(e -> { //search
            button1.setGraphic(icons[0][0]);
            button2.setGraphic(icons[1][1]);
            // button3.setGraphic(icons[2][0]);
            // button4.setGraphic(icons[3][0]);

            goToSearch();
        });
        refreshBtn.setOnAction(e -> { //search
            refreshItems();
        });
        // optional buttons below for future functionality
        /* button3.setOnAction(e -> {
            button1.setGraphic(icons[0][0]);
            button2.setGraphic(icons[1][0]);
            button3.setGraphic(icons[2][1]);
            button4.setGraphic(icons[3][0]);
        });
        button4.setOnAction(e -> {
            button1.setGraphic(icons[0][0]);
            button2.setGraphic(icons[1][0]);
            // button3.setGraphic(icons[2][0]);
            button4.setGraphic(icons[3][1]);
        }); */
    }

    // Change Right pane to show Search box instead of header, when enter is pressed, show search results list view
    private void goToSearch() {

        // Search text field for user to type search terms
        TextField search = new TextField("Search");
        Font headerFont = Font.font("Avenir", FontWeight.findByWeight(900), FontPosture.REGULAR, 18);
        search.setFont(headerFont);
        search.setStyle("-fx-border-radius: 0 0 0 0; -fx-background-color: transparent; -fx-border-width: 0 0 0 0;  -fx-text-inner-color: grey");

        // clear search text field when clicked, and use dark font for search terms
        search.setOnMousePressed(e -> {
            if(search.getStyle().contains("grey")) {
                search.clear();
                search.setStyle(
                        "-fx-border-radius: 0 0 0 0; -fx-background-color: transparent; -fx-border-width: 0 0 0 0; -fx-text-inner-color: black"
                );
            }
        });

        // to hold search text field
        HBox hBox = new HBox(search);
        hBox.setPadding(new Insets(5, 5, 5, 5));
        hBox.setAlignment(Pos.CENTER);
        hBox.setMaxHeight(40);
        hBox.setStyle("-fx-background-color: whitesmoke; -fx-border-width: 0 0 1 0; -fx-border-color: darkgrey");

        // VBox to hold search field and search results list view
        VBox rightPlus = new VBox(hBox);
        rightPlus.prefWidthProperty().bind(pane.widthProperty().subtract(115));
        search.prefWidthProperty().bind(hBox.widthProperty());
        search.prefHeightProperty().bind(hBox.heightProperty());
        pane.setRight(rightPlus);

        // on enter
        search.setOnKeyPressed(e -> {
            if(e.getCode() == KeyCode.ENTER) {
                // wrap each search term in query
                String[] searchTerms = search.getCharacters().toString().split(" ");
                StringBuilder searchQuery = new StringBuilder();
                int i = 0;
                for(String term : searchTerms) {
                    String[] caseCategories = {"PUE-", "URS-", "SEC-", "PUR-", "INS-"};
                    if(Arrays.asList(caseCategories).contains(term.substring(0, 4))) {
                        searchQuery.append("substringof(%27")
                                .append(term)
                                .append("%27%2CMATTER_ID)");
                    } else {
                        searchQuery.append("substringof(%27")
                                .append(term)
                                .append("%27%2CDocument_Name)");
                    }

                    if(i < searchTerms.length - 1)
                        searchQuery.append("%20and%20");

                    i++;
                }
                searchQuery.append("%20eq%20true&$select=MATTER_ID%2CMATTER_NO%2CCase_Number%2CDocument_Name%2CDate_Filed%2CDocID%2CFileName");
                // System.out.println(query.toString());

                // load and build new results pane
                ArrayList<Filing> searchResults = caseData.getFilings("CaseDetails/GetDocuments", searchQuery.toString());
                items = FXCollections.observableArrayList(searchResults);
                RightPane searchPane = new RightPane(items);
                if (rightPlus.getChildren().size() > 1) rightPlus.getChildren().remove(1);
                rightPlus.getChildren().add(searchPane);
                rightPlus.setVgrow(searchPane, Priority.ALWAYS);
                rightPlus.alignmentProperty().setValue(Pos.TOP_CENTER);
                pane.setRight(rightPlus);
            }
        });
    }

    // set right pane to show weekly filings list view
    private void goToWeeklyFilings(VBox vBox) {
        pane.setRight(vBox);
    }

    // reload items into the list view
    private void refreshItems() {

        System.out.println("Refreshing weekly filing...");

        ArrayList<Filing> weeklyFilings = caseData.getFilings("DailyFilings/GetAllDailyFilings", query);
        items = FXCollections.observableArrayList(weeklyFilings);
        right.setItems(items);

        System.out.println("Refreshed!");

    }

    public static void main(String[] args) {
        launch(args);
    }
}
