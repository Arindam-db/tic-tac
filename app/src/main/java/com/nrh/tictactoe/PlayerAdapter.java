package com.nrh.tictactoe;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class PlayerAdapter extends RecyclerView.Adapter<PlayerAdapter.PlayerViewHolder> {

    private Context context;
    private List<String> playerEmails;
    private OnInviteClickListener onInviteClickListener;

    public PlayerAdapter(Context context, List<String> playerEmails, OnInviteClickListener onInviteClickListener) {
        this.context = context;
        this.playerEmails = playerEmails;
        this.onInviteClickListener = onInviteClickListener;
    }

    @NonNull
    @Override
    public PlayerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_player, parent, false);
        return new PlayerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlayerViewHolder holder, int position) {
        String email = playerEmails.get(position);
        holder.playerEmailTextView.setText(email);
        holder.inviteButton.setOnClickListener(v -> onInviteClickListener.onInviteClick(email));
    }

    @Override
    public int getItemCount() {
        return playerEmails.size();
    }

    public interface OnInviteClickListener {
        void onInviteClick(String email);
    }

    static class PlayerViewHolder extends RecyclerView.ViewHolder {

        TextView playerEmailTextView;
        Button inviteButton;

        PlayerViewHolder(@NonNull View itemView) {
            super(itemView);
            playerEmailTextView = itemView.findViewById(R.id.playerEmailTextView);
            inviteButton = itemView.findViewById(R.id.inviteButton);
        }
    }
}
