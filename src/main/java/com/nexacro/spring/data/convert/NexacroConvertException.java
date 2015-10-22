package com.nexacro.spring.data.convert;

import com.nexacro.spring.NexacroException;

/**
 * <p>{@code NexacroConverter}에서 데이터 변환시 발생하는 예외이다.
 *
 * @author Park SeongMin
 * @since 2015. 7. 28.
 * @version 1.0
 * @see
 */

public class NexacroConvertException extends NexacroException {

    /* serialVersionUID */
    private static final long serialVersionUID = 2572392591528637297L;

    /**
     * 기본 생성자이다.
     */
    public NexacroConvertException() {
        ;
    }

    /**
     * 메시지를 가지는 생성자이다.
     * 
     * @param message
     *            메시지
     */
    public NexacroConvertException(String message) {
        super(message);
    }

    /**
     * 메시지와 원천(cause) 예외를 가지는 생성자이다.
     * 
     * @param message
     *            메시지
     * @param cause
     *            원천 예외
     */
    public NexacroConvertException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
