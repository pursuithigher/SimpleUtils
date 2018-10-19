package hw.sdk.utils;


import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import hw.sdk.HwSdkAppConstant;

/**
 * <p>
 * 查询类接口要求：HTTPS+HmacSha256参数鉴权
 * 操作类接口要求：HTTPS+SHA256WithRSA签名
 * <p>
 * 【华为阅读-SDV1】【设计类-加密问题-密钥硬编码-非合规】【白盒】【红十条】发现多处密钥硬编码和返回值硬编码，私钥需分段存储，已增加攻击获取私钥难度
 *
 * @author lizz 2018年04月12日
 */
public class HwEncrpt {
    /**
     * 签名类型 1 查询类
     */
    public static final int SIGN_TYPE_1 = 1;
    /**
     * 签名类型 2 操作类
     */
    public static final int SIGN_TYPE_2 = 2;

    private static final byte[] RSA_PRIVATE_KEY = Base64.decode("MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQC9Q4Y5QX5j08HrnbY3irfKdkEl"
            + "lAU2OORnAjlXDyCzcm2Z6ZRrGvtTZUAMelfU5PWS6XGEm3d4kJEKbXi4Crl8o2E/E3YJPk1lQD1d"
            + "0JTdrvZleETN1ViHZFSQwS3L94Woh0E3TPebaEYq88eExvKu1tDdjSoFjBbgMezySnas5Nc2xF28"
            + Constants.RSA_KEY
            + "x52e71nafqfbjXxZuEtpu92oJd6A9mWbd0BZTk72ZHUmDcKcqjfcEH19SWOphMJFYkxU5FRoIEr3"
            + "/zisyTO4Mt33ZmwELOrY9PdlyAAyed7ZoH+hlTr7c025QROvb2LmqgRiUT56tMECgYEA+jH5m6iM"
            + "RK6XjiBhSUnlr3DzRybwlQrtIj5sZprWe2my5uYHG3jbViYIO7GtQvMTnDrBCxNhuM6dPrL0cRnb"
            + "sp/iBMXe3pyjT/aWveBkn4R+UpBsnbtDn28r1MZpCDtr5UNc0TPj4KFJvjnV/e8oGoyYEroECqcw"
            + "1LqNOGDiLhkCgYEAwaemNePYrXW+MVX/hatfLQ96tpxwf7yuHdENZ2q5AFw73GJWYvC8VY+TcoKP"
            + "AmeoCUMltI3TrS6K5Q/GoLd5K2BsoJrSxQNQFd3ehWAtdOuPDvQ5rn/2fsvgvc3rOvJh7uNnwEZC"
            + "I/45WQg+UFWref4PPc+ArNtp9Xj2y7LndwkCgYARojIQeXmhYZjG6JtSugWZLuHGkwUDzChYcIPd"
            + HwSdkAppConstant.RSA_KEY);

    private static PrivateKey priKey = null;

    private static byte[] secretKeys = null;


    static {

        try {
            PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(RSA_PRIVATE_KEY);

            secretKeys = (Constants.SECRET_KEYS + "VDgHcd4iFCf6CbYtABeDtGR6UgTkT1oE4p2Rom7CDEH3ysOvDKzjWB3R" + HwEncryptParam.SECRET_KEYS).getBytes("UTF-8");

            KeyFactory keyf = KeyFactory.getInstance("RSA");
            priKey = keyf.generatePrivate(priPKCS8);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * sha256_HMAC加密
     *
     * @param message 消息
     * @return 加密后字符串
     */
    private static String hmacSha256(String message) {
        String hash = "";
        try {
            Mac sha256HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(secretKeys, "HmacSHA256");
            sha256HMAC.init(secretKey);
            hash = Base64.encode(sha256HMAC.doFinal(message.getBytes("UTF-8")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hash;
    }

    /**
     * 使用开发者联盟网站分配的支付私钥对支付信息进行签名
     * 强烈建议在 商户服务端做签名处理，且私钥存储在服务端，防止信息泄露
     * 开发者通过服务器获取服务器端的签名之后，再进行支付请求
     *
     * @param content
     * @return
     */
    private static String sha256withRsa(String content) {
        //使用加密算法规则
        try {
            Signature signature = Signature.getInstance("SHA256WithRSA");
            signature.initSign(priKey);
            signature.update(content.toString().getBytes("UTF-8"));
            byte[] signed = signature.sign();
            return Base64.encode(signed);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (SignatureException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 1：查询类签名
     * 2：操作类签名
     *
     * @param content  content
     * @param signType signType
     * @return sign
     */
    public static String hwEncrptSign(String content, int signType) {
        String sign = "";
        if (signType == SIGN_TYPE_1) {
            sign = hmacSha256(content);
        } else if (signType == SIGN_TYPE_2) {
            sign = sha256withRsa(content);
        }
        return sign;
    }

}
