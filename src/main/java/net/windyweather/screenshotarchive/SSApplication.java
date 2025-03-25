package net.windyweather.screenshotarchive;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
//make a change
public class SSApplication extends Application {

    SSController ssctrl;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(SSApplication.class.getResource("screen-shot-archive.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 700, 700);
        stage.setTitle("ScreenShotArchive");
        stage.setScene(scene);

        // get the controller so we can call it with window events
        ssctrl = (SSController) fxmlLoader.getController();
        stage.addEventHandler( WindowEvent.ANY, new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                ssctrl.handleWindowEvent(event);
            }
        });
        stage.show();

    }

    public static void main(String[] args) {
        launch();
    }
}