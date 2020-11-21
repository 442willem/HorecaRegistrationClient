import java.rmi.RemoteException;
import java.util.TimerTask;

public class TimedTaskDaily extends TimerTask {

    CateringFacility cf;
    User user;

    public TimedTaskDaily(CateringFacility c, User u){
        cf=c;
        user=u;
    }


    public void run()  {
        try {
            cf.getDailySecret();
            cf.getDailyNym();
            cf.generateQRcode();

            user.clearLogs();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}

