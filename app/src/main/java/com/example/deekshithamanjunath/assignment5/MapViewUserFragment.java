package com.example.deekshithamanjunath.assignment5;

import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MapViewUserFragment extends Fragment implements OnMapReadyCallback, View.OnClickListener, GoogleMap.OnInfoWindowClickListener {

    View v;
    Button refresh, load;

    UserAttributes userFieldsMapping;

    ArrayList<String> firebaseArray;
    ArrayList<Integer> userIds = new ArrayList<>();
    ArrayList<Integer> filterUserIds;

    String filteredCountryValue, filteredStateValue;
    int filteredYearValue;

    ViewContainerActivity filterValues;

    private boolean flag = false;
    private boolean checkLoggedUser = false;

    private GoogleMap mGoogleMap;
    MapView mMapView;

    private FirebaseUser firebaseAuthLoggedMap;
    String currentLoggedUserMap;
    DataBase dataBaseMap;

    int count = 100;
    int afterid, beforeid;

    String name;
    double latitude = 0.0, longitude = 0.0;

    String filterName;
    double filterLatitude=0.0,filterLongitude=0.0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.fragment_map_view_user, container, false);
        filterValues = (ViewContainerActivity) getActivity();

        refresh = (Button) v.findViewById(R.id.buttonMapViewRefresh);
        refresh.setOnClickListener(this);

        load = (Button) v.findViewById(R.id.buttonMapViewLoadMore);
        load.setOnClickListener(this);

        mMapView = (MapView) v.findViewById(R.id.mapUsers);

        firebaseAuthLoggedMap = FirebaseAuth.getInstance().getCurrentUser();
        currentLoggedUserMap = firebaseAuthLoggedMap.getDisplayName();

        filteredCountryValue = filterValues.getCountry();
        filteredStateValue = filterValues.getState();
        filteredYearValue = filterValues.getYear();
        System.out.println(filteredYearValue);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();
        mMapView.getMapAsync(this);
    }

    public void getNextId() {
        RequestQueue requestId = VolleySingleton.getInstance().getVSRequestQueue();
        StringRequest requestForId = new StringRequest("http://bismarck.sdsu.edu/hometown/nextid", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                int id = value(Integer.parseInt(response));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        requestId.add(requestForId);
    }

    public int value(int id) {
        beforeid = id;
        afterid = beforeid - 25;
        mapPlottingGetLocations("http://bismarck.sdsu.edu/hometown/users?reverse=true&afterid=" + afterid + "&beforeid=" + beforeid);
        return id;
    }

    public void mapPlottingGetLocations(String url) {
        dataBaseMap = new DataBase(getActivity());

        RequestQueue mapRequest = VolleySingleton.getInstance().getVSRequestQueue();
        JsonArrayRequest userMapRequestArray = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    userFieldsMapping = new UserAttributes();
                    try {
                        JSONObject jsonObjMapper = response.getJSONObject(i);
                        double latCheck = jsonObjMapper.getDouble("latitude");
                        double longCheck = jsonObjMapper.getDouble("longitude");

                        if ((latCheck == 0.0) && (longCheck == 0.0)) {
                            double settingLat = 0.0, settingLong = 0.0;
                            String zeroLatCountry = jsonObjMapper.getString("country");
                            String zeroLatState = jsonObjMapper.getString("state");
                            try {

                                double[] coordinates = geocoder(zeroLatState, zeroLatCountry);
                                settingLat = coordinates[0];
                                settingLong = coordinates[1];

                            } catch (Exception error) {
                                Log.e("rew", "Address lookup Error", error);
                            }

                            dataBaseMap.addUsers(jsonObjMapper.getString("nickname"), jsonObjMapper.getString("city"), settingLong
                                    , jsonObjMapper.getString("state"), jsonObjMapper.getInt("year"), jsonObjMapper.getInt("id"), settingLat,
                                    jsonObjMapper.getString("time-stamp"), jsonObjMapper.getString("country"));

                        } else {
                            dataBaseMap.addUsers(jsonObjMapper.getString("nickname"), jsonObjMapper.getString("city"), jsonObjMapper.getDouble("longitude")
                                    , jsonObjMapper.getString("state"), jsonObjMapper.getInt("year"), jsonObjMapper.getInt("id"), jsonObjMapper.getDouble("latitude"),
                                    jsonObjMapper.getString("time-stamp"), jsonObjMapper.getString("country"));

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                if (userIds != null) {

                    userIds.clear();
                }

                Cursor sort = dataBaseMap.sortData();
                if (sort != null) {
                    while (sort.moveToNext()) {
                        if (!sort.getString(0).equals("")) {
                            name = sort.getString(0);
                            longitude = sort.getDouble(2);
                            latitude = sort.getDouble(6);
                            plotMap(latitude, longitude, name);
                            userIds.add(sort.getInt(5));
                        }
                    }
                    sort.close();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        mapRequest.add(userMapRequestArray);
    }


    public void filterMapPlottingGetLocations(String url) {
        dataBaseMap = new DataBase(getActivity());

        RequestQueue mapRequest = VolleySingleton.getInstance().getVSRequestQueue();
        JsonArrayRequest userMapRequestArray = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    userFieldsMapping = new UserAttributes();
                    try {
                        JSONObject jsonObjMapper = response.getJSONObject(i);
                        double latCheck = jsonObjMapper.getDouble("latitude");
                        double longCheck = jsonObjMapper.getDouble("longitude");

                        if ((latCheck == 0.0) && (longCheck == 0.0)) {
                            double settingLat = 0.0, settingLong = 0.0;
                            String zeroLatCountry = jsonObjMapper.getString("country");
                            String zeroLatState = jsonObjMapper.getString("state");
                            try {

                                double[] coordinates = geocoder(zeroLatState, zeroLatCountry);
                                settingLat = coordinates[0];
                                settingLong = coordinates[1];

                            } catch (Exception error) {
                                Log.e("rew", "Address lookup Error", error);
                            }

                            dataBaseMap.addUsers(jsonObjMapper.getString("nickname"), jsonObjMapper.getString("city"), settingLong
                                    , jsonObjMapper.getString("state"), jsonObjMapper.getInt("year"), jsonObjMapper.getInt("id"), settingLat,
                                    jsonObjMapper.getString("time-stamp"), jsonObjMapper.getString("country"));

                        } else {
                            dataBaseMap.addUsers(jsonObjMapper.getString("nickname"), jsonObjMapper.getString("city"), jsonObjMapper.getDouble("longitude")
                                    , jsonObjMapper.getString("state"), jsonObjMapper.getInt("year"), jsonObjMapper.getInt("id"), jsonObjMapper.getDouble("latitude"),
                                    jsonObjMapper.getString("time-stamp"), jsonObjMapper.getString("country"));

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                if (userIds != null) {
                    userIds.clear();
                }

                if ((filteredCountryValue.equals("")) && (filteredStateValue.equals("")) && (filteredYearValue != 0)) {
                    Cursor dataYear = dataBaseMap.filterYear(filteredYearValue);
                    if (dataYear != null) {
                        while (dataYear.moveToNext()) {

                            filterName = dataYear.getString(0);
                            filterLongitude = dataYear.getDouble(2);
                            filterLatitude = dataYear.getDouble(6);
                            plotMap(filterLatitude,filterLongitude,filterName);
                            filterUserIds.add(dataYear.getInt(5));
                        }
                        dataYear.close();
                    }
                } else if ((!filteredCountryValue.equals("")) && (filteredStateValue.equals("")) && (filteredYearValue == 0)) {
                    Cursor dataCountry = dataBaseMap.filterCountry(filteredCountryValue);
                    if (dataCountry != null) {
                        while (dataCountry.moveToNext()) {
                            filterName = dataCountry.getString(0);
                            filterLongitude = dataCountry.getDouble(2);
                            filterLatitude = dataCountry.getDouble(6);
                            plotMap(filterLatitude,filterLongitude,filterName);
                            filterUserIds.add(dataCountry.getInt(5));
                        }
                        dataCountry.close();
                    }
                }
                else if ((!filteredCountryValue.equals("")) && (filteredStateValue.equals("")) && (filteredYearValue != 0)) {
                    Cursor dataCountryYear = dataBaseMap.filterCountryYear(filteredCountryValue, filteredYearValue);
                    if (dataCountryYear != null) {
                        while (dataCountryYear.moveToNext()) {
                            filterName = dataCountryYear.getString(0);
                            filterLongitude = dataCountryYear.getDouble(2);
                            filterLatitude = dataCountryYear.getDouble(6);
                            plotMap(filterLatitude,filterLongitude,filterName);
                            filterUserIds.add(dataCountryYear.getInt(5));
                        }
                        dataCountryYear.close();
                    }
                }
                else if ((!filteredCountryValue.equals("")) && (!filteredStateValue.equals("")) && (filteredYearValue == 0)) {
                    Cursor dataCountryState = dataBaseMap.filterCountryState(filteredCountryValue, filteredStateValue);
                    if (dataCountryState != null) {
                        while (dataCountryState.moveToNext()) {
                            filterName = dataCountryState.getString(0);
                            filterLongitude = dataCountryState.getDouble(2);
                            filterLatitude = dataCountryState.getDouble(6);
                            plotMap(filterLatitude,filterLongitude,filterName);
                            filterUserIds.add(dataCountryState.getInt(5));
                        }
                        dataCountryState.close();
                    }
                }
                else if ((!filteredCountryValue.equals("")) && (!filteredStateValue.equals("")) && (filteredYearValue != 0)) {
                    Cursor dataCountryStateYear = dataBaseMap.filterCountryStateYear(filteredCountryValue, filteredStateValue, filteredYearValue);
                    if (dataCountryStateYear != null) {
                        while (dataCountryStateYear.moveToNext()) {
                            filterName = dataCountryStateYear.getString(0);
                            filterLongitude = dataCountryStateYear.getDouble(2);
                            filterLatitude = dataCountryStateYear.getDouble(6);
                            plotMap(filterLatitude,filterLongitude,filterName);
                            filterUserIds.add(dataCountryStateYear.getInt(5));
                        }
                        dataCountryStateYear.close();
                    }
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        mapRequest.add(userMapRequestArray);
    }


    public double[] geocoder(String state, String country) {
        double settingLat = 0.0, settingLong = 0.0;
        Geocoder locator = new Geocoder(getContext());
        try {
            List<Address> position =
                    locator.getFromLocationName(state + "," + country, 1);
            for (Address point : position) {
                settingLat = point.getLatitude();
                settingLong = point.getLongitude();
            }

        } catch (Exception error) {
            Log.e("rew", "Address lookup Error", error);
        }
        return new double[]{settingLat, settingLong};
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(getContext());
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mGoogleMap = googleMap;
        mGoogleMap.setOnInfoWindowClickListener(this);

        if(filteredCountryValue == null && filteredStateValue == null && filteredYearValue == 0) {
            getNextId();
        }
        else if(filteredCountryValue.isEmpty() && filteredYearValue == 0 && filteredStateValue.isEmpty())
        {
            getNextId();
        }
        else if((filteredYearValue!=0) || (filteredStateValue!=null) || (filteredCountryValue!=null))
        {
            if( filterUserIds==null)
            {
                filters();
            }
            else {
                filterUserIds.clear();
                filters();
            }
        }
    }

    public void filters() {
        dataBaseMap = new DataBase(getActivity());
        filterUserIds = new ArrayList<>();
        if ((filteredCountryValue.equals("")) && (filteredStateValue.equals("")) && (filteredYearValue == 0)) {

        }
        else {

            if ((filteredCountryValue.equals("")) && (filteredStateValue.equals("")) && (filteredYearValue != 0)) {
                Cursor dataYear = dataBaseMap.filterYear(filteredYearValue);
                if (dataYear != null) {
                    while (dataYear.moveToNext()) {

                        filterName = dataYear.getString(0);
                        filterLongitude = dataYear.getDouble(2);
                        filterLatitude = dataYear.getDouble(6);
                        plotMap(filterLatitude,filterLongitude,filterName);
                        filterUserIds.add(dataYear.getInt(5));
                    }
                    dataYear.close();
                }
            } else if ((!filteredCountryValue.equals("")) && (filteredStateValue.equals("")) && (filteredYearValue == 0)) {
                Cursor dataCountry = dataBaseMap.filterCountry(filteredCountryValue);
                if (dataCountry != null) {
                    while (dataCountry.moveToNext()) {
                        filterName = dataCountry.getString(0);
                        filterLongitude = dataCountry.getDouble(2);
                        filterLatitude = dataCountry.getDouble(6);
                        plotMap(filterLatitude,filterLongitude,filterName);
                        filterUserIds.add(dataCountry.getInt(5));
                    }
                    dataCountry.close();
                }
            }
            else if ((!filteredCountryValue.equals("")) && (filteredStateValue.equals("")) && (filteredYearValue != 0)) {
                Cursor dataCountryYear = dataBaseMap.filterCountryYear(filteredCountryValue, filteredYearValue);
                if (dataCountryYear != null) {
                    while (dataCountryYear.moveToNext()) {
                        filterName = dataCountryYear.getString(0);
                        filterLongitude = dataCountryYear.getDouble(2);
                        filterLatitude = dataCountryYear.getDouble(6);
                        plotMap(filterLatitude,filterLongitude,filterName);
                        filterUserIds.add(dataCountryYear.getInt(5));
                    }
                    dataCountryYear.close();
                }
            }
            else if ((!filteredCountryValue.equals("")) && (!filteredStateValue.equals("")) && (filteredYearValue == 0)) {
                Cursor dataCountryState = dataBaseMap.filterCountryState(filteredCountryValue, filteredStateValue);
                if (dataCountryState != null) {
                    while (dataCountryState.moveToNext()) {
                        filterName = dataCountryState.getString(0);
                        filterLongitude = dataCountryState.getDouble(2);
                        filterLatitude = dataCountryState.getDouble(6);
                        plotMap(filterLatitude,filterLongitude,filterName);
                        filterUserIds.add(dataCountryState.getInt(5));
                    }
                    dataCountryState.close();
                }
            }
            else if ((!filteredCountryValue.equals("")) && (!filteredStateValue.equals("")) && (filteredYearValue != 0)) {
                Cursor dataCountryStateYear = dataBaseMap.filterCountryStateYear(filteredCountryValue, filteredStateValue, filteredYearValue);
                if (dataCountryStateYear != null) {
                    while (dataCountryStateYear.moveToNext()) {
                        filterName = dataCountryStateYear.getString(0);
                        filterLongitude = dataCountryStateYear.getDouble(2);
                        filterLatitude = dataCountryStateYear.getDouble(6);
                        plotMap(filterLatitude,filterLongitude,filterName);
                        filterUserIds.add(dataCountryStateYear.getInt(5));
                    }
                    dataCountryStateYear.close();
                }
          }
       }
    }

    public void plotMap(double lat, double longi, String title) {

        mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(lat, longi)).title(title).visible(true).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        if ((filteredCountryValue == null) && (filteredStateValue == null)) {
            LatLng place = new LatLng(0, 0);
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(place));
        }

        mMapView.postInvalidate();
    }

    public void loadData() {
        if ((filteredCountryValue == null) && (filteredStateValue == null) && (filteredYearValue == 0)) {
            int lastId = userIds.get(userIds.size() - 1);
            beforeid = lastId;
            afterid = beforeid - count;
            Cursor idCheck = dataBaseMap.checkIDs(afterid, beforeid);
            if (idCheck != null) {
                Toast.makeText(this.getActivity(), "LOADING....", Toast.LENGTH_LONG).show();
                mapPlottingGetLocations("http://bismarck.sdsu.edu/hometown/users?reverse=true&afterid=" + afterid + "&beforeid=" + beforeid);
            }
        }

        else if ((filteredCountryValue.isEmpty()) && (filteredStateValue.isEmpty()) && (filteredYearValue == 0)) {
            int lastId = userIds.get(userIds.size() - 1);
            beforeid = lastId;
            afterid = beforeid - count;
            Cursor idCheck = dataBaseMap.checkIDs(afterid, beforeid);
            if (idCheck != null) {
                Toast.makeText(this.getActivity(), "LOADING....", Toast.LENGTH_LONG).show();
                mapPlottingGetLocations("http://bismarck.sdsu.edu/hometown/users?reverse=true&afterid=" + afterid + "&beforeid=" + beforeid);
            }
        }

        else if ((filteredCountryValue.isEmpty()) && (filteredStateValue.isEmpty()) && (filteredYearValue != 0)) {
            int lastId = filterUserIds.get(filterUserIds.size() - 1);
            beforeid = lastId;
            afterid = beforeid - count;
            Cursor idCheck = dataBaseMap.checkIDs(afterid, beforeid);
            if (idCheck != null) {
                Toast.makeText(this.getActivity(), "LOADING....", Toast.LENGTH_LONG).show();
                filterMapPlottingGetLocations("http://bismarck.sdsu.edu/hometown/users?reverse=true&afterid=" + afterid + "&beforeid=" + beforeid + "&year=" + filteredYearValue);
            }
        }

        else if ((!filteredCountryValue.isEmpty()) && (filteredStateValue.isEmpty()) && (filteredYearValue == 0)) {

            int lastId = filterUserIds.get(filterUserIds.size() - 1);
            beforeid = lastId;
            afterid = beforeid - count;
            Cursor idCheck = dataBaseMap.checkIDs(afterid, beforeid);
            if (idCheck != null) {
                Toast.makeText(this.getActivity(), "LOADING....", Toast.LENGTH_LONG).show();
                filterMapPlottingGetLocations("http://bismarck.sdsu.edu/hometown/users?reverse=true&afterid=" + afterid + "&beforeid=" + beforeid + "&country=" + filteredCountryValue);
            }
        }

        else if((!filteredCountryValue.isEmpty()) && (filteredStateValue.isEmpty()) && (filteredYearValue != 0)) {
            int lastId = filterUserIds.get(filterUserIds.size() - 1);
            beforeid = lastId;
            afterid = beforeid - count;
            Cursor idCheck = dataBaseMap.checkIDs(afterid, beforeid);
            if (idCheck != null) {
                Toast.makeText(this.getActivity(), "LOADING....", Toast.LENGTH_LONG).show();
                filterMapPlottingGetLocations("http://bismarck.sdsu.edu/hometown/users?reverse=true&afterid=" + afterid + "&beforeid=" + beforeid + "&country=" + filteredCountryValue + "&year=" + filteredYearValue);
            }
        }

        else if((!filteredCountryValue.isEmpty()) && (!filteredStateValue.isEmpty()) && (filteredYearValue == 0))
        {
            int lastId = filterUserIds.get(filterUserIds.size() - 1);
            beforeid = lastId;
            afterid = beforeid - count;
            Cursor idCheck = dataBaseMap.checkIDs(afterid, beforeid);
            if (idCheck != null) {
                Toast.makeText(this.getActivity(), "LOADING....", Toast.LENGTH_LONG).show();
                filterMapPlottingGetLocations("http://bismarck.sdsu.edu/hometown/users?reverse=true&afterid=" + afterid + "&beforeid=" + beforeid + "&country=" + filteredCountryValue + "&state=" + filteredStateValue);
            }
        }

        else if((!filteredCountryValue.isEmpty()) && (!filteredStateValue.isEmpty()) && (filteredYearValue != 0)) {

            int lastId = filterUserIds.get(filterUserIds.size() - 1);
            beforeid = lastId;
            afterid = beforeid - count;
            Cursor idCheck = dataBaseMap.checkIDs(afterid, beforeid);
            if (idCheck != null) {
                Toast.makeText(this.getActivity(), "LOADING....", Toast.LENGTH_LONG).show();
                filterMapPlottingGetLocations("http://bismarck.sdsu.edu/hometown/users?reverse=true&afterid=" + afterid + "&beforeid=" + beforeid + "&country=" + filteredCountryValue + "&state=" + filteredStateValue + "&year=" + filteredYearValue);
            }
        }

    }

    public void refreshValue() {
        if ((filteredCountryValue == null) && (filteredStateValue == null) && (filteredYearValue == 0)) {

            RequestQueue nextUpdateId = VolleySingleton.getInstance().getVSRequestQueue();
            StringRequest updateIdRequest = new StringRequest("http://bismarck.sdsu.edu/hometown/nextid", new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    int id = latestId(Integer.parseInt(response));
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                }

            });
            nextUpdateId.add(updateIdRequest);
        }
        else if((filteredCountryValue.isEmpty()) && (filteredStateValue.isEmpty()) && (filteredYearValue == 0))
        {
            RequestQueue nextUpdateId = VolleySingleton.getInstance().getVSRequestQueue();
            StringRequest updateIdRequest = new StringRequest("http://bismarck.sdsu.edu/hometown/nextid", new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    int id = latestId(Integer.parseInt(response));
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                }

            });
            nextUpdateId.add(updateIdRequest);
        }
        else
        {
            getNextId();
            Toast.makeText(this.getActivity(),"Updated", Toast.LENGTH_LONG).show();
        }
    }

    public int latestId(int id) {
        beforeid = id;
        int currentTopId = userIds.get(0);
        if (beforeid > currentTopId + 1) {
            afterid = beforeid - count;
            mapPlottingGetLocations("http://bismarck.sdsu.edu/hometown/users?reverse=true&afterid=" + afterid + "&beforeid=" + beforeid);
            Toast.makeText(this.getActivity(), "List Updated....", Toast.LENGTH_LONG).show();
        } else if (beforeid <= currentTopId + 1) {
            Toast.makeText(this.getActivity(), "No New Data Exists.", Toast.LENGTH_LONG).show();
        }
        return id;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonMapViewRefresh:
                refreshValue();
                break;
            case R.id.buttonMapViewLoadMore:
                loadData();
                break;
        }

    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        final String userName = marker.getTitle();
        System.out.println("The marker selected is " + userName);

        final FirebaseDatabase firebaseData = FirebaseDatabase.getInstance();
        DatabaseReference refDatabase = firebaseData.getReference("userattributes");

        refDatabase.orderByChild("nickname").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                firebaseArray = new ArrayList<String>();
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    String users = (String) singleSnapshot.child("nickname").getValue();
                    firebaseArray.add(users);
                }
                System.out.println("The firebase users in MapView are "+firebaseArray);

                for (Object listName : firebaseArray) {
                    if(listName != null) {
                        if (listName.equals(userName)) {
                            if (listName.equals(currentLoggedUserMap)) {
                                checkLoggedUser = true;
                            } else {
                                checkLoggedUser = false;
                                flag = true;
                                System.out.println("User Selected is " + listName);
                                SetUserMapView userMaps = (SetUserMapView) getActivity();
                                userMaps.setUserMapView(listName);
                                break;
                            }
                        }
                    }
                }
                if (flag) {
                    Toast.makeText(getContext(), "The User Selected is Online!! Please Click on the Chat Option in the Menu to Chat.", Toast.LENGTH_LONG).show();
                } else if (checkLoggedUser) {
                    Toast.makeText(getContext(),"Cannot Chat with Logged in User, Please Select Another Firebase User.",Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(getContext(), "The User is not Avaiable to Chat.", Toast.LENGTH_SHORT).show();
                }
                firebaseArray.clear();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    public interface SetUserMapView
    {
        public void setUserMapView(Object mapName);
    }
}
