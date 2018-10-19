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


/**
 * Used to drive the FileDownload Service connection event.
 */
public class DownloadServiceConnectChangedEvent extends com.dzbook.filedownloader.event.IDownloadEvent {
    /**
     * id
     */
    public static final String ID = "event.service.connect.changed";

    private final ConnectStatus status;
    private final Class<?> serviceClass;

    /**
     * 构造
     *
     * @param status       状态
     * @param serviceClass class
     */
    public DownloadServiceConnectChangedEvent(final ConnectStatus status,
                                              final Class<?> serviceClass) {
        super(ID);

        this.status = status;
        this.serviceClass = serviceClass;
    }

    /**
     * 连接状态枚举
     */
    public enum ConnectStatus {
        /**
         * 连接
         */
        connected, disconnected,
        // the process hosting the service has crashed or been killed. (do not be unbound manually)
        /**
         * lost
         */
        lost
    }

    public ConnectStatus getStatus() {
        return status;
    }

    /**
     * service
     *
     * @param aServiceClass serviceClass
     * @return 是否
     */
    public boolean isSuchService(final Class<?> aServiceClass) {
        return this.serviceClass != null
                && this.serviceClass.getName().equals(aServiceClass.getName());

    }
}
