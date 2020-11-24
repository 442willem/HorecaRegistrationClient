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
    private Button ShareLogs;
    @FXML
    private TextField DataString;
    @FXML
    private ImageView Image;

    User user;

    public void initialize() {
        System.out.println("initialising controller");
        EnterFacility.setOnAction(new EnterButtonListener());
        LeaveFacility.setOnAction(new LeaveButtonListener());
        LeaveFacility.setVisible(false);
        ShareLogs.setOnAction(new ShareLogsButtonListener());
    }

    public Controller() {

    }

    public void setUser(User u) {
        user = u;
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
            EnterFacility.setVisible(false);
            LeaveFacility.setVisible(true);
            ShareLogs.setVisible(false);
            DataString.clear();
        }
    }

    private class LeaveButtonListener implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent e) {
            user.leaveCateringFacility();
            EnterFacility.setVisible(true);
            LeaveFacility.setVisible(false);
            ShareLogs.setVisible(true);
        }
    }

    private class ShareLogsButtonListener implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent e) {
            user.shareLogs();
            EnterFacility.setVisible(true);
            LeaveFacility.setVisible(false);
            ShareLogs.setVisible(true);
        }
    }
}
