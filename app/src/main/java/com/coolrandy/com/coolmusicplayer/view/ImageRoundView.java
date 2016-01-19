package com.coolrandy.com.coolmusicplayer.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

/**
 * Created by admin on 2015/12/29.
 */
public class ImageRoundView extends ImageView {

    private static final int TYPE_CIRCLE = 0;

    private static final int TYPE_ROUND = 1;

    /**
     * 圆角的大小
     */
    private int mRadius;

    /**
     * 控件的宽度
     */
    private int mWidth;

    /**
     * 控件的高度
     */
    private int mHeight;

    private int type;

    public ImageRoundView(Context context) {
        super(context);
    }

    public ImageRoundView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ImageRoundView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        Log.d("randy", "imageType= " + type);

    }

    /**
     * 绘制
     */
    @Override
    protected void onDraw(Canvas canvas) {
        mHeight = getHeight();
        mWidth = getWidth();
        Drawable drawable = getDrawable();
        if (drawable == null) {
            return;
        }
        if (getWidth() == 0 || getHeight() == 0) {
            return;
        }
        Bitmap b = null;
        if (drawable instanceof BitmapDrawable) {
            b =  ((BitmapDrawable) drawable).getBitmap();
        }
        if(b == null) {
            super.onDraw(canvas);
            return;
        }
        canvas.drawBitmap(createRoundConerImage(b), 0, 0, null);
    }

    /**
     * 根据原图和变长绘制圆形图片
     *
     * @param source
     * @param min
     * @return
     */
    private Bitmap createCircleImage(Bitmap source, int min) {
        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        Bitmap target = Bitmap.createBitmap(min, min, Bitmap.Config.ARGB_8888);
        /**
         * 产生一个同样大小的画布
         */
        Canvas canvas = new Canvas(target);
        /**
         * 首先绘制圆形
         */
        canvas.drawCircle(min / 2, min / 2, min / 2, paint);
        /**
         * 使用SRC_IN，参考上面的说明
         */
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        /**
         * 绘制图片
         */
        canvas.drawBitmap(source, 0, 0, paint);
        return target;
    }

    /**
     * 根据原图添加圆角
     *
     * @param source
     * @return
     */
    private Bitmap createRoundConerImage(Bitmap source) {
        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        //绘制空心效果
        // paint.setStyle(Style.STROKE);
        Bitmap target = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(target);
        RectF rect = new RectF(0, 0, mWidth, mHeight);
        canvas.drawRoundRect(rect, 20, 20, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(resize(source), 0, 0, paint);
        return target;
    }

    private Bitmap resize(Bitmap bitmap) {
        Matrix matrix = new Matrix();
        //注意：参数必须是float类型
        matrix.postScale((float)mWidth/(float)bitmap.getWidth(), (float)mHeight/(float)bitmap.getHeight()); //长和宽放大缩小的比例
        //java.lang.IllegalArgumentException: x + width must be <= bitmap.width()
        Bitmap resizeBmp = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
        return resizeBmp;
    }
}
