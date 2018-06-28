package com.carpenoctem.myapplication1;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Lakshay Singhla on 25-Oct-17.
 */

public class SplashScreen extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private int index =1;
    private String check, num;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        num = getIntent().getStringExtra("number");

        mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
           @Override
           public void onDataChange(DataSnapshot dataSnapshot) {
               for(index = 1;index<5;index++){
                   check = dataSnapshot.child(num).child("bus" + index).child("check").getValue(String.class);
                   if(check == null || !check.equals("false")){
                       mDatabase.child(num).child("bus" + index).child("check").setValue("false");
                       Intent intent = new Intent(SplashScreen.this , MapsActivity.class);
                       intent.putExtra("index",index);
                       intent.putExtra("number",num);
                       startActivity(intent);
                       finish();
                       break;
                   }
               }
           }

           @Override
           public void onCancelled(DatabaseError databaseError) {

           }
       });
    }

}
