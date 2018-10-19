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

package com.dzbook.filedownloader;

/**
 * Simplify the {@link com.dzbook.filedownloader.FileDownloadListener}.
 */
public class FileDownloadSampleListener extends com.dzbook.filedownloader.FileDownloadListener {

    @Override
    protected void pending(com.dzbook.filedownloader.BaseDownloadTask task, int soFarBytes, int totalBytes) {

    }

    @Override
    protected void progress(com.dzbook.filedownloader.BaseDownloadTask task, int soFarBytes, int totalBytes) {

    }

    @Override
    protected void blockComplete(com.dzbook.filedownloader.BaseDownloadTask task) {

    }

    @Override
    protected void completed(com.dzbook.filedownloader.BaseDownloadTask task) {

    }

    @Override
    protected void paused(com.dzbook.filedownloader.BaseDownloadTask task, int soFarBytes, int totalBytes) {

    }

    @Override
    protected void error(com.dzbook.filedownloader.BaseDownloadTask task, Throwable e) {

    }

    @Override
    protected void warn(BaseDownloadTask task) {

    }
}
