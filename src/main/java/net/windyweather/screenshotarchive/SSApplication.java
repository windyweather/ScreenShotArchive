package net.windyweather.screenshotarchive;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.util.prefs.Preferences;

import static net.windyweather.screenshotarchive.SSController.printSysOut;

//make a change
public class SSApplication extends Application {


    public static final String WINDOW_POSITION_X = "Window_Position_X";
    public static final String WINDOW_POSITION_Y = "Window_Position_Y";
    public static final String WINDOW_WIDTH = "Window_Width";
    public static final String WINDOW_HEIGHT = "Window_Height";
    private static final double DEFAULT_X = 10;
    private static final double DEFAULT_Y = 10;
    private static final double DEFAULT_WIDTH = 800;
    private static final double DEFAULT_HEIGHT = 800;
    public static final String NODE_NAME = "ScreenShotArchive";
    //private static final String BUNDLE = "Bundle";

    SSController ssCtrl;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(SSApplication.class.getResource("screen-shot-archive.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 700, 700);
        stage.setTitle("Screen Shot Archive");
        stage.setScene(scene);

        // get the controller so we can call it with window events
        ssCtrl = (SSController) fxmlLoader.getController();
        stage.addEventHandler( WindowEvent.ANY, new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                ssCtrl.handleWindowEvent(event);
            }
        });
        stage.show();
        /* very cool way to save and restore window size and position.
          David Bell's blog
          Found at: https://broadlyapplicable.blogspot.com/2015/02/javafx-restore-window-size-position.html
          Thanks David Bell from February 23, 2015
          JavaFX Restore Window Size & Position
         */
        // Pull the saved preferences and set the stage size and start location

        Preferences pref = Preferences.userRoot().node(NODE_NAME);
        double x = pref.getDouble(WINDOW_POSITION_X, DEFAULT_X);
        double y = pref.getDouble(WINDOW_POSITION_Y, DEFAULT_Y);
        double width = pref.getDouble(WINDOW_WIDTH, DEFAULT_WIDTH);
        double height = pref.getDouble(WINDOW_HEIGHT, DEFAULT_HEIGHT);
        stage.setX(x);
        stage.setY(y);
        stage.setWidth(width);
        stage.setHeight(height);

        printSysOut(String.format("App Start: Restore Window Pos/Size  [%.0f,%.0f] / [%.0f,%.0f]", x,y, width, height) );

        /*
          Wake up the controller to restore the pairs
         */
        printSysOut("SSApplication:start - calling SSController:RestorePairsList");
        ssCtrl.RestorePairsList();
        printSysOut("SSApplication:start - back from SSController:RestorePairsList");

        // When the stage closes store the current size and window location.

        stage.setOnCloseRequest((final WindowEvent event) -> {

            printSysOut("stage.setOnCloseRequest: Save Window Pos/Size");
            Preferences preferences = Preferences.userRoot().node(NODE_NAME);
            preferences.putDouble(WINDOW_POSITION_X, stage.getX());
            preferences.putDouble(WINDOW_POSITION_Y, stage.getY());
            preferences.putDouble(WINDOW_WIDTH, stage.getWidth());
            preferences.putDouble(WINDOW_HEIGHT, stage.getHeight());
        });
    }


    public static void main(String[] args) {
        Application.launch();
    }
}