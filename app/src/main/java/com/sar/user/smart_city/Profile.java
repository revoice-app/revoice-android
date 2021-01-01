package com.sar.user.smart_city;

import android.content.Intent;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.util.Log;
import android.view.InputEvent;
import android.view.View;
import android.widget.HorizontalScrollView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Profile extends AppCompatActivity {

    private AppCompatTextView name;
    private AppCompatTextView email;
    private AppCompatTextView team;
    private AppCompatTextView logout;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference myRef;
    private AppCompatImageView backIV;
    private RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);
        name=findViewById(R.id.name);
        email=findViewById(R.id.email);
        team=findViewById(R.id.team);
        logout=findViewById(R.id.logout);
        backIV=findViewById(R.id.backIV);
        recyclerView=findViewById(R.id.rect);


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(Profile.this, MainActivity.class));
            }
        });

        backIV.setOnClickListener(view -> {
            finish();
        });

        firebaseAuth = com.google.firebase.auth.FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser()!= null)
        {
            name.setText(firebaseAuth.getCurrentUser().getDisplayName());
            email.setText(firebaseAuth.getCurrentUser().getEmail());
        }

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef= database.getReference("review/"+FirebaseAuth.getInstance().getUid().toString());

        List<ModelReview> reviews=new ArrayList<>();
        myRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //here you will get the data
                        for(DataSnapshot dataSnapshot2:dataSnapshot.getChildren())
                        {

                                reviews.add(new ModelReview(dataSnapshot2.child("text").getValue().toString(),dataSnapshot2.child("image").getValue().toString(),dataSnapshot2.child("textClass").getValue().toString(),dataSnapshot2.child("location").getValue().toString(),dataSnapshot2.getKey(),dataSnapshot2.child("textSent").getValue().toString()));

                        }
                        recyclerView.setAdapter(new ReviewAdap(Profile.this,reviews));

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));



//        myRef.child(FirebaseAuth.getInstance().getUid().toString()).gaddOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                Log.d("popo","oo");
//            }
//        });

//        myRef.child("review").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//
//                for(DataSnapshot uniqueKeySnapshot : dataSnapshot.getChildren()){
//                    //Loop 1 to go through all the child nodes of users
//                    for(DataSnapshot booksSnapshot : uniqueKey.child("Books").getChildren()){
//                        //loop 2 to go through all the child nodes of books node
//                        String bookskey = booksSnapshot.getKey();
//                        String booksValue = booksSnapshot.getValue();
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
    }

    private void collectReviews(Map<String, Object> value) {
        ArrayList<Long> review = new ArrayList<>();

        //iterate through each user, ignoring their UID
        for (Map.Entry<String, Object> entry : value.entrySet()){

            //Get user map
            Map singleUser = (Map) entry.getValue();
            //Get phone field and append to list
            review.add((Long) singleUser.get("Input"));
        }

        team.setText(review.toString());
    }
}
