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

package com.dzbook.filedownloader.message;

/**
 * The internal message snapshot station.
 * <p>
 * Making message snapshots keep flowing in order.
 */
public class MessageSnapshotFlow {

    private volatile MessageSnapshotThreadPool flowThreadPool;
    private volatile MessageReceiver receiver;

    /**
     * holder类
     */
    public static final class HolderClass {
        private static final MessageSnapshotFlow INSTANCE = new MessageSnapshotFlow();
    }

    public static MessageSnapshotFlow getImpl() {
        return HolderClass.INSTANCE;
    }

    /**
     * 设置接受
     *
     * @param receiver 接受
     */
    public void setReceiver(MessageReceiver receiver) {
        this.receiver = receiver;
        if (receiver == null) {
            this.flowThreadPool = null;
        } else {
            this.flowThreadPool = new MessageSnapshotThreadPool(5, receiver);
        }
    }

    /**
     * inflow
     *
     * @param snapshot snapshot
     */
    public void inflow(final com.dzbook.filedownloader.message.MessageSnapshot snapshot) {
        if (snapshot instanceof com.dzbook.filedownloader.message.IFlowDirectly) {
            if (receiver != null) {
                receiver.receive(snapshot);
            }
        } else {
            if (flowThreadPool != null) {
                flowThreadPool.execute(snapshot);
            }
        }

    }

    /**
     * 消息接受接口
     */
    public interface MessageReceiver {
        /**
         * 接收
         *
         * @param snapshot snapshot
         */
        void receive(MessageSnapshot snapshot);
    }
}
