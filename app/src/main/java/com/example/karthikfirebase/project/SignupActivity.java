package com.example.karthikfirebase.project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.karthikfirebase.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignupActivity extends AppCompatActivity {

    EditText mail,pass;
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;
    String mailStr,passwordStr;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        mail=findViewById(R.id.email);
        pass=findViewById(R.id.password);

        firebaseAuth=FirebaseAuth.getInstance();
        progressDialog=new ProgressDialog(this);
        progressDialog.setCancelable(false);

        sharedPreferences=getSharedPreferences("LOGIN",MODE_PRIVATE);
        editor=sharedPreferences.edit();

        findViewById(R.id.signup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createUser();
            }
        });

    }

    private void createUser() {
        progressDialog.show();
        progressDialog.setMessage("Creating user.....");

        mailStr=mail.getText().toString();
        passwordStr=pass.getText().toString();

        firebaseAuth.createUserWithEmailAndPassword(mailStr,passwordStr).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressDialog.dismiss();
                if(!task.isSuccessful())
                {
                    Toast.makeText(SignupActivity.this, ""+task.getException(), Toast.LENGTH_SHORT).show();
                }
                else
                {
                    editor.putString("usermail",mailStr);
                    editor.putBoolean("status",true);
                    editor.commit();
                    finish();
                    startActivity(new Intent(SignupActivity.this,HomeActivity.class));
                    Toast.makeText(SignupActivity.this, "Successfully Signed Up", Toast.LENGTH_SHORT).show();
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(SignupActivity.this, ""+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();


            }
        });

    }
}