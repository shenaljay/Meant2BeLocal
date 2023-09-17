package com.example.meant2belocal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;


public class Register extends AppCompatActivity {


    // create object of database reference class to access firebase's real time database
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://meant2belocal-default-rtdb.firebaseio.com/");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        final EditText fullname = findViewById(R.id.fullname);
        final EditText email = findViewById(R.id.email);
        final EditText phone = findViewById(R.id.phone);
        final EditText beneficiary = findViewById(R.id.beneficiary);
        final EditText password = findViewById(R.id.password);
        final EditText conPassword = findViewById(R.id.conPassword);

        final Button registerBtn = findViewById(R.id.registerBtn);
        final TextView loginNowBtn = findViewById(R.id.loginNow);


        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //get data from EditTexts into String variables
                final String fullnameTxt = fullname.getText().toString();
                final String emailTxt = email.getText().toString();
                final String phoneTxt = phone.getText().toString();
                final String beneficiaryTxt = beneficiary.getText().toString();
                final String passwordTxt = password.getText().toString();
                final String conPasswordTxt = conPassword.getText().toString();



                //check if user fill all the fields before sending data to firebase
                if (fullnameTxt.isEmpty() || emailTxt.isEmpty() || phoneTxt.isEmpty() || beneficiaryTxt.isEmpty() || passwordTxt.isEmpty()){
                    Toast.makeText(Register.this, "Please fill fields!", Toast.LENGTH_SHORT).show();
                }

                // check if passwords are matching
                else if (!passwordTxt.equals(conPasswordTxt)){
                    Toast.makeText(Register.this, "Passwords are not matching!", Toast.LENGTH_SHORT).show();
                }

                else{
                    databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            // check if phone is not registered before

                            if(snapshot.hasChild(phoneTxt)){
                                Toast.makeText(Register.this, "Phone number is already Registered!", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                String passwordi = "password";
                                String message = passwordTxt;
                                String encryptPass = null;
                                try {
                                    encryptPass = AESCrypt.encrypt(passwordi, message);
                                }catch (GeneralSecurityException e){
                                    //handle error

                                }
                                // sending data to firebase Realtime Database
                                // phone number is the unique identifier
                                databaseReference.child("users").child(phoneTxt).child("fullname").setValue(fullnameTxt);
                                databaseReference.child("users").child(phoneTxt).child("email").setValue(emailTxt);
                                databaseReference.child("users").child(phoneTxt).child("beneficiary").setValue(beneficiaryTxt);
                                databaseReference.child("users").child(phoneTxt).child("password").setValue(encryptPass);

                                Toast.makeText(Register.this, "User registered successfully!", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
            }
        });
        loginNowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

}