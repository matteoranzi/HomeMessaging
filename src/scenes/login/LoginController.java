package scenes.login;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

/**
 * Created by IntelliJ IDEA.
 * User: matteoranzi
 * Date: 27/11/18
 * Time: 0.20
 */
public class LoginController {
    @FXML private TextField usernameTextField;
    @FXML private Button loginButton;

    @FXML private void initialize(){
        Platform.runLater(() -> loginButton.requestFocus());
    }

}