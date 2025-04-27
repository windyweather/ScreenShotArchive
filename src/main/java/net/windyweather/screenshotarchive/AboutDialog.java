package net.windyweather.screenshotarchive;

import javafx.event.ActionEvent;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.awt.*;
import java.io.IOException;
import java.util.Objects;

import static java.awt.event.WindowEvent.WINDOW_ACTIVATED;
import static javafx.stage.WindowEvent.*;
import static net.windyweather.screenshotarchive.SSController.printSysOut;

public class AboutDialog {
    public Label lblSSAVersion;
    public TextArea taAboutText;
    public Hyperlink hlLinkToGitHub;
    public Button btnCloseAboutDialog;

    private final String[] sAboutText = new String[] {
            "Screen Shot Archive is a program to manage, view and backup screenshots from all the games you play. ",
            "The model the program uses is that each game stores screen shots in folders in various places, including in various subfolders of your home folder. This is called the Source. For example:\n",
            "C:\\Users\\Yournamehere\\OneDrive\\Documents\\game\\screens or in\n",
            "SomeSSD:\\GameCompany\\GameFolder\\ScreenShots.\n\n",
            "So with these game folders all over the place how to do you conveniently:\n\n",
            "1 - View the files.\n",
            "2 - Backup the files to a common place. Called the Destination.\n",
            "3 - Organize them so you can easily find them by date over the years that you play. The shots are stored in sub-folders based on the modify dates of the files. The date choices are: year and month, or year month and day.\n",
            "4 - The screen shot images can be renamed with a prefix indicating the game so you can find the files by game name. Renaming is optional.\n",
            "5 - If the files are renamed, the remainder of the name is the last modified date/time of the file to make it unique.\n\n",
            "See more at the link below:"
            };


    /*
        Wait until the window is "Shown" before we load up the TextArea with the content above.
        Before that it won't take. Like the TextArea doesn't fully exist or something.
     */
    public void SetStuffUp() {

        SSController.printSysOut("AboutDialog - SetStuffUp called");
        taAboutText.setEditable( true );
        taAboutText.clear();
        for (String s : sAboutText) {
            taAboutText.appendText(s);
        }
        taAboutText.setEditable( false );
        taAboutText.deselect();
        taAboutText.home();
        /*
            Put the version number in the dialog
         */
        lblSSAVersion.setText( "Version " + SSApplication.APP_VERSION);

    }

    /*
        During Initialize, fix up the text area.
     */
    @FXML

    public void start( Stage stage ) {

    printSysOut("About Start called");

    }


    public void initialize(){
        SetStuffUp();
    }

    public void OnCloseAbout(ActionEvent actionEvent) {
        /*
          get the scene from any GUI item, and get window from that.
          Then that's the stage and call close on it.
         */
        Stage stage = (Stage) btnCloseAboutDialog.getScene().getWindow();
        stage.close();
    }

    /*
        We have to do the link ourselves
     */
    public void OnLinkToGitHub(ActionEvent actionEvent) throws IOException {
        String uri = hlLinkToGitHub.getText();
        System.out.println(String.format("Open GitHub link in the browser: %s", uri));
        Desktop desktop = Desktop.getDesktop();
        desktop.browse( java.net.URI.create(uri));
    }
}
