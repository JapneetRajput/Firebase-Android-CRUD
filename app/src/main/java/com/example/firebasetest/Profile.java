package com.example.firebasetest;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class Profile extends AppCompatActivity {

    EditText name,address,number;
    Button submit;

    FirebaseDatabase db = FirebaseDatabase.getInstance();
    DatabaseReference reference = db.getReference().child("Users");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        name = findViewById(R.id.nameEt);
        number = findViewById(R.id.number_Et);
        address = findViewById(R.id.addressEt);
        submit = findViewById(R.id.button2);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Name = name.getText().toString();
                String Number = number.getText().toString();
                String Address = address.getText().toString();

                HashMap<String,String> userMap = new HashMap<>();
                userMap.put("Name", Name);
                userMap.put("Address", Address);
                userMap.put("Number", Number);

                reference.push().setValue(userMap);

                Toast.makeText(Profile.this, "Details uploaded!", Toast.LENGTH_SHORT).show();
                name.setText("");
                number.setText("");
                address.setText("");
            }
        });

    }
}