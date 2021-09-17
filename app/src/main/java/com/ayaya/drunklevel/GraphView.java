package com.ayaya.drunklevel;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.Map;
import java.util.SortedMap;

public class GraphView extends View {
    
    private SortedMap<Long,Float> points = null;
    private Paint paint;
    
    public GraphView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        paint = new Paint();
        paint.setColor(Color.RED);
    }
    
    public void setPoints(SortedMap<Long, Float> points) {
        this.points = points;
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (points == null || points.isEmpty())
            return;
        Long minX = null, maxX = null;
        Float minY = null, maxY = null;
        for (Map.Entry<Long,Float> point : points.entrySet()) {
            if (minX==null || point.getKey()<minX)
                minX = point.getKey();
            if (maxX==null || point.getKey()>maxX)
                maxX = point.getKey();
            if (minY==null || point.getValue()<minY)
                minY = point.getValue();
            if (maxY==null || point.getValue()>maxY)
                maxY = point.getValue();
        }
        float w = (float)getWidth()/(maxX-minX);
        float h = (float)getHeight()/(maxY-minY);
        Map.Entry<Long,Float> previousPoint = null;
        for (Map.Entry<Long,Float> point : points.entrySet()) {
            if (previousPoint == null) {
                previousPoint = point;
                continue;
            }
            canvas.drawLine((previousPoint.getKey()-minX)*w, getHeight()-(previousPoint.getValue()-minY)*h, (point.getKey()-minX)*w, getHeight()-(point.getValue()-minY)*h, paint);
            int sx = (int) ((previousPoint.getKey()-minX)*w), sy = (int) ((previousPoint.getValue()-minY)*h), ex = (int) ((point.getKey()-minX)*w), ey = (int) ((point.getValue()-minY)*h);
            String eee = "eee";
        }
    }
}
