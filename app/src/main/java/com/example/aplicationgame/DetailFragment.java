package com.example.aplicationgame;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import androidx.fragment.app.DialogFragment;

public class DetailFragment extends DialogFragment {

    private String detailText;

    public DetailFragment() {}

    public static DetailFragment newInstance(String detail) {
        DetailFragment fragment = new DetailFragment();
        Bundle args = new Bundle();
        args.putString("detail", detail);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            detailText = getArguments().getString("detail");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        TextView detailTextView = view.findViewById(R.id.detail_text);
        Button closeButton = view.findViewById(R.id.close_button);

        detailTextView.setText(detailText);
        closeButton.setOnClickListener(v -> dismiss());

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            params.height = (int) (getResources().getDisplayMetrics().heightPixels * 0.5); // 50% dari tinggi layar
            getDialog().getWindow().setAttributes(params);
        }
    }
}