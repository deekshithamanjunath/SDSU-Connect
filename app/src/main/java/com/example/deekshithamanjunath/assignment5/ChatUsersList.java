package com.example.deekshithamanjunath.assignment5;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.LinkedHashSet;

public class ChatUsersList extends AppCompatActivity implements AdapterView.OnItemClickListener{

    ListView chatList;
    String userId;
    ArrayList<String> list = new ArrayList<>();

    DatabaseReference chatId;
    FirebaseUser logged;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_users_list);
        chatList = (ListView) findViewById(R.id.chatListView);

        chatList.setOnItemClickListener(this);

        chatId = FirebaseDatabase.getInstance().getReference("ActiveChats");
        System.out.println("keys are "+chatId.getKey().toString());

        logged = FirebaseAuth.getInstance().getCurrentUser();
        userId = logged.getDisplayName();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;

        getWindow().setLayout((int) (width*.8), (int) (height*.9));
        retriveData();
        list.clear();

    }

    public void retriveData()
    {
        chatId.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                getList(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getList(DataSnapshot dataSnapshot)
    {
        list.clear();
        for(DataSnapshot data: dataSnapshot.getChildren())
        {
            if(data.getKey().toString().contains(userId)) {
                String[] split = data.getKey().split("-");
                System.out.println(split[0]);
                for (String user : split) {
                    list.add(user.trim());
                    list.remove(userId);
                    System.out.println("the list is " + list);
                }
            }
        }
        LinkedHashSet<String> lhs = new LinkedHashSet<String>();
        lhs.addAll(list);
        list.clear();
        list.addAll(lhs);

        if(list.size() > 0) {

            ArrayAdapter chatAdapter = new ArrayAdapter(ChatUsersList.this, android.R.layout.simple_list_item_1,list);
            chatList.setAdapter(chatAdapter);
        }
        else {
            Toast.makeText(this,"No Chat History Exists",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String name = parent.getItemAtPosition(position).toString();
        System.out.println("Selected user history is "+name);
        Intent i =getIntent();
        i.putExtra("receiver",name);
        setResult(RESULT_OK, i);
        finish();
    }
}
