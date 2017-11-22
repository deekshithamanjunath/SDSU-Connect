package com.example.deekshithamanjunath.assignment5;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {

    String sender;
    String recipientUser;

    ListView view;
    EditText typedMessage;
    Button send;
    TextView nameView;
    ScrollView scroll;

    private DatabaseReference chattingUser;

    private String refKey;
    private String currentMessage,currentUser;
    private int usersCode = 10;

    String selectedUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        typedMessage = (EditText) findViewById(R.id.message);
        nameView = (TextView) findViewById(R.id.chats);
        send = (Button) findViewById(R.id.buttonSend);

        send.setOnClickListener(this);
        scroll = (ScrollView) findViewById(R.id.scrollView);

        Intent users = getIntent();
        sender = users.getStringExtra("loggedUser");
        recipientUser = users.getStringExtra("convoUser");

        setTitle(recipientUser.toUpperCase());
        getConversation(sender, recipientUser);

        if(selectedUser!=null)
        {
            setTitle(selectedUser.toUpperCase());
            getConversation(sender, selectedUser);
        }

        Bundle bundle = getIntent().getExtras();
        bundle.getString("receiver");

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;

        getWindow().setLayout((int) (width*.8), (int) (height*.9));
    }

    public void getConversation(String chatSender, String receiver)
    {
        System.out.println("the receiver is "+receiver);
        nameView.setText("");

        int one = chatSender.compareTo(receiver);
        int two = receiver.compareTo(chatSender);

        String id;
        String conversationId = "";

        if(one > two)
        {
            id = chatSender+ " - " +receiver;
        }
        else
        {
            id = receiver+ " - " +chatSender;
        }
        conversationId = id;

        System.out.println("The conversation id is "+conversationId);
        chattingUser = FirebaseDatabase.getInstance().getReference().child("ActiveChats").child(conversationId);
        chattingUser.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                appendConversation(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                appendConversation(dataSnapshot);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void appendConversation(DataSnapshot dataSnapshot) {

        Iterator iter = dataSnapshot.getChildren().iterator();

        while (iter.hasNext())
        {
            currentMessage = ((DataSnapshot)iter.next()).getValue().toString();
            currentUser = ((DataSnapshot)iter.next()).getValue().toString();
            nameView.append(currentUser.toUpperCase() + " : " + currentMessage+"\n");
        }
    }

    @Override
    public void onClick(View v) {

        String editTextValue = typedMessage.getText().toString();
        if(editTextValue.isEmpty()) {
            typedMessage.setError("Enter Message to Send!");
            typedMessage.requestFocus();
        }
        else
        {
            Map<String, Object> message = new HashMap<String, Object>();

            refKey = chattingUser.push().getKey();
            chattingUser.updateChildren(message);

            DatabaseReference conversation = chattingUser.child(refKey);

            Map<String, Object> messageValues = new HashMap<String, Object>();
            messageValues.put("sender", sender);
            messageValues.put("message", typedMessage.getText().toString());
            conversation.updateChildren(messageValues);
            typedMessage.setText("");
        }
        scroll.fullScroll(View.FOCUS_DOWN);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflateMenu = getMenuInflater();
        inflateMenu.inflate(R.menu.chatlist, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        item.getItemId();
        Intent listUsers = new Intent(this,ChatUsersList.class);
        startActivityForResult(listUsers, usersCode);
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 10)
        {
            switch (resultCode)
            {
                case RESULT_OK:
                    selectedUser = data.getStringExtra("receiver");
                    System.out.println("Selected user for next conversation is "+ selectedUser);
                    setTitle(selectedUser.toUpperCase());
                    getConversation(sender,selectedUser);
                case RESULT_CANCELED:
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent back = new Intent(this,ViewContainerActivity.class);
        startActivity(back);
    }
}
