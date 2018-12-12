package customNodes.SpeechBox;

import com.jfoenix.controls.JFXProgressBar;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * Created by IntelliJ IDEA.
 * User: matteoranzi
 * Date: 12/12/18
 * Time: 20.33
 */
public class FileSpeechBox extends SpeechBox {
    private String fileName;
    private JFXProgressBar progressBar;

    public FileSpeechBox(String fileName, SpeechDirection direction){
        super(direction);
        this.fileName = fileName;

        super.initialiseDefaults();
        setupElements();
        super.configure();
    }

    @Override
    protected void setupElements() {
        displayedMessage = new VBox();
        progressBar = new JFXProgressBar(JFXProgressBar.INDETERMINATE_PROGRESS);
        Label filenameLabel = new Label(this.fileName);

        ((VBox)displayedMessage).setPadding(new Insets(5));
        ((VBox)displayedMessage).setAlignment(Pos.CENTER_LEFT);
        ((VBox)displayedMessage).getChildren().addAll(filenameLabel, progressBar);

        if(direction == SpeechDirection.LEFT){
            ((VBox)displayedMessage).setBackground(DEFAULT_RECEIVER_BACKGROUND);

        }else{
            ((VBox)displayedMessage).setBackground(DEFAULT_SENDER_BACKGROUND);
        }
    }
}