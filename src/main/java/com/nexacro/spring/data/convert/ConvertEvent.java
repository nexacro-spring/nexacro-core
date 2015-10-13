package com.nexacro.spring.data.convert;

import java.util.EventObject;

/**
 * <pre>
 * Statements
 * </pre>
 *
 * @ClassName   : ConvertEvent.java
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

public class ConvertEvent extends EventObject {

    /* serialVersionUID */
    private static final long serialVersionUID = -8987092428557969722L;
    
    /* converted value */
    private Object value;
    
    /**
     * Statements
     *
     * @param source
     */
    public ConvertEvent(Object source, Object value) {
        super(source);
        this.value = value;
    }

    /**
     * @return the convertedValue
     */
    public Object getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(Object value) {
        this.value = value;
    }
    
}
