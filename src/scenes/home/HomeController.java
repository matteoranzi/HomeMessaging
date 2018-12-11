package scenes.home;

import com.jfoenix.controls.JFXTextArea;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import models.chat.Chat;
import models.chat.ChatsList;
import models.messages.LogOutMessage;
import models.messages.Message;
import models.messages.MessageType;
import models.messages.TextMessage;
import models.user.Status;
import network.HelloListener;
import network.HelloMessageSender;
import network.MessagesManager;
import utils.Log;
import utils.exceptions.NotInitializedClassException;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import models.user.User;
import models.user.UsersList;

import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: matteoranzi
 * Date: 27/11/18
 * Time: 22.50
 */
public class HomeController {
    public static final String WINDOW_TITLE = "HomeMessaging - Home";

    private boolean initializedClass = false;
    private Log log;

    private HelloListener helloListener;
    private MessagesManager messagesManager;

    private User localUser;
    private UsersList usersList;
    private ChatsList chatsList;

    private Chat currentOpenedChat;

    private FileChooser fileChooser;
    private File file;
    private File storedFileDirectory;

    private Stage stage;

    @FXML private ListView<String> availableUsersListView;
    @FXML private ListView<String> messagesListView;
    @FXML private Button sendMessageButton;
    @FXML private Button sendFileButton;
    @FXML private JFXTextArea messageJFXTextArea;
    @FXML private AnchorPane noUserAvailableAnchorPane;
    @FXML private AnchorPane noChatSelectedAnchorPane;
    @FXML private AnchorPane chatSelectedAnchorPane;
    @FXML private AnchorPane usersAvailableAnchorPane;
    @FXML private Label usernameAtChatCodeLabel;
    @FXML private Label userChatLabel;
    @FXML private AnchorPane mainAnchorPane;
    @FXML private BorderPane fileBorderPane;
    @FXML private BorderPane messageBorderPane;

    //TODO send message to all
    //TODO disable sendFileButton when host unreachable


    @FXML private void initialize(){
        chatSelectedAnchorPane.setVisible(false);
        usersAvailableAnchorPane.setVisible(false);

        addListeners();
    }

    @FXML private void handleSendFileButtonAction(ActionEvent event){
        log.debug("choose file");

        if(this.file != null){
            fileChooser.setInitialDirectory(this.file.getAbsoluteFile().getParentFile());
        }
        File tmpFile = fileChooser.showOpenDialog(((Node)event.getSource()).getScene().getWindow());//or use «new Stage()» ?
        if(tmpFile != null){
            this.file = tmpFile;
            log.debug("Selected file: " + file.getAbsolutePath());
        }
    }

    @FXML private void handleSendMessageButtonAction(ActionEvent event){
        //TODO manage "Enter" button to send message and "Ctrl + Button" to new line
        if(!messageJFXTextArea.getText().equals("")){//send the message only if there is text inside the text area
            Message message = new TextMessage(localUser.getID(), messageJFXTextArea.getText(), MessageType.SENT);
            currentOpenedChat.addMessage(message);
            addMessageListView(message);

            messagesManager.sendMessage(currentOpenedChat.getUser(), message);

            messageJFXTextArea.setText("");//clear the content
            messageJFXTextArea.requestFocus();
        }
    }

    public void addReceivedMessage(User user, Message message){
        Chat chat = chatsList.getChat(user);
        chat.addMessage(message);

        if(currentOpenedChat == chat){
            addMessageListView(message);
        }else{
            chat.incrementNotificationCounter();
            updateAvailableUserListView();
        }
    }

    private void addMessageListView(Message message){
        Platform.runLater(() -> {
            if(message.getMessageType() == MessageType.RECEIVED){
                messagesListView.getItems().add("RECEIVED --> " + message.getText());

            }else{
                messagesListView.getItems().add("SENT --> " + message.getText());
            }
        });
    }

    //TODO update single cell
    private void updateAvailableUserListView(){
        Platform.runLater(() -> {
            noUserAvailableAnchorPane.setVisible(false);
            usersAvailableAnchorPane.setVisible(true);

            availableUsersListView.getItems().clear();

            for(User user: usersList.getUsers()){
                Chat chat = chatsList.getChat(user);
                String s;
                if(user.getStatus() == Status.UNREACABLE){
                    s = "[U] ";
                }else{
                    s = "[R] ";
                }

                if(chat.getNotificationCounter() > 0){
                    availableUsersListView.getItems().add(s + user.getUsername() + " - " + chat.getNotificationCounter());
                }else{
                    availableUsersListView.getItems().add(s + user.getUsername());
                }
            }
        });
    }

