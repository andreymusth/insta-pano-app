package com.tzkt.andrey.instapano.limiter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.tzkt.andrey.instapano.utils.BitmapUtils;

import java.util.ArrayList;

public class LimiterView extends View {


    // double tap area
    long mStartTime;
    static final int MAX_DURATION = 150;
    //

    private static final float RADIUS = 60f;
    private static final int HEIGHT = 50;
    private static final int WIDTH = 10;

    private static final float LOW_INSTAGRAM_RATIO = 1080f / 1350f;
    private static final float HIGH_INSTAGRAM_RATIO = 1080f / 556f;

    private PointF mCurrent;

    private float mRatio;

    private OnDoubleTapListener mListener;

    // edges of limiter
    private PointF mLeftTop, mLeftBottom, mRightTop, mRightBottom;

    // actual edges of image
    private float mLeft, mRight, mTop, mBottom;

    // variables for remember deltas between touch event and actual edges
    private float mDeltaLeftX, mDeltaRightX, mDeltaTopY, mDeltaBottomY;

    private Paint mPaint, mBackgroundPaint;

    private Bitmap mScaledImage;
    private PointF mTopLeftEdge;

    private int mPartsQuantity;
    private ArrayList<Float> mLinesCoords;

    private int mCurrentCircle = 0;

    public LimiterView(Context context) {
        super(context);

        initPaints();
    }

