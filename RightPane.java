package final_project;

import com.sun.deploy.uitoolkit.impl.fx.HostServicesFactory;
import com.sun.javafx.application.HostServicesDelegate;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Callback;

/*
* The RightPane class is a list view with a custom cell factory to handle Filing objects
* This class inherits all it's methods from ListView
* The Cell Factory assembles Text objects into VBoxes, formats them, and displays them in the list view
* Styling is also added in the constructor to the ListView
* This pane can hold results for searches and weekly filings
 */

public class RightPane
        extends ListView<Filing> {

    RightPane(ObservableList<Filing> items) {

        // final ListView<Filing> list = new ListView<>(items);

        this.setItems(items);
        this.setCellFactory(new Callback<ListView<Filing>, ListCell<Filing>>() {

            @Override
            public ListCell<Filing> call(ListView<Filing> arg0) {
                return new ListCell<Filing>() {

                    @Override
                    protected void updateItem(Filing item, boolean bln) {
                        super.updateItem(item, bln);
                        if (item != null) {

                            final int space = 40;

                            Text caseNumber = new Text(item.getCaseNumber());
                            Text filer = new Text(item.getFiler());
                            Text documentDescription = new Text(item.getDocumentDescription());
                            Text dateFiled = new Text("DATE FILED: " + item.getDateAsString());

                            Font heavy = Font.font("Avenir", FontWeight.BOLD, FontPosture.REGULAR, 16);
                            Font boldItalics = Font.font("Avenir", FontWeight.findByWeight(700), FontPosture.ITALIC, 14);
                            Font regular = Font.font("Avenir", FontWeight.MEDIUM, FontPosture.REGULAR, 14);
                            Font boldSmall = Font.font("Avenir", FontWeight.BOLD, FontPosture.REGULAR, 10);

                            caseNumber.wrappingWidthProperty().bind(this.widthProperty().subtract(space));
                            filer.wrappingWidthProperty().bind(this.widthProperty().subtract(space));
                            documentDescription.wrappingWidthProperty().bind(this.widthProperty().subtract(space));
                            dateFiled.wrappingWidthProperty().bind(this.widthProperty().subtract(space));

                            caseNumber.setFont(heavy);
                            filer.setFont(boldItalics);
                            documentDescription.setFont(regular);
                            dateFiled.setFont(boldSmall);

                            caseNumber.setOnMouseClicked(e -> {
                                HostServicesDelegate hostServices = HostServicesFactory.getInstance(new Main());
                                hostServices.showDocument(item.getCaseURL());
                            });

                            documentDescription.setOnMouseClicked(e -> {
                                HostServicesDelegate hostServices = HostServicesFactory.getInstance(new Main());
                                hostServices.showDocument(item.getDocumentURL());
                            });

                            caseNumber.setFill(Color.valueOf("#1c355e"));

                            caseNumber.setOnMouseEntered(e -> {
                                caseNumber.setStyle("-fx-underline: true");
                            });
                            documentDescription.setOnMouseEntered(e -> {
                                documentDescription.setStyle("-fx-underline: true");
                            });

                            caseNumber.setOnMouseExited(e -> {
                                caseNumber.setStyle("-fx-underline: false");
                            });
                            documentDescription.setOnMouseExited(e -> {
                                documentDescription.setStyle("-fx-underline: false");
                            });

                            VBox vBox = new VBox(caseNumber, filer, documentDescription, dateFiled);
                            vBox.setAlignment(Pos.CENTER_LEFT);
                            vBox.setSpacing(5);
                            vBox.setPadding(new Insets(5, 5, 5, 5));

                            setGraphic(vBox);
                        }
                    }
                };
            }
        });

        this.setStyle("-fx-background-color: #f5f5f5; -fx-border-radius: 0 0 5 0; -fx-background-radius: 0 0 5 0");
    }

}
