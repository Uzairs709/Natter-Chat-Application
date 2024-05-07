package com.example.natterchatapp.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
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
import com.example.natterchatapp.adapters.ChatAdapter;
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

    private View viewBackground;
    private AppCompatImageView acivBack, acivInfo;
    private TextView tvChatName;
    private RecyclerView chatRecyclerView;
    private ProgressBar chatProgressBar;
    private FrameLayout layoutSend;
    private EditText inputMessage;
    private User receivedUser;
    private ArrayList<ChatMessage> messages;
    private ChatAdapter adapter;
    private FirebaseFirestore db;
    private Preference sharedPref;
    private String conversationId = "";
    private boolean isReceiverAvailable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat);
        receivedUser = (User) getIntent().getSerializableExtra(KEYS.KEY_USER);

        init();
        listners();
        if (receivedUser != null) {
            tvChatName.setText(receivedUser.getName());
        }
        listenMessages();
    }

    private void listners() {
        acivBack.setOnClickListener(v -> finish());
        layoutSend.setOnClickListener(v -> sendMessage());

    }

    private void listenMessages() {
        db.collection(KEYS.KEY_COLLECTION_CHAT)
                .whereEqualTo(KEYS.KEY_SENDER_ID, sharedPref.getString(KEYS.KEY_USER_ID))
                .whereEqualTo(KEYS.KEY_RECEIVER_ID, receivedUser.getId())
                .addSnapshotListener(eventListener);
        db.collection(KEYS.KEY_COLLECTION_CHAT)
                .whereEqualTo(KEYS.KEY_SENDER_ID, receivedUser.getId())
                .whereEqualTo(KEYS.KEY_RECEIVER_ID, sharedPref.getString(KEYS.KEY_USER_ID))
                .addSnapshotListener(eventListener);

    }

    private final EventListener<QuerySnapshot> eventListener = (value, error) -> {
        if (error != null) {
            return;
        }
        if (value != null) {
            int count = messages.size();
            for (DocumentChange doc : value.getDocumentChanges()) {
                if (doc.getType() == DocumentChange.Type.ADDED) {
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.setSenderId(doc.getDocument().getString(KEYS.KEY_SENDER_ID));
                    chatMessage.setReceiverId(doc.getDocument().getString(KEYS.KEY_RECEIVER_ID));
                    chatMessage.setMessage(doc.getDocument().getString(KEYS.KEY_MESSAGE));
                    chatMessage.setDateTime(getReadableDateTime(doc.getDocument().getDate(KEYS.KEY_TIMESTAMP)));
                    chatMessage.setDateObj(doc.getDocument().getDate(KEYS.KEY_TIMESTAMP));
                    messages.add(chatMessage);
                }
            }
            Collections.sort(messages, (obj1, obj2) -> obj1.getDateObj().compareTo(obj2.getDateObj()));
            if (count == 0) {
                adapter.notifyDataSetChanged();
            } else {
                adapter.notifyItemRangeInserted(messages.size(), messages.size());
                chatRecyclerView.smoothScrollToPosition(messages.size() - 1);
            }
            chatRecyclerView.setVisibility(View.VISIBLE);
        }
        chatProgressBar.setVisibility(View.GONE);
    };


    private String getReadableDateTime(Date date) {
        return new SimpleDateFormat(" MMMM dd, yyyy -hh:mm a",
                Locale.getDefault()).format(date);
    }

    private Bitmap getBitmapFromEncodedString(String encodedImage) {
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    public void init() {
        viewBackground = findViewById(R.id.viewChatBackground);
        acivBack = findViewById(R.id.acivChatBack);
        acivInfo = findViewById(R.id.acivChatInfo);
        tvChatName = findViewById(R.id.tvChatName);
        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        chatProgressBar = findViewById(R.id.chatProgressBar);
        layoutSend = findViewById(R.id.layoutSend);
        inputMessage = findViewById(R.id.inputMessage);

        sharedPref = new Preference(ChatActivity.this);
        messages = new ArrayList<>();
        adapter = new ChatAdapter(messages,
                sharedPref.getString(KEYS.KEY_USER_ID),
                getBitmapFromEncodedString(receivedUser.getImage()));

        chatRecyclerView.setAdapter(adapter);
        db = FirebaseFirestore.getInstance();
    }


    private void sendMessage() {
        if (!inputMessage.getText().toString().isEmpty()) {
            HashMap<String, Object> message = new HashMap<>();
            message.put(KEYS.KEY_USER_ID, sharedPref.getString(KEYS.KEY_USER_ID));
            message.put(KEYS.KEY_RECEIVER_ID, receivedUser.getId());
            message.put(KEYS.KEY_MESSAGE, inputMessage.getText().toString().trim());
            message.put(KEYS.KEY_TIMESTAMP, new Date());
            db.collection(KEYS.KEY_COLLECTION_CHAT).add(message);
            inputMessage.setText("");
        }
    }
}