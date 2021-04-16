package com.myapp.reminderapp;

import com.myapp.reminderapp.sql.Sql;
import org.junit.Assert;
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
    public void show() {
        Sql s = new Sql(InstrumentationRegistry.getInstrumentation().getContext());
        boolean duplicateTask = s.checkDuplicateTask("u_id_1","hajsh");
        Assert.assertEquals(true,duplicateTask);
    }
}