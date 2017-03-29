package com.codepath.apps.simpletweets.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.simpletweets.R;
import com.codepath.apps.simpletweets.TwitterApplication;
import com.codepath.apps.simpletweets.models.User;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddNewTweetDialog extends DialogFragment {

    private static final int TWEET_MAX_LENGTH = 140;

    @BindView(R.id.etBody)
    EditText tvBody;

    @BindView(R.id.tvName)
    TextView tvName;

    @BindView(R.id.tvScreeName)
    TextView tvScreenName;

    @BindView(R.id.ivProfileImage)
    ImageView ivProfile;

    @BindView(R.id.btnAdd)
    Button btnAdd;

    @BindView(R.id.tilTweet)
    TextInputLayout tilTweet;

    public interface AddTweetListener {
        void addTweet(String tweetBody);
    }


    public AddNewTweetDialog() {

    }

    public static AddNewTweetDialog newInstance() {
        AddNewTweetDialog dialog = new AddNewTweetDialog();
        return dialog;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.add_tweet_dialog_fragment, container);
        ButterKnife.bind(this, rootView);
//        getDialog().setTitle(R.string.filter_dialog_name);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        User accountUser = TwitterApplication.getAccountUser();

        btnAdd.setOnClickListener(v -> {

            AddTweetListener listener = (AddTweetListener) getActivity();
            listener.addTweet(tvBody.getText().toString());
            dismiss();
        });

        if (accountUser == null) {
            Log.d("ERROR", "Account user is not set");
            return;
        }

        tvName.setText(accountUser.getName());
        tvScreenName.setText(String.format("@%s", accountUser.getScreenName()));
        Glide.with(getContext()).load(accountUser.getProfileUrl()).into(ivProfile);

        setupFloatingLabelError();

    }

    private void setupFloatingLabelError() {
        tilTweet.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence text, int start, int count, int after) {
                if (text.length() >= TWEET_MAX_LENGTH) {
                    tilTweet.setError(getString(R.string.too_long_tweet_error));
                    tilTweet.setErrorEnabled(true);
                    btnAdd.setEnabled(false);
                } else {
                    tilTweet.setErrorEnabled(false);
                    btnAdd.setEnabled(true);
                }
                tilTweet.setHint(String.format("%d characters are left ", TWEET_MAX_LENGTH - text.length()));
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


}
