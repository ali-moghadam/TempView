package com.alirnp.tempview;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;

public class TempView extends View {

    private static final String DEFAULT_TEXT_TIME = "03 C`";
    private static final String DEFAULT_TEXT_STATUS = "temperature";
    private final static int DEFAULT_BACKGROUND_PROGRESS_COLOR = Color.parseColor("#F5F5F5");
    private final static float DEFAULT_BACKGROUND_PROGRESS_RADIUS = dpToPx(120);
    private final static float DEFAULT_BACKGROUND_PROGRESS_STROKE_WIDTH = dpToPx(20);
    private final static float DEFAULT_CIRCLE_STROKE_WIDTH = dpToPx(3);
    private final static int DEFAULT_PROGRESS_COLOR = Color.parseColor("#1a8dff");
    private final static int DEFAULT_START_TIME_STROKE_COLOR = Color.parseColor("#00E676");
    private final static int DEFAULT_CLOCK_COLOR = Color.parseColor("#CFD8DC");
    private final static int DEFAULT_TEXT_COLOR = Color.parseColor("#2196F3");
    private final static int DEFAULT_DEGREE_COLOR = Color.parseColor("#2196F3");
    private static float DEFAULT_SPACE_TEXT = 45;
    private static int DEFAULT_CENTER_TEXT_SIZE = dpToPx(32);
    private static int DEFAULT_TOP_TEXT_SIZE = dpToPx(20);
    private static int DEFAULT_DRAWABLE_SIZE = dpToPx(20);
    private final static float DEFAULT_MIN_VALUE = -10;
    private final static float DEFAULT_MAX_VALUE = 14;

    private final static float START_DEGREE = 270;
    private final static float END_DEGREE = 310;

    private Context context;
    private float mDegreeValue;
    private float mRadiusBackgroundProgress;
    private float mStrokeWithBackgroundProgress;
    private float mStrokeWithCircle;
    private int mColorProgress;
    private int mColorBackgroundProgress;
    private int mColorValue;
    private int mColorText;
    private int mColorDegree;
    private float mFloatValue;
    private String mStringTextCenter;
    private String mStringTextStatus;
    private Paint mPaintBackgroundProgress;
    private Paint mPaintValue;
    private Paint mPaintProgress;
    private Paint mPaintHandClock;
    private Paint mPaintHandClockColored;
    private Paint mPaintTopText;
    private Paint mPaintCenterText;
    private Paint mPaintDrawable;
    private RectF mRectBackground;
    private RectF mRectProgress;
    private RectF mRectClock;
    private RectF mRectDrawable;
    private Bitmap mBitmap;
    private float mFloatLengthOfClockLines;
    private float mFloatBeginOfClockLines;
    private int mWidthBackgroundProgress;
    private int mHeightBackgroundProgress;
    private CircleArea mCircleArea = new CircleArea();

    private OnSeekChangeListener onSeekCirclesListener;

    private boolean accessMoving;
    private boolean isIndicator;
    private static final String TAG = "TempViewLog";
    private float mIntegerMinValue;
    private float mIntegerMaxValue;
    private int mTextSizeTop;
    private int mTextSizeCenter;
    private int mIntDrawableSize;

    private float xPositionText;
    private float yPositionText;
    private float xPositionDrawable;
    private float yPositionDrawable;

    public TempView(Context context) {
        super(context);
        this.context = context;
    }

