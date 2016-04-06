package com.nexacro.spring.servicelayout;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.context.support.XmlWebApplicationContext;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@Configuration
@ComponentScan(basePackages={"com.nexacro.spring.servicelayout", "com.nexacro.spring.resolve"})
@EnableWebMvc
public class RequestMappingView {
	public static void main(String[] args) {
		
		MockServletContext servletContext = new MockServletContext();
		/* JavaConfig 설정시 */
		 AnnotationConfigWebApplicationContext wac = new AnnotationConfigWebApplicationContext();
		 wac.setServletContext(servletContext);
		 wac.register(RequestMappingView.class);
		 wac.refresh();

		/* xml config 설정시 */
//		XmlWebApplicationContext wac = new XmlWebApplicationContext();
//		wac.setServletContext(servletContext);
//		wac.setConfigLocations(new String[] { "com/nexacro/spring/servicelayout/application-config.xml" });
//		wac.refresh();
		
		 ParameterNameDiscoverer parameterNameDiscoverer = new LocalVariableTableParameterNameDiscoverer();
		 
		RequestMappingHandlerMapping mapping = wac.getBean(RequestMappingHandlerMapping.class);
		Map<RequestMappingInfo, HandlerMethod> map = mapping.getHandlerMethods();
		
		for (final Map.Entry<RequestMappingInfo, HandlerMethod> entry : map.entrySet()) {
			
			RequestMappingInfo requestMappingInfo = entry.getKey();
			HandlerMethod handlerMethod = entry.getValue();
			System.out.println(handlerMethod.getMethod().getName());
			
			String[] parameterNames = parameterNameDiscoverer.getParameterNames(handlerMethod.getMethod());
			
			Annotation[][] paramAnnotaions = handlerMethod.getMethod().getParameterAnnotations();
			Set<String> parameters = new HashSet<String>();
			for (int i = 0; i < paramAnnotaions.length; i++) {
				Annotation[] annotations = paramAnnotaions[i];
				for (int j = 0; j < annotations.length; j++) {
					Annotation annotation = annotations[j];
					
					System.out.println("\t"+parameterNames[i]);

					if (annotation instanceof RequestParam) {
						parameters.add(parameterNames[i]);
					}
				}
			}

			Set<String> patterns = requestMappingInfo.getPatternsCondition()
					.getPatterns();
			for (String pattern : patterns) {
				ServiceLayout api = new ServiceLayout(pattern, requestMappingInfo.getMethodsCondition().getMethods(), parameters);
//				serviceLayouts.add(api);
				System.out.println(api.toString());
			}
			
//			System.out.println(entry.getKey().getPatternsCondition().getPatterns() + " : " + entry.getValue().getMethod());
		}
		
		wac.close();
	}
}