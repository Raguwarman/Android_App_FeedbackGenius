package com.testing.feedbackgenius;


import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private EditText registrationNumberEditText, feedbackEditText;
    private Button saveButton, displayButton,deleteButton;
    private TextView displayTextView;
    private SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        registrationNumberEditText = findViewById(R.id.registrationNumberEditText);
        feedbackEditText = findViewById(R.id.feedbackEditText);
        saveButton = findViewById(R.id.saveButton);
        displayButton = findViewById(R.id.displayButton);
        displayTextView = findViewById(R.id.displayTextView);

        // Create or open the database
        database = openOrCreateDatabase("StudentFeedbackDB", MODE_PRIVATE, null);
        database.execSQL("CREATE TABLE IF NOT EXISTS Feedback (RegistrationNumber VARCHAR, Feedback VARCHAR);");
        deleteButton = findViewById(R.id.deleteButton);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveFeedback();
            }
        });

        displayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayFeedback();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteFeedback();
            }
        });

    }

    private void saveFeedback() {
        String registrationNumber = registrationNumberEditText.getText().toString().trim();
        String feedback = feedbackEditText.getText().toString().trim();

        if (registrationNumber.isEmpty() || feedback.isEmpty()) {
            Toast.makeText(this, "Please enter registration number and feedback", Toast.LENGTH_SHORT).show();
            return;
        }

        String insertQuery = "INSERT INTO Feedback (RegistrationNumber, Feedback) VALUES (?, ?);";
        database.execSQL(insertQuery, new String[]{registrationNumber, feedback});
        Toast.makeText(this, "Feedback saved successfully", Toast.LENGTH_SHORT).show();
    }

    private void displayFeedback() {
        String registrationNumber = registrationNumberEditText.getText().toString().trim();

        if (registrationNumber.isEmpty()) {
            Toast.makeText(this, "Please enter registration number", Toast.LENGTH_SHORT).show();
            return;
        }

        String selectQuery = "SELECT Feedback FROM Feedback WHERE RegistrationNumber = ?;";
        Cursor cursor = database.rawQuery(selectQuery, new String[]{registrationNumber});

        if (cursor.moveToFirst()) {
            @SuppressLint("Range") String feedback = cursor.getString(cursor.getColumnIndex("Feedback"));
            displayTextView.setText(feedback);
        } else {
            displayTextView.setText("No feedback found for the entered registration number");
        }

        cursor.close();
    }
    private void deleteFeedback() {
        String registrationNumber = registrationNumberEditText.getText().toString().trim();

        if (registrationNumber.isEmpty()) {
            Toast.makeText(this, "Please enter registration number", Toast.LENGTH_SHORT).show();
            return;
        }

        String deleteQuery = "DELETE FROM Feedback WHERE RegistrationNumber = ?;";
        database.execSQL(deleteQuery, new String[]{registrationNumber});
        Toast.makeText(this, "Feedback deleted successfully", Toast.LENGTH_SHORT).show();

        // Clear the input fields and display area
        registrationNumberEditText.setText("");
        feedbackEditText.setText("");
        displayTextView.setText("");
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Close the database connection
        database.close();
    }
}
