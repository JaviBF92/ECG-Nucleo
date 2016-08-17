package com.example.javibf.ecg_nucleo;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.List;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;

/**
 * Created by javibf on 12/08/16.
 */
public class MainActivity extends AppCompatActivity {

    private LineChart chart;
    private BluetoothSPP bt;
    MyBroadcastReceiver mReceiver = new MyBroadcastReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mReceiver, filter);

        chart = (LineChart) findViewById(R.id.chart);
        chart.setTouchEnabled(false);
        XAxis xAxis = chart.getXAxis();
        xAxis.setEnabled(false);

        bt = new BluetoothSPP(getApplicationContext());

        List<Entry> list = new ArrayList<>();
        list.add(new Entry(0, 1f));
        list.add(new Entry(1, 0.1f));
        list.add(new Entry(2, 0.5f));
        LineDataSet data = new LineDataSet(list, "Valores");
        data.setAxisDependency(YAxis.AxisDependency.LEFT);

        LineData linedata = new LineData(data);
        chart.setData(linedata);

    }

    protected void onStart(){
        super.onStart();

    }

    protected void onResume(){
        super.onResume();
    }

    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    public void incrementa(){
        LineData data = chart.getData();
        ILineDataSet set = data.getDataSetByIndex(0);
        data.addEntry(new Entry(set.getEntryCount(), 0.6f), 0);
        data.notifyDataChanged();
        chart.notifyDataSetChanged();
        chart.moveViewToX(data.getEntryCount());
    }

    private class MyBroadcastReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        TextView msg = (TextView) findViewById(R.id.msg);
                        msg.setVisibility(View.VISIBLE);
                        msg.setTextColor(0xffff0000);
                        msg.setText("Bluetooth not enabled.");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        TextView tv = (TextView) findViewById(R.id.msg);
                        tv.setVisibility(View.GONE);
                        break;
                }
            }
        }
    }

}
