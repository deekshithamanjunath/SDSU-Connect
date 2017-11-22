package com.example.deekshithamanjunath.assignment5;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
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


public class ListViewUsersFragment extends Fragment implements AbsListView.OnScrollListener,View.OnClickListener, AdapterView.OnItemClickListener {

    View v;
    Button refresh;
    ListView lv;

    private boolean flag = false;
    private boolean userCheck = false;

    UserAttributes userFields;

    ArrayList<String> userListNickname = new ArrayList<>();
    ArrayList<String> filterList;
    ArrayList<String> firebaseArray;
    ArrayAdapter<String> nicknamesAdapter;
    ArrayList<Integer> userIds;
    ArrayList<Integer> filterUserIds;

    ViewContainerActivity filterValues;

    String filteredCountryValue, filteredStateValue;
    int filteredYearValue;

    private FirebaseUser firebaseAuthLogged;
    String currentLoggedUser;

    DataBase dataBase;

    int count = 25;
    int afterid,beforeid;
    int presentFirstVisibleItem, presentItemVisibleCount, presentScrollState, totalItems;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        filterValues = (ViewContainerActivity) getActivity();
        v = inflater.inflate(R.layout.fragment_list_view_users, container, false);

        refresh = (Button) v.findViewById(R.id.buttonListViewRefresh);
        refresh.setOnClickListener(this);

        lv = (ListView) v.findViewById(R.id.viewUsers);
        lv.setOnItemClickListener(this);
        lv.setOnScrollListener(this);

        dataBase = new DataBase(getActivity());

        firebaseAuthLogged = FirebaseAuth.getInstance().getCurrentUser();
        currentLoggedUser = firebaseAuthLogged.getDisplayName();

        filteredCountryValue = filterValues.getCountry();
        filteredStateValue = filterValues.getState();
        filteredYearValue = filterValues.getYear();
        System.out.println(filteredCountryValue);

        if(filteredCountryValue == null && filteredStateValue == null && filteredYearValue == 0) {
            getNextId();
        }
        else if(filteredCountryValue.isEmpty() && filteredYearValue == 0 && filteredStateValue.isEmpty())
            {
                getNextId();
            }
        else if((filteredYearValue!=0) || (filteredStateValue!=null) || (filteredCountryValue!=null))
        {
            if((filterList== null) && filterUserIds==null)
            {
                filters();
            }
            else {
                filterList.clear();
                filterUserIds.clear();
                filters();
            }
        }
        return v;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    public void getNextId()
    {
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
            afterid = beforeid - count;
            serverConnect("http://bismarck.sdsu.edu/hometown/users?reverse=true&afterid=" + afterid + "&beforeid=" + beforeid);
        return id;
    }

