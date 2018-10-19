package com.dzbook.utils;

import java.util.List;

/**
 * List Utils
 *
 * @author <a href="http://www.trinea.cn" target="_blank">Trinea</a> 2011-7-22
 */
public class ListUtils {

    /**
     * is null or its size is 0
     * <p/>
     * <pre>
     * isEmpty(null)   =   true;
     * isEmpty({})     =   true;
     * isEmpty({1})    =   false;
     * </pre>
     *
     * @param sourceList sourceList
     * @return if list is null or its size is 0, return true, else return false.
     */
    public static boolean isEmpty(List<?> sourceList) {
        return sourceList == null || sourceList.size() == 0;
    }
}
