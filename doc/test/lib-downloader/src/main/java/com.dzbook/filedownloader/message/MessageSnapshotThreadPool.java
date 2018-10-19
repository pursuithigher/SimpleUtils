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

import com.dzbook.filedownloader.util.FileDownloadExecutors;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * For guaranteeing only one-thread-pool for one-task, the task will be identified by its ID, make
 * sure the same task will be invoked in FIFO.
 */
public class MessageSnapshotThreadPool {

    private final List<FlowSingleExecutor> executorList;

    private final com.dzbook.filedownloader.message.MessageSnapshotFlow.MessageReceiver receiver;

    MessageSnapshotThreadPool(@SuppressWarnings("SameParameterValue") final int poolCount,
                              com.dzbook.filedownloader.message.MessageSnapshotFlow.MessageReceiver receiver) {
        this.receiver = receiver;
        executorList = new ArrayList<>();
        for (int i = 0; i < poolCount; i++) {
            executorList.add(new FlowSingleExecutor(i));
        }
    }

    /**
     * 添加任务
     *
     * @param snapshot snapshot
     */
    public void execute(final com.dzbook.filedownloader.message.MessageSnapshot snapshot) {
        FlowSingleExecutor targetPool = null;
        try {
            synchronized (executorList) {
                final int id = snapshot.getId();
                // Case 1. already had same task in executorList, so execute this event after
                // before-one.
                for (FlowSingleExecutor executor : executorList) {
                    if (executor.enQueueTaskIdList.contains(id)) {
                        targetPool = executor;
                        break;
                    }
                }

                // Case 2. no same task in executorList, so execute in executor which has the count
                // of active task is least.
                if (targetPool == null) {
                    int leastTaskCount = 0;
                    for (FlowSingleExecutor executor : executorList) {
                        if (executor.enQueueTaskIdList.size() <= 0) {
                            targetPool = executor;
                            break;
                        }

                        if (leastTaskCount == 0
                                || executor.enQueueTaskIdList.size() < leastTaskCount) {
                            leastTaskCount = executor.enQueueTaskIdList.size();
                            targetPool = executor;
                        }
                    }
                }

                //noinspection ConstantConditions
                targetPool.enqueue(id);
            }
        } finally {
            //noinspection ConstantConditions
            if (targetPool != null) {
                targetPool.execute(snapshot);
            }
        }
    }

    /**
     * 单线程池
     */
    public class FlowSingleExecutor {
        private final List<Integer> enQueueTaskIdList = new ArrayList<>();
        private final Executor mExecutor;

        /**
         * 构造
         *
         * @param index 线程数
         */
        public FlowSingleExecutor(int index) {
            mExecutor = FileDownloadExecutors.newDefaultThreadPool(1, "Flow-" + index);
        }

        /**
         * 添加队列
         *
         * @param id id
         */
        public void enqueue(final int id) {
            enQueueTaskIdList.add(id);
        }

        /**
         * 执行
         *
         * @param snapshot snapshot
         */
        public void execute(final MessageSnapshot snapshot) {
            mExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    receiver.receive(snapshot);
                    enQueueTaskIdList.remove((Integer) snapshot.getId());
                }
            });
        }

    }
}
