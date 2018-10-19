/*
 * Copyright (c) 2015 LingoChamp Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dzbook.filedownloader.message;

import com.dzbook.filedownloader.model.FileDownloadStatus;
import com.dzbook.filedownloader.util.FileDownloadUtils;

/**
 * The interface of block complete message.
 *
 * @see SmallMessageSnapshot
 * @see LargeMessageSnapshot
 */

public interface BlockCompleteMessage {

    /**
     * transmitToCompleted
     *
     * @return MessageSnapshot
     */
    com.dzbook.filedownloader.message.MessageSnapshot transmitToCompleted();

    /**
     * BlockCompleteMessageImpl
     */
    class BlockCompleteMessageImpl extends com.dzbook.filedownloader.message.MessageSnapshot implements BlockCompleteMessage {
        private final com.dzbook.filedownloader.message.MessageSnapshot mCompletedSnapshot;

        public BlockCompleteMessageImpl(com.dzbook.filedownloader.message.MessageSnapshot snapshot) {
            super(snapshot.getId());
            if (snapshot.getStatus() != FileDownloadStatus.COMPLETED) {
                throw new IllegalArgumentException(FileDownloadUtils.formatString("can't create the block complete message for id[%d], status[%d]", snapshot.getId(), snapshot.getStatus()));
            }
            this.mCompletedSnapshot = snapshot;
        }

        @Override
        public com.dzbook.filedownloader.message.MessageSnapshot transmitToCompleted() {
            return this.mCompletedSnapshot;
        }

        @Override
        public byte getStatus() {
            return FileDownloadStatus.BLOCK_COMPLETE;
        }
    }

}
