package com.tollerpro.sector4dev.tollerprov1;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spanned;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tollerpro.sector4dev.tollerprov1.R;

import java.util.Date;

/**
 * Created by Sector4 Dev on 9/25/2017.
 */

public class popActivity extends Activity {

    private long timerAlert=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.popup_layout);

        /*DisplayMetrics popDM=new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(popDM);
        int width=popDM.widthPixels;
        int height=popDM.heightPixels;

        getWindow().setLayout((int)(width),(int)(height));*/

        final TextView tdate = (TextView) findViewById(R.id.alerttimer);

        CountDownTimer counter=new CountDownTimer(30000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timerAlert=millisUntilFinished;
                tdate.setText(millisUntilFinished/1000+"");
            }

            @Override
            public void onFinish() {
                finish();
            }
        };
        counter.start();

        ProgressBar mProgressBarBig=(ProgressBar) findViewById(R.id.progressBarAlert);
        ProgressAnimations(mProgressBarBig,timerAlert,30000);
    }

    private void ProgressAnimations(ProgressBar curProgress, long fromTime, long toTime){

        ObjectAnimator animationS = ObjectAnimator.ofInt (curProgress, "progress", 0, 100); // see this max value coming back here, we animale towards that value
        long duration  = toTime-fromTime;

        animationS.setDuration (Math.abs(duration)); //in milliseconds
        animationS.setInterpolator (new LinearInterpolator());
        animationS.start ();
    }
}
