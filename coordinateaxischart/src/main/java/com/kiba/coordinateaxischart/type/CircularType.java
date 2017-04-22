package com.kiba.coordinateaxischart.type;

/**
 * a,b,c的值必须按照如下公式进行传递：<br>
 * The a, b, c values' setting according to the following formula:<br>
 * y = a * sin(cx + d) + b <br>
 * y = a * cos(cx + d) + b <br>
 * y = a * tan(cx + d) + b <br>
 * y = a * cot(cx + d) + b <br>
 * @see Circular
 */
public class CircularType extends LogType {

    public Circular type;

    /**
     * a,b,c的值必须按照如下公式进行传递：<br>
     * The a, b, c values' setting according to the following formula:<br>
     * y = a * sin(cx + d) + b <br>
     * y = a * cos(cx + d) + b <br>
     * y = a * tan(cx + d) + b <br>
     * y = a * cot(cx + d) + b <br>
     * @see Circular
     * @param a a value
     * @param b b value
     * @param c c value
     * @param d d value
     * @param type {@link Circular}
     */
    public CircularType(float a, float b, float c, float d, Circular type) {
        super(a, b, c, d);
        this.type = type;
    }

    public enum Circular{
        SIN, // sin
        COS, // cos
        TAN, // tan
        COT  // cot
    }
}
