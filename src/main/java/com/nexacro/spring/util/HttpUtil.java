package com.nexacro.spring.util;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

/**
 * 
 * HTTP Util class
 * 
 * @author Park SeongMin
 * @since 07.27.2015
 * @version 1.0
 * @see
 */
public abstract class HttpUtil {

    public static final String HEADER_USER_AGENT = "User-Agent";
    public static final String HEADER_CONTENT_ENCODING = "Content-Encoding";
    public static final String HEADER_CONTENT_TYPE = "Content-Type";
    public static final String HEADER_ACCEPT_CHARSET = "Accept-Charset";
    public static final String HEADER_ACCEPT_LANGUAGE = "Accept-Language";

    public static String getHeaderValue(HttpServletRequest request, String targetHeaderName) {

        if (targetHeaderName == null) {
            return null;
        }

        @SuppressWarnings("unchecked")
        Enumeration enumeration = request.getHeaderNames();
        while (enumeration.hasMoreElements()) {
            String headerName = (String) enumeration.nextElement();
            if (targetHeaderName.equalsIgnoreCase(headerName)) {
                return request.getHeader(headerName);
            }
        }

        return null;
    }

    public static String getRemoteAddr(HttpServletRequest request) {

        /*
         * http://lesstif.com/pages/viewpage.action?pageId=20775886
         * 
         * WAS 는 보통 2차 방화벽 안에 있고 Web Server 를 통해 client 에서 호출되거나 cluster로 구성되어
         * load balancer 에서 호출되는데 이럴 경우에서 getRemoteAddr() 을 호출하면 웹서버나 load
         * balancer의 IP 가 나옴
         * WebLogic 의 web server 연계 모듈인 weblogic connector 는
         * 위 헤더를 사용하지 않고 Proxy-Client-IP 나 WL-Proxy-Client-IP 를 사용하므로
         * weblogic 에서 도는 application 작성시 수정이 필요함
         */

        String clientIp = request.getHeader("X-Forwarded-For");
        if (!isEmpty(clientIp)) {
            return getFirstIp(clientIp);
        }

        clientIp = request.getHeader("Proxy-Client-IP");
        if (!isEmpty(clientIp)) {
            return getFirstIp(clientIp);
        }

        clientIp = request.getHeader("WL-Proxy-Client-IP");
        if (!isEmpty(clientIp)) {
            return getFirstIp(clientIp);
        }

        clientIp = request.getHeader("HTTP_CLIENT_IP");
        if (!isEmpty(clientIp)) {
            return getFirstIp(clientIp);
        }

        clientIp = request.getHeader("HTTP_X_FORWARDED_FOR");
        if (!isEmpty(clientIp)) {
            return getFirstIp(clientIp);
        }

        return request.getRemoteAddr();

    }

    private static boolean isEmpty(String str) {
        if (str == null || str.length() == 0 || "unknown".equalsIgnoreCase(str)) {
            return true;
        }

        return false;
    }

    private static String getFirstIp(String ip) {

        if (ip == null) {
            return null;
        }

        String[] split = ip.split(",");
        return split[0];
    }

}
