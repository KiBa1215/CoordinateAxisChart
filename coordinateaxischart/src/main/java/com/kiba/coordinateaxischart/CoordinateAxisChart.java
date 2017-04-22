package com.kiba.coordinateaxischart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.kiba.coordinateaxischart.exception.FunctionNotValidException;
import com.kiba.coordinateaxischart.exception.FunctionTypeException;
import com.kiba.coordinateaxischart.type.CircularType;
import com.kiba.coordinateaxischart.type.ExpType;
import com.kiba.coordinateaxischart.type.FuncType;
import com.kiba.coordinateaxischart.type.LinearType;
import com.kiba.coordinateaxischart.type.LogType;
import com.kiba.coordinateaxischart.type.PowerType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by KiBa-PC on 2017/4/18.
 */

public class CoordinateAxisChart extends View {

    private final int DEFAULT_AXIS_WIDTH = 2;
    private final int DEFAULT_FUNCTION_LINE_WIDTH = 3;
    private final int DEFAULT_COORDINATE_TEXT_SIZE = 16;
    private final int DEFAULT_SEGMENT_SIZE = 50;
    private final int DEFAULT_PRECISION = 1;
    private final int DEFAULT_AXIS_POINT_RADIUS = 5;
    private final int DEFAULT_AXIS_COLOR = Color.BLACK;
    private final int DEFAULT_MAX = 5;
    private final int DEFAULT_SINGLE_POINT_RADIUS = 8;
    private final int DEFAULT_SINGLE_POINT_COLOR = DEFAULT_AXIS_COLOR;

    private static final String TAG = "CoordinateAxisChart";

    private static final float PI = (float) Math.PI;

    private float width; // view width
    private float height; // view height

    private Paint axisPaint;
    private Paint functionLinePaint;
    private Paint pointPaint;

    private int lineColor           = Color.RED;
    private int axisColor           = DEFAULT_AXIS_COLOR; // axis color
    private int axisWidth           = DEFAULT_AXIS_WIDTH;
    private int functionLineWidth   = DEFAULT_FUNCTION_LINE_WIDTH;
    private int axisPointRadius     = DEFAULT_AXIS_POINT_RADIUS;
    private int segmentSize         = DEFAULT_SEGMENT_SIZE; // by default, 50 points will be taken
    private int dx                  = DEFAULT_PRECISION;
    private int coordinateTextSize  = DEFAULT_COORDINATE_TEXT_SIZE; // the text size of text beside axis
    private int max                 = DEFAULT_MAX; // the max of the axis value

    private float unitLength; // the length between two neighbour points of axises
    private int xMax = 0;
    private int yMax = 0;

    // all points are points with raw coordinates
    private PointF origin;
    private PointF leftPoint;
    private PointF rightPoint;
    private PointF topPoint;
    private PointF bottomPoint;

    private Float a;
    private Float b;
    private Float c;
    private Float d;
    private CircularType.Circular circular;

    private LinearType type;

    private PointF[] xPointsValues; // logic points, not raw points

    private ChartConfig config;

    private List<FunctionLine> lines = new ArrayList<>();
    private List<SinglePoint> points = new ArrayList<>();

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

        setConfig(new ChartConfig(), false);

        axisPaint = new Paint();
        axisPaint.setStrokeWidth(axisWidth);
        axisPaint.setColor(axisColor);
        axisPaint.setAntiAlias(true);
        axisPaint.setStyle(Paint.Style.STROKE);
        axisPaint.setTextSize(coordinateTextSize);

        functionLinePaint = new Paint();
        functionLinePaint.setStrokeWidth(functionLineWidth);
        functionLinePaint.setColor(lineColor);
        functionLinePaint.setAntiAlias(true);
        functionLinePaint.setDither(true);
        functionLinePaint.setStyle(Paint.Style.STROKE);

        pointPaint = new Paint();
        pointPaint.setColor(DEFAULT_SINGLE_POINT_COLOR);
        pointPaint.setStyle(Paint.Style.FILL);
        pointPaint.setAntiAlias(true);

        // prepare an array to cache the split points
        xPointsValues = new PointF[segmentSize];
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

        if(config == null){
            return;
        }

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

        // draw points
        for (int i = 0; i < points.size(); i++) {
            SinglePoint point = points.get(i);
            drawPoint(point, canvas);
        }

