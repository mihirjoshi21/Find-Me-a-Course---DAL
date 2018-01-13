package com.conem.app.findmeacoursedal.adapter;


import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.conem.app.findmeacoursedal.R;
import com.conem.app.findmeacoursedal.util.ProjectUtil;

import java.util.ArrayList;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mj on 5/26/2017.
 */
public class SubjectAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final String SUBJECT_NAME_PATTERN = "(CSCI \\d{4,}.*?)<.*";
    public static final String FONT_COLOR_PATTERN = ".*?(<font color[^>]*>.*?</font>).*";

    static class RecyclerViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.text_subject)
        TextView textSubject;
        @BindView(R.id.text_seats)
        TextView textSeats;
        @BindView(R.id.check)
        CheckBox check;

        public RecyclerViewHolders(Context context, View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }

        @Override
        public void onClick(View view) {
        }

    }

    private Activity mActivity;
    private final ArrayList<String> mSubjects;
    private final Set<String> mSavedSet;

    public SubjectAdapter(Activity activity, ArrayList<String> subjects) {
        mActivity = activity;
        mSubjects = subjects;
        mSavedSet = ProjectUtil.getSharedPreferencesString(mActivity);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutView;
        layoutView = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.item_subject, parent, false);
        return new RecyclerViewHolders(mActivity, layoutView);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        ((RecyclerViewHolders) holder).textSubject.
                setText(mSubjects.get(position).replaceAll(SUBJECT_NAME_PATTERN, "$1"));
        ((RecyclerViewHolders) holder).textSeats.
                setText(Html.fromHtml(mSubjects.get(position)
                        .replaceAll(FONT_COLOR_PATTERN, "$1").replaceAll("darkred", "#8B0000")));

        ((RecyclerViewHolders) holder).check.setChecked(mSavedSet
                .contains(((RecyclerViewHolders) holder).textSubject.getText().toString()));

        ((RecyclerViewHolders) holder).check.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                mSavedSet.add(((RecyclerViewHolders) holder).textSubject.getText().toString());
            } else {
                mSavedSet.remove(((RecyclerViewHolders) holder).textSubject.getText().toString());
            }
            ProjectUtil.setSharedSet(mActivity, mSavedSet);
        });

        ((RecyclerViewHolders) holder).itemView.setOnClickListener(v -> {

        });

    }

    @Override
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
    }

    @Override
    public int getItemCount() {
        return mSubjects.size();
    }

}


