import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Timer;

public class UserApp extends Application {

    @Override
    public void start(Stage stage) {
        try {
            System.out.println("loading root...");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("GUI.fxml"));
            System.out.println("loading root...");

            Parent root = loader.load();
            System.out.println("loading controller...");


            User user = new User();
            user.setController(loader.getController());
            user.setGsmNummer(102);
            user.setNaam("JEOF");
            user.connectToServer();

            stage.setTitle("User: "+user.getNaam());
            stage.setScene(new Scene(root, 500, 250));
            stage.setResizable(false);
            stage.show();
            Platform.setImplicitExit(true);
            System.out.println("GUI opgestart");


            user.retrieveMyTokens();

            //start up daily tasks
            LocalTime midnight = LocalTime.MIDNIGHT;
            LocalDate today = LocalDate.now();
            LocalDateTime localDateTime = LocalDateTime.of(today,midnight);
            LocalDateTime daily = localDateTime.minusMinutes(5);
            Timer timer = new Timer();
            timer.schedule(new TimedTaskDailyUser(user), Date.from(daily.atZone(ZoneId.systemDefault()).toInstant()), 86400000);

            System.out.print(LocalDateTime.now().toString());
            user.shareLogs();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void stop() {
        System.exit(0);
    }
}

