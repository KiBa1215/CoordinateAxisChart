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
        try {
//            coordinateAxisChart.setFunctionType(new LinearType(2, 1));
            coordinateAxisChart.setFunctionType(new PowerType(1, 1, 3));
//            coordinateAxisChart.setFunctionType(new LogType(1, 0, 1, 0));
//            coordinateAxisChart.setFunctionType(new ExpType(1, 0, 2f));
//            coordinateAxisChart.setFunctionType(new CircularType(1, 0, 1, 0, CircularType.Circular.SIN));
//            coordinateAxisChart.setFunctionType(new CircularType(1, 0, 1f, 0, CircularType.Circular.TAN));

        } catch (FunctionTypeException e) {
            e.printStackTrace();
        }

    }
}
