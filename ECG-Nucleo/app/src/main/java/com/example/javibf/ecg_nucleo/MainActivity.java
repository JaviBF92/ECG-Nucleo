package com.example.javibf.ecg_nucleo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class MainActivity extends AppCompatActivity {

    private LineChart chart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        chart = (LineChart) findViewById(R.id.chart);

        List<Entry> list = new ArrayList<>();
        list.add(new Entry(0, 1f));
        list.add(new Entry(1, 0.1f));
        list.add(new Entry(2, 0.5f));
        LineDataSet data = new LineDataSet(list, "Valores");
        data.setAxisDependency(YAxis.AxisDependency.LEFT);

        LineData linedata = new LineData(data);
        chart.setData(linedata);


        incrementa();
    }

    public void incrementa(){
        LineData data = chart.getData();
        ILineDataSet set = data.getDataSetByIndex(0);
        data.addEntry(new Entry(set.getEntryCount(), 0.6f), 0);
        data.notifyDataChanged();
        chart.notifyDataSetChanged();
        chart.moveViewToX(data.getEntryCount());
    }

}
