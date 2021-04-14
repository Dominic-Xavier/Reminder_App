package com.myapp.reminderapp;

import android.util.Log;

import com.myapp.reminderapp.sql.Sql;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

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
    public void show() throws JSONException {
        JSONObject jsonObject;
        Sql s = new Sql(InstrumentationRegistry.getInstrumentation().getTargetContext());
        jsonObject = s.allDatas();
        Log.i("JSON Object is:", ""+jsonObject);
    }
}