package com.kiba.coordinateaxischart;

import android.graphics.PointF;

import com.kiba.coordinateaxischart.exception.FunctionNotValidException;
import com.kiba.coordinateaxischart.exception.FunctionTypeException;
import com.kiba.coordinateaxischart.type.CircularType.Circular;
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

    public static float getLogYValue(float a, float b, float c, float d, float x) throws FunctionNotValidException {
        if(c * x + d <= 0){
            throw new FunctionNotValidException("The value inside log() cannot be 0 or negative.");
        }
        return (float) (a * Math.log(c * x + d) + b);
    }

    public static Float getCircularYValue(float a, float b, float c, float d, float x, Circular type) throws FunctionTypeException {
        switch (type){
            case SIN:
                return (float) (a * Math.sin(c * x + d) + b);
            case COS:
                return (float) (a * Math.cos(c * x + d) + b);
            case TAN:
                return (float) (a * Math.tan(c * x + d) + b);
            case COT:
                float tan = (float) (a * Math.tan(c * x + d) + b);
                if(tan != 0){
                    return 1 / (float) (a * Math.tan(c * x + d) + b);
                }else{
                    throw new FunctionTypeException("cot(kπ) {n∈Z} is not valid.");
                }
            default:
                throw new FunctionTypeException("No 'Circular Type' found.");
        }
    }

    public static PointF getPointByType(Float a, Float b, Float c, Float d, Float x, FuncType type, Circular circular){
        switch (type){
            case LINEAR_TYPE:
                return new PointF(x, getLinearYValue(a, b, x));
            case POWER_TYPE:
                return new PointF(x, getPowYValue(a, b, c, x));
            case EXP_TYPE:
                return new PointF(x, getExpYValue(a, b, c, x));
            case LOG_TYPE:
                try {
                    return new PointF(x, getLogYValue(a, b, c, d, x));
                } catch (FunctionNotValidException e) {
                    e.printStackTrace();
                }
                break;
            case CIRCULAR_TYPE:
                try {
                    return new PointF(x, getCircularYValue(a, b, c, d, x, circular));
                } catch (FunctionTypeException e) {
                    e.printStackTrace();
                }
                break;
        }
        return null;
    }
}
