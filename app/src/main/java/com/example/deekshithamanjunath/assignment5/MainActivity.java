package com.example.deekshithamanjunath.assignment5;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    EditText eEmail, ePassword;
    Button logIn, signUp;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        eEmail = (EditText) findViewById(R.id.email);
        ePassword = (EditText) findViewById(R.id.password);

        eEmail.requestFocus();
        eEmail.setNextFocusDownId(R.id.password);

        logIn = (Button) findViewById(R.id.buttonlogin);
        logIn.setOnClickListener(this);
        signUp = (Button) findViewById(R.id.buttonRegister);
        signUp.setOnClickListener(this);

        progressDialog = new ProgressDialog(this);

        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser() != null)
        {
            Intent start = new Intent(this, ViewContainerActivity.class);
            startActivity(start);
            finish();
        }

        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
        {
        }
        else
        {
            Toast.makeText(this, "Error! Make Sure that the Device has Network Connection and Reopen the App.", Toast.LENGTH_SHORT).show();
            pauseThread.start();
        }
    }

    Thread pauseThread = new Thread(){
        @Override
        public void run() {
            try {
                Thread.sleep(3500);
                MainActivity.this.finish();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.buttonlogin:
                loggedIn();
                break;
            case R.id.buttonRegister:
                registered();
                break;
        }
    }

    public void loggedIn(){

        String logEmail = eEmail.getText().toString().trim();
        String logPasswd = ePassword.getText().toString().trim();

        if(TextUtils.isEmpty(logEmail))
        {
            eEmail.setError("Enter E-mail");
            eEmail.requestFocus();
        }

        else if (TextUtils.isEmpty(logPasswd))
        {
            ePassword.setError("Enter Password");
            ePassword.requestFocus();
        }
        else
        {
            progressDialog.setMessage("Logging In...");
            progressDialog.show();

            firebaseAuth.signInWithEmailAndPassword(logEmail,logPasswd).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    progressDialog.dismiss();
                    if(task.isSuccessful())
                    {
                        finish();
                        Intent start = new Intent(MainActivity.this, ViewContainerActivity.class);
                        startActivity(start);

                        Toast.makeText(MainActivity.this, "Login Successful! ", Toast.LENGTH_LONG).show();
                    }
                        else {
                        Toast.makeText(MainActivity.this, "Login Unsuccessful, Please check Email and Password.", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    public void registered()
    {
        Intent register = new Intent(this,RegisterActivity.class);
        startActivity(register);
        finish();
    }

}
