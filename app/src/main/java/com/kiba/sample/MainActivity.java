package com.kiba.sample;

import android.graphics.Color;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.kiba.coordinateaxischart.ChartConfig;
import com.kiba.coordinateaxischart.CoordinateAxisChart;
import com.kiba.coordinateaxischart.FunctionLine;
import com.kiba.coordinateaxischart.SinglePoint;
import com.kiba.coordinateaxischart.type.CircularType;
import com.kiba.coordinateaxischart.type.ExpType;
import com.kiba.coordinateaxischart.type.LinearType;
import com.kiba.coordinateaxischart.type.LogType;
import com.kiba.coordinateaxischart.type.PowerType;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    CoordinateAxisChart coordinateAxisChart;
    Button linearBtn, powerBtn, logBtn, sinBtn, expBtn, resetBtn, pointBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        coordinateAxisChart = (CoordinateAxisChart) findViewById(R.id.coordinateAxisChart);
        linearBtn = (Button) findViewById(R.id.linear);
        powerBtn = (Button) findViewById(R.id.power);
        logBtn = (Button) findViewById(R.id.log);
        sinBtn = (Button) findViewById(R.id.sin);
        expBtn = (Button) findViewById(R.id.exp);
        resetBtn = (Button) findViewById(R.id.reset);
        pointBtn = (Button) findViewById(R.id.point);

        ChartConfig config = new ChartConfig();
        config.setMax(12);
        config.setPrecision(1);
        config.setSegmentSize(50);
        coordinateAxisChart.setConfig(config);

        linearBtn.setOnClickListener(this);
        powerBtn.setOnClickListener(this);
        logBtn.setOnClickListener(this);
        sinBtn.setOnClickListener(this);
        expBtn.setOnClickListener(this);
        resetBtn.setOnClickListener(this);
        pointBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.linear:
                FunctionLine<LinearType> line1 = new FunctionLine<>(new LinearType(2, 1), Color.parseColor("#43A047"));
                coordinateAxisChart.addFunctionLine(line1);
                break;
            case R.id.power:
                FunctionLine<PowerType> line2 = new FunctionLine<>(new PowerType(1, 0, 2), Color.parseColor("#e53935"));
                coordinateAxisChart.addFunctionLine(line2);
                break;
            case R.id.log:
                FunctionLine<LogType> line3 = new FunctionLine<>(new LogType(1, 0, 1, 0), Color.parseColor("#757575"));
                coordinateAxisChart.addFunctionLine(line3);
                break;
            case R.id.sin:
                FunctionLine<CircularType> line4 = new FunctionLine<>(new CircularType(1, 0, 1, 0, CircularType.Circular.SIN), Color.parseColor("#FFCA28"));
                coordinateAxisChart.addFunctionLine(line4);
                break;
            case R.id.exp:
                FunctionLine<ExpType> line5 = new FunctionLine<>(new ExpType(1, 0, 2), Color.parseColor("#00B0FF"));
                coordinateAxisChart.addFunctionLine(line5);
                break;
            case R.id.point:
                SinglePoint point = new SinglePoint(new PointF(1f, 2f));
                point.setPointColor(Color.RED);
                coordinateAxisChart.addPoint(point);
                break;
            case R.id.reset:
                coordinateAxisChart.reset();
                return;
        }
        coordinateAxisChart.invalidate();
    }
}
