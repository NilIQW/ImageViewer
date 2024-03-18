package GUI;

import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

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
    private List<File> imageFiles;
    private int currentIndex;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadImagesButton.setOnAction(event -> chooseImage());
        nextImageButton.setOnAction(event -> showNextImage());
        previousImageButton.setOnAction(event -> showPreviousImage());

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
