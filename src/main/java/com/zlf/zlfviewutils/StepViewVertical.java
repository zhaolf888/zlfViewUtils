package com.zlf.zlfviewutils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import java.util.Map;

/**
 * 项目名称：com.zlf.zlfviewutils
 * 项目版本：Test
 * 创建人：zhaolifeng
 * 创建时间：2017/7/28 0028 16:58
 * 修改人：Administrator
 * 修改时间：2017/7/28 0028 16:58
 * 类描述： 纵向进度指示器
 * 修改备注：
 * 属性	介绍	类型	默认	是否必须
 * v_bg_radius	背景 ○ 的半径	dimension	5	否
 * v_pro_radius	已完成 ○ 的半径	dimension	2	否
 * v_bg_width	背景线的宽度	dimension	3	否
 * v_bg_color	背景的颜色	color	#cdcbcc	否
 * v_pro_width	已完成线的宽度	dimension	2	否
 * v_pro_color	已完成的颜色	color	#029dd5	否
 * v_interval	○ 与 ○之间的间距	dimension	80	否
 * v_bgPositionX	指示线距view左边缘的距离	dimension	100	否
 * v_textPaddingLeft	步骤描述文字与指示线的距离	dimension	10	否
 * v_timePaddingRight	时间与指示线的距离	dimension	15	否
 * v_max_step	总步骤(○)的个数	int	5	否
 * v_pro_step	已完成步骤	int	1	否
 * v_textsize	字体大小	dimension	10	否
 * v_textMoveTop	指示器右侧文字位置上下移动的距离	dimension	5	否
 * v_timeMoveTop	指示器左侧文字位置上下移动的距离	dimension	4	否
 *
 * <com.zlf.zlfviewutils.StepViewVertical
 * android:id="@+id/vflow"
 * android:layout_width="match_parent"
 * android:layout_height="wrap_content"
 * android:paddingBottom="10dp"
 * android:paddingRight="10dp"
 * android:paddingTop="10dp"
 * app:v_bgPositionX="40dp"
 * app:v_bg_color="#029dd5"
 * app:v_bg_radius="8dp"
 * app:v_bg_width="4dp"
 * app:v_interval="80dp"
 * app:v_max_step="10"
 * app:v_pro_color="#cdcbcc"
 * app:v_pro_radius="8dp"
 * app:v_pro_step="9"
 * app:v_pro_width="4dp"
 * app:v_textMoveTop="7dp"
 * app:v_textsize="14dp" />
 */

public class StepViewVertical extends View {

    private Paint bgPaint;
    private Paint proPaint;
    private TextPaint textPaint;
    private float bgRadius;
    private float proRadius;
    private int lineBgWidth;
    private int bgColor;
    private int lineProWidth;
    private int proColor;
    private int interval;
    private int bgPositionX;
    private int maxStep;
    private int proStep;
    private int textPaddingLeft;
    private int timePaddingRight;
    private int textMoveTop;
    private int timeMoveTop;
    private int textsize;
    private float starY;
    private float stopY;
    private String[] titles;
    private String[] times;
    private int border;
    private Map<String, String> map;

    public StepViewVertical(Context context) {
        this(context, null);
    }

    public StepViewVertical(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StepViewVertical(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.StepViewVertical);
        bgRadius = ta.getDimension(R.styleable.StepViewVertical_v_bg_radius, 10);
        proRadius = ta.getDimension(R.styleable.StepViewVertical_v_pro_radius, 8);
        lineBgWidth = (int) ta.getDimension(R.styleable.StepViewVertical_v_bg_width, 3f);
        bgColor = ta.getColor(R.styleable.StepViewVertical_v_bg_color, Color.parseColor("#cdcbcc"));
        lineProWidth = (int) ta.getDimension(R.styleable.StepViewVertical_v_pro_width, 2f);
        proColor = ta.getColor(R.styleable.StepViewVertical_v_pro_color, Color.parseColor("#029dd5"));
        interval = (int) ta.getDimension(R.styleable.StepViewVertical_v_interval, 140);
        maxStep = ta.getInt(R.styleable.StepViewVertical_v_max_step, 5);
        proStep = ta.getInt(R.styleable.StepViewVertical_v_pro_step, 3);
        bgPositionX = (int) ta.getDimension(R.styleable.StepViewVertical_v_bgPositionX, 200);
        textPaddingLeft = (int) ta.getDimension(R.styleable.StepViewVertical_v_textPaddingLeft, 40);
        timePaddingRight = (int) ta.getDimension(R.styleable.StepViewVertical_v_timePaddingRight, 80);
        textMoveTop = (int) ta.getDimension(R.styleable.StepViewVertical_v_textMoveTop, 10);
        timeMoveTop = (int) ta.getDimension(R.styleable.StepViewVertical_v_timeMoveTop, 8);
        textsize = (int) ta.getDimension(R.styleable.StepViewVertical_v_textsize, 17);
        ta.recycle();
        init();
    }

