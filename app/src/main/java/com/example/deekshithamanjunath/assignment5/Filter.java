package com.example.deekshithamanjunath.assignment5;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Filter extends AppCompatActivity implements View.OnClickListener{

    EditText filterValueCountry, filterValueState, filterValueYear;
    Button filterSetCountry, filterSetState, filterDone;

    public static final int returnFilterCountryCode = 50;
    public static final int returnFilterStateCode = 60;

    String filteredCountryValue, filteredStateValue;
    int filteredYearValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        filterValueCountry = (EditText) findViewById(R.id.filterCountry);
        filterValueState = (EditText) findViewById(R.id.filterState);
        filterValueYear = (EditText) findViewById(R.id.filterYear);

        filterSetCountry = (Button) findViewById(R.id.buttonFilterCountrySet);
        filterSetCountry.setOnClickListener(this);

        filterSetState = (Button) findViewById(R.id.buttonFilterStateSet);
        filterSetState.setOnClickListener(this);

        filterDone = (Button) findViewById(R.id.buttonFilterDone);
        filterDone.setOnClickListener(this);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;

        getWindow().setLayout((int) (width*.8), (int) (height*.9));
    }

    public void setCountry()
    {
        Intent select = new Intent(this,FilterContainerActivity.class);
        Bundle countryFlag = new Bundle();
        countryFlag.putInt("Set", 1);
        select.putExtras(countryFlag);
        startActivityForResult(select,returnFilterCountryCode);

    }

    public void setState()
    {
        if ((filterValueCountry.getText().toString().equals(null) || (filterValueCountry.getText().toString().equals(""))))
        {
            Toast.makeText(this,"Cannot Select State with Empty Country Field!",Toast.LENGTH_LONG).show();
        }
        else {
            Intent select = new Intent(this, FilterContainerActivity.class);
            Bundle stateFlag = new Bundle();
            stateFlag.putInt("Set", 2);
            select.putExtras(stateFlag);
            select.putExtra("country", filterValueCountry.getText().toString());
            startActivityForResult(select, returnFilterStateCode);
        }
    }

    public void applyFilter()
    {
        filteredCountryValue = filterValueCountry.getText().toString();
        filteredStateValue = filterValueState.getText().toString();
        String year = filterValueYear.getText().toString();

        if ((year.equals(null)) ||year.equals(""))
        {
            Intent sort = new Intent(this, ViewContainerActivity.class);
            sort.putExtra("CountryValue", filteredCountryValue);
            sort.putExtra("StateValue", filteredStateValue);
            sort.putExtra("YearValue", filteredYearValue);
            startActivity(sort);
            finish();
        }
        else if (year.length()<4||year.length()>4)
        {
            filterValueYear.setError("Error! Enter valid length for year!");
            filterValueYear.requestFocus();

        }
        else if ((Integer.parseInt(year) < 1970) || (Integer.parseInt(year) > 2017)) {
            filterValueYear.setError("Error! Date must be between 1970 and 2017.");
            filterValueYear.requestFocus();
        }
        else {
            filteredYearValue = Integer.parseInt(year);

            Log.d("Tag", "Working");
            Intent sort = new Intent(this, ViewContainerActivity.class);
            sort.putExtra("CountryValue", filteredCountryValue);
            sort.putExtra("StateValue", filteredStateValue);
            sort.putExtra("YearValue", filteredYearValue);
            startActivity(sort);
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonFilterCountrySet:
                setCountry();
                break;

            case R.id.buttonFilterStateSet:
                setState();
                break;

            case R.id.buttonFilterDone:
                applyFilter();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 50)
        {
            switch (resultCode)
            {
                case RESULT_OK:
                    filterValueCountry.setText(data.getStringExtra("CountryForFilter").toString());
                    break;
                case RESULT_CANCELED:
                    break;
                default:
                    break;
            }
        }
        else if(requestCode == 60)
        {
            switch (resultCode)
            {
                case RESULT_OK:
                    filterValueState.setText(data.getStringExtra("StateForFilter").toString());
                    break;
                case RESULT_CANCELED:
                    break;
                default:
                    break;
            }
        }
    }
}
