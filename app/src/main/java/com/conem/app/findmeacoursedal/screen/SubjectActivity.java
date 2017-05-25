package com.conem.app.findmeacoursedal.screen;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.conem.app.findmeacoursedal.R;
import com.conem.app.findmeacoursedal.adapter.SubjectAdapter;
import com.conem.app.findmeacoursedal.service.TimeTableService;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by mj on 5/26/2017.
 */

public class SubjectActivity extends AppCompatActivity {

    public static final String[] URL = {"https://dalonline.dal.ca/PROD/fysktime.P_DisplaySchedule?s_term=201810&s_crn=&s_subj=CSCI&s_numb=&n=21&s_district=100",
            "https://dalonline.dal.ca/PROD/fysktime.P_DisplaySchedule?s_term=201820&s_crn=&s_subj=CSCI&s_numb=&n=21&s_district=100"};
    //TODO Summer URL

    public static final int TIMEOUT = 60000;
    public static final String PATTERN = "CSCI \\d{3,}.*?T\\d{3,}\\s*<";

    @Bind(R.id.spinner)
    Spinner spinner;
    @Bind(R.id.recycle_list)
    RecyclerView recycleList;
    @Bind(R.id.progress)
    ProgressBar progress;

    private SubjectActivity mActivity;
    private AsyncTask mAsyncTask;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mActivity = this;
        ButterKnife.bind(mActivity);

        startService(new Intent(this,TimeTableService.class));

        setLayoutManager(mActivity, recycleList, LinearLayoutManager.VERTICAL);
        spinner.setAdapter(new ArrayAdapter<>(mActivity, android.R.layout.simple_spinner_dropdown_item,
                new String[]{"FALL", "WINTER"}));

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                searchDal(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(mActivity);
        mAsyncTask.cancel(true);
    }

    private void searchDal(final int position) {

        if (mAsyncTask != null) mAsyncTask.cancel(true);

        setVisibility(true);
        mAsyncTask = new AsyncTask<Void, ArrayList<String>, ArrayList<String>>() {

            @Override
            protected ArrayList<String> doInBackground(Void... params) {

                int count = -1;
                ArrayList<String> subjectList = new ArrayList<>();
                try {
                    Document doc = Jsoup.connect(URL[position]).timeout(TIMEOUT).post();

                    Matcher matcher = Pattern.compile(PATTERN).matcher(doc.toString().replaceAll("\\n", ""));

                    while (matcher.find()) {
                        subjectList.add(matcher.group());
                    }
                    count = 0;

                    System.out.println(count);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return subjectList;
            }

            @Override
            protected void onPostExecute(ArrayList<String> subjects) {
                super.onPostExecute(subjects);

                setVisibility(false);
                SubjectAdapter subjectAdapter = new SubjectAdapter(mActivity, subjects);
                recycleList.setAdapter(subjectAdapter);

            }
        };
        mAsyncTask.execute((Void[]) null);
    }

    /**
     * Set recyclerView
     *
     * @param activity     activity instance
     * @param recyclerView recyclerView instance
     * @param orientation  orientation of recyclerView
     */
    private void setLayoutManager(Activity activity, RecyclerView recyclerView, int orientation) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
        linearLayoutManager.setOrientation(orientation);
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    /**
     * Show progress or recyclerView
     *
     * @param showProgress true if show progress
     */
    private void setVisibility(boolean showProgress) {
        progress.setVisibility(showProgress ? View.VISIBLE : View.GONE);
        recycleList.setVisibility(!showProgress ? View.VISIBLE : View.GONE);
    }
}
