package com.example.deekshithamanjunath.assignment5;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ViewContainerActivity extends AppCompatActivity implements ListViewUsersFragment.SetUserListView, MapViewUserFragment.SetUserMapView {

    public String getCountry() {
        return country;
    }

    private String country;

    public int getYear() {
        return year;
    }

    public String getState() {

        return state;
    }

    private String state;
    private int year;
    String onlineUser;
    String currentLoggedUser;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseAuthLogged;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_container);

        FragmentManager fragmentManagerList = getSupportFragmentManager();
        FragmentTransaction listView = fragmentManagerList.beginTransaction();
        ListViewUsersFragment viewL = new ListViewUsersFragment();
        listView.replace(R.id.listViewUsers,viewL);
        listView.commit();


        firebaseAuthLogged = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseAuthLogged!=null) {
            currentLoggedUser = firebaseAuthLogged.getDisplayName();
        }

        FragmentManager fragmentManagerMap = getSupportFragmentManager();
        FragmentTransaction mapView = fragmentManagerMap.beginTransaction();
        MapViewUserFragment viewM = new MapViewUserFragment();
        mapView.replace(R.id.mapViewUsers,viewM);
        mapView.commit();

        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() == null)
        {
            Intent logout = new Intent(this,MainActivity.class);
            startActivity(logout);
            finish();
        }

        Intent filterValues = getIntent();
        if (filterValues != null) {
            country = filterValues.getStringExtra("CountryValue");
            state = filterValues.getStringExtra("StateValue");
            year = filterValues.getIntExtra("YearValue", 0);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflateMenu = getMenuInflater();
        inflateMenu.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.filter:
                Intent filterSelection = new Intent(this, Filter.class);
                startActivity(filterSelection);
                break;
            case R.id.chat:
                if(onlineUser !=null) {
                    Intent chat = new Intent(this,ChatActivity.class);
                    chat.putExtra("loggedUser", currentLoggedUser);
                    chat.putExtra("convoUser",onlineUser);
                    startActivity(chat);
                    finish();
                }
                else {
                    Toast.makeText(this,"Please Select A User to Chat With",Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.logout:
                firebaseAuth.signOut();
                finish();
                Intent logOut = new Intent(this, MainActivity.class);
                startActivity(logOut);
                Toast.makeText(this, "Logged Out.",Toast.LENGTH_SHORT).show();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setUserListView(Object name) {
        onlineUser = name.toString();
        System.out.println("user is "+onlineUser);
    }

    @Override
    public void setUserMapView(Object mapName) {
        onlineUser = mapName.toString();
        System.out.println("The user passed through interface is "+onlineUser);
    }
}

