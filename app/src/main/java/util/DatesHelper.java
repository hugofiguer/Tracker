package util;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class DatesHelper {

    private static  String      TAG  =  "DatesHelper";

    private static DatesHelper helper;

    private DatesHelper(){
    }
    public static synchronized DatesHelper sharedInstance(){
        if (helper == null)
            helper = new DatesHelper();
        return helper;
    }

    public static long daysFromLastUpdate(Date lastUpdate) {

        Date now                    = new Date();
        Calendar cal_now            = Calendar.getInstance();
        cal_now.setTime(now);
        Calendar call_last          = Calendar.getInstance();
        call_last.setTime(lastUpdate);

        Calendar date = (Calendar) call_last.clone();
        long daysBetween = 0;
        while (date.before(cal_now)) {
            Log.d("TAG","1 day of difference");
            date.add(Calendar.DAY_OF_MONTH, 1);
            daysBetween++;
        }
        return daysBetween;
    }

    public static String getStringDate (Date date){
        return new SimpleDateFormat("yyyy-MM-dd HH:mm").format(date);
    }

    public static String getStringDateDays (Date date){
        return new SimpleDateFormat("yyyy-MM-dd").format(date);
    }
}
