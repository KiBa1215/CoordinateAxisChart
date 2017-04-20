package com.kiba.coordinateaxischart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.kiba.coordinateaxischart.exception.FunctionTypeException;
import com.kiba.coordinateaxischart.type.CircularType;
import com.kiba.coordinateaxischart.type.ExpType;
import com.kiba.coordinateaxischart.type.FuncType;
import com.kiba.coordinateaxischart.type.LinearType;
import com.kiba.coordinateaxischart.type.LogType;
import com.kiba.coordinateaxischart.type.PowerType;
import com.orhanobut.logger.Logger;

/**
 * Created by KiBa-PC on 2017/4/18.
 */

public class CoordinateAxisChart extends View {

    private float width; // view width
    private float height; // view height

    private Paint axisPaint;
    private Paint functionLinePaint;

    private int axisColor = Color.BLACK; // axis color
    private int lineColor = Color.RED;

    private int AXIS_WIDTH = 0;
    private int FUNCTION_LINE_WIDTH = 0;
    private int POINT_RADIUS = 5;

    private int SEGMENT_SIZE = 30; // by default, 30 points will be taken
    private int dx = 2;

    private int COORDINATE_TEXT_SIZE = 0; // the text size of text beside axis

    private int max = 5; // the max of the axis value
    private float unitLength; // the length between two neighbour points of axises

    // all points are points with raw coordinates
    private PointF origin;
    private PointF leftPoint;
    private PointF rightPoint;
    private PointF topPoint;
    private PointF bottomPoint;

    private Float a;
    private Float b;
    private Float c;

    private LinearType type;

    private PointF[] xPointsValues; // logic points, not raw points

    public CoordinateAxisChart(Context context){
        super(context);
        init(context);
    }