    private void init() {
        bgPaint = new Paint();
        bgPaint.setAntiAlias(true);
        bgPaint.setStyle(Paint.Style.FILL);
        bgPaint.setColor(bgColor);
        bgPaint.setStrokeWidth(lineBgWidth);

        proPaint = new Paint();
        proPaint.setAntiAlias(true);
        proPaint.setStyle(Paint.Style.FILL);
        proPaint.setColor(proColor);
        proPaint.setStrokeWidth(lineProWidth);

        textPaint = new TextPaint();
        textPaint.setTextSize(textsize);
        textPaint.setAntiAlias(true);
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
        int bgWidth;
        if (widthMode == MeasureSpec.EXACTLY) {
            bgWidth = MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft() - getPaddingRight();
        } else
            bgWidth = dip2px(getContext(), 311);
        starY = getPaddingTop() + bgRadius;
        stopY = getPaddingTop() + bgRadius + (maxStep - 1) * interval;
        float bottom = stopY + bgRadius + getPaddingBottom();
        border = bgWidth - (bgPositionX + textPaddingLeft);
        setMeasuredDimension(bgWidth, (int) bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawBg(canvas);
        drawProgress(canvas);
        drawText(canvas);
    }

    private void drawText(Canvas canvas) {
        for (int i = 0; i < maxStep; i++) {
            setPaintColor(i);
            if (null != times && i < proStep)
                canvas.drawText(times[i], bgPositionX - timePaddingRight, stopY - (i * interval) + timeMoveTop, textPaint);
            if (null != titles) {
                canvas.save();
                canvas.translate(bgPositionX + textPaddingLeft, (stopY - (i * interval) - textMoveTop));
                StaticLayout sl = new StaticLayout(titles[i], textPaint, border, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                sl.draw(canvas);
                canvas.restore();
            }
        }
    }

    private void drawProgress(Canvas canvas) {
        int linePro;
        float lastBottom = stopY;
        for (int i = 0; i < proStep; i++) {
            setPaintColor(i);
            if (i == 0 || i == maxStep - 1)
                linePro = interval / 2;
            else
                linePro = interval;
            canvas.drawLine(bgPositionX, lastBottom, bgPositionX, lastBottom - linePro, proPaint);
            lastBottom = lastBottom - linePro;
            canvas.drawCircle(bgPositionX, stopY - (i * interval), proRadius, proPaint);
        }
    }

    private void setPaintColor(int i) {
        if (i < proStep) {
            textPaint.setColor(proColor);
        } else {
            textPaint.setColor(bgColor);
        }
        if (titles == null || map == null) return;
        String title = titles[i];
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (title.contains(entry.getKey())) {
                String value = entry.getValue();
                proPaint.setColor(Color.parseColor(value));
                textPaint.setColor(Color.parseColor(value));
                return;
            } else {
                proPaint.setColor(proColor);
            }
        }
    }

    private void drawBg(Canvas canvas) {
        canvas.drawLine(bgPositionX, stopY, bgPositionX, starY, bgPaint);
        for (int i = 0; i < maxStep; i++) {
            canvas.drawCircle(bgPositionX, stopY - (i * interval), bgRadius, bgPaint);
        }
    }

    public void setKeyColor(Map<String, String> map) {
        this.map = map;
    }

    public void setProgress(int progress, int maxStep, String[] titles, String[] times) {
        proStep = progress;
        this.maxStep = maxStep;
        this.titles = titles;
        this.times = times;
        invalidate();
    }
}
