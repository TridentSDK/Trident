package net.tridentsdk.server.util;

import junit.framework.Assert;
import org.junit.Test;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Iterator;

import static org.junit.Assert.*;

public class ConcurrentCircularArrayTest {

    @Test
    public void testAdd() throws Exception {
        ConcurrentCircularArray<Double> array = new ConcurrentCircularArray<>(50);
        for(int i = 0; i < 20; i ++) {
            array.add((double)i);
        }
        if(array.size() != 20) {
            fail("Failed to add elements");
        }

        ConcurrentCircularArray<Double> array2 = new ConcurrentCircularArray<>(50);
        for(int i = 0; i < 60; i ++) {
            array2.add((double)i);
        }
        if(array2.size() != 50) {
            fail("Failed to over add elements");
        }

        Double [] testArray = (Double[]) buildArray(array2, Double.class);

        if (arrayContains(testArray, 1D)) {
            fail("Failed to overwrite value");
        }

        if (!arrayContains(testArray, 59d)) {
            System.out.println("Found values: " + Arrays.toString(testArray));
            fail("Failed to add later value");
        }

    }

    @Test
    public void testIsEmpty() throws Exception {
        ConcurrentCircularArray<Double> array = new ConcurrentCircularArray<>(50);
        Assert.assertTrue("Array was not empty",array.isEmpty());

        array.add(5d);
        Assert.assertFalse("Array was empty", array.isEmpty());

        array.remove(0,null);
        Assert.assertTrue("Array was not emptied",array.isEmpty());
    }


    public <T> T[] buildArray(ConcurrentCircularArray<T> array, Class<T> clazz) {
        Iterator<T> iter = array.iterator();
        @SuppressWarnings("unchecked")
        T [] retVal = (T[]) Array.newInstance(clazz,array.size());
        int index = 0;

        while(iter.hasNext()) {
            retVal[index] = iter.next();
            index ++;
        }
        return retVal;
    }

    public boolean arrayContains (Object[] array, Object obj) {
        for(Object item: array) {
            if(obj.equals(item)) {
                return true;
            }
        }
        return false;
    }
}