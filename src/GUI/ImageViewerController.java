package GUI;

import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class ImageViewerController implements Initializable {
    @FXML
    private Label redPixelsLabel;
    @FXML
    private Label greenPixelsLabel;
    @FXML
    private Label bluePixelsLabel;
    @FXML
    private Label mixedPixelsLabel;
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
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private Label nameLabel;
    private static List<File> imageFiles;
    private int currentIndex;
    private Timeline slideshowTimeline;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadImagesButton.setOnAction(event -> chooseImage());
        nextImageButton.setOnAction(event -> showNextImage());
        previousImageButton.setOnAction(event -> showPreviousImage());
        startSlideshowButton.setOnAction(event -> startSlideshow());
        stopSlideshowButton.setOnAction(event -> stopSlideshow());
        libraryButton.setOnAction(event -> showLibrary());
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED); // Hide vertical scrollbar
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED); // Hide horizontal scrollbar
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

    private void startSlideshow() {
        if (slideshowTimeline != null) {
            slideshowTimeline.stop();
        }

        slideshowTimeline = new Timeline(new KeyFrame(Duration.seconds(2), event -> showNextImage()));
        slideshowTimeline.setCycleCount(Timeline.INDEFINITE);
        slideshowTimeline.play();
    }
    private void stopSlideshow() {
        if (slideshowTimeline != null) {
            slideshowTimeline.stop();
        }
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
        try {
            Image image = new Image(file.toURI().toString());
            imageView.setImage(image);
            imageView.setPreserveRatio(true);
            imageView.setFitWidth(1000);

            countColors(image);

        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Image Loading Failed");
            alert.setContentText("An error occurred while loading the image: " + e.getMessage());
            alert.showAndWait();
        }
    }
    private void countColors(Image image) {
        Task<Map<String, Integer>> countColorTasks = new Task<Map<String, Integer>>() {
            @Override
            protected Map<String, Integer> call() throws Exception {
                final int width = (int) image.getWidth();
                final int height = (int) image.getHeight();
                PixelReader pixelReader = image.getPixelReader();

                int redCount = 0, greenCount = 0, blueCount = 0, mixedCount = 0;

                for (int x = 0; x < width; x++) {
                    for (int y = 0; y < height; y++) {

                        if (isCancelled()) {
                            break;
                        }

                        Color color = pixelReader.getColor(x, y);
                        double red = color.getRed();
                        double green = color.getGreen();
                        double blue = color.getBlue();

                        if (red > green && red > blue) {
                            redCount++;
                        } else if (green > red && green > blue) {
                            greenCount++;
                        } else if (blue > red && blue > green) {
                            blueCount++;
                        } else {
                            mixedCount++;
                        }
                    }
                }

                Map<String, Integer> result = new HashMap<>();
                result.put("Red", redCount);
                result.put("Green", greenCount);
                result.put("Blue", blueCount);
                result.put("Mixed", mixedCount);

                return result;
            }
        };

        countColorTasks.setOnSucceeded(e -> {
            Map<String, Integer> result = countColorTasks.getValue();
            updateUI(result);
        });

        countColorTasks.setOnFailed(e -> {
            Throwable problem = countColorTasks.getException();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error: " + problem.getMessage());
            alert.showAndWait();
        });

        new Thread(countColorTasks).start();
    }
    private void updateUI(Map<String, Integer> result) {
        Platform.runLater(() -> {
            redPixelsLabel.setText("Red Pixels: " + result.get("Red"));
            greenPixelsLabel.setText("Green Pixels: " + result.get("Green"));
            bluePixelsLabel.setText("Blue Pixels: " + result.get("Blue"));
            mixedPixelsLabel.setText("Mixed Pixels: " + result.get("Mixed"));
        });
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
