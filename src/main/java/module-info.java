module net.windyweather.screenshotarchive {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;

    opens net.windyweather.screenshotarchive to javafx.fxml;
    exports net.windyweather.screenshotarchive;
}