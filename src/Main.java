import GUI.ImageViewerController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/GUI/ImageViewer.fxml"));
        Parent root = loader.load();

        ImageViewerController imageViewerController = loader.getController();

        Scene scene = new Scene(root);
        scene.getStylesheets().add("GUI/CSS/stylesheet.css");
        primaryStage.setScene(scene);

        primaryStage.show();
    }
}