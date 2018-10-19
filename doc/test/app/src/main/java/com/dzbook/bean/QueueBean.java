package com.dzbook.bean;

import java.util.HashMap;

/**
 * QueueBean
 *
 * @author caimantang on 2017/9/5.
 */
public class QueueBean {
    /**
     * name
     */
    public String name;
    /**
     * map
     */
    public HashMap<String, String> map;

    /**
     * QueueBean
     * @param name name
     * @param map map
     */
    public QueueBean(String name, HashMap<String, String> map) {
        this.name = name;
        this.map = map;
    }

    @Override
    public String toString() {
        return "QueueBean{" + "name='" + name + '\'' + ", map=" + map + '}';
    }
}
