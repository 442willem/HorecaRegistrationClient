import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.*;


public class Controller {
    @FXML
    private Button EnterFacility;
    @FXML
    private Button LeaveFacility;
    @FXML
    private TextField DataString;
    @FXML
    private ImageView Image;

    User user;

    public void initialize() {
        System.out.println("initialising controller");
        EnterFacility.setOnAction(new EnterButtonListener());
        LeaveFacility.setOnAction(new LeaveButtonListener());
    }

    public Controller() {

    }

    public void setUser(User u){
        user=u;
    }

    public void updateImage(WritableImage i) {
        Image.setImage(i);
        Image.setX(10);
        Image.setY(10);
        Image.setFitWidth(575);
        Image.setPreserveRatio(true);
    }

    private class EnterButtonListener implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent e) {
            user.scanQR(DataString.getText());
        }
    }
    private class LeaveButtonListener implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent e) {
            user.leaveCateringFacility();
        }
    }
}
