package com.nexacro.spring.context;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Spring의 Bean을 획득하기 위한 ApplicationContext를 제공한다.
 * @author Park SeongMin
 *
 */
public class ApplicationContextProvider implements ApplicationContextAware {

	// 참고 URL
	// http://blog.jdevelop.eu/?p=154
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		// ApplicationContext가 초기화 된 후 호출 된다.
		SpringAppContext.getInstance().setApplicationContext(applicationContext);
	}

}
