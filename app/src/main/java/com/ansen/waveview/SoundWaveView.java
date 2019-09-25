package com.ansen.waveview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ansen on 2015/12/16 20:06.
 *
 * @E-mail: ansen360@126.com
 * @Blog: "http://blog.csdn.net/qq_25804863"
 * @Github: "https://github.com/ansen360"
 * @PROJECT_NAME: SoundWaveView
 * @PACKAGE_NAME: com.ansen.soundwaveview
 * @Description: TODO
 */
public class SoundWaveView extends View {

    private int voiceLineColor;
    private Paint paintVoicLine;
    private int sensibility = 4;    // 灵敏度
    private float maxVolume = 100;
    private float translateX = 0;
    private boolean isSet = false;

    private float amplitude = 1; // 振幅
    private float volume = 10;
    private int fineness = 1;
    private float targetVolume = 1;

    private long lastTime = 0;
    private int lineSpeed = 90;

    List<Path> paths = null;

    public SoundWaveView(Context context) {
        super(context);
    }

    public SoundWaveView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAtts(context, attrs);
    }

    public SoundWaveView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAtts(context, attrs);
    }

    private void initAtts(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.voiceView);
        voiceLineColor = typedArray.getColor(R.styleable.voiceView_voiceLine, Color.BLACK);
        maxVolume = typedArray.getFloat(R.styleable.voiceView_maxVolume, 100);
        sensibility = typedArray.getInt(R.styleable.voiceView_sensibility, 4);
        lineSpeed = typedArray.getInt(R.styleable.voiceView_lineSpeed, 90);
        fineness = typedArray.getInt(R.styleable.voiceView_fineness, 1);
        paths = new ArrayList<>(20);
        for (int i = 0; i < 20; i++) {
            paths.add(new Path());
        }
        typedArray.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawVoiceLine(canvas);
        invalidate();
    }


    private void drawVoiceLine(Canvas canvas) {
        lineChange();
        if (paintVoicLine == null) {
            paintVoicLine = new Paint();
            paintVoicLine.setColor(voiceLineColor);
            paintVoicLine.setAntiAlias(true);
            paintVoicLine.setStyle(Paint.Style.STROKE);
            paintVoicLine.setStrokeWidth(2);
        }
        canvas.save();
        // 确定声波水平线的位置
        int moveY = getHeight() / 2;
        for (int i = 0; i < paths.size(); i++) {
            paths.get(i).reset();
            paths.get(i).moveTo(getWidth(), getHeight() / 2);   //恢复到水平的位置
        }
        // 画声波
        for (float i = getWidth() - 1; i >= 0; i -= fineness) {
            amplitude = 4 * volume * i / getWidth() - 4 * volume * i * i / getWidth() / getWidth();
            for (int n = 1; n <= paths.size(); n++) {
                float sin = amplitude * (float) Math.sin((i - Math.pow(1.22, n)) * Math.PI / 180 - translateX);
                paths.get(n - 1).lineTo(i, (2 * n * sin / paths.size() - 15 * sin / paths.size() + moveY));
            }
        }
        // 颜色的渐变
        for (int n = 0; n < paths.size(); n++) {
            if (n == paths.size() - 1) {
                paintVoicLine.setAlpha(255);
            } else {
                paintVoicLine.setAlpha(n * 130 / paths.size());
            }
            if (paintVoicLine.getAlpha() > 0) {
                canvas.drawPath(paths.get(n), paintVoicLine);
            }
        }
        canvas.restore();
    }


    public void setVolume(int volume) {
        if (volume > maxVolume * sensibility / 25) {
            isSet = true;
            this.targetVolume = getHeight() * volume / 2 / maxVolume;
        }
    }

    private void lineChange() {
        if (lastTime == 0) {
            lastTime = System.currentTimeMillis();
            translateX += 1.5;
        } else {
            if (System.currentTimeMillis() - lastTime > lineSpeed) {
                lastTime = System.currentTimeMillis();
                translateX += 1.5;
            } else {
                return;
            }
        }
        if (volume < targetVolume && isSet) {
            volume += getHeight() / 30;
        } else {
            isSet = false;
            if (volume <= 10) {
                volume = 10;
            } else {
                if (volume < getHeight() / 30) {
                    volume -= getHeight() / 60;
                } else {
                    volume -= getHeight() / 30;
                }
            }
        }
    }

}
