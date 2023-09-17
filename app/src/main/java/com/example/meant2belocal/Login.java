package com.example.meant2belocal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.scottyab.aescrypt.AESCrypt;

import java.net.PasswordAuthentication;
import java.security.GeneralSecurityException;

public class Login extends AppCompatActivity {

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://meant2belocal-default-rtdb.firebaseio.com/");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final EditText phone = findViewById(R.id.phone);
        final EditText password = findViewById(R.id.password);
        final Button loginBtn = findViewById(R.id.loginBtn);
        final TextView registerNowBtn = findViewById(R.id.registerNowBtn);

        users user;
        user = new users();

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String phoneTxt = phone.getText().toString();
                final String passwordTxt = password.getText().toString();
                user.setId(phoneTxt);

                if (phoneTxt.isEmpty() || passwordTxt.isEmpty()){
                    Toast.makeText(Login.this, "Please enter your phone number or password!", Toast.LENGTH_SHORT).show();
                }
                else{

                    databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            // check if mobile exist in database
                            if(snapshot.hasChild(phoneTxt)){

                                // mobile is existing
                                // getting password from firebase data and authenticate

                                final String getPassword = snapshot.child(phoneTxt).child("password").getValue(String.class);
                                String passworde = "password";
                                String message = passwordTxt;
                                String decryptedpswd = null;
                                try {
                                    decryptedpswd = AESCrypt.encrypt(passworde, message);
                                }catch (GeneralSecurityException e){

                                }
                                if(getPassword.equals(decryptedpswd)){



                                    databaseReference.child("location").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                                            databaseReference.child("current_user").child("id").setValue(phoneTxt);
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });

                                    Toast.makeText(Login.this, "Successfully logged in!", Toast.LENGTH_SHORT).show();

                                    // open main activity on success
                                    startActivity(new Intent(Login.this, MainActivity.class));
                                    finish();
                                }
                                else{
                                    Toast.makeText(Login.this, "Wrong Password!", Toast.LENGTH_SHORT).show();
                                }
                            }
                            else{
                                Toast.makeText(Login.this, "Wrong Mobile number!", Toast.LENGTH_SHORT).show();
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });

        registerNowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // open register activity
                startActivity(new Intent(Login.this, Register.class));

            }
        });
    }
}