package solution;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;


import java.net.URL;
import java.util.ResourceBundle;

public class ScreenControler implements Initializable {

    @FXML
    ImageView imageScreen;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        imageScreen = new ImageView(new Image(getClass().getResourceAsStream("Screenshot.png")));
    }
}
