import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application{

    @Override
    public void start(Stage stage) {
        try
        {

            CateringFacility cateringFacility1 =new CateringFacility();
            cateringFacility1.setUniqueIDCF("Kastart BVBA");
            cateringFacility1.connectToServer();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void stop(){
        System.exit(0);
    }
}
