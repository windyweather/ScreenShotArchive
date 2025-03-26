package net.windyweather.screenshotarchive;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.WindowEvent;

import javax.swing.*;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import static javafx.stage.WindowEvent.*;

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
    public TextField txtStatus;
    public ComboBox<String> cbChooseFolderSuffix;
    public TextField txtFilePrefix;
    public CheckBox chkPreserveFileNames;
    public CheckBox chkTestLogOnly;
    public TextField txtDestPath;
    public Button btnSetDestPath;
    public Button btnSetSourcePath;
    public TextField txtSourcePath;
    @FXML
    private Label lblImageName;

    //
    // Do this in one place so we can easily turn it off later
    //
    private void printSysOut( String str ) {
        System.out.println(str);
    }


    /* Which OS are we running. Not sure why we care. But maybe we do.
     */
    private boolean isOsWindows()
    {
        String osName = System.getProperty ("os.name");
        printSysOut(osName);
        if ( osName.contains("Windows") ) {
            return true;
        }
        else {
            return false;
        }
    }

    private boolean isOsLinux()
    {
        String osName = System.getProperty ("os.name");
        printSysOut(osName);
        if ( osName.contains("Linux") ) {
            return true;
        }
        else {
            return false;
        }
    }


    @FXML


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



    /*
    Go find our saved windows pos/size and saved pairs
     */
    public void OnWindowShown( WindowEvent evt) {
        //lblImageName.setText("Set on Window Shown");
        // Set combo box value choices
        //cbChooseFolderSuffix.
        ObservableList<String> sol = FXCollections.observableArrayList("yyyy_MM", "", "yyyy_MM_dd");
        cbChooseFolderSuffix.setItems(sol);
        cbChooseFolderSuffix.getSelectionModel().selectFirst();
        // reading defaults will not open a Pair.
        if (isOsWindows() )
        {
            printSysOut("Windows platform");
        }
        else if (isOsLinux()) {
            printSysOut( "Linux Platform");
        }
        else {
            printSysOut( "Unknown Platform");
        }
    }
    /*
    Save the windows pos/size and save the pairs
     */
    public void OnWindowCloseRequest( WindowEvent evt) {
        printSysOut( "ShowRunnerEvents closeOurApplication - save your stuff here" );
        // write the defaults file
        saveDefaultsFile();

    }


    // handle window events here in the controller so we have access to
    // All the items and methods of the window. At last at WINDOW_SHOW and beyond.
    public void handleWindowEvent(WindowEvent event) {
        EventType<WindowEvent> state = event.getEventType();
        if (state == WINDOW_SHOWN) {
            System.out.println("Window shown");
            OnWindowShown(event);
        }
        else if (state == WINDOW_HIDING) {
            System.out.println("Window hiding");
        }
        else if ( state == WINDOW_CLOSE_REQUEST) {
            System.out.println("Window close request");
            OnWindowCloseRequest(event);
            }
        }
    //
    // Put some text in the status line to say what's up
    //
    public void setStatus( String sts ) {

        txtStatus.setText( sts );
    }

    // Just a dummy action to cause txtStatus to be declared.
    public void OnKeyPressedStatus(KeyEvent keyEvent) {
        System.out.println("OnKeyPressedStatus");
    }

    /*
     * usually means that everything worked up to now
     */
    public boolean isStatusEmpty() {
        return txtStatus.getText().isEmpty();
    }



    //
    // use the java System.Properties class for ini files
    // and write them in XML format.
    // Both the default file and the show files are stored in this way.
    //

    private String propertyFilePathPrefix()
    {
        return System.getProperty("user.home")+ File.separator+".windyweather";
    }

    private String defaultsFilePath()
    {
        return propertyFilePathPrefix() + File.separator+"ScreenShotArchiveDefaults.xml";
    }


    //
    // Restore the Defaults File from XML
    //
    private void restoreDefaultsFile()
    {
        /*
        use preferences class to do this
         */



        /*
        some defaults
         */
        /*
        String sImpressPath = defProps.getProperty("ImpressPath");
        String sImpressOptions = defProps.getProperty("ImpressOptions");
        String sShowPath = defProps.getProperty("ShowPath");

        if ( 0 != sImpressPath.length() ) {
            tfImpressPath.setText(sImpressPath);
        }
        if ( 0 != sImpressOptions.length() ) {
            tfOptions.setText(sImpressOptions);
        }
        if ( 0 != sShowPath.length() ) {
            tfShowPath.setText( sShowPath );
        }
*/
        printSysOut("restoreDefaultsFile ");
        //defProps.list(System.out);
        setStatus("Defaults restored");
    }

    //
    // store the gui to the defaults file
    //
    private void saveDefaultsFile()
    {
       // use Preferences class
        setStatus("Defaults saved");
    }


    public void OnSetDestinPath(ActionEvent actionEvent) {
    }

    public void OnSetSourcePath(ActionEvent actionEvent) {
    }
}
