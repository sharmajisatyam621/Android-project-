package com.example.userinput; // Make sure this matches your project's actual package name

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity; // This fixes the "cannot find symbol" error

public class MainActivity extends AppCompatActivity {

    // 1. Declare the views
    EditText etUserInput;
    Button btnClickMe;
    TextView tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // This links the Java to your XML design

        // 2. Initialize views by linking them to the IDs you created in XML
        etUserInput = findViewById(R.id.et_user_input);
        btnClickMe = findViewById(R.id.btn_click_me);
        tvResult = findViewById(R.id.tv_result);

        // 3. Set the Click Listener
        btnClickMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the text from the input box
                String input = etUserInput.getText().toString();

                if (input.trim().isEmpty()) {
                    tvResult.setText("Please enter something!");
                } else {
                    // Update the TextView to display the input
                    tvResult.setText("You typed: " + input);

                    // Show a small popup confirmation
                    Toast.makeText(MainActivity.this, "Text Updated!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}