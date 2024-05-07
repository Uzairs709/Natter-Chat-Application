package com.example.natterchatapp.adapters;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.natterchatapp.databinding.ItemContainerRecieveMessageBinding;
import com.example.natterchatapp.databinding.ItemContainerSentMessageBinding;
import com.example.natterchatapp.models.ChatMessage;

import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final ArrayList<ChatMessage> chatMessages;
    private final Bitmap receiverProfileImage;
    private final String senderId;
    public static final int VIEW_TYPE_SENT = 1;
    public static final int VIEW_TYPE_RECEIVED = 2;

    public ChatAdapter(ArrayList<ChatMessage> chatMessages, String senderId, Bitmap recieverProfileImage) {
        this.chatMessages = chatMessages;
        this.senderId = senderId;
        this.receiverProfileImage = recieverProfileImage;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_SENT) {
            return new SentMessageViewHolder(
                    ItemContainerSentMessageBinding
                            .inflate(LayoutInflater.from(parent.getContext())
                                    , parent,
                                    false));
        } else {
            return new RecievedMessageViewHolder(
                    ItemContainerRecieveMessageBinding
                            .inflate(LayoutInflater.from(parent.getContext())
                                    , parent,
                                    false));
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == VIEW_TYPE_SENT) {
            ((SentMessageViewHolder) holder).setData(chatMessages.get(position));
        } else {
            ((RecievedMessageViewHolder) holder).setData(chatMessages.get(position), receiverProfileImage);
        }
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (chatMessages.get(position).getSenderId().equals(senderId)) {
            return VIEW_TYPE_SENT;
        } else {
            return VIEW_TYPE_RECEIVED;
        }
    }

    static class SentMessageViewHolder extends RecyclerView.ViewHolder {
        ItemContainerSentMessageBinding binding;

        public SentMessageViewHolder(ItemContainerSentMessageBinding itemContainerSentMessageBinding) {
            super(itemContainerSentMessageBinding.getRoot());
            binding = itemContainerSentMessageBinding;
        }

        void setData(ChatMessage chatMessage) {
            binding.tvSentMessage.setText(chatMessage.getMessage());
            binding.tvSentDateTime.setText(chatMessage.getDateTime());
        }
    }

    static class RecievedMessageViewHolder extends RecyclerView.ViewHolder {
        ItemContainerRecieveMessageBinding binding;

        public RecievedMessageViewHolder(ItemContainerRecieveMessageBinding itemContainerReceivedMessageBinding) {
            super(itemContainerReceivedMessageBinding.getRoot());
            binding = itemContainerReceivedMessageBinding;
        }

        void setData(ChatMessage chatMessage, Bitmap receiverProfileImage) {
            binding.tvReceivedMessage.setText(chatMessage.getMessage());
            binding.tvReceivedDateTime.setText(chatMessage.getDateTime());
            binding.rivReceivedMessageProfile.setImageBitmap(receiverProfileImage);
        }
    }

}