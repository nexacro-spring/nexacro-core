package com.nexacro.spring.resolve;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.handler.AbstractHandlerExceptionResolver;

import com.nexacro.spring.NexacroException;
import com.nexacro.spring.util.NexacroUtil;
import com.nexacro.spring.view.NexacroModelAndView;
import com.nexacro.spring.view.NexacroView;

public class NexcroMappingExceptionResolver extends AbstractHandlerExceptionResolver {

	private final Logger logger = LoggerFactory.getLogger(NexcroMappingExceptionResolver.class);

    private View view;

    public View getView() {
        if(view == null) {
            return new NexacroView();
        } else {
            return view;
        }
    }

    public void setView(View view) {
        this.view = view;
    }
    
    public NexcroMappingExceptionResolver() {
    }
    
	@Override
	protected ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
	    
	    // nexacro 요청이 아닌 경우 별도 ExceptionResolver 가 처리 할 수 있도록 null을 반환 한다.
	    if(NexacroUtil.isNexacroRequest(request)) {
	        
	        // for able framework
	        prepareResolveException(request, response, handler, ex);
            
            // Nexacro Exception 만을 handling 하도록 한다.
            // for nexacro request
            NexacroModelAndView mav = new NexacroModelAndView(getView());
            
            if(ex instanceof NexacroException){ // NexacroConvertException
                NexacroException nexaExp = (NexacroException) ex;
                mav.setErrorCode(nexaExp.getErrorCode());
                mav.setErrorMsg(nexaExp.getErrorMsg());
            } else {
                // PlatformException..
                mav.setErrorCode(NexacroException.DEFAULT_ERROR_CODE);              //Undefined error Code
//                mav.setErrorMsg(NexacroException.DEFAULT_MESSAGE);         
                mav.setErrorMsg(ex.toString());         
            }
            
            return mav;
	    }
        
	    return null;
	}

	private void prepareResolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
	    
	    if (this.logger.isDebugEnabled()) {
            this.logger.debug("Resolving exception from handler [" + handler + "]: " + ex);
        }
        
        logException(ex, request);

	}

}