    public void filters() {
        filterUserIds = new ArrayList<>();
        filterList = new ArrayList<>();

        if ((filteredCountryValue.equals("")) && (filteredStateValue.equals("")) && (filteredYearValue == 0)) {

        }
        else {
            userListNickname.clear();

            if ((filteredCountryValue.equals("")) && (filteredStateValue.equals("")) && (filteredYearValue != 0)) {
                Cursor dataYear = dataBase.filterYear(filteredYearValue);
                if (dataYear != null) {
                    while (dataYear.moveToNext()) {
                        filterList.add(dataYear.getString(0) + "\n" + dataYear.getString(1) + "," + dataYear.getString(3) + "\n" + dataYear.getString(8) + "\n" + dataYear.getString(4));
                        filterUserIds.add(dataYear.getInt(5));
                    }
                    dataYear.close();
                }
            } else if ((!filteredCountryValue.equals("")) && (filteredStateValue.equals("")) && (filteredYearValue == 0)) {
                Cursor dataCountry = dataBase.filterCountry(filteredCountryValue);
                if (dataCountry != null) {
                    while (dataCountry.moveToNext()) {
                        filterList.add(dataCountry.getString(0) + "\n" + dataCountry.getString(1) + "," + dataCountry.getString(3) + "\n" + dataCountry.getString(8) + "\n" + dataCountry.getString(4));
                        filterUserIds.add(dataCountry.getInt(5));
                    }
                    dataCountry.close();
                }
            } else if ((!filteredCountryValue.equals("")) && (filteredStateValue.equals("")) && (filteredYearValue != 0)) {
                Cursor dataCountryYear = dataBase.filterCountryYear(filteredCountryValue, filteredYearValue);
                if (dataCountryYear != null) {
                    while (dataCountryYear.moveToNext()) {
                        filterList.add(dataCountryYear.getString(0) + "\n" + dataCountryYear.getString(1) + "," + dataCountryYear.getString(3) + "\n" + dataCountryYear.getString(8) + "\n" + dataCountryYear.getString(4));
                        filterUserIds.add(dataCountryYear.getInt(5));
                    }
                    dataCountryYear.close();
                }
            } else if ((!filteredCountryValue.equals("")) && (!filteredStateValue.equals("")) && (filteredYearValue == 0)) {
                Cursor dataCountryState = dataBase.filterCountryState(filteredCountryValue, filteredStateValue);
                if (dataCountryState != null) {
                    while (dataCountryState.moveToNext()) {
                        filterList.add(dataCountryState.getString(0) + "\n" + dataCountryState.getString(1) + "," + dataCountryState.getString(3) + "\n" + dataCountryState.getString(8) + "\n" + dataCountryState.getString(4));
                        filterUserIds.add(dataCountryState.getInt(5));
                    }
                    dataCountryState.close();
                }
            } else if ((!filteredCountryValue.equals("")) && (!filteredStateValue.equals("")) && (filteredYearValue != 0)) {
                Cursor dataCountryStateYear = dataBase.filterCountryStateYear(filteredCountryValue, filteredStateValue, filteredYearValue);
                if (dataCountryStateYear != null) {
                    while (dataCountryStateYear.moveToNext()) {
                        filterList.add(dataCountryStateYear.getString(0) + "\n" + dataCountryStateYear.getString(1) + "," + dataCountryStateYear.getString(3) + "\n" + dataCountryStateYear.getString(8) + "\n" + dataCountryStateYear.getString(4));
                        filterUserIds.add(dataCountryStateYear.getInt(5));
                    }
                    dataCountryStateYear.close();
                }
            }

            nicknamesAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, filterList);
            lv.setAdapter(nicknamesAdapter);
            nicknamesAdapter.notifyDataSetChanged();
        }
    }


    public void serverConnect(String url)
    {
        dataBase = new DataBase(getActivity());

        RequestQueue requestView = VolleySingleton.getInstance().getVSRequestQueue();
        JsonArrayRequest userArrayJsonRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                for (int i = 0; i < response.length();i ++)
                {
                    userFields = new UserAttributes();
                    try {
                        JSONObject jsonObj = response.getJSONObject(i);

                        dataBase.addUsers(jsonObj.getString("nickname"),jsonObj.getString("city"),jsonObj.getDouble("longitude")
                        , jsonObj.getString("state"), jsonObj.getInt("year"), jsonObj.getInt("id"), jsonObj.getDouble("latitude"), jsonObj.getString("time-stamp"), jsonObj.getString("country"));


                    }catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                if ((userListNickname != null) && (userIds != null)) {
                    userListNickname.clear();
                    userIds.clear();
                }

                Cursor data = dataBase.sortData();
                if (data != null) {
                    userIds = new ArrayList<>();
                    while(data.moveToNext()) {
                        if (!data.getString(0).equals(""))
                        {
                            userListNickname.add(data.getString(0)+"\n"+data.getString(1)+","+data.getString(3)+"\n"+data.getString(8)+"\n"+data.getString(4));
                            userIds.add(data.getInt(5));
                        }
                    }
                    data.close();
                }
                if (userListNickname != null) {
                    nicknamesAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, userListNickname);
                    lv.setAdapter(nicknamesAdapter);
                }
                    nicknamesAdapter.notifyDataSetChanged();
            }

        }
                , new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestView.add(userArrayJsonRequest);
    }

    public void filterServerConnect(String url)
    {
        dataBase = new DataBase(getActivity());

        RequestQueue requestView = VolleySingleton.getInstance().getVSRequestQueue();
        JsonArrayRequest userArrayJsonRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                for (int i = 0; i < response.length();i ++)
                {
                    userFields = new UserAttributes();
                    try {
                        JSONObject jsonObj = response.getJSONObject(i);

                        dataBase.addUsers(jsonObj.getString("nickname"),jsonObj.getString("city"),jsonObj.getDouble("longitude")
                                , jsonObj.getString("state"), jsonObj.getInt("year"), jsonObj.getInt("id"), jsonObj.getDouble("latitude"), jsonObj.getString("time-stamp"), jsonObj.getString("country"));


                    }catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                if ((filterList != null) && (filterUserIds != null)) {
                    filterList.clear();
                    filterUserIds.clear();
                }

                if ((filteredCountryValue.equals("")) && (filteredStateValue.equals("")) && (filteredYearValue != 0)) {
                    Cursor dataYear = dataBase.filterYear(filteredYearValue);
                    if (dataYear != null) {
                        while (dataYear.moveToNext()) {
                            filterList.add(dataYear.getString(0) + "\n" + dataYear.getString(1) + "," + dataYear.getString(3) + "\n" + dataYear.getString(8) + "\n" + dataYear.getString(4));
                            filterUserIds.add(dataYear.getInt(5));
                        }
                        dataYear.close();
                    }
                } else if ((!filteredCountryValue.equals("")) && (filteredStateValue.equals("")) && (filteredYearValue == 0)) {
                    Cursor dataCountry = dataBase.filterCountry(filteredCountryValue);
                    if (dataCountry != null) {
                        while (dataCountry.moveToNext()) {
                            filterList.add(dataCountry.getString(0) + "\n" + dataCountry.getString(1) + "," + dataCountry.getString(3) + "\n" + dataCountry.getString(8) + "\n" + dataCountry.getString(4));
                            filterUserIds.add(dataCountry.getInt(5));
                        }
                        dataCountry.close();
                    }
                } else if ((!filteredCountryValue.equals("")) && (filteredStateValue.equals("")) && (filteredYearValue != 0)) {
                    Cursor dataCountryYear = dataBase.filterCountryYear(filteredCountryValue, filteredYearValue);
                    if (dataCountryYear != null) {
                        while (dataCountryYear.moveToNext()) {
                            filterList.add(dataCountryYear.getString(0) + "\n" + dataCountryYear.getString(1) + "," + dataCountryYear.getString(3) + "\n" + dataCountryYear.getString(8) + "\n" + dataCountryYear.getString(4));
                            filterUserIds.add(dataCountryYear.getInt(5));
                        }
                        dataCountryYear.close();
                    }
                } else if ((!filteredCountryValue.equals("")) && (!filteredStateValue.equals("")) && (filteredYearValue == 0)) {
                    Cursor dataCountryState = dataBase.filterCountryState(filteredCountryValue, filteredStateValue);
                    if (dataCountryState != null) {
                        while (dataCountryState.moveToNext()) {
                            filterList.add(dataCountryState.getString(0) + "\n" + dataCountryState.getString(1) + "," + dataCountryState.getString(3) + "\n" + dataCountryState.getString(8) + "\n" + dataCountryState.getString(4));
                            filterUserIds.add(dataCountryState.getInt(5));
                        }
                        dataCountryState.close();
                    }
                } else if ((!filteredCountryValue.equals("")) && (!filteredStateValue.equals("")) && (filteredYearValue != 0)) {
                    Cursor dataCountryStateYear = dataBase.filterCountryStateYear(filteredCountryValue, filteredStateValue, filteredYearValue);
                    if (dataCountryStateYear != null) {
                        while (dataCountryStateYear.moveToNext()) {
                            filterList.add(dataCountryStateYear.getString(0) + "\n" + dataCountryStateYear.getString(1) + "," + dataCountryStateYear.getString(3) + "\n" + dataCountryStateYear.getString(8) + "\n" + dataCountryStateYear.getString(4));
                            filterUserIds.add(dataCountryStateYear.getInt(5));
                        }
                        dataCountryStateYear.close();
                    }
                }

                if (filterList != null) {
                    nicknamesAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, filterList);
                    lv.setAdapter(nicknamesAdapter);
                }
                nicknamesAdapter.notifyDataSetChanged();
            }

        }
                , new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestView.add(userArrayJsonRequest);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        this.presentScrollState = scrollState;

        if ((filteredCountryValue==null) && (filteredStateValue==null) && (filteredYearValue == 0)) {
            if (totalItems - presentFirstVisibleItem == presentItemVisibleCount && this.presentScrollState == SCROLL_STATE_IDLE) {
                int lastId = userIds.get(totalItems - 1);
                beforeid = lastId;
                afterid = beforeid - count;
                Cursor idCheck = dataBase.checkIDs(afterid, beforeid);
                if (idCheck != null) {
                    Toast.makeText(this.getActivity(), "LOADING....", Toast.LENGTH_LONG).show();
                    serverConnect("http://bismarck.sdsu.edu/hometown/users?reverse=true&afterid=" + afterid + "&beforeid=" + beforeid);
                }
            }
        }
        else if ((filteredCountryValue.isEmpty()) && (filteredStateValue.isEmpty()) && (filteredYearValue == 0))
        {
            if (totalItems - presentFirstVisibleItem == presentItemVisibleCount && this.presentScrollState == SCROLL_STATE_IDLE) {
                int lastId = userIds.get(totalItems - 1);
                beforeid = lastId;
                afterid = beforeid - count;
                Cursor idCheck = dataBase.checkIDs(afterid, beforeid);
                if (idCheck != null) {
                    Toast.makeText(this.getActivity(), "LOADING....", Toast.LENGTH_LONG).show();
                    serverConnect("http://bismarck.sdsu.edu/hometown/users?reverse=true&afterid=" + afterid + "&beforeid=" + beforeid);
                }
            }
        }
        else if ((filteredCountryValue.isEmpty()) && (filteredStateValue.isEmpty()) && (filteredYearValue != 0)) {
            if (totalItems - presentFirstVisibleItem == presentItemVisibleCount && this.presentScrollState == SCROLL_STATE_IDLE) {
                int lastId = filterUserIds.get(totalItems - 1);
                beforeid = lastId;
                afterid = beforeid - 100;
                Cursor idCheck = dataBase.checkIDs(afterid, beforeid);
                if (idCheck != null) {
                    Toast.makeText(this.getActivity(), "LOADING....", Toast.LENGTH_LONG).show();
                    filterServerConnect("http://bismarck.sdsu.edu/hometown/users?reverse=true&afterid=" + afterid + "&beforeid=" + beforeid + "&year=" + filteredYearValue);
                }
            }
        } else if ((!filteredCountryValue.isEmpty()) && (filteredStateValue.isEmpty()) && (filteredYearValue == 0)) {
            if (totalItems - presentFirstVisibleItem == presentItemVisibleCount && this.presentScrollState == SCROLL_STATE_IDLE) {
                int lastId = filterUserIds.get(totalItems - 1);
                beforeid = lastId;
                afterid = beforeid - 100;
                Cursor idCheck = dataBase.checkIDs(afterid, beforeid);
                if (idCheck != null) {
                    Toast.makeText(this.getActivity(), "LOADING....", Toast.LENGTH_LONG).show();
                    filterServerConnect("http://bismarck.sdsu.edu/hometown/users?reverse=true&afterid=" + afterid + "&beforeid=" + beforeid + "&country=" + filteredCountryValue);
                }
            }
        }

        else if((!filteredCountryValue.isEmpty()) && (filteredStateValue.isEmpty()) && (filteredYearValue != 0))
        {
            if (totalItems - presentFirstVisibleItem == presentItemVisibleCount && this.presentScrollState == SCROLL_STATE_IDLE) {
                int lastId = filterUserIds.get(totalItems - 1);
                beforeid = lastId;
                afterid = beforeid - 100;
                Cursor idCheck = dataBase.checkIDs(afterid, beforeid);
                if (idCheck != null) {
                    Toast.makeText(this.getActivity(), "LOADING....", Toast.LENGTH_LONG).show();
                    filterServerConnect("http://bismarck.sdsu.edu/hometown/users?reverse=true&afterid=" + afterid + "&beforeid=" + beforeid + "&country=" +filteredCountryValue+ "&year=" +filteredYearValue);
                }
            }
        }
        else if((!filteredCountryValue.isEmpty()) && (!filteredStateValue.isEmpty()) && (filteredYearValue == 0))
        {
            if (totalItems - presentFirstVisibleItem == presentItemVisibleCount && this.presentScrollState == SCROLL_STATE_IDLE) {
                int lastId = filterUserIds.get(totalItems - 1);
                beforeid = lastId;
                afterid = beforeid - 100;
                Cursor idCheck = dataBase.checkIDs(afterid, beforeid);
                if (idCheck != null) {
                    Toast.makeText(this.getActivity(), "LOADING....", Toast.LENGTH_LONG).show();
                    filterServerConnect("http://bismarck.sdsu.edu/hometown/users?reverse=true&afterid=" + afterid + "&beforeid=" + beforeid + "&country=" +filteredCountryValue+ "&state=" +filteredStateValue);
                }
            }
        }
        else if((!filteredCountryValue.isEmpty()) && (!filteredStateValue.isEmpty()) && (filteredYearValue != 0))
        {
            if (totalItems - presentFirstVisibleItem == presentItemVisibleCount && this.presentScrollState == SCROLL_STATE_IDLE) {
                int lastId = filterUserIds.get(totalItems - 1);
                beforeid = lastId;
                afterid = beforeid - 100;
                Cursor idCheck = dataBase.checkIDs(afterid, beforeid);
                if (idCheck != null) {
                    Toast.makeText(this.getActivity(), "LOADING....", Toast.LENGTH_LONG).show();
                    filterServerConnect("http://bismarck.sdsu.edu/hometown/users?reverse=true&afterid=" + afterid + "&beforeid=" + beforeid + "&country=" +filteredCountryValue+ "&state=" +filteredStateValue+ "&year=" +filteredYearValue);
                }
            }
        }

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        this.presentFirstVisibleItem = firstVisibleItem;
        this.presentItemVisibleCount = visibleItemCount;
        this.totalItems = totalItemCount;
    }

    public void updateId() {
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

    public int latestId(int id)
    {
        beforeid = id;
        int currentTopId = userIds.get(0);
        if (beforeid > currentTopId + 1) {
            afterid = beforeid - count;
            serverConnect("http://bismarck.sdsu.edu/hometown/users?reverse=true&afterid=" + afterid + "&beforeid=" + beforeid);
            Toast.makeText(this.getActivity(),"List Updated....", Toast.LENGTH_LONG).show();
        }
        else if (beforeid <= currentTopId + 1)
        {
            Toast.makeText(this.getActivity(),"No New Data Exists.", Toast.LENGTH_LONG).show();
        }
        return id;
    }


    @Override
    public void onClick(View v) {
        updateId();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        String chatUser = parent.getItemAtPosition(position).toString();
        String [] name = chatUser.split("\n");
        final String chatName = name[0];
        System.out.println(chatName);

        final FirebaseDatabase firebaseData = FirebaseDatabase.getInstance();
        DatabaseReference refDatabase = firebaseData.getReference("userattributes");

        refDatabase.orderByChild("nickname").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                firebaseArray = new ArrayList<String>();
                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    String users = (String) singleSnapshot.child("nickname").getValue();
                    firebaseArray.add(users);
                }
                System.out.println("The firebase users are "+firebaseArray);

                for (Object listName : firebaseArray) {
                    if(listName!=null) {
                        if (listName.equals(chatName)) {
                            if (listName.equals(currentLoggedUser)) {
                                userCheck = true;
                            } else {
                                userCheck = false;
                                flag = true;
                                SetUserListView user = (SetUserListView) getActivity();
                                user.setUserListView(listName);
                                break;
                            }
                        }
                    }
                }
                if(flag)
                {
                    Toast.makeText(getActivity(),"The User Selected is Online!! Please Click on the Chat Option in the Menu to Chat.",Toast.LENGTH_LONG).show();
                }

                else if(userCheck)
                {
                    Toast.makeText(getActivity(),"Cannot Chat with Logged in User, Please Select Another Firebase User.",Toast.LENGTH_LONG).show();
                }
                else
                {
                    Toast.makeText(getActivity(),"The User is not Avaiable to Chat.",Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    public interface SetUserListView
    {
        public void setUserListView(Object name);
    }

}
