package final_project;

import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.layout.FlowPane;

/*
 * The left hand pane to be used in the stage
 * Vertical flow pane with specific styling
 * In the main application, this pane is used to house buttons for changing between search and weekly filings
 */

public class LeftPane
        extends FlowPane {

    LeftPane() {

        this.setStyle("-fx-background-color: #1c355e; -fx-border-radius: 0 0 0 5; -fx-background-radius: 0 0 0 5");
        this.setOrientation(Orientation.VERTICAL);
        this.setVgap(15);
        this.setPadding(new Insets(20, 20, 20, 20));
        this.setAlignment(Pos.TOP_CENTER);
    }

}