    private void updateMessagesListView(Chat chat){
        Platform.runLater(() -> {
            messagesListView.getItems().clear();
            for(Message message: chat.getMessages()){
                addMessageListView(message);
            }
        });
    }

    public void removeUserFromList(User user){
        //TODO choose if remove or not the user from the list
    }

    public void addUserToList(User user){
        checkIfInitialized();

        if(usersList.addUser(user) == !UsersList.USER_ALREADY_PRESENT){
            chatsList.createChat(user);

            updateAvailableUserListView();
        }
    }

    public void initializeClass(Stage stage, String username, String chatCode){
        this.stage = stage;
        log = new Log(this.getClass(), Log.DEBUG);
        //No chatCode specified
        if(chatCode.equals("")){
            localUser = new User(username, User.DEFAULT_CHAT_CODE, null);
        }else{
            localUser = new User(username, chatCode, null);
        }

        usernameAtChatCodeLabel.setText(localUser.getUsername() + "@" + localUser.getChatCode());

        fileChooser = new FileChooser();

        usersList = new UsersList();
        chatsList = new ChatsList();
        currentOpenedChat = null;

        helloListener = new HelloListener(this, localUser);
        messagesManager = new MessagesManager(this, localUser);

        //TODO create nice GUI during servers initialization (if needed)
        helloListener.start();
        messagesManager.start();

        sendHelloPacket();

        stage.setOnCloseRequest(windowEvent -> {
            log.debug("Sending logout messages...");
            for(User user: usersList.getUsers()){
                if(user.getStatus() == Status.REACHABLE){
                    log.debug("LogOutMessage to: " + user.getUsername());
                    messagesManager.sendMessage(user, new LogOutMessage(localUser.getID(), MessageType.SENT));
                }
            }
        });

        initializedClass = true;
    }

    private void sendHelloPacket(){
        //send the hello packet once the servers are ready to receive responses
        Thread t = new Thread(() -> {
            while (!helloListener.isReady() || !messagesManager.isReady());

            HelloMessageSender helloMessageSender = new HelloMessageSender(localUser.getID());
            helloMessageSender.start();
        });
        t.setDaemon(true);
        t.start();
    }

    public void updateChatView(){
        updateAvailableUserListView();

        if(currentOpenedChat != null && currentOpenedChat.getUser().getStatus() == Status.UNREACABLE){
            disableSendMessagesInterface(true);
        }

    }

    private void disableSendMessagesInterface(boolean e){
        Platform.runLater(() -> {
            messageJFXTextArea.setDisable(e);
            sendMessageButton.setDisable(e);
            sendFileButton.setDisable(e);
        });
    }

    private void addListeners(){
        availableUsersListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            int i = availableUsersListView.getSelectionModel().getSelectedIndex();
            if(i >= 0){
                log.debug("Selected User: " + usersList.getUser(i).getUsername());

                noChatSelectedAnchorPane.setVisible(false);
                chatSelectedAnchorPane.setVisible(true);

                User user = usersList.getUser(i);
                if(user.getStatus() == Status.UNREACABLE){
                    disableSendMessagesInterface(true);
                }else{
                    disableSendMessagesInterface(false);
                }

                currentOpenedChat = chatsList.getChat(user);
                currentOpenedChat.resetNotificationCounter();

                userChatLabel.setText(user.getUsername());

                updateMessagesListView(currentOpenedChat);
                updateAvailableUserListView();
            }
        });

        messageJFXTextArea.textProperty().addListener((observable, oldValue, newValue) -> {
            if(messageJFXTextArea.getText().equals("")){
                messageBorderPane.setVisible(false);
                fileBorderPane.setVisible(true);
            }else{
                messageBorderPane.setVisible(true);
                fileBorderPane.setVisible(false);
            }
        });
    }

    public UsersList getUsersList(){
        return usersList;
    }

    private void checkIfInitialized(){
        if(!initializedClass) try {
            throw new NotInitializedClassException("HomeController class not initialized");
        } catch (NotInitializedClassException e) {
            e.printStackTrace();
        }
    }
}