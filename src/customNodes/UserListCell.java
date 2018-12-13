package customNodes;

import javafx.geometry.Pos;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Callback;
import models.user.Status;
import models.user.User;
import scenes.home.HomeController;
import utils.Log;

/**
 * Created by IntelliJ IDEA.
 * User: matteoranzi
 * Date: 13/12/18
 * Time: 2.13
 */
public class UserListCell implements Callback<ListView<User>, ListCell<User>>{
    private Log log;
    private HomeController homeController;

    public UserListCell(HomeController homeController){
        this.homeController = homeController;
        this.log = new Log(this.getClass(), Log.DEBUG);
    }

    @Override
    public ListCell<User> call(ListView<User> p) {
        ListCell<User> cell = new ListCell<>(){
            @Override
            protected void updateItem(User user, boolean empty) {
                super.updateItem(user, empty);

                setGraphic(null);
                setText(null);

                if(user != null){
                    HBox hBox = new HBox();
                    Text username = new Text(user.getUsername());
                    username.setFill(Color.WHITE);

                    ImageView statusImageView = new ImageView();
                    Image statusImage;
                    if(user.getStatus() == Status.REACHABLE){
                        statusImage = new Image(getClass().getResource("/resources/images/reachable.png").toString(), 16, 16, true, true);
                       }else{
                        statusImage = new Image(getClass().getResource("/resources/images/unreachable.png").toString(), 16, 16, true, true);
                    }
                    statusImageView.setImage(statusImage);

                    ImageView pictureImageView = new ImageView();
                    Image userImage = new Image(getClass().getResource("/resources/images/default.png").toString(), 40, 40, true, true);
                    pictureImageView.setImage(userImage);

                    int notificationCounter = homeController.getChatsList().getChat(user).getNotificationCounter();

                    if(notificationCounter > 0){
                        HBox notificationCounteHBox = new HBox();

                        Circle circle = new Circle(10);
                        circle.setFill(Color.valueOf("#ff7400"));
                        circle.setStroke(Color.TRANSPARENT);

                        Text counterText = new Text(Integer.toString(notificationCounter));
                        counterText.setFill(Color.WHITE);

                        StackPane notificationCounterStackPane = new StackPane();
                        notificationCounterStackPane.getChildren().addAll(circle, counterText);

                        notificationCounteHBox.getChildren().addAll(notificationCounterStackPane);
                        notificationCounteHBox.setAlignment(Pos.CENTER_RIGHT);
                        HBox.setHgrow(notificationCounteHBox, Priority.ALWAYS);

                        hBox.getChildren().addAll(statusImageView, pictureImageView, username, notificationCounteHBox);
                    }else{
                        hBox.getChildren().addAll(statusImageView, pictureImageView, username);
                    }
                    hBox.setAlignment(Pos.CENTER_LEFT);
                    hBox.setSpacing(5);
                    setGraphic(hBox);
                }
            }
        };

        return cell;
    }
}