package customNodes.SpeechBox;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;

public class TextSpeechBox extends SpeechBox {
    private String message;

    public TextSpeechBox(String message, SpeechDirection direction){
        super(direction);
        this.message = message;

        super.initialiseDefaults();
        setupElements();
        super.configure();
    }

    @Override
    protected void setupElements() {
        displayedMessage = new Label(message);

        ((Label)displayedMessage).setPadding(new Insets(5));
        ((Label)displayedMessage).setWrapText(true);

        if(direction == SpeechDirection.LEFT){
            ((Label)displayedMessage).setBackground(DEFAULT_RECEIVER_BACKGROUND);
            ((Label)displayedMessage).setAlignment(Pos.CENTER_LEFT);
        }else{
            ((Label)displayedMessage).setBackground(DEFAULT_SENDER_BACKGROUND);
            ((Label)displayedMessage).setAlignment(Pos.CENTER_RIGHT);
        }
    }
}