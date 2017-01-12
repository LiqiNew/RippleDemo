package com.liqi.rippledemo.diy.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;

import com.liqi.rippledemo.R;

/**
 * 波纹效果自定义控件
 * Created by LiQi on 2017/1/5.
 */
public class RippleView extends View {
    private final int CIRCLESIZE = 5;
    int indxe;
    int mHeight, mWidth;
    /**
     * 画笔
     */
    private Paint mPaint;
    /**
     * 图片
     */
    private Bitmap mBitmap;
    private int widthBitmap, heightBitmap;
    private int paddingLeft, paddingRight, paddingTop, paddingBottom;
    /**
     * 浅深还是深浅效果运行标签
     */
    private boolean mSqNot;
    /**
     * 判断是否自动运行效果
     */
    private boolean mStarWhat;
    /**
     * 圆环颜色值
     */
    private int mRoundRimColor;
    /**
     * 圆环大小值
     */
    private int mRoundRimSize;
    /**
     * 波痕震动幅度
     */
    private int mCircle = 10;
    /**
     * 圆半径
     */
    private int mRippleSize;
    /**
     * 判断是否执行
     */
    private boolean starTag = true;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //当波痕震动幅度已经超过设置宽度和高度的时候，重新计算
            if (indxe >= CIRCLESIZE)
                indxe = 0;
            invalidate();
        }
    };

    public RippleView(Context context) {
        super(context);
    }

    public RippleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public RippleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        mPaint = new Paint();
        mPaint.setAntiAlias(true); // 消除锯齿
        mPaint.setStyle(Paint.Style.STROKE); // 绘制空心圆
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs,
                R.styleable.RippleAttrs);
        Drawable drawable = typedArray.getDrawable(R.styleable.RippleAttrs_image);
        if (null == drawable)
            drawable = getResources().getDrawable(R.mipmap.ic_launcher);
        bitmapWHPadding((BitmapDrawable) drawable);

        mSqNot = typedArray.getBoolean(R.styleable.RippleAttrs_sqNot, true);
        mStarWhat = typedArray.getBoolean(R.styleable.RippleAttrs_starWhat, false);
        ColorStateList mTextColor = typedArray.getColorStateList(R.styleable.RippleAttrs_roundRimColor);
        setColor(mTextColor != null ? mTextColor : ColorStateList.valueOf(0xFF000000));

        mRoundRimSize = typedArray.getInteger(R.styleable.RippleAttrs_roundRimSize, -1);
        if (mRoundRimSize == -1)
            mRoundRimSize = 3;
        if (mStarWhat)
            star();
        typedArray.recycle();
    }

    /**
     * 获取bitmap宽度和padding值
     *
     * @param drawable
     */
    private void bitmapWHPadding(BitmapDrawable drawable) {
        mBitmap = drawable.getBitmap();
        widthBitmap = mBitmap.getWidth();
        heightBitmap = mBitmap.getHeight();
        paddingLeft = getPaddingLeft();
        paddingRight = getPaddingRight();
        paddingTop = getPaddingTop();
        paddingBottom = getPaddingBottom();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth() - paddingLeft - paddingRight;
        int height = getHeight() - paddingTop - paddingBottom;


        // 计算图片左边位置
        int left = (width / 2) - (widthBitmap / 2);
        // 计算图片上边位置
        int top = (height / 2) - (heightBitmap / 2);

        mRippleSize = widthBitmap / 2 + 6; // 设置内圆半径
        // 绘制外圆
        mPaint.setColor(mRoundRimColor);
        mPaint.setStrokeWidth(mRoundRimSize);
        int cx = width / 2;
        int cy = height / 2;
        if (mSqNot)
            algorithmRippleQS(canvas, cx, cy);
        else
            algorithmRippleSQ(canvas, cx, cy);
        indxe++;
        canvas.drawBitmap(mBitmap, left, top, null);
    }

    /**
     * 圆圈显示方式，浅深
     *
     * @param canvas
     * @param cx     X
     * @param cy     Y
     */
    private void algorithmRippleQS(Canvas canvas, int cx, int cy) {
        int alphaSize = 255;
        int alphaTemporarily = 0;
        int rippleTemporarily = 0;
        for (int i = 0; i < CIRCLESIZE; i++) {
            int alpha = -1;
            if (i < indxe) {
                int temporarily = alphaTemporarily + 50;
                alphaTemporarily = temporarily > alphaSize ? alphaSize : temporarily;
                alpha = alphaTemporarily;
            } else if (i > indxe) {
                alpha = 0;
            } else {
                alpha = alphaSize;
            }
            if (alpha != 0) {
                drawCircle(canvas, alpha, cx, cy, mRippleSize + rippleTemporarily);
                rippleTemporarily = mCircle * (i + 1);
            }
        }
    }

    /**
     * 圆圈显示方式，深浅
     *
     * @param canvas
     * @param cx     X
     * @param cy     Y
     */
    private void algorithmRippleSQ(Canvas canvas, int cx, int cy) {
        int alphaSize = 255;
        int rippleTemporarily = 0;
        for (int i = 0; i < CIRCLESIZE; i++) {
            if (i != 0) {
                alphaSize = alphaSize - 50;
            }
            if (i <= indxe) {
                drawCircle(canvas, alphaSize, cx, cy, mRippleSize + rippleTemporarily);
                rippleTemporarily = mCircle * (i + 1);
            }
        }
    }

    /**
     * 绘制外圆
     *
     * @param canvas
     * @param a          透明度
     * @param cx         X
     * @param cy         Y
     * @param rippleSize 圆半径
     */
    private void drawCircle(Canvas canvas, int a, int cx, int cy, int rippleSize) {
        mPaint.setAlpha(a);
        canvas.drawCircle(cx, cy, rippleSize,
                mPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mHeight == 0 && mWidth == 0) {
            mHeight = heightBitmap + ((mRippleSize + (mCircle * CIRCLESIZE)) * 2) + (mRoundRimSize * 5) + paddingTop + paddingBottom + 10;
            mWidth = widthBitmap + ((mRippleSize + (mCircle * CIRCLESIZE)) * 2) + (mRoundRimSize * 5) + paddingLeft + paddingRight + 10;
        }
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);

        if (widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(mWidth, mHeight);
        } else if (widthSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(mWidth, heightSpecSize);
        } else if (heightSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(widthSpecSize, mHeight);
        }

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    /**
     * Sets the text color.
     *
     * @attr ref android.R.styleable#TextView_textColor
     */
    public void setColor(ColorStateList colors) {
        if (colors == null) {
            throw new NullPointerException();
        }
        int color = colors.getColorForState(getDrawableState(), 0);
        if (color != mRoundRimColor) {
            mRoundRimColor = color;
        }
    }

    public boolean isStarTag() {
        return starTag;
    }

    public void star() {
        starTag = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (starTag) {
                    try {
                        Thread.sleep(500);
                        if (starTag) {
                            mHandler.sendEmptyMessage(1);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public void stop() {
        starTag = false;
    }
}
