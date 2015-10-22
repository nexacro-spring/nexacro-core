package com.nexacro.spring.data.convert;

import com.nexacro.xapi.data.Variable;

/**
 * <p>Variable의 데이터 변환 시  처리되는 EventObject이다.
 * 
 * @author Park SeongMin
 * @since 08.09.2015
 * @version 1.0
 * @see
 */
public class ConvertVariableEvent extends ConvertEvent {

    /* serialVersionUID */
    private static final long serialVersionUID = 7527494468054562758L;
    
    /**
     * Statements
     *
     * @param source
     */
    public ConvertVariableEvent(Variable source, Object targetValue) {
        super(source, targetValue);
    }

}
