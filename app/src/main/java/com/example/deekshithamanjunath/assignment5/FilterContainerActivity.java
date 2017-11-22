package com.example.deekshithamanjunath.assignment5;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;

public class FilterContainerActivity extends AppCompatActivity implements CountryFilterFragment.SetFilterCountry, StateFilterFragment.SetFilterState{

    private String filterCountryFinal;
    private String filterStateFinal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_container);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;

        getWindow().setLayout((int) (width*.8), (int) (height*.9));

        int flag = getIntent().getExtras().getInt("Set");

        if (flag == 1) {
            FragmentManager fragmentManagerCountry = getSupportFragmentManager();
            FragmentTransaction filterCountrySelect = fragmentManagerCountry.beginTransaction();

            CountryFilterFragment countryFilterFragment = new CountryFilterFragment();
            filterCountrySelect.replace(R.id.filterFragmentContainer, countryFilterFragment);
            filterCountrySelect.commit();
        }
        else if(flag == 2)
        {
            Intent i = getIntent();

            String countryselected = i.getStringExtra("country");
            System.out.println(countryselected);

            Bundle bundle= new Bundle();
            bundle.putString("countryselection",countryselected);

            FragmentManager fragmentManagerState = getSupportFragmentManager();
            FragmentTransaction filterStateSelect = fragmentManagerState.beginTransaction();

            StateFilterFragment stateFilterFragment = new StateFilterFragment();
            stateFilterFragment.setArguments(bundle);
            filterStateSelect.replace(R.id.filterFragmentContainer, stateFilterFragment);
            filterStateSelect.commit();
        }
    }

    @Override
    public void setFilterCountry(String country) {
        filterCountryFinal = country;
        Intent intent = getIntent();
        intent.putExtra("CountryForFilter",filterCountryFinal);
        setResult(RESULT_OK,intent);
        finish();
    }

    @Override
    public void setFilterState(String state) {
        filterStateFinal = state;
        Intent intent=getIntent();
        intent.putExtra("StateForFilter",filterStateFinal);
        setResult(RESULT_OK,intent);
        finish();
    }
}
