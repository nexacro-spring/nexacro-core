package com.nexacro.spring.beans.factory.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

/**
 * context-configuration.xml에 정의 된 propertyPlaceholderConfigurer 적용.
 *
 */
public class MvcPropertyPlaceholderConfigurer implements BeanFactoryPostProcessor {

 /*
ContextLoaderListener에서 로드되는 Context-Configuration.xml에서 로드 되는 PropertyPlaceHolderCongiurer는
해당 설정 파일들이 로드 된 후 해당 Factory가 생성 된후  properties를 변경하게 된다.
하지만 dispatcher-servlet.xml이 로드 될 때의 시점과 달라 context-configuration.xml에 정의 된 PropertyPlaceHolderConfigurer가 적용되지 않는다. 

이렇게 처리하는 방법 과 별도로 dispatcher-servlet.xml에  <context:property-placeholder location="classpath:..."/>로 정의하는 방법이 있다.
하지만 암호화 처리를 위한 bean 등록도 필요하다.
 */
 
	private PropertyPlaceholderConfigurer configurer;

	public MvcPropertyPlaceholderConfigurer(PropertyPlaceholderConfigurer configurer) {
		this.configurer = configurer;
	}

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		configurer.postProcessBeanFactory(beanFactory);
	}
	

}