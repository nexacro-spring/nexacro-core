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

public class NexacroMappingExceptionResolver extends AbstractHandlerExceptionResolver {

	private final Logger logger = LoggerFactory.getLogger(NexacroMappingExceptionResolver.class);

	private String defaultErrorMsg = NexacroException.DEFAULT_MESSAGE;
	private boolean shouldSendStackTrace = false;
	private boolean shouldLogStackTrace = false;

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
    
    public void setDefaultErrorMsg(String defaultErrorMsg) {
		this.defaultErrorMsg = defaultErrorMsg;
	}

	public void setShouldSendStackTrace(boolean shouldSendStackTrace) {
		this.shouldSendStackTrace = shouldSendStackTrace;
	}
	
	public void setShouldLogStackTrace(boolean shouldLogStackTrace) {
		this.shouldLogStackTrace = shouldLogStackTrace;
	}

	public NexacroMappingExceptionResolver() {
    }
    
	@Override
	protected ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
	    
	    // nexacro 요청이 아닌 경우 별도 ExceptionResolver 가 처리 할 수 있도록 null을 반환 한다.
	    if(NexacroUtil.isNexacroRequest(request)) {
	        
	        prepareResolveException(request, response, handler, ex);
            
            // Nexacro Exception 만을 handling 하도록 한다.
            // for nexacro request
            NexacroModelAndView mav = new NexacroModelAndView(getView());
            
            if(ex instanceof NexacroException){ // NexacroConvertException
                NexacroException nexaExp = (NexacroException) ex;
                mav.setErrorCode(nexaExp.getErrorCode());
                mav.setErrorMsg(getExceptionMessage(ex));
            } else {
                // PlatformException..
                mav.setErrorCode(NexacroException.DEFAULT_ERROR_CODE);              //Undefined error Code
//                mav.setErrorMsg(NexacroException.DEFAULT_MESSAGE);         
                mav.setErrorMsg(getExceptionMessage(ex));         
            }
            
            return mav;
	    }
        
	    return null;
	}

	private void prepareResolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
	    
        logException(ex, request);
        
        if(this.shouldLogStackTrace) {
        	logger.error(ex.getMessage(), ex);
        }

	}

	private String getExceptionMessage(Exception e) {
		
		if(this.shouldSendStackTrace) {
			
			String message = e.getMessage();
			
			if(e instanceof NexacroException) {
				String errorMsg = ((NexacroException) e).getErrorMsg();
				if(errorMsg != null) {
					message = "errorMsg="+ errorMsg +", stackMessage=" +message;
				}
			}
			
			return message;
			
		} else {
			return this.defaultErrorMsg;
		}
		
	}
	
	
}
