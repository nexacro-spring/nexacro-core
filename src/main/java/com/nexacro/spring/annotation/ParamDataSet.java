package com.nexacro.spring.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.nexacro.spring.context.NexacroContextHolder;

/**
 * <p><code>DataSet</code>을 List혹은 POJO형태의 데이터로 변환을 수행하기 위한 annotation이다.
 * 
 * <p>Spring의 Controller내 메서드 파라매터에서 사용가능하다. 아래는 doService 메서드 호출 시 데이터셋 변환 예제이다.
 * <blockquote>
 * Ex> public void doService(@ParamDataSet(name="dsUnit") List<Map> dsUnits)
 * </blockquote>
 * @author Park SeongMin
 * @since 07.28.2015
 * @version 1.0
 * @see NexacroContextHolder
 */
@Target({ java.lang.annotation.ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ParamDataSet {
	/**
	 * 데이터셋의 식별자
	 * @return dsName
	 */
	public abstract String name();
}