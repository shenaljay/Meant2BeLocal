package com.example.meant2belocal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class Nominations extends AppCompatActivity {

    //DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://meant2belocal-default-rtdb.firebaseio.com/");
    DatabaseReference databaseReference;

    nominee nomi;

    EditText nom;
    Button nombtn, home;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nominations);

        nomi = new nominee();

        nom = findViewById(R.id.nomTxt);
        nombtn = findViewById(R.id.btnNominate);
        home = findViewById(R.id.btnhome);

        nombtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(nom.getText().toString().isEmpty()){
                    Toast.makeText(Nominations.this, "Please Enter a Nominee!", Toast.LENGTH_SHORT).show();
                }

                else{
                databaseReference = FirebaseDatabase.getInstance().getReference().child("nominations");

                nomi.getNomineeid();
                nomi.setNvalue(nom.getText().toString().trim());
                databaseReference.push().setValue(nomi);


                Toast.makeText(Nominations.this, "Nomination added successfully!", Toast.LENGTH_SHORT).show();

                startActivity(new Intent(Nominations.this, MainActivity.class));}
            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Nominations.this, MainActivity.class));
            }
        });

    }
}