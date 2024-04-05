package GUI;

import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
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
import java.util.List;
import java.util.ResourceBundle;

public class ImageViewerController implements Initializable {
    public Label redPixelsLabel;
    public Label greenPixelsLabel;
    public Label bluePixelsLabel;
    public Label mixedPixelsLabel;
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
    private Thread slideshowThread;

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
        if (slideshowThread != null && slideshowThread.isAlive()) {
            slideshowThread.interrupt();
        }
    }

    private void startSlideshow() {
        stopSlideshow();

        slideshowThread = new Thread(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    Platform.runLater(() -> showNextImage());
                    Thread.sleep(1500);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        slideshowThread.start();
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

        countColors(image);
    }

    private void countColors(Image image) {
        new Thread(() -> {
            final int height = (int) image.getHeight();
            final int width = (int) image.getWidth();
            PixelReader pixelReader = image.getPixelReader();

            int redCount = 0, greenCount = 0, blueCount = 0, mixedCount = 0;

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
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

                    final int finalRedCount = redCount;
                    final int finalGreenCount = greenCount;
                    final int finalBlueCount = blueCount;
                    final int finalMixedCount = mixedCount;

                    Platform.runLater(() -> {
                        redPixelsLabel.setText("Red Pixels: " + finalRedCount);
                        greenPixelsLabel.setText("Green Pixels: " + finalGreenCount);
                        bluePixelsLabel.setText("Blue Pixels: " + finalBlueCount);
                        mixedPixelsLabel.setText("Mixed Pixels: " + finalMixedCount);
                    });
                }
            }
        }).start();
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
