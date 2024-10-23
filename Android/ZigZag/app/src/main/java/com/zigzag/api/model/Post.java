package com.zigzag.api.model;


import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.zigzag.R;

import android.os.Bundle;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Post {
    private final String regex = "#(\\w{2,})";
    private int id;
    private String authorId;
    private String text;
    private String expiryDate;
    private String createdAt;
    private String updatedAt;
    private Location location;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getText() {
        return text;
    }

    public SpannableString getSpannableText(Context context) {
        SpannableString spannableString = new SpannableString(text);
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            ClickableSpan clickableSpan = getClickableSpan(context, matcher);

            spannableString.setSpan(clickableSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        return spannableString;
    }

    private static @NonNull ClickableSpan getClickableSpan(Context context, Matcher matcher) {
        String hashtag = matcher.group(1);

        return new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {

               Log.d("HASHTAG", "Clicked on hashtag: " + hashtag);

               // CODE TO OPEN TAG SEARCHING FRAGMENT

                NavController navController = Navigation.findNavController(view);
                Bundle bundle = new Bundle();
                bundle.putString("hashtag", hashtag);

                navController.navigate(R.id.action_mainFragment_to_searchFragment, bundle);

            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(ContextCompat.getColor(context, R.color.hashtag_text));
                ds.setUnderlineText(false);
            }
        };
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}

