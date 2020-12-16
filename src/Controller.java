import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
    @FXML
    private Label InfectedLabel;
    @FXML
    private Button FlushCapsules;
    @FXML
    private Button CheckCritical;

    User user;

    public void initialize() {
        System.out.println("initialising controller");
        EnterFacility.setOnAction(new EnterButtonListener());
        LeaveFacility.setOnAction(new LeaveButtonListener());
        LeaveFacility.setVisible(false);
        ShareLogs.setOnAction(new ShareLogsButtonListener());
        FlushCapsules.setOnAction(new FlushCapsulesButtonListener());
        CheckCritical.setOnAction(new CheckCriticalButtonListener());
    }

    public Controller() {

    }

    public void setUser(User u) {
        user = u;
    }

    public void updateImage(Image i) {
        Image.setImage(i);
        Image.setFitWidth(100);
        Image.setPreserveRatio(true);
    }

    public void setInfected(){
        InfectedLabel.setVisible(true);
    }

    private class EnterButtonListener implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent e) {
            user.scanQR(DataString.getText());
            EnterFacility.setVisible(false);
            LeaveFacility.setVisible(true);
            ShareLogs.setVisible(false);
            DataString.clear();
            DataString.setVisible(false);
        }
    }

    private class LeaveButtonListener implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent e) {
            user.leaveCateringFacility();
            EnterFacility.setVisible(true);
            LeaveFacility.setVisible(false);
            ShareLogs.setVisible(true);
            DataString.setVisible(true);
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
    private class FlushCapsulesButtonListener implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent e) {
            user.flushCapsules();
        }
    }
    private class CheckCriticalButtonListener implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent e) {
            user.fetchCriticalLogs();
        }
    }
}