    public CoordinateAxisChart(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CoordinateAxisChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {

        // define the values of widths
        AXIS_WIDTH = Utils.dip2px(context, 1f);
        FUNCTION_LINE_WIDTH = Utils.dip2px(context, 1.5f);
        // coordinate text size
        COORDINATE_TEXT_SIZE = Utils.sp2px(context, 8f);

        axisPaint = new Paint();
        axisPaint.setStrokeWidth(AXIS_WIDTH);
        axisPaint.setColor(axisColor);
        axisPaint.setAntiAlias(true);
        axisPaint.setStyle(Paint.Style.STROKE);
        axisPaint.setTextSize(COORDINATE_TEXT_SIZE);

        functionLinePaint = new Paint();
        functionLinePaint.setStrokeWidth(FUNCTION_LINE_WIDTH);
        functionLinePaint.setColor(lineColor);
        functionLinePaint.setAntiAlias(true);
        functionLinePaint.setDither(true);
        functionLinePaint.setStyle(Paint.Style.STROKE);

        // prepare an array to cache the split points
        xPointsValues = new PointF[SEGMENT_SIZE];
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        width = getMeasuredWidth();
        height = getMeasuredHeight();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(origin == null){
            origin = new PointF();
            origin.set(width / 2f, height / 2f);

            leftPoint = new PointF();
            leftPoint.set(0, height / 2f);

            rightPoint = new PointF();
            rightPoint.set(width, height / 2f);

            topPoint = new PointF();
            topPoint.set(width / 2f, 0);

            bottomPoint = new PointF();
            bottomPoint.set(width / 2f, height);
        }

        drawAxis(canvas);

        drawFuncLine(canvas);

    }

    private void drawAxis(Canvas canvas) {
        // draw x axis
        canvas.drawLine(leftPoint.x, leftPoint.y, rightPoint.x, rightPoint.y, axisPaint);
        canvas.drawLine(topPoint.x, topPoint.y, bottomPoint.x, bottomPoint.y, axisPaint);
        // draw axis arrows
        // y axis arrow
        Path path = new Path();
        path.moveTo(topPoint.x, topPoint.y);
        path.lineTo(topPoint.x - 10, topPoint.y + 20);
        path.lineTo(topPoint.x + 10, topPoint.y + 20);
        path.close();
        axisPaint.setStyle(Paint.Style.FILL);
        canvas.drawPath(path, axisPaint);
        // x axis arrow
        path.moveTo(rightPoint.x, rightPoint.y);
        path.lineTo(rightPoint.x - 20, rightPoint.y - 10);
        path.lineTo(rightPoint.x - 20, rightPoint.y + 10);
        path.close();
        canvas.drawPath(path, axisPaint);

        // draw coordinate points
        unitLength = width > height? height / 2 / (max + 1) : width / 2 / (max + 1); // +1 is for not to overlap with arrows
        int xMax = (int) (width > height? width / unitLength : height / unitLength);
        int yMax;
        if(xMax >= max){
            yMax = max;
        }else{
            yMax = xMax;
            xMax = max;
        }

        // x- coordinate points
        for (int i = 0; i < xMax; i++) {
            float x = origin.x - unitLength * (i + 1);
            float y = origin.y;
            if(x > leftPoint.x){
                path.moveTo(x, y);
                path.close();
                canvas.drawCircle(x, y, POINT_RADIUS, axisPaint);
                String coorText = String.valueOf(-(i + 1));
                canvas.drawText(coorText, x, y + COORDINATE_TEXT_SIZE, axisPaint);
            }
        }
        // x+ coordinate points
        for (int i = 0; i < xMax; i++) {
            float x = origin.x + unitLength * (i + 1);
            float y = origin.y;
            if(x < rightPoint.x){
                path.moveTo(x, y);
                path.close();
                canvas.drawCircle(x, y, POINT_RADIUS, axisPaint);
                String coorText = String.valueOf(i + 1);
                canvas.drawText(coorText, x, y + COORDINATE_TEXT_SIZE, axisPaint);
            }
        }
        // y+ coordinate points
        for (int i = 0; i < yMax; i++) {
            float x = origin.x;
            float y = origin.y - unitLength * (i + 1);
            if(y > topPoint.y){
                path.moveTo(x, y);
                path.close();
                canvas.drawCircle(x, y, POINT_RADIUS, axisPaint);
                String coorText = String.valueOf(i + 1);
                canvas.drawText(coorText, x - COORDINATE_TEXT_SIZE, y, axisPaint);
            }
        }
        // y- coordinate points
        for (int i = 0; i < yMax; i++) {
            float x = origin.x;
            float y = origin.y + unitLength * (i + 1);
            if(y < bottomPoint.y){
                path.moveTo(x, y);
                path.close();
                canvas.drawCircle(x, y, POINT_RADIUS, axisPaint);
                String coorText = String.valueOf(-(i + 1));
                canvas.drawText(coorText, x - COORDINATE_TEXT_SIZE * 1.2f, y, axisPaint);
            }
        }

        axisPaint.setStyle(Paint.Style.STROKE);
    }

    private void drawFuncLine(Canvas canvas) {
        if (type != null) {
            switch (type.getClass().getSimpleName()) {
                case "LinearType":
                    generateLinearLines(a, b, canvas);
                    break;
                case "PowerType":
                    if(c == 1){
                        generateLinearLines(a, b, canvas);
                    }else{
                        generatePowerLines(a, b, c, canvas);
                    }
                    break;
                case "ExpType":
                    if(c == 1){
                        generateLinearLines(0f, a + b, canvas);
                    }else{
                        generateExpLines(a, b, c, canvas);
                    }
                    break;
                case "LogType":
                    break;
                case "CircularType":
                    break;
            }
        }
    }

    /**
     * generate the linear function lines
     * @param a {@link LinearType#LinearType(float, float)}
     * @param b {@link LinearType#LinearType(float, float)}
     * @param canvas canvas
     */
    private void generateLinearLines(Float a, Float b, Canvas canvas) {
        // raw
        PointF start = leftPoint;
        PointF end = rightPoint;
        // logical
        PointF startLogic = convertRawPoint2Logical(start, unitLength);
        PointF endLogic = convertRawPoint2Logical(end, unitLength);
        // calculate
        startLogic.y = FuncUtils.getLinearYValue(a, b, startLogic.x);
        endLogic.y = FuncUtils.getLinearYValue(a, b, endLogic.x);
        // convert logical to raw
        PointF startRaw = convertLogicalPoint2Raw(startLogic, unitLength);
        PointF endRaw = convertLogicalPoint2Raw(endLogic, unitLength);
        // draw lines
        canvas.drawLine(startRaw.x, startRaw.y, endRaw.x, endRaw.y, functionLinePaint);
    }

    /**
     * generate the power function lines
     * @param a {@link PowerType#PowerType(float, float, float)}
     * @param b {@link PowerType#PowerType(float, float, float)}
     * @param canvas canvas
     */
    private void generatePowerLines(Float a, Float b, Float c, Canvas canvas){
        // raw
        PointF start = leftPoint;
        PointF end = rightPoint;

        float unit = (end.x - start.x) / xPointsValues.length;

        for (int i = 0; i < xPointsValues.length; i++) {
            // get the split point
            PointF split = new PointF(start.x + i * unit, start.y);
            // logical
            PointF splitLogic = convertRawPoint2Logical(split, unitLength);
            // calculate
            splitLogic.y = FuncUtils.getPowYValue(a, b, c, splitLogic.x);
            // convert logical to raw
            PointF splitRaw = convertLogicalPoint2Raw(splitLogic, unitLength);
            xPointsValues[i] = splitRaw;
        }

        drawBezier(canvas, FuncType.POWER_TYPE);

    }

    /**
     * generate the exp function lines
     * @param a {@link ExpType#ExpType(float, float, float)}
     * @param b {@link ExpType#ExpType(float, float, float)}
     * @param canvas canvas
     */
    private void generateExpLines(Float a, Float b, Float c, Canvas canvas) {
        // raw
        PointF start = leftPoint;
        PointF end = rightPoint;

        float unit = (end.x - start.x) / xPointsValues.length;

        for (int i = 0; i < xPointsValues.length; i++) {
            // get the split point
            PointF split = new PointF(start.x + i * unit, start.y);
            // logical
            PointF splitLogic = convertRawPoint2Logical(split, unitLength);
            // calculate
            splitLogic.y = FuncUtils.getExpYValue(a, b, c, splitLogic.x);
            // convert logical to raw
            PointF splitRaw = convertLogicalPoint2Raw(splitLogic, unitLength);
            xPointsValues[i] = splitRaw;
        }

        drawBezier(canvas, FuncType.EXP_TYPE);
    }

    private void drawBezier(Canvas canvas, FuncType type) {
        if(xPointsValues != null && xPointsValues.length > 0){
            Path path = new Path();
            for (int i = 0; i < xPointsValues.length; i++) {
                // if out of screen, do not render it
                if((xPointsValues[i].y <= height && xPointsValues[i].y >= 0) ||
                        ( i < xPointsValues.length - 1 && xPointsValues[i + 1].y <= height && xPointsValues[i + 1].y >= 0) ||
                        ( i > 0 && xPointsValues[i - 1].y <= height && xPointsValues[i - 1].y >= 0) ){

                    path.moveTo(xPointsValues[i].x, xPointsValues[i].y);

                    // get a point on the line which super near the current point
                    float ad_x1 = xPointsValues[i].x + dx;
                    PointF dpLogic1 = convertRawPoint2Logical(ad_x1, origin.y, unitLength, origin);
                    PointF dp1 = FuncUtils.getPointByType(a, b, c, dpLogic1.x, type);
                    // get a line near xPointsValues[i]
                    float[] tangentLineFuncCoefficients1 = FuncUtils.computeLinearFuncsByPoints(
                                    convertRawPoint2Logical(xPointsValues[i], unitLength)
                                    , dp1);
                    if(tangentLineFuncCoefficients1 == null){
                        Logger.w("tangentLineFuncCoefficients1 == null");
                        return;
                    }

                    // get a point on the line which super near the current point
                    float ad_x2 = xPointsValues[i + 1].x - dx;
                    PointF dpLogic2 = convertRawPoint2Logical(ad_x2, origin.y, unitLength, origin);
                    PointF dp2 = FuncUtils.getPointByType(a, b, c, dpLogic2.x, type);
                    // get a line near xPointsValues[i + 1]
                    float[] tangentLineFuncCoefficients2 = FuncUtils.computeLinearFuncsByPoints(
                                    convertRawPoint2Logical(xPointsValues[i + 1], unitLength)
                                    , dp2);
                    if(tangentLineFuncCoefficients2 == null){
                        Logger.w("tangentLineFuncCoefficients2 == null");
                        return;
                    }

                    // compute the intersection point as the control point of bezier curve
                    PointF controlPointLogic =  FuncUtils.intersectionBetweenLinearFuncs(
                            tangentLineFuncCoefficients1[0],
                            tangentLineFuncCoefficients1[1],
                            tangentLineFuncCoefficients2[0],
                            tangentLineFuncCoefficients2[1]
                    );
                    if(controlPointLogic == null){
                        Logger.w("controlPointLogic == null");
                        return;
                    }
                    PointF controlPointRaw = convertLogicalPoint2Raw(controlPointLogic, unitLength);
                    path.quadTo(controlPointRaw.x, controlPointRaw.y, xPointsValues[i + 1].x, xPointsValues[i + 1].y);
                    canvas.drawPath(path, functionLinePaint);
                }
            }
        }
    }

    public <T extends LinearType> void setFunctionType(T type) throws FunctionTypeException {
        if(type != null){
            switch (type.getClass().getSimpleName()){
                case "LinearType":
                    a = type.a;
                    b = type.b;
                    c = null;
                    this.type = type;
                    break;
                case "PowerType":
                    PowerType powerType = (PowerType) type;
                    a = powerType.a;
                    b = powerType.b;
                    c = powerType.c;
                    this.type = powerType;
                    break;
                case "ExpType":
                    ExpType expType = (ExpType) type;
                    a = expType.a;
                    b = expType.b;
                    c = expType.c;
                    this.type = expType;
                    break;
                case "LogType":
                    LogType logType = (LogType) type;
                    a = logType.a;
                    b = logType.b;
                    c = logType.c;
                    this.type = logType;
                    break;
                case "CircularType":
                    CircularType circularType = (CircularType) type;
                    a = circularType.a;
                    b = circularType.b;
                    c = circularType.c;
                    this.type = circularType;
                    break;
                default:
                    throw new FunctionTypeException("Function type error. ");
            }
            invalidate();
        }
    }

    public void reset(){
        a = null;
        b = null;
        c = null;
    }

    private PointF convertLogicalPoint2Raw(PointF logical, float unitLength){
        return convertLogicalPoint2Raw(logical.x, logical.y, unitLength, origin);
    }

    private PointF convertLogicalPoint2Raw(float x, float y, float unitLength, PointF origin){
        float rawX = origin.x + x * unitLength;
        float rawY = origin.y - y * unitLength;
        return new PointF(rawX, rawY);
    }

    private PointF convertRawPoint2Logical(PointF raw, float unitLength){
        return convertRawPoint2Logical(raw.x, raw.y, unitLength, origin);
    }

    private PointF convertRawPoint2Logical(float x, float y, float unitLength, PointF origin){
        float logicalX = (x - origin.x) / unitLength;
        float logicalY = (origin.y - y) / unitLength;
        return new PointF(logicalX, logicalY);
    }

}
