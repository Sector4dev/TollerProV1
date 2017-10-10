package app;

/**
 * Created by Sector4 Dev on 30-Jul-17.
 */

public class AppConfig {
    // Server user login url
    public static String URL_LOGIN = "http://tollerapp-back.herokuapp.com/users/sign_in";

    // Server user register url
    //public static String URL_REGISTER = "http://192.168.1.6/android_login_api/register.php";

    //Server using Scedulesets url
    public static String URL_SCHEDULESETS = "https://tollerapp-back.herokuapp.com/schedulesets?filter%5Buser%5D=";
    public static String URL_ASSIGNATIONS = "https://tollerapp-back.herokuapp.com/assignations/";
    public static String URL_TIMINGS="https://tollerapp-back.herokuapp.com/timings/";

    public static String URL_EXAMSCHEDULESETS = "https://tollerapp-back.herokuapp.com/examschedulesets?filter%5Buser%5D=";
    public static String URL_EXAMASSIGNATION = "https://tollerapp-back.herokuapp.com/examassignations/";
    public static String URL_EXAMTIMINGS = "https://tollerapp-back.herokuapp.com/examtimings/";

    public static String URL_AUDIO="https://tollerapp-back.herokuapp.com/audios/";
    public static String URL_FULLDATA="http://tollerapp-back.herokuapp.com/users/";

}
