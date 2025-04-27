package net.windyweather.screenshotarchive;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.util.Objects;
import java.util.prefs.Preferences;

import static net.windyweather.screenshotarchive.SSController.printSysOut;

//make a change
public class SSApplication extends Application {

    public static final String ORGANIZATION = "windyweather";
    public static final String APPLICATIONNAME = "ScreenShotArchive";
    public static final String APP_VERSION = "1.0.1";


    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(SSApplication.class.getResource("screen-shot-archive.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 700, 700);
        stage.setTitle("Screen Shot Archive");
        stage.setScene(scene);

        /*
            Stick a program icon on the window
         */
        printSysOut("Icon Setup");
        try {
            Image imgIcon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("games-icon.png")) );
            stage.getIcons().add(imgIcon);
            printSysOut("Icon Set");

        } catch ( Exception e ) {
            printSysOut("Error setting icon");
            printSysOut( e.toString() );
        }

        /*
            get the controller so we can call it with window events
            This is needed when the app is closed with the X at the top right of the window
         */
        SSController ssCtrl;
        ssCtrl = (SSController) fxmlLoader.getController();

        stage.setOnHiding( e->ssCtrl.AppCloseStuffToDo() );
        stage.show();

        /*
            Use our fancy new XML file reader to restore the window pos/size
            window.xml is added to the app name. It must not collide with the Pairs.xml file.
         */
        WindowSaveRestore.RestoreWindowPosSize( stage, ORGANIZATION, APPLICATIONNAME);

        printSysOut("App Start") ;

    }

    /*
        Great minds differ over whether this is called by the platform when a JAR is built.
        Apparently another class is required that is not JavaFx to actually start the
        program. Go see the class called JarMain for that class.
     */
    public static void main(String[] args) {
        Application.launch();
    }
}