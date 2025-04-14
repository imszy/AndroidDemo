package com.example.demoapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    
    private int counter = 0;
    private TextView counterTextView;
    private Button incrementButton;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        counterTextView = findViewById(R.id.counterTextView);
        incrementButton = findViewById(R.id.incrementButton);
        
        updateCounterDisplay();
        
        incrementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                counter++;
                updateCounterDisplay();
                Toast.makeText(MainActivity.this, "Counter incremented!", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void updateCounterDisplay() {
        counterTextView.setText("Count: " + counter);
    }
} 