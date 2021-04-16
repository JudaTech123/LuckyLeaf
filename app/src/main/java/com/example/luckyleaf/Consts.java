package com.example.luckyleaf;

import android.content.Context;

import androidx.core.content.ContextCompat;

public class Consts {
    private int locked_color=-1,open_color=-1,unlocked_color=-1,undefined_color=-1;
    private static Consts instance;
    public static Consts getInstance() {
        if (instance==null)
            instance=new Consts();
        return instance;
    }
    public void initColors(Context context)
    {
        undefined_color = ContextCompat.getColor(context, R.color.undifned_color);
        open_color = ContextCompat.getColor(context, R.color.open_color);
        unlocked_color = ContextCompat.getColor(context, R.color.unlocked_color);
        locked_color = ContextCompat.getColor(context, R.color.locked_color);
    }

    public int getLocked_color() {
        return locked_color;
    }

    public int getOpen_color() {
        return open_color;
    }

    public int getUndefined_color() {
        return undefined_color;
    }

    public int getUnlocked_color() {
        return unlocked_color;
    }
}
