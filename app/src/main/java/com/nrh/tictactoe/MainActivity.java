package com.nrh.tictactoe;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
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
    private TextView navUsernameTextView;
    private ImageView statusIndicator; // Add ImageView for status indicator

    private DatabaseReference userStatusRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

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
        navUsernameTextView = headerView.findViewById(R.id.nav_view);
        statusIndicator = headerView.findViewById(R.id.status_indicator); // Adjust for your ImageView

        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users").child(userId);

            // Display email
            navEmailTextView.setText(currentUser.getEmail());

            // Retrieve and display username
            databaseReference.child("username").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String username = dataSnapshot.getValue(String.class);
                    if (username == null || username.isEmpty()) {
                        // Username is not set, navigate to UserNameSelect
                        startActivity(new Intent(MainActivity.this, UserNameSelect.class));
                        finish();
                    } else {
                        // Username is set, display it and enable profile edit
                        navUsernameTextView.setText(username);

                        // Set onClickListener for navUsernameTextView
                        navUsernameTextView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // Handle the click on the username here
                                startActivity(new Intent(MainActivity.this, UserNameSelect.class));
                                // Start your profile edit activity or perform other actions
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle errors in retrieving username
                    Toast.makeText(MainActivity.this, "Failed to load username: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

            // Set user presence status
            userStatusRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("presence");

            userStatusRef.setValue("online"); // Set initial status as online

            userStatusRef.onDisconnect().setValue("offline"); // Set status as offline when disconnected

            // Listen for status changes
            userStatusRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String status = dataSnapshot.getValue(String.class);
                    if (status != null && status.equals("online")) {
                        statusIndicator.setImageResource(R.drawable.green_dot); // Set online status indicator
                    } else {
                        statusIndicator.setImageResource(R.drawable.red_dot); // Set offline status indicator
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
                Intent intent = new Intent(MainActivity.this, MatchFind.class);
                startActivity(intent);
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
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_home) {
            // Handle home navigation
        } else if (id == R.id.nav_profile) {
            // Handle profile navigation
            startActivity(new Intent(MainActivity.this, UserProfileActivity.class));
        } else if (id == R.id.nav_logout) {
            // Set status to offline on logout
            userStatusRef.setValue("offline");
            mAuth.signOut();
            startActivity(new Intent(MainActivity.this, SignInActivity.class));
            finish();
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
