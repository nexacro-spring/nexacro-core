package com.nexacro.spring.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import com.nexacro.spring.NexacroConstants;
import com.nexacro.xapi.tx.PlatformException;

/**
 * <pre>
 * 현재 Thread 단위의 Web 요청에 대한 Nexacro Platform 데이터를 가진다. 
 * </pre>
 *
 * @ClassName   : NexacroContextHolder.java
 * @author Park SeongMin
 * @since 2015. 8. 11.
 * @version 1.0
 * @see
 * @Modification Information
 * <pre>
 *     since          author              description
 *  ===========    =============    ===========================
 *  2015. 8. 11.     Park SeongMin     최초 생성
 * </pre>
 */

public abstract class NexacroContextHolder {
    
    public static NexacroContext getNexacroContext(HttpServletRequest request, HttpServletResponse response) throws PlatformException {
        NexacroContext nexacroContext = new NexacroContext(request, response);
        RequestContextHolder.getRequestAttributes().setAttribute(NexacroConstants.ATTRIBUTE.NEXACRO_REQUEST, NexacroConstants.ATTRIBUTE.NEXACRO_REQUEST, RequestAttributes.SCOPE_REQUEST);
        setNexacroContext(nexacroContext);
        return nexacroContext;
    }
    
    public static void setNexacroContext(NexacroContext context) {
        RequestContextHolder.getRequestAttributes().setAttribute(NexacroConstants.ATTRIBUTE.NEXACRO_CACHE_DATA, context, RequestAttributes.SCOPE_REQUEST);
    }
    
    public static NexacroContext getNexacroContext() {
        Object context = RequestContextHolder.getRequestAttributes().getAttribute(NexacroConstants.ATTRIBUTE.NEXACRO_CACHE_DATA, RequestAttributes.SCOPE_REQUEST);
        if(context == null) {
            return null;
        }
        if(context instanceof NexacroContext) {
            return (NexacroContext) context;
        }
        return null;
    }

}
