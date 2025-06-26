package com.example.knzwapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

public class LoginActivity extends AppCompatActivity {
    EditText loginUsername, loginPassword;
    Button loginButton, biometricButton;
    TextView signupRedirect;
    private static final String TAG = "LoginActivity";
    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;
    private FirebaseAuth mAuth;
    private DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        loginUsername = findViewById(R.id.et_login_Username);
        loginPassword = findViewById(R.id.et_login_Password);
        signupRedirect = findViewById(R.id.tvSignupRedirect);
        loginButton = findViewById(R.id.btnLogin);
        biometricButton = findViewById(R.id.btnBiometric);
        mAuth = FirebaseAuth.getInstance();

        loginButton.setOnClickListener(v -> {
            if (!validUsername() | !validPassword()) return;
            checkUser();
        });

        signupRedirect.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, SignupActivity.class)));

        executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                Toast.makeText(LoginActivity.this, "Biometric error: " + errString, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                mAuth.signInAnonymously().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            String uid = user.getUid();
                            String username = "bio_" + uid.substring(0, 8);
                            String password = "";

                            Map<String, Object> userData = new HashMap<>();
                            userData.put("username", username);
                            userData.put("password", password);

                            usersRef.child(uid).setValue(userData).addOnCompleteListener(dbTask -> {
                                if (dbTask.isSuccessful()) {
                                    Toast.makeText(LoginActivity.this, "Biometric login success", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                    finish();
                                } else {
                                    Toast.makeText(LoginActivity.this, "Failed to save user: " + dbTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    Log.e(TAG, "Database write error", dbTask.getException());
                                }
                            });
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "Auth failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }


            @Override
            public void onAuthenticationFailed() {
                Toast.makeText(LoginActivity.this, "Biometric failed", Toast.LENGTH_SHORT).show();
            }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric Login")
                .setSubtitle("Use fingerprint or face")
                .setNegativeButtonText("Cancel")
                .build();

        biometricButton.setOnClickListener(v -> biometricPrompt.authenticate(promptInfo));
    }

    public Boolean validUsername() {
        String valid = loginUsername.getText().toString().trim();
        if (valid.isEmpty()) {
            loginUsername.setError("Please enter username");
            return false;
        } else {
            loginUsername.setError(null);
            return true;
        }
    }

    public Boolean validPassword() {
        String valid = loginPassword.getText().toString().trim();
        if (valid.isEmpty()) {
            loginPassword.setError("Please enter password");
            return false;
        } else {
            loginPassword.setError(null);
            return true;
        }
    }

    public void checkUser() {
        try {
            if (mAuth.getCurrentUser() == null) {
                mAuth.signInAnonymously().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        performUserCheck();
                    } else {
                        Toast.makeText(LoginActivity.this, "Auth failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Auth failure: " + task.getException().getMessage());
                    }
                });
            } else {
                performUserCheck();
            }
        } catch (Exception e) {
            Toast.makeText(LoginActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

    private void performUserCheck() {
        String username = loginUsername.getText().toString().trim();
        String password = loginPassword.getText().toString().trim();

        Query checkDatabase = usersRef.orderByChild("username").equalTo(username);
        checkDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        String dbPassword = userSnapshot.child("password").getValue(String.class);
                        if (dbPassword != null && dbPassword.equals(password)) {
                            String uid = userSnapshot.getKey();
                            usersRef.child(uid).setValue(new HelperClass(username, password));
                            Toast.makeText(LoginActivity.this, "Login success", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        } else {
                            loginPassword.setError("Wrong password");
                            loginPassword.requestFocus();
                        }
                    }
                } else {
                    loginUsername.setError("User does not exist");
                    loginUsername.requestFocus();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(LoginActivity.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Database error: " + error.getMessage());
            }
        });
    }
}