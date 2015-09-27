package com.xxxifan.devbox.library.helpers;

import android.text.TextUtils;
import android.widget.EditText;

/**
 * Created by xifan on 15-7-24.
 */
public class FieldChecker {

    public static boolean isEmptyField(EditText item) {
        return checkEmptyField(item) > -1;
    }

    public static int checkEmptyField(EditText... items) {
        for (int i = 0, s = items.length; i < s; i++) {
            if (TextUtils.isEmpty(items[i].getText())) {
                return i;
            }
        }
        return -1;
    }

    public static boolean checkEqualField(EditText... items) {
        boolean result = true;
        String str1, str2;
        for (int i = items.length - 1; i >= 0; i--) {
            if (i - 1 < 0) {
                break;
            }

            str1 = items[i].getText().toString();
            str2 = items[i - 1].getText().toString();
            if (TextUtils.isEmpty(str1) || TextUtils.isEmpty(str2) || !str1.equals(str2)) {
                result = false;
                break;
            }
        }

        return result;
    }

}
