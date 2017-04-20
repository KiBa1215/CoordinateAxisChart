package com.kiba.coordinateaxischart.type;

/**
 * a,b,c的值必须按照如下公式进行传递：<br/>
 * The a, b, c values' setting according to the following formula:<br/>
 * y = a * log(c * x) + b
 */
public class LogType extends PowerType {

    /**
     * a,b,c的值必须按照如下公式进行传递：<br/>
     * The a, b, c values' setting according to the following formula:<br/>
     * y = a * log(c * x) + b
     * @param a a value
     * @param b b value
     * @param c c value
     */
    public LogType(float a, float b, float c) {
        super(a, b, c);
    }
}
