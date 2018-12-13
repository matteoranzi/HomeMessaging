package customNodes.SpeechBox;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;

/**
 * Created by IntelliJ IDEA.
 * User: matteoranzi
 * Date: 11/12/18
 * Time: 21.56
 */
public abstract class SpeechBox extends HBox {
    protected Color DEFAULT_SENDER_COLOR = Color.valueOf("#FFFFFF");
    protected Color DEFAULT_RECEIVER_COLOR = Color.valueOf("#ffa625");
    protected Background DEFAULT_SENDER_BACKGROUND, DEFAULT_RECEIVER_BACKGROUND;

    protected SpeechDirection direction;
    protected Node displayedMessage;
    protected SVGPath directionIndicator;

    protected abstract void setupElements();

    public SpeechBox(SpeechDirection direction){
        this.direction = direction;
    }

    protected void initialiseDefaults(){
        DEFAULT_SENDER_BACKGROUND = new Background(
                new BackgroundFill(DEFAULT_SENDER_COLOR, new CornerRadii(5,0,5,5,false), Insets.EMPTY));
        DEFAULT_RECEIVER_BACKGROUND = new Background(
                new BackgroundFill(DEFAULT_RECEIVER_COLOR, new CornerRadii(0,5,5,5,false), Insets.EMPTY));
    }

    protected void configure(){
        HBox container;
        directionIndicator = new SVGPath();

        if(direction == SpeechDirection.LEFT){
            directionIndicator.setContent("M0 0 L10 0 L10 10 Z");
            directionIndicator.setFill(DEFAULT_RECEIVER_COLOR);

            container = new HBox(directionIndicator, displayedMessage);
            //Use at most 75% of the width provided to the TextSpeechBox for displaying the message
            container.maxWidthProperty().bind(widthProperty().multiply(0.75));
            getChildren().setAll(container);
            setAlignment(Pos.CENTER_LEFT);
        }
        else{
            directionIndicator.setContent("M10 0 L0 10 L0 0 Z");
            directionIndicator.setFill(DEFAULT_SENDER_COLOR);

            container = new HBox(displayedMessage, directionIndicator);
            //Use at most 75% of the width provided to the TextSpeechBox for displaying the message
            container.maxWidthProperty().bind(widthProperty().multiply(0.75));
            getChildren().setAll(container);
            setAlignment(Pos.CENTER_RIGHT);
        }
    }
}