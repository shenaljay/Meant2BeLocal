package com.example.meant2belocal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    TextView txt1,txt2,txt3,txt4,txt5;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txt1 = findViewById(R.id.txtid1);
        txt2 = findViewById(R.id.txtid2);
        txt3 = findViewById(R.id.txtid3);
        txt4 = findViewById(R.id.txtid4);
        txt5 = findViewById(R.id.txtid5);


        databaseReference = FirebaseDatabase.getInstance().getReference().child("post");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChildren()){
                    txt1.setText(snapshot.child("001").child("event").getValue().toString());
                    txt2.setText(snapshot.child("002").child("event").getValue().toString());
                    txt3.setText(snapshot.child("003").child("event").getValue().toString());
                    txt4.setText(snapshot.child("004").child("event").getValue().toString());
                    txt5.setText(snapshot.child("005").child("event").getValue().toString());

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        final Button scanQrBtn = findViewById(R.id.btn_scanqr);

        scanQrBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, QR.class));
                finish();
            }
        });
    }
}