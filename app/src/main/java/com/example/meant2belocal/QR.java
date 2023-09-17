package com.example.meant2belocal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Image;
import android.os.Bundle;
import android.util.Size;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.datatransport.runtime.dagger.multibindings.ElementsIntoSet;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.mlkit.vision.barcode.Barcode;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.common.InputImage;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class QR extends AppCompatActivity {

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://meant2belocal-default-rtdb.firebaseio.com/");
    //DatabaseReference dbRef1 = FirebaseDatabase.getInstance().getReferenceFromUrl("https://meant2belocal-default-rtdb.firebaseio.com/");

    private TextView qrCodeTxt;
    private PreviewView previewView;
    private Button checkin;
    private ListenableFuture<ProcessCameraProvider> cameraProviderListenableFuture;
    public String qrdata;


    //Calendar calendar;
    //SimpleDateFormat simpleDateFormat;
    //String Date;
    //private Button checkin = findViewById(R.id.CheckinBtn);;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr);

        qrCodeTxt = findViewById(R.id.qrCideTxt);
        previewView = findViewById(R.id.CameraPreview);
        checkin = findViewById(R.id.btnCheckin);
        users user;
        user = new users();


        //calendar=Calendar.getInstance();
        //simpleDateFormat = new SimpleDateFormat("dd-mm-yyyy");
        //Date = simpleDateFormat.format(calendar.getTime());

        //Checking for camera permission
        if(ContextCompat.checkSelfPermission(QR.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
            init();
        }
        else{
            ActivityCompat.requestPermissions(QR.this, new String[]{Manifest.permission.CAMERA}, 101);
        }


        checkin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                //final String val;

                //dbRef1 = FirebaseDatabase.getInstance().getReference().child("current_user");

                /*databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.hasChildren()){
                            user.setId(snapshot.child("id").getValue().toString());
                        }
                   }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }

                });*/



                if(qrCodeTxt.getText().toString().isEmpty()){
                    Toast.makeText(QR.this, "Please scan again!", Toast.LENGTH_SHORT).show();
                }

                else{
                databaseReference = FirebaseDatabase.getInstance().getReference().child("CheckedIns");
                user.getId();
                user.setPlace(qrCodeTxt.getText().toString().trim());
                //user.setCurdate(Date);
                databaseReference.push().setValue(user);





                    Toast.makeText(QR.this, "Successfully checked in!", Toast.LENGTH_SHORT).show();

                    // open main activity on success
                    startActivity(new Intent(QR.this, Nominations.class));}


            }

        });

    }

    private void init(){
        cameraProviderListenableFuture = ProcessCameraProvider.getInstance(QR.this);

        cameraProviderListenableFuture.addListener(new Runnable() {
            @Override
            public void run() {

                try {
                    ProcessCameraProvider cameraProvider = cameraProviderListenableFuture.get();
                    bindImageAnalysis(cameraProvider);

                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }, ContextCompat.getMainExecutor(QR.this));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            init();
        }
        else{
            Toast.makeText(QR.this, "Permission Denied", Toast.LENGTH_SHORT).show();
        }
    }
    private void bindImageAnalysis(ProcessCameraProvider processCameraProvider){

        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder().setTargetResolution(new Size(1280, 720))
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST).build();

        imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(QR.this), new ImageAnalysis.Analyzer() {
            @Override
            public void analyze(@NonNull ImageProxy image) {

                @SuppressLint("UnsafeOptInUsageError") Image mediaImage = image.getImage();

                if(mediaImage!=null){
                    InputImage image2 = InputImage.fromMediaImage(mediaImage, image.getImageInfo().getRotationDegrees());

                    BarcodeScanner scanner = BarcodeScanning.getClient();

                    Task<List<Barcode>> results = scanner.process(image2);

                    results.addOnSuccessListener(new OnSuccessListener<List<Barcode>>() {
                        @Override
                        public void onSuccess(@NonNull List<Barcode> barcodes) {

                            for(Barcode barcode : barcodes){
                                final String getValue = barcode.getRawValue();

                                 qrCodeTxt.setText(getValue);
                                 qrdata = qrCodeTxt.getText().toString();

                            }


                            image.close();
                            mediaImage.close();

                        }

                    });
                }
            }
        });

        Preview preview = new Preview.Builder().build();
        CameraSelector cameraSelector = new CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());
        processCameraProvider.bindToLifecycle(this, cameraSelector, imageAnalysis, preview);
    }

}