package com.example.deekshithamanjunath.assignment5;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class ContainerActivity extends AppCompatActivity implements StateFragment.SetPlace, MapCoordinatesFragment.SetCoordinates{

    private String countryFinal,stateFinal;
    private double latitude, longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container);

        int flag = getIntent().getExtras().getInt("Set");

        if (flag == 1)
        {
            FragmentManager fragCountry = getSupportFragmentManager();
            FragmentTransaction countryList = fragCountry.beginTransaction();
            CountryFragment countries = new CountryFragment();

            countryList.replace(R.id.fragmentContainer,countries);
            countryList.commit();
        }

        else if (flag == 2)
        {
            MapCoordinatesFragment map = new MapCoordinatesFragment();
            FragmentManager mapping = getSupportFragmentManager();
            FragmentTransaction trans = mapping.beginTransaction();
            trans.replace(R.id.fragmentContainer,map);
            trans.addToBackStack(null);
            trans.commit();
        }
    }

    @Override
    public void setPlace(String country, String state) {
        countryFinal = country;
        stateFinal = state;
        Intent intent=getIntent();
        intent.putExtra("Country",countryFinal);
        intent.putExtra("State",stateFinal);
        setResult(RESULT_OK,intent);
        finish();
    }

    @Override
    public void set(double lat, double longi) {
        latitude = lat;
        longitude = longi;
        Intent back = getIntent();
        back.putExtra("Latitude", latitude);
        back.putExtra("Longitude", longitude);
        setResult(RESULT_OK,back);
        finish();
    }
}