    public TempView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        this.context = context;
        init(attrs);
    }

    public TempView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init(attrs);

    }

    public void setOnSeekCirclesListener(OnSeekChangeListener onSeekCirclesListener) {
        this.onSeekCirclesListener = onSeekCirclesListener;
    }


    public void setMinValue(float value) {
        mIntegerMinValue = value;
        invalidate();
    }

    public void setMaxValue(float value) {
        mIntegerMaxValue = value;
        invalidate();
    }

    private static void setTextSizeForWidth(Paint paint, float desiredWidth, String text) {

        final float testTextSize = dpToPx(48f);

        paint.setTextSize(testTextSize);
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);

        DEFAULT_SPACE_TEXT = (testTextSize * desiredWidth / bounds.width()) / 1.2f;

        paint.setTextSize(DEFAULT_SPACE_TEXT);
    }

    private static int dpToPx(float dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    private static void fillCircleStrokeBorder(
            Canvas c, float cx, float cy, float radius,
            int circleColor, float borderWidth, int borderColor, Paint p) {

        int saveColor = p.getColor();
        p.setColor(circleColor);
        Paint.Style saveStyle = p.getStyle();
        p.setStyle(Paint.Style.FILL);
        c.drawCircle(cx, cy, radius, p);
        if (borderWidth > 0) {
            p.setColor(borderColor);
            p.setStyle(Paint.Style.STROKE);
            float saveStrokeWidth = p.getStrokeWidth();
            p.setStrokeWidth(borderWidth);
            c.drawCircle(cx, cy, radius - (borderWidth / 2), p);
            p.setStrokeWidth(saveStrokeWidth);
        }
        p.setColor(saveColor);
        p.setStyle(saveStyle);
    }
    
    public void setIsIndicator(boolean isIndicator) {
        this.isIndicator = isIndicator;
        invalidate();
    }

    private void setCurrentValue(float value) {
        this.mFloatValue = value;
        value = validateValue(value);
        value = rotateValue(value);

        mDegreeValue = (value - mIntegerMinValue) * getDegreePerHand();

    }

    private void setCurrentTemp(float value) {
        mStringTextCenter = String.format("%s C°", Math.round(value));
        mFloatValue = value;
        invalidate();

    }

    public void setTemp(float value) {

        mStringTextCenter = String.format("%s C°", Math.round(value));
        mFloatValue = value;
        mDegreeValue = (validateValue(rotateValue(value)) - mIntegerMinValue) * getDegreePerHand();
        invalidate();

    }

    public void setTextStatus(String status) {
        this.mStringTextStatus = status;
        invalidate();
    }

    public void setDrawable(@DrawableRes int drawable) {
        setVectorBitmap(drawable);
        invalidate();
    }

    private void setTextSizeForWidthSingleText(Paint paint, float desiredWidth, String text) {

        final float testTextSize = dpToPx(1f);

        paint.setTextSize(testTextSize);
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);

        DEFAULT_SPACE_TEXT = testTextSize * desiredWidth / bounds.width();

        paint.setTextSize(DEFAULT_SPACE_TEXT);
    }

    private void init(AttributeSet attrs) {

        mStringTextCenter = DEFAULT_TEXT_TIME;
        mStringTextStatus = DEFAULT_TEXT_STATUS;

        if (attrs != null) {

            TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.TempView, 0, 0);

            try {

                isIndicator = a.getBoolean(R.styleable.TempView_tv_is_indicator, true);

                mColorBackgroundProgress = a.getColor(R.styleable.TempView_tv_color_background_progress, DEFAULT_BACKGROUND_PROGRESS_COLOR);
                mColorValue = a.getColor(R.styleable.TempView_tv_color_value, DEFAULT_START_TIME_STROKE_COLOR);
                mColorProgress = a.getColor(R.styleable.TempView_tv_color_progress, DEFAULT_PROGRESS_COLOR);
                mColorText = a.getColor(R.styleable.TempView_tv_color_text, DEFAULT_TEXT_COLOR);
                mColorDegree = a.getColor(R.styleable.TempView_tv_color_degree, DEFAULT_DEGREE_COLOR);

                mStrokeWithCircle = a.getDimension(R.styleable.TempView_tv_stroke_width_circle, DEFAULT_CIRCLE_STROKE_WIDTH);
                mStrokeWithBackgroundProgress = a.getDimension(R.styleable.TempView_tv_stroke_width_background_progress, DEFAULT_BACKGROUND_PROGRESS_STROKE_WIDTH);

                mStringTextCenter = a.getString(R.styleable.TempView_tv_text_center);
                if (mStringTextCenter == null)
                    mStringTextCenter = DEFAULT_TEXT_TIME;


                mStringTextStatus = a.getString(R.styleable.TempView_tv_text_status);
                if (mStringTextStatus == null)
                    mStringTextStatus = DEFAULT_TEXT_STATUS;


                mIntegerMinValue = a.getFloat(R.styleable.TempView_tv_min_value, DEFAULT_MIN_VALUE);
                mIntegerMaxValue = a.getFloat(R.styleable.TempView_tv_max_value, DEFAULT_MAX_VALUE);

                mTextSizeTop = a.getDimensionPixelSize(R.styleable.TempView_tv_size_text_top, DEFAULT_TOP_TEXT_SIZE);
                mTextSizeCenter = a.getDimensionPixelSize(R.styleable.TempView_tv_size_text_center, DEFAULT_CENTER_TEXT_SIZE);
                mIntDrawableSize = a.getDimensionPixelSize(R.styleable.TempView_tv_size_drawable, DEFAULT_DRAWABLE_SIZE);


                setVectorBitmap(a.getResourceId(R.styleable.TempView_tv_drawable, 0));
                setCurrentValue(a.getFloat(R.styleable.TempView_tv_current_value, 0));
                setCurrentTemp(mFloatValue);


            } finally {

                a.recycle();
            }

            mPaintBackgroundProgress = new Paint();
            mPaintBackgroundProgress.setAntiAlias(true);
            mPaintBackgroundProgress.setColor(mColorBackgroundProgress);
            mPaintBackgroundProgress.setStrokeWidth(mStrokeWithBackgroundProgress);
            mPaintBackgroundProgress.setStyle(Paint.Style.STROKE);
            mPaintBackgroundProgress.setStrokeCap(Paint.Cap.ROUND);


            mPaintValue = new Paint();
            mPaintValue.setAntiAlias(true);
            mPaintValue.setStrokeWidth(mStrokeWithCircle);
            mPaintValue.setColor(mColorValue);
            mPaintValue.setStyle(Paint.Style.FILL_AND_STROKE);

            mPaintProgress = new Paint();
            mPaintProgress.setAntiAlias(true);
            mPaintProgress.setStrokeWidth(mPaintBackgroundProgress.getStrokeWidth());
            mPaintProgress.setColor(mColorProgress);
            mPaintProgress.setStrokeCap(Paint.Cap.ROUND);
            mPaintProgress.setStyle(Paint.Style.STROKE);


            mPaintHandClock = new Paint();
            mPaintHandClock.setAntiAlias(true);
            mPaintHandClock.setStrokeWidth(mPaintBackgroundProgress.getStrokeWidth() / 3.6f);
            mPaintHandClock.setColor(DEFAULT_CLOCK_COLOR);
            mPaintHandClock.setStyle(Paint.Style.STROKE);
            mPaintHandClock.setStrokeCap(Paint.Cap.ROUND);

            mPaintHandClockColored = new Paint();
            mPaintHandClockColored.setAntiAlias(true);
            mPaintHandClockColored.setStrokeWidth(mPaintBackgroundProgress.getStrokeWidth() / 3.6f);
            mPaintHandClockColored.setColor(mColorDegree);
            mPaintHandClockColored.setStyle(Paint.Style.STROKE);
            mPaintHandClockColored.setStrokeCap(Paint.Cap.ROUND);

            mPaintCenterText = new Paint();
            mPaintCenterText.setAntiAlias(true);
            mPaintCenterText.setColor(mColorText);
            mPaintCenterText.setTextAlign(Paint.Align.CENTER);
            mPaintCenterText.setStyle(Paint.Style.FILL_AND_STROKE);
            mPaintCenterText.setTextSize(mTextSizeCenter);

            mPaintTopText = new Paint();
            mPaintTopText.setAntiAlias(true);
            mPaintTopText.setColor(mColorText);
            mPaintTopText.setTextAlign(Paint.Align.CENTER);
            mPaintTopText.setStyle(Paint.Style.FILL_AND_STROKE);
            mPaintTopText.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.ITALIC));
            mPaintTopText.setTextSize(mTextSizeTop);

            mPaintDrawable = new Paint();
            mPaintDrawable.setAntiAlias(true);
            mPaintDrawable.setColorFilter(new PorterDuffColorFilter(mColorText, PorterDuff.Mode.SRC_IN));

            mRectProgress = new RectF();
            mRectBackground = new RectF();
            mRectClock = new RectF();
            mRectDrawable = new RectF();

        }

    }

    private void setVectorBitmap(@DrawableRes int drawable) {
        if (drawable == 0) return;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        mBitmap = ResourceUtil.getBitmap(context, drawable);

    }

    private float getDegreePerHand() {
        return END_DEGREE / getLeftValue();
    }

    private float getLeftValue() {
        return (mIntegerMaxValue - mIntegerMinValue);
    }

    private float getSweepProgressArc() {

        float sweep = (START_DEGREE < mDegreeValue) ? mDegreeValue - START_DEGREE : 360 - (START_DEGREE - mDegreeValue);

        if (mDegreeValue == START_DEGREE)
            sweep = 1;


        return sweep;
    }

    private float getDrawXOnBackgroundProgress(float degree, float backgroundRadius, float backgroundWidth) {
        float drawX = (float) Math.cos(Math.toRadians(degree));
        drawX *= backgroundRadius;
        drawX += backgroundWidth / 2;
        return drawX;
    }

    private float getDrawYOnBackgroundProgress(float degree, float backgroundRadius, float backgroundHeight) {
        float drawY = (float) Math.sin(Math.toRadians(degree));
        drawY *= backgroundRadius;
        drawY += backgroundHeight / 2;
        return drawY;
    }

    private float rotateValue(float value) {
        float _3_5 = getLeftValue() / 3.5f;
        value = value - _3_5;
        return value;
    }

    private float validateValue(float value) {
        if (value < mIntegerMinValue)
            value = mIntegerMinValue;

        if (value > mIntegerMaxValue)
            value = mIntegerMaxValue;

        return value;
    }

    private float getValueFromAngel(double angel) {
        return (float) ((angel + (360 - START_DEGREE)) / getDegreePerHand()) + mIntegerMinValue;
    }

    private int getDesireHeight() {
        return (int) ((mRadiusBackgroundProgress * 2) + mPaintBackgroundProgress.getStrokeWidth() + getVerticalPadding());
    }

    private int getVerticalPadding() {
        return getPaddingTop() + getPaddingBottom();
    }

    private int getDesireWidth() {
        return (int) ((mRadiusBackgroundProgress * 2) + mPaintBackgroundProgress.getStrokeWidth() + getHorizontalPadding());
    }

    private int getHorizontalPadding() {
        return getPaddingLeft() + getPaddingRight();
    }

    private double getAngleFromPoint(double firstPointX, double firstPointY, double secondPointX, double secondPointY) {

        if ((secondPointX > firstPointX)) {

            return (Math.atan2((secondPointX - firstPointX), (firstPointY - secondPointY)) * 180 / Math.PI);

        } else if ((secondPointX < firstPointX)) {

            return 360 - (Math.atan2((firstPointX - secondPointX), (firstPointY - secondPointY)) * 180 / Math.PI);

        }

        return Math.atan2(0, 0);

    }

    private CircleArea getCircleArea(float centerX, float centerY, float radius) {

        radius = radius + (radius / 2);
        mCircleArea.setXStart(centerX - radius);
        mCircleArea.setXEnd(centerX + radius);
        mCircleArea.setYStart(centerY - radius);
        mCircleArea.setYEnd(centerY + radius);
        return mCircleArea;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mFloatBeginOfClockLines = mRadiusBackgroundProgress - (mStrokeWithBackgroundProgress * 1.70f);
        mFloatLengthOfClockLines = mRadiusBackgroundProgress - (mStrokeWithBackgroundProgress) - (mStrokeWithCircle * 5.5f);

        mRectBackground.set(
                (float) mWidthBackgroundProgress / 2 - mRadiusBackgroundProgress + (mPaintBackgroundProgress.getStrokeWidth() / 1.2f),
                (float) mHeightBackgroundProgress / 2 - mRadiusBackgroundProgress + (mPaintBackgroundProgress.getStrokeWidth() / 1.2f),
                (float) mWidthBackgroundProgress / 2 + mRadiusBackgroundProgress - (mPaintBackgroundProgress.getStrokeWidth() / 1.2f),
                (float) mHeightBackgroundProgress / 2 + mRadiusBackgroundProgress - (mPaintBackgroundProgress.getStrokeWidth() / 1.2f));

        mRectProgress.set(
                (float) mWidthBackgroundProgress / 2 - mRadiusBackgroundProgress + (mPaintBackgroundProgress.getStrokeWidth() / 1.2f),
                (float) mHeightBackgroundProgress / 2 - mRadiusBackgroundProgress + (mPaintBackgroundProgress.getStrokeWidth() / 1.2f),
                (float) mWidthBackgroundProgress / 2 + mRadiusBackgroundProgress - (mPaintBackgroundProgress.getStrokeWidth() / 1.2f),
                (float) mHeightBackgroundProgress / 2 + mRadiusBackgroundProgress - (mPaintBackgroundProgress.getStrokeWidth() / 1.2f));


        mRectClock.set(
                ((float) (mWidthBackgroundProgress / 2) - mRadiusBackgroundProgress),
                ((float) (mHeightBackgroundProgress / 2) - mRadiusBackgroundProgress),
                ((float) (mWidthBackgroundProgress / 2) + mRadiusBackgroundProgress),
                ((float) (mHeightBackgroundProgress / 2) + mRadiusBackgroundProgress));


        xPositionText = (float) (mWidthBackgroundProgress / 2);
        yPositionText = (float) mHeightBackgroundProgress / 2 - ((mPaintCenterText.descent() + mPaintCenterText.ascent()) / 2);


        if (!isIndicator || mStringTextStatus.equals("")) {
            // center drawable
            xPositionDrawable = (float) (mWidthBackgroundProgress / 2) - (mRadiusBackgroundProgress / 2) + (mStrokeWithBackgroundProgress / 2);
            yPositionDrawable = (float) mHeightBackgroundProgress / 2;
        } else {
            xPositionDrawable = (float) (mWidthBackgroundProgress / 2) - (mRadiusBackgroundProgress / 2) + (mStrokeWithBackgroundProgress / 2);
            yPositionDrawable = (float) (mHeightBackgroundProgress / 2) + (mRadiusBackgroundProgress / 5) + ((mPaintCenterText.descent() + mPaintCenterText.ascent()) / 2);
        }

        mRectDrawable.set(
                xPositionDrawable - mIntDrawableSize,
                yPositionDrawable - mIntDrawableSize,
                xPositionDrawable + mIntDrawableSize,
                yPositionDrawable + mIntDrawableSize

        );

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int widthMeasureMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthMeasureSize = MeasureSpec.getSize(widthMeasureSpec);

        int heightMeasureMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightMeasureSize = MeasureSpec.getSize(heightMeasureSpec);


        switch (widthMeasureMode) {
            case MeasureSpec.UNSPECIFIED:
                mWidthBackgroundProgress = getDesireWidth();
                break;

            case MeasureSpec.EXACTLY:
                mWidthBackgroundProgress = widthMeasureSize + getHorizontalPadding();
                break;

            case MeasureSpec.AT_MOST:
                mWidthBackgroundProgress = Math.min(widthMeasureSize, getDesireWidth() + getHorizontalPadding());

                break;
        }


        switch (heightMeasureMode) {
            case MeasureSpec.UNSPECIFIED:
                mHeightBackgroundProgress = getDesireHeight();
                break;

            case MeasureSpec.EXACTLY:
                mHeightBackgroundProgress = heightMeasureSize + getVerticalPadding();


                break;

            case MeasureSpec.AT_MOST:
                mHeightBackgroundProgress = Math.min(heightMeasureSize, getDesireHeight() + getHorizontalPadding());

                break;
        }

        if (widthMeasureMode == MeasureSpec.EXACTLY || heightMeasureMode == MeasureSpec.EXACTLY) {
            int size = Math.min(widthMeasureSize - getHorizontalPadding(), heightMeasureSize - getVerticalPadding());
            mRadiusBackgroundProgress = (size - mPaintBackgroundProgress.getStrokeWidth()) / 2;
        } else {
            mRadiusBackgroundProgress = DEFAULT_BACKGROUND_PROGRESS_RADIUS;
        }

        int length = Math.min(mWidthBackgroundProgress, mHeightBackgroundProgress) + (int) mStrokeWithCircle;

        setMeasuredDimension(length, length);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

                /* draw center lines
       canvas.drawLine(0, (float)getHeight() / 2 , getWidth(), (float)getHeight() / 2 , mPaintTopText);
        canvas.drawLine((float) getWidth() / 2, 0, (float) getWidth() / 2, getHeight(), mPaintTopText);

         */

        float mFloatCenterXCircle = getDrawXOnBackgroundProgress(mDegreeValue, mRadiusBackgroundProgress - (mPaintBackgroundProgress.getStrokeWidth() / 1.2f), mWidthBackgroundProgress);
        float mFloatCenterYCircle = getDrawYOnBackgroundProgress(mDegreeValue, mRadiusBackgroundProgress - (mPaintBackgroundProgress.getStrokeWidth() / 1.2f), mHeightBackgroundProgress);
        //ADD CIRCLE AREA FOR DETECT TOUCH
        mCircleArea = getCircleArea(mFloatCenterXCircle, mFloatCenterYCircle, mPaintBackgroundProgress.getStrokeWidth());

        //BACKGROUNDS
        canvas.drawArc(mRectBackground, START_DEGREE, END_DEGREE, false, mPaintBackgroundProgress);


        float sweep = getSweepProgressArc();
        //PROGRESS TIME
        canvas.drawArc(mRectProgress, START_DEGREE, sweep, false, mPaintProgress);


        //TEXT
        if (isIndicator) {

            if (mStringTextStatus.equals("")) {
                canvas.drawText(mStringTextCenter, xPositionText, yPositionText, mPaintCenterText);
            } else {
                canvas.drawText(mStringTextCenter, (float) mWidthBackgroundProgress / 2, ((float) mHeightBackgroundProgress / 2) + mRadiusBackgroundProgress / 5, mPaintCenterText);
                canvas.drawText(mStringTextStatus, (float) mWidthBackgroundProgress / 2, ((float) mHeightBackgroundProgress / 2) - mRadiusBackgroundProgress / 5, mPaintTopText);
            }
        } else {

            canvas.drawText(mStringTextCenter, xPositionText, yPositionText, mPaintCenterText);

        }

        if (mBitmap != null)
            canvas.drawBitmap(mBitmap, null, mRectDrawable, mPaintDrawable);


        //CIRCLE VALUE
        if (!isIndicator)
            fillCircleStrokeBorder(canvas, mFloatCenterXCircle, mFloatCenterYCircle, mPaintBackgroundProgress.getStrokeWidth() / 2f, Color.WHITE, mStrokeWithCircle, mColorValue, mPaintValue);


        {
            //LINES

            float angel = START_DEGREE - getDegreePerHand();

            float x1, y1, x2, y2;

            for (int i = -1; i < getLeftValue(); i++) {

                angel += getDegreePerHand();

                if (i % 2 != 0) {
                    x1 = (float) (Math.cos(Math.toRadians(angel))) * (mFloatBeginOfClockLines - 0) + (float) (mWidthBackgroundProgress / 2);
                    y1 = (float) (Math.sin(Math.toRadians(angel))) * (mFloatBeginOfClockLines - 0) + (float) (mHeightBackgroundProgress / 2);
                    x2 = (float) (Math.cos(Math.toRadians(angel))) * (mFloatLengthOfClockLines - 10) + (float) (mWidthBackgroundProgress / 2);
                    y2 = (float) (Math.sin(Math.toRadians(angel))) * (mFloatLengthOfClockLines - 10) + (float) (mHeightBackgroundProgress / 2);

                } else {
                    x1 = (float) (Math.cos(Math.toRadians(angel))) * (mFloatBeginOfClockLines - 10) + (float) (mWidthBackgroundProgress / 2);
                    y1 = (float) (Math.sin(Math.toRadians(angel))) * (mFloatBeginOfClockLines - 10) + (float) (mHeightBackgroundProgress / 2);
                    x2 = (float) (Math.cos(Math.toRadians(angel))) * (mFloatLengthOfClockLines - 0) + (float) (mWidthBackgroundProgress / 2);
                    y2 = (float) (Math.sin(Math.toRadians(angel))) * (mFloatLengthOfClockLines - 0) + (float) (mHeightBackgroundProgress / 2);
                }

                float current = mFloatValue - 0.5f - mIntegerMinValue;


                if (i < current)
                    canvas.drawLine(x1, y1, x2, y2, mPaintHandClockColored);
                else
                    canvas.drawLine(x1, y1, x2, y2, mPaintHandClock);

            }

        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (isIndicator)
            return false;

        int x = (int) event.getX();
        int y = (int) event.getY();

        double angel;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                boolean found = (x >= mCircleArea.getXStart()
                        && x <= mCircleArea.getXEnd()
                        && y >= mCircleArea.getYStart()
                        && y <= mCircleArea.getYEnd());

                if (found) {
                    accessMoving = true;
                    break;
                } else {
                    accessMoving = false;
                }


                break;
            case MotionEvent.ACTION_MOVE:

                if (accessMoving) {

                    angel = getAngleFromPoint((double) mWidthBackgroundProgress / 2, (double) mHeightBackgroundProgress / 2, (double) x, (double) y) - 90;


                    if (angel > (START_DEGREE - 360) && angel < (END_DEGREE - 90)) {
                        mDegreeValue = (float) angel;

                        setCurrentValue(getValueFromAngel(mDegreeValue));

                        int val = Math.round(getValueFromAngel(mDegreeValue));
                        setCurrentTemp(val);

                        if (onSeekCirclesListener != null)
                            onSeekCirclesListener.onSeekChange(val);

                        invalidate();
                    }

                }
                break;
            case MotionEvent.ACTION_UP:

                if (onSeekCirclesListener != null)
                    onSeekCirclesListener.onSeekComplete(Math.round(getValueFromAngel(mDegreeValue)));

                accessMoving = false;

                performClick();
                break;
        }

        return true;
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    private static class CircleArea {

        private float xStart;
        private float xEnd;

        private float yStart;
        private float yEnd;

        float getXStart() {
            return xStart;
        }

        void setXStart(float xStart) {
            this.xStart = xStart;
        }

        float getXEnd() {
            return xEnd;
        }

        void setXEnd(float xEnd) {
            this.xEnd = xEnd;
        }

        float getYStart() {
            return yStart;
        }

        void setYStart(float yStart) {
            this.yStart = yStart;
        }

        float getYEnd() {
            return yEnd;
        }

        void setYEnd(float yEnd) {
            this.yEnd = yEnd;
        }
    }

    public interface OnSeekChangeListener {
        void onSeekChange(int value);

        void onSeekComplete(int value);
    }
}