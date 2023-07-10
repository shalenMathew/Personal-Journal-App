package com.example.personaljournal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import util.JournalUser;


public class RegisterActivity extends AppCompatActivity {

    EditText username_create ;
    EditText email_create;
    EditText password_create;

    Button registerBtn;

    Button back;

    ProgressBar registerProgressBar ;

//Firebase Authentication
    FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
    FirebaseAuth.AuthStateListener authStateListener;
    FirebaseUser currUser;

    //Firestore
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    // Pointing to collection =>Users
CollectionReference collectionReference = db.collection("Users");



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        username_create = findViewById(R.id.username_create);
        email_create = findViewById(R.id.email_create);
        password_create = findViewById(R.id.password_create);
        registerBtn= findViewById(R.id.registerBtn);
        back = findViewById(R.id.back);
        registerProgressBar=findViewById(R.id.registerProgressBar);

        registerProgressBar.setVisibility(View.INVISIBLE);

        // authentication listener

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                currUser = firebaseAuth.getCurrentUser();

                if(currUser!=null){

                }else{

                }


            }
        };





        //REGISTER
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!TextUtils.isEmpty(username_create.getText()) && !TextUtils.isEmpty(email_create.getText())
                        && !TextUtils.isEmpty(password_create.getText())){

                    String username = username_create.getText().toString().trim();
                    String email = email_create.getText().toString().trim();
                    String password = password_create.getText().toString().trim();

                    CreateNewAccount(username,email,password);

                }else {

                    Toast.makeText(RegisterActivity.this, "Fields are empty", Toast.LENGTH_SHORT).show();

                }


            }
        });


        // NEXT
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


    }

    private void CreateNewAccount(String username, String email, String password) {


        registerProgressBar.setVisibility(View.VISIBLE);

        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){

                    currUser = firebaseAuth.getCurrentUser();
                    assert currUser != null;
                    final  String currUserId = currUser.getUid();

                    Map<String,String> userObj = new HashMap<>();
                    userObj.put("username",username);
                    userObj.put("userId",currUserId);

                    collectionReference.add(userObj).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {


                            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                    registerProgressBar.setVisibility(View.INVISIBLE);

                                    if(Objects.requireNonNull(task.getResult().exists())){

                                        String name = task.getResult().getString("username");
                                        String userId = task.getResult().getString("userId");

                                        // adding global variable
                                        JournalUser journalUser = JournalUser.getInstance();
                                        journalUser.setUsername(name);
                                        journalUser.setUserId(userId);

                                        Intent i = new Intent(RegisterActivity.this, AddJournalActivity.class);
                                        i.putExtra("username",name);
                                        i.putExtra("userId",userId);

                                        startActivity(i);


                                    }else{

                                    }


                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    registerProgressBar.setVisibility(View.INVISIBLE);
                                    Toast.makeText(RegisterActivity.this, "something went wrong", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    });

                }else {

                    // if account creation failed

                    registerProgressBar.setVisibility(View.INVISIBLE);
                    if(task.getException()!=null){

                        String error = task.getException().getMessage();
                        Toast.makeText(RegisterActivity.this, error, Toast.LENGTH_SHORT).show();

                    }

                }

            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();

        currUser = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);

    }
}