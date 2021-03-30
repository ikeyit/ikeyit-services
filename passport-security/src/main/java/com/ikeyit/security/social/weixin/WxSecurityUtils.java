package com.ikeyit.security.social.weixin;

import org.apache.commons.lang3.RandomStringUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.util.Arrays;
import java.util.Base64;

public class WxSecurityUtils {

    static Logger log = LoggerFactory.getLogger(WxSecurityUtils.class);

    public static final String AES = "AES";

    public static final String ALGORITHM = "AES/CBC/PKCS7Padding";

    static {
        Security.addProvider(new BouncyCastleProvider());
        //加到main方法里
//        Security.setProperty("crypto.policy", "unlimited");
    }

    /**
     * 签名
     *
     * @param str
     * @return
     */
    public static String sha1(String str) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] digest = md.digest(str.getBytes());
            return toHexString(digest);
        } catch (NoSuchAlgorithmException e) {
            //never occur
        }
        return null;
    }

    /**
     * 转化为16进制表示
     *
     * @param bytes
     * @return
     */
    public static String toHexString(byte[] bytes) {

        StringBuffer buf = new StringBuffer(bytes.length * 2);
        for (int i = 0; i < bytes.length; i++) {
            int v = bytes[i] & 0xff;
            if (v < 0x10) {
                buf.append("0");
            }
            buf.append(Integer.toHexString(v));
        }
        return buf.toString().toLowerCase();

    }

    /**
     * 签名
     *
     * @param params
     * @return
     */
    public static String sha1(String... params) {
        Arrays.sort(params);
        StringBuffer buffer = new StringBuffer();
        for (String param : params)
            buffer.append(param);

        return sha1(buffer.toString());
    }


    public static byte[] decodePKCS7(byte[] decrypted) {
        int pad = (int) decrypted[decrypted.length - 1];
        if (pad < 1 || pad > 32) {
            pad = 0;
        }
        return Arrays.copyOfRange(decrypted, 0, decrypted.length - pad);
    }


    public static byte[] decryptAES_CBC_PKCS7Padding(byte[] content, byte[] keyByte, byte[] ivByte) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            SecretKeySpec skeySpec = new SecretKeySpec(keyByte, AES);
            IvParameterSpec iv = new IvParameterSpec(ivByte);
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
//            byte[] original = cipher.doFinal(content);
//            return decodePKCS7(original);

            return cipher.doFinal(content);
        } catch (Exception e) {
            throw new IllegalStateException("解密微信消息失败", e);
        }
    }

    public static byte[] encryptAES_CBC_PKCS7Padding(byte[] content, byte[] keyByte, byte[] ivByte) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            SecretKeySpec skeySpec = new SecretKeySpec(keyByte, "AES");
            IvParameterSpec iv = new IvParameterSpec(ivByte);
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
            return cipher.doFinal(content);
        } catch (Exception e) {
            throw new IllegalStateException("加密微信消息失败", e);
        }

    }

    public static String decryptUserInfo(String sessionKey, String encryptData, String iv) {
        byte[] aesKeyBytes = Base64.getDecoder().decode(sessionKey);
        byte[] ivBytes = Base64.getDecoder().decode(iv);
        byte[] encryptDataBytes = Base64.getDecoder().decode(encryptData);
        // 使用BASE64对密文进行解码
        byte[] bytes = WxSecurityUtils.decryptAES_CBC_PKCS7Padding(encryptDataBytes, aesKeyBytes, ivBytes);
        return new String(bytes);

    }


    public static String decryptWxMsg(String encodingAESKey, String encryptData, String appId) {
        String content = null, from_appid = null;
        byte[] aesKey = Base64.getDecoder().decode(encodingAESKey + '=');
        // 使用BASE64对密文进行解码
        byte[] bytes = WxSecurityUtils.decryptAES_CBC_PKCS7Padding(Base64.getDecoder().decode(encryptData), aesKey,
                Arrays.copyOfRange(aesKey, 0, 16));
        // 分离16位随机字符串,网络字节序和AppId
        byte[] lenBytes = Arrays.copyOfRange(bytes, 16, 20);
        int len = lenBytes[3] & 0xFF |
                (lenBytes[2] & 0xFF) << 8 |
                (lenBytes[1] & 0xFF) << 16 |
                (lenBytes[0] & 0xFF) << 24;
        try {
            content = new String(Arrays.copyOfRange(bytes, 20, 20 + len), "UTF-8");
            from_appid = new String(Arrays.copyOfRange(bytes, 20 + len, bytes.length), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            //不应该发生
            throw new IllegalStateException("编码异常", e);
        }

        // appid不相同的情况
        if (!from_appid.equals(appId))
            throw new IllegalStateException("微信消息体中的appId不符合！");
        return content;

    }

    public static String encryptWxMsg(String encodingAESKey, String text, String appId) {
        try {
            String randomStr = RandomStringUtils.randomAlphabetic(16);
            byte[] randomStrBytes = randomStr.getBytes("UTF-8");
            byte[] textBytes = text.getBytes("UTF-8");
            byte[] appidBytes = appId.getBytes("UTF-8");
            assert randomStrBytes.length == 16;
            int len = text.length();
            byte[] networkBytesOrder = new byte[]{(byte) ((len >> 24) & 0xFF), (byte) ((len >> 16) & 0xFF),
                    (byte) ((len >> 8) & 0xFF), (byte) (len & 0xFF)};

            // randomStr + networkBytesOrder + text + appid
            byte[] bytes = new byte[randomStrBytes.length + networkBytesOrder.length + textBytes.length
                    + appidBytes.length];
            int destPos = 0;
            System.arraycopy(randomStrBytes, 0, bytes, destPos, randomStrBytes.length);
            destPos += randomStrBytes.length;
            System.arraycopy(networkBytesOrder, 0, bytes, destPos, networkBytesOrder.length);
            destPos += networkBytesOrder.length;
            System.arraycopy(textBytes, 0, bytes, destPos, textBytes.length);
            destPos += textBytes.length;
            System.arraycopy(appidBytes, 0, bytes, destPos, appidBytes.length);

            // AES_CBC_PKCS7Padding加密
            byte[] aesKeyBytes = Base64.getDecoder().decode(encodingAESKey + '=');
            byte[] ivkeyBytes = Arrays.copyOfRange(aesKeyBytes, 0, 16);
            byte[] encrypted = encryptAES_CBC_PKCS7Padding(bytes, aesKeyBytes, ivkeyBytes);

            // 使用BASE64对加密后的字符串进行编码
            String base64Encrypted = Base64.getEncoder().encodeToString(encrypted);

            return base64Encrypted;
        } catch (UnsupportedEncodingException e) {
            // 不应该发生
            throw new IllegalStateException("编码异常", e);
        }

    }


}
