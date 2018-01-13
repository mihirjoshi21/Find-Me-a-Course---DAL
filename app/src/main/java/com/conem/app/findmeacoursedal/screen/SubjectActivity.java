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
import com.conem.app.findmeacoursedal.util.ProjectUtil;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by mj on 5/26/2017.
 */

public class SubjectActivity extends AppCompatActivity {

    public static final String[] URL = {"https://dalonline.dal.ca/PROD/fysktime.P_DisplaySchedule?s_term=201810&s_crn=&s_subj=CSCI&s_numb=&n=1&s_district=100",
            "https://dalonline.dal.ca/PROD/fysktime.P_DisplaySchedule?s_term=201820&s_crn=&s_subj=CSCI&s_numb=&n=1&s_district=100"};
    //TODO Summer URL

    public static final int TIMEOUT = 60000;
    public static final String PATTERN = "CSCI \\d{3,}.*?T\\d{3,}\\s*<";

    @BindView(R.id.spinner)
    Spinner spinner;
    @BindView(R.id.spinner_page)
    Spinner spinnerPage;
    @BindView(R.id.recycle_list)
    RecyclerView recycleList;
    @BindView(R.id.progress)
    ProgressBar progress;

    private SubjectActivity mActivity;
    private AsyncTask<Void, ArrayList<String>, ArrayList<String>> mAsyncTask;
    private Unbinder unbinder;
    private int termPosition = 0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mActivity = this;
        unbinder = ButterKnife.bind(mActivity);

        setLayoutManager(mActivity, recycleList, LinearLayoutManager.VERTICAL);
        spinner.setAdapter(new ArrayAdapter<>(mActivity, android.R.layout.simple_spinner_dropdown_item,
                new String[]{"FALL", "WINTER"}));
        spinnerPage.setAdapter(new ArrayAdapter<>(mActivity, android.R.layout.simple_spinner_dropdown_item,
                new String[]{"1", "2", "3"}));

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (mAsyncTask != null && !mAsyncTask.isCancelled()) {
                    System.out.println("Cancelling");
                    mAsyncTask.cancel(true);
                }
                termPosition = position;
                spinnerPage.setSelection(0);
                searchDal(position, 0);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerPage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (mAsyncTask != null && !mAsyncTask.isCancelled()) {
                    System.out.println("Cancelling");
                    mAsyncTask.cancel(true);
                }
                searchDal(termPosition, position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        startService(new Intent(this, TimeTableService.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        stopService(new Intent(this, TimeTableService.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        mAsyncTask.cancel(true);
    }

    private void searchDal(final int position, final int pagePosition) {

        setVisibility(true);
        mAsyncTask = new AsyncTask<Void, ArrayList<String>, ArrayList<String>>() {

            @Override
            protected ArrayList<String> doInBackground(Void... params) {

                System.out.println("Background");

                ArrayList<String> subjectList = new ArrayList<>();
                try {
                    Matcher matcher = Pattern.compile(PATTERN).matcher(ProjectUtil
                            .readUrl(pagePosition == 0 ? URL[position] :
                                    (pagePosition == 1 ? URL[position].replaceAll("n=\\d+", "n=21")
                                            : URL[position].replaceAll("n=\\d+", "n=41")))
                            .replaceAll("\\n", ""));

                    while (matcher.find()) {
                        subjectList.add(matcher.group());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return subjectList;
            }

            @Override
            protected void onCancelled(ArrayList<String> strings) {
                super.onCancelled(strings);
                System.out.println(strings);
            }

            @Override
            protected void onPostExecute(ArrayList<String> subjects) {
                super.onPostExecute(subjects);

                setVisibility(false);
                System.out.println("Post");
                SubjectAdapter subjectAdapter = new SubjectAdapter(mActivity, subjects);
                recycleList.setAdapter(subjectAdapter);

            }
        };
        mAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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
