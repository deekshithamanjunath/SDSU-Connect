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


public class StateFilterFragment extends Fragment implements AdapterView.OnItemClickListener{

    ArrayList<String> filterStateArray = new ArrayList<String>();

    ListView filterListStates;
    ArrayAdapter<String> flsAdapter;

    String filterCountrySelection;
    String filterStateSelection;
    View v;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

       v = inflater.inflate(R.layout.fragment_state_filter, container, false);
        filterListStates = (ListView) v.findViewById(R.id.filterStateList);
        filterListStates.setOnItemClickListener(this);

        Bundle countrySelected = getArguments();
        filterCountrySelection = countrySelected.getString("countryselection");

        RequestQueue requestFilterQState = VolleySingleton.getInstance().getVSRequestQueue();
        StringRequest filterStringReqState = new StringRequest(Request.Method.GET, "http://bismarck.sdsu.edu/hometown/states?country="+filterCountrySelection, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String[] al = response.replaceAll("]", "").replace("[", "").replace("\"", "").split(",");
                for(int i = 0; i < al.length; i++)
                {
                    filterStateArray.add(al[i]);
                }

                flsAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, filterStateArray);
                filterListStates.setAdapter(flsAdapter);
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), "Error" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        requestFilterQState.add(filterStringReqState);
        return v;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        filterStateSelection = parent.getItemAtPosition(position).toString();
        SetFilterState placeListerner = (SetFilterState) getActivity();
        placeListerner.setFilterState(filterStateSelection);
    }

    public interface SetFilterState
    {
        void setFilterState(String state);
    }

}
