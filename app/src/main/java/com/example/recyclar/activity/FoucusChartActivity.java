package com.example.recyclar.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.recyclar.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FoucusChartActivity extends AppCompatActivity {

    private LineChart lineChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foucus_chart);

        lineChart = findViewById(R.id.lineChart);
        lineChart.getDescription().setEnabled(false); // 不显示描述
        lineChart.setTouchEnabled(true);  // 启用触摸操作
        lineChart.setDragEnabled(true);   // 启用拖拽
        lineChart.setScaleEnabled(true);  // 启用缩放
        lineChart.setPinchZoom(true);     // 启用双指缩放

        // 获取过去7天的专注次数
        List<Entry> entries = getFocusDataForLast7Days();

        // 创建折线数据集
        LineDataSet dataSet = new LineDataSet(entries, "专注次数");
        dataSet.setColor(Color.BLUE);  // 设置折线的颜色
        dataSet.setValueTextColor(Color.BLACK); // 设置数据点数值的颜色
        dataSet.setDrawIcons(false);  // 不显示数据点的图标
        dataSet.setDrawValues(false); // 不显示数据点的数值

        // 创建 LineData 并设置到图表中
        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);
        lineChart.invalidate(); // 刷新图表

        // 设置 X 轴的配置
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);  // 设置X轴的间距
        xAxis.setLabelCount(7, true);  // 显示7个标签
        xAxis.setValueFormatter(new IndexAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int dayOffset = (int) value;
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DATE, -dayOffset);
                SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd", Locale.getDefault());
                return dateFormat.format(calendar.getTime());
            }
        });

        // 设置 Y 轴的配置
        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setAxisMinimum(0f); // 设置Y轴的最小值为0
        leftAxis.setGranularity(1f); // 设置Y轴的间距
        lineChart.getAxisRight().setEnabled(false); // 关闭右侧Y轴
    }

    private List<Entry> getFocusDataForLast7Days() {
        List<Entry> entries = new ArrayList<>();

        String todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        SharedPreferences sharedPreferences = getSharedPreferences("FocusTimes", Context.MODE_PRIVATE);

        // 确保数据有效性，避免负数或无效数据
        for (int i = 6; i >= 0; i--) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE, -i);
            String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.getTime());

            int focusCount = sharedPreferences.getInt(date, 0);  // 默认返回0，如果没有数据的话
            // 确保focusCount不是负数
            if (focusCount < 0) {
                focusCount = 0;
            }
            Log.d("FocusChart", "Date: " + date + ", Focus Count: " + focusCount);
            entries.add(new Entry(i, focusCount)); // x轴为日期，y轴为专注次数
        }

        if (entries.isEmpty()) {
            Log.e("FocusChart", "No focus data available for the last 7 days.");
        }

        return entries;
    }
}
