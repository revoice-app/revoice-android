package com.sar.user.smart_city;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.tensorflow.lite.examples.textclassification.client.Result;
import org.tensorflow.lite.examples.textclassification.client.TextClassificationClient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class FinalAct extends AppCompatActivity implements LocationListener {
    TextView textView;
    private TextClassificationClient client;
    private MaterialButton materialButton;

    private Handler handler;
    private static ProgressDialog progress;
    private ImageView imageView;
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;
    private TextView textView2;
    LocationManager locationManager;
    private static  String aap="";

    final int[] ap = {2};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final);
        textView=findViewById(R.id.txvResult);
        imageView=findViewById(R.id.image);
        textView2=findViewById(R.id.textClass);


        String Time = java.text.DateFormat.getTimeInstance().format(new Date());
        String date_L = java.text.DateFormat.getDateInstance().format(new Date());
        String aa=date_L+" "+Time;


        client = new TextClassificationClient(getApplicationContext());
        handler = new Handler();
        Intent intent=getIntent();


        Random random=new Random();
        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference("review/"+ FirebaseAuth.getInstance().getUid().toString()+"/"+aa);
        String textSent=intent.getStringExtra("textSent");
        String text=intent.getStringExtra("text");
        String textclass=intent.getStringExtra("class");
        Log.d("popoll",textSent);
        textView.setText(textSent);




        byte[] d=intent.getByteArrayExtra("image");
        if(d.length>0) {
            imageView.setImageBitmap(BitmapFactory.decodeByteArray(d, 0, d.length));
            textView2.setText(textclass);

        }

        //classify("nice");


        materialButton=findViewById(R.id.button);


        materialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showLoader(FinalAct.this);
                if(ap[0]==2)
                {
                    getLocation();

                }
                else {

                    databaseReference.child("image").setValue(Base64.encodeToString(d, Base64.DEFAULT)).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(FinalAct.this, "Something went wrong", Toast.LENGTH_SHORT).show();

                            startActivity(new Intent(FinalAct.this, MainScreen.class));

                        }
                    });

                    databaseReference.child("textSent").setValue(textSent).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {


                        }
                    });
                    databaseReference.child("text").setValue(text).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                        }
                    });


                    databaseReference.child("textClass").setValue(textclass).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(FinalAct.this, "Done", Toast.LENGTH_SHORT).show();

                            startActivity(new Intent(FinalAct.this, MainScreen.class));
                        }
                    });
                    databaseReference.child("location").setValue(aap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                        }
                    });
                }


            }


        });


    }
    public static void showLoader(Context context) {
        hideLoader(context);
        if (null != context && !(((Activity) context).isFinishing())) {
            //show dialog
            progress = ProgressDialog.show(context, null, null, false, false);
            progress.setContentView(R.layout.common_loader);
            progress.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            progress.show();
        }
    }
    public static void hideLoader(Context context) {
        if (null != progress && progress.isShowing() && !(((Activity) context).isFinishing())) {
            progress.hide();
            progress.dismiss();
        }
        progress = null;
    }
    public void  getLocation() {
        String locationText;
        if (ContextCompat.checkSelfPermission(FinalAct.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(FinalAct.this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, 100);
        }
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, (LocationListener) this);

        } catch (SecurityException e) {
            e.printStackTrace();
        }


    }
    @Override
    public void onLocationChanged(Location location) {
        Toast.makeText(this, ""+location.getLatitude()+","+location.getLongitude(), Toast.LENGTH_SHORT).show();
        try {
            Geocoder geocoder = new Geocoder(FinalAct.this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
            String address = addresses.get(0).getAddressLine(0);
            aap=location.getLatitude()+","+location.getLongitude()+", "+address;
            ap[0]-=1;
            materialButton.setText("Submit");
            hideLoader(FinalAct.this);




        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(FinalAct.this,"Please on gps option",Toast.LENGTH_SHORT).show();
        hideLoader(FinalAct.this);
    }

}


