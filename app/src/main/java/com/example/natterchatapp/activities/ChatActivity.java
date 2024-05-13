package com.example.natterchatapp.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.natterchatapp.adapters.ChatAdapter;
import com.example.natterchatapp.databinding.ActivityChatBinding;
import com.example.natterchatapp.models.ChatMessage;
import com.example.natterchatapp.models.User;
import com.example.natterchatapp.utilities.KEYS;
import com.example.natterchatapp.utilities.Preference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class ChatActivity extends AppCompatActivity {

    private ActivityChatBinding binding;
    private User receivedUser;
    private ArrayList<ChatMessage> chatMessages;
    private ChatAdapter chatAdapter;
    private Preference sharedPref;
    private FirebaseFirestore database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        loadUser();
        setListener();
        init();
        listenMessage();
    }

    private void init() {
        sharedPref = new Preference(getApplicationContext());
        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(chatMessages,
                sharedPref.getString(KEYS.KEY_USER_ID),
                getBitmapFromString(receivedUser.getImage())
        );
        binding.chatRecyclerView.setAdapter(chatAdapter);
        database = FirebaseFirestore.getInstance();
    }

    private void sendMessage() {
        HashMap<String, Object> message = new HashMap<>();
        message.put(KEYS.KEY_SENDER_ID, sharedPref.getString(KEYS.KEY_USER_ID));
        message.put(KEYS.KEY_RECEIVER_ID, receivedUser.getId());
        message.put(KEYS.KEY_MESSAGE, binding.inputMessage.getText().toString());
        message.put(KEYS.KEY_TIMESTAMP, new Date());
        database.collection(KEYS.KEY_COLLECTION_CHAT).add(message);
        binding.inputMessage.setText(null);

    }

    private void listenMessage(){
        database.collection(KEYS.KEY_COLLECTION_CHAT)
                .whereEqualTo(KEYS.KEY_SENDER_ID,sharedPref.getString(KEYS.KEY_USER_ID))
                .whereEqualTo(KEYS.KEY_RECEIVER_ID,receivedUser.getId())
                .addSnapshotListener(eventListener);
        database.collection(KEYS.KEY_COLLECTION_CHAT)
                .whereEqualTo(KEYS.KEY_SENDER_ID,receivedUser.getId())
                .whereEqualTo(KEYS.KEY_RECEIVER_ID,sharedPref.getString(KEYS.KEY_USER_ID))
                .addSnapshotListener(eventListener);
    }

    private final EventListener<QuerySnapshot> eventListener = ((value, error) -> {
        if (error != null) {
            return;
        }
        if (value != null) {
            int count = chatMessages.size();
            for (DocumentChange documentChange : value.getDocumentChanges()) {
                if (documentChange.getType() == DocumentChange.Type.ADDED) {
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.setSenderId(documentChange.getDocument().getString(KEYS.KEY_SENDER_ID));
                    chatMessage.setReceiverId(documentChange.getDocument().getString(KEYS.KEY_RECEIVER_ID));
                    chatMessage.setMessage(documentChange.getDocument().getString(KEYS.KEY_MESSAGE));
                    chatMessage.setDateTime(getReadableDateTime(documentChange.getDocument().getDate(KEYS.KEY_TIMESTAMP)));
                    chatMessage.setDateObj(documentChange.getDocument().getDate(KEYS.KEY_TIMESTAMP));
                    chatMessages.add(chatMessage);
                }
            }Collections.sort(chatMessages,(obj1,obj2)->obj1.getDateObj().compareTo(obj2.getDateObj()));
            if(count==0){
                chatAdapter.notifyDataSetChanged();
            }else{
                chatAdapter.notifyItemRangeInserted(chatMessages.size(),chatMessages.size());
                binding.chatRecyclerView.smoothScrollToPosition(chatMessages.size()-1);
            }
            binding.chatRecyclerView.setVisibility(View.VISIBLE);
        }
        binding.chatProgressBar.setVisibility(View.GONE);
    });

    private Bitmap getBitmapFromString(String encoded) {
        byte[] bytes = Base64.decode(encoded, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    private void setListener() {

        binding.acivChatBack.setOnClickListener(v -> finish());
        binding.layoutSend.setOnClickListener(v -> sendMessage());
    }

    private void loadUser() {
        receivedUser = (User) getIntent().getSerializableExtra(KEYS.KEY_USER);
        binding.tvChatName.setText(receivedUser.getName());
    }

    private String getReadableDateTime(Date date) {
        return new SimpleDateFormat("MMMM dd,yyyy-hh:mm a", Locale.getDefault()).format(date);
    }
}