package com.nexacro.spring.context;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import com.nexacro.spring.NexacroConstants;
import com.nexacro.xapi.tx.PlatformException;

/**
 * <p>HTTP 요청에 대한  <code>NexacroContext</code>를 Thread 단위로 관리한다. 
 *
 * @ClassName   : NexacroContextHolder.java
 * @author Park SeongMin
 * @since 08.11.2015
 * @version 1.0
 * @see NexacroContext
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
