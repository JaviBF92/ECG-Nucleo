package com.example.javibf.ecg_nucleo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    LineChart chart;
    LineDataSet data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        chart = (LineChart) findViewById(R.id.chart);
        List<Entry> list = new ArrayList<>();
        list.add(new Entry(10.0f, 0));
        list.add(new Entry(15.0f, 1));
        data = new LineDataSet(list, "Valores");
        data.setAxisDependency(YAxis.AxisDependency.LEFT);
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(data);
        List<String> etiq = new ArrayList<>();
        etiq.add("1");
        etiq.add("2");
        etiq.add("3");
        LineData linedata = new LineData(etiq, dataSets);
        chart.setData(linedata);
        chart.invalidate();
    }

    void incrementa(){

    }
}
