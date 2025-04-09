package net.windyweather.screenshotarchive;

import javafx.event.ActionEvent;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import static java.awt.event.WindowEvent.WINDOW_ACTIVATED;
import static javafx.stage.WindowEvent.*;

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

    }

    /*
        During Initialize, fix up the text area.
     */
    @FXML
    void initialize(){
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

    // handle window events here in the controller so we have access to
    // All the items and methods of the window. At last at WINDOW_SHOW and beyond.
    public void handleWindowEvent(WindowEvent event) {
        EventType<WindowEvent> state = event.getEventType();
        if (state == WINDOW_SHOWN) {
            System.out.println("About Window shown");
            /*
                We have to wait until here to rewrite the TextArea with the
                content with new lines in it. DOESN'T WORK
             */
            SetStuffUp();
        }
        if (state == WINDOW_SHOWING) {
            System.out.println("About Window showing");
            /*
                We have to wait until here to rewrite the TextArea with the
                content with new lines in it. DOESN'T WORK
             */
            SetStuffUp();
        }

        else if (state == WINDOW_HIDING) {
            System.out.println("About Window hiding");
        }
        else if ( state == WINDOW_CLOSE_REQUEST) {
            System.out.println("About Window close request");
        }
    }

}
