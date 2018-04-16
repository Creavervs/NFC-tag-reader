package com.example.surface.near_field_communication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class HelpActivity extends AppCompatActivity {
    private Button button;
    private TextView textView;

    @Override


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_page);

        button = (Button) findViewById(R.id.backButton);
        textView= (TextView) findViewById(R.id.aboutUs);

        textView.setText(R.string.help_text);

        textView.setMovementMethod(new ScrollingMovementMethod()); // sets a scrolling textView for us to view information
        button.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                startActivity(new Intent(HelpActivity.this, MainActivity.class));
            }
        });
    }
}
