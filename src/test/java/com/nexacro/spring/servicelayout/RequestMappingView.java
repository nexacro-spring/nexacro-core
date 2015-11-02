package com.nexacro.spring.servicelayout;

import java.util.Map;

import org.springframework.mock.web.MockServletContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.context.support.XmlWebApplicationContext;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

public class RequestMappingView {
	public static void main(String[] args) {
		
		MockServletContext servletContext = new MockServletContext();
		/* JavaConfig 설정시 */
		// AnnotationConfigWebApplicationContext wac = new
		// AnnotationConfigWebApplicationContext();
		// wac.setServletContext(servletContext);
		// wac.register(WebConfig.class);

		/* xml config 설정시 */
		XmlWebApplicationContext wac = new XmlWebApplicationContext();
		wac.setServletContext(servletContext);
		wac.setConfigLocations(new String[] { "com/nexacro/spring/servicelayout/application-config.xml" });
		wac.refresh();
		
		RequestMappingHandlerMapping mapping = wac.getBean(RequestMappingHandlerMapping.class);
		Map<RequestMappingInfo, HandlerMethod> map = mapping.getHandlerMethods();
		
		for (final Map.Entry<RequestMappingInfo, HandlerMethod> entry : map.entrySet()) {
			System.out.println(entry.getKey().getPatternsCondition().getPatterns() + " : " + entry.getValue().getMethod());
		}
		
		wac.close();
	}
}