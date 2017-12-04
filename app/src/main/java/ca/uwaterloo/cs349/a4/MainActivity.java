package ca.uwaterloo.cs349.a4;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.Observable;
import java.util.Observer;

public class MainActivity extends AppCompatActivity implements Observer {

    // Private Vars
    Model model;
    Button startButton;
    Button settingButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //model
        this.model = model;
        model = Model.getInstance();
        model.addObserver(this);

        // Get button reference.
        startButton = (Button) findViewById(R.id.start_button);
        settingButton = (Button) findViewById(R.id.setting_button);

        //set button listener
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), PlayActivity.class);
                startActivity(myIntent);
            }
        });
        settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), SettingActivity.class);
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

    }

}
