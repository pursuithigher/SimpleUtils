package com.dzpay.recharge.bean;

import java.util.HashMap;

/**
 * 操作类型
 *
 * @author lizhongzhong
 */
public enum RechargeAction {


    /**
     * 消费,需要检查支付的先检查支付，不需要检查支付的直接支付，需要充值且允许充值的引导到充值页面
     */
    PAY_CHECK {
        @Override
        public int actionCode() {
            return 30;
        }
    },
    /**
     * 支付
     */
    PAY {
        @Override
        public int actionCode() {
            return 31;
        }
    },
    /**
     * 充值
     */
    RECHARGE {
        @Override
        public int actionCode() {
            return 42;
        }
    },

    /**
     * 下订单请求
     */
    MAKE_ORDER_REQUEST {
        @Override
        public int actionCode() {
            return 45;
        }
    },
    /**
     * 联通短信wap支付
     */
    SMS_UNION_WAP_PAY {
        @Override
        public int actionCode() {
            return 46;
        }
    },


    /** 包月计费action开始 **/

    /**
     * 包月支付
     */
    MONTH_PAY {
        @Override
        public int actionCode() {
            return 61;
        }
    },

    /**
     * sdk支付成功
     */
    MONTH_SDK_PAY_SUCCESS {
        @Override
        public int actionCode() {
            return 65;
        }
    },

    /**
     * 服务器通知失败
     */
    MONTH_SDK_PAY_SUCCESS_NOTIFY_FAIL {
        @Override
        public int actionCode() {
            return 66;
        }
    },

    /**
     * 包月充值
     */
    MONTH_RECHARGE {
        @Override
        public int actionCode() {
            return 68;
        }
    },

    /**
     * 下订单请求
     */
    MONTH_MAKE_ORDER_REQUEST {
        @Override
        public int actionCode() {
            return 69;
        }
    },

    /**
     * 联通短信wap支付
     */
    MONTH_SMS_UNION_WAP_PAY {
        @Override
        public int actionCode() {
            return 70;
        }
    },
    /** 包月计费action结束 **/


    /**
     * 信元电信短信wap支付
     */
    SMS_XINYUAN_TELECOM_WAP_PAY {
        @Override
        public int actionCode() {
            return 71;
        }
    },

    /**
     * 默认值
     */
    NONE {
        @Override
        public int actionCode() {
            return 99;
        }
    },
    /**
     * 打包订购 一键购 组合购
     */
    PACKBOOK_ORDER {
        @Override
        public int actionCode() {
            return 72;
        }
    },
    /**
     * 漫画支付
     */
    COMIC_PAY {
        @Override
        public int actionCode() {
            return 73;
        }
    },
    /**
     * 漫画支付检查
     */
    COMIC_PAY_CHECK {
        @Override
        public int actionCode() {
            return 74;
        }
    };

    private static HashMap<Integer, RechargeAction> map = new HashMap<Integer, RechargeAction>();

    /**
     * code
     * @return int
     */
    public abstract int actionCode();

    /**
     * 获取action
     *
     * @param ordinal 操作类型
     * @return RechargeAction
     */
    public static RechargeAction getByOrdinal(int ordinal) {
        if (map.isEmpty()) {
            for (RechargeAction action : RechargeAction.values()) {
                map.put(action.ordinal(), action);
            }
        }
        if (map.containsKey(ordinal)) {
            return map.get(ordinal);
        }
        return NONE;
    }
}
