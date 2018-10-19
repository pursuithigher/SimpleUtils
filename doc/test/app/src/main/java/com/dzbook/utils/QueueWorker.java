package com.dzbook.utils;


import com.dzbook.activity.account.RechargeRecordActivity;
import com.dzbook.activity.detail.BookDetailActivity;
import com.dzbook.activity.detail.BookDetailChapterActivity;
import com.dzbook.activity.reader.MissingContentActivity;
import com.dzbook.activity.reader.ReaderActivity;
import com.dzbook.activity.reader.ReaderCatalogActivity;
import com.dzbook.bean.QueueBean;
import com.dzbook.fragment.main.BaseFragment;
import com.dzbook.log.LogConstants;
import com.dzbook.recharge.order.LotOrderPageActivity;
import com.dzbook.recharge.order.SingleOrderActivity;
import com.dzbook.recharge.ui.RechargeListActivity;
import com.iss.app.BaseActivity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * 工作队列
 *
 * @author caimantang on 2017/8/10.
 * 先进先出
 */
public class QueueWorker {

    /**
     * 队列最大值
     */
    public static final int CAPACITY = 2;
    private static final QueueWorker INSTANCE = new QueueWorker();
    private static HashSet<String> dzPage;

    //阻塞队列
    private ArrayBlockingQueue<QueueBean> queue;


    private QueueWorker() {
        queue = new ArrayBlockingQueue<QueueBean>(CAPACITY);
    }


    public static final QueueWorker getInstance() {
        return INSTANCE;
    }

    /**
     * 加入队列
     *
     * @param object object
     */
    public void add(Object object) {
        try {
            if (null == object) {
                return;
            }
            if (isContains(object)) {
                return;
            }
            String pi = "";
            String ps = "";
            if (object instanceof BaseActivity) {
                pi = ((BaseActivity) object).getPI();
                ps = ((BaseActivity) object).getPS();

            } else if (object instanceof BaseFragment) {
                pi = ((BaseFragment) object).getPI();
                ps = ((BaseFragment) object).getPS();

            }
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put(LogConstants.MAP_PI, pi);
            hashMap.put(LogConstants.MAP_PS, ps);
            if (queue.size() >= CAPACITY) {
                queue.take();
            }
            queue.put(new QueueBean(object.getClass().getSimpleName(), hashMap));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 加入队列
     *
     * @param queueBean queueBean
     */
    public void addQueue(QueueBean queueBean) {
        try {
            if (null == queueBean) {
                return;
            }
            if (isContains(queueBean.name)) {
                return;
            }
            if (queue.size() >= CAPACITY) {
                queue.take();
            }
            queue.put(queueBean);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isContains(Object o) {
        if (o instanceof String) {
            return dzPage.contains(o.toString());
        }
        return dzPage.contains(o.getClass().getSimpleName());
    }

    /**
     * getFromEnd
     *
     * @return queue
     */
    public QueueBean getFromEnd() {
        try {
            int size = queue.size();
            int index = size - 1;
            if (index < 0) {
                return null;
            }
            QueueBean[] array = queue.toArray(new QueueBean[size]);
            QueueBean queueBean = array[index];
            if (null != queueBean && BookDetailActivity.class.getSimpleName().equals(queueBean.name)) {
                index = size - 2;
                if (index < 0) {
                    return null;
                }
                return array[index];
            }
            return queueBean;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    static {
        dzPage = new HashSet<>();
        dzPage.add(ReaderActivity.TAG);//阅读器页
        dzPage.add(ReaderCatalogActivity.TAG);//阅读器目录页面
        dzPage.add(BookDetailChapterActivity.TAG);//书籍章节页面
        dzPage.add(RechargeListActivity.TAG);//自有充值界面
        dzPage.add(SingleOrderActivity.TAG);//自有订购页面
        dzPage.add(RechargeRecordActivity.TAG);//充值记录页面

        dzPage.add(LotOrderPageActivity.TAG);//自有批量订购页面
        dzPage.add(SingleOrderActivity.TAG);//自有单章订购界面
        dzPage.add(MissingContentActivity.TAG);//缺章领取看点页面
        dzPage.add(LotOrderPageActivity.TAG);//批量订购页面
    }
}
