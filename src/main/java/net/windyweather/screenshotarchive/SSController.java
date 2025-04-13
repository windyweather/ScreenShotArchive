package net.windyweather.screenshotarchive;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.*;
import java.util.Objects;
import java.util.prefs.Preferences;

import static javafx.stage.WindowEvent.*;
import static net.windyweather.screenshotarchive.SSAFilesHelper.FileHelperCleanSource;
import static net.windyweather.screenshotarchive.SSApplication.NODE_NAME;
import static net.windyweather.screenshotarchive.SSApplication.WINDOW_POSITION_X;
import static net.windyweather.screenshotarchive.SSApplication.WINDOW_POSITION_Y;
import static net.windyweather.screenshotarchive.SSApplication.WINDOW_WIDTH;
import static net.windyweather.screenshotarchive.SSApplication.WINDOW_HEIGHT;

// See if we can find DirectoryScanner somewhere
import org.codehaus.plexus.util.DirectoryScanner;

/*
    The controller drives the GUI or vice versa
 */
public class SSController {
    private static final String FOLDER_SUFFIX_DEFAULT = "yyyy_MM";

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

    public SplitPane splitPaneOutsideContainer;
    public Label lblImageName;
    public ScrollPane spImagePane;
    public Button btnGoImageBack10;
    public Button btnGoImageBackOne;
    public ScrollBar sbImageListScrollBar;
    public Button btnImageForwardOne;
    public Button btnImageForward10;


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

    public Button btnMovePairUp;
    public Button btnMovePairDown;
    public Button btnMovePairTop;
    public Button btnMakeTestPairs;
    public ImageView imgImageView;


    ObservableList<SSArchivePair> listPairs = FXCollections.observableArrayList();
    public ListView<SSArchivePair> lvScreenShotPairs;


    @FXML

    /*
        List of images from the last View Source / Destination
        Which Image are we pointed to
        and do we have any images at all
     */
    private String[] sImageList;
    private String  sImageBasePath;
    private int intImageIndex;
    private boolean bImagesValid;

    public Image anImage;
    private double dZoomScale;

/*
    A pair for testing
 */
    private static SSArchivePair anArchivePair;
    static {
        // Make sure we have an SSArchivePair
        anArchivePair = new SSArchivePair();
    }

