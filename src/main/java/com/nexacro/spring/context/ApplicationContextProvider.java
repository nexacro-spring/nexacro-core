package com.nexacro.spring.context;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Spring의 ApplicationContext를 제공한다.
 * 
 * @author Park SeongMin
 * @since 10.15.2015
 * @version 1.0
 *
 */
public class ApplicationContextProvider implements ApplicationContextAware {

	// 참고 URL
	// http://blog.jdevelop.eu/?p=154
	// Spring의 Bean들이 로드 된 후 ApplicationContext가 설정된다.
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		// ApplicationContext가 초기화 된 후 호출 된다.
		SpringAppContext.getInstance().setApplicationContext(applicationContext);
	}

}
