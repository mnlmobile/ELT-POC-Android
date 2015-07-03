package org.cambridge.eltpoc.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;

import org.cambridge.eltpoc.R;

/**
 * Created by etorres on 6/22/15.
 */
public class UIUtils {
    public static Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        return resizedBitmap;
    }

    public static String[] getNavigationArray(Context context) {
        String[] navigationArray = new String[3];
        navigationArray[0] = context.getResources().getString(R.string.my_learning);
        navigationArray[1] = context.getResources().getString(R.string.my_teaching);
        navigationArray[2] = context.getResources().getString(R.string.sign_out);
        return navigationArray;
    }

    public static int[] getNavigationDrawables() {
        int[] navigationDrawables = new int[3];
        navigationDrawables[0] = R.drawable.ic_school_white_36dp;
        navigationDrawables[1] = R.drawable.ic_account_balance_white_36dp;
        navigationDrawables[2] = R.drawable.ic_input_white_36dp;
        return navigationDrawables;
    }
}