    /*
   Put some text in the status line to say what's up
    */
    public void setStatus( String sts ) {

        txtStatus.setText( sts );
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

    /*
        Open an image from the list based on the index
        Just do nothing if we don't have one
     */

    void OpenImageFromList( ) {

        /*
            Check for sanity of the call
         */
        if ( !bImagesValid || intImageIndex >= sImageList.length ) {
            setStatus("OpenImageFromList no images or invalid index");
            lblImageName.setText("");
            ClearImage();
            return;
        }
        /*
            Time the following process in Milliseconds just to see how long it takes
         */
        long intStartOpen = System.currentTimeMillis();
        /*
            Get the image we are interested in.
            Reconstruct the absolute path from the base path we saved, and
            the file name returned by the scanner
         */
        String sImageFileName = sImageList[intImageIndex];
        String sImageFilePath = sImageBasePath + File.separator + sImageFileName;
        printSysOut(String.format( "openImageFromList - Opening index %d %s", intImageIndex, sImageFilePath ));
        lblImageName.setText( sImageFileName );
        File selectedImageFile = new File( sImageFilePath );
        /*
            we appear to have a file so we will try to load the image with it.
         */
        InputStream imageAsStream;
        try {
            //assert selectedImageFile != null;
            imageAsStream = new FileInputStream(selectedImageFile);
        } catch (FileNotFoundException e) {
            printSysOut("Somehow, image file not found");
            throw new RuntimeException(e);
        }
        /*
            Fire up the image in the GUI
            Turn the imageview back on in cased it was turned off
         */
        imgImageView.setVisible( true );
        anImage = new Image( imageAsStream );
        imgImageView.setImage( anImage );

        imgImageView.setPreserveRatio(true);

        /*
            Don't need the following because the ScrollPane is already connected
            to the imageview by fxml
         */
        //spScrollPane = new ScrollPane(imgImageView);
        spImagePane.setPannable(true);
        //spImagePane.setHvalue(0.5);
        //spImagePane.setVvalue(0.5);

        /*
            Scrollbar shows us where we are in the list
         */
        sbImageListScrollBar.setValue( intImageIndex);
         /*
            Now set up a scroll wheel based zoom
            and set a default zoom scale
         */

        dZoomScale = 1.0;
        printSysOut("setup Wheel to zoom - ImageView Scroll Event");
        imgImageView.setOnScroll(
                new EventHandler<ScrollEvent>() {
                    @Override
                    public void handle(ScrollEvent event) {
                        event.consume();

                        //printSysOut("Zoom with wheel");
                        double zoomFactor = 1.05;
                        double deltaY = event.getDeltaY();
                    /*
                        Don't zoom forever. Just ignore it after
                        a while.
                     */
                        double dScale = dZoomScale;
                        if (deltaY > 0.0 && dScale > 10.0) {
                            printSysOut("Don't scale too big");
                            //event.consume();
                            return;
                        } else if (deltaY < 0.0 && dScale < 0.20) {
                            printSysOut("Don't scale too small");
                            //event.consume();
                            return;
                        }

                        if (deltaY < 0) {
                            zoomFactor = 0.95;
                        }

                        var x = spImagePane.getHvalue();
                        var y = spImagePane.getVvalue();
                        /*
                            What happens if we don't restore x and y?
                         */
                        /*
                            Let's zoom image with setFitWidth rather than setScale
                            but save our zoom so we can report it
                         */
                        dZoomScale = dZoomScale * zoomFactor;
                        imgImageView.setFitWidth(anImage.getWidth() * zoomFactor);

                        String scaleReport = String.format("ImageView scale factor %.3f", imgImageView.getScaleX() * dZoomScale);
                        setStatus(String.format("Zoom %.3f", dZoomScale));

                        /*
                            We saved this above, don't mess with it again
                         */
                        if ( false ) {
                        /*
                            Lets try this here and see if that fixes the pan after zoom
                         */
                            x = spImagePane.getHvalue();
                            y = spImagePane.getVvalue();
                        }
                        /*
                         ********************************************************************
                         *************** The following statement appears to have made it work
                         * Now wheel zooming preserves panning to the corners
                         ********************************************************************
                         */
                        imgImageView.setFitWidth(anImage.getWidth() * dZoomScale);
                        /*
                            Restore these for best behaviour of position in window after zoom
                         */
                        spImagePane.setHvalue(x);
                        spImagePane.setVvalue(y);

                    }
                } );
        long intEndOpen = System.currentTimeMillis();
        printSysOut(String.format("OpenImageFromList %d ms", intEndOpen - intStartOpen));
    } // OpenImageFromList

    void ClearImage() {
        /*
            clear the image we are looking at. Don't change anything else
         */
        imgImageView.setVisible( false );
        lblImageName.setText("");
        sbImageListScrollBar.setMax( 0 );
        sbImageListScrollBar.setMin( 0 );
        sbImageListScrollBar.setValue( 0 );
    }

    /*
        Enable / Disable the function buttons based on whether we have
        paths that are valid.
     */
    void EnableFunctionButtons() {
        boolean bSourceBlank = txtSourcePath.getText().isBlank();
        boolean bDestinationBlank = txtDestPath.getText().isBlank();

        if ( bSourceBlank && bDestinationBlank ) {
            btnViewSource.setDisable( true );
            btnViewDestination.setDisable( true );
            btnCopySource.setDisable( true );
            btnDeleteSource.setDisable( true );
            return;
        }
        if (bSourceBlank) {
            btnViewSource.setDisable( true );
            btnDeleteSource.setDisable( true );
            btnCopySource.setDisable( true );
        }
        if ( bDestinationBlank ) {
            btnViewDestination.setDisable( true );
            btnCopySource.setDisable( true );
        }
        /*
            Check to see if we have valid paths and set the buttons accordingly
         */
        File fileSrc = new File( txtSourcePath.getText() );
        File fileDst = new File( txtDestPath.getText() );
        btnViewSource.setDisable( !fileSrc.isDirectory() );
        btnDeleteSource.setDisable( !fileSrc.isDirectory() );
        btnViewDestination.setDisable( !fileDst.isDirectory() );
        btnCopySource.setDisable( !( fileSrc.isDirectory() && fileDst.isDirectory() ) );

    } // EnableFunctionButtons

    @FXML
    /*
        Called to initialize the controller
     */
    void initialize(){
        SetUpStuff();
    }
    /*
        Called from App to set things up
     */
    public void SetUpStuff(){

        // initialize combo box choices
        ObservableList<String> sol = FXCollections.observableArrayList("yyyy_MM", "", "yyyy_MM_dd");
        cbChooseFolderSuffix.setItems(sol);
        cbChooseFolderSuffix.getSelectionModel().selectFirst();

        /*
          Wake up the controller to restore the pairs
         */
        printSysOut("SetUpStuff - calling RestorePairsList");
        RestorePairsList();
        printSysOut("SetUpStuff - back from RestorePairsList");

        /*
            If we have pairs, load up the first one in the GUI
         */
        //printSysOut("SetUpStuff - if we have pairs, load up the first one");


        /*
        set some paths for testing
        obsolete now that we are saving / restoring pairs
         */
        if ( false ) {
            String sTestImagePath = "D:\\MMO_Pictures\\AlienBlackout";

            txtSourcePath.setText(sTestImagePath);
            txtDestPath.setText(sTestImagePath);
        }

        intImageIndex = 0;
        bImagesValid = false;

        /*
            Set the function buttons and enable listeners for file paths
         */
        EnableFunctionButtons();
        txtSourcePath.textProperty().addListener( ( observable, oldvalue, newvalue) -> {
            EnableFunctionButtons();
        });
        txtDestPath.textProperty().addListener( ( observable, oldvalue, newvalue) -> {
            EnableFunctionButtons();
        });

    }

    /*
        Allow manually store pairs at any time,
        not just on close
     */
    public void OnMenuSavePairs(ActionEvent actionEvent) {
        // Just save our pairs
        int numPairs = listPairs.size();
        // Always update store since if there are none we clear the store
        SavePairsList();
        if ( numPairs > 0 ) {

            setStatus(String.format("%d Pairs saved", numPairs) );
        } else {
            setStatus("No Pairs to Save");
        }
    }

    public void OnMenuCloseApplication(ActionEvent actionEvent) {
        /*
          Save stuff and close the stage to shut us down
         */
        CloseAppAndStage();
    }

    /*
        Launch the about dialog, and wait until the user closes it
     */
    public void onAboutApplication(ActionEvent actionEvent) throws IOException {
        printSysOut("onAboutApplication - launch about dialog");

        Stage stageOfUs = (Stage) txtSelectedPairName.getScene().getWindow();
        Stage stage = new Stage();

        FXMLLoader fxmlloader = new FXMLLoader( AboutDialog.class.getResource("about-dialog.fxml"));
        Scene aboutScene = new Scene( fxmlloader.load() );
        AboutDialog aboutControl = (AboutDialog) fxmlloader.getController();

        Parent root = FXMLLoader.load(
                Objects.requireNonNull(AboutDialog.class.getResource("about-dialog.fxml")));
        stage.setScene(new Scene(root));
        stage.setTitle("About Screen Shot Archive");
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setResizable(false);
        stage.initOwner( stageOfUs );

        // get the controller so we can call it with window events
        //ssCtrl = (SSController) fxmlLoader.getController();
        stage.addEventHandler( WindowEvent.ANY, new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                aboutControl.handleWindowEvent(event);
            }
        });


