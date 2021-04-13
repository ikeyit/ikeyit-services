package com.ikeyit.common.utils;

/**
 * 隐私相关的一些方法
 */
public class PrivacyUtils {

    /**
     * 脱敏，隐藏手机号，电子邮箱地址等个人信息。
     * @param text 带脱敏的文字
     * @param size 被*替代的长度
     * @return
     */
    public static String hidePrivacy(String text, int size) {
        if (text == null || text.length() == 0)
            return text;

        int len = text.length();
        char[] chars = new char[len];
        if (len >= size + 2) {
            //开头和末尾都有显示的字符
            int startLen = (len - size) / 2;
            int i = 0;
            for (;i < startLen; i++)
                chars[i] = text.charAt(i);
            for (;i < startLen + size; i++)
                chars[i] = '*';
            for (;i < len; i++)
                chars[i] = text.charAt(i);
        } else if (len <= size + 1) {
            //仅开头显示1个字符
            int i = 0;
            chars[i] = text.charAt(i);
            for (i++;i < len;i++)
                chars[i] = '*';
        }

        return new String(chars);
    }

    /**
     * 邮箱地址脱敏
     * @param email
     * @param size
     * @return
     */
    public static String hideEmail(String email, int size) {
        if (email == null || email.length() == 0)
            return email;
        String[] parts = email.split("@");
        return PrivacyUtils.hidePrivacy(parts[0], size) + '@' + parts[1];
    }
}
