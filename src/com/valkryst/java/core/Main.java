package core;

import controller.MainScreenController;
import eu.hansolo.enzo.notification.Notification;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    public static void main(final String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setMinWidth(850);
        primaryStage.setMinHeight(520);

        Notification.Notifier.setPopupLocation(primaryStage, Pos.BOTTOM_CENTER);

        final MainScreenController controller = new MainScreenController(primaryStage);

        final Scene scene = new Scene(controller.getView());

        scene.getStylesheets().add("global.css");
        scene.getRoot().getStyleClass().add("main-root");

        primaryStage.setTitle("Dynamic Dialogue Writing Program");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
