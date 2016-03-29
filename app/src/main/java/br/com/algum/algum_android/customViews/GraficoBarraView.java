package br.com.algum.algum_android.customViews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.HashMap;
import java.util.Iterator;

import br.com.algum.algum_android.R;

/**
 * TODO: document your custom view class.
 */
public class GraficoBarraView extends View {
    private int mClosedColor = Color.GRAY;

    private int colors[] = {
            Color.parseColor("#1B7070"),
            Color.parseColor("#BB2D2D"),
            Color.parseColor("#BB6D2D"),
            Color.parseColor("#249624"),
            Color.parseColor("#3F2A81"),
            Color.parseColor("#BBA32D")
    };

    private int mSizePercent = 100;
    private HashMap<String,Float> valores;


    public GraficoBarraView(Context context) {
        super(context);
        init(null, 0);
    }

    public GraficoBarraView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public GraficoBarraView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.GraficoBarraView, defStyle, 0);

        a.recycle();

        // Update TextPaint and text measurements from attributes
        invalidateTextPaintAndMeasurements();
    }

    private void invalidateTextPaintAndMeasurements() {

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();

        int contentWidth = getWidth() - paddingLeft - paddingRight;
        int contentHeight = getHeight() - paddingTop - paddingBottom;

        float rectLeft = paddingLeft;
        float rectTop = paddingTop;
        float rectRight = ((contentWidth*mSizePercent)/100) + paddingLeft;
        float rectBottom = contentHeight + paddingTop;

        rectBottom = 55;

        Paint paintRect = new Paint();
        paintRect.setColor(mClosedColor);

        Paint paintText = new Paint();

        canvas.drawRect(rectLeft, rectTop, rectRight, rectBottom, paintRect);

        if(valores != null){
            float total = 0;
            Iterator it = valores.entrySet().iterator();
            while (it.hasNext()){
                HashMap.Entry<String,Float> pair = (HashMap.Entry<String,Float>)it.next();
                total += pair.getValue();
            }

            float linhaPos = rectBottom + 60;
            float left = rectLeft;
            int contador = 0;
            it = valores.entrySet().iterator();
            while(it.hasNext()){
                HashMap.Entry<String,Float> pair2 = (HashMap.Entry<String,Float>)it.next();
                paintRect.setColor(colors[contador%6]);
                contador += 1;
                float right = left + ((rectRight-rectLeft)*pair2.getValue())/total;
                canvas.drawRect(left, rectTop + ((rectBottom - rectTop) * 0.2f), right,rectBottom,paintRect);
                left += right-left;
                canvas.drawCircle(rectLeft + 60, linhaPos,15f,paintRect);
                paintText.setTextAlign(Paint.Align.LEFT);
                paintText.setColor(Color.BLACK);
                paintText.setTextSize(30);
                canvas.drawText(pair2.getKey(), rectLeft + 100, linhaPos + 10, paintText);
                paintText.setTextAlign(Paint.Align.RIGHT);
                canvas.drawText("R$ " + pair2.getValue().toString(), contentWidth - 90, linhaPos + 10, paintText);
                paintText.setColor(Color.GRAY);
                paintText.setTextSize(20);
                canvas.drawText("(" + String.format("%.2f", (pair2.getValue() / total) * 100) + "%)", contentWidth, linhaPos + 10, paintText);
                linhaPos += 60;

            }
        }

    }

    public int getmClosedColor() {
        return mClosedColor;
    }

    public void setmClosedColor(int mClosedColor) {
        this.mClosedColor = mClosedColor;
    }

    public int getmSizePercent() {
        return mSizePercent;
    }

    public void setmSizePercent(int mSizePercent) {
        this.mSizePercent = mSizePercent;
    }

    public HashMap<String, Float> getValores() {
        return valores;
    }

    public void setValores(HashMap<String, Float> valores) {
        this.valores = valores;
    }
}
