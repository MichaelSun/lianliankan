package com.tinygame.lianliankan.utils;

import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

public class AnimationCreator {

    public static final Animation getShakeAnimatin() {
        Animation a = new TranslateAnimation(0.0f, 3.0f, 0.0f, 0.0f);
        a.setDuration(1000);
        a.setRepeatMode(Animation.RESTART);
        a.setRepeatCount(Animation.INFINITE);
        
        return a;
    }
    
}
