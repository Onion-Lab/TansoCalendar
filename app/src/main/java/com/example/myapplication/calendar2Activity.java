package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AppOpsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.CarbonDAO;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class calendar2Activity extends AppCompatActivity implements OnDateSelectedListener {

    private float tanso = 0;

    private long runningtime = SystemClock.uptimeMillis();

    private String todate = "";

    private DBHelper DB = new DBHelper(calendar2Activity.this);
    MaterialCalendarView materialCalendarView;
    Button permissionButton;

    Button bt;
    EditText et;
    TextView tv,tvcont;

    String tempdate = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar2);

        materialCalendarView = findViewById(R.id.calendarView);
        bt = findViewById(R.id.savebt);
        et = findViewById(R.id.memosave);
        tv = findViewById(R.id.memo);
        tvcont = findViewById(R.id.memocontent);
        permissionButton = findViewById(R.id.permissionButton);

        Calendar cal = Calendar.getInstance();
        String format = "yyyy-MM";
        String format2 = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        SimpleDateFormat sdf2 = new SimpleDateFormat(format2);
        String date = sdf.format(cal.getTime());
        String[] date2 = sdf2.format(cal.getTime()).split("-");

        ArrayList<CarbonDAO> temp = DB.selectCarbonTable();

        Log.e("telechips",date2[2]);

        for(int i = 1 ; i <= Integer.parseInt(date2[2]); i++){
            Log.e("test1",""+i);
            AppCarbonInfo appCarbonInfo = getApplicationUsingTime(Integer.parseInt(date2[0]), Integer.parseInt(date2[1])-1, i);

            try{
                DB.insertCarbonTable(date2[0]+"-"+date2[1]+"-"+Integer.toString(i),appCarbonInfo.getTotalCarbonEmit(), DB.getCarbonDAOByDate(date2[0]+"-"+date2[1]+Integer.toString(i)).memo);
            }catch(NullPointerException e){
                DB.insertCarbonTable(date2[0]+"-"+date2[1]+"-"+Integer.toString(i),appCarbonInfo.getTotalCarbonEmit(), " ");
            }
        }

        for(int i = 0 ; i < temp.size(); i++){
            Log.e("telechips",temp.get(i).carbondate + "\t"+ temp.get(i).memo + "\t" + temp.get(i).carbonemit);
        }


        ArrayList<CarbonDAO> higher = DB.getDateHigherThanAverage(Integer.parseInt(date.split("-")[0]),Integer.parseInt(date.split("-")[1]));
        ArrayList<CarbonDAO> lower = DB.getDateLowerThanAverage(Integer.parseInt(date.split("-")[0]),Integer.parseInt(date.split("-")[1]));

        for(int i = 0 ; i < higher.size(); i++){
            materialCalendarView.addDecorator(new EventDecorator(Color.RED, change_calenarday(higher.get(i).carbondate), calendar2Activity.this));
        }

        for(int i = 0 ; i < lower.size(); i++){
            materialCalendarView.addDecorator(new EventDecorator2(Color.RED, change_calenarday(lower.get(i).carbondate), calendar2Activity.this));
        }
        materialCalendarView.setOnDateChangedListener(this);


        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                bt.setVisibility(View.INVISIBLE);
                et.setVisibility(View.INVISIBLE);
                tv.findViewById(View.INVISIBLE);
                tvcont.setVisibility(View.INVISIBLE);



                DB.insertCarbonTable(tempdate,0,et.getText().toString());

                Toast.makeText(getApplicationContext(), "메모가 저장되었습니다",Toast.LENGTH_SHORT).show();
                et.setText("");
            }
        });

        if(!checkPermission()){
            permissionButton.setVisibility(View.VISIBLE);
        }
        else{
            permissionButton.setVisibility(View.INVISIBLE);
        }

        permissionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!checkPermission())
                    startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
            }
        });
    }

    public Set<CalendarDay> change_calenarday(String ymd){
        String[] temp = ymd.split("-");

        return Collections.singleton(CalendarDay.from( Integer.parseInt(temp[0]),Integer.parseInt(temp[1])-1,Integer.parseInt(temp[2])));
    }

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
        bt.setVisibility(View.VISIBLE);
        et.setVisibility(View.VISIBLE);
        tv.setVisibility(View.VISIBLE);
        tvcont.setVisibility(View.VISIBLE);

        AppCarbonInfo appCarbonInfo = getApplicationUsingTime(date.getYear(), date.getMonth(), date.getDay());

        tempdate = Integer.toString(date.getYear())+"-"+Integer.toString(date.getMonth())+"-"+Integer.toString(date.getDay());
        Toast.makeText(getApplicationContext(), appCarbonInfo.getTotalTimeMinute() + "분, 탄소:" + appCarbonInfo.getTotalCarbonEmit() + "g", Toast.LENGTH_SHORT).show();
        try{
            tvcont.setText(DB.getCarbonDAOByDate(tempdate).memo);
            DB.insertCarbonTable(tempdate,appCarbonInfo.getTotalCarbonEmit(),DB.getCarbonDAOByDate(tempdate).memo);
        }catch(NullPointerException e){
            tvcont.setText("");
            DB.insertCarbonTable(tempdate,appCarbonInfo.getTotalCarbonEmit(),"");
        }
    }

    // 큰놈
    public class EventDecorator implements DayViewDecorator {

        private final Drawable drawable;
        private int color;
        private HashSet<CalendarDay> dates;

        @SuppressLint("UseCompatLoadingForDrawables")
        public EventDecorator(int color, Collection<CalendarDay> dates, Activity context) {
            drawable = context.getResources().getDrawable(R.drawable.more);
            this.color = color;
            this.dates = new HashSet<>(dates);
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return dates.contains(day);
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.setSelectionDrawable(drawable);
        }
    }

    // 작은놈
    public class EventDecorator2 implements DayViewDecorator {

        private final Drawable drawable;
        private int color;
        private HashSet<CalendarDay> dates;

        @SuppressLint("UseCompatLoadingForDrawables")
        public EventDecorator2(int color, Collection<CalendarDay> dates, Activity context) {
            drawable = context.getResources().getDrawable(R.drawable.more2);
            this.color = color;
            this.dates = new HashSet<>(dates);
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return dates.contains(day);
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.setSelectionDrawable(drawable);
        }
    }

    private AppCarbonInfo getApplicationUsingTime(int year, int month, int date) {
        Calendar startCalendar = Calendar.getInstance();
        Calendar endCalendar = Calendar.getInstance();
        startCalendar.set(year, month, date, 0, 0, 0);
        endCalendar.set(year, month, date, 23, 59, 59);
//        endCalendar.add(Calendar.SECOND, 1);
        UsageStatsManager usageStatsManager = (UsageStatsManager) getApplicationContext().getSystemService(Context.USAGE_STATS_SERVICE);
        List<UsageStats> usageStatsList = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startCalendar.getTimeInMillis(), endCalendar.getTimeInMillis());
        AppCarbonInfo appCarbonInfo = new AppCarbonInfo();
        for(UsageStats usageStats : usageStatsList)
        {
            int appTimeMinute = (int)(usageStats.getTotalTimeInForeground() / (1000 * 60));
            if(usageStats.getPackageName().equals("com.google.android.youtube"))
            {
                appCarbonInfo.youtubeCarbonEmit = appCarbonInfo.youtubeCarbonEmit + appTimeMinute * 16.8;
                appCarbonInfo.youtubeTimeMinute = appCarbonInfo.youtubeTimeMinute + appTimeMinute;
            }
            else if(usageStats.getPackageName().equals("com.netflix.mediaclient"))
            {
                appCarbonInfo.netflixCarbonEmit = appCarbonInfo.netflixCarbonEmit + appTimeMinute * 7.3;
                appCarbonInfo.netflixTimeMinute = appCarbonInfo.netflixTimeMinute + appTimeMinute;
            }
            else if(usageStats.getPackageName().equals("com.google.android.apps.meetings"))
            {
                appCarbonInfo.googleMeetCarbonEmit = appCarbonInfo.googleMeetCarbonEmit +  appTimeMinute * 3.4;
                appCarbonInfo.googleMeetTimeMinute = appCarbonInfo.googleMeetTimeMinute + appTimeMinute;
            }
            else if(usageStats.getPackageName().equals("us.zoom.videomeetings"))
            {
                appCarbonInfo.zoomCarbonEmit = appCarbonInfo.zoomCarbonEmit + appTimeMinute * 2.62;
                appCarbonInfo.zoomTimeMinute = appCarbonInfo.zoomTimeMinute + appTimeMinute;
            }
            else if(usageStats.getPackageName().equals("com.skype.raider"))
            {
                appCarbonInfo.skypeCarbonEmit = appCarbonInfo.skypeCarbonEmit + appTimeMinute * 2;
                appCarbonInfo.skypeTimeMinute = appCarbonInfo.skypeTimeMinute + appTimeMinute;
            }
            else if(usageStats.getPackageName().equals("com.ss.android.ugc.trill"))
            {
                appCarbonInfo.tiKTokCarbonEmit = appCarbonInfo.tiKTokCarbonEmit + appTimeMinute * 1.5;
                appCarbonInfo.tiKTokTimeMinute = appCarbonInfo.tiKTokTimeMinute + appTimeMinute;
            }
            else if(usageStats.getPackageName().equals("com.facebook.katana"))
            {
                appCarbonInfo.facebookCarbonEmit = appCarbonInfo.facebookCarbonEmit + appTimeMinute * 0.2;
                appCarbonInfo.facebookTimeMinute = appCarbonInfo.facebookTimeMinute + appTimeMinute;
            }
            else if(usageStats.getPackageName().equals("com.sec.android.app.sbrowser"))
            {
                appCarbonInfo.internetCarbonEmit = appCarbonInfo.internetCarbonEmit + appTimeMinute * 0.2;
                appCarbonInfo.internetTimeMinute = appCarbonInfo.internetTimeMinute + appTimeMinute;
            }
            else if(usageStats.getPackageName().equals("com.instagram.android"))
            {
                appCarbonInfo.instagramCarbonEmit = appCarbonInfo.instagramCarbonEmit + appTimeMinute * 0.1;
                appCarbonInfo.instagramTimeMinute = appCarbonInfo.instagramTimeMinute + appTimeMinute;
            }
            else if(usageStats.getPackageName().equals("com.kakao.talk"))
            {
                appCarbonInfo.kakaoTalkCarbonEmit = appCarbonInfo.kakaoTalkCarbonEmit + appTimeMinute * 0.4;
                appCarbonInfo.kakaoTalkTimeMinute = appCarbonInfo.kakaoTalkTimeMinute + appTimeMinute;
            }
        }
        return appCarbonInfo;
    }

    private boolean checkPermission(){

        boolean granted = false;

        AppOpsManager appOps = (AppOpsManager) getApplicationContext()
                .getSystemService(Context.APP_OPS_SERVICE);

        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(), getApplicationContext().getPackageName());

        if (mode == AppOpsManager.MODE_DEFAULT) {
            granted = (getApplicationContext().checkCallingOrSelfPermission(
                    android.Manifest.permission.PACKAGE_USAGE_STATS) == PackageManager.PERMISSION_GRANTED);
        }
        else {
            granted = (mode == AppOpsManager.MODE_ALLOWED);
        }

        return granted;
    }

}