package com.nexacro.spring.data.convert;

import java.util.EventListener;

/**
 * <p>{@link NexacroConverter#convert(Object, ConvertDefinition)}에서 데이터 변환 시 
 * <code>DataSet</code>의 행의 값 변환 혹은 <code>Variable</code>의 값 변경을 처리하는 <code>EventListener</code>이다.
 *
 * @author Park SeongMin
 * @since 08.09.2015
 * @version 1.0
 * @see
 */

public interface NexacroConvertListener extends EventListener {
    
	/**
	 * 대상 객체에 값이 할당되기 직전 호출 된다.
	 * <code>DataSet</code>의 각행의 값 변환 혹은 <code>Variable</code>의 값 변환
	 * @param event
	 */
    void convertedValue(ConvertEvent event);

}
