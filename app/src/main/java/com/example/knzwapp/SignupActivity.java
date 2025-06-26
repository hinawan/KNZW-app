package com.example.knzwapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.android.gms.auth.*;
import com.google.android.gms.common.*;
import com.google.firebase.database.*;

public class SignupActivity extends AppCompatActivity {

    EditText signupUsername, signupPassword, conPassword;
    TextView loginRedirect;
    Button signupButton;
    FirebaseDatabase database;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        signupUsername=findViewById(R.id.et_signup_Username);
        signupPassword=findViewById(R.id.et_signup_Password);
        conPassword=findViewById(R.id.etConPassword);
        signupButton=findViewById(R.id.btnSignup);
        loginRedirect=findViewById(R.id.tvLoginRedirect);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                database=FirebaseDatabase.getInstance();
                reference=database.getReference("users");

                String username=signupUsername.getText().toString().trim();
                String password=signupPassword.getText().toString().trim();
                String confirmPassword=conPassword.getText().toString().trim();

                if(username.isEmpty()){
                    Toast.makeText(SignupActivity.this,"Username is empty!",Toast.LENGTH_SHORT).show();
                    return;
                } else if(!confirmPassword.equals(password)){
                    Toast.makeText(SignupActivity.this,"Password do not match!",Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    HelperClass helperclass=new HelperClass(username,password);
                    reference.child(username).setValue(helperclass);
                    Toast.makeText(SignupActivity.this,"Signup successful!",Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(SignupActivity.this, LoginActivity.class);
                    startActivity(intent);
                };

            }
        });

        loginRedirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });



    }
}