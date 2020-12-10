import java.rmi.RemoteException;
import java.util.TimerTask;

public class TimedTaskDailyUser extends TimerTask {

    User user;

    public TimedTaskDailyUser(User u){
        user=u;
    }


    public void run()  {
        user.clearLogs();
        user.fetchCriticalLogs();
        user.retrieveMyTokens();
    }
}

