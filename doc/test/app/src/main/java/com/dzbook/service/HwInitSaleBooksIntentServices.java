package com.dzbook.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Environment;
import android.support.annotation.Nullable;

import com.dzbook.bean.LocalFileBean;
import com.dzbook.bean.LocalFileUtils;
import com.dzbook.event.EventBusUtils;
import com.dzbook.event.EventConstant;
import com.dzbook.utils.ListUtils;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * HwInitSaleBooksIntentServices
 */
public class HwInitSaleBooksIntentServices extends IntentService {

    /**
     * 启动页预访问书城数据
     */
    public static final int SPLASH_INIT_SALE_BOOKS_TYPE = 2;

    /**
     * SERVICE_TYPE
     */
    public static final String SERVICE_TYPE = "service_type";

    private static final String SALE_BOOK_PATH = "/EbookDemo/";

    /**
     * HwInitSaleBooksIntentServices
     */
    public HwInitSaleBooksIntentServices() {
        super("HwInitSaleBooksIntentServices");
    }

    private boolean isDemoVersion() {
        try {
            Class<?> clazz = Class.forName("android.os.SystemProperties");
            if (clazz != null) {
                Method method = clazz.getDeclaredMethod("get", String.class, String.class);
                if (method != null && !method.isAccessible()) {
                    method.setAccessible(true);
                }
                String vendortype = (String) method.invoke(null, "ro.hw.vendor", "");
                String countrytype = (String) method.invoke(null, "ro.hw.country", "");
                return "demo".equalsIgnoreCase(vendortype) || "demo".equalsIgnoreCase(countrytype);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException ex) {
            ex.printStackTrace();
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        } catch (InvocationTargetException ex) {
            ex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            int type = intent.getIntExtra(SERVICE_TYPE, -1);
            if (type == SPLASH_INIT_SALE_BOOKS_TYPE) {
                if (isDemoVersion()) {
                    searchLocalSaleEpubBooks(Environment.getExternalStorageDirectory().getAbsolutePath() + SALE_BOOK_PATH);
                }
            }
        }
    }

    /**
     * 卖场模式：/EbookDemo/下的100本epub书籍
     *
     * @param path path
     */
    public void searchLocalSaleEpubBooks(final String path) {
        File file = new File(path);
        if (!file.exists()) {
            return;
        }
        File[] files = file.listFiles();
        if (files == null || files.length <= 0) {
            return;
        }
        ArrayList<LocalFileBean> list = new ArrayList<>();
        int count = 0;
        for (File childrenFile : files) {
            if (childrenFile.isFile() && childrenFile.getName().toLowerCase().contains("epub") && count < 100) {
                count++;
                LocalFileBean fileBean = LocalFileBean.fileToLocalBean(getApplicationContext(), childrenFile, "skipfiletypecheck");
                if (fileBean != null) {
                    list.add(fileBean);
                }
            }
        }
        if (list.size() > 0) {
            addLocalSaleEpubBooksToShelf(list);
        }
    }

    /**
     * 将卖场模式书添加到书架
     *
     * @param list list
     */
    public void addLocalSaleEpubBooksToShelf(ArrayList<LocalFileBean> list) {
        if (ListUtils.isEmpty(list)) {
            return;
        }

        for (LocalFileBean bean : list) {
            if (bean.fileType == LocalFileBean.TYPE_EPUB) {
                LocalFileUtils.insertLocalDb(getApplicationContext(), bean);
            }
        }

        EventBusUtils.sendMessage(EventConstant.SHELF_LOCAL_REFRESH, EventConstant.TYPE_MAINSHELFFRAGMENT, null);
    }


}
