package com.nexacro.spring.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StopWatch;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.nexacro.spring.NexacroConstants;
import com.nexacro.spring.context.NexacroContext;
import com.nexacro.spring.context.NexacroContextHolder;
import com.nexacro.xapi.data.Debugger;
import com.nexacro.xapi.data.PlatformData;
import com.nexacro.xapi.tx.HttpPlatformRequest;

/**
 * <pre>
 * Statements
 * </pre>
 * 
 * @ClassName : NexacroWebSecurityInterceptor.java
 * @Description : 클래스 설명을 기술합니다.
 * @author Park SeongMin
 * @since 2015. 8. 11.
 * @version 1.0
 * @see
 * @Modification Information
 * 
 *               <pre>
 *     since          author              description
 *  ===========    =============    ===========================
 *  2015. 8. 11.     Park SeongMin     최초 생성
 * </pre>
 */

public class NexacroInterceptor extends HandlerInterceptorAdapter {

    private Logger logger = LoggerFactory.getLogger(NexacroInterceptor.class);
    private Logger performanceLogger = LoggerFactory.getLogger(NexacroConstants.PERFORMANCE_LOGGER);
    
    /**
     * This implementation always returns <code>true</code>.
     */
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        parseNexacroRequest(request, response, handler);
        return true;
    }

    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
            ModelAndView modelAndView) throws Exception {
        super.postHandle(request, response, handler, modelAndView);
    }

    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        super.afterCompletion(request, response, handler, ex);
    }

    private void parseNexacroRequest(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // check in able framework
//        checkSecurityWithServletRequest(request);
//        checkSecurityMultipart(request);
        
        StopWatch sw = new StopWatch(getClass().getSimpleName());
        try {
            sw.start("parse request");
            NexacroContext context = NexacroContextHolder.getNexacroContext(request, response);
            HttpPlatformRequest platformRequest = context.getPlatformRequest();
//            sw.start("check security");
//            checkSecurityWithPlatformRequest(platformRequest);
        } finally {
            sw.stop();
            if(performanceLogger.isTraceEnabled()) {
                performanceLogger.trace(sw.prettyPrint());
            }
        }
        
      
    }
    
    private void checkSecurityWithPlatformRequest(HttpPlatformRequest request) throws SecurityException {
        PlatformData platformData = request.getData();
        
//        SecurityUtil.checkSecurity(platformData);
        
        if(logger.isDebugEnabled()) {
            logger.debug("got request=[{}]", new Debugger().detail(platformData));
        }
        
    }

}
