package com.nexacro.spring.context;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nexacro.spring.data.NexacroFirstRowHandler;
import com.nexacro.spring.util.CharsetUtil;
import com.nexacro.xapi.data.PlatformData;
import com.nexacro.xapi.tx.HttpPlatformRequest;
import com.nexacro.xapi.tx.HttpPlatformResponse;
import com.nexacro.xapi.tx.PlatformException;
import com.nexacro.xapi.tx.PlatformType;
import com.nexacro.xapi.util.StringUtils;

/**
 * <p>HTTP 요청으로 부터 데이터를 수신받으며, 수신 된 데이터를 저장한다.
 * <p>또한 데이터 분할 전송을 위한 NexacroFirstRowHandler를 제공한다.
 *
 * @author Park SeongMin
 * @since 07.28.2015
 * @version 1.0
 * @see NexacroContextHolder
 */
public class NexacroContext {
    
    /* 데이터 수신시 HTTP GET 데이터 등록 여부의 키 */
    private static final String REGISTER_GET_PARAMETER = "http.getparameter.register";
    /* HTTP GET 데이터 등록시 Variable 형식으로 변환 여부의 키 */
    private static final String GET_PARAMETER_AS_VARIABLE = "http.getparameter.asvariable";
    
    private HttpServletRequest request;
    private HttpServletResponse response;
    
    private HttpPlatformRequest platformRequest;
    private HttpPlatformResponse platformResponse;
    
    private NexacroFirstRowHandler firstRowHandler;
    
    public NexacroContext(HttpServletRequest request, HttpServletResponse response) throws PlatformException {
        parseRequest(request, response);
    }
    
    private void parseRequest(HttpServletRequest request, HttpServletResponse response) throws PlatformException {
               
        this.request = request;
        this.response = response;
        
        InputStream inputStream = null;
        try {
            inputStream = request.getInputStream();
        } catch (IOException e) {
            throw new PlatformException("Could not get HTTP InputStream", e);
        }

        String charsetOfRequest = CharsetUtil.getCharsetOfRequest(request, PlatformType.DEFAULT_CHAR_SET);
        
        String httpContentType = request.getContentType();
        String userAgent = request.getHeader("User-Agent");
        String contentType = findContentType(httpContentType, userAgent);
        
        // SSV 자동처리를 위해 inputStream을 사용하도록 한다.
        HttpPlatformRequest httpPlatformRequest = new HttpPlatformRequest(inputStream);
        if(PlatformType.HTTP_CONTENT_TYPE_BINARY.equals(httpContentType)) {
            httpPlatformRequest.setContentType(contentType);
        }
        
        try {
        	httpPlatformRequest.receiveData();
        } catch(PlatformException e) {
        	// ExceptionResolver에서 상세한 로그를 남긴다. 간략로그만을 남기도록 한다.
        	Logger logger = LoggerFactory.getLogger(getClass());
        	logger.error("receive platform data failed. e="+e.getMessage());
        	throw e;
        }
        
        // SSV 일 경우에만 contentType 존재. 그 외 처리를 위해 설정.
        if(httpPlatformRequest.getContentType() == null) {
            httpPlatformRequest.setContentType(contentType);
        }
        
        HttpPlatformResponse httpPlatformResponse = new HttpPlatformResponse(response, httpPlatformRequest);
        
        this.platformRequest = httpPlatformRequest;
        this.platformResponse = httpPlatformResponse;
        
    }
    
    public HttpPlatformRequest getPlatformRequest() {
        return platformRequest;
    }

    public HttpPlatformResponse getPlatformResponse() {
        if(platformResponse != null) {
            return platformResponse;
        }
        platformResponse = new HttpPlatformResponse(response, platformRequest);
        return platformResponse;
    }
    
    public NexacroFirstRowHandler getFirstRowHandler() {
        if(firstRowHandler != null) {
            return firstRowHandler;
        }
        
        firstRowHandler = new NexacroFirstRowHandler(response, platformRequest);
        
        return firstRowHandler;
    }
    
    public boolean isFirstRowFired() {
        if(firstRowHandler != null) {
            return firstRowHandler.isFirstRowStarted();
        }
        return false;
    }

    public PlatformData getPlatformData() {
        return this.platformRequest != null ? this.platformRequest.getData(): null;
    }
    
    /* HTTP의 ContentType으로부터 송수신 형식을 검색한다. */
    String findContentType(String httpContentType, String userAgent) {
        // http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html

        if (StringUtils.isEmpty(httpContentType)) {
            return null;
        }

        int index = httpContentType.indexOf(';');
        String contentType = (index == -1) ? httpContentType : httpContentType.substring(0, index);

        if (PlatformType.HTTP_CONTENT_TYPE_XML.equals(contentType)) {
            if (isMiPlatform(userAgent)) {
                return PlatformType.CONTENT_TYPE_MI_XML;
            } else {
                return PlatformType.CONTENT_TYPE_XML;
            }
        } else if (PlatformType.HTTP_CONTENT_TYPE_BINARY.equals(contentType)) {
            if (isMiPlatform(userAgent)) {
                return PlatformType.CONTENT_TYPE_MI_BINARY;
            } else {
                return PlatformType.CONTENT_TYPE_BINARY;
            }
        } else if (PlatformType.HTTP_CONTENT_TYPE_HTML.equals(contentType)) {
            return PlatformType.CONTENT_TYPE_HTML;
        }

        return null;
    }
    
    /* MiPlatform 여부 */
    private boolean isMiPlatform(String userAgent) {
        // MiPlatform 3.1;win32;1400x1050
        if (userAgent == null) {
            return false;
        } else {
            return (userAgent.startsWith("MiPlatform"));
        }
    }
    
}