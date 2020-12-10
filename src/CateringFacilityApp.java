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

public class CateringFacilityApp extends Application{

    @Override
    public void start(Stage stage) {
        try
        {
            CateringFacility cateringFacility1 =new CateringFacility();
            cateringFacility1.setUniqueIDCF("KastartBVBA");
            cateringFacility1.setLocation("Onderbergen 42, 9000 Gent");
            cateringFacility1.connectToServer();


            /*cateringFacility1.getDailySecret();
            cateringFacility1.getDailyNym();
            cateringFacility1.generateQRcode();*/

            //start up daily tasks
            LocalTime midnight = LocalTime.MIDNIGHT;
            LocalDate today = LocalDate.now();
            LocalDateTime localDateTime = LocalDateTime.of(today,midnight);
            Timer timer = new Timer();
            timer.schedule(new TimedTaskDailyCF(cateringFacility1), Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant()), 86400000);


        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void stop(){
        System.exit(0);
    }
}
