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
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

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

    /*
        GUI fields for the pair
     */
    public TextField txtSelectedPairName;
    public TextField txtSourcePath;
    public TextField txtDestPath;
    public CheckBox chkSearchSubFolders;
    public ComboBox<String> cbChooseFolderSuffix;
    public TextField txtFilePrefix;
    public CheckBox chkPreserveFileNames;

    public CheckBox chkTestLogOnly;

    public Button btnSetDestPath;
    public Button btnSetSourcePath;


    @FXML
    private Label lblImageName;

/*
    A pair for testing
 */
    private static final SSArchivePair anArchivePair;
    static {
        // Make sure we have an SSArchivePair
        anArchivePair = new SSArchivePair();
    }


    //
    // Do this in one place so we can easily turn it off later
    //
    public static void printSysOut( String str ) {
        System.out.println(str);
    }


    /* Which OS are we running. Not sure why we care. But maybe we do.
     */
    private boolean isOsWindows()
    {
        String osName = System.getProperty ("os.name");
        printSysOut(osName);
        return osName.contains("Windows");
    }

    private boolean isOsLinux()
    {
        String osName = System.getProperty ("os.name");
        printSysOut(osName);
        return osName.contains("Linux");
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

    /*
        Get and put stuff from / to the GUI with our local pair
     */
    private boolean GetPairFromGui() {

        anArchivePair.sPairName = txtSelectedPairName.getText();
        if (anArchivePair.sPairName.isEmpty()) {
            return false;
        }
        anArchivePair.sSourcePath = txtSourcePath.getText();
        anArchivePair.sDestinationPath = txtDestPath.getText();
        anArchivePair.sFolderSuffix = cbChooseFolderSuffix.getValue();
        anArchivePair.sFilePrefix = txtFilePrefix.getText();
        anArchivePair.bSearchSubFolders = chkSearchSubFolders.isSelected();
        anArchivePair.bPreserveFileNames = chkPreserveFileNames.isSelected();
        return true;
    }

    /*
        Load up the GUI from the pair. But in case we are cleaning
        the GUI, then do it regardless
     */
    private void PutGuiFromPair() {

        txtSelectedPairName.setText( anArchivePair.sPairName);
        txtSourcePath.setText(anArchivePair.sSourcePath);
        txtDestPath.setText(anArchivePair.sDestinationPath);
        cbChooseFolderSuffix.setValue( anArchivePair.sFolderSuffix);
        txtFilePrefix.setText(anArchivePair.sFilePrefix);
        chkSearchSubFolders.setSelected(anArchivePair.bSearchSubFolders);
        chkPreserveFileNames.setSelected(anArchivePair.bPreserveFileNames);

      }

    public void btnAddPair(ActionEvent actionEvent) {
        /*
            Test with index zero
         */
        if ( !GetPairFromGui() ) {
            setStatus("Enter a path name first");
            return;
        }
        if (!anArchivePair.PutPairToStore( 0 ) ) {
            setStatus("Error writing pair to store");
        }
        setStatus("Pair Written to Store with Idx 0");

    }

    public void OnRemovePair(ActionEvent actionEvent) {
        /*
          Test Remove Pair from Store
         */
        anArchivePair.RemovePairFromStore( 0 );
        anArchivePair.ClearPair();
        PutGuiFromPair();
        setStatus("Pair idx 0 removed from Store");
    }

    public void OnUpdatePair(ActionEvent actionEvent) {
        /*
            Test with Index zero
         */
        if ( anArchivePair.GetPairFromStore( 0 ) ) {
            PutGuiFromPair();
            setStatus("Pair read from Store with Idx 0");
        }
        else {
            setStatus("No Pair in Store with Idx 0");
        }

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
        /*
          get the scene from any GUI item, and get window from that.
          Then that's the stage and call close on it.
         */
        printSysOut("OnCloseAppButton: closing the app");
        /*
          Window close event never called. So do close stuff
          from here too.
         */
        AppCloseStuffToDo();

        // Now call the stage to actually close us up
        Stage stage = (Stage) txtSelectedPairName.getScene().getWindow();
        stage.close();
    }



    /*
    Go find our saved windows pos/size and saved pairs
     */
    public void OnWindowShown( WindowEvent evt) {

        // initialize combo box choices
        ObservableList<String> sol = FXCollections.observableArrayList("yyyy_MM", "", "yyyy_MM_dd");
        cbChooseFolderSuffix.setItems(sol);
        cbChooseFolderSuffix.getSelectionModel().selectFirst();

        /*
          Woops Too early to do this here.
          The SSArchivePair is not yet set up
          we will call it after we restore the windows pos/size
          */
        /*
        // reading Pairs will not open a Pair.
        printSysOut("OnWindowShown - call RestorePairsList()");
        RestorePairsList();
         */

        /*
          We don't care what OS, but just check in case later we care
         */
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
    break out the close stuff here so we can call it from two places
     */
    void AppCloseStuffToDo() {
        printSysOut( "SSController: AppCloseStuffToDo - save your stuff here" );
        // write the Pairs List
        // Window pos / size are saved in SSApplication
        SavePairsList();
    }
    /*
    Save the windows pos/size and save the pairs
     */
    public void OnWindowCloseRequest(WindowEvent evt) {
        printSysOut("OnWindowCloseRequest: window close button");
        AppCloseStuffToDo();

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



    /*
    // use the java System.Properties class for ini files
    // and write them in XML format.
    // Both the default file and the show files are stored in this way.
    */
/*
    private String propertyFilePathPrefix()
    {
        return System.getProperty("user.home")+ File.separator+".windyweather";
    }

    private String defaultsFilePath()
    {
        return propertyFilePathPrefix() + File.separator+"ScreenShotArchiveDefaults.xml";
    }
*/

    //
    // Restore the Pair List File from Preferences
    //
    public void RestorePairsList() {
        /*
        use preferences class to do this
         */
        printSysOut("RestorePairsList starting");

         /*
          For testing, just read the first pair and stuff it in the GUI
         */
        if (!anArchivePair.GetPairFromStore(0)) {
            anArchivePair.ClearPair();
            setStatus("No pairs to restore");
            printSysOut( "No pairs to restore");
        }
        else {
            setStatus("Pair idx 0 restored");
            printSysOut("Pair idx 0 restored");
            printSysOut(String.format("anArchivePair: Src:%s -- Dst:%s", anArchivePair.sSourcePath, anArchivePair.sDestinationPath));
        }
        PutGuiFromPair();

        printSysOut("RestorePairsList ");
        setStatus("Pairs restored");
        }

    //
    // store the gui to the defaults file
    //
    private void SavePairsList()
    {
       /*
        use Preferences class
        For testing just use idx 0
        */

        GetPairFromGui();
        printSysOut("SavePairsList");
        printSysOut(String.format("anArchivePair: Src:%s -- Dst:%s", anArchivePair.sSourcePath, anArchivePair.sDestinationPath));
        anArchivePair.PutPairToStore( 0 );
        setStatus("Pairs saved");
    }


    public void OnSetDestinPath(ActionEvent actionEvent) {
    }

    public void OnSetSourcePath(ActionEvent actionEvent) {
    }
}
