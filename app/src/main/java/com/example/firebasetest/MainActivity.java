package com.example.firebasetest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.firebasetest.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    EditText name,address,number;
    Button test;

    FirebaseDatabase db = FirebaseDatabase.getInstance();
    DatabaseReference reference = db.getReference().child("Users");

    private ActivityMainBinding binding;

    private PhoneAuthProvider.ForceResendingToken forceResendingToken;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks nCallbacks;

    private String nVerificationId;

    private static final String TAG ="MAIN_TAG";

    private FirebaseAuth firebaseAuth;

    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        name = findViewById(R.id.nameEt);
//        number = findViewById(R.id.phoneEt);
//        address = findViewById(R.id.addressEt);
//        test = findViewById(R.id.test);

//        binding.test.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(),Profile.class);
//                startActivity(intent);
//            }
//        });

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.phoneL1.setVisibility((View.VISIBLE));
        binding.codeL1.setVisibility(View.GONE);
//        binding.phoneContinueBtn.setVisibility(View.GONE);

        firebaseAuth = FirebaseAuth.getInstance();

        pd= new ProgressDialog(this);
        pd.setTitle("Please wait...");
        pd.setCanceledOnTouchOutside(false);

        binding.test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Profile.class));
            }
        });

        nCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                  /*This callback will be invoked in two situations:
                  * 1-Instant verification. In some cases the phone number can be instantly
                  *     verified without needing to send or enter a verification code.
                  * 2-Auto-retrieval. On some devices Google Play services can automatically
                  *     detect the incoming verification SMS and perform verification without
                  *     user action.*/
                    signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                /* This callback is invoked in an invalid request for verification is made,
                    for instance if the phone number format is not valid. */
                    pd.dismiss();
                Toast.makeText(MainActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                super.onCodeSent(verificationId, forceResendingToken);
                /* The SMS verification code has been sent to the provided phone number, we
                    now need to ask the user to enter the code and then construct a credential
                    by combining the code with a verification ID.*/
                Log.d(TAG, "OnCodeSent: " + verificationId);

                nVerificationId = verificationId;
                forceResendingToken = token;
                pd.dismiss();

                binding.phoneL1.setVisibility(View.GONE);
                binding.codeL1.setVisibility(View.VISIBLE);

                Toast.makeText(MainActivity.this, "Verification code sent", Toast.LENGTH_SHORT).show();
            }
        };

        binding.phoneContinueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String Name = binding.nameEt.getText().toString();
                String Number = binding.phoneEt.getText().toString();
                String Address = binding.addressEt.getText().toString();

                HashMap<String,String> userMap = new HashMap<>();
                userMap.put("Name", Name);
                userMap.put("Address", Address);
                userMap.put("Number", Number);

                if(Name.isEmpty()||Address.isEmpty()){
                    Toast.makeText(getApplicationContext(), "All fields are mandatory!", Toast.LENGTH_SHORT).show();
                }
                else {

                    String phone = Number;
                    if(TextUtils.isEmpty(phone)){
                        Toast.makeText(MainActivity.this, "Please enter valid Phone number", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        startPhoneNumberVerification(phone);
                        reference.push().setValue(userMap);
                    }
                }

            }
        });

        binding.resendCodeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = binding.phoneEt.getText().toString().trim();
                if(TextUtils.isEmpty(phone)){
                    Toast.makeText(MainActivity.this, "Please enter phone number", Toast.LENGTH_SHORT).show();
                }
                else{
                    resendVerification(phone, forceResendingToken);
                }
            }
        });

        binding.codeSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = binding.codeEt.getText().toString().trim();
                if(TextUtils.isEmpty(code)){
                    Toast.makeText(MainActivity.this, "Please enter Verification code", Toast.LENGTH_SHORT).show();
                }
                else{
                    verifyPhoneNumberWithCode(nVerificationId, code);
                }
            }
        });
    }


    private void startPhoneNumberVerification(String phone) {
        pd.setMessage("Verifying Phone Number");
        pd.show();

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(firebaseAuth)
                .setPhoneNumber(phone)
                .setTimeout(68L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(nCallbacks)
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void resendVerification(String phone, PhoneAuthProvider.ForceResendingToken token) {
        pd.setMessage("Resending Code");
        pd.show();

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(firebaseAuth)
                        .setPhoneNumber(phone)
                        .setTimeout(68L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(nCallbacks)
                        .setForceResendingToken(token)
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        pd.setMessage("Verifying Code");
        pd.show();

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId,code);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        pd.setMessage("Logging in");

        firebaseAuth.signInWithCredential(credential)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        pd.dismiss();
                        String phone = firebaseAuth.getCurrentUser().getPhoneNumber();
                        Toast.makeText(MainActivity.this, "Logged in as " +phone, Toast.LENGTH_SHORT).show();

                        startActivity(new Intent(MainActivity.this, Profile.class));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(MainActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}