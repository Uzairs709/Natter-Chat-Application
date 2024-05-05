package com.example.natterchatapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.natterchatapp.R;
import com.example.natterchatapp.models.User;
import com.example.natterchatapp.utilities.KEYS;

public class ChatActivity extends AppCompatActivity {

    View viewBackground;
    AppCompatImageView acivBack,acivInfo;
    TextView tvChatName;
    RecyclerView chatRecyclerView;
    ProgressBar  chatProgressBar;
    FrameLayout layoutSend;
    EditText inputMessage;
    User receivedUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat);
        Intent intent=getIntent();
        receivedUser=(User)intent.getSerializableExtra(KEYS.KEY_USER);
        if(receivedUser!=null) {
            tvChatName.setText(receivedUser.getName());
        }
        init();
        acivBack.setOnClickListener(v->finish());


    }



    public void init(){
        viewBackground=findViewById(R.id.viewBackground);
        acivBack=findViewById(R.id.acivBack);
        acivInfo=findViewById(R.id.acivInfo);
        tvChatName=findViewById(R.id.tvChatName);
        chatRecyclerView=findViewById(R.id.chatRecyclerView);
        chatProgressBar=findViewById(R.id.chatProgressBar);
        layoutSend=findViewById(R.id.layoutSend);
        inputMessage=findViewById(R.id.inputMessage);

    }
}