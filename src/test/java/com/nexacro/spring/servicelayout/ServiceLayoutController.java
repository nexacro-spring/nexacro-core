package com.nexacro.spring.servicelayout;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@Controller
public class ServiceLayoutController {

	// ---------- getter/setter------------
//	@Value("#{config}")
//	private Properties config;

	private final RequestMappingHandlerMapping handlerMapping;

	@Autowired
	public ServiceLayoutController(RequestMappingHandlerMapping handlerMapping) {
		this.handlerMapping = handlerMapping;
	}

	@RequestMapping("/serviceLayout.do")
	public String execute(HttpServletRequest request, Model model)
			throws IllegalAccessException {

		// session check..
		// LoginUser loginUser = (LoginUser)
		// request.getSession().getAttribute("loginUser");

		ParameterNameDiscoverer parameterNameDiscoverer = new LocalVariableTableParameterNameDiscoverer();
		List<ServiceLayout> serviceLayouts = new ArrayList<ServiceLayout>();

		Map<RequestMappingInfo, HandlerMethod> handlerMethods = this.handlerMapping.getHandlerMethods();
		Set<RequestMappingInfo> reqMappingInfoKeys = handlerMethods.keySet();

		for (RequestMappingInfo requestMappingInfoKey : reqMappingInfoKeys) {

			HandlerMethod handlerMethod = handlerMethods.get(requestMappingInfoKey);
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

			Set<String> patterns = requestMappingInfoKey.getPatternsCondition()
					.getPatterns();
			for (String pattern : patterns) {
				ServiceLayout api = new ServiceLayout(pattern,
						requestMappingInfoKey.getMethodsCondition()
								.getMethods(), parameters);
				serviceLayouts.add(api);
			}
			
		}

		model.addAttribute("apis", serviceLayouts);

		return "serviceLayout";
	}

}
