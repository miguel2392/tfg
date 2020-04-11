package com.example.myapplication;

import androidx.core.util.Pair;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }
    @Test
    public void testConcatenation() {

        String idAsignatura = "ASDF";
        String idPresentacion = "12345678998765432100";
        String result = AdvertisingDataHelper.generateDeviceName(idAsignatura,idPresentacion);
        String expectedresult = "AAAASDF12345678998765432100";
        assertEquals(expectedresult,result);

    }

    @Test
    public void testDesconcatenation(){
        String total = "AAAqjNN3AjUbXdAFn0QdLo5urG";
        Pair<String,String> result2 = AdvertisingDataHelper.recoverIds(total);
        Pair<String,String> expectedresult2 = new Pair<>("qjNN","12345678998765432100");
        assertEquals(expectedresult2,result2);
    }
}