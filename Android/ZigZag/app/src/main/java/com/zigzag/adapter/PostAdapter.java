package com.zigzag.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zigzag.R;
import com.zigzag.api.model.Post;

import java.util.ArrayList;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {
    private List<Post> posts = new ArrayList<>();



    public static class PostViewHolder extends RecyclerView.ViewHolder {
        private final TextView timeAgoTextView;
        private final TextView contentTextView;
        private final TextView distanceTextView;

        public PostViewHolder(View itemView) {
            super(itemView);
            timeAgoTextView = itemView.findViewById(R.id.post_time_ago_text);
            contentTextView = itemView.findViewById(R.id.post_content_text);
            distanceTextView = itemView.findViewById(R.id.post_distance_text);
        }

        public void bind(Post post) {
            timeAgoTextView.setText(post.getCreatedAt());
            contentTextView.setText(post.getSpannableText(itemView.getContext()));
            distanceTextView.setText(post.getLocation().getDistanceString());

            // setMovementMethod is required to make the TextView allow click events.
            contentTextView.setMovementMethod(android.text.method.LinkMovementMethod.getInstance());
        }
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        holder.bind(posts.get(position));
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public void updatePosts(List<Post> newPosts) {
        posts.clear();
        posts.addAll(newPosts);
        notifyDataSetChanged();
    }


}
