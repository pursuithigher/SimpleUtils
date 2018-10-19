/*
 * Copyright (c) 2015 LingoChamp Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dzbook.filedownloader.event;

import com.dzbook.filedownloader.util.FileDownloadExecutors;
import com.dzbook.filedownloader.util.FileDownloadLog;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.Executor;

/**
 * Implementing actions for event pool.
 */
public class DownloadEventPoolImpl implements com.dzbook.filedownloader.event.IDownloadEventPool {

    private final Executor threadPool = FileDownloadExecutors.newDefaultThreadPool(10, "EventPool");

    private final HashMap<String, LinkedList<com.dzbook.filedownloader.event.IDownloadListener>> listenersMap = new HashMap<>();

    @Override
    public boolean addListener(final String eventId, final com.dzbook.filedownloader.event.IDownloadListener listener) {
        if (FileDownloadLog.NEED_LOG) {
            FileDownloadLog.v(this, "setListener %s", eventId);
        }
        if (listener == null) {
            throw new IllegalArgumentException("listener must not be null!");
        }

        LinkedList<com.dzbook.filedownloader.event.IDownloadListener> container = listenersMap.get(eventId);

        if (container == null) {
            synchronized (eventId.intern()) {
                container = listenersMap.get(eventId);
                if (container == null) {
                    listenersMap.put(eventId, container = new LinkedList<>());
                }
            }
        }


        synchronized (eventId.intern()) {
            return container.add(listener);
        }
    }

    @Override
    public boolean removeListener(final String eventId, final com.dzbook.filedownloader.event.IDownloadListener listener) {
        if (FileDownloadLog.NEED_LOG) {
            FileDownloadLog.v(this, "removeListener %s", eventId);
        }

        LinkedList<com.dzbook.filedownloader.event.IDownloadListener> container = listenersMap.get(eventId);
        if (container == null) {
            synchronized (eventId.intern()) {
                container = listenersMap.get(eventId);
            }
        }

        if (container == null || listener == null) {
            return false;
        }

        synchronized (eventId.intern()) {
            boolean succeed = container.remove(listener);
            if (container.size() <= 0) {
                listenersMap.remove(eventId);
            }
            return succeed;
        }
    }

    @Override
    public boolean publish(final com.dzbook.filedownloader.event.IDownloadEvent event) {
        if (FileDownloadLog.NEED_LOG) {
            FileDownloadLog.v(this, "publish %s", event.getId());
        }
        if (event == null) {
            throw new IllegalArgumentException("event must not be null!");
        }
        String eventId = event.getId();
        LinkedList<com.dzbook.filedownloader.event.IDownloadListener> listeners = listenersMap.get(eventId);
        if (listeners == null) {
            synchronized (eventId.intern()) {
                listeners = listenersMap.get(eventId);
                if (listeners == null) {
                    if (FileDownloadLog.NEED_LOG) {
                        FileDownloadLog.d(this, "No listener for this event %s", eventId);
                    }
                    return false;
                }
            }
        }

        trigger(listeners, event);
        return true;
    }

    @Override
    public void asyncPublishInNewThread(final com.dzbook.filedownloader.event.IDownloadEvent event) {
        if (FileDownloadLog.NEED_LOG) {
            FileDownloadLog.v(this, "asyncPublishInNewThread %s", event.getId());
        }
        if (event == null) {
            throw new IllegalArgumentException("event must not be null!");
        }

        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                DownloadEventPoolImpl.this.publish(event);
            }
        });
    }

    private void trigger(final LinkedList<com.dzbook.filedownloader.event.IDownloadListener> listeners,
                         final IDownloadEvent event) {

        final Object[] lists = listeners.toArray();
        for (Object o : lists) {
            if (o == null) {
                continue; // it has been removed while before listeners.toArray().
            }

            if (((IDownloadListener) o).callback(event)) {
                break;
            }
        }

        if (event.callback != null) {
            event.callback.run();
        }
    }
}
