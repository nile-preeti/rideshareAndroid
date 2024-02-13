package com.ridesharedriver.app.custom;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.TextView;

import com.ridesharedriver.app.acitivities.HomeActivity;
import com.ridesharedriver.app.session.SessionManager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class LoginTimerCountDown {

    private static final long MILLIS_IN_FUTURE = 100000000;
    private TextView textView;
    private Date date1, date2;
    private Activity context;

    int hr, min, sec;
    int currentHr, currentMin, currentSec;
    boolean isPaused;




    public LoginTimerCountDown(TextView textView,Activity context) {
        this.textView = textView;
        this.context = context;
        this.isPaused = isPaused;

        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //TaskState state = new InitialState();



        try {

            String Second =SessionManager.getTotalTime();
            sec = Integer.parseInt(Second);

            Log.d("Second",Second+"");

            date1 = (Date) formatter.parse(SessionManager.getLoginTime());
            date2 = (Date) formatter.parse(SessionManager.getCurrentTime());

            currentSec = Integer.parseInt(SessionManager.getCurrentTime());

            Log.e("timer_con","I was called "+date1.toString()+", "+date2.toString());







//            Calendar calLogin = toCalendar(date1);
//            Calendar calCurrent = toCalendar(date2);
//            long milisec = calLogin.getTimeInMillis() - calCurrent.getTimeInMillis();
//            Calendar cal = Calendar.getInstance();
//            cal.setTimeInMillis(milisec);
//            long diff = date2.getTime() - date1.getTime();
//            long diffSeconds = diff / 1000 % 60;
//            long diffMinutes = diff / (60 * 1000) % 60;
//            long diffHours = diff / (60 * 60 * 1000);
//
//            sec = (int) diffSeconds;
//            min = (int) diffMinutes;
//            hr = (int) diffHours;
            //startTimer(false);

        } catch (Exception ex) {

        }


    }


    public void startTimer(boolean isPaused) {

        CountDownTimer newtimer = new CountDownTimer(MILLIS_IN_FUTURE, 1000) {

            @SuppressLint("SuspiciousIndentation")
            public void onTick(long millisUntilFinished) {

                sec++;
                if(sec>59)
                {
                    sec=0;
                    min++;
                }
                if(min>59)
                {
                    min=0;
                    hr++;
                }
                if(hr>=17)
                {
                    if(!SessionManager.getIsLogoutStatus())
                    {
                        try {
                            ((HomeActivity)context).is_online(SessionManager.getUserId(),"3", false);
                        }
                        catch (Exception ex)
                        {

                        }
                    }
                }
                if(hr<10)
                {
                    if(SessionManager.getOfflineAlertStatus())
                    {
                        SessionManager.setOfflineAlertStatus(false);
                    }
                }
                if(hr>=10 && hr<12)
                {
                    if(((HomeActivity)context).alertDialog!=null)
                    {
                        if(!((HomeActivity)context).alertDialog.isShowing())
                        {
                            if(!SessionManager.getOfflineAlertStatus())
                            ((HomeActivity) context).showOfflineAlert();
                        }
                    }
                    else {
                        if(!SessionManager.getOfflineAlertStatus())
                        ((HomeActivity) context).showOfflineAlert();
                    }
                }
                Log.e("login_timer",hr+":"+min+":"+sec);
//                context.runOnUiThread(new Runnable() {
//                    public void run() {
//
//
//                    }
//                });

                if (isPaused){
                    textView.setText("00:00:00");
                }else {
                    textView.setText(String.format(Locale.US, "%02d:%02d:%02d", hr, min, sec));
                }


            }

            public void onFinish() {
                textView.setText("00:00:00");            }
        };
        if (isPaused){
            newtimer.cancel();
        }else {
            newtimer.start();
        }
    }




    public static Calendar toCalendar(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }


}


