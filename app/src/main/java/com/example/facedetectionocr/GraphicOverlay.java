package com.example.facedetectionocr;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import java.util.HashSet;
import java.util.Set;

public class GraphicOverlay extends View {
    Paint facePaint;
    Set<Rect> rects;
    Rect currentRect;
    public GraphicOverlay(Context context) {
        super(context);
        rects=new HashSet<>();
        currentRect=new Rect();
        init();
    }
    public GraphicOverlay(Context context, AttributeSet attrs) {
        super(context, attrs);
        rects=new HashSet<>();
        currentRect=new Rect();
        init();
    }
    public void add(Rect rect){
        rects.add(rect);
        currentRect=rect;
        invalidate();
    }
    public void clear(){
        rects.clear();
        postInvalidate();
    }
    private void init() {
        facePaint = new Paint();
        facePaint.setColor(Color.GREEN);
        facePaint.setStrokeWidth(4.0f);
        facePaint.setStyle(Paint.Style.STROKE);
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (rects.size()!=0) {
            for(Rect rect:rects) {
                canvas.drawRect(rect, facePaint);
            }
        }
    }
}