package com.nexacro.spring.data.convert;

import java.util.EventObject;

/**
 * <p>{@link NexacroConverter#convert(Object, ConvertDefinition)}에서 데이터 변환 시 
 * <code>DataSet</code>의 행의 값 변환 혹은 <code>Variable</code>의 값 변환 시 처리되는 EventObject이다.
 *
 * @author Park SeongMin
 * @since 08.09.2015
 * @version 1.0
 * @see
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
     * 데이터 변환이 이루어지는  현재 값을 반환한다.
     * @return the convertedValue
     */
    public Object getValue() {
        return value;
    }

    /**
     * 데이터 변환이 이루어지는 값을 변경한다.
     * @param value the value to set
     */
    public void setValue(Object value) {
        this.value = value;
    }
    
}
