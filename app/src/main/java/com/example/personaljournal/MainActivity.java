package com.example.personaljournal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import util.JournalUser;


public class MainActivity extends AppCompatActivity {


    //    1. first add the google-service.json
    // while making google-service file dont forget to add SHA-1 no in for sign-in authentication, u can find it in Gradle
    // which is somewhere around here  (on left side)--->
    // Go to app-> task-> android->signingReport Run it or double click it the coy then sha-1 no and paste it in app
//     2.add dependency and plugin and classpath

    //3. main activity
    // 4.Register activity


    // FireBase Authentication
    // to give authentication  go to firebase site go to the ->journal app -> Build -> firebase authentication -> enable email/ password
//    also dont forget to add dependency


    // if wanna know about details of all fire base function check JournalApp in 'Firebase' folder
    private FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currUser;


    // firebase connection
    // Enable firestore through site  first
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    // now making reference that points toward 'Users'
    CollectionReference collectionReference = db.collection("Users");

    EditText emailET;
    EditText passwordET;
    Button loginBtn;
    Button createAccBtn;

    TextView forgotPassword;

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        emailET=findViewById(R.id.emailET);
        passwordET = findViewById(R.id.passwordET);

        createAccBtn =findViewById(R.id.createAccBtn);
        loginBtn = findViewById(R.id.loginBtn);

        forgotPassword=findViewById(R.id.forgotPasswordTxtView);
        progressBar=findViewById(R.id.progressBarLogin);

        progressBar.setVisibility(View.GONE);

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(MainActivity.this, ForgotPasswordActivity.class);

                if(emailET!=null) {
                    i.putExtra("email", emailET.getText().toString());
                }
                startActivity(i);

            }
        });


  createAccBtn.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {

        startActivity(new Intent(MainActivity.this, RegisterActivity.class));
    }
});


 loginBtn.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        progressBar.setVisibility(View.VISIBLE);
        LoginEmailAndPasswordUser(emailET.getText().toString().trim(),passwordET.getText().toString().trim());
    }
});
    }

    private void LoginEmailAndPasswordUser(String email, String password) {

if (!TextUtils.isEmpty(email)  && !TextUtils.isEmpty(password)){

    firebaseAuth.signInWithEmailAndPassword(email,password)
            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {


                    if(task.isSuccessful()){

                        currUser =firebaseAuth.getCurrentUser();
                        assert currUser != null;
                        final String currUserId = currUser.getUid();

                        collectionReference.whereEqualTo("userId",currUserId)
                                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                    @Override
                                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                                        if(error!=null){
                                            Log.w("collectionReference error","Error retrieving documents"+error.getMessage());
                                        }

                                        assert  value !=null;

                                        if( !value.isEmpty()){

                                            for(QueryDocumentSnapshot snapshot : value){

                                                JournalUser journalUser = JournalUser.getInstance();
                                                journalUser.setUsername(snapshot.getString("username"));
                                                journalUser.setUserId(snapshot.getString("userId"));

                                            }
                                            progressBar.setVisibility(View.GONE);
                                            startActivity(new Intent(MainActivity.this, JournalListActivity.class));

                                        }

                                    }
                                });

                    }else {

                        Toast.makeText(MainActivity.this, "Email or password is wrong", Toast.LENGTH_SHORT).show();

                    }



                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            });

}

    }


}