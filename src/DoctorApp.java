import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.time.LocalDateTime;

public class DoctorApp extends Application{

    @Override
    public void start(Stage stage) {
        try
        {
            Doctor doctor = new Doctor();
            doctor.connectToServer();
            System.out.println("reading logs");
            doctor.readLogs();
            System.out.println("sending logs");
            doctor.sendLogs();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void stop(){
        System.exit(0);
    }
}
