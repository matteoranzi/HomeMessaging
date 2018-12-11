package scenes.login;

import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import scenes.home.HomeController;
import utils.Log;

import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: matteoranzi
 * Date: 27/11/18
 * Time: 0.20
 */
public class LoginController {
    public static final String WINDOW_TITLE = "HomeMessaging - Login";

    @FXML private JFXTextField usernameJFXTextField;
    @FXML private JFXTextField chatCodeJFXTextField;
    @FXML private Button loginButton;
    @FXML private AnchorPane mainAnchorPane;

    private Log log;

    public LoginController(){
        log = new Log(this.getClass(), Log.DEBUG);
    }

    @FXML private void initialize(){
        RequiredFieldValidator requiredFieldValidator = new RequiredFieldValidator();
        requiredFieldValidator.setMessage("Username cannot be empty");

        usernameJFXTextField.getValidators().add(requiredFieldValidator);

        Platform.runLater(() -> {
            usernameJFXTextField.requestFocus();
        });

        addListeners();
    }

    @FXML private void handleLoginButtonAction(ActionEvent event) throws IOException {


        if(!usernameJFXTextField.validate()){
            log.debug("username text empty");
            usernameJFXTextField.requestFocus();
        }else{
            FXMLLoader homeControllerLoader = new FXMLLoader(getClass().getResource("../home/homeView.fxml"));
            Parent homeControllerRoot = homeControllerLoader.load();//TODO handle exception

            HomeController homeController = homeControllerLoader.getController();

            Stage homeControllerStage = new Stage();
            homeController.initializeClass(homeControllerStage, usernameJFXTextField.getText(), chatCodeJFXTextField.getText());
            homeControllerStage.setScene(new Scene(homeControllerRoot));
            homeControllerStage.setTitle(HomeController.WINDOW_TITLE);
            homeControllerStage.setResizable(false);
            homeControllerStage.show();

            //Close current window
            ((Stage)((Node) event.getSource()).getScene().getWindow()).close();
        }
    }

    private void addListeners(){
        usernameJFXTextField.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            if (!newValue) {
                usernameJFXTextField.validate();
            }
        });

        JFXTextFieldInputValidation(usernameJFXTextField);
        JFXTextFieldInputValidation(chatCodeJFXTextField);
    }

    private void JFXTextFieldInputValidation(JFXTextField textField){
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[!@#$%^&*(),.?\":{}|<> ]") || !oldValue.matches("[!@#$%^&*(),.?\":{}|<> ]")) {//Remove all special characters
                textField.setText(newValue.replaceAll("[!@#$%^&*(),.?\":{}|<> ]", ""));
            }
        });
    }

}