package com.sourabhkaushk.safelock;

import androidx.annotation.IdRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.TextView;

import com.sourabhkaushk.safelock.Listeners.FramesListener;
import com.sourabhkaushk.safelock.customViews.CircularSeekBar;
import com.sourabhkaushk.safelock.databinding.ActivityMainBinding;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    int pinBlock = 0;
    private static final int PIN_DURATION = 300;
    private CountDownTimer countDownTimer;

    void stopTracking(CircularSeekBar seekBar) {
        countDownTimer = null;
        int s = Math.round(seekBar.getProgress()) / 10;
        Log.d("tracking started===", s + "");
        binding.pinView.setText(binding.pinView.getText() + "" + s);
        MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.click);
        mp.start();
        if (binding.pinView.getText().length() == 4 && !binding.pinView.getText().toString().equals("1234")) {

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    reset();
                    MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.error);
                    mp.start();

                }
            },1000);

            return;

        }else if (binding.pinView.getText().length() == 4){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    final MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.audio5);
                    mp.setLooping(true);
                    mp.start();
                    binding.animation.setReverseFrameListener(new FramesListener() {
                        @Override
                        public boolean onFrameChanged(String frameName) {
                            return true;
                        }

                        @Override
                        public void onAnimationFinished() {
                            mp.stop();
                        }
                    });
                    binding.animation.startReversAnimation();

                }
            },1000);

        }
        pinBlock++;
        if (pinBlock == 1) {
            scaleView(binding.pin2, 7);
        } else if (pinBlock == 2) {
            scaleView(binding.pin3, 8.5);
        } else if (pinBlock == 3) {
            scaleView(binding.pin4, 11.2);
        }

        float startAngle = binding.pinDefault.getCurrentPointerAngle();
        binding.pinDefault.setStartAngle(startAngle);
        binding.pinDefault.setEndAngle(startAngle);
        if (pinBlock % 2 == 0) {
            binding.pinDefault.setNegativeEnabled(false);
        } else {
            binding.pinDefault.setNegativeEnabled(true);
        }

        binding.pinDefault.setProgress(0);
        if (binding.pinView.getText().length() < 3) {
            countDownTimer = new CountDownTimer(PIN_DURATION, PIN_DURATION) {
                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {

                    countDownTimer = null;
                    stopTracking(binding.pinDefault);

                }
            };
        }


    }

    private void reset() {
        countDownTimer = null;
        binding.pinDefault.setNegativeEnabled(false);
        binding.pinDefault.setStartAngle(270f);
        binding.pinDefault.setEndAngle(270f);

        pinBlock = 0;
        binding.pinDefault.setProgress(0);
        binding.pinView.setText("");
        binding.pin1.setProgress(0);
        binding.pin2.setProgress(0);
        binding.pin3.setProgress(0);
        binding.pin4.setProgress(0);
        LowScaleView(binding.pin2, 7);
        LowScaleView(binding.pin3, 8.5);
        LowScaleView(binding.pin4, 11.2);
        binding.pinDefault.setLockEnabled(true);
        binding.pinDefault.setLockEnabled(false);
        if (binding.pinView.getText().length() < 3) {
            countDownTimer = new CountDownTimer(PIN_DURATION, PIN_DURATION) {
                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {

                    countDownTimer = null;
                    stopTracking(binding.pinDefault);

                }
            };
        }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        scaleView(binding.pin1, 6);
        countDownTimer = new CountDownTimer(PIN_DURATION, PIN_DURATION) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {

                if (binding.pinView.getText().length() < 3) {

                    stopTracking(binding.pinDefault);
                }
            }
        };
        binding.pinDefault.setOnSeekBarChangeListener(new CircularSeekBar.OnCircularSeekBarChangeListener() {
            @Override
            public void onProgressChanged(final CircularSeekBar circularSeekBar, float progress, boolean fromUser) {

                int s = Math.round(progress) / 10;
                Log.d("tracking started===", s + "");
                if (s == 10) {
                    s = 0;
                }
                binding.pinNum.setText(s + "");
                if (pinBlock == 0) {
                    binding.pin1.setProgress(progress);
                } else if (pinBlock == 1) {
                    binding.pin2.setProgress(-progress);
                } else if (pinBlock == 2) {
                    binding.pin3.setProgress(progress);
                } else if (pinBlock == 3) {

                    binding.pin4.setProgress(-progress);
                }
                if (countDownTimer != null ) {
                    countDownTimer.cancel();
                    if(binding.pinView.getText().length() < 3){
                        countDownTimer.start();
                    }

                }

            }

            @Override
            public void onStopTrackingTouch(CircularSeekBar seekBar) {
                if (binding.pinView.getText().length() == 3) {

                    stopTracking(binding.pinDefault);
                }
            }

            @Override
            public void onStartTrackingTouch(CircularSeekBar seekBar) {

            }
        });
        binding.pin3.setOnSeekBarChangeListener(new CircularSeekBar.OnCircularSeekBarChangeListener() {
            @Override
            public void onProgressChanged(CircularSeekBar circularSeekBar, float progress, boolean fromUser) {
                float startAngle = circularSeekBar.getCurrentPointerAngle();
                binding.pin4.setProgress(0);
                binding.pin4.setStartAngle(startAngle);
                binding.pin4.setEndAngle(startAngle);
            }

            @Override
            public void onStopTrackingTouch(CircularSeekBar seekBar) {
                Log.d("tracking started===", seekBar.getProgress() + "");
            }

            @Override
            public void onStartTrackingTouch(CircularSeekBar seekBar) {
                Log.d("tracking started===", seekBar.getProgress() + "");
            }
        });

        binding.pin2.setOnSeekBarChangeListener(new CircularSeekBar.OnCircularSeekBarChangeListener() {
            @Override
            public void onProgressChanged(CircularSeekBar circularSeekBar, float progress, boolean fromUser) {
                float startAngle = circularSeekBar.getCurrentPointerAngle();
                binding.pin3.setProgress(0);
                binding.pin3.setStartAngle(startAngle);
                binding.pin3.setEndAngle(startAngle);
            }

            @Override
            public void onStopTrackingTouch(CircularSeekBar seekBar) {

            }

            @Override
            public void onStartTrackingTouch(CircularSeekBar seekBar) {
                Log.d("tracking started===", seekBar.getProgress() + "");
            }
        });

        binding.pin1.setOnSeekBarChangeListener(new CircularSeekBar.OnCircularSeekBarChangeListener() {
            @Override
            public void onProgressChanged(CircularSeekBar circularSeekBar, float progress, boolean fromUser) {
                float startAngle = circularSeekBar.getCurrentPointerAngle();
                binding.pin2.setProgress(0);
                binding.pin2.setStartAngle(startAngle);
                binding.pin2.setEndAngle(startAngle);

            }

            @Override
            public void onStopTrackingTouch(CircularSeekBar seekBar) {
                Log.d("tracking stopped===", seekBar.getProgress() + "");
//                binding.pin1.setEnabled(false);
//
            }

            @Override
            public void onStartTrackingTouch(CircularSeekBar seekBar) {
                Log.d("tracking started===", seekBar.getProgress() + "");
            }
        });
