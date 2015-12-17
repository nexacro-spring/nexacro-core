package com.nexacro.spring.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StopWatch;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.nexacro.spring.NexacroConstants;
import com.nexacro.spring.context.NexacroContext;
import com.nexacro.spring.context.NexacroContextHolder;
import com.nexacro.xapi.data.Debugger;
import com.nexacro.xapi.data.PlatformData;

/**
 * nexacro platform으로 부터 데이터를 수신받아 PlatformData로 변환하는 {@link HandlerInterceptor} 이다.
 * 
 * @author Park SeongMin
 * @since 08.11.2015
 * @version 1.0
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

        StopWatch sw = new StopWatch(getClass().getSimpleName());
        try {
            sw.start("parse request");
            NexacroContext context = NexacroContextHolder.getNexacroContext(request, response);
            PlatformData platformData = context.getPlatformData();
            if(logger.isDebugEnabled()) {
                logger.debug("got request=[{}]", new Debugger().detail(platformData));
            }
        } finally {
            sw.stop();
            if(performanceLogger.isTraceEnabled()) {
                performanceLogger.trace(sw.prettyPrint());
            }
        }
      
    }
    
}
