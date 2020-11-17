import javafx.application.Application;
import javafx.stage.Stage;

import java.net.UnknownServiceException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;

public class Main extends Application{

    @Override
    public void start(Stage stage) {
        try
        {

            CateringFacility cateringFacility1 =new CateringFacility();
            cateringFacility1.setUniqueIDCF("KastartBVBA");
            cateringFacility1.setLocation("Onderbergen 42, 9000 Gent");
            cateringFacility1.connectToServer();

            /*DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = dateFormatter .parse("2020-11-20 00:00:01");
            Timer timer = new Timer();
            timer.schedule(new TimedTask(),date, 86400000 );     */

            cateringFacility1.getDailySecret();
            cateringFacility1.getDailyNym();
            cateringFacility1.generateQRcode();

            User user = new User();
            user.setGsmNummer(102);
            user.setNaam("JEOF");
            user.connectToServer();

            user.retrieveMyTokens();

            //Use this if you want to execute it repeatedly
            //int period = 10000;//10secs
            //timer.schedule(new MyTimeTask(), date, period );

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void stop(){
        System.exit(0);
    }
}
