package com.projects.tiji.instashare;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private EditText nameField;
    private EditText passField;
    private EditText emailField;
    private FirebaseAuth  mAuth;
    private DatabaseReference   mDatabase;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        nameField=(EditText)findViewById(R.id.nameField);
        passField=(EditText)findViewById(R.id.passField);
        emailField=(EditText)findViewById(R.id.emailField);
        mAuth=FirebaseAuth.getInstance();
        mDatabase= FirebaseDatabase.getInstance().getReference().child("Users");
        progressDialog=new ProgressDialog(RegisterActivity.this);
    }
    public void registerButtonClicked(View  view){
        final String  name=nameField.getText().toString().trim();
        String  email=emailField.getText().toString().trim();
        String  pass=passField.getText().toString().trim();
        if(!TextUtils.isEmpty(name)&&!TextUtils.isEmpty(email)&&!TextUtils.isEmpty(pass)){
            progressDialog.setTitle("Registering on InstaShare..... ");
            progressDialog.show();
            mAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        String  user_id=mAuth.getCurrentUser().getUid();
                        DatabaseReference current_user_db=mDatabase.child(user_id);
                        current_user_db.child("Name").setValue(name);
                        current_user_db.child("Image").setValue("default");
                        progressDialog.dismiss();
                        Intent  mainIntent=new Intent(RegisterActivity.this,LoginActivity.class);
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(mainIntent);
                    }
                }
            });


        }

    }


}
