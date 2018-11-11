package com.dg.musicexample.utils;

import android.util.Log;

/**
 * Created by Giang Long on 11/10/2018.
 * Skype: gianglong7695@gmail.com (id: gianglong7695_1)
 * Phone: 0979 579 283
 */
public class Logs {
    public static boolean DEBUG = true;

    public static void d(String message) {
        if (DEBUG) {
            StackTraceElement stackTraceElement = (new Throwable()).getStackTrace()[1];
            Log.d(stackTraceElement.getFileName() +
                    "/" + stackTraceElement.getLineNumber(), message);
        }

    }

    public static void w(String message) {

        if (DEBUG) {
            StackTraceElement stackTraceElement = (new Throwable()).getStackTrace()[1];
            Log.w(stackTraceElement.getFileName() +
                    "/" + stackTraceElement.getLineNumber(), message);
        }

    }

    public static void i(String message) {
        if (DEBUG) {
            StackTraceElement stackTraceElement = (new Throwable()).getStackTrace()[1];
            Log.i(stackTraceElement.getFileName() +
                    "/" + stackTraceElement.getLineNumber(), message);
        }

    }

    public static void e(String message) {
        if (DEBUG) {
            StackTraceElement stackTraceElement = (new Throwable()).getStackTrace()[1];
            Log.e(stackTraceElement.getFileName() +
                    "/" + stackTraceElement.getLineNumber(), message);
        }

    }


    public static void e(int message) {
        if (DEBUG) {
            StackTraceElement stackTraceElement = (new Throwable()).getStackTrace()[1];
            Log.e(stackTraceElement.getFileName() +
                    "/" + stackTraceElement.getLineNumber(), message + "");
        }

    }
}
