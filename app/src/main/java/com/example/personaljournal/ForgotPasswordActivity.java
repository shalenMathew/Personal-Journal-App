package com.example.personaljournal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

public class ForgotPasswordActivity extends AppCompatActivity {

    Button savePasswordBtn;
    EditText enterEmail;
    ProgressBar forgerProgressbar;

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        

        savePasswordBtn=findViewById(R.id.savePasswordBtn);
        enterEmail=findViewById(R.id.enterEmail);
        forgerProgressbar=findViewById(R.id.forgetProgressBar);

        firebaseAuth=FirebaseAuth.getInstance();

        forgerProgressbar.setVisibility(View.INVISIBLE);


        Intent intent = getIntent();

        if(!intent.getStringExtra("email").isEmpty()){

            String email = intent.getStringExtra("email");
            enterEmail.setText(email);

        }

        
        savePasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sentResetEmail(enterEmail);

            }
        });
        
        
    }

    private void sentResetEmail(EditText enterEmail) {

        String currEmail = enterEmail.getText().toString().trim();

        if(!TextUtils.isEmpty(currEmail)){

            firebaseAuth.sendPasswordResetEmail(currEmail)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {

                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            forgerProgressbar.setVisibility(View.VISIBLE);

                            if (task.isSuccessful()){
                                Toast.makeText(ForgotPasswordActivity.this, "email sent successfully", Toast.LENGTH_SHORT).show();

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        finish();
                                    }
                                },2000);

                            }else {
                                Toast.makeText(ForgotPasswordActivity.this, "Couldn't find the user ", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


        }else{
            Toast.makeText(this, "enter email", Toast.LENGTH_SHORT).show();
        }


    }


}


