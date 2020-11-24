import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.time.LocalDateTime;

public class Main extends Application{

    @Override
    public void start(Stage stage) {
        try
        {
            System.out.println("loading root...");

            FXMLLoader loader= new FXMLLoader(getClass().getResource("GUI.fxml"));
            System.out.println("loading root...");

            Parent root = loader.load();
            System.out.println("loading controller...");

            stage.setTitle("user.User");
            stage.setScene(new Scene(root, 520, 400));
            stage.show();
            Platform.setImplicitExit(true);
            System.out.println("GUI opgestart");

            CateringFacility cateringFacility1 =new CateringFacility();
            cateringFacility1.setUniqueIDCF("KastartBVBA");
            cateringFacility1.setLocation("Onderbergen 42, 9000 Gent");
            cateringFacility1.connectToServer();



            cateringFacility1.getDailySecret();
            cateringFacility1.getDailyNym();
            cateringFacility1.generateQRcode();

            User user = new User();
            user.setController(loader.getController());
            user.setGsmNummer(102);
            user.setNaam("JEOF");
            user.connectToServer();

            user.retrieveMyTokens();
            /*DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = dateFormatter .parse("2020-11-20 00:00:01");
            Timer timer = new Timer();
            timer.schedule(new user.TimedTaskDaily(cateringFacility1,user),date, 86400000 );
            Timer timer2 = new Timer();
            timer2.schedule(new TimedTask2Weekly(),date, 1209600000 );     */

            System.out.print(LocalDateTime.now().toString());
            user.shareLogs();

            Doctor doctor = new Doctor();
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
