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

public class StateFragment extends Fragment implements AdapterView.OnItemClickListener
{
    ArrayList<String> stateList = new ArrayList<String>();
    ListView listVState;
    ArrayAdapter<String> lvsAdapter;

    String countrySelection;
    String stateSelection;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View vState = inflater.inflate(R.layout.fragment_state, container, false);
        listVState = (ListView) vState.findViewById(R.id.stateList);
        listVState.setOnItemClickListener(this);

        Bundle stateBundle = getArguments();
        countrySelection = stateBundle.getString("state");

        RequestQueue requestQState = VolleySingleton.getInstance().getVSRequestQueue();
        StringRequest stringReqState = new StringRequest(Request.Method.GET, "http://bismarck.sdsu.edu/hometown/states?country="+countrySelection, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String[] al = response.replaceAll("]", "").replace("[", "").replace("\"", "").split(",");
                for(int i = 0; i < al.length; i++)
                {
                    stateList.add(al[i]);
                }
                lvsAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, stateList);
                listVState.setAdapter(lvsAdapter);
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), "Error" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        requestQState.add(stringReqState);
        return vState;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        stateSelection = parent.getItemAtPosition(position).toString();
        SetPlace placeListerner = (SetPlace) getActivity();
        placeListerner.setPlace(countrySelection,stateSelection);
    }

    public interface SetPlace
    {
        public void setPlace(String country, String state);
    }
}
