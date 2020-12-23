package com.sar.user.smart_city;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.tensorflow.lite.examples.textclassification.client.Result;
import org.tensorflow.lite.examples.textclassification.client.TextClassificationClient;

import java.util.List;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class FinalAct extends AppCompatActivity {
    TextView textView;
    private TextClassificationClient client;
    private MaterialButton materialButton;

    private Handler handler;
    private static ProgressDialog progress;
    private ImageView imageView;
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;
    private TextView textView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final);
        textView=findViewById(R.id.txvResult);
        imageView=findViewById(R.id.image);
        textView2=findViewById(R.id.textClass);

        client = new TextClassificationClient(getApplicationContext());
        handler = new Handler();
        Intent intent=getIntent();
        Random random=new Random();
        int aa=random.nextInt(100000);
        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference("review/"+ FirebaseAuth.getInstance().getUid().toString()+"/"+String.valueOf(aa));

        String text=intent.getStringExtra("text");
        String textclass=intent.getStringExtra("class");
        textView.setText(text);
        textView2.setText(textclass);


        byte[] d=intent.getByteArrayExtra("image");
        if(d.length>0) {
            imageView.setImageBitmap(BitmapFactory.decodeByteArray(d, 0, d.length));
        }

        //classify("nice");


        materialButton=findViewById(R.id.button);
        materialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoader(FinalAct.this);
                databaseReference.child("text").setValue(text);
                databaseReference.child("image").setValue(Base64.encodeToString(d, Base64.DEFAULT)).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(FinalAct.this,"Something went wrong",Toast.LENGTH_SHORT).show();

                        startActivity(new Intent(FinalAct.this,MainScreen.class));

                    }
                });
                databaseReference.child("textClass").setValue(textclass).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(FinalAct.this,"Done! Thank you",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(FinalAct.this,MainScreen.class));
                    }
                });
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

}


