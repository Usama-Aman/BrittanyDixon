package com.cp.brittany.dixon.ruler;

import android.content.Context;

import androidx.annotation.NonNull;

final class RulerViewUtils {

    /**
     * Convert SP to pixel.
     *
     * @param context Context.
     * @param spValue Value in sp to convert.
     *
     * @return Value in pixels.
     */
    static int sp2px(@NonNull final Context context,
                     final float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }
}