package com.kiba.coordinateaxischart;

import android.graphics.PointF;

/**
 * Created by KiBa on 2017/4/22.
 */

public class SinglePoint {

    private PointF point;

    private Integer pointRadius;

    private Integer pointColor;

    public SinglePoint(PointF point) {
        this.point = point;
    }

    public PointF getPoint() {
        return point;
    }

    public void setPoint(PointF point) {
        this.point = point;
    }

    public Integer getPointRadius() {
        return pointRadius;
    }

    public void setPointRadius(Integer pointRadius) {
        this.pointRadius = pointRadius;
    }

    public Integer getPointColor() {
        return pointColor;
    }

    public void setPointColor(Integer pointColor) {
        this.pointColor = pointColor;
    }
}
