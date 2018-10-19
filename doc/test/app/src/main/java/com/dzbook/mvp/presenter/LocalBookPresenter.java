package com.dzbook.mvp.presenter;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.dzbook.bean.LocalFileBean;
import com.dzbook.bean.LocalFileUtils;
import com.dzbook.database.bean.BookInfo;
import com.dzbook.dialog.common.DialogLoading;
import com.dzbook.lib.utils.ALog;
import com.dzbook.lib.utils.SDCardUtil;
import com.dzbook.log.DzLog;
import com.dzbook.log.LogConstants;
import com.dzbook.mvp.UI.LocalFileUI;
import com.dzbook.utils.FileUtils;
import com.dzbook.utils.ListUtils;
import com.dzbook.view.common.dialog.CustomHintDialog;
import com.dzbook.view.common.dialog.base.CustomDialogBusiness;
import com.ishugui.R;
import com.iss.view.common.ToastAlone;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.WeakHashMap;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * LocalBookPresenter
 *
 * @author wxliao on 17/10/19.
 */
public class LocalBookPresenter {
    private static final String[] SUPPORT_SUFFIX = {".txt", ".epub", ".doc", ".docx", ".pdf", ".ppt", ".pptx", ".pps", ".ppsx", ".xls", ".xlsx"};

    private Context context;
    private final String rootDirPath;
    private String currentDirPath;
    private LocalFileUI mUI;

    private final String dzDirName;
    private boolean isSearchIndex;
    private boolean isSearchLocalFile;

    private WeakHashMap<String, Object> pathDataCache;
    private Disposable searchDisposable;

    /**
     * 构造
     *
     * @param ui ui
     */
    public LocalBookPresenter(LocalFileUI ui) {
        mUI = ui;
        if (mUI != null) {
            context = mUI.getContext();
        }
        currentDirPath = rootDirPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        dzDirName = FileUtils.APP_ROOT_DIR_PATH.substring(0, FileUtils.APP_ROOT_DIR_PATH.length() - 1);
    }

    /**
     * 刷新选中状态
     */
    public void refreshSelectState() {
        mUI.refreshSelectState();
    }

    /**
     * 搜做本地文件
     */
    public synchronized void searchLocalFile() {
        if (isSearchLocalFile) {
            return;
        }
        isSearchLocalFile = true;
        setCurrentPath(rootDirPath);
    }

    /**
     * 获取上一级
     */
    public void getPre() {
        if (TextUtils.equals(currentDirPath, rootDirPath)) {
            mUI.showMessage("已经到最上一级了!");
            return;
        }
        File file = new File(currentDirPath);
        setCurrentPath(file.getParent());
    }

