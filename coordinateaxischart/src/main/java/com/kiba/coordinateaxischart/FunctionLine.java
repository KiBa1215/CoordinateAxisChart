package com.kiba.coordinateaxischart;

import com.kiba.coordinateaxischart.type.LinearType;

/**
 * Created by KiBa-PC on 2017/4/21.
 */

public class FunctionLine<T extends LinearType> {

    private T functionType;

    private Integer lineColor;

    private Integer lineWidth;

    public FunctionLine(T functionType) {
        this.functionType = functionType;
    }

    public FunctionLine(T functionType, int lineColor) {
        this.functionType = functionType;
        this.lineColor = lineColor;
    }

    public FunctionLine(T functionType, int lineColor, int lineWidth) {
        this.functionType = functionType;
        this.lineColor = lineColor;
        this.lineWidth = lineWidth;
    }

    public T getFunctionType() {
        return functionType;
    }

    public void setFunctionType(T functionType) {
        this.functionType = functionType;
    }

    public Integer getLineColor() {
        return lineColor;
    }

    public void setLineColor(int lineColor) {
        this.lineColor = lineColor;
    }

    public Integer getLineWidth() {
        return lineWidth;
    }

    public void setLineWidth(int lineWidth) {
        this.lineWidth = lineWidth;
    }
}