        // draw function lines
        for (int i = 0; i < lines.size(); i++) {
            FunctionLine line = lines.get(i);
            this.type = line.getFunctionType();
            if(line.getLineColor() != null){
                this.functionLinePaint.setColor(line.getLineColor());
            }else{
                this.functionLinePaint.setColor(lineColor);
            }
            if(line.getLineWidth() != null){
                this.functionLinePaint.setStrokeWidth(line.getLineWidth());
            }else{
                this.functionLinePaint.setStrokeWidth(functionLineWidth);
            }
            try {
                resetStatus();
                setFunctionType(line.getFunctionType());
                drawFuncLine(canvas);
            } catch (FunctionTypeException e) {
                e.printStackTrace();
            }
        }

    }

    private void drawPoint(SinglePoint point, Canvas canvas) {

        PointF pointRaw = convertLogicalPoint2Raw(point.getPoint(), unitLength);
        if(point.getPointColor() != null){
            pointPaint.setColor(point.getPointColor());
        }
        int radius = point.getPointRadius() == null ? DEFAULT_SINGLE_POINT_RADIUS : point.getPointRadius();
        canvas.drawCircle(pointRaw.x, pointRaw.y, radius, pointPaint);
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
        xMax = (int) (width > height? width / unitLength : height / unitLength);
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
                canvas.drawCircle(x, y, axisPointRadius, axisPaint);
                String coorText = String.valueOf(-(i + 1));
                canvas.drawText(coorText, x, y + coordinateTextSize, axisPaint);
            }
        }
        // x+ coordinate points
        for (int i = 0; i < xMax; i++) {
            float x = origin.x + unitLength * (i + 1);
            float y = origin.y;
            if(x < rightPoint.x){
                path.moveTo(x, y);
                path.close();
                canvas.drawCircle(x, y, axisPointRadius, axisPaint);
                String coorText = String.valueOf(i + 1);
                canvas.drawText(coorText, x, y + coordinateTextSize, axisPaint);
            }
        }
        // y+ coordinate points
        for (int i = 0; i < yMax; i++) {
            float x = origin.x;
            float y = origin.y - unitLength * (i + 1);
            if(y > topPoint.y){
                path.moveTo(x, y);
                path.close();
                canvas.drawCircle(x, y, axisPointRadius, axisPaint);
                String coorText = String.valueOf(i + 1);
                canvas.drawText(coorText, x - coordinateTextSize, y, axisPaint);
            }
        }
        // y- coordinate points
        for (int i = 0; i < yMax; i++) {
            float x = origin.x;
            float y = origin.y + unitLength * (i + 1);
            if(y < bottomPoint.y){
                path.moveTo(x, y);
                path.close();
                canvas.drawCircle(x, y, axisPointRadius, axisPaint);
                String coorText = String.valueOf(-(i + 1));
                canvas.drawText(coorText, x - coordinateTextSize * 1.2f, y, axisPaint);
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
                    }else if(c == 0){
                        generateLinearLines(0f, b, canvas);
                    }else{
                        generateExpLines(a, b, c, canvas);
                    }
                    break;
                case "LogType":
                    generateLogLines(a, b, c, d, canvas);
                    break;
                case "CircularType":
                    generateCircularLines(a, b, c, d, canvas, circular);
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
     * @param c {@link ExpType#ExpType(float, float, float)}
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

    /**
     * generate the log function lines
     * @param a {@link LogType#LogType(float, float, float, float)}
     * @param b {@link LogType#LogType(float, float, float, float)}
     * @param c {@link LogType#LogType(float, float, float, float)}
     * @param d {@link LogType#LogType(float, float, float, float)}
     * @param canvas canvas
     */
    private void generateLogLines(Float a, Float b, Float c, Float d, Canvas canvas){
        // raw
        PointF start = new PointF();
        start.set(origin);
        start.x += 1;
        PointF end = rightPoint;

        float unit = (end.x - start.x) / xPointsValues.length;

        for (int i = 0; i < xPointsValues.length; i++) {
            // get the split point
            PointF split = new PointF(start.x + i * unit, start.y);
            // logical
            PointF splitLogic = convertRawPoint2Logical(split, unitLength);
            // calculate
            if(splitLogic.x == 0f){
                continue;
            }
            try {
                splitLogic.y = FuncUtils.getLogYValue(a, b, c, d, splitLogic.x);
            } catch (FunctionNotValidException e) {
                continue;
            }
            // convert logical to raw
            PointF splitRaw = convertLogicalPoint2Raw(splitLogic, unitLength);
            xPointsValues[i] = splitRaw;
        }

        drawBezier(canvas, FuncType.LOG_TYPE);
    }

    /**
     * generate the log function lines
     * @param a {@link LogType#LogType(float, float, float, float)}
     * @param b {@link LogType#LogType(float, float, float, float)}
     * @param c {@link LogType#LogType(float, float, float, float)}
     * @param d {@link LogType#LogType(float, float, float, float)}
     * @param canvas canvas
     */
    private void generateCircularLines(Float a, Float b, Float c, Float d, Canvas canvas, CircularType.Circular type){
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
            Float y;
            try {
                y = FuncUtils.getCircularYValue(a, b, c, d, splitLogic.x, type);
            } catch (FunctionTypeException e) {
                continue;
            }
            splitLogic.y = y;
            // convert logical to raw
            PointF splitRaw = convertLogicalPoint2Raw(splitLogic, unitLength);
            xPointsValues[i] = splitRaw;
        }

        drawBezier(canvas, FuncType.CIRCULAR_TYPE);
    }

    private void drawBezier(Canvas canvas, FuncType type) {
        if(xPointsValues != null && xPointsValues.length > 0){
            Path path = new Path();
            int k = -xMax;
            // if it is tangent function
            if(circular != null && circular.equals(CircularType.Circular.TAN)){
                for (k = -xMax / 2; k < 0; k++) { // from left to right
                    float leftX = k * PI - PI / 2;
                    float rightX = k * PI + PI / 2;
                    if (-xMax / 2 >= leftX && -xMax / 2 <= rightX){
                        break;
                    }
                }
            }
            // if it is cotangent function
            if(circular != null && circular.equals(CircularType.Circular.COT)){
                for (k = -xMax / 2; k < 0; k++) { // from left to right
                    float leftX = k * PI - PI / 2;
                    float rightX = k * PI + PI / 2;
                    if (-xMax / 2 >= leftX && -xMax / 2 <= rightX){
                        break;
                    }
                }
            }

            for (int i = 0; i < xPointsValues.length - 1; i++) {
                // if out of screen, do not render it 超出屏幕范围的点 不会绘制曲线
                if(  xPointsValues[i] != null && xPointsValues[i + 1] != null &&
                     (
                        // if current point is inside screen 判断当前点是否在屏幕内
                        (xPointsValues[i].y <= height && xPointsValues[i].y >= 0) ||
                        // if the next point is inside screen 判断下一个点是否在屏幕内
                        ( i < xPointsValues.length - 1 && xPointsValues[i + 1].y <= height && xPointsValues[i + 1].y >= 0) ||
                        // the previous point is inside screen 判断前一个点是否在屏幕内
                        ( i > 0 && xPointsValues[i - 1].y <= height && xPointsValues[i - 1].y >= 0)
                     )
                ){
                    path.moveTo(xPointsValues[i].x, xPointsValues[i].y);
                    /*
                     * 接下来将会计算得到两个相邻的点的切线方程，由此再算出两条切线的交点，将这个交点作为贝塞尔曲线的控制点
                     * next will get two tangent lines of two adjacent points.
                     * according to these two lines, will have a intersection point which will be used as the control point of a bezier curve.
                     */
                    // get a point on the line which super near the current point
                    float ad_x1 = xPointsValues[i].x + dx;
                    PointF dpLogic1 = convertRawPoint2Logical(ad_x1, origin.y, unitLength, origin);
                    PointF dp1 = FuncUtils.getPointByType(a, b, c, d, dpLogic1.x, type, circular);
                    if(dp1 == null){
                        continue;
                    }
                    // get a line near xPointsValues[i]
                    float[] tangentLineFuncCoefficients1 = FuncUtils.computeLinearFuncsByPoints(
                                    convertRawPoint2Logical(xPointsValues[i], unitLength)
                                    , dp1);
                    if(tangentLineFuncCoefficients1 == null){
                        Log.w(TAG, "tangentLineFuncCoefficients1 == null");
                        return;
                    }

                    // get a point on the line which super near the (current + 1) point
                    float ad_x2 = xPointsValues[i + 1].x - dx;
                    PointF dpLogic2 = convertRawPoint2Logical(ad_x2, origin.y, unitLength, origin);
                    PointF dp2 = FuncUtils.getPointByType(a, b, c, d, dpLogic2.x, type, circular);
                    // get a line near xPointsValues[i + 1]
                    float[] tangentLineFuncCoefficients2 = FuncUtils.computeLinearFuncsByPoints(
                                    convertRawPoint2Logical(xPointsValues[i + 1], unitLength)
                                    , dp2);
                    if(tangentLineFuncCoefficients2 == null){
                        Log.w(TAG, "tangentLineFuncCoefficients2 == null");
                        return;
                    }

                    // if it is the tan func
                    if(circular != null && circular.equals(CircularType.Circular.TAN)){
                        float domainLeft = k * PI - PI / 2;
                        float domainRight = k * PI + PI / 2;
                        if (dpLogic1.x > domainLeft && dpLogic1.x < domainRight){
                            if(dpLogic2.x > domainRight) {
                                k++;
                                continue;
                            }
                        }
                    }

                    // if it is the cot func
                    if(circular != null && circular.equals(CircularType.Circular.COT)){
                        float domain = k * PI;
                        while(dpLogic1.x > domain){
                            k++;
                            domain = k * PI;
                        }
                        if (dpLogic1.x < domain){
                            if(dpLogic2.x > domain) {
                                k++;
                                continue;
                            }
                        }
                    }

                    // compute the intersection point as the control point of bezier curve
                    PointF controlPointLogic =  FuncUtils.intersectionBetweenLinearFuncs(
                            tangentLineFuncCoefficients1[0],
                            tangentLineFuncCoefficients1[1],
                            tangentLineFuncCoefficients2[0],
                            tangentLineFuncCoefficients2[1]
                    );
                    if(controlPointLogic == null){
                        Log.w(TAG, "controlPointLogic == null");
                        return;
                    }
                    PointF controlPointRaw = convertLogicalPoint2Raw(controlPointLogic, unitLength);
                    path.quadTo(controlPointRaw.x, controlPointRaw.y, xPointsValues[i + 1].x, xPointsValues[i + 1].y);
                    canvas.drawPath(path, functionLinePaint);
                }
            }
        }
    }

    private  <T extends LinearType> void setFunctionType(T type) throws FunctionTypeException {
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
                    d = logType.d;
                    this.type = logType;
                    break;
                case "CircularType":
                    CircularType circularType = (CircularType) type;
                    a = circularType.a;
                    b = circularType.b;
                    c = circularType.c;
                    d = circularType.d;
                    circular = circularType.type;
                    this.type = circularType;
                    break;
                default:
                    throw new FunctionTypeException("Function type error.");
            }
        }
    }

    private void resetStatus(){
        a = null;
        b = null;
        c = null;
        d = null;
        circular = null;
        this.type = null;
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

    public void addFunctionLine(FunctionLine line){
        if(lines != null){
            lines.add(line);
        }
    }

    public void addPoint(SinglePoint point){
        if(points != null){
            points.add(point);
        }
    }

    public void reset(){
        if(lines != null){
            lines.clear();
        }
        if(points != null){
            points.clear();
        }
        invalidate();
    }

    public void setConfig(ChartConfig config) {
        setConfig(config, true);
    }

    private void setConfig(ChartConfig config, boolean invalidate){
        this.config = config;
        // axis color
        if(config.getAxisColor() != null){
            setAxisColor(config.getAxisColor());
        }else{
            setAxisColor(DEFAULT_AXIS_COLOR);
            this.config.setAxisColor(DEFAULT_AXIS_COLOR);
        }
        // axis width
        if(config.getAxisWidth() != null){
            setAxisWidth(config.getAxisWidth());
        }else{
            setAxisWidth(DEFAULT_AXIS_WIDTH);
            this.config.setAxisWidth(DEFAULT_AXIS_WIDTH);
        }
        // max values
        if(config.getMax() != null){
            setMax(config.getMax());
        }else{
            setMax(DEFAULT_MAX);
            this.config.setMax(DEFAULT_MAX);
        }
        // dx
        if(config.getPrecision() != null){
            setPrecision(config.getPrecision());
        }else{
            setPrecision(DEFAULT_PRECISION);
            this.config.setPrecision(DEFAULT_PRECISION);
        }
        // segment size
        if(config.getSegmentSize() != null){
            setSegmentSize(config.getSegmentSize());
        }else{
            setSegmentSize(DEFAULT_SEGMENT_SIZE);
            this.config.setSegmentSize(DEFAULT_SEGMENT_SIZE);
        }
        // axis point radius
        if(config.getAxisPointRadius() != null){
            setAxisPointRadius(config.getAxisPointRadius());
        }else{
            setAxisPointRadius(DEFAULT_AXIS_POINT_RADIUS);
            this.config.setAxisPointRadius(DEFAULT_AXIS_POINT_RADIUS);
        }
        if(invalidate){
            invalidate();
        }
    }

    public void setAxisWidth(int axisWidth) {
        this.axisWidth = axisWidth;
    }

    /**
     * 函数曲线的精度，这个精度用于计算两点间切线的交点。推荐值：1-10<br>
     * The precision of the function curve, it's used to calculate the intersection point of two points' tangent lines.
     * value recommended: 1-10
     * @param precision precision of the function curve
     */
    public void setPrecision(int precision) {
        this.dx = precision;
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
    public void setSegmentSize(int segmentSize){
        this.segmentSize = segmentSize;
    }

    public void setAxisColor(int axisColor) {
        this.axisColor = axisColor;
    }

    public void setAxisPointRadius(int axisPointRadius) {
        this.axisPointRadius = axisPointRadius;
    }

    /**
     * The max value that the axises have.
     * @param max axis max value
     */
    public void setMax(int max) {
        this.max = max;
    }
}