    /**
     * setCurrentPath
     *
     * @param path path
     */
    @SuppressWarnings("unchecked")
    public void setCurrentPath(final String path) {
        currentDirPath = path;
        if (pathDataCache != null) {
            Object map = pathDataCache.get(path);
            if (null != map && map instanceof ArrayList) {
                mUI.refreshLocalInfo((ArrayList<LocalFileBean>) map, currentDirPath);
            }
        }
        scanFileObservable(path).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new DisposableObserver<ArrayList<LocalFileBean>>() {
            @Override
            public void onNext(ArrayList<LocalFileBean> list) {
                if (list != null) {
                    Collections.sort(list, LocalFileBean.getCompareType(LocalFileBean.TYPE_SORT_NAME));
                    bindTitleStatus(list);
                    mUI.refreshLocalInfo(list, currentDirPath);
                    if (pathDataCache == null) {
                        pathDataCache = new WeakHashMap<>();
                    }
                    pathDataCache.put(path, list);
                }
            }

            @Override
            public void onError(Throwable e) {
                isSearchLocalFile = false;
            }

            @Override
            public void onComplete() {
                isSearchLocalFile = false;
            }
        });
    }


    /**
     * 删除书籍
     *
     * @param list list
     */
    public void removeBooks(final ArrayList<LocalFileBean> list) {
        if (list.size() == 0) {
            return;
        }
        final CustomHintDialog dialog = new CustomHintDialog(context);
        int size = list.size();
        if (size == 1) {
            dialog.setDesc(mUI.getContext().getResources().getString(R.string.str_shelf_delete_this_books));
        } else {
            dialog.setDesc(String.format(mUI.getContext().getResources().getString(R.string.str_shelf_delete_books), size));
        }
        dialog.setConfirmTxt(context.getString(R.string.delete));
        dialog.setCancelTxt(context.getString(R.string.cancel));
        dialog.setCheckListener(new CustomDialogBusiness.OnCheckListener() {
            @Override
            public void clickConfirm(Object object) {
                for (LocalFileBean bean : list) {
                    String path = bean.filePath;
                    if (TextUtils.isEmpty(path) || bean.isTitle) {
                        continue;
                    }
                    File file = new File(path);
                    if (file.isFile() && file.exists()) {
                        if (file.delete()) {
                            ALog.dWz("delete success  " + file);
                        }
                    }
                }
                dialog.dismiss();
                mUI.deleteBean(list);
            }

            @Override
            public void clickCancel() {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    /**
     * 加入书架
     *
     * @param list list
     */
    public void addToShelf(final ArrayList<LocalFileBean> list) {
        if (list.size() == 0) {
            return;
        }
        Long filesSize = 0L;
        for (LocalFileBean bean : list) {
            filesSize += bean.size;
        }
        if (SDCardUtil.getInstance().isSDCardCanWrite(filesSize)) {
            addToShelfEvent(list);
        } else {
            ToastAlone.showShort(R.string.the_space_is_insufficient);
        }
    }

    private void addToShelfEvent(final ArrayList<LocalFileBean> list) {
        final DialogLoading dialog = new DialogLoading(context);
        addShelfObservable(list).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new DisposableObserver<LocalFileBean>() {
            @Override
            public void onNext(LocalFileBean bean) {
                mUI.bookAdded(bean);
            }

            @Override
            public void onError(Throwable e) {
                dialog.dismiss();
            }

            @Override
            public void onComplete() {
                dialog.dismiss();
                mUI.bookAddComplete(list);
            }

            @Override
            protected void onStart() {
                dialog.show();
            }
        });

    }

    private Observable<LocalFileBean> addShelfObservable(final ArrayList<LocalFileBean> list) {
        return Observable.create(new ObservableOnSubscribe<LocalFileBean>() {
            @Override
            public void subscribe(ObservableEmitter<LocalFileBean> e) {
                for (LocalFileBean bean : list) {
                    BookInfo bookInfo = LocalFileUtils.insertLocalDb(context.getApplicationContext(), bean);
                    bean.isImportSuccess = bookInfo != null;
                    e.onNext(bean);
                }
                e.onComplete();
            }
        });
    }

    private Observable<ArrayList<LocalFileBean>> scanFileObservable(final String parentPath) {
        return Observable.create(new ObservableOnSubscribe<ArrayList<LocalFileBean>>() {

            @Override
            public void subscribe(ObservableEmitter<ArrayList<LocalFileBean>> e) {
                File file = new File(parentPath);
                if (!file.exists() || !file.isDirectory()) {
                    e.onError(new IllegalArgumentException(""));
                }

                ArrayList<LocalFileBean> resultList = new ArrayList<>();
                File[] files = file.listFiles();
                if (file.length() > 0) {
                    for (File child : files) {
                        LocalFileBean localFileBean = LocalFileBean.fileToLocalBean(context, child, dzDirName);
                        if (localFileBean != null) {
                            resultList.add(localFileBean);
                        }
                    }
                }

                if (resultList.size() > 0) {
                    Collections.sort(resultList, LocalFileBean.getCompareType(LocalFileBean.TYPE_SORT_NAME));
                }

                e.onNext(resultList);
                e.onComplete();
            }
        });
    }


    private void disposeSearch() {
        if (searchDisposable != null && !searchDisposable.isDisposed()) {
            searchDisposable.dispose();
        }
    }

    /**
     * searchIndexFile
     */
    public synchronized void searchIndexFile() {
        if (isSearchIndex) {
            return;
        }
        isSearchIndex = true;
        disposeSearch();

        Observable<LocalFileBean> observable;
        if (currentDirPath.contains("sdcard1")) {
            observable = searchFileObservable(currentDirPath);
        } else {
            observable = searchFileByProviderObservable(currentDirPath);
        }

        final ArrayList<LocalFileBean> beanList = new ArrayList<>();

        searchDisposable = observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new DisposableObserver<LocalFileBean>() {
            @Override
            public void onNext(LocalFileBean bean) {
                bean.setSortType(LocalFileBean.TYPE_SORT_TIEM);
                beanList.add(bean);
            }

            @Override
            public void onError(Throwable e) {
                isSearchIndex = false;
                mUI.refreshIndexError();
            }

            @Override
            public void onComplete() {
                isSearchIndex = false;
                if (!ListUtils.isEmpty(beanList)) {
                    try {
                        Collections.sort(beanList, LocalFileBean.getCompareType(LocalFileBean.TYPE_SORT_TIEM));
                        bindTitleStatus(beanList);
                        mUI.refreshIndexInfo(beanList, currentDirPath);
                    } catch (Exception e) {
                        HashMap<String, String> map = new HashMap<>();
                        map.put("tag", "localImport");
                        map.put("booklistsize", beanList.size() + "");
                        map.put("exception", ALog.getStackTraceString(e));
                        DzLog.getInstance().logEvent(LogConstants.EVENT_CRASH, map, "");
                        mUI.refreshIndexError();
                    }
                } else {
                    // 是否展示为null
                    mUI.refreshIndexError();
                }
            }

            @Override
            protected void onStart() {
            }
        });
    }

    private void emitFile(File file, ObservableEmitter<LocalFileBean> e) {
        if (null != file) {
            File[] files = FileUtils.getListFile(file);
            if (null != files && files.length > 0) {
                for (File child : files) {
                    emitFile(child, e);
                }
            } else {
                LocalFileBean localFileBean = LocalFileBean.fileToLocalBean(context, file, dzDirName);
                if (localFileBean != null) {
                    e.onNext(localFileBean);
                }
            }
        }
    }

    private Observable<LocalFileBean> searchFileObservable(final String path) {
        return Observable.create(new ObservableOnSubscribe<LocalFileBean>() {
            @Override
            public void subscribe(ObservableEmitter<LocalFileBean> e) {
                File file = new File(path);
                emitFile(file, e);
                e.onComplete();
            }
        });
    }

    private Observable<LocalFileBean> searchFileByProviderObservable(final String path) {
        return Observable.create(new ObservableOnSubscribe<LocalFileBean>() {
            @Override
            public void subscribe(ObservableEmitter<LocalFileBean> e) {
                String volumeName = "external";
                Uri uri = MediaStore.Files.getContentUri(volumeName);
                String fileName = FileUtils.APP_ROOT_DIR_PATH.substring(0, FileUtils.APP_ROOT_DIR_PATH.length() - 1);
                String[] columns = new String[]{MediaStore.Files.FileColumns._ID, MediaStore.Files.FileColumns.DATA, MediaStore.Files.FileColumns.SIZE, MediaStore.Files.FileColumns.DATE_MODIFIED};
                Cursor c = context.getContentResolver().query(uri, columns, buildDocSelection(path), null, null);
                if (c != null) {
                    while (c.moveToNext()) {
                        String filePath = c.getString(1);
                        if (TextUtils.isEmpty(filePath) || filePath.contains(fileName)) {
                            continue;
                        }
                        File file = new File(filePath);
                        if (file.length() < 10 || !file.isFile()) {
                            continue;
                        }

                        LocalFileBean localFileBean = LocalFileBean.fileToLocalBean(context, file, dzDirName);
                        if (localFileBean != null) {
                            e.onNext(localFileBean);
                        }
                    }
                    c.close();
                }
                e.onComplete();
            }
        });
    }

    /**
     * splicing selections
     *
     * @return the sections
     */
    private String buildDocSelection(String pathFilter) {
        StringBuilder selection = new StringBuilder();
        for (String key : SUPPORT_SUFFIX) {
            selection.append("(" + MediaStore.Files.FileColumns.DATA + " like '%").append(key).append("') OR ");
        }
        selection.delete(selection.lastIndexOf(")") + 1, selection.length());

        if (!TextUtils.isEmpty(pathFilter)) {
            selection.append("AND (" + MediaStore.Files.FileColumns.DATA + " like '%").append(pathFilter).append("%')");
        }
        return selection.toString();
    }

    private void bindTitleStatus(ArrayList<LocalFileBean> list) {
        LocalFileBean item;
        LocalFileBean itemTitleCache = null;
        for (int i = 0; i < list.size(); i++) {
            item = list.get(i);
            boolean resullt = itemTitleCache == null || !(item.fileType == itemTitleCache.fileType) && (item.fileType == LocalFileBean.TYPE_DIR || itemTitleCache.fileType == LocalFileBean.TYPE_DIR);
            if (resullt || item.sortType == LocalFileBean.TYPE_SORT_NAME && !item.firstLetter.equals(itemTitleCache.firstLetter) || item.sortType == LocalFileBean.TYPE_SORT_TIEM && !item.lastModifiedDesc.equals(itemTitleCache.lastModifiedDesc)) {
                itemTitleCache = new LocalFileBean();
                itemTitleCache.firstLetter = item.firstLetter;
                if (item.fileType != LocalFileBean.TYPE_DIR || i == 0) {
                    itemTitleCache.fileName = item.fileName;
                    itemTitleCache.isTitle = true;
                    itemTitleCache.fileType = item.fileType;
                    itemTitleCache.sortType = item.sortType;
                    itemTitleCache.lastModified = item.lastModified;
                    itemTitleCache.lastModifiedDesc = item.lastModifiedDesc;
                    list.add(i, itemTitleCache);
                }
                i++;
            }
        }
    }

    public boolean isLocalShouldEmpty() {
        return rootDirPath.equals(currentDirPath);
    }
}
