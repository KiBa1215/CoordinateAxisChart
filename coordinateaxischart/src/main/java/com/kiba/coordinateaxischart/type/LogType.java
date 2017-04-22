package com.kiba.coordinateaxischart.type;

/**
 * a,b,c的值必须按照如下公式进行传递：<br>
 * The a, b, c values' setting according to the following formula:<br>
 * y = a * log(c * x + d) + b
 */
public class LogType extends PowerType {

    public float d;

    /**
     * a,b,c的值必须按照如下公式进行传递：<br>
     * The a, b, c values' setting according to the following formula:<br>
     * y = a * log(c * x + d) + b
     * @param a a value
     * @param b b value
     * @param c c value
     * @param d d value
     */
    public LogType(float a, float b, float c, float d) {
        super(a, b, c);
        this.d = d;
    }
}