//
//        final TextView textEvent = findTheViewById(R.id.text_event);
//        final TextView textProgress = findTheViewById(R.id.text_progress);
//
//        CircularSeekBar seekBar = (CircularSeekBar) findViewById(R.id.seek_bar);
//        seekBar.setOnSeekBarChangeListener(new CircularSeekBar.OnCircularSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(CircularSeekBar circularSeekBar, float progress, boolean fromUser) {
//                String message = String.format("Progress changed to %.2f, fromUser %s", progress, fromUser);
//                Log.d("Main", message);
//                textProgress.setText(message);
//            }
//
//            @Override
//            public void onStopTrackingTouch(CircularSeekBar seekBar) {
//                Log.d("Main", "onStopTrackingTouch");
//                textEvent.setText("");
//            }
//
//            @Override
//            public void onStartTrackingTouch(CircularSeekBar seekBar) {
//                Log.d("Main", "onStartTrackingTouch");
//                textEvent.setText("touched | ");
//            }
//        });
        final MediaPlayer mp1 = MediaPlayer.create(getApplicationContext(), R.raw.audio1);
        mp1.setLooping(true);
        mp1.start();
        binding.animation.setFramesListener(new FramesListener() {
            @Override
            public boolean onFrameChanged(String frameName) {
                return true;
            }

            @Override
            public void onAnimationFinished() {
                mp1.stop();
            }
        });

        binding.animation.startAnimation();
    }


    public void scaleView(final View v, double endScale) {
        ArrayList<Animator> animatorList = new ArrayList<>();
        AnimatorSet animatorSet = new AnimatorSet();
        ValueAnimator anim = ValueAnimator.ofInt(v.getLayoutParams().height, (int) (v.getLayoutParams().height * endScale));
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = v.getLayoutParams();
                layoutParams.height = val;
                v.setLayoutParams(layoutParams);
            }
        });
        anim.setDuration(150);
//        animatorList.add(anim);
        ValueAnimator anim2 = ValueAnimator.ofInt(v.getLayoutParams().width, (int) (v.getLayoutParams().width * endScale));
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = v.getLayoutParams();
                layoutParams.width = val;
                v.setLayoutParams(layoutParams);
            }
        });
        anim2.setDuration(150);
        anim.start();
        anim2.start();
    }

    public void LowScaleView(final View v, double endScale) {
        ArrayList<Animator> animatorList = new ArrayList<>();
        AnimatorSet animatorSet = new AnimatorSet();
        ValueAnimator anim = ValueAnimator.ofInt(v.getLayoutParams().height, (int) (v.getLayoutParams().height / endScale));
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = v.getLayoutParams();
                layoutParams.height = val;
                v.setLayoutParams(layoutParams);
            }
        });
        anim.setDuration(150);
//        animatorList.add(anim);
        ValueAnimator anim2 = ValueAnimator.ofInt(v.getLayoutParams().width, (int) (v.getLayoutParams().width / endScale));
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = v.getLayoutParams();
                layoutParams.width = val;
                v.setLayoutParams(layoutParams);
            }
        });
        anim2.setDuration(150);
        anim.start();
        anim2.start();
    }

}