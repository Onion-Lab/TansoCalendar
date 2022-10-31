package com.example.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements Button.OnClickListener {

    PieChart pieChart;
    Button sentenceButton, pointButton, calendarButton;
    int[] colorArray = new int[] {Color.GREEN, Color.BLUE};
    String[] sentence = {
            "*미니멀 라이프 실천하기",
            "*나무 심기",
            "*찬물로 옷 세탁하기",
            "*절수형 샤워헤드로 교체해서 물 절약하기",
            "*전자기기 플러그 뽑기",
            "*유기농이나 제철 지역 농산물 먹기",
            "*고기 적게 먹기",
            "*메일함 정리하기",
            "*영상 다운로드해서 보기",
            "*유튜브 등 영상 자동재생 기능 차단하여 사용하기",
            "*컴퓨터 절전프로그램(그린터치) 사용하기",
            "*적정실내온도 유지하기",
            "*스마트폰, 전자기기 배경화면 밝기 낮춰서 절전모드로 사용하기",
            "*머리말릴때 드라이기 냉,온풍 함께 사용하기",
            "*장바구니 이용하기",
            "*계단이용하기",
            "*텀블러 사용하기",
            "*점심시간 등 하루 1시간 소등하기",
            "*샤워시간 줄이기",
            "*빨래 한번에 모아서 하기",
            "*종이영수증 대신 전자영수증 받기",
            "*세수나 설거지할때 물받아서 사용하기",
            "*개인손수건 들고다니기",
            "*재활용 잘하기",
            "*되도록이면 이코노미 클래스 타기",
            "*자동차의 타이어에 공기압을 적절하게 유지",
            "*과속운전은 많은 탄소량을 배출함",
            "*LED 사용하기",
            "*노트북이 데스크탑 컴퓨터보다 충전 및 작동에 더 적은 에너지를 씀",
            "*적절한 물량만 전기포트에 채우기",
            "*택배박스 재사용하기",
            "*건조기 대신 빨래널기",
            "*온수 사용을 줄이기"
    };
    private DBHelper DB = new DBHelper(MainActivity.this);
    TextView tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pieChart = findViewById(R.id.picChart);
        sentenceButton = findViewById(R.id.sentenceButton);
        pointButton = findViewById(R.id.pointButton);
        calendarButton = findViewById(R.id.calendarButton);
        tv = findViewById(R.id.Higher);

        sentenceButton.setOnClickListener(this) ;
        pointButton.setOnClickListener(this) ;
        calendarButton.setOnClickListener(this) ;

        PieDataSet pieDataSet = new PieDataSet(data1(),"Weekend chart");
        pieDataSet.setColors(colorArray);
        PieData pieData = new PieData(pieDataSet);
        pieChart.setDrawEntryLabels(true);
        pieChart.setUsePercentValues(true);
        pieData.setValueTextSize(30);
        pieChart.setCenterText("CO2");
        pieChart.setCenterTextSize(25);
        pieChart.setHoleRadius(30);
        pieChart.setData(pieData);
        pieChart.invalidate();
    }

    private ArrayList<PieEntry> data1() {
        ArrayList<PieEntry> datavalue = new ArrayList<>();

        double this_week = DB.getCarbonAverageThisWeek();
        double last_week = DB.getCarbonAverageLastWeek();

        datavalue.add(new PieEntry((float) (last_week/(this_week+last_week)),"Last Week"));
        datavalue.add(new PieEntry((float) (this_week/(this_week+last_week)),"This Week"));

        if(this_week > last_week){
            tv.setText("이번주에 더 많이 사용했어요");
        }else tv.setText("저번주에 더 많이 사용했어요");

        return datavalue;
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.sentenceButton :
                AlertDialog.Builder myAlertBuilder =
                        new AlertDialog.Builder(MainActivity.this);
                // alert의 title과 Messege 세팅
                myAlertBuilder.setTitle("탄소 배출 줄이는 팁");
                int max_num_value = sentence.length-1;
                int min_num_value = 0;
                Random random = new Random();

                int randomNum = random.nextInt(max_num_value - min_num_value + 1) + min_num_value;

                myAlertBuilder.setMessage(sentence[randomNum]);
                // 버튼 추가 (Ok 버튼과 Cancle 버튼 )
                myAlertBuilder.setPositiveButton("Ok",new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog,int which){
                    }
                });
                myAlertBuilder.setNegativeButton("Cancle", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                // Alert를 생성해주고 보여주는 메소드(show를 선언해야 Alert가 생성됨)
                myAlertBuilder.show();
                break ;
            case R.id.pointButton :
                Log.e("sex","pointButton");
                break ;
            case R.id.calendarButton :
                Intent intent = new Intent(getApplicationContext(),calendar2Activity.class);
                startActivity(intent);
                break ;
        }
    }
    public void onClickShowAlert(View view) {

    }
}