package ca.uwaterloo.cs349.a4;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Observable;
import java.util.Observer;

public class SettingActivity extends AppCompatActivity implements Observer {
    //model
    Model model;
    //difficulty
    RadioGroup diff_choice;

    //number of bubbles
    Button minus_btn;
    TextView saved_n;
    Button plus_btn;

    //save button
    Button save_btn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        //model
        this.model = model;
        model = Model.getInstance();
        model.addObserver(this);

        //difficulty radio button
        diff_choice = findViewById(R.id.diff_choice);
        findViewById(R.id.easy_diff).setId(100);
        findViewById(R.id.hard_diff).setId(300);
        findViewById(R.id.normal_diff).setId(200);

        diff_choice.check(model.getDifficultyId());
        diff_choice.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Log.d("From view", "Change");
                model.setDifficultyId(checkedId);
            }
        });

        //change number of bubbles

        saved_n = findViewById(R.id.n_of_bubble);
        saved_n.setText(String.valueOf(model.getN_btn()));

        minus_btn = findViewById(R.id.minus_btn);
        minus_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                model.setN_btn(model.getN_btn() - 1);
            }
        });

        plus_btn = findViewById(R.id.plus_btn);
        plus_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                model.setN_btn(model.getN_btn() + 1);
            }
        });

        //save
        save_btn = findViewById(R.id.save_btn);
        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), MainActivity.class);
                startActivity(myIntent);
                finish();
            }
        });

    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        // Remove observer when activity is destroyed.
        Log.d("Play Activity: ", "destroyed");
        model.deleteObserver(this);
    }

    @Override
    public void update(Observable o, Object arg){
        saved_n.setText(String.valueOf(model.getN_btn()));

        //update number of bubbles that are supposed to be showed
        //alert user when it reaches min or max value
        if(model.getN_btn()<=1){
            Toast.makeText(SettingActivity.this,
                    "Min number is 1", Toast.LENGTH_SHORT).show();
        }
        if(model.getN_btn()>=6){
            Toast.makeText(SettingActivity.this,
                    "Max number is 6", Toast.LENGTH_SHORT).show();
        }
    }

}
