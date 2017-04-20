package com.kiba.sample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.kiba.coordinateaxischart.CoordinateAxisChart;
import com.kiba.coordinateaxischart.exception.FunctionTypeException;
import com.kiba.coordinateaxischart.type.CircularType;
import com.kiba.coordinateaxischart.type.ExpType;
import com.kiba.coordinateaxischart.type.LinearType;
import com.kiba.coordinateaxischart.type.LogType;
import com.kiba.coordinateaxischart.type.PowerType;

public class MainActivity extends AppCompatActivity {

    CoordinateAxisChart coordinateAxisChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        coordinateAxisChart = (CoordinateAxisChart) findViewById(R.id.coordinateAxisChart);

        coordinateAxisChart.post(new Runnable() {
            @Override
            public void run() {
                try {
//                    coordinateAxisChart.setFunctionType(new CircularType(1, 0, 1, 0, CircularType.Circular.SIN));
                    coordinateAxisChart.setFunctionType(new LogType(1, 0, 1, 0));

                } catch (FunctionTypeException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
