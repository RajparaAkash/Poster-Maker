package com.postermaker.flyerdesigner.creator.custom_view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.internal.view.SupportMenu;

import java.util.Arrays;

import com.postermaker.flyerdesigner.creator.imageloader.Poster_Glide_Image_Utils;

public class Poster_Grid_Line_Saparator extends AppCompatImageView {

    private Context mContext;

    private final float[] bounds, bitmapPoints;

    boolean isInCenterX, isInCenterY, isInRotate;

    public Matrix matrix;

    private Paint paint, paintLine, paintLine1, rotatePaint;
    private View rotation;

    public Matrix getMatrix() {
        return this.matrix;
    }

    public void setMatrix(Matrix matrix) {
        this.matrix = matrix;
    }


    public Poster_Grid_Line_Saparator(Context context, @Nullable AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.isInCenterX = false;
        this.isInCenterY = false;
        this.bitmapPoints = new float[8];
        this.bounds = new float[8];
        this.matrix = new Matrix();
        this.isInRotate = false;
        initialization(context);
    }

    public Poster_Grid_Line_Saparator(Context context) {
        super(context);
        this.isInCenterX = false;
        this.isInCenterY = false;
        this.bitmapPoints = new float[8];
        this.bounds = new float[8];
        this.matrix = new Matrix();
        this.isInRotate = false;
        initialization(context);
    }


    private void initialization(Context context) {
        this.mContext = context;
        this.paint = new Paint();
        this.paint.setColor(-1);
        this.paint.setStrokeWidth((float) Poster_Glide_Image_Utils.convertDpToPx(context, 2.0f));
        this.paint.setPathEffect(new DashPathEffect(new float[]{5.0f, 5.0f}, 1.0f));
        this.paint.setStyle(Style.STROKE);
        this.paintLine = new Paint();
        this.paintLine.setAntiAlias(true);
        this.paintLine.setStrokeWidth(3.2f);
        this.paintLine.setColor(SupportMenu.CATEGORY_MASK);
        this.paintLine.setAlpha(255);
        this.paintLine.setStyle(Style.FILL_AND_STROKE);
        this.paintLine.setAntiAlias(true);
        this.paintLine1 = new Paint();
        this.paintLine1.setAlpha(255);
        this.paintLine1.setStrokeWidth(2.0f);
        this.paintLine1.setColor(Color.argb(50, 74, 255, 255));
        this.paintLine1.setStyle(Style.FILL_AND_STROKE);
        this.paintLine1.setPathEffect(new DashPathEffect(new float[]{10.0f, 5.0f}, 0.0f));
        this.rotatePaint = new Paint();
        this.rotatePaint.setAlpha(255);
        this.rotatePaint.setStrokeWidth(2.0f);
        this.rotatePaint.setColor(SupportMenu.CATEGORY_MASK);
        this.rotatePaint.setStyle(Style.FILL_AND_STROKE);
        this.rotatePaint.setPathEffect(new DashPathEffect(new float[]{10.0f, 5.0f}, 0.0f));
    }

    public void setCenterValues(boolean z, boolean z2) {
        this.isInCenterX = z;
        this.isInCenterY = z2;
        invalidate();
    }

    private void draw_grid_Line(Canvas canvas, int i, int i2, int i3, int i4, Paint paint) {
        Path path = new Path();
        path.moveTo((float) i, (float) i2);
        path.lineTo((float) i3, (float) i4);
        canvas.drawPath(path, paint);
    }

    public void set_grid_view_rotation(View view) {
        this.rotation = view;
    }

    public View get_grid_view_rotation() {
        return this.rotation;
    }

    public void get_Grid_Bound_Points(@NonNull float[] fArr) {
        fArr[0] = 0.0f;
        fArr[1] = 0.0f;
        fArr[2] = (float) get_grid_view_rotation().getWidth();
        fArr[3] = 0.0f;
        fArr[4] = 0.0f;
        fArr[5] = (float) get_grid_view_rotation().getHeight();
        fArr[6] = (float) get_grid_view_rotation().getWidth();
        fArr[7] = (float) get_grid_view_rotation().getHeight();
    }


    public Poster_Grid_Line_Saparator(Context context, @Nullable AttributeSet attributeSet) {
        super(context, attributeSet);
        this.isInCenterX = false;
        this.isInCenterY = false;
        this.bitmapPoints = new float[8];
        this.bounds = new float[8];
        this.matrix = new Matrix();
        this.isInRotate = false;
        initialization(context);
    }

    public void get_Sticker_Grid_Points(@Nullable View view, @NonNull float[] fArr) {
        if (view == null) {
            Arrays.fill(fArr, 0.0f);
            return;
        }
        get_Grid_Bound_Points(this.bounds);
        get_Mapped_Grid_Points(fArr, this.bounds);
    }

    public void get_Mapped_Grid_Points(@NonNull float[] fArr, @NonNull float[] fArr2) {
        this.matrix.mapPoints(fArr, fArr2);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.getWidth();
        canvas.getHeight();
        drawGridLines(canvas);
    }

    private void drawGridLines(Canvas canvas) {
        int i = 0;
        if (this.isInCenterX) {
            canvas.drawLine((float) (canvas.getWidth() / 2), 0.0f, (float) (canvas.getWidth() / 2), (float) canvas.getHeight(), this.paintLine);
            this.isInCenterX = false;
        }
        if (this.isInCenterY) {
            canvas.drawLine(0.0f, (float) (canvas.getHeight() / 2), (float) canvas.getWidth(), (float) (canvas.getHeight() / 2), this.paintLine);
            this.isInCenterY = false;
        }
        float width = ((float) canvas.getWidth()) / 10.0f;
        float height = ((float) canvas.getHeight()) / 10.0f;
        int i2 = 0;
        while (true) {
            float f = (float) i2;
            if (f > 10.0f) {
                break;
            }
            float f2 = f * width;
            canvas.drawLine(f2, 0.0f, f2, (float) canvas.getHeight(), this.paintLine1);
            i2++;
        }
        while (true) {
            width = (float) i;
            if (width <= 10.0f) {
                float f3 = width * height;
                canvas.drawLine(0.0f, f3, (float) canvas.getWidth(), f3, this.paintLine1);
                i++;
            } else {
                return;
            }
        }
    }

    public boolean isInRotate() {
        return this.isInRotate;
    }

    public void setInRotate(boolean z) {
        this.isInRotate = z;
    }

}
