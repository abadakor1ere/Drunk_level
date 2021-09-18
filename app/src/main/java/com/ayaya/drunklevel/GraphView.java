package com.ayaya.drunklevel;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.ScaleGestureDetector;
import android.view.View;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.SortedMap;

public class GraphView extends View implements ScaleGestureDetector.OnScaleGestureListener {
    
    private List<Pair<Long,Float>> points = null;
    private Paint paint;
    private Paint textPaint;
    private float zoom = 1;
    
    private static SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    
    public GraphView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStrokeWidth(8);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        textPaint = new Paint();
        textPaint.setTextSize(20);
        textPaint.setAntiAlias(true);
        final ScaleGestureDetector scaleDetector = new ScaleGestureDetector(getContext(), this);
        setOnTouchListener((view, motionEvent) -> {
            scaleDetector.onTouchEvent(motionEvent);
            return true;
        });
    }
    
    public void setPoints(List<Pair<Long,Float>> points) {
        this.points = points;
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (points == null || points.isEmpty())
            return;
        Long minX = null, maxX = null;
        Float minY = 0f, maxY = 0.1f;
        for (Pair<Long,Float> point : points) {
            if (minX==null || point.first<minX)
                minX = point.first;
            if (maxX==null || point.first>maxX)
                maxX = point.first;
            if (minY==null || point.second<minY)
                minY = point.second;
            if (maxY==null || point.second>maxY)
                maxY = point.second;
        }
        textPaint.setTextSize(getWidth()/32);
        float w = (float)getWidth()/(maxX-minX);
        float h = (float)(getHeight()-textPaint.getTextSize()*3)/(maxY-minY);
        // Tracer de la courbe
        Path path = new Path();
        boolean first = true;
        for (Pair<Long,Float> point : points) {
            if (first) {
                path.moveTo(z((point.first-minX)*w, zoom, getWidth()/2), getHeight()-(point.second-minY)*h-textPaint.getTextSize()*2);
                first = false;
                continue;
            }
            float t = (point.first-minX)*w;
            path.lineTo(z((point.first-minX)*w, zoom, getWidth()/2), getHeight()-(point.second-minY)*h-textPaint.getTextSize()*2);
        }
        paint.setPathEffect(null);
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(8);
        canvas.drawPath(path, paint);
        // Affichage des concentrations (y) max et min
        textPaint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText(new BigDecimal(maxY).setScale(2,BigDecimal.ROUND_HALF_UP)+"", 0, textPaint.getTextSize(), textPaint);
        canvas.drawText(new BigDecimal(minY).setScale(2,BigDecimal.ROUND_HALF_UP)+"", 0, getHeight()-textPaint.getTextSize(), textPaint);
        // Tracer de la droite actuelle
        paint.setPathEffect(new DashPathEffect(new float[]{10, 10}, 0));
        float nowRefX = z((new Date().getTime()/1000-minX)*w, zoom, getWidth()/2);
        paint.setColor(Color.RED);
        paint.setStrokeWidth(2);
        canvas.drawLine(nowRefX, 0, nowRefX, getHeight(), paint);
        // Tracer les droites et horaires references (x)
        float minRefXPos = textPaint.getTextSize()*5;
        float maxRefXPos = getWidth()-textPaint.getTextSize()*3;
        paint.setColor(Color.BLUE);
        canvas.drawLine(minRefXPos, 0, minRefXPos, getWidth()-textPaint.getTextSize(), paint);
        canvas.drawLine(maxRefXPos, 0, maxRefXPos, getWidth()-textPaint.getTextSize(), paint);
        textPaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText((zoom<.5?dateFormat:timeFormat).format(new Date((minX+(long)(z(minRefXPos,1/zoom,getWidth()/2)/w))*1000)), minRefXPos, getHeight(), textPaint);
        canvas.drawText((zoom<.5?dateFormat:timeFormat).format(new Date((minX+(long)(z(maxRefXPos,1/zoom,getWidth()/2)/w))*1000)), maxRefXPos, getHeight(), textPaint);
        canvas.drawText("⌚", nowRefX, getHeight(), textPaint);
    }
    
    private static float z(float value, float zoom, float origin) {
        return (value-origin)*zoom+origin;
    }
    
    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        zoom *= detector.getScaleFactor();
        invalidate();
        return true;
    }
    
    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        return true;
    }
    
    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {
    
    }
}
