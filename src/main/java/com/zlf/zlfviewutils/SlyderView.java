package com.zlf.zlfviewutils;

/**
 * User: zhaolifeng
 * Date: 2016-01-27
 * Time: 15:09
 * 抽奖转盘 的自定义View
 * 
 * 调用 SlyderView slyderView = new SlyderView(this);
 *         slyderView.play();
           slyderView.setRunningTime(5*1000);
 */

import android.app.Activity;
import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.EmbossMaskFilter;
import android.graphics.MaskFilter;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

public class SlyderView extends View{
    public SlyderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }
    public SlyderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }
    public SlyderView(Context context) {
        super(context);
        init(context);
    }
    /**
     * 屏幕宽度
     */
    private int screenW;
    /**
     * 屏幕的高度
     */
    private int screenH;
    /**
     * 分割的度数
     */
    private int [] drgrees = {20,50,40,90,70,40,50};
    /***
     * 分割的文字
     */
    private String [] strs = {"level1","level2","level3","level4","level5","level6","level7"};
    /**
     * 分割的颜色
     */
    private int [] colos = new int[] { 0xfed9c960, 0xfe57c8c8, 0xfe9fe558, 0xfef6b000, 0xfef46212, 0xfecf2911, 0xfe9d3011 };
    /**
     * 画笔
     */
    private Paint paint;
    /**
     * 文字的大小
     */
    private float textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 15, getResources().getDisplayMetrics());
    /**
     * 文字的颜色
     */
    private int textcolor = Color.WHITE;
    /**
     * 园的半径
     */
    private float radius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 100, getResources().getDisplayMetrics());
    /**
     * 画文字的距离
     */
    private float textdis = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 80, getResources().getDisplayMetrics());
    /**
     * 画箭头的大小
     */
    private float roketSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics());

    private float initDegress = 0;
    /**
     * 圆心
     */
    private float centerX;
    /**
     * 圆心
     */
    private float centerY;
    /**
     * 立体边缘
     */
    private MaskFilter filter = new EmbossMaskFilter(new float[] { 1, 1, 1 },0.4f, 6, 3.5f);
    private MaskFilter outerFilter = new BlurMaskFilter(10, BlurMaskFilter.Blur.OUTER);
    private MaskFilter innerFilter = new BlurMaskFilter(10, BlurMaskFilter.Blur.INNER);

    /**
     * 停止的地方
     */
    private int stopNum;
    @SuppressWarnings("deprecation")
    private void init(Context context){
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Style.FILL);
        paint.setColor(Color.WHITE);
        screenW = ((Activity)context).getWindowManager().getDefaultDisplay().getWidth();
        screenH = ((Activity)context).getWindowManager().getDefaultDisplay().getHeight();
        int[] colores = new int[3];
        colores[0] = Color.rgb(0xfF, 0x99, 0x00);
        colores[1] = Color.rgb(0xff, 0xff, 0x00);
        colores[2] = Color.rgb(0xff, 0x99, 0x00);
        float[] positions = new float[3];
        positions[0] = 0.0f;
        positions[1] = 0.5f;
        positions[2] = 1.0f;
        gradient = new RadialGradient(centerX, centerY, radius/5, colores, positions, TileMode.CLAMP);
    }
    /**
     * 绘制三角箭头
     */
    private Path path = new Path();
    /**
     * 绘制矩形框
     */
    private RectF oval;
    /**
     * 外圆内阴影矩阵
     */
    private ColorMatrixColorFilter colorFilter = new ColorMatrixColorFilter(new float[]{
            1,0,0,0,0,
            0,1,0,0,0,
            0,0,1,0,0,
            0,0,0,-1,255
    });
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        centerX = screenW/2;
        centerY = screenH/2;
        oval = new RectF(centerX-radius,centerY-radius,centerX+radius,centerY+radius);
        float start = 0;
        paint.setColor(Color.rgb(0xdd, 0xdd, 0xdd));
        paint.setAlpha(127);
        canvas.drawCircle(centerX, centerY, radius+10, paint);
        paint.setAlpha(255);
        //画扇形
        paint.setAntiAlias(true);
        for(int i=0;i<drgrees.length;i++){
            float sweepAngle = drgrees[i];
            float startAngle = start;
            paint.setColor(colos[i%colos.length]);
            canvas.drawArc(oval, startAngle, sweepAngle, true, paint);
            start += drgrees[i];
        }
        //画文字
        paint.setColor(textcolor);
        paint.setAntiAlias(true);
        paint.setTextSize(textSize);
        paint.setTextAlign(Paint.Align.RIGHT);
        start = 0;
        for(int i=0;i<drgrees.length;i++){
            canvas.save();
            canvas.rotate(start+drgrees[i]/2, centerX, centerY);
            canvas.drawText(strs[i], centerX+textdis, centerY, paint);
            canvas.restore();
            start += drgrees[i];
        }
        int saveCount = canvas.save();
        //画外层立体效果
        paint.setColorFilter(colorFilter);
        canvas.saveLayer(oval,paint,Canvas.ALL_SAVE_FLAG);
        paint.setColorFilter(null);
        canvas.drawARGB(255, 0, 0, 0);
        paint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
        canvas.drawCircle(centerX, centerY, radius, paint);
        paint.setXfermode(null);
        paint.setMaskFilter(innerFilter);
        paint.setColor(Color.argb(0xff, 0, 0, 0));
        canvas.drawCircle(centerX, centerY, radius, paint);
        paint.setMaskFilter(null);
        canvas.restoreToCount(saveCount);
        //画内圆和内园效果
        canvas.save();
        paint.setColor(Color.argb(0xff, 0, 0, 0));
        paint.setAntiAlias(true);
        paint.setMaskFilter(outerFilter);
        canvas.rotate(initDegress, centerX, centerY);
        canvas.drawCircle(centerX, centerY, radius/3, paint);
        paint.setMaskFilter(null);
        paint.setColor(Color.WHITE);
        canvas.drawCircle(centerX, centerY, radius/3, paint);
        //画三角型叠加当箭头
        path.moveTo(centerX-radius/3, centerY);
        path.lineTo(centerX, centerY-radius/3-roketSize);
        path.lineTo(centerX+radius/3, centerY);
        path.close();
        canvas.drawPath(path, paint);
        canvas.restore();
        paint.setMaskFilter(filter);
        paint.setColor(Color.GREEN);
        paint.setShader(gradient);
        canvas.drawCircle(centerX, centerY, radius/5, paint);
        paint.setMaskFilter(null);
        paint.setShader(null);
        //重绘调整三角的指向达到滚动的效果，现实项目中可不能这样用的，效率太低下了，拆分View用动画完成滚动才是王道
        if(isRunning){
            if(initDegress>=360){
                initDegress = 0;
            }
            initDegress +=4;
            invalidate();
        }
        if(isStoping){
            if(initDegress<=360){
                initDegress+=4;
                invalidate();
            }else{
                if(initDegress-360<stop_degress){
                    initDegress+=2;
                    invalidate();
                }
            }
        }
    }
    private boolean isRunning = false;
    private boolean isStoping = false;
    private int stop_degress =90;
    /**
     * 渐变
     */
    private RadialGradient gradient;

    /**
     * 开始转动
     * 
     */
    public void play(){
        isRunning = true;
        invalidate();
    }

    /**
     * 停止转动
     * @param count
     */
    public void stop(int count){
        for(int i =0;i<=count;i++){
            if(i == count){
                stop_degress +=drgrees[i]/2;
            }else{
                stop_degress +=drgrees[i];
            }
        }
        isStoping = true;
        isRunning = false;
        invalidate();
    }
    public int getSize(){
        return drgrees.length;
    }

    /**
     * 设置转动的时间
     * @param time
     */
    public void setRunningTime(final long time){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(time);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if(stopNum==0){
                    stopNum =Math.round((int) (Math.random() * (getSize() - 1)) + 1);//随机一个 0-数量大小-1 的随机数
                }
                Log.e("666", "num==" + stopNum);
                stop(stopNum);
            }
        }).start();
    }


    /**
     * 设置最后停留的位置
     * @param num
     */
    public void setWhereNumStop(int num){
        stopNum=num;//顺序和分割的文字数组的顺序一致    0就是停在数组第一个位置   6就是停在数组第七个位置
    }
}