    public LimiterView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        initPaints();
    }

    public LimiterView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initPaints();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        mCurrent.set(event.getX(), event.getY());

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                calculateDeltas();

                // if action is double tap
                if (System.currentTimeMillis() - mStartTime <= MAX_DURATION) {
                    mListener.onDoubleTap();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (ifEventInCircle()) {
                    setRestrictions();
                    setActualCoordinates();
                } else if (ifEventInMovableArea()) {
                    moveRectangle();
                    setRestrictions();
                }
                break;
            case MotionEvent.ACTION_UP:
                mStartTime = System.currentTimeMillis();
            case MotionEvent.ACTION_CANCEL:
                break;
        }
        invalidate();
        return true;
    }

    private void setActualColors(){
        if (restrictedPieceFitsInstagram()) {
            setPaintsColors(Colors.GREEN, Colors.GREEN_TRANSPARENT);
        } else {
            setPaintsColors(Colors.RED, Colors.RED_TRANSPARENT);
        }
    }
    
    private boolean restrictedPieceFitsInstagram(){
        mRatio = (float) (mLinesCoords.get(0) - mLeftTop.x) / (mLeftBottom.y - mLeftTop.y);
        return LOW_INSTAGRAM_RATIO <= mRatio && mRatio <= HIGH_INSTAGRAM_RATIO;
    }

    public void setPartsQuantity(int partsQuantity) {
        mPartsQuantity = partsQuantity;
        refillLinesCoords();
        invalidate();
    }

    private void moveRectangle(){

        mLeftTop.set(mCurrent.x - mDeltaLeftX, mCurrent.y - mDeltaTopY);
        mRightTop.set(mCurrent.x + mDeltaRightX, mLeftTop.y);
        mRightBottom.set(mRightTop.x, mCurrent.y + mDeltaBottomY);
        mLeftBottom.set(mLeftTop.x, mRightBottom.y);

    }

    private void calculateDeltas() {

        // variable for left delta between mCurrent and Left edge of restricted area

        mDeltaLeftX = mCurrent.x - mLeftTop.x;
        mDeltaRightX = mRightTop.x - mCurrent.x;

        mDeltaTopY = mCurrent.y - mLeftTop.y;
        mDeltaBottomY = mLeftBottom.y - mCurrent.y;
    }

    private void setRestrictions(){

        if (mCurrent.x >= mRight) {
            mCurrent.set(mRight, mCurrent.y);
        }
        if (mCurrent.x < mLeft) {
            mCurrent.set(mLeft, mCurrent.y);
        }
        if (mCurrent.y >= mBottom) {
            mCurrent.set(mCurrent.x, mBottom);
        }
        if (mCurrent.y <= mTop) {
            mCurrent.set(mCurrent.x, mTop);
        }

        if (mCurrentCircle == 1) {
            if (mRightTop.x - mLeftTop.x <= 3 * HEIGHT) {
                mCurrent.set(mLeftTop.x - 20, mCurrent.y);
            }
            if (mLeftBottom.y - mLeftTop.y <= 2 * HEIGHT) {
                mCurrent.set(mCurrent.x, mLeftTop.y - 20);
            }
        }

        if (mCurrentCircle == 1) {
            if (mRightTop.x - mLeftTop.x <= 3 * HEIGHT) {
                mCurrent.set(mLeftTop.x - 20, mCurrent.y);
            }
            if (mLeftBottom.y - mLeftTop.y <= 2 * HEIGHT) {
                mCurrent.set(mCurrent.x, mLeftTop.y - 20);
            }
        }
        if (mCurrentCircle == 2) {
            if (mRightTop.x - mLeftTop.x <= 3 * HEIGHT) {
                mCurrent.set(mRightTop.x + 20, mCurrent.y);
            }
            if (mRightBottom.y - mRightTop.y <= 2 * HEIGHT) {
                mCurrent.set(mCurrent.x, mRightTop.y - 20);
            }
        }
        if (mCurrentCircle == 3) {
            if (mRightBottom.x - mLeftBottom.x <= 3 * HEIGHT) {
                mCurrent.set(mRightBottom.x + 20, mCurrent.y);
            }
            if (mRightBottom.y - mRightTop.y <= 2 * HEIGHT) {
                mCurrent.set(mCurrent.x, mRightBottom.y + 20);
            }
        }
        if (mCurrentCircle == 4) {
            if (mRightBottom.x - mLeftBottom.x <= 3 * HEIGHT) {
                mCurrent.set(mLeftBottom.x - 20, mCurrent.y);
            }
            if (mLeftBottom.y - mLeftTop.y <= 2 * HEIGHT) {
                mCurrent.set(mCurrent.x, mLeftBottom.y + 20);
            }
        }

        if (mLeftTop.x < mLeft) {
            setCornersCoords(mLeft, mLeft + mDeltaLeftX + mDeltaRightX, mLeftTop.y, mLeftBottom.y);
        }

        if (mLeftTop.y < mTop) {
            setCornersCoords(mLeftTop.x, mRightTop.x, mTop, mTop + mDeltaTopY + mDeltaBottomY);
        }

        if (mRightTop.x > mRight) {
            setCornersCoords(mRight - mDeltaLeftX - mDeltaRightX, mRight, mRightTop.y, mRightBottom.y);
        }

        if (mRightBottom.y > mBottom) {
            setCornersCoords(mLeftTop.x, mRightTop.x, mBottom - mDeltaBottomY - mDeltaTopY, mBottom);
        }
    }


    private void setCornersCoords(float left, float right, float top, float bottom) {
        mLeftTop.set(left, top);
        mRightTop.set(right, top);
        mRightBottom.set(right, bottom);
        mLeftBottom.set(left, bottom);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        redrawView(canvas);
    }

    private void redrawView(Canvas canvas) {

        setActualColors();
        // drawing bitmap
        canvas.drawBitmap(mScaledImage, mTopLeftEdge.x, mTopLeftEdge.y, null);

        // top left corner
        canvas.drawRect(mLeftTop.x, mLeftTop.y, mLeftTop.x + WIDTH, mLeftTop.y + HEIGHT, mPaint);
        canvas.drawRect(mLeftTop.x, mLeftTop.y, mLeftTop.x + HEIGHT, mLeftTop.y + WIDTH, mPaint);

        // bottom left corner
        canvas.drawRect(mLeftBottom.x, mLeftBottom.y - HEIGHT, mLeftBottom.x + WIDTH, mLeftBottom.y, mPaint);
        canvas.drawRect(mLeftBottom.x, mLeftBottom.y - WIDTH, mLeftBottom.x + HEIGHT, mLeftBottom.y, mPaint);

        //top right corner
        canvas.drawRect(mRightTop.x - WIDTH, mRightTop.y, mRightTop.x , mRightTop.y + HEIGHT, mPaint);
        canvas.drawRect(mRightTop.x - HEIGHT, mRightTop.y, mRightTop.x, mRightTop.y + WIDTH, mPaint);

        // bottom right corner
        canvas.drawRect(mRightBottom.x - WIDTH, mRightBottom.y - HEIGHT, mRightBottom.x, mRightBottom.y, mPaint);
        canvas.drawRect(mRightBottom.x - HEIGHT, mRightBottom.y - WIDTH, mRightBottom.x, mRightBottom.y, mPaint);

        // drawing fainted area
        canvas.drawRect(mLeft, mTop, mRight, mLeftTop.y, mBackgroundPaint);
        canvas.drawRect(mLeft, mLeftBottom.y, mRight, mBottom, mBackgroundPaint);
        canvas.drawRect(mLeft, mLeftTop.y, mLeftTop.x, mLeftBottom.y, mBackgroundPaint);
        canvas.drawRect(mRightTop.x, mRightTop.y, mRight, mRightBottom.y, mBackgroundPaint);

        canvas.drawLine(mLeftTop.x, mLeftTop.y, mRightTop.x, mRightTop.y, mPaint);
        canvas.drawLine(mLeftTop.x, mLeftTop.y, mLeftBottom.x, mLeftBottom.y, mPaint);
        canvas.drawLine(mRightBottom.x, mRightBottom.y, mRightTop.x, mRightTop.y, mPaint);
        canvas.drawLine(mRightBottom.x, mRightBottom.y, mLeftBottom.x, mLeftBottom.y, mPaint);

        // drawing parts lines
        refillLinesCoords();
        for (float x: mLinesCoords) {
            canvas.drawLine(x, mLeftTop.y, x, mLeftBottom.y, mPaint);
        };

    }

    private void refillLinesCoords(){

        mLinesCoords.clear();

        float partWidth = (mRightTop.x - mLeftTop.x) / mPartsQuantity;
        for (int i = 1; i < mPartsQuantity; i++) {
            // add x
            mLinesCoords.add(mLeftTop.x + partWidth * i);
        }

    }

    private void setActualCoordinates(){

        switch (mCurrentCircle) {
            case 1:

                mLeftTop.set(mCurrent.x, mCurrent.y);
                mLeftBottom.set(mCurrent.x, mLeftBottom.y);
                mRightTop.set(mRightTop.x, mCurrent.y);
                break;

            case 2:
                mRightTop.set(mCurrent.x, mCurrent.y);
                mLeftTop.set(mLeftTop.x, mCurrent.y);
                mRightBottom.set(mCurrent.x, mRightBottom.y);
                break;

            case 3:
                mRightBottom.set(mCurrent.x, mCurrent.y);
                mRightTop.set(mCurrent.x, mRightTop.y);
                mLeftBottom.set(mLeftBottom.x, mCurrent.y);
                break;

            case 4:
                mLeftBottom.set(mCurrent.x, mCurrent.y);
                mLeftTop.set(mCurrent.x, mLeftTop.y);
                mRightBottom.set(mRightBottom.x, mCurrent.y);
                break;

        }
    }

    public void init(Bitmap bitmap, int partsQuantity, int top, int bottom, int left, int right){

        float maxSize = 0;
        boolean stretchHeight = false;

        float containerHeight = bottom - top;
        float containerWidth = right - left;

        if (containerHeight / containerWidth > (float) bitmap.getHeight() / bitmap.getWidth()) {
            // stretch width
            maxSize = containerWidth;
        } else {
           // stretch height
            maxSize = containerHeight;
            stretchHeight = true;
        }

        mScaledImage  = BitmapUtils.getScaledImage(bitmap, maxSize, stretchHeight);

        // calculating top left edge of bitmap
        mTopLeftEdge = new PointF(((right - left) - mScaledImage.getWidth())/2, ((bottom - top) - mScaledImage.getHeight())/ 2);

        mCurrent = new PointF();

        mLeft = mTopLeftEdge.x;
        mTop = mTopLeftEdge.y;
        mBottom = mTopLeftEdge.y + mScaledImage.getHeight();
        mRight = mTopLeftEdge.x + mScaledImage.getWidth();

        mLeftTop = new PointF(mLeft, mTop);
        mLeftBottom = new PointF(mLeft, mBottom);

        mRightTop = new PointF(mRight, mTop);
        mRightBottom = new PointF(mRight, mBottom);

        mPartsQuantity = partsQuantity;
        mLinesCoords = new ArrayList<>();
        refillLinesCoords();
    }

    private boolean ifEventInCircle() {
        if (Math.sqrt(Math.pow((mCurrent.x - mLeftTop.x), 2) + Math.pow((mCurrent.y - mLeftTop.y), 2)) < RADIUS) {
            mCurrentCircle = 1;
            return true;
        } else if (Math.sqrt(Math.pow((mCurrent.x - mRightTop.x), 2) + Math.pow((mCurrent.y - mRightTop.y), 2)) < RADIUS) {
            mCurrentCircle = 2;
            return true;
        } else if (Math.sqrt(Math.pow((mCurrent.x - mRightBottom.x), 2) + Math.pow((mCurrent.y - mRightBottom.y), 2)) < RADIUS) {
            mCurrentCircle = 3;
            return true;
        } else if (Math.sqrt(Math.pow((mCurrent.x - mLeftBottom.x), 2) + Math.pow((mCurrent.y - mLeftBottom.y), 2)) < RADIUS) {
            mCurrentCircle = 4;
            return true;
        }
        return false;
    }

    private boolean ifEventInMovableArea() {
        if (mCurrent.x > mLeftTop.x && mCurrent.x < mRightTop.x
            && mCurrent.y > mLeftTop.y && mCurrent.y < mLeftBottom.y) {
            return true;
        }

        return false;
    }

    private void initPaints(){

        mPaint  = new Paint();
        mPaint.setStrokeWidth(2.5f);
        mBackgroundPaint = new Paint();

        setPaintsColors(Colors.RED, Colors.RED_TRANSPARENT);

    }

    public void setPaintsColors(int color1, int color2) {
        mPaint.setColor(color1);
        mBackgroundPaint.setColor(color2);
    }

    public interface OnDoubleTapListener {
        public void onDoubleTap();
    }

    public Bitmap getScaledImage() {
        return mScaledImage;
    }

    public PointF getLeftTop() {
        return mLeftTop;
    }

    public float getTopEdgeOfImage() {
        return mTop;
    }

    public float getLeftEdgeOfImage() {
        return mLeft;
    }

    public PointF getLeftBottom() {
        return mLeftBottom;
    }

    public PointF getRightTop() {
        return mRightTop;
    }

    public void setOnDoubleTapListener(OnDoubleTapListener listener) {
        mListener = listener;
    }

    public int getPaintColor(){
        return mPaint.getColor();
    }
}
