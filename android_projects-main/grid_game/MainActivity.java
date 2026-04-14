package com.example.usinggridlayout;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Handler;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    Button b1, b2, b3, b4;
    int first = 0, second = 0;
    Button firstBtn, secondBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Correctly assign each button
        b1 = findViewById(R.id.b1);
        b2 = findViewById(R.id.b2);
        b3 = findViewById(R.id.b3);
        b4 = findViewById(R.id.b4);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { click(b1, 1); } // Pair A
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { click(b2, 1); } // Pair A
        });

        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { click(b3, 2); } // Pair B
        });

        b4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { click(b4, 2); } // Pair B
        });
    }

    void click(Button btn, int value) {
        // 1. Prevent clicking the same button twice or clicking while we are waiting
        if (btn == firstBtn || second != 0) return;

        if (first == 0) {
            // First button clicked
            first = value;
            firstBtn = btn;
            show(btn, value);
        } else {
            // Second button clicked
            second = value;
            secondBtn = btn;
            show(btn, value);

            if (first == second) {
                // MATCH: Keep them flipped and disable them
                Toast.makeText(this, "Match!", Toast.LENGTH_SHORT).show();
                firstBtn.setEnabled(false);
                secondBtn.setEnabled(false);

                // Clear variables so we can pick a new pair
                resetSelection();
            } else {
                // NO MATCH: Wait 1 second so the user can actually SEE the image
                Toast.makeText(this, "Try Again", Toast.LENGTH_SHORT).show();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Flip them back to the 'hidden' image
                        firstBtn.setBackgroundResource(R.drawable.download);
                        secondBtn.setBackgroundResource(R.drawable.download);
                        resetSelection();
                    }
                }, 1000); // 1000 milliseconds = 1 second
            }
        }
    }

    // Helper method to clear our tracking variables
    void resetSelection() {
        first = 0;
        second = 0;
        firstBtn = null;
        secondBtn = null;
    }

    void show(Button btn, int v) {
        if (v == 1) btn.setBackgroundResource(R.drawable.download_1);
        else btn.setBackgroundResource(R.drawable.download_2);
    }
}