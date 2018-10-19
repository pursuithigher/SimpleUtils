package com.dzbook.filedownloader;

import com.dzbook.filedownloader.message.MessageSnapshot;

interface IFileDownloadIPCCallback {
    oneway void callback(in MessageSnapshot snapshot);
}
