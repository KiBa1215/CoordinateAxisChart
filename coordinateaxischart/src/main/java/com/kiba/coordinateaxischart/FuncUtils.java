package com.kiba.coordinateaxischart;

import android.graphics.PointF;

import com.kiba.coordinateaxischart.type.FuncType;

/**
 * Created by KiBa-PC on 2017/4/20.
 */

public class FuncUtils {

    public static PointF intersectionBetweenLinearFuncs(float a1, float b1, float a2, float b2){
        if(a1 != a2){
            float x = (b2 - b1) / (a1- a2);
            float y = a1 * x + b1;
            return new PointF(x, y);
        }
        return null;
    }

    public static float[] computeLinearFuncsByPoints(PointF p1, PointF p2){
        if(!p1.equals(p2)){
            float a = (p1.y - p2.y) / (p1.x - p2.x);
            float b = p1.y - a * p1.x;
            return new float[]{a, b};
        }
        return null;
    }

    public static float getLinearYValue(float a, float b, float x){
        return a * x + b;
    }

    public static float getPowYValue(float a, float b, float c, float x){
        return (float) (a * Math.pow(x, c) + b);
    }

    public static float getExpYValue(float a, float b, float c, float x){
        return (float) (a * Math.pow(c, x) + b);
    }

    public static PointF getPointByType(float a, float b, float c, float x, FuncType type){
        switch (type){
            case LINEAR_TYPE:
                return new PointF(x, getLinearYValue(a, b, x));
            case POWER_TYPE:
                return new PointF(x, getPowYValue(a, b, c, x));
            case EXP_TYPE:
                return new PointF(x, getExpYValue(a, b, c, x));
            case LOG_TYPE:
            case CIRCULAR_TYPE:
        }
        return null;
    }
}
