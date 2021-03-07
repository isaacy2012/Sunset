package com.innerCat.sunset.factories;

import android.view.View;
import android.view.animation.Animation;

public class AnimationListenerFactory {
    public static Animation.AnimationListener getAnimationListener( View view, int visibility ) {
        return new Animation.AnimationListener(){
            @Override
            public void onAnimationStart(Animation anim) { }
            @Override
            public void onAnimationRepeat(Animation anim) { }

            @Override
            public void onAnimationEnd(Animation anim) {
                view.setVisibility(visibility);
            }
        };
    }
}
