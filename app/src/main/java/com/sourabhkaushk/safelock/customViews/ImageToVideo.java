package com.sourabhkaushk.safelock.customViews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import com.sourabhkaushk.safelock.Listeners.FramesListener;
import com.sourabhkaushk.safelock.R;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;

public class ImageToVideo extends AppCompatImageView {

    @Nullable
    private String framesPrefix;
    private int framesPerSecond=25;
    private CountDownTimer countDownTimer;
    @Nullable
    private FramesListener framesListener;
    private FramesListener reverseFrameListener;

    @Nullable
    public FramesListener getFramesListener() {
        return framesListener;
    }

    public void setFramesListener(@Nullable FramesListener framesListener) {
        this.framesListener = framesListener;
    }

    @Nullable
    public String getFramesPrefix() {
        return framesPrefix;
    }

    public void setFramesPrefix(@Nullable String framesPrefix) {
        this.framesPrefix = framesPrefix;
    }

    public int getFramesPerSecond() {
        return framesPerSecond;
    }

    public void setFramesPerSecond(int framesPerSecond) {
        this.framesPerSecond = framesPerSecond;
    }

    public FramesListener getReverseFrameListener() {
        return reverseFrameListener;
    }

    public void setReverseFrameListener(FramesListener reverseFrameListener) {
        this.reverseFrameListener = reverseFrameListener;
    }

    public ImageToVideo(Context context) {
        super(context);
        
        init(context,null,0);
    }

    public ImageToVideo(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs,0);
    }

    public ImageToVideo(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs,defStyleAttr);
    }


    private void init(Context context,@Nullable AttributeSet attrs,int defStyleAttr) {
        final TypedArray attrArray = getContext().obtainStyledAttributes(attrs, R.styleable.itv_ImageToVideo, defStyleAttr, 0);
        initAttributes(attrArray);
        startFrames(context);

    }

    private void startFrames(final Context context) {
        if(TextUtils.isEmpty(framesPrefix)){
            return;
        }
        Field[] fields=R.raw.class.getFields();
        final ArrayList<String> fileNames=new ArrayList<>();
        for(int count=0; count < fields.length; count++){
            String name=fields[count].getName();
            if(name.contains(framesPrefix) && !name.contains(framesPrefix+"_end")){
                fileNames.add(name);
            }
        }
        Collections.sort(fileNames);
        float temp=fileNames.size()/framesPerSecond;
        temp++;
        int totalTime= (int) Math.ceil(temp);
        final int interval=(int) Math.ceil(1000/framesPerSecond);
        countDownTimer=new CountDownTimer(totalTime* 1000L,interval) {
            @Override
            public void onTick(long millisUntilFinished) {
                if(fileNames.size()>0){
                    String name=fileNames.get(0);
                    int resId = getResources().getIdentifier("raw/"+name, null, context.getPackageName());
                    InputStream imageStream = context.getResources().openRawResource(resId);
                    Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
                    setAdjustViewBounds(true);
                    setScaleType(ScaleType.CENTER_CROP);
                    setImageBitmap(bitmap);
                    fileNames.remove(0);
                    if(framesListener!=null){
                        boolean isPause=framesListener.onFrameChanged(name);
                        if(!isPause){
                            countDownTimer.cancel();
                        }
                    }
                }


            }

            @Override
            public void onFinish() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    for (float i = getZ(); i >= -1.0f; i = i - 0.01f) {
                        final float finalI = i;
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                setZ(finalI);

                            }
                        },interval);
                    }
                    }
                if(framesListener!=null){
                    framesListener.onAnimationFinished();
                }

            }
        };

    }

    private void reversFrames(final Context context) {
        if(TextUtils.isEmpty(framesPrefix)){
            return;
        }
        Field[] fields=R.raw.class.getFields();
        final ArrayList<String> fileNames=new ArrayList<>();
        for(int count=0; count < fields.length; count++){
            String name=fields[count].getName();
            if(name.contains(framesPrefix+"_end")){
                fileNames.add(name);
            }
        }
        Collections.sort(fileNames,Collections.<String>reverseOrder());
        float temp=fileNames.size()/framesPerSecond;
        temp++;
        int totalTime= (int) Math.ceil(temp);
        final int interval=(int) Math.ceil(1000/framesPerSecond);
        CountDownTimer countDownTimer2=new CountDownTimer(totalTime* 1000L,interval) {
            @Override
            public void onTick(long millisUntilFinished) {
                if(fileNames.size()>0){
                    String name=fileNames.get(0);
                    int resId = getResources().getIdentifier("raw/"+name, null, context.getPackageName());
                    InputStream imageStream = context.getResources().openRawResource(resId);
                    Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
                    setAdjustViewBounds(true);
                    setScaleType(ScaleType.CENTER_CROP);
                    setImageBitmap(bitmap);
                    fileNames.remove(0);
                    if(reverseFrameListener!=null){
                        boolean isPause=reverseFrameListener.onFrameChanged(name);
                        if(!isPause){
                            cancel();
                        }
                    }
                }
            }

            @Override
            public void onFinish() {
                if(reverseFrameListener!=null){
                    reverseFrameListener.onAnimationFinished();
                }


            }
        };
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            for (float i = getZ(); i < 1.0f; i = i + 0.01f) {
                final float finalI = i;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setZ(finalI);
                    }
                },interval);
            }
        }
        countDownTimer2.start();

    }
    public void startReversAnimation(){
        reversFrames(getContext());
    }


    public void startAnimation(){
        countDownTimer.start();
    }

    private void initAttributes(TypedArray attrArray) {
        framesPrefix =attrArray.getString(R.styleable.itv_ImageToVideo_itv_frames_prefix);
        framesPerSecond=attrArray.getInt(R.styleable.itv_ImageToVideo_itv_fps,25);

    }


}
