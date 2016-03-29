package br.com.algum.algum_android.customViews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.shapes.RectShape;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import br.com.algum.algum_android.R;

/**
 * TODO: document your custom view class.
 */
public class PercentView extends View {
    private int mStartColor = Color.GRAY;
    private int mEndColor = Color.GRAY;
    private int mBallColor = Color.BLACK;
    private int mBallExceedColor = Color.RED;
    private float mValue = -1;

    private RectShape mRect;

    private TextPaint mTextPaint;
    private float mTextWidth;
    private float mTextHeight;

    public PercentView(Context context) {
        super(context);
        init(null, 0);
    }

    public PercentView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public PercentView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.PercentView, defStyle, 0);

        if (a.hasValue(R.styleable.PercentView_value)) {
            mValue = a.getFloat(R.styleable.PercentView_value, mValue);
        }

        if (a.hasValue(R.styleable.PercentView_startColor)) {
            mStartColor = a.getColor(
                    R.styleable.PercentView_startColor,
                    mStartColor);
        }

        if (a.hasValue(R.styleable.PercentView_endColor)) {
            mEndColor = a.getColor(
                    R.styleable.PercentView_endColor,
                    mEndColor);
        }

        if (a.hasValue(R.styleable.PercentView_ballColor)) {
            mBallColor = a.getColor(
                    R.styleable.PercentView_ballColor,
                    mBallColor);
        }

        if (a.hasValue(R.styleable.PercentView_ballColorExceed)) {
            mBallExceedColor = a.getColor(
                    R.styleable.PercentView_ballColorExceed,
                    mBallExceedColor);
        }

        a.recycle();

        // Update TextPaint and text measurements from attributes
        invalidateAndMeasurements();
    }

    private void invalidateAndMeasurements() {

      /*  mTextPaint.setTextSize(mExampleDimension);
        mTextPaint.setColor(mExampleColor);
        mTextWidth = mTextPaint.measureText(mExampleString);

        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        mTextHeight = fontMetrics.bottom;*/
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

//        Paint p = new Paint();
//        p.setColor(Color.YELLOW);
//        canvas.drawRect(0, 0, getWidth(), getHeight(), p);

        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();

        int contentWidth = getWidth() - paddingLeft - paddingRight;
        int contentHeight = getHeight() - paddingTop - paddingBottom;

        float rectLeft = 0 + paddingLeft + (contentHeight/4);
        float rectTop = (contentHeight/4) + paddingTop;
        float rectRight = contentWidth + paddingLeft - (contentHeight/4);
        float rectBottom = ((contentHeight/4) * 3) + paddingTop;
        float rectRadius = contentHeight / 4;

        Paint paintRect = new Paint();
        Shader gradient = new LinearGradient(rectLeft,rectTop,rectRight,rectBottom, mStartColor, mEndColor, Shader.TileMode.REPEAT);
        paintRect.setShader(gradient);
        //canvas.drawRoundRect(rectLeft, rectTop, rectRight, rectBottom, rectRadius, rectRadius, paintRect);
        canvas.drawRect(rectLeft, rectTop, rectRight, rectBottom, paintRect);

        if(mValue >= 0){
            Paint paintCircle = new Paint();

            if (mValue >= 100){
                mValue = 100;
                paintCircle.setColor(mBallExceedColor);
            }else{
                paintCircle.setColor(mBallColor);
            }

            float circleRadius = ((contentHeight * 4)/8) ;
            float circleX = (((rectRight-rectLeft)/100)*mValue)+paddingLeft+rectRadius;
            float circleY = (getHeight()/2);

            canvas.drawCircle(circleX, circleY, circleRadius, paintCircle);
        }
    }

    /**
     * Gets the Value attribute value.
     *
     * @return The Value attribute value.
     */
    public float getValue() {
        return mValue;
    }

    /**
     * Sets the view's value attribute value. In the example view
     *
     * @param value The Value attribute value to use.
     */
    public void setValue(float value) {
        mValue = value;
        invalidateAndMeasurements();
    }

    /**
     * Gets the start color attribute value.
     *
     * @return The start color attribute value.
     */
    public int getStartColor() {
        return mStartColor;
    }

    /**
     * Sets the view's start color attribute value
     *
     * @param startColor The example color attribute value to use.
     */
    public void setStartColor(int startColor) {
        mStartColor = startColor;
        invalidateAndMeasurements();
    }

    /**
     * Gets the end color attribute value.
     *
     * @return The end color attribute value.
     */
    public int getEndColor() {
        return mEndColor;
    }

    /**
     * Sets the view's end color attribute value
     *
     * @param endColor The end color attribute value to use.
     */
    public void setEndColor(int endColor) {
        mEndColor = endColor;
        invalidateAndMeasurements();
    }

    /**
     * Gets the end color attribute value.
     *
     * @return The end color attribute value.
     */
    public int getBallColor() {
        return mBallColor;
    }

    /**
     * Sets the view's end color attribute value
     *
     * @param ballColor The end color attribute value to use.
     */
    public void setBallColor(int ballColor) {
        mBallColor = ballColor;
        invalidateAndMeasurements();
    }

    /**
     * Gets the end color attribute value.
     *
     * @return The end color attribute value.
     */
    public int getBallColorExceed() {
        return mBallExceedColor;
    }

    /**
     * Sets the view's end color attribute value
     *
     * @param ballColorExceedColor The end color attribute value to use.
     */
    public void setBallColorExceed(int ballColorExceedColor) {
        mBallExceedColor = ballColorExceedColor;
        invalidateAndMeasurements();
    }

}
