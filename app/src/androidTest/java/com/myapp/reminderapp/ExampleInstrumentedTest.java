package com.myapp.reminderapp;

import android.content.Context;
import android.util.Log;

import com.myapp.reminderapp.sql.sql;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import androidx.test.platform.app.InstrumentationRegistry;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */

public class ExampleInstrumentedTest {
    @Test
    public void show(){
        sql s = new sql(InstrumentationRegistry.getInstrumentation().getTargetContext());
        Map<String, Set<String>> map = s.allDatas();
        System.out.println(map);
        Log.v("Map Object", String.valueOf(map));
    }
}