package com.snn.kts;

import android.content.Context;
import android.widget.Toast;

class mToast {
    private static Toast toast;

    static void showToast(Context context, String str) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(context, str, Toast.LENGTH_SHORT);
        toast.show();
    }
}
