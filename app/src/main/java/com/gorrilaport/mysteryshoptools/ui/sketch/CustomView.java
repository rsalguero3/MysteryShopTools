package com.gorrilaport.mysteryshoptools.ui.sketch;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.gorrilaport.mysteryshoptools.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CustomView extends View {

    //drawing path
    private Path drawPath;

    //core instance variables
    private Paint canvasPaint;


    private Paint drawPaint;

    //initial color
    private int paintColor = Color.BLACK;

    //canvas
    private Canvas drawCanvas;

    //canvas bitmap
    public Bitmap canvasBitmap;

    private float currentBrushSize, lastBrushSize;

    private boolean erase=false;


    private ArrayList<Path> paths = new ArrayList<Path>();
    private ArrayList<Path> undonePaths = new ArrayList<Path>();

    private Map<Path, Integer> colorsMap = new HashMap<Path, Integer>();
    private Map<Path,Float> strokeMap=new HashMap<Path, Float>();



    private String tempColor;




    public CustomView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init(){
        //setup initial brush size
        currentBrushSize = getResources().getInteger(R.integer.small_size);
        lastBrushSize = currentBrushSize;

        //instantiate the the drawpath and the drawpaint
        drawPath = new Path();
        drawPaint = new Paint();

        //set initial  color
        // drawPaint.setColor(paintColor);

        //set default properties for the Path
        this.canvasPaint = new Paint();
        this.canvasPaint.setAntiAlias(true);
        this.canvasPaint.setDither(true);
        this.canvasPaint.setColor(paintColor);
        this.canvasPaint.setStyle(Paint.Style.STROKE);
        this.canvasPaint.setStrokeJoin(Paint.Join.ROUND);
        this.canvasPaint.setStrokeCap(Paint.Cap.ROUND);
        this.canvasPaint.setStrokeWidth(currentBrushSize);

        if (erase){
            drawPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        }else {
            drawPaint.setXfermode(null);
        }


    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        //canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(canvasBitmap, 0, 0, drawPaint);
        for (Path p : paths){
            canvasPaint.setColor(colorsMap.get(p));
            canvasPaint.setStrokeWidth(strokeMap.get(p));
            canvas.drawPath(p, canvasPaint);
        }
        canvasPaint.setColor(paintColor);
        canvas.drawPath(drawPath, canvasPaint);
        // this.setBackgroundColor(paintColor);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                touch_start(touchX, touchY);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                touch_move(touchX, touchY);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                touch_up();
                invalidate();
                break;
            default:
                return false;
        }
        return true;
    }

    //method to set color
    public void setColor(String newColor){
        // invalidate();
        paintColor = Color.parseColor(newColor);
        canvasPaint.setColor(paintColor);
        tempColor = newColor;

    }

    public int getColor(){
        return paintColor;
    }





    private float mX, mY;
    private static final float TOUCH_TOLERANCE = 4;

    private void touch_start(float x, float y) {
        undonePaths.clear();
        drawPath.reset();
        drawPath.moveTo(x, y);
        mX = x;
        mY = y;
        paths.add(drawPath);
        colorsMap.put(drawPath, paintColor);
        strokeMap.put(drawPath, currentBrushSize);
    }
    private void touch_move(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            drawPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
            mX = x;
            mY = y;
        }
    }
    private void touch_up() {
        drawPath.lineTo(mX, mY);
        // commit the path to our offscreen
        drawCanvas.drawPath(drawPath, canvasPaint);

        drawPath = new Path();
        paths.add(drawPath);
        colorsMap.put(drawPath, paintColor);
        strokeMap.put(drawPath, currentBrushSize);
        // kill this so we don't double draw

        drawPath.reset();

    }


    public void clear() {
        drawPath = new Path();
        paths.clear();
        drawCanvas.drawColor(Color.WHITE);
        invalidate();

    }
}
