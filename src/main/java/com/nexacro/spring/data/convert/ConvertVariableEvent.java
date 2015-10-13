package com.nexacro.spring.data.convert;

import com.nexacro.xapi.data.Variable;

/**
 * <pre>
 * Statements
 * </pre>
 *
 * @ClassName   : ConvertVariableEvent.java
 * @Description : 클래스 설명을 기술합니다.
 * @author Park SeongMin
 * @since 2015. 8. 9.
 * @version 1.0
 * @see
 * @Modification Information
 * <pre>
 *     since          author              description
 *  ===========    =============    ===========================
 *  2015. 8. 9.     Park SeongMin     최초 생성
 * </pre>
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
