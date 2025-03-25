module net.windyweather.screenshotarchive {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;
    requires java.desktop;
    requires java.prefs;

    opens net.windyweather.screenshotarchive to javafx.fxml;
    exports net.windyweather.screenshotarchive;
}