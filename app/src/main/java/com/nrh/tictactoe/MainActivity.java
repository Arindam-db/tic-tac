package com.nrh.tictactoe;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private FirebaseAuth mAuth;
    private ImageView statusIndicator;
    private DatabaseReference userStatusRef;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        // Get header view and initialize TextViews and ImageView
        View headerView = navigationView.getHeaderView(0);
        TextView navEmailTextView = headerView.findViewById(R.id.unique_id);
        statusIndicator = headerView.findViewById(R.id.status_indicator);

        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users").child(userId);

            // Display email
            navEmailTextView.setText(currentUser.getEmail());

            // Set user presence status
            userStatusRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("presence");

            if (isNetworkAvailable()) {
                userStatusRef.setValue("online"); // Set initial status as online
                userStatusRef.onDisconnect().setValue("offline"); // Set status as offline when disconnected
            } else {
                Toast.makeText(MainActivity.this, "Connect to internet", Toast.LENGTH_SHORT).show();
                statusIndicator.setImageResource(R.drawable.red_dot);
            }

            // Listen for status changes
            userStatusRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (isNetworkAvailable()) {
                        String status = dataSnapshot.getValue(String.class);
                        if (status != null) {
                            Log.d("UserStatus", "User status: " + status); // Debugging
                            if (status.equals("online")) {
                                statusIndicator.setImageResource(R.drawable.green_dot); // Set online status indicator
                            } else {
                                statusIndicator.setImageResource(R.drawable.red_dot); // Set offline status indicator
                            }
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "Connect to internet", Toast.LENGTH_SHORT).show();
                        statusIndicator.setImageResource(R.drawable.red_dot);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle errors in listening for status changes
                    Toast.makeText(MainActivity.this, "Failed to listen for status changes: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            // User not signed in, navigate to SignInActivity
            startActivity(new Intent(MainActivity.this, SignInActivity.class));
            finish();
        }

        findViewById(R.id.online_select).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkAvailable()) {
                    Intent intent = new Intent(MainActivity.this, MatchFind.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "Connect to internet", Toast.LENGTH_SHORT).show();
                }
            }
        });

        findViewById(R.id.offline_select).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, OfflineGameActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (userStatusRef != null) {
            userStatusRef.setValue("offline");
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_home) {
            // Handle home navigation
        } else if (id == R.id.nav_profile) {
            // Handle profile navigation
            if (currentUser != null) {
                String email = currentUser.getEmail();
                showEmailDialog(email);
            }
        } else if (id == R.id.nav_logout) {
            // Set status to offline on logout
            if (userStatusRef != null) {
                userStatusRef.setValue("offline");
            }
            mAuth.signOut();
            startActivity(new Intent(MainActivity.this, SignInActivity.class));
            finish();
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showEmailDialog(String email) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("User Email");
        builder.setMessage("Your email address is:\n" + email);
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
