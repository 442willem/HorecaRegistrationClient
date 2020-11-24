import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.time.LocalDateTime;

public class CateringFacilityApp extends Application{

    @Override
    public void start(Stage stage) {
        try
        {
            CateringFacility cateringFacility1 =new CateringFacility();
            cateringFacility1.setUniqueIDCF("KastartBVBA");
            cateringFacility1.setLocation("Onderbergen 42, 9000 Gent");
            cateringFacility1.connectToServer();


            cateringFacility1.getDailySecret();
            cateringFacility1.getDailyNym();
            cateringFacility1.generateQRcode();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void stop(){
        System.exit(0);
    }
}
