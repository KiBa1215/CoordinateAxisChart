package com.kiba.coordinateaxischart.type;

/**
 * a,b,c的值必须按照如下公式进行传递：<br/>
 * The a, b, c values' setting according to the following formula:<br/>
 * y = a * pow(x, c) + b
 */
public class PowerType extends LinearType {

    public float c;

    /**
     * a,b,c的值必须按照如下公式进行传递：<br/>
     * The a, b, c values' setting according to the following formula:<br/>
     * y = a * pow(x, c) + b
     * @param a a value
     * @param b b value
     * @param c c value
     */
    public PowerType(float a, float b, float c) {
        super(a, b);
        this.c = c;
    }
}
