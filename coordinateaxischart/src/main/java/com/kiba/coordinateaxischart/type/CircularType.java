package com.kiba.coordinateaxischart.type;

/**
 * a,b,c的值必须按照如下公式进行传递：<br/>
 * The a, b, c values' setting according to the following formula:<br/>
 * y = a * sin(x, c) + b
 * @see Circular
 */
public class CircularType extends PowerType {

    public Circular type;

    /**
     * a,b,c的值必须按照如下公式进行传递：<br/>
     * The a, b, c values' setting according to the following formula:<br/>
     * y = a * sin(x, c) + b
     * @see Circular
     * @param a a value
     * @param b b value
     * @param c c value
     * @param type {@link Circular}
     */
    public CircularType(float a, float b, float c, Circular type) {
        super(a, b, c);
        this.type = type;
    }

    enum Circular{
        SIN, // sin
        COS, // cos
        TAN, // tan
        COT  // cot
    }
}
