package com.zlf.zlfviewutils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.Map;

/**
 * 项目名称：com.zlf.zlfviewutils
 * 项目版本：Test
 * 创建人：zhaolifeng
 * 创建时间：2017/7/28 0028 16:57
 * 修改人：Administrator
 * 修改时间：2017/7/28 0028 16:57
 * 类描述：  横向进度指示器
 * 修改备注：
 * 属性	介绍	类型	默认	是否必须
 * h_bg_radius	背景 ○ 的半径	dimension	5	否
 * h_pro_radius	已完成 ○ 的半径	dimension	2	否
 * h_bg_width	背景线的宽度	dimension	3	否
 * h_bg_color	背景的颜色	color	#cdcbcc	否
 * h_pro_width	已完成线的宽度	dimension	2	否
 * h_pro_color	已完成的颜色	color	#029dd5	否
 * h_text_padding	步骤描述文字(title)与○的距离	dimension	10	否
 * h_time_padding	时间与○的距离	dimension	15	否
 * h_max_step	总步骤(○)的个数	int	5	否
 * h_pro_step	已完成步骤	int	1	否
 * h_textsize	字体大小	dimension	10	否
 *
 * <com.zlf.zlfviewutils.StepViewHorizontal
 * android:id="@+id/hflowview4"
 * android:layout_width="match_parent"
 * android:layout_height="80dp"
 * android:paddingLeft="30dp"
 * app:h_bg_radius="6dp"
 * app:h_bg_width='4dp'
 * app:h_pro_radius='4dp'
 * app:h_pro_width="2dp"
 * app:h_text_padding='10dp'
 * app:h_textsize='10dp'
 * app:h_time_padding='17dp' />
 */

public class StepViewHorizontal extends View {

    private Paint bgPaint;
    private Paint proPaint;
    private float bgRadius;
    private float proRadius;
    private float startX;
    private float stopX;
    private float bgCenterY;
    private int lineBgWidth;
    private int bgColor;
    private int lineProWidth;
    private int proColor;
    private int textPadding;
    private int timePadding;
    private int maxStep;
    private int textSize;
    private int proStep;
    private int interval;
    private String[] titles = {"提交", "接单", "取件", "配送", "完成"};
    private String[] times = {"12:20"};
    private Map<String, String> map;

    public StepViewHorizontal(Context context) {
        this(context, null);
    }

    public StepViewHorizontal(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StepViewHorizontal(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.StepViewHorizontal);
        bgRadius = ta.getDimension(R.styleable.StepViewHorizontal_h_bg_radius, 10);
        proRadius = ta.getDimension(R.styleable.StepViewHorizontal_h_pro_radius, 8);
        lineBgWidth = (int) ta.getDimension(R.styleable.StepViewHorizontal_h_bg_width, 3f);
        bgColor = ta.getColor(R.styleable.StepViewHorizontal_h_bg_color, Color.parseColor("#cdcbcc"));
        lineProWidth = (int) ta.getDimension(R.styleable.StepViewHorizontal_h_pro_width, 2f);
        proColor = ta.getColor(R.styleable.StepViewHorizontal_h_pro_color, Color.parseColor("#029dd5"));
        textPadding = (int) ta.getDimension(R.styleable.StepViewHorizontal_h_text_padding, 20);
        timePadding = (int) ta.getDimension(R.styleable.StepViewHorizontal_h_time_padding, 30);
        maxStep = ta.getInt(R.styleable.StepViewHorizontal_h_max_step, 5);
        textSize = (int) ta.getDimension(R.styleable.StepViewHorizontal_h_textsize, 20);
        proStep = ta.getInt(R.styleable.StepViewHorizontal_h_pro_step, 1);
        ta.recycle();
        init();
    }

    private void init() {
        bgPaint = new Paint();
        bgPaint.setAntiAlias(true);
        bgPaint.setStyle(Paint.Style.FILL);
        bgPaint.setColor(bgColor);
        bgPaint.setStrokeWidth(lineBgWidth);
        bgPaint.setTextSize(textSize);
        bgPaint.setTextAlign(Paint.Align.CENTER);

        proPaint = new Paint();
        proPaint.setAntiAlias(true);
        proPaint.setStyle(Paint.Style.FILL);
        proPaint.setColor(proColor);
        proPaint.setStrokeWidth(lineProWidth);
        proPaint.setTextSize(textSize);
        proPaint.setTextAlign(Paint.Align.CENTER);

    }

    /**
     * 把密度转换为像素
     */
    static int dip2px(Context context, float px) {
        final float scale = getScreenDensity(context);
        return (int) (px * scale + 0.5);
    }

    /**
     * 得到设备的密度
     */
    private static float getScreenDensity(Context context) {
        return context.getResources().getDisplayMetrics().density;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int bgWidth;
        if (widthMode == MeasureSpec.EXACTLY) {
            bgWidth = MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft() - getPaddingRight();
        } else
            bgWidth = dip2px(getContext(), 311);

        int bgHeight;
        if (heightMode == MeasureSpec.EXACTLY) {
            bgHeight = MeasureSpec.getSize(heightMeasureSpec) - getPaddingTop() - getPaddingBottom();
        } else
            bgHeight = dip2px(getContext(), 49);
        float left = getPaddingLeft() + bgRadius;
        stopX = bgWidth - bgRadius;
        startX = left;
        bgCenterY = bgHeight / 2;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        interval = (int) ((stopX - startX) / (maxStep - 1));
        drawBg(canvas);
        drawProgress(canvas);
        drawText(canvas);
    }

    private void drawText(Canvas canvas) {
        for (int i = 0; i < maxStep; i++) {
            if (i < proStep) {
                setPaintColor(i);
                if (null != titles && i < titles.length)
                    canvas.drawText(titles[i], startX + (i * interval), bgCenterY - textPadding, proPaint);
                if (null != times && i < times.length)
                    canvas.drawText(times[i], startX + (i * interval), bgCenterY + timePadding, proPaint);
            } else {
                if (null != titles && i < titles.length) {
                    String title = titles[i];
                    if (null == title) continue;
                    canvas.drawText(title, startX + (i * interval), bgCenterY - textPadding, bgPaint);
                }
            }
        }
    }

    private void setPaintColor(int i) {
        if (titles == null || map == null) return;
        String title = titles[i];
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (title.contains(entry.getKey())) {
                proPaint.setColor(Color.parseColor(entry.getValue()));
                return;
            } else {
                proPaint.setColor(proColor);
            }
        }
    }

    private void drawProgress(Canvas canvas) {
        int linePro;
        float lastLeft = startX;
        for (int i = 0; i < proStep; i++) {
            setPaintColor(i);
            if (i == 0 || i == maxStep - 1)
                linePro = interval / 2;
            else
                linePro = interval;
            canvas.drawLine(lastLeft, bgCenterY, lastLeft + linePro, bgCenterY, proPaint);
            lastLeft = lastLeft + linePro;
            canvas.drawCircle(startX + (i * interval), bgCenterY, proRadius, proPaint);
        }
    }

    private void drawBg(Canvas canvas) {
        canvas.drawLine(startX, bgCenterY, stopX, bgCenterY, bgPaint);
        for (int i = 0; i < maxStep; i++) {
            canvas.drawCircle(startX + (i * interval), bgCenterY, bgRadius, bgPaint);
        }
    }


    public void setProgress(int progress, int maxStep, String[] titles, String[] times) {
        proStep = progress;
        this.maxStep = maxStep;
        this.titles = titles;
        this.times = times;
        invalidate();
    }

    public void setKeyColor(Map<String, String> map) {
        this.map = map;
    }
}

