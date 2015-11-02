package com.nexacro.spring.resolve;

import java.util.Locale;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.handler.AbstractHandlerExceptionResolver;

import com.nexacro.spring.NexacroException;
import com.nexacro.spring.servlet.NexacroInterceptor;
import com.nexacro.spring.util.NexacroUtil;
import com.nexacro.spring.view.NexacroModelAndView;
import com.nexacro.spring.view.NexacroView;

/**
 * nexacro platform 요청에 대한 예외 발생 시 처리되는 {@link HandlerExceptionResolver} 이다.
 * <p>{@link NexacroInterceptor}와 매핑 된 예외만을 처리한다.
 *
 * @author Park SeongMin
 * @since 08.03.2015
 * @version 1.0
 * @see NexacroException
 * 
 */
public class NexacroMappingExceptionResolver extends AbstractHandlerExceptionResolver {

	private final Logger logger = LoggerFactory.getLogger(NexacroMappingExceptionResolver.class);

	private MessageSource messageSource;
	
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

    /**
     * 예외 발생 시 처리되는 {@link org.springframework.web.servlet.View}를 반환한다.
     * 
     * @param view
     */
    public void setView(View view) {
        this.view = view;
    }
    
    /**
     * 예외 발생 시 응답으로 전송되는 기본 에러메시지이다.
     * 
     * @param defaultErrorMsg
     * @see #setShouldSendStackTrace(boolean)
     */
    public void setDefaultErrorMsg(String defaultErrorMsg) {
		this.defaultErrorMsg = defaultErrorMsg;
	}

    /**
     * 응답으로 예외의 메세지 정보를 전송할지에 대한 설정이다.
     * <p>설정 된 값이 <code>false</code>일 경우 예외 정보는 응답으로 전달되지 않는다. 
     * <p>하지만 {@link NexacroException#setErrorMsg(String)} 에러메시지가 설정 된 경우 해당 메시지가 전송된다. 그렇지 않을 경우 {@link #setDefaultErrorMsg(String)}로 설정 된 값이 응답으로 전송된다.
     * 
     * @param shouldSendStackTrace
     * @see #setDefaultErrorMsg(String)
     */
	public void setShouldSendStackTrace(boolean shouldSendStackTrace) {
		this.shouldSendStackTrace = shouldSendStackTrace;
	}
	
	/**
	 * 예외 정보를 로깅할지에 대한 설정이다. 
	 * @param shouldLogStackTrace
	 */
	public void setShouldLogStackTrace(boolean shouldLogStackTrace) {
		this.shouldLogStackTrace = shouldLogStackTrace;
	}

	/**
	 * {@link MessageSource}를 설정한다.
	 * 
	 * @param messageSource
	 */
	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}
	
	protected ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
	    
		prepareResolveException(request, response, handler, ex);
		
		// nexacro 요청이 아닌 경우 별도 ExceptionResolver 가 처리 할 수 있도록 null을 반환 한다.
	    if(NexacroUtil.isNexacroRequest(request)) {
            
            // Nexacro Exception 만을 handling 하도록 한다.
            // for nexacro request
            NexacroModelAndView mav = new NexacroModelAndView(getView());
            
            if(ex instanceof NexacroException){ // NexacroConvertException
                NexacroException nexaExp = (NexacroException) ex;
                mav.setErrorCode(nexaExp.getErrorCode());
                mav.setErrorMsg(getExceptionSendMessage(ex));
            } else {
                // PlatformException..
                mav.setErrorCode(NexacroException.DEFAULT_ERROR_CODE);              //Undefined error Code
//                mav.setErrorMsg(NexacroException.DEFAULT_MESSAGE);         
                mav.setErrorMsg(getExceptionSendMessage(ex));         
            }
            
            return mav;
	    }
        
	    return null;
	}

	private void prepareResolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
	    
        logException(ex, request);

        if(this.shouldLogStackTrace) {
        	String exceptionMessage = getExceptionLogMessage(ex);
        	logger.error(exceptionMessage, ex);
        }
        
	}

	private String getExceptionLogMessage(Exception e) {
		
		String exceptionMessage = getLocalizedMessage(e.getMessage());
		
		if(e instanceof NexacroException) {
			String userErrorMsg = getLocalizedMessage(((NexacroException) e).getErrorMsg());
			exceptionMessage = "errorMsg="+ userErrorMsg +", stackMessage=" +exceptionMessage;
		}
		
		return exceptionMessage;
	}
	
	private String getExceptionSendMessage(Exception e) {
		
		String userErrorMsg = null;
		if(e instanceof NexacroException) {
			userErrorMsg = getLocalizedMessage(((NexacroException) e).getErrorMsg());
		}
		
		String exceptionMessage = null;
		if(this.shouldSendStackTrace) {
			
			// 혹은 cause message... 
			// 혹은 사용자가 예외를 핸들링 하기 때문에 사용자가 정의한 메세지를 던지도록 할까..?
			exceptionMessage = getLocalizedMessage(e.getMessage());
			if(userErrorMsg != null) {
				exceptionMessage = "errorMsg="+ userErrorMsg +", stackMessage=" +exceptionMessage;
			}
			
		} else {
			if(userErrorMsg != null) {
				exceptionMessage = userErrorMsg;
			} else {
				String localizedMessage = getLocalizedMessage(this.defaultErrorMsg);
				exceptionMessage = localizedMessage;
			}
		}
		
		return exceptionMessage;
	}
	
	private String getLocalizedMessage(String reason) {
		if(reason == null) {
			return null;
		}
		if (this.messageSource != null) {
			reason = this.messageSource.getMessage(reason, null,reason, LocaleContextHolder.getLocale());
		}
		return reason;
	}
	
	
}
