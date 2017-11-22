package com.example.deekshithamanjunath.assignment5;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.ArrayList;

public class CountryFilterFragment extends Fragment implements AdapterView.OnItemClickListener{

    ArrayList<String> filterCountryArray = new ArrayList<String>();

    ListView filterListCountries;
    ArrayAdapter<String> flvAdapter;

    String setFilterCountry;
    View v;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.fragment_country_filter, container, false);
        filterListCountries = (ListView) v.findViewById(R.id.filterCountryList);

        filterListCountries.setOnItemClickListener(this);

        RequestQueue requestFilterQ = VolleySingleton.getInstance().getVSRequestQueue();
        StringRequest filterStringReq = new StringRequest(Request.Method.GET, "http://bismarck.sdsu.edu/hometown/countries", new Response.Listener<String>() {
            @Override
            public void onResponse(String response)
            {
                String[] al = response.replaceAll("]", "").replace("[", "").replace("\"", "").split(",");
                for(int i = 0; i < al.length; i++)
                {
                    filterCountryArray.add(al[i]);
                }

                flvAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, filterCountryArray);
                filterListCountries.setAdapter(flvAdapter);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), "Error" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        requestFilterQ.add(filterStringReq);
        return v;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        setFilterCountry = parent.getItemAtPosition(position).toString();

        Bundle countryBundle = new Bundle();
        countryBundle.putString("filterState", setFilterCountry);

        StateFilterFragment filterStates = new StateFilterFragment();
        filterStates.setArguments(countryBundle);
        SetFilterCountry placeListerner = (SetFilterCountry) getActivity();
        placeListerner.setFilterCountry(setFilterCountry);

    }

    public interface SetFilterCountry
    {
        void setFilterCountry(String country);
    }

}
