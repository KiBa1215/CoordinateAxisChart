package com.kiba.coordinateaxischart;

/**
 * Created by KiBa on 2017/4/22.
 */

public class ChartConfig {

    private Integer axisWidth;

    private Integer max;

    private Integer precision;

    private Integer segmentSize;

    private Integer axisColor;

    private Integer axisPointRadius;

    public Integer getAxisWidth() {
        return axisWidth;
    }

    public void setAxisWidth(Integer axisWidth) {
        this.axisWidth = axisWidth;
    }

    public Integer getMax() {
        return max;
    }

    /**
     * The max value that the axises have.
     * @param max axis max value
     */
    public void setMax(Integer max) {
        this.max = max;
    }


    public Integer getPrecision() {
        return precision;
    }

    /**
     * 函数曲线的精度，这个精度用于计算两点间切线的交点。推荐值：1-10<br>
     * The precision of the function curve, it's used to calculate the intersection point of two points' tangent lines.
     * value recommended: 1-10
     * @param precision precision of the function curve
     */
    public void setPrecision(Integer precision) {
        this.precision = precision;
    }

    public Integer getSegmentSize() {
        return segmentSize;
    }

    /**
     * 将x轴分割成segmentSize个点，成像时会将这些点连接起来。<br>
     * 注：size并不是越大越好，根据不同函数可做不同的调整（推荐值在30-100之间），尤其是tan和cot函数（目前尚未做优化）。<br>
     * The x axis will be equally separated to some segment points according to segmentSize,
     * and will connect these points when drawing the function.<br>
     * <b>ATTENTION</b>: size is not the bigger the better,
     * you have to adjust the size by different function types(30-100 is recommended),
     * especially <b>tan() and cot()</b> function (not optimized yet) need adjustment.
     * @param segmentSize segment size
     */
    public void setSegmentSize(Integer segmentSize) {
        this.segmentSize = segmentSize;
    }

    public Integer getAxisColor() {
        return axisColor;
    }

    public void setAxisColor(Integer axisColor) {
        this.axisColor = axisColor;
    }

    public Integer getAxisPointRadius() {
        return axisPointRadius;
    }

    public void setAxisPointRadius(Integer axisPointRadius) {
        this.axisPointRadius = axisPointRadius;
    }
}
