package com.example.surface.near_field_communication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class helpPage extends AppCompatActivity {
    private Button button;

    @Override


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_page);

        button = (Button) findViewById(R.id.backButton);

        button.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                startActivity(new Intent(helpPage.this, MainActivity.class));
            }
        });
    }
}
