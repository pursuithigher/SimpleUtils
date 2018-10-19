/*
 * Copyright (C) 2012 Markus Junginger, greenrobot (http://greenrobot.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dzbook.event;

import android.util.Log;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SubscriberMethodFinder
 */
class SubscriberMethodFinder {
    private static final String ON_EVENT_METHOD_NAME = "onEvent";

    /*
     * In newer class files, compilers may add methods. Those are called bridge or synthetic methods.
     * EventBus must ignore both. There modifiers are not public but defined in the Java class file format:
     * http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.6-200-A.1
     */
    private static final int BRIDGE = 0x40;
    private static final int SYNTHETIC = 0x1000;

    private static final int MODIFIERS_IGNORE = Modifier.ABSTRACT | Modifier.STATIC | BRIDGE | SYNTHETIC;
    private static final Map<String, List<SubscriberMethod>> METHOD_CACHE = new HashMap<String, List<SubscriberMethod>>();

    private final Map<Class<?>, Class<?>> skipMethodVerificationForClasses;

    SubscriberMethodFinder(List<Class<?>> skipMethodVerificationForClassesList) {
        skipMethodVerificationForClasses = new ConcurrentHashMap<Class<?>, Class<?>>();
        if (skipMethodVerificationForClassesList != null) {
            for (Class<?> clazz : skipMethodVerificationForClassesList) {
                skipMethodVerificationForClasses.put(clazz, clazz);
            }
        }
    }

    static void clearCaches() {
        synchronized (METHOD_CACHE) {
            METHOD_CACHE.clear();
        }
    }

    List<SubscriberMethod> findSubscriberMethods(Class<?> subscriberClass) {
        String key = subscriberClass.getName();
        List<SubscriberMethod> subscriberMethods;
        synchronized (METHOD_CACHE) {
            subscriberMethods = METHOD_CACHE.get(key);
        }
        if (subscriberMethods != null) {
            return subscriberMethods;
        }
        subscriberMethods = new ArrayList<SubscriberMethod>();
        Class<?> clazz = subscriberClass;
        HashSet<String> eventTypesFound = new HashSet<String>();
        StringBuilder methodKeyBuilder = new StringBuilder();
        while (clazz != null) {
            String name = clazz.getName();
            if (name.startsWith("java.") || name.startsWith("javax.") || name.startsWith("android.")) {
                // Skip system classes, this just degrades performance
                break;
            }

            // Starting with EventBus 2.2 we enforced methods to be public (might change with annotations again)
            Method[] methods = clazz.getDeclaredMethods();
            for (Method method : methods) {
                String methodName = method.getName();
                if (methodName.startsWith(ON_EVENT_METHOD_NAME)) {
                    addEventMethod(subscriberMethods, clazz, eventTypesFound, methodKeyBuilder, method, methodName);
                }
            }
            clazz = clazz.getSuperclass();
        }
        if (subscriberMethods.isEmpty()) {
            throw new EventBusException("Subscriber " + subscriberClass + " has no public methods called "
                    + ON_EVENT_METHOD_NAME);
        } else {
            synchronized (METHOD_CACHE) {
                METHOD_CACHE.put(key, subscriberMethods);
            }
            return subscriberMethods;
        }
    }

    private void addEventMethod(List<SubscriberMethod> subscriberMethods, Class<?> clazz, HashSet<String> eventTypesFound, StringBuilder methodKeyBuilder, Method method, String methodName) {
        int modifiers = method.getModifiers();
        if ((modifiers & Modifier.PUBLIC) != 0 && (modifiers & MODIFIERS_IGNORE) == 0) {
            Class<?>[] parameterTypes = method.getParameterTypes();
            if (parameterTypes.length == 1) {
                String modifierString = methodName.substring(ON_EVENT_METHOD_NAME.length());
                ThreadMode threadMode;
                if (modifierString.length() == 0) {
                    threadMode = ThreadMode.PostThread;
                } else if ("MainThread".equals(modifierString)) {
                    threadMode = ThreadMode.MainThread;
                } else if ("BackgroundThread".equals(modifierString)) {
                    threadMode = ThreadMode.BackgroundThread;
                } else if ("Async".equals(modifierString)) {
                    threadMode = ThreadMode.Async;
                } else {
                    if (skipMethodVerificationForClasses.containsKey(clazz)) {
                        return;
                    } else {
                        throw new EventBusException("Illegal onEvent method, check for typos: " + method);
                    }
                }
                Class<?> eventType = parameterTypes[0];
                methodKeyBuilder.setLength(0);
                methodKeyBuilder.append(methodName);
                methodKeyBuilder.append('>').append(eventType.getName());
                String methodKey = methodKeyBuilder.toString();
                if (eventTypesFound.add(methodKey)) {
                    // Only add if not already found in a sub class
                    subscriberMethods.add(new SubscriberMethod(method, threadMode, eventType));
                }
            }
        } else if (!skipMethodVerificationForClasses.containsKey(clazz)) {
            Log.d(EventBus.TAG, "Skipping method (not public, static or abstract): " + clazz + "."
                    + methodName);
        }
    }

}