        printSysOut("onAbout - show about dialog");
        stage.show();
        // get the controller so we can call it with window events
        //aboutControl.SetStuffUp();

    }


    public void onGoImagesStart(ActionEvent actionEvent) {
        if ( bImagesValid ) {
            intImageIndex = 0;
            OpenImageFromList();
            setStatus("First Image Displayed");
        } else {
            ClearImage();
            setStatus("No images to display");
        }
    }

    public void onGoImageBack10(ActionEvent actionEvent) {
        if ( bImagesValid && intImageIndex >= 10 ) {
            intImageIndex = intImageIndex - 10;
            OpenImageFromList();
            setStatus("Back 10 Images Displayed");
        } else {
            onGoImagesStart( actionEvent );
        }
    }

    public void onGoImageBackOne(ActionEvent actionEvent) {
        if ( bImagesValid && intImageIndex >= 1 ) {
            intImageIndex--;
            OpenImageFromList();
            setStatus("Back 1 Image Displayed");
        } else {
            onGoImagesStart( actionEvent );
        }
    }

    public void onGoImageForwardOne(ActionEvent actionEvent) {
        if ( bImagesValid && intImageIndex < (sImageList.length -1 ) ) {
            intImageIndex++;
            OpenImageFromList();
            setStatus("Forward 1 Image Displayed");
        } else {
            onGoImagesEnd(actionEvent);
        }
    }

    public void onGoImageForward10(ActionEvent actionEvent) {
        if ( bImagesValid && intImageIndex < (sImageList.length -11 ) ) {
            intImageIndex += 10;
            OpenImageFromList();
            setStatus("Forward 10 Image Displayed");
        } else {
            onGoImagesEnd(actionEvent);
        }
    }

    public void onGoImagesEnd(ActionEvent actionEvent) {
        if ( bImagesValid ) {
            intImageIndex = sImageList.length - 1;
            OpenImageFromList();
            setStatus("Last Image Displayed");
            printSysOut("Last Image Displayed");
        } else {
            ClearImage();
            setStatus("No images to display");
        }
    }


    /*
        Handle events from the List View of pairs
     */
    public void OnListViewMouseClicked(MouseEvent mouseEvent) {

        int idx = lvScreenShotPairs.getSelectionModel().getSelectedIndex();

        printSysOut(String.format("OnListViewMouseClicked - click with Idx %d", idx) );
        if ( idx == -1 ) {
            printSysOut("OnListViewMouseClicked - No selected item");
            ClearGuiItems();
            setStatus("No pair selected");
            return;
        }
        anArchivePair = listPairs.get(idx);

        /*
            now populate the GUI items
         */
        PutGuiFromPair();
        setStatus("Pair selected");
    }

    /*
        Move a pair to the top of the list
        It must be the most frequent game you play
     */
    public void OnMovePairTop(ActionEvent actionEvent) {
        int idx = lvScreenShotPairs.getSelectionModel().getSelectedIndex();
        if ( idx == 0 ) {
            setStatus( "Pair is already at the top of the list");
            printSysOut("OnMovePairTop - already at top");
            return;
        }
        printSysOut(String.format("OnMovePairTop - moving idx %d to top", idx ) );
        SSArchivePair pair = listPairs.get(idx);
        listPairs.remove(idx);
        listPairs.addFirst(pair );
        SelectAndFocusIndex( 0);
        setStatus("Pair moved to top of list");

    }

    public void OnMovePairUp(ActionEvent actionEvent) {
        int idx = lvScreenShotPairs.getSelectionModel().getSelectedIndex();
        if ( idx == 0 ) {
            // already at top so we are done
            printSysOut(String.format("OnMovePairUp - idx %d already at top", idx ) );
            SelectAndFocusIndex( idx );
            setStatus("Pair already at top of list");
            return;
        }
        printSysOut(String.format("OnMovePairUp - moving idx %d Up one item", idx ) );
        SSArchivePair pair = listPairs.get(idx);
        listPairs.remove(idx);
        listPairs.add( idx-1, pair);
        SelectAndFocusIndex( idx-1);
        setStatus("Pair moved up one in the list");
    }

    public void OnMovePairDown(ActionEvent actionEvent) {
        int idx = lvScreenShotPairs.getSelectionModel().getSelectedIndex();
        if ( (idx+1) == listPairs.size() ) {
            // already at bottom so we are done
            printSysOut(String.format("OnMovePairDown - idx %d already at bottom", idx ) );
            SelectAndFocusIndex(idx);
            setStatus("Pair is already at the bottom of the list");
            return;
        }
        printSysOut(String.format("OnMovePairDown - moving idx %d Down one item", idx ) );
        SSArchivePair pair = listPairs.get(idx);
        listPairs.remove(idx);
        listPairs.add( idx+1, pair);
        SelectAndFocusIndex( idx+1);
        setStatus("Pair moved down one in the list");
    }

    /*
        Clean out the pair items from GUI
     */
    private void ClearGuiItems() {
        txtSelectedPairName.setText( "");
        txtSourcePath.setText("");
        txtDestPath.setText("");
        cbChooseFolderSuffix.setValue( FOLDER_SUFFIX_DEFAULT );
        txtFilePrefix.setText("");
        chkSearchSubFolders.setSelected( false );
        chkPreserveFileNames.setSelected( false );
    }

    /*
        Get and put stuff from / to the GUI with our local pair
        Always make a new pair here in case we are going to add a pair
     */
    private boolean GetPairFromGui() {

        String sPairName = txtSelectedPairName.getText();
        if ( sPairName.isEmpty()) {
            return false;
        }
        anArchivePair = new SSArchivePair();
        anArchivePair.sPairName = sPairName;
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
    private void PutGuiFromPair( ) {

        txtSelectedPairName.setText( anArchivePair.sPairName);
        txtSourcePath.setText(anArchivePair.sSourcePath);
        txtDestPath.setText(anArchivePair.sDestinationPath);
        cbChooseFolderSuffix.setValue( anArchivePair.sFolderSuffix);
        txtFilePrefix.setText(anArchivePair.sFilePrefix);
        chkSearchSubFolders.setSelected(anArchivePair.bSearchSubFolders);
        chkPreserveFileNames.setSelected(anArchivePair.bPreserveFileNames);

      }

    /*
    Make sure item of interest is selected and visible
    */
    private void SelectAndFocusIndex( int idx ) {
        lvScreenShotPairs.getSelectionModel().select(idx);
        if (!lvScreenShotPairs.isVisible() ){
            lvScreenShotPairs.getFocusModel().focus(idx);
            lvScreenShotPairs.scrollTo( idx);
        }
        lvScreenShotPairs.scrollTo( idx);
    }

    /*
        Just print some pair so we know what's up
     */
    private void PrintAPair(String sWhich, SSArchivePair pair)
    {
        printSysOut(String.format("A Pair %s with name %s",sWhich, pair.sPairName));
    }

    /*
        Assume we have just done a GetPairFromGUI so anArchivePair
        is loaded up.
     */
    private SSArchivePair MakePairForList() {
        SSArchivePair pair = new SSArchivePair();

        pair.sPairName = anArchivePair.sPairName;
        pair.sSourcePath = anArchivePair.sSourcePath;
        pair.sDestinationPath = anArchivePair.sDestinationPath;
        pair.sFolderSuffix = anArchivePair.sFolderSuffix;
        pair.sFilePrefix = anArchivePair.sFilePrefix;
        pair.bSearchSubFolders = anArchivePair.bSearchSubFolders;
        pair.bPreserveFileNames = anArchivePair.bPreserveFileNames;
        // return the manually copied pair
        return pair;
    }
    /*
        Add a new pair at the end of the list and
        then select and focus it
     */
    public void btnAddPair(ActionEvent actionEvent) {
        /*
            Need to have a pathname. Not Unique, but still
         */
        if ( !GetPairFromGui() ) {
            setStatus("Enter a path name first");
            return;
        }
        printSysOut("GetPairFromGui called");

        if ( true ) { // did getPairFromGUI mess up the list?
        /*
            Add an item to the end of the listPairs.
            We don't check for duplicate names.
            Then select and focus on the item we just
            added at the end of the list
         */
            printSysOut(("btnAddPair - Add a pair from GUI at the end of list"));
            PrintAPair("anArchivePair", anArchivePair);
            SSArchivePair aPair = MakePairForList();
            PrintAPair("aPair", aPair);
            listPairs.addLast(aPair);
        /*
            Gotta tell the ListView about the list again or once?
            Anyway, apparently every time.
         */
            lvScreenShotPairs.setItems(listPairs);
            int idx = listPairs.size() - 1;
            intImageIndex = idx;
            SelectAndFocusIndex(idx);
            setStatus("Pair added to list at end");
        }
    }

    /*
        remove a pair from the ListView
     */
    public void OnRemovePair(ActionEvent actionEvent) {

        /*
            Find the selected item in the ListView and then
            remove it from the Observable list. The Listview
            will update automatically
            This does not remove it from the store.
            Assume whole list will be saved on close, or
            user will "Save Pairs" to update the store.
         */
        int idx = lvScreenShotPairs.getSelectionModel().getSelectedIndex();
        if (idx == -1)
        {
            setStatus("Select a pair in list first");
            return;
        }
        listPairs.remove( idx );
        ClearGuiItems();
        anArchivePair.ClearPair();
        setStatus(String.format("Pair idx %d removed from list", idx) );
    }

    /*
        Replace the contents of the currently selected pair in the list
        with the contents of the GUI fields
     */
    public void OnUpdatePair(ActionEvent actionEvent) {
        /*
         Copy GUI back into the local pair, and then
         put a copy in the listPairs to update the ListView.
         */
        int idx = lvScreenShotPairs.getSelectionModel().getSelectedIndex();
        if (idx == -1)
        {
            setStatus("Select a pair in list first");
            return;
        }

        /*
            Need to have a pair name. Not Unique, but still
         */
        if ( !GetPairFromGui() ) {
            setStatus("Enter a pair name first");
            return;
        }
        /*
           Replace the currently selected item in the list
         */
        printSysOut(("btnUpdatePair - Update a selected pair in the list from the GUI"));
        PrintAPair( "anArchivePair", anArchivePair);
        /*
            GetPairFromGui actually made a new pair to avoid
            clobbering the old one, so we can use that one.
         */
        SSArchivePair aPair = anArchivePair; //MakePairForList();
        PrintAPair( "aPair", aPair );
        listPairs.remove(idx);
        listPairs.add( idx, aPair );
        /*
            Gotta tell the ListView about the list again or once?
            Anyway, apparently every time.
         */
        lvScreenShotPairs.setItems( listPairs );
        SelectAndFocusIndex( idx);
        setStatus("Pair updated in the list");
    }


    /*
        Copy Images in the Source Path, to the Destination Path with the options
     */
    public void OnCopySource(ActionEvent actionEvent) throws IOException {


        /*
        public static String  FileHelperCopySource( String sSourcePath, String sDestinationPath,
                                               String sFolderPrefix, String sFilePrefix,
                                                boolean bPreserveFileNames, boolean bSourceSubFolders )
         */
        String sSourcePath = txtSourcePath.getText();
        String sDestinationPath = txtDestPath.getText();
        String sFolderPrefix = cbChooseFolderSuffix.getValue();
        String sFilePrefix = txtFilePrefix.getText();
        boolean bPreserveFileNames = chkPreserveFileNames.isSelected();
        boolean bSearchSubFolders = chkSearchSubFolders.isSelected();

        if ( sSourcePath.isBlank() || sDestinationPath.isBlank() ) {
            setStatus("Set Source and Destination Paths to Copy Source Images");
            return;
        }
        String sCopyStatus;

        sCopyStatus = SSAFilesHelper.FileHelperCopySource( sSourcePath, sDestinationPath,
                sFolderPrefix, sFilePrefix, bPreserveFileNames, bSearchSubFolders );

        setStatus( sCopyStatus );

    }

    /*
        Call the File Helper to Delete source images from the
        game folder
     */
    public void OnDeleteSource(ActionEvent actionEvent) {
        String sSourcePath = txtSourcePath.getText();
        boolean bSearchSubFolders = chkSearchSubFolders.isSelected();

        /*
            Depend on the Enable/Disable of the buttons to not send
            us here if we don't belong. LOL
         */
        String sts = FileHelperCleanSource(  sSourcePath, bSearchSubFolders );
        setStatus( sts );

    }

    private void CloseAppAndStage() {
        /*
          Window close event never called. So do close stuff
          from here too.
         */
        AppCloseStuffToDo();
        /*
          get the scene from any GUI item, and get window from that.
          Then that's the stage and call close on it.
         */
        Stage stage = (Stage) txtSelectedPairName.getScene().getWindow();
        stage.close();
    }
    public void OnCloseAppButton(ActionEvent actionEvent) {

        printSysOut("OnCloseAppButton: closing the app");
        CloseAppAndStage();
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
        show or hide the make test pairs button
         */
        btnMakeTestPairs.setVisible( chkTestLogOnly.isSelected() );
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

        printSysOut("AppCloseStuffToDo: Save Window Pos/Size");

        /*
            The place this was done in the App class didn't work
            if a menu item or button closed the app.
         */
        Stage stage = (Stage)splitPaneOutsideContainer.getScene().getWindow();
        Preferences preferences = Preferences.userRoot().node(NODE_NAME);
        preferences.putDouble(WINDOW_POSITION_X, stage.getX());
        preferences.putDouble(WINDOW_POSITION_Y, stage.getY());
        preferences.putDouble(WINDOW_WIDTH, stage.getWidth());
        preferences.putDouble(WINDOW_HEIGHT, stage.getHeight());
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
            printSysOut("Window shown");
            OnWindowShown(event);
        }
        else if (state == WINDOW_HIDING) {
            printSysOut("Window hiding");
        }
        else if ( state == WINDOW_CLOSE_REQUEST) {
            printSysOut("Window close request");
            OnWindowCloseRequest(event);
            }
        }


    // Just a dummy action to cause txtStatus to be declared.
    public void OnKeyPressedStatus(KeyEvent keyEvent) {
        printSysOut("OnKeyPressedStatus");
    }

    /*
     * usually means that everything worked up to now
     */
    public boolean isStatusEmpty() {
        return txtStatus.getText().isEmpty();
    }




    //
    // Restore the Pair List File from Preferences
    //
    public void RestorePairsList() {

        printSysOut("RestorePairsList starting");
        /*
            All the methods to restore are in the SSArchivePair class
         */
        int numPairs = anArchivePair.GetNumberPairs();
        printSysOut(String.format("%d pairs found in store", numPairs));

        for (int i = 0; i < numPairs; i++) {
            if (!anArchivePair.GetPairFromStore(i)) {
                printSysOut(String.format("Missing pair %d", i));
            }
            else {
                printSysOut(String.format("Found Pair %d in Store", i));
                PrintAPair("anArchivePair", anArchivePair);
                SSArchivePair aPair = MakePairForList();
                listPairs.addLast(aPair);
            }
        }

        /*
            Gotta tell the ListView about the list again or once?
            Anyway, apparently every time.
            Then select and focus on first one
            ok if none, I guess.
         */
        lvScreenShotPairs.setItems(listPairs);
        int numFound = listPairs.size();
        if (numFound != 0) {
            SelectAndFocusIndex( 0);
            anArchivePair = listPairs.get( 0 );
            PutGuiFromPair();
            setStatus(String.format("%d pairs restored", numFound));
        } else {
            setStatus("No pairs found to restore");
        }
    }

    /*
        Save the list of pairs in the store
     */
    private void SavePairsList() {
        printSysOut("SavePairsList - starting");
        // see how many pairs we have to save
        int numPairs = listPairs.size();
        if (numPairs == 0 ) {
            // clear items from the store just to clean it out
            // There might be stuff left in the store, but we have
            // no pairs now.
            int oldPairs = anArchivePair.GetNumberPairs();
            if (oldPairs != 0) {
                printSysOut(String.format("No Pairs to save. Clearing Store of %d old pairs", oldPairs) );
                anArchivePair.ClearPairStore(oldPairs);
            } else {
                printSysOut("No old pairs to clear from store");
            }
            /*
                Notice that if we store fewer than we had before,
                we leave the few at the end in the store.
                Not a problem I think.
             */
            // make sure we update the store to say none
            anArchivePair.PutNumberPairs( numPairs );
            printSysOut("Store cleared");
            return;
        }
        for (int i=0; i < numPairs; i++) {
            SSArchivePair pair = listPairs.get(i);
            PrintAPair( "Storing Pair", pair);
            pair.PutPairToStore( i );
        }
        anArchivePair.PutNumberPairs( numPairs );
        printSysOut(String.format("SavePairsList - complete %d pairs", numPairs) );
    }

    /*
    Just keep this code for testing if we need it
     */
    void RestoreAPairForTesting(){
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

    /*
      store the gui to the defaults file
      Keep this code for testing.
    */
    private void SavePairsListForTesting()
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

    /*
        Use directoryChooser dialogs launched from stage.
     */
    public void OnSetDestinPath(ActionEvent actionEvent) {
        /*
            Get a path based on the last path we've seen
         */
        Stage stage = (Stage) txtSelectedPairName.getScene().getWindow();
        DirectoryChooser dirChooser = new DirectoryChooser();
        dirChooser.setTitle("Set the Destination Path");
        String lastPath = txtDestPath.getText();
        if (!lastPath.isBlank()) {
            File aFile = new File(lastPath);
            // do we have a valid path to a folder here?
            if ( aFile.isDirectory() ) {
                dirChooser.setInitialDirectory(aFile);
            }
        }
        /*
           We either set the initial path if we had one
           or we'll just go in blind and let the user
           navigate where he/she wants.
           Launch the chooser dialog and then stuff
           the result into the source path
       */
        File selDir = dirChooser.showDialog(( stage ));
        /*
            make sure we got something back otherwise just ignore it
         */
        if ( selDir != null ) {
            txtDestPath.setText(selDir.getAbsolutePath());
            setStatus("Destination Path Set");
        } else {
            setStatus("No path selected");
        }
    }

    public void OnSetSourcePath(ActionEvent actionEvent) {
        /*
            Get a path based on the last path we've seen
         */
        Stage stage = (Stage) txtSelectedPairName.getScene().getWindow();
        DirectoryChooser dirChooser = new DirectoryChooser();
        dirChooser.setTitle("Set the Source Path");
        String lastPath = txtSourcePath.getText();
        if (!lastPath.isBlank()) {
            File aFile = new File(lastPath);
            // do we have a valid path to a folder here?
            if ( aFile.isDirectory() ) {
                dirChooser.setInitialDirectory(aFile);
            }
        }
        /*
           We either set the initial path if we had one
           or we'll just go in blind and let the user
           navigate where he/she wants.
           Launch the chooser dialog and then stuff
           the result into the source path
       */
        File selDir = dirChooser.showDialog(( stage ));
        /*
            make sure we got something back otherwise just ignore it
         */
        if ( selDir != null ) {
            txtSourcePath.setText(selDir.getAbsolutePath());
            setStatus("Source Path Set");
        } else {
            setStatus("No path selected");
        }
    }


    /*
    Add some dummy pairs to test the list pair functions
     */
    static int intTestPairIdx = 0;

    private SSArchivePair FillTestPair() {
        SSArchivePair pair = new SSArchivePair();

        pair.sPairName = String.format("Pair_%d", intTestPairIdx);
        pair.sSourcePath = String.format("Source_%d", intTestPairIdx);
        pair.sDestinationPath = String.format("Destination_%d", intTestPairIdx);
        pair.sFolderSuffix = "yyyy_MM";
        pair.sFilePrefix = String.format("SOMEGAME_%d", intTestPairIdx%10);
        pair.bSearchSubFolders = (intTestPairIdx %5) == 0;
        pair.bPreserveFileNames = (intTestPairIdx %20) == 0;
        // All test pair names are different
        intTestPairIdx++;
        return pair;
    }
    public void OnMakeTestPairs(ActionEvent actionEvent) {

        if (false) {
            printSysOut("OnMakeTestPairs - make some test pairs");
            for (int i = 0; i < 10; i++) {
                listPairs.add(FillTestPair());
            }
        /*
            Update the pairs list in the ListView
         */
            lvScreenShotPairs.setItems(listPairs);
        }

        //sbImageListScrollBar.setDisable( !sbImageListScrollBar.isDisable() );
    }

    /*
      Unless we are logging, don't show make test pairs
     */
    public void OnTestLogOnly(ActionEvent actionEvent) {

        btnMakeTestPairs.setVisible( chkTestLogOnly.isSelected() );

    }


    /*
    Use DirectoryScanner to get a list of images in the specified folder
    with or without the File Prefix. Source does not use prefix, destination does
    Destination also uses the FolderSuffix based on today's date
 */
    private String[] GetImagesInFolder( String sFolder, boolean bUsePfx,
                                        boolean bUseFolderSuffix, boolean bUseSubFolders ){
        long intStartOpen = System.currentTimeMillis();
        /*
            If we are using the folder suffix, then it's based on today's date
            if it's blank, use nothing, if not, then pass it to the filehelper class
            to get a suffix string for the folder
         */
        String sFolderSuffix = "";
        if (false) {
            if (bUseFolderSuffix) {
                String sFolderSuffixCode = cbChooseFolderSuffix.getValue();
                if (!sFolderSuffixCode.isBlank()) {
                    sFolderSuffix = SSAFilesHelper.GetTodayFolderSuffix(sFolderSuffixCode);
                }
            }
        }


        /*
            we care about only three image types: *.bmp, *.jpg, *.png
            for Destination, we use the File Prefix. For source, get all images.
         */
        String sFPfx = "";
        if ( bUsePfx) {
            sFPfx = txtFilePrefix.getText();
        }

        /*
            Set things up to search for subfolders if we should
            Should only be used for Source since some games store images
            in subfolders, oddly.
         */
        String sSubPfx = "";
        if ( bUseSubFolders ) {
            if ( chkSearchSubFolders.isSelected() ) {
                sSubPfx = "**\\";
            }
        }
        /*
            Save the base path because we need it later to find
            the images. Only the file names are saved in the
            scanner result list.
         */
        sImageBasePath = sFolder+File.separator+sFolderSuffix;
        String[] saIncludeImages = new String[]{sSubPfx+sFPfx + "*.bmp",sFPfx + sSubPfx+ "*.jpg", sSubPfx+sFPfx+"*.png"  };

        DirectoryScanner scanner = new DirectoryScanner();
        scanner.setIncludes( saIncludeImages );
        scanner.setCaseSensitive( false );
        scanner.setBasedir( new File( sImageBasePath ));
        String[] files = new String[0];
        try {
            scanner.scan();
            files = scanner.getIncludedFiles();
        } catch ( IllegalStateException exc ) {
            printSysOut(String.format("GetImagesInFolder - scan failed for : %s", sImageBasePath) );
        }

        int iHowMany = files.length;

        /*
            Set the scroll bar limits to show the position in the list as
            we view images.
         */
        if ( iHowMany != 0 ) {
            sbImageListScrollBar.setMax(iHowMany - 1);
        } else {
            sbImageListScrollBar.setMax( 1 );
        }
        sbImageListScrollBar.setMin( 0 );

        printSysOut(String.format("GetImagesInFolder found %d files in %s", iHowMany, sImageBasePath));
        long intEndOpen = System.currentTimeMillis();
        printSysOut(String.format("GetImagesInFolder %d ms", intEndOpen - intStartOpen));
        return files;

    }

    /*
        Scan the Source for files. Don't use the File Prefix in the Source
     */
    public void OnViewSource(ActionEvent actionEvent) {

        String[] sImageFileNames = GetImagesInFolder(txtSourcePath.getText(), false, false, true);

        if (sImageFileNames.length > 0) {
            bImagesValid = true;
            sImageList = sImageFileNames;
            intImageIndex = sImageList.length - 1;
            onGoImagesEnd( actionEvent );
        } else {
            bImagesValid = false;
            sImageList = sImageFileNames;
            intImageIndex = 0;
            ClearImage();
        }
        setStatus(String.format("%d Source Images Found", sImageFileNames.length));
    }

    /*
        Based on the folderSuffix, search today's destination folder
     */
    public void OnViewDestination(ActionEvent actionEvent) {

        String[] sImageFileNames = GetImagesInFolder( txtDestPath.getText(), true , false, false);

        if (sImageFileNames.length > 0) {
            bImagesValid = true;
            sImageList = sImageFileNames;
            intImageIndex = sImageList.length - 1;
            onGoImagesEnd( actionEvent );
        } else {
            bImagesValid = false;
            sImageList = sImageFileNames;
            intImageIndex = 0;
            ClearImage();

        }
        setStatus(String.format("%d Destination Images Found", sImageFileNames.length));
    }

    public void ImgOnMouseDragged(MouseEvent mouseEvent) {
    }

    public void ImgOnMouseClicked(MouseEvent mouseEvent) {
    }
}
