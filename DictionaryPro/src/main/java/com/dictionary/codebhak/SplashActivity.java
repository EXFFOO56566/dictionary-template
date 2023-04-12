package com.dictionary.codebhak;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Window;
import android.view.WindowManager;


/**
 * Created by ThangTB on 09/02/2015.
 */
public class SplashActivity extends Activity{

    //The countdown to control the time that wil be this acivity
    CountDownTimer countdown = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.wordbook_splash);

        //Initializing the countdawn. Its duration is 3 seconds
        countdown = new CountDownTimer(3000, 500) {

            public void onTick(long millisUntilFinished) {

            }
            //When the countdown finishes
            @Override
            public void onFinish() {
                // TODO Auto-generated method stub
                //Calling the menu activity
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                //Cancelling the countdown
                countdown.cancel();
                //Finishing this activity
                finish();
            }


        };
        //Starting the countdown
        countdown.start();



    }

    @Override
    public void onBackPressed() {

    }
}
