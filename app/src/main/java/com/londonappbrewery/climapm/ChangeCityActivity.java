package com.londonappbrewery.climapm;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class ChangeCityActivity extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_city_layout);
        final EditText changeEditText = (EditText) findViewById(R.id.queryET);
        ImageButton backButton = (ImageButton) findViewById(R.id.backButton);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        changeEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                Intent changeCityIntent = new Intent(ChangeCityActivity.this, WeatherController.class);
                String cityName = changeEditText.getText().toString();

                changeCityIntent.putExtra("City", cityName);
                startActivity(changeCityIntent);

                return false;
            }
        });

    }
}
