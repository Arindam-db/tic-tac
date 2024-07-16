package com.nrh.tictactoe;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Logger;

public class UserNameSelect extends AppCompatActivity {
    private EditText nameInput;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private ProgressBar progressBar;

    private static final String TAG = "UserNameSelect";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_name_select);

        nameInput = findViewById(R.id.nameInput);
        Button submitButton = findViewById(R.id.submitButton);
        progressBar = findViewById(R.id.progressBar);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        // Enable detailed Firebase logging
        FirebaseDatabase.getInstance().setLogLevel(Logger.Level.DEBUG);

        // Test write to Firebase
        databaseReference.child("test").setValue("TestValue")
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Test write succeeded");
                    } else {
                        Log.e(TAG, "Test write failed", task.getException());
                    }
                });

        submitButton.setOnClickListener(v -> {
            Log.d(TAG, "Submit button clicked");
            saveUsername();
        });
    }

    private void saveUsername() {
        String username = nameInput.getText().toString();

        if (TextUtils.isEmpty(username)) {
            Toast.makeText(this, "Please enter a username", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            // Show ProgressBar
            progressBar.setVisibility(View.VISIBLE);

            Log.d(TAG, "Saving username for user ID: " + userId);

            databaseReference.child(userId).child("username").setValue(username)
                    .addOnCompleteListener(task -> {
                        // Hide ProgressBar
                        progressBar.setVisibility(View.GONE);

                        if (task.isSuccessful()) {
                            Log.d(TAG, "Username saved successfully");
                            Toast.makeText(UserNameSelect.this, "Username saved", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(UserNameSelect.this, UserProfileActivity.class));
                            finish();
                        } else {
                            // Display error message from Firebase
                            Exception exception = task.getException();
                            if (exception != null) {
                                Log.e(TAG, "Error saving username: ", exception);
                                Toast.makeText(UserNameSelect.this, "Error: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                            } else {
                                Log.e(TAG, "Failed to save username with no exception");
                                Toast.makeText(UserNameSelect.this, "Failed to save username", Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        // Hide ProgressBar
                        progressBar.setVisibility(View.GONE);

                        Log.e(TAG, "Failed to save username: ", e);
                        Toast.makeText(UserNameSelect.this, "Failed to save username: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            Log.e(TAG, "User not authenticated");
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
        }
    }
}
