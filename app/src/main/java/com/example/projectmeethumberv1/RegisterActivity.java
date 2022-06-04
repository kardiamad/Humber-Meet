package com.example.projectmeethumberv1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    public static final String TAG = "TAG";
    Button btnLoginReg, btnRegister;
    EditText txtNameReg, txtEmailAddressReg, txtPasswordReg, txtConfirmPasswordReg, txtPhoneReg;
    FirebaseAuth fAuth;
    ProgressBar progressBar;
    FirebaseFirestore fStore;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        btnLoginReg = findViewById(R.id.btnLoginReg);
        btnRegister = findViewById(R.id.btnRegisterReg);
        txtNameReg = findViewById(R.id.txtNameReg);
        txtEmailAddressReg = findViewById(R.id.txtEmailAddressReg);
        txtPhoneReg = findViewById(R.id.txtPhoneReg);
        txtPasswordReg = findViewById(R.id.txtPasswordReg);
        txtConfirmPasswordReg = findViewById(R.id.txtConfirmPasswordReg);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        progressBar = findViewById(R.id.progressBarReg);

        // send to homescreen if already logged in
        if(fAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }



        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Validating data
                String name = txtNameReg.getText().toString().trim();
                String email = txtEmailAddressReg.getText().toString().trim();
                String password = txtPasswordReg.getText().toString().trim();
                String confirmPassword = txtConfirmPasswordReg.getText().toString().trim();
                String phone = txtPhoneReg.getText().toString().trim();

                if(name.isEmpty()){
                    txtNameReg.setError("Please enter name");
                    return;
                }
                if(email.isEmpty()){
                    txtEmailAddressReg.setError("Please enter an email");
                    return;
                }
                if(phone.isEmpty()){
                    txtEmailAddressReg.setError("Please enter a phone number");
                    return;
                }
                if(password.isEmpty()){
                    txtPasswordReg.setError("Please enter a password");
                    return;
                }
                if(confirmPassword.isEmpty()){
                    txtConfirmPasswordReg.setError("Please confirm the password by entering it again");
                    return;
                }

                if(!password.equals(confirmPassword)){
                    txtConfirmPasswordReg.setError("Password does not match");
                    return;
                }

                // Control will come here only when data is validated
                progressBar.setVisibility(View.VISIBLE);

//                fAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
//                    @Override
//                    public void onSuccess(AuthResult authResult) {
//                        // send user to next page if register successfull
//                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
//                        finish();
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                });

                fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            // send verification link
                            FirebaseUser fUser = fAuth.getCurrentUser();
                            fUser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(RegisterActivity.this, "Verification Email Has been Sent.", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "onFailure: Email not sent " + e.getMessage());
                                }
                            });

                            Toast.makeText(RegisterActivity.this, "User Created.", Toast.LENGTH_SHORT).show();
                            userID = fAuth.getCurrentUser().getUid();
                            DocumentReference documentReference = fStore.collection("users").document(userID);
                            Map<String,Object> user = new HashMap<>();
                            user.put("name",name);
                            user.put("email",email);
                            user.put("phone",phone);
                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "onSuccess: user Profile is created for "+ userID);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "onFailure: " + e.toString());
                                }
                            });
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));

                        }else {
                            Toast.makeText(RegisterActivity.this, "Error ! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });

        // send to login page
        btnLoginReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent loginPageIntent = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(loginPageIntent);

            }
        });

    }
}