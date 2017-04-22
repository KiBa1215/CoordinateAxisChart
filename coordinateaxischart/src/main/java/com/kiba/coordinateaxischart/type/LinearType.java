package com.kiba.coordinateaxischart.type;
/**
 * a,b的值必须按照如下公式进行传递：<br>
 * The a, b values' setting according to the following formula:<br>
 * y = a * x + b
 */
public class LinearType {

    /**
     * a,b的值必须按照如下公式进行传递：<br>
     * The a, b values' setting according to the following formula:<br>
     * y = a * x + b
     * @param a a value
     * @param b b value
     */
    public LinearType(float a, float b) {
        this.a = a;
        this.b = b;
    }

    public float a;
    public float b;

}
