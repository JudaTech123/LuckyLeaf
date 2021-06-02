package com.example.luckyleaf.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class AreaBox extends View {
    Rect drawRect = new Rect(0,0,0,0);
    public AreaBox(Context context) {
        super(context);
    }

    public AreaBox(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AreaBox(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        getDrawingRect(drawRect);
    }

    private Path createRoundedRectPath(float left, float top, float right, float bottom, float rx, float ry, boolean conformToOriginalPost) {
        Path path = new Path();
        if (rx < 0) rx = 0;
        if (ry < 0) ry = 0;
        float width = right - left;
        float height = bottom - top;
        if (rx > width/2) rx = width/2;
        if (ry > height/2) ry = height/2;
        float widthMinusCorners = (width - (2 * rx));
        float heightMinusCorners = (height - (2 * ry));

        path.moveTo(right, top + ry);
        path.rQuadTo(0, -ry, -rx, -ry);//top-right corner
        path.rLineTo(-widthMinusCorners, 0);
        path.rQuadTo(-rx, 0, -rx, ry); //top-left corner
        path.rLineTo(0, heightMinusCorners);

        if (conformToOriginalPost) {
            path.rLineTo(0, ry);
            path.rLineTo(width, 0);
            path.rLineTo(0, -ry);
        }
        else {
            path.rQuadTo(0, ry, rx, ry);//bottom-left corner
            path.rLineTo(widthMinusCorners, 0);
            path.rQuadTo(rx, 0, rx, -ry); //bottom-right corner
        }

        path.rLineTo(0, -heightMinusCorners);

        path.close();//Given close, last lineto can be removed.

        return path;
    }

    private Path createPariatRoundedRectPath(float left, float top, float right, float bottom, float rx, float ry, boolean conformToOriginalPost) {
        Path path = new Path();
        if (rx < 0) rx = 0;
        if (ry < 0) ry = 0;
        float width = right - left;
        float height = bottom - top;
        if (rx > width/2) rx = width/2;
        if (ry > height/2) ry = height/2;

        path.moveTo(left+drawRect.width()*3/4, top);
        path.rLineTo(drawRect.width()/4-rx, 0);
        path.moveTo(right, top + ry);
        path.rQuadTo(0, -ry, -rx, -ry);//top-right corner
        path.moveTo(right, top + ry);
        path.rLineTo(0, height-ry*2);
        path.rQuadTo(0, ry, -rx, ry);//bottom-left corner
        path.rLineTo(-width+rx*2, 0);
        path.rQuadTo(-rx, 0, -rx, -ry);//bottom-left corner
        path.rLineTo(0, -height/4);

        return path;
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setStyle(Paint.Style.STROKE);
        p.setColor(0xFF000000);
        p.setStrokeWidth(5);
        Path path = createPariatRoundedRectPath(drawRect.left,drawRect.top,drawRect.right,drawRect.bottom,50,50,false);
        canvas.drawPath(path,p);
    }
}
