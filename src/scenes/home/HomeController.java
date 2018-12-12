package scenes.home;

import com.jfoenix.controls.JFXTextArea;
import customNodes.ConversationView;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import models.chat.Chat;
import models.chat.ChatsList;
import models.messages.*;
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
    private ConversationView conversationView;

    @FXML private ListView<String> availableUsersListView;
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
    @FXML private AnchorPane speechContainerAnchorPane;

    //TODO send message to all
    //TODO save the conversationView instead to instantiate new one each time (in order to simply add the new message instead to add the old ones too)
    //TODO nice user list GUI
    /*
    avatarImageView.setImage(new Image("file:/home/matteoranzi/Scaricati/square1.png"));
    avatarImageView.setClip(new Circle(35, 35, 30));
    */



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

            Message message = new FileMessage(localUser.getID(), this.file.getName(), MessageType.SENT);
            currentOpenedChat.addMessage(message);
            addMessageToSpeech(message);

            messagesManager.sendFileMessage(currentOpenedChat.getUser(), message, this.file.getAbsolutePath());

            messageJFXTextArea.requestFocus();
        }
    }

    @FXML private void handleSendMessageButtonAction(ActionEvent event){
        //TODO manage "Ctrl + Enter" button to send message and "Button" to new line
        if(!messageJFXTextArea.getText().equals("")){//send the message only if there is text inside the text area
            Message message = new TextMessage(localUser.getID(), messageJFXTextArea.getText(), MessageType.SENT);
            currentOpenedChat.addMessage(message);
            addMessageToSpeech(message);

            messagesManager.sendTextMessage(currentOpenedChat.getUser(), message);

            messageJFXTextArea.setText("");//clear the content
            messageJFXTextArea.requestFocus();
        }
    }

    public void addReceivedMessage(User user, Message message){
        Chat chat = chatsList.getChat(user);
        chat.addMessage(message);

        if(currentOpenedChat == chat){
            addMessageToSpeech(message);
        }else{
            chat.incrementNotificationCounter();
            updateAvailableUserListView();
        }
    }

    private void addMessageToSpeech(Message message){
        Platform.runLater(() -> {
            if(message.getMessageType() == MessageType.RECEIVED){
                if(message instanceof TextMessage){
                    conversationView.addReceivedTextMessage(message.getText());
                }else if(message instanceof FileMessage){
                    conversationView.addReceivedFileMessage(message.getText());
                }

            }else{
                if(message instanceof TextMessage){
                    conversationView.addSentTextMessage(message.getText());
                }else if(message instanceof FileMessage){
                    conversationView.addSentFileMessage(message.getText());
                }
            }
        });
    }

    //TODO update single cell
    private void updateAvailableUserListView(){
        Platform.runLater(() -> {
            if(!usersAvailableAnchorPane.isVisible()){
                noUserAvailableAnchorPane.setVisible(false);
                usersAvailableAnchorPane.setVisible(true);
            }


            availableUsersListView.getItems().clear();//This causes GUI bug

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

    private void clearConversationView(){
        speechContainerAnchorPane.getChildren().remove(conversationView);
        conversationView = new ConversationView();

        AnchorPane.setTopAnchor(conversationView, 0.0);
        AnchorPane.setLeftAnchor(conversationView, 0.0);
        AnchorPane.setRightAnchor(conversationView, 0.0);
        AnchorPane.setBottomAnchor(conversationView, 0.0);
        speechContainerAnchorPane.getChildren().add(conversationView);
    }

    private void updateMessagesListView(Chat chat){
        Platform.runLater(() -> {
            //Clear the speech changing it
            clearConversationView();

            for(Message message: chat.getMessages()){
                addMessageToSpeech(message);
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

        clearConversationView();

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
                    messagesManager.sendTextMessage(user, new LogOutMessage(localUser.getID(), MessageType.SENT));
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
                if(currentOpenedChat == null || usersList.getUser(i) != currentOpenedChat.getUser()) { //avoid to re-open the current chat
                    log.debug("Selected User: " + usersList.getUser(i).getUsername());

                    Platform.runLater(() -> {
                        noChatSelectedAnchorPane.setVisible(false);
                        chatSelectedAnchorPane.setVisible(true);
                    });

                    User user = usersList.getUser(i);
                    if(user.getStatus() == Status.UNREACABLE){
                        disableSendMessagesInterface(true);
                    }else{
                        disableSendMessagesInterface(false);
                    }

                    currentOpenedChat = chatsList.getChat(user);
                    currentOpenedChat.resetNotificationCounter();

                    Platform.runLater(() -> {
                        userChatLabel.setText(user.getUsername());
                    });

                    updateMessagesListView(currentOpenedChat);
                    updateAvailableUserListView();
                }
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