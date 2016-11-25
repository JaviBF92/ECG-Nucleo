package com.example.javibf.ecg_nucleo;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.concurrent.TimeUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.palaima.smoothbluetooth.Device;
import io.palaima.smoothbluetooth.SmoothBluetooth;


public class MainActivity extends AppCompatActivity {

    private LineChart chart;

    private TextView msg;

    private final Integer entryNumber = 60;
    private final String address = "00:06:71:00:2D:C0";
    private MyBroadcastReceiver mReceiver;
    private SmoothBluetooth mSmoothBluetooth;
    private StringBuffer buff;

    private long timelapse;
    private int pulse;

    private List<Float> limit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mReceiver = new MyBroadcastReceiver();
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mReceiver, filter);

        chart = (LineChart) findViewById(R.id.chart);
        msg = (TextView) findViewById(R.id.msg);

        mSmoothBluetooth = new SmoothBluetooth(this, SmoothBluetooth.ConnectionTo.OTHER_DEVICE,
                SmoothBluetooth.Connection.INSECURE, mListener);

        buff = new StringBuffer();
        timelapse = 0L;
        pulse = 0;

        limit = new ArrayList<>();
        limit.add(0.6f);

        chart.setTouchEnabled(false);
        chart.setDescription("");

        Legend legend = chart.getLegend();
        legend.setEnabled(false);

        XAxis xAxis = chart.getXAxis();
        YAxis yAxisL  = chart.getAxisLeft();
        YAxis yAxisR  = chart.getAxisRight();
        xAxis.setEnabled(false);
        xAxis.setAxisMaxValue(entryNumber);
        yAxisL.setAxisMinValue(0);
        yAxisR.setAxisMinValue(0);
        yAxisL.setAxisMaxValue(1);
        yAxisR.setAxisMaxValue(1);
        yAxisR.setDrawLabels(false);

        List<Entry> list = new ArrayList<>();
        LineDataSet data = new LineDataSet(list, "Señal");
        data.setDrawValues(false);
        data.setCircleSize(1f);

        data.setAxisDependency(YAxis.AxisDependency.LEFT);
        data.setLineWidth(2f);

        LineData linedata = new LineData(data);
        chart.setData(linedata);
    }

    protected void onStart(){
        super.onStart();
        mSmoothBluetooth.tryConnection();
    }

    protected void onResume(){
        super.onResume();
    }

    public void onDestroy() {
        super.onDestroy();
        mSmoothBluetooth.stop();
        unregisterReceiver(mReceiver);
    }

    public void processData(String value){

        LineData data = chart.getData();
        ILineDataSet set = data.getDataSetByIndex(0);

        Integer entryCount = set.getEntryCount();

        Float fValue = Float.parseFloat(value);


        Float mLimit = 0f;

        for(Float f: limit){
            mLimit += f;
        }

        mLimit = mLimit/limit.size() -0.03f;

        if(entryCount > 2) {
            float yLast1 = set.getEntryForIndex(entryCount - 2).getY();
            float yLast2 = set.getEntryForIndex(entryCount - 1).getY();
            if (yLast1 < yLast2 && yLast2 > fValue && yLast2 > mLimit) {
                pulse ++;
                limit.add(yLast2);
                if(limit.size() >= 7){
                    limit.remove(0);
                }
            }
        }
        
        data.addEntry(new Entry(entryCount, fValue), 0);

        XAxis xAxis = chart.getXAxis();

        if(entryCount >= entryNumber){
            xAxis.setAxisMinValue(entryCount - entryNumber);
            xAxis.resetAxisMaxValue();
        }

        data.notifyDataChanged();
        chart.notifyDataSetChanged();
        chart.moveViewToX(data.getEntryCount());
    }

    public void beatsPerMinute(){
        if(timelapse == 0){
            timelapse = System.nanoTime();
        }else if((int)TimeUnit.NANOSECONDS.toSeconds(System.nanoTime() - timelapse)  >= 6){
            msg.setVisibility(View.VISIBLE);
            msg.setTextColor(Color.BLUE);
            msg.setText("PPM: "+Double.toString(pulse*10.0));
            pulse = 0;
            timelapse = System.nanoTime();
        }
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
                        msg.setVisibility(View.VISIBLE);
                        msg.setTextColor(Color.RED);
                        msg.setText("Bluetooth not enabled.");
                        break;

                    case BluetoothAdapter.STATE_ON:
                        msg.setVisibility(View.GONE);
                        mSmoothBluetooth.tryConnection();
                        break;
                }
            }
        }
    }

    private SmoothBluetooth.Listener mListener = new SmoothBluetooth.Listener() {

        @Override
        public void onBluetoothNotSupported() {
            //device does not support bluetooth
            msg.setVisibility(View.VISIBLE);
            msg.setTextColor(Color.RED);
            msg.setText("Bluetooth not supported.");
        }

        @Override
        public void onBluetoothNotEnabled() {
            //bluetooth is disabled, probably call Intent request to enable bluetooth
            msg.setVisibility(View.VISIBLE);
            msg.setTextColor(Color.RED);
            msg.setText("Bluetooth not enabled.");
        }

        @Override
        public void onConnecting(Device device) {
            //called when connecting to particular device
            msg.setVisibility(View.VISIBLE);
            msg.setTextColor(Color.BLUE);
            msg.setText("Connecting...");
        }

        @Override
        public void onConnected(Device device) {
            //called when connected to particular device
            msg.setVisibility(View.GONE);
        }

        @Override
        public void onDisconnected() {
            //called when disconnected from device
            mSmoothBluetooth.tryConnection();
        }

        @Override
        public void onConnectionFailed(Device device) {
            //called when connection failed to particular device
            mSmoothBluetooth.tryConnection();
        }

        @Override
        public void onDiscoveryStarted() {
            //called when discovery is started
        }

        @Override
        public void onDiscoveryFinished() {
            //called when discovery is finished
        }

        @Override
        public void onNoDevicesFound() {
            //called when no devices found
            msg.setVisibility(View.VISIBLE);
            msg.setTextColor(Color.RED);
            msg.setText("Device not found");
        }

        @Override
        public void onDevicesFound(final List<Device> deviceList,
                                   final SmoothBluetooth.ConnectionCallback connectionCallback) {
            //receives discovered devices list and connection callback
            //you can filter devices list and connect to specific one
            //connectionCallback.connectTo(deviceList.get(position));
            for(Device d: deviceList){
                if(address.equals(d.getAddress())){
                    connectionCallback.connectTo(d);
                    break;
                }
            }

        }

        @Override
        public void onDataReceived(int data) {
            //receives all bytes
            char c = (char)data;
            buff.append(c);
            String pattern = "\\{.{4}\\}";
            Pattern p = Pattern.compile(pattern);
            Matcher match = p.matcher(buff);

            while(match.find()){
                String s = buff.substring(match.start(), match.end());
                String value = s.substring(1, s.length()-1);
                processData(value);
                buff.delete(0, match.end());
            }

            beatsPerMinute();
        }
    };

}
