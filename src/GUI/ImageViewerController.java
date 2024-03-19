package GUI;

import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ImageViewerController implements Initializable {
    @FXML
    private MFXButton loadImagesButton;
    @FXML
    private ImageView imageView;
    @FXML
    private MFXButton nextImageButton;
    @FXML
    private MFXButton previousImageButton;
    @FXML
    private MFXButton startSlideshowButton;
    @FXML
    private MFXButton stopSlideshowButton;
    @FXML
    private MFXButton libraryButton;
    private static List<File> imageFiles;
    private int currentIndex;
    private Timeline slideshowTimeline;
    @FXML
    private Label nameLabel;



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadImagesButton.setOnAction(event -> chooseImage());
        nextImageButton.setOnAction(event -> showNextImage());
        previousImageButton.setOnAction(event -> showPreviousImage());
        startSlideshowButton.setOnAction(event -> startSlideshow());
        stopSlideshowButton.setOnAction(event -> stopSlideshow());
        libraryButton.setOnAction(event -> showLibrary());
    }

    private void showLibrary() {
        try {
            ListView<File> imagesListview = new ListView<>();
            ObservableList<File> observableImagesList = FXCollections.observableList(imageFiles);
            imagesListview.setItems(observableImagesList);

            VBox root = new VBox(imagesListview);
            Scene scene = new Scene(root, 400, 300); // Adjust width and height as needed

            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Image Library");
            stage.show();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("No images chosen");
            alert.showAndWait();

        }
    }

    private void stopSlideshow() {
        if (slideshowTimeline != null) {
            slideshowTimeline.stop();
        }
    }

    private void startSlideshow() {
        if (slideshowTimeline != null) {
            slideshowTimeline.stop();
        }

        slideshowTimeline = new Timeline(new KeyFrame(Duration.seconds(2), event -> showNextImage()));
        slideshowTimeline.setCycleCount(Timeline.INDEFINITE);
        slideshowTimeline.play();
    }

    private void chooseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif"));
        imageFiles = fileChooser.showOpenMultipleDialog(null);
        if (imageFiles != null && !imageFiles.isEmpty()) {
            currentIndex = 0;
            loadImage(imageFiles.get(currentIndex));
        }
    }

    private void loadImage(File file) {
        Image image = new Image(file.toURI().toString());
        imageView.setImage(image);

        nameLabel.setText(file.getName());

    }

    private void showNextImage() {
        if (imageFiles != null && !imageFiles.isEmpty()) {
            currentIndex = (currentIndex + 1) % imageFiles.size();
            loadImage(imageFiles.get(currentIndex));
        }
    }
    private void showPreviousImage() {
        if (imageFiles != null && !imageFiles.isEmpty()) {
            currentIndex = (currentIndex - 1 + imageFiles.size()) % imageFiles.size();
            loadImage(imageFiles.get(currentIndex));
        }
    }

}
