package sunhdj.com.mivideoloadingview;


import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by huangdaju on 2017/9/13.
 */

public class WaveLoadingView extends View {

    private Paint mPaint;
    private Path mPath;
    private Line[] lines = new Line[5];
    private int cvX, cvY;
    private int space = 20;
    private int lineH = 10;
    private STATUS currentStatus = STATUS.FIRST_LOADING;
    private ValueAnimator mValueAnimator;

    enum STATUS {
        FIRST_LOADING,
        SECOND_LOADING,
        THIRD_LOADING,
        FOURTH_LOADING,
        FIRTH_LOADING,
        LOADING_COMPLETE,
        FIRTH_DISMISS,
        FOURTH_DISMISS,
        THIRD_DISMISS,
        SECOND_DISMISS,
        FIRST_DISMISS
    }

    public WaveLoadingView(Context context) {
        super(context);
    }

    public WaveLoadingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initPaint();
    }

    public WaveLoadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void initPaint() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeWidth(20);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < lines.length; i++) {
            Line line = lines[i];
            canvas.drawLine(line.startX, line.startY, line.endX, line.endY, mPaint);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        cvX = getMeasuredWidth() >> 1;
        cvY = getMeasuredHeight() >> 1;
        initLines();
    }

    private void initLines() {
        Line lin1 = new Line(20, cvX, cvY, cvX, cvY - lineH, "#cccccc");
        Line lin2 = new Line(20, cvX + 2 * space, cvY, cvX + 2 * space, cvY - lineH, "#cccccc");
        Line lin3 = new Line(20, cvX + 4 * space, cvY, cvX + 4 * space, cvY - lineH, "#cccccc");
        Line lin4 = new Line(20, cvX + 6 * space, cvY, cvX + 6 * space, cvY - lineH, "#cccccc");
        Line lin5 = new Line(20, cvX + 8 * space, cvY, cvX + 8 * space, cvY - lineH, "#cccccc");
        lines[0] = lin1;
        lines[1] = lin2;
        lines[2] = lin3;
        lines[3] = lin4;
        lines[4] = lin5;
    }


    public void startLinesAnimation() {
        initLines();
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
                if (currentStatus == STATUS.FIRST_LOADING) {
                    currentStatus = STATUS.SECOND_LOADING;
                } else if (currentStatus == STATUS.SECOND_LOADING) {
                    currentStatus = STATUS.THIRD_LOADING;
                } else if (currentStatus == STATUS.THIRD_LOADING) {
                    currentStatus = STATUS.FOURTH_LOADING;
                } else if (currentStatus == STATUS.FOURTH_LOADING) {
                    currentStatus = STATUS.FIRTH_LOADING;
                }else if (currentStatus == STATUS.FIRTH_LOADING) {
                    currentStatus = STATUS.LOADING_COMPLETE;
                    reverseLineStart();
                } else if (currentStatus == STATUS.LOADING_COMPLETE) {
                    currentStatus = STATUS.FIRTH_DISMISS;
                } else if (currentStatus == STATUS.FIRTH_DISMISS) {
                    currentStatus = STATUS.FOURTH_DISMISS;
                } else if (currentStatus == STATUS.FOURTH_DISMISS) {
                    currentStatus = STATUS.THIRD_DISMISS;
                } else if (currentStatus == STATUS.THIRD_DISMISS) {
                    currentStatus = STATUS.SECOND_DISMISS;
                } else if (currentStatus == STATUS.SECOND_DISMISS) {
                    currentStatus = STATUS.FIRST_DISMISS;
                } else if (currentStatus == STATUS.FIRST_DISMISS) {
                    Log.e("wangjinfeng", "onAnimationRepeat");
                    currentStatus = STATUS.FIRST_LOADING;
                    reverseLineStart();
                }
            }

        });



        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                //目前的插值(0-1)
                float fraction = animation.getAnimatedFraction();
                if (currentStatus == STATUS.FIRST_DISMISS || currentStatus == STATUS.SECOND_DISMISS || currentStatus == STATUS.THIRD_DISMISS || currentStatus == STATUS.FOURTH_DISMISS
                        || currentStatus == STATUS.FIRTH_DISMISS) {
                    fraction = fraction - 1 ;
                }
                //根据目前执行的状态，取出对应的需要处理的直线
                Line line = lines[0];
                if (currentStatus == STATUS.FIRST_LOADING || currentStatus == STATUS.FIRST_DISMISS) {
                    line = lines[0];
                } else if (currentStatus == STATUS.SECOND_LOADING || currentStatus == STATUS.SECOND_DISMISS) {
                    line = lines[1];
                } else if (currentStatus == STATUS.THIRD_LOADING || currentStatus == STATUS.THIRD_DISMISS) {
                    line = lines[2];
                } else if (currentStatus == STATUS.FOURTH_LOADING || currentStatus == STATUS.FOURTH_DISMISS) {
                    line = lines[3];
                }else if (currentStatus == STATUS.FIRTH_LOADING || currentStatus == STATUS.FIRTH_DISMISS) {
                    line = lines[4];
                } else if (currentStatus == STATUS.LOADING_COMPLETE) {
                    //如果是LOADING_COMPLETE状态的话，此次动画效果保持不变
                    invalidate();
                    return;
                }
                //这里是直线变化的过程，计算目前current的坐标应当处在什么位置上
                //当fration为0的时候，current的坐标为start位置，当fratcion为1的时候，current的坐标是end位置
                line.startY = line.startY - (int) (fraction * lineH);
                line.endY = line.endY + (int) (fraction * lineH);
                invalidate();
            }

        });

        mValueAnimator.start();
    }

    private void reverseLineStart() {
//        for (int i = 0; i < lines.length; i++) {
//            int startX = lines[i].startX;
//            int startY = lines[i].startY;
//            mTriangles[i].startX = mTriangles[i].endX1;
//            mTriangles[i].startY = mTriangles[i].endY1;
//            mTriangles[i].endX1 = startX;
//            mTriangles[i].endY1 = startY;
//            mTriangles[i].currentX1 = mTriangles[i].endX1;
//            mTriangles[i].currentY1 = mTriangles[i].endY1;
//        }
    }


}
