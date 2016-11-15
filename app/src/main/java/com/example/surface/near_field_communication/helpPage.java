package com.example.surface.near_field_communication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class helpPage extends AppCompatActivity {
    private Button button;
    private TextView textView;

    @Override


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_page);

        button = (Button) findViewById(R.id.backButton);
        textView= (TextView) findViewById(R.id.aboutUs);

        // Textview information
        textView.setText("Haha you were close! Looks like it's up now. Will pull it in a minute and see if it's all come through\n" +
                "Your Victoria II spans the globe from 1836 to the start of 1936 with over 200 playable nations.[4] Like its predecessor, Victoria II focuses on internal management, covering the industrialization and social/political changes in a country with dozens of different government types. The game gives a lot of importance to the economy of a country by having a complex market system with over 50 types of goods and factories.[4] While warfare is a component of the game it is not the primary focus as in other Paradox Interactive games such as the Hearts of Iron series.[5]\n" +
                "\n" +
                "Nations' populations are divided into cultures, religions, and occupations. There are several different population groups or \"pops\" including aristocrats, officers, clergy, capitalists, clerks, craftsmen, soldiers, laborers, and farmers. Victoria II introduces two new groups, artisans and bureaucrats. As in other Paradox titles, like Europa Universalis, historical missions that are micro-objectives in the larger game have been added. There are thousands of historical events and decisions as well.[4] These events and nationalist forces can lead to the creation or disintegration of nation states.[6]\n" +
                "\n" +
                "Victoria II contains a number of changes and improvements from its predecessor. The interface was streamlined when compared to the original game, which was described by producer Johan Andersson as, \"the interface God forgot\".[7] Automation of various tasks has been added, including trade and population promotion. The education system has been overhauled by having clergy educate people of the same religion, and each population group now has their own literacy levels. Education and literacy's importance is reflected in the vast technology system that contains thousands of inventions.[4] Additionally, the functioning of ideology in the game was tweaked such that population groups are more sensitive to changes in their country's situation, as well as inclined to agitate for specific levels of political and social reforms.[8]build.gradle file is still 4 days old. Not sure if that will be a problem or not. So this is a message that Patrick and i had a long time ago and lets see if we are going to run out of space or not");

        textView.setMovementMethod(new ScrollingMovementMethod()); // sets a scrolling textView for us to view information
        button.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                startActivity(new Intent(helpPage.this, MainActivity.class));
            }
        });
    }
}
