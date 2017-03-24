package com.codepath.apps.simpletweets.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.simpletweets.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddNewTweetDialog extends DialogFragment {

    @BindView(R.id.etBody)
    EditText tvBody;

    @BindView(R.id.tvName)
    TextView tvName;

    @BindView(R.id.tvScreeName)
    TextView tvScreenName;

    @BindView(R.id.ivProfileImage)
    ImageView ivProfile;

    @BindView(R.id.btnAdd)
    Button button;


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
    }


}
