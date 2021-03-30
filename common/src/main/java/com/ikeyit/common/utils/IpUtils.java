package com.ikeyit.common.utils;

import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class IpUtils {

    private final static String LOCALHOST_IPV4 = "127.0.0.1";

    private final static String LOCALHOST_IPV6 = "0:0:0:0:0:0:0:1";

    private final static String UNKNOWN = "unknown";

    public static String getClientIp(HttpServletRequest request ) {
        if(request == null)
            return null;

        String ipAddress = request.getHeader("X-Forwarded-For");
        if(StringUtils.isEmpty(ipAddress) || UNKNOWN.equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("X-Real-IP");
        }
        if(StringUtils.isEmpty(ipAddress) || UNKNOWN.equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if(StringUtils.isEmpty(ipAddress) || UNKNOWN.equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (StringUtils.isEmpty(ipAddress) || UNKNOWN.equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("HTTP_CLIENT_IP");
        }
        if (StringUtils.isEmpty(ipAddress) || UNKNOWN.equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (StringUtils.isEmpty(ipAddress) || UNKNOWN.equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }
        if(StringUtils.isEmpty(ipAddress) || UNKNOWN.equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
            if(LOCALHOST_IPV4.equals(ipAddress) || LOCALHOST_IPV6.equals(ipAddress)) {
                try {
                    InetAddress inetAddress = InetAddress.getLocalHost();
                    ipAddress = inetAddress.getHostAddress();
                } catch (UnknownHostException e) {
                    //ignore
                }
            }
        }

        if(!StringUtils.isEmpty(ipAddress)
                && ipAddress.length() > 15
                && ipAddress.indexOf(",") > 0) {
            ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
        }

        return ipAddress;
    }


    public static String getClientIp() {
        HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
        return getClientIp(request);
    }
}
