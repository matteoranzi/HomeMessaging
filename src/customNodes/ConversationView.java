package customNodes;

import customNodes.SpeechBox.FileSpeechBox;
import customNodes.SpeechBox.SpeechDirection;
import customNodes.SpeechBox.TextSpeechBox;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import utils.Log;

public class ConversationView extends AnchorPane {
    private ObservableList<Node> speechBubbles = FXCollections.observableArrayList();
    private ScrollPane messageScroller;
    private VBox messageContainer;

    Log log;

    public ConversationView(){
        log = new Log(this.getClass(), Log.DEBUG);
        setupElements();
    }

    private void setupElements(){
        setupMessageDisplay();

        AnchorPane.setTopAnchor(messageScroller, 0.0);
        AnchorPane.setLeftAnchor(messageScroller, 0.0);
        AnchorPane.setRightAnchor(messageScroller, 0.0);
        AnchorPane.setBottomAnchor(messageScroller, 0.0);

        getChildren().setAll(messageScroller);
    }


    private void setupMessageDisplay(){
        messageContainer = new VBox(5);
        Bindings.bindContentBidirectional(speechBubbles, messageContainer.getChildren());

        messageScroller = new ScrollPane(messageContainer);
        messageScroller.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        messageScroller.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        messageScroller.setPrefHeight(300);
        messageScroller.prefWidthProperty().bind(messageContainer.prefWidthProperty().subtract(5));
        messageScroller.setFitToWidth(true);
        //Make the scroller scroll to the bottom when a new message is added
        speechBubbles.addListener((ListChangeListener<Node>) change -> {
            while (change.next()) {
                if(change.wasAdded()){
                    messageScroller.setVvalue(messageScroller.getVmax());
                }
            }
        });
    }

    public void addSentTextMessage(String message){
        log.debug("Adding right message");
        speechBubbles.add(new TextSpeechBox(message, SpeechDirection.RIGHT));
    }

    public void addReceivedTextMessage(String message){
        log.debug("Adding left message");
        speechBubbles.add(new TextSpeechBox(message, SpeechDirection.LEFT));
    }

    public void addSentFileMessage(String fileName){
        speechBubbles.add(new FileSpeechBox(fileName, SpeechDirection.RIGHT));
    }

    public void addReceivedFileMessage(String filename){
        speechBubbles.add(new FileSpeechBox(filename, SpeechDirection.LEFT));
    }
}