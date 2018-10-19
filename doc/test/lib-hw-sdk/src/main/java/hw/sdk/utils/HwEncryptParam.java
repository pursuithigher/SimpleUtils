package hw.sdk.utils;


import android.text.TextUtils;

import com.dzbook.lib.utils.ALog;

import java.io.ByteArrayOutputStream;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.interfaces.RSAKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

/**
 * HwEncryptParam
 *
 * @author CHENDENGYU on 2018-04-26 13:37.
 */
public class HwEncryptParam {

    /**
     * SECRET KEYS
     */
    public static final String SECRET_KEYS = "reXNEJE28af5bumzH2lRXxPv9WASwq4KCYPWPEZvtr5rq18eF6450M0JJ6fd1DimWgvS4dpobQ4lkRTo";

    private static final String CHARSET = "UTF-8";
    private static final String RSA_ALGORITHM = "RSA/ECB/OAEPPadding";
    private static final byte[] RSA_PUBLIC_KEY = Base64.decode("MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAhtPXESfuK8WLE0R4Y2ehFlV3evPnvV4bYhRC6sxhhtANUz70wvMyAzr+EhzF838UbXouXThD4Y3xNhQww90aeSXnywC4omQttQv8K0++0I5s7+QF66MWMj/PQaWiFbm07M6X4jeaKOi33Kc/9rgjgwelnQ3a7I3vrydLN2r06vWUnemiCH4VDBCEGxu3PYtFX25BS+1r39NoLeYZexofmSaYZJnq9Si8q0uEyiV1x/Y1NkzmzXmxIJxrk47QBwtQvkM7wG8VZglrQa8OE/aZWtL/jaIqN4ZObpM70IZq3+itZV7vasFbLWbDJFcexk3zqmtN6t+0gi9kjtBjOvzNGwIDAQAB");

    private static RSAPublicKey pubKey = null;

    static {
        try {
            X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(RSA_PUBLIC_KEY);

            KeyFactory keyf = KeyFactory.getInstance("RSA");
            PublicKey publicKey = keyf.generatePublic(pubKeySpec);
            pubKey = publicKey instanceof RSAPublicKey ? (RSAPublicKey) publicKey : null;
        } catch (NoSuchAlgorithmException e) {
            ALog.printStackTrace(e);
        } catch (InvalidKeySpecException e) {
            ALog.printStackTrace(e);
        }
    }


    /**
     * 公钥加密操作
     *
     * @param data data
     * @return str
     */
    public static String hwEncrpt(String data) {
        if (TextUtils.isEmpty(data)) {
            return "";
        }
        return keyOper(data, pubKey, pubKey, true);
    }

    /**
     * 密钥操作
     *
     * @param data    data
     * @param rsaKey  rsaKey
     * @param encrypt encrypt
     * @return str
     */
    private static <T extends Key, K extends RSAKey> String keyOper(String data, T key, K rsaKey, boolean encrypt) {
        try {
            int en = encrypt ? Cipher.ENCRYPT_MODE : Cipher.DECRYPT_MODE;
            byte[] dataByte = encrypt ? data.getBytes(CHARSET) : Base64.decode(data);
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            cipher.init(en, key);
            byte[] afterByte = rsaSplitCodec(cipher, en, dataByte, rsaKey.getModulus().bitLength());
            return encrypt ? Base64.encode(afterByte) : new String(afterByte, "UTF-8");
        } catch (Exception e) {
            throw new RuntimeException("解密字符串[" + data + "]时遇到异常", e);
        }
    }

    private static byte[] rsaSplitCodec(Cipher cipher, int opmode, byte[] datas, int keySize) {
        int maxBlock = 0;
        if (opmode == Cipher.DECRYPT_MODE) {
            maxBlock = keySize / 8;
        } else {
            maxBlock = keySize / 8 - 11;
        }

        int offSet = 0;
        byte[] buff;
        int i = 0;
        ByteArrayOutputStream out = null;
        try {
            out = new ByteArrayOutputStream();
            while (datas.length > offSet) {
                if (datas.length - offSet > maxBlock) {
                    buff = cipher.doFinal(datas, offSet, maxBlock);
                } else {
                    buff = cipher.doFinal(datas, offSet, datas.length - offSet);
                }
                out.write(buff, 0, buff.length);
                i++;
                offSet = i * maxBlock;
            }
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("加解密阀值为[" + maxBlock + "]的数据时发生异常", e);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (Exception e) {
                    ALog.printStackTrace(e);
                }
            }
        }
    }

}
