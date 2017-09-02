package sunhdj.com.mivideoloadingview;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by huangdaju on 17/9/1.
 */

public class MiVideoLoadingView extends View {

    private int mWidth, mHeight;
    private int cvX, cvY;
    private int edge = 100;
    private Paint mPaint;
    private Path mPath;
    private Triangle[] mTriangles = new Triangle[4];
    private STATUS currentStatus = STATUS.MID_LOADING;
    private ValueAnimator mValueAnimator;

    enum STATUS {
        MID_LOADING,
        FIRST_LOADING,
        SECOND_LOADING,
        THIRD_LOADING,
        LOADING_COMPLETE,
        THIRD_DISMISS,
        SECOND_DISMISS,
        FIRST_DISMISS,
        MID_DISMISS
    }

    public MiVideoLoadingView(Context context) {
        super(context);
    }

    public MiVideoLoadingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initPaint();
    }

    public MiVideoLoadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    private void initPaint() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        mPath = new Path();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < mTriangles.length; i++) {
            Triangle tr = mTriangles[i];
            mPath.reset();
            mPath.moveTo(tr.startX, tr.startY);

            mPath.lineTo(tr.currentX1, tr.currentY1);
            mPath.lineTo(tr.currentX2, tr.currentY2);
            mPath.close();

            mPaint.setColor(Color.parseColor(tr.color));
            canvas.drawPath(mPath, mPaint);
            if (currentStatus == STATUS.MID_LOADING) {
                break;
            }
        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth();
        cvX = mWidth >> 1;
        mHeight = getMeasuredHeight();
        cvY = mHeight >> 1;
        initTriange();
    }

    public void startTranglesAnimation() {
        initTriange();
        if (mValueAnimator != null && mValueAnimator.isRunning()) {
            mValueAnimator.cancel();
        }

        mValueAnimator = ValueAnimator.ofFloat(0, 1);
        mValueAnimator.setDuration(300);
        mValueAnimator.setRepeatCount(-1);
        mValueAnimator.setRepeatMode(ValueAnimator.RESTART);
        mValueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                //当上一个动画状态执行完之后进入下一个阶段。
                if (currentStatus == STATUS.MID_LOADING) {
                    currentStatus = STATUS.FIRST_LOADING;
                } else if (currentStatus == STATUS.FIRST_LOADING) {
                    currentStatus = STATUS.SECOND_LOADING;
                } else if (currentStatus == STATUS.SECOND_LOADING) {
                    currentStatus = STATUS.THIRD_LOADING;
                } else if (currentStatus == STATUS.THIRD_LOADING) {
                    currentStatus = STATUS.LOADING_COMPLETE;
                    reverseTriangleStart();
                } else if (currentStatus == STATUS.LOADING_COMPLETE) {
                    currentStatus = STATUS.THIRD_DISMISS;
                } else if (currentStatus == STATUS.THIRD_DISMISS) {
                    currentStatus = STATUS.FIRST_DISMISS;
                } else if (currentStatus == STATUS.FIRST_DISMISS) {
                    currentStatus = STATUS.SECOND_DISMISS;
                } else if (currentStatus == STATUS.SECOND_DISMISS) {
                    currentStatus = STATUS.MID_DISMISS;
                } else if (currentStatus == STATUS.MID_DISMISS) {
                    Log.e("wangjinfeng", "onAnimationRepeat");
                    currentStatus = STATUS.MID_LOADING;
                    reverseTriangleStart();
                }
            }
        });

        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                //或者目前的插值(0-1)
                float fraction = animation.getAnimatedFraction();
                //如果目前的动画是消失状态，则插值正好是反过来的，是1-0，所以需要用1-fraction
                if (currentStatus == STATUS.FIRST_DISMISS || currentStatus == STATUS.SECOND_DISMISS || currentStatus == STATUS.THIRD_DISMISS || currentStatus == STATUS.MID_DISMISS) {
                    fraction = 1 - fraction;
                }
                //根据目前执行的状态，取出对应的需要处理的三角形
                Triangle triangleView = mTriangles[0];
                if (currentStatus == STATUS.MID_LOADING || currentStatus == STATUS.MID_DISMISS) {
                    triangleView = mTriangles[0];
                } else if (currentStatus == STATUS.FIRST_LOADING || currentStatus == STATUS.FIRST_DISMISS) {
                    triangleView = mTriangles[1];
                } else if (currentStatus == STATUS.SECOND_LOADING || currentStatus == STATUS.SECOND_DISMISS) {
                    triangleView = mTriangles[2];
                } else if (currentStatus == STATUS.THIRD_LOADING || currentStatus == STATUS.THIRD_DISMISS) {
                    triangleView = mTriangles[3];
                } else if (currentStatus == STATUS.LOADING_COMPLETE) {
                    //如果是LOADING_COMPLETE状态的话，此次动画效果保持不变
                    invalidate();
                    return;
                }
                //这里是三角形变化的过程，计算目前current的坐标应当处在什么位置上
                //当fration为0的时候，current的坐标为start位置，当fratcion为1的时候，current的坐标是end位置
                triangleView.currentX1 = (int) (triangleView.startX + fraction * (triangleView.endX1 - triangleView.startX));
                triangleView.currentY1 = (int) (triangleView.startY + fraction * (triangleView.endY1 - triangleView.startY));
                triangleView.currentX2 = (int) (triangleView.startX + fraction * (triangleView.endX2 - triangleView.startX));
                triangleView.currentY2 = (int) (triangleView.startY + fraction * (triangleView.endY2 - triangleView.startY));
                invalidate();
            }
        });

        mValueAnimator.start();
    }


    /**
     * 初始化三角形
     */
    private void initTriange() {
        currentStatus = STATUS.MID_LOADING;
        Triangle triangle = new Triangle();

        double offset = Math.sqrt(Math.pow(edge, 2) - Math.pow(edge / 2, 2));
        triangle.startX = (int) (cvX + offset / 2);
        triangle.startY = (int) (cvY + edge / 2);
        triangle.endX1 = triangle.startX;
        triangle.endY1 = (int) (cvY - edge / 2);
        triangle.endX2 = (int) (cvX - offset / 2);
        triangle.endY2 = cvY;

        triangle.currentX1 = triangle.startX;
        triangle.currentY1 = triangle.startY;
        triangle.currentX2 = triangle.startX;
        triangle.currentY2 = triangle.startY;

        triangle.color = "#be8cd5";

        mTriangles[0] = triangle;

        Triangle firstTriangle = new Triangle();
        firstTriangle.startX = triangle.endX2;
        firstTriangle.startY = triangle.endY2;
        firstTriangle.endX1 = triangle.endX1;
        firstTriangle.endY1 = triangle.endY1;
        firstTriangle.endX2 = firstTriangle.startX;
        firstTriangle.endY2 = firstTriangle.startY - edge;
        firstTriangle.color = "#fcb131";
        mTriangles[1] = firstTriangle;

        Triangle secondTriange = new Triangle();
        secondTriange.startX = triangle.endX1;
        secondTriange.startY = triangle.endY1;
        secondTriange.endX1 = triangle.startX;
        secondTriange.endY1 = triangle.startY;
        secondTriange.endX2 = (int) (triangle.startX + offset);
        secondTriange.endY2 = triangle.startY - edge / 2;
        secondTriange.color = "#67c6ca";
        mTriangles[2] = secondTriange;

        Triangle thirdTriange = new Triangle();
        thirdTriange.startX = triangle.startX;
        thirdTriange.startY = triangle.startY;
        thirdTriange.endX1 = triangle.endX2;
        thirdTriange.endY1 = triangle.endY2;
        thirdTriange.endX2 = triangle.endX2;
        thirdTriange.endY2 = triangle.startY + edge / 2;
        thirdTriange.color = "#eb7583";
        mTriangles[3] = thirdTriange;
    }

    private void reverseTriangleStart() {
        for (int i = 0; i < mTriangles.length; i++) {
            int startX = mTriangles[i].startX;
            int startY = mTriangles[i].startY;
            mTriangles[i].startX = mTriangles[i].endX1;
            mTriangles[i].startY = mTriangles[i].endY1;
            mTriangles[i].endX1 = startX;
            mTriangles[i].endY1 = startY;
            mTriangles[i].currentX1 = mTriangles[i].endX1;
            mTriangles[i].currentY1 = mTriangles[i].endY1;
        }
    }


}

