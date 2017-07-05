package com;

import org.junit.Test;

import java.util.List;

import static org.mockito.Mockito.*;

/**
 * Created by qzzhu on 17-7-3.
 */

public class MockTest {

    @Test
    public void mockList(){
        List mocklist = mock(List.class);

        mocklist.add("one");
        verify(mocklist).add("one");
        mocklist.clear();
        verify(mocklist).clear();

        when(mocklist.get(0)).thenReturn("first");

        //verify(mocklist).get(0);
    }
}
