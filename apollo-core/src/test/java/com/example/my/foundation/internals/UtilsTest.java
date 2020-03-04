package com.example.my.foundation.internals;

import org.junit.Assert;
import org.junit.Test;
/**
 * UtilsTest
 */
public class UtilsTest {
    @Test
    public void testIsOSWindows(){
        boolean actual= Utils.isOSWindows();//Mac OS X
        Assert.assertTrue(actual);
    }
    
}