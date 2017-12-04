package ca.uwaterloo.cs349.a4;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Observable;
import java.util.Observer;

import static android.graphics.Color.WHITE;

public class PlayActivity extends AppCompatActivity implements Observer {

    //fields
    Model model;
    TextView message_view;
    TextView score_view;
    Button back_home;//Quit game button
    Button continue_playing;//continue button
    Handler afterClickHandler;//handler that request nextbutton after a delayed time

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        //model
        this.model = model;
        model = Model.getInstance();
        model.addObserver(this);

        //set handler
        afterClickHandler = new Handler();

        //set textview
        message_view = (TextView)findViewById(R.id.message_view);
        score_view = (TextView)findViewById(R.id.score_view);

        //HOME button
        back_home = (Button) findViewById(R.id.back_home);
        back_home.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                model.simon.init(model.getN_btn(), true);
                Intent myIntent = new Intent(view.getContext(), MainActivity.class);
                startActivity(myIntent);
                finish();
            }
        });

        //continue button
        continue_playing = (Button) findViewById(R.id.continue_playing);
        continue_playing.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if(model.simon.getState() == State.START || model.simon.getState() == State.LOSE || model.simon.getState() == State.WIN) {
                    model.simon.newRound();
                    model.updateNextButton();
                }else{
                    Log.d("Activity: ", "Play your game man..");
                }

            }
        });

        //handler that set all buttons
        //use this to wait for parent layout set up, so that buttons can measure height ans width
        final Handler handleSetAllButtons = new Handler();
        handleSetAllButtons.postDelayed(new Runnable() {
            @Override
            public void run() {
                setAllButtons();
            }
        }, 200);
        model.setMode('f');//free mode (no CPU and HUMAN playing)
        model.setBtn_setup(0);
        model.setTextAndScore();
        model.updateAll();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        // Remove observer when activity is destroyed.
        Log.d("Play Activity: ", "destroyed");
        model.deleteObserver(this);
    }

    public void setAllButtons(){
        int n_btn = model.getN_btn();
        int diff = model.getDifficultyId();

        //add buttons
        LinearLayout layout = (LinearLayout) findViewById(R.id.parent_linear_layout);
        layout.setGravity(Gravity.CENTER);

        //calculate how many buttons should be put vertically
        int row_n = (model.getN_btn() >= 2 ?  2 : 1);//if we have more than 1 button, we put 2 buttons in a row; otherwise, that only one button will take the whole row
        int col_n = (int)Math.ceil((double)n_btn/(double)row_n);

        int counter = 0;
        for (int i = 0; i < col_n; i++) {
            LinearLayout row = new LinearLayout(this);
            row.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            row.setGravity(Gravity.CENTER);
            //build row_n buttons in one row
            for (int j = 0; j < row_n; j++) {
                if(counter >= n_btn){
                    break;
                }
                Button one_btn = new Button(this);
                int w = layout.getMeasuredWidth()/row_n;
                int h = layout.getMeasuredHeight()/col_n;
                int d = w < h ? w : h;
                //get the dimention of each button
                one_btn.setLayoutParams(new LinearLayout.LayoutParams(d - d/10, d - d/10));
                one_btn.setText(String.valueOf(counter + 1));
                one_btn.setId(counter + 1);
                one_btn.setBackgroundResource(R.drawable.round_button);
                //button initially disabled
                one_btn.setEnabled(false);
                one_btn.setTextSize(24);
                one_btn.setTextColor(WHITE);
                //set flash animation in onClicker
                one_btn.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        Animation mAnimation = new AlphaAnimation(1, 0);
                        //play back speed is either 400, 800, or 1200
                        //flash animation
                        mAnimation.setDuration(model.getPlayback_speed()/2 - model.getPlayback_speed()/4);
                        mAnimation.setInterpolator(new LinearInterpolator());
                        mAnimation.setRepeatCount(1);
                        mAnimation.setRepeatMode(Animation.REVERSE);
                        Button btn = (Button) findViewById(v.getId());
                        btn.startAnimation(mAnimation);

                        //verification when human mode
                        if(model.getMode() == 'h'){
                            Log.d("Activity: ", "verified");
                            model.simon.verifyButton(v.getId());
                            afterClickHandler.postDelayed(requestNextBtn, 0);
                        }
                    }
                });
                row.addView(one_btn);
                //add to model
                counter ++;
            }

            layout.addView(row);
        }
    }

    public void disableAllBtns(){
        int n = model.getN_btn();
        for(int i=0; i<n; i++){
            Button one_btn = (Button)findViewById(i+1);
            one_btn.setEnabled(false);
        }
    }

    public void enableAllBtns(){
        int n = model.getN_btn();
        for(int i=0; i<n; i++){
            Button one_btn = (Button)findViewById(i+1);
            one_btn.setEnabled(true);
        }
    }

    public Runnable requestNextBtn = new Runnable() {
        @Override
        public void run() {
            model.updateNextButton();
        }
    };


    @Override
    public void update(Observable o, Object arg){
        //update message
        message_view.setText(model.getMessage_to_show());
        score_view.setText(model.getScore_to_show());

        //handle click
        //click btn that computer clicked
        if(model.getMode() == 'm'){
            disableAllBtns();
            Button needClick = (Button)findViewById(model.getBtn_need_click());
            needClick.performClick();
            afterClickHandler.postDelayed(requestNextBtn, model.getPlayback_speed());
        }else if(model.getMode() == 'h'){
            enableAllBtns();
        }else{
            //set the "touch to start view to be visible"
            if(model.getBtn_setup() == 0){
                model.setBtn_setup(1);
            }else{
                disableAllBtns();
            }
        }
    }
}
