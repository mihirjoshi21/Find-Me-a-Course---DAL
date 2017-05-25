package com.conem.app.findmeacoursedal.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import com.conem.app.findmeacoursedal.R;
import com.conem.app.findmeacoursedal.adapter.SubjectAdapter;
import com.conem.app.findmeacoursedal.screen.SubjectActivity;
import com.conem.app.findmeacoursedal.util.ProjectUtil;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.conem.app.findmeacoursedal.screen.SubjectActivity.PATTERN;

public class TimeTableService extends Service {
    public TimeTableService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                new AsyncTask<Void, Void, ArrayList<String>>() {

                    @Override
                    protected ArrayList<String> doInBackground(Void... params) {
                        int count = -1;
                        ArrayList<String> subjectAvailable = new ArrayList<>();

                        try {

                            Set<String> savedItem = ProjectUtil.getSharedPreferencesString(getBaseContext());
                            for (String url : SubjectActivity.URL) {
                                Document doc = Jsoup.connect(url)
                                        .timeout(SubjectActivity.TIMEOUT)
                                        .post();
                                Matcher matcher = Pattern.compile(PATTERN).matcher(doc.toString()
                                        .replaceAll("\\n", ""));

                                while (matcher.find()) {
                                    if (savedItem.contains(matcher.group()
                                            .replaceAll(SubjectAdapter.SUBJECT_NAME_PATTERN, "$1")) &&
                                            !matcher.group().contains("darkred")) {
                                        subjectAvailable.add(matcher.group()
                                                .replaceAll(SubjectAdapter.SUBJECT_NAME_PATTERN, "$1"));
                                    }
                                }

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        return subjectAvailable;
                    }

                    @Override
                    protected void onPostExecute(ArrayList<String> s) {
                        super.onPostExecute(s);
                        if (!s.isEmpty()) {
                            NotificationManager mNotificationManager = (NotificationManager)
                                    getSystemService(Context.NOTIFICATION_SERVICE);
                            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getBaseContext())
                                    .setSmallIcon(getNotificationIcon())
                                    .setLargeIcon(BitmapFactory.decodeResource(getBaseContext().getResources(),
                                            R.mipmap.ic_launcher))
                                    .setAutoCancel(true)
                                    .setContentText(TextUtils.join(", ", s))
                                    .setStyle(new NotificationCompat.BigTextStyle().bigText(TextUtils.join(", ", s)))
                                    .setContentTitle("Subject Available");

                            Notification notification = notificationBuilder.build();

                            notification.flags |= Notification.FLAG_AUTO_CANCEL;
                            notification.defaults |= Notification.DEFAULT_LIGHTS | Notification.FLAG_ONLY_ALERT_ONCE |
                                    Notification.FLAG_AUTO_CANCEL;

                            mNotificationManager.notify(1, notification);
                        }
                    }
                }.execute();
            }
        };

        Timer timer = new Timer();
        timer.schedule(timerTask, 1000, 300000);


        return START_STICKY;
    }

    private static int getNotificationIcon() {
        boolean useWhiteIcon = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP);
        return useWhiteIcon ? R.drawable.silhouette : R.mipmap.ic_launcher;
    }
}
