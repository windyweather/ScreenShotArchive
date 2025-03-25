package net.windyweather.screenshotarchive;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollBar;
//make a change
public class SSController {
    public Button btnUpdatePair;
    public Button btnRemovePair;
    public Button btnAddPair;
    public Button btnViewSource;
    public Button btnViewDestination;
    public Button btnCopySource;
    public Button btnDeleteSource;
    public Button btnCloseAppButton;
    public MenuItem miSavePairs;
    public MenuItem miCloseApplication;
    public MenuItem miAboutApplication;
    public Button btnGoImagesStart;
    public ScrollBar sbSlideToImage;
    public Button btnGoImagesEnd;
    @FXML
    private Label lblImageName;

    @FXML
    protected void lblImageName() {
        lblImageName.setText("Welcome to JavaFX Application!");
    }

    public void OnMenuSavePairs(ActionEvent actionEvent) {
    }

    public void OnMenuCloseApplication(ActionEvent actionEvent) {
    }

    public void onAboutApplication(ActionEvent actionEvent) {
    }

    public void onGoImagesStart(ActionEvent actionEvent) {
    }

    public void onGoImagesEnd(ActionEvent actionEvent) {
    }

    public void btnAddPair(ActionEvent actionEvent) {
    }

    public void OnRemovePair(ActionEvent actionEvent) {
    }

    public void OnUpdatePair(ActionEvent actionEvent) {
    }

    public void OnViewSource(ActionEvent actionEvent) {
    }

    public void OnViewDestination(ActionEvent actionEvent) {
    }

    public void OnCopySource(ActionEvent actionEvent) {
    }

    public void OnDeleteSource(ActionEvent actionEvent) {
    }

    public void OnCloseAppButton(ActionEvent actionEvent) {
    }
}