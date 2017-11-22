package com.example.deekshithamanjunath.assignment5;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    Button countryValue, cityValue, submit;

    EditText enickname;
    EditText e_email;
    EditText epassword;
    EditText ecountry;
    EditText estate;
    EditText ecity;
    EditText eyear;

    String fnickname, f_email, fpassword, fcountry, fstate, fcity, year;

    double flatitude = 0.0, flongitude = 0.0;

    public static final int returnCode = 10;
    public static final int returnMapCode = 20;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        countryValue = (Button) findViewById(R.id.buttonCountryState);
        countryValue.setOnClickListener(this);
        cityValue = (Button) findViewById(R.id.buttonCity);
        cityValue.setOnClickListener(this);
        submit = (Button) findViewById(R.id.buttonRegistrationSubmit);
        submit.setOnClickListener(this);

        enickname = (EditText) findViewById(R.id.nickName);
        e_email = (EditText) findViewById(R.id.emailAddress);
        epassword = (EditText) findViewById(R.id.passwordRegister);
        ecountry = (EditText) findViewById(R.id.countryName);
        estate = (EditText) findViewById(R.id.stateName);
        ecity = (EditText) findViewById(R.id.cityName);
        eyear = (EditText) findViewById(R.id.year);

        enickname.requestFocus();
        enickname.setNextFocusDownId(R.id.emailAddress);
        e_email.setNextFocusDownId(R.id.passwordRegister);
        epassword.setNextFocusDownId(R.id.cityName);
        ecity.setNextFocusDownId(R.id.year);

        progressDialog = new ProgressDialog(this);

        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() != null)
        {
            Intent start = new Intent(this, ViewContainerActivity.class);
            startActivity(start);
            finish();
        }

    }

    public void setLocation() {
        Intent navigate = new Intent(this, ContainerActivity.class);
        Bundle flag = new Bundle();
        flag.putInt("Set", 1);
        navigate.putExtras(flag);
        startActivityForResult(navigate, returnCode);
    }

    public void getCoordinate() {
        Intent mapping = new Intent(this, ContainerActivity.class);
        Bundle flag = new Bundle();
        flag.putInt("Set", 2);
        mapping.putExtras(flag);
        startActivityForResult(mapping, returnMapCode);
    }


    public void submitForm() {
        fnickname = enickname.getText().toString();
        f_email = e_email.getText().toString();
        fpassword = epassword.getText().toString();
        fcountry = ecountry.getText().toString();
        fstate = estate.getText().toString();
        fcity = ecity.getText().toString();
        year = eyear.getText().toString();

        if (TextUtils.isEmpty(fnickname)) {
            enickname.setError("Enter Valid Nickname!");
            enickname.requestFocus();
        } else if ((TextUtils.isEmpty(f_email))) {
            e_email.setError("Enter E-mail");
            e_email.requestFocus();
        } else if ((TextUtils.isEmpty(fpassword)) || (fpassword.length() < 3)) {
            epassword.setError("Enter Valid Password!");
            enickname.requestFocus();
        } else if (TextUtils.isEmpty(fcountry)) {
            ecountry.setError("Enter User's Country!");
            ecountry.requestFocus();
        } else if (TextUtils.isEmpty(fstate)) {
            estate.setError("Enter User's State!");
            estate.requestFocus();
        } else if (TextUtils.isEmpty(fcity)) {
            ecity.setError("Enter User's City!");
            ecity.requestFocus();
        } else if (TextUtils.isEmpty(year)) {
            eyear.setError("Please Enter Joining Year!");
            eyear.requestFocus();
        } else if ((Integer.parseInt(year) < 1970) || (Integer.parseInt(year) > 2017)) {
            eyear.setError("Error! Date must be between 1970 and 2017.");
            eyear.requestFocus();
        } else if ((flatitude == 0.0) || (flongitude == 0.0)) {
            geoLocator();
        } else {
            JSONObject formQueries = new JSONObject();
            try {
                int intYear = Integer.parseInt(year);
                formQueries.put("nickname", fnickname);
                formQueries.put("password", fpassword);
                formQueries.put("country", fcountry);
                formQueries.put("state", fstate);
                formQueries.put("year", intYear);
                formQueries.put("city", fcity);
                formQueries.put("latitude", flatitude);
                formQueries.put("longitude", flongitude);

            } catch (JSONException error) {
                Log.i("rew", "error", error);
            }

            RequestQueue postServer = VolleySingleton.getInstance().getVSRequestQueue();
            JsonObjectRequest jsObj = new JsonObjectRequest(Request.Method.POST, "http://bismarck.sdsu.edu/hometown/adduser", formQueries, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    enickname.setError("User Nickname already exists! Please chose a different name.");
                    enickname.requestFocus();
                }
            });
            postServer.add(jsObj);
        }

    }

    public void firebaseRegistration()
    {

        String email = e_email.getText().toString().trim();
        String passwd = epassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            e_email.setError("Enter E-mail");
            e_email.requestFocus();
        } else if (TextUtils.isEmpty(passwd)) {
            epassword.setError("Enter Password");
            epassword.requestFocus();
        } else {
            progressDialog.setMessage("Registering...");
            progressDialog.show();

            firebaseAuth.createUserWithEmailAndPassword(email, passwd).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        firebaseUser = task.getResult().getUser();
                        databaseReference = FirebaseDatabase.getInstance().getReference("userattributes");

                        String nick = enickname.getText().toString();
                        databaseReference.child(firebaseUser.getUid()).child("nickname").setValue(nick);

                        final UserProfileChangeRequest profileChange = new UserProfileChangeRequest.Builder()
                                .setDisplayName(nick).build();

                        firebaseUser.updateProfile(profileChange).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    progressDialog.dismiss();
                                    Intent form = new Intent(RegisterActivity.this, ViewContainerActivity.class);
                                    startActivity(form);
                                    finish();
                                    Toast.makeText(RegisterActivity.this, "Registration Complete, Welcome to SDSU Connect!", Toast.LENGTH_LONG).show();

                                } else {
                                    Toast.makeText(RegisterActivity.this, "Registration Incomplete, Please try again.", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                    else {
                    }
                }
            });
        }

    }

    public void geoLocator()
    {
        Geocoder locator = new Geocoder(this);
        try {
            List<Address> position =
                    locator.getFromLocationName(fstate+ "," + fcountry,1);
            for (Address point : position)
            {
                flatitude = point.getLatitude();
                flongitude = point.getLongitude();
                Toast.makeText(getApplicationContext(),"Are you sure about the Information Entered? If yes Click on Submit again.",Toast.LENGTH_LONG).show();
            }

        } catch (Exception error)
        {
            Log.e("rew", "Address lookup Error", error);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonCountryState:
                setLocation();
                break;

            case R.id.buttonCity:
                getCoordinate();
                break;

            case R.id.buttonRegistrationSubmit:
                submitForm();
                firebaseRegistration();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 10)
        {
            switch (resultCode)
            {
                case RESULT_OK:
                    ecountry.setText(data.getStringExtra("Country").toString());
                    estate.setText(data.getStringExtra("State").toString());
                    break;
                case RESULT_CANCELED:
                    break;
                default:
                    break;
            }
        }
        else {
            switch (resultCode)
            {
                case RESULT_OK:
                    flatitude = data.getDoubleExtra("Latitude",0.0);
                    flongitude = data.getDoubleExtra("Longitude",0.0);
                    break;
                case RESULT_CANCELED:
                    break;
                default:
                    break;
            }

        }
    }
}
