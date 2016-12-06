package hcil.umd.edu.cmsc434animationtimer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.View;

import java.text.MessageFormat;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by jonf on 12/1/2016.
 */

public class AnimationView extends View {

    private Paint _paintText = new Paint();
    private Paint _paintBall = new Paint();
    private float _ballRadius = 40f;
    private float _xBallLocation = 0;
    private int _desiredFramesPerSecond = 80;

    // This is for measuring frame rate, you can ignore
    private float _actualFramesPerSecond = -1;
    private long _startTime = -1;
    private int _frameCnt = 0;

    //https://developer.android.com/reference/java/util/Timer.html
    private Timer _timer = new Timer("AnimationView");

    public AnimationView(Context context) {
        super(context);
        init(null, null, 0);
    }

    public AnimationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public AnimationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }


    public void init(Context context, AttributeSet attrs, int defStyleAttr) {
        _paintBall.setStyle(Paint.Style.FILL);
        _paintBall.setAntiAlias(true);
        _paintBall.setColor(Color.RED);

        _paintText.setColor(Color.BLACK);
        _paintText.setTextSize(40f);

        _xBallLocation = _ballRadius;

        // https://developer.android.com/referecance/java/util/Timer.html#scheduleAtFixedRate(java.util.TimerTask, long, long)
        // 60 fps will have period of 16.67
        // 40 fps will have period of 25
        long periodInMillis = 1000 / _desiredFramesPerSecond;
        _timer.schedule(new FramerateIndependentAnimationTimerTask(this), 0, periodInMillis);
    }

    @Override
    public void onDraw(Canvas canvas) {
        // start time helps measure fps calculations
        if(_startTime == -1) {
            _startTime = SystemClock.elapsedRealtime();
        }
        _frameCnt++;

        super.onDraw(canvas);
        float xCircle = _xBallLocation;
        float yCircle = getHeight() / 2f;
        canvas.drawCircle(xCircle, yCircle, _ballRadius, _paintBall);

        // The code below is about measuring and printing out fps calculations. You can ignore
        long endTime = SystemClock.elapsedRealtime();
        long elapsedTimeInMs = endTime - _startTime;
        if(elapsedTimeInMs > 1000){
            _actualFramesPerSecond = _frameCnt / (elapsedTimeInMs/1000f);
            _frameCnt = 0;
            _startTime = -1;
        }
        //MessageFormat: https://developer.android.com/reference/java/text/MessageFormat.html
        canvas.drawText(MessageFormat.format("fps: {0,number,#.#}", _actualFramesPerSecond), 5, 40, _paintText);
    }

    class AnimationTimerTask extends TimerTask{
        private AnimationView _animationView;
        private float _step = 10f;

        public AnimationTimerTask(AnimationView animationView){
            _animationView = animationView;
        }

        @Override
        public void run() {
            _xBallLocation += _step;

            // check ball boundary
            if(_xBallLocation + _ballRadius >= getWidth() ||
                    (_xBallLocation - _ballRadius <= 0 && _step < 0)){
                // switch directions
                _step = -1 * _step;
            }

             _animationView.postInvalidate();
        }
    }

    //TimerTask: https://developer.android.com/reference/java/util/TimerTask.html
    class FramerateIndependentAnimationTimerTask extends TimerTask{

        private AnimationView _animationView;
        private float _stepPixelsPerSecond = 200;
        private long _lastTimeInMs = -1;

        public FramerateIndependentAnimationTimerTask(AnimationView animationView){
            _animationView = animationView;
        }

        @Override
        public void run() {
            if(_lastTimeInMs == -1){
                _lastTimeInMs = SystemClock.elapsedRealtime();
            }
            long curTimeInMs = SystemClock.elapsedRealtime();

            _xBallLocation += _stepPixelsPerSecond * (curTimeInMs - _lastTimeInMs)/1000f;

            // check ball boundary
            if(_xBallLocation + _ballRadius >= getWidth() ||
                    (_xBallLocation - _ballRadius <= 0 && _stepPixelsPerSecond < 0)){
                // switch directions
                _stepPixelsPerSecond = -1 * _stepPixelsPerSecond;
            }

            _animationView.postInvalidate();
            _lastTimeInMs = curTimeInMs;
        }
    }

}
