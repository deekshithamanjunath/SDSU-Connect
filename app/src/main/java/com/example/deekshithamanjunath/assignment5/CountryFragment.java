package com.example.deekshithamanjunath.assignment5;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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


public class CountryFragment extends Fragment implements AdapterView.OnItemClickListener
{
    ArrayList<String> countries = new ArrayList<String>();
    ArrayAdapter<String> lvAdapter;

    ListView listCountries;
    String setCountry;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_country, container, false);
        listCountries = (ListView) v.findViewById(R.id.countryList);
        listCountries.setOnItemClickListener(this);

        RequestQueue requestQ = VolleySingleton.getInstance().getVSRequestQueue();
        StringRequest stringReq = new StringRequest(Request.Method.GET, "http://bismarck.sdsu.edu/hometown/countries", new Response.Listener<String>() {
            @Override
            public void onResponse(String response)
            {
                String[] al = response.replaceAll("]", "").replace("[", "").replace("\"", "").split(",");
                for(int i = 0; i < al.length; i++)
                {
                    countries.add(al[i]);
                }

                lvAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, countries);
                listCountries.setAdapter(lvAdapter);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), "Error" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        requestQ.add(stringReq);
        return v;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        setCountry = parent.getItemAtPosition(position).toString();
        Bundle countryBundle = new Bundle();

        countryBundle.putString("state", setCountry);
        StateFragment states = new StateFragment();
        states.setArguments(countryBundle);

        FragmentManager stateFrag = getFragmentManager();
        FragmentTransaction transState = stateFrag.beginTransaction();
        transState.replace(R.id.fragmentContainer,states);
        transState.commit();
    }
}
