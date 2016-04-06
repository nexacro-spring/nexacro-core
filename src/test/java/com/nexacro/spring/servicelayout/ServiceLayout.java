package com.nexacro.spring.servicelayout;

import java.util.Set;

import org.springframework.web.bind.annotation.RequestMethod;

public class ServiceLayout {

	private String pattern;
	private Set<RequestMethod> methods;
	private Set<String> parameters;

	public ServiceLayout(String pattern, Set<RequestMethod> methods,
			Set<String> parameters) {
		this.pattern = pattern;
		this.methods = methods;
		this.parameters = parameters;
	}

	public String getPattern() {
		return pattern;
	}

	public Set<RequestMethod> getMethods() {
		return methods;
	}

	public Set<String> getParameters() {
		return parameters;
	}

	public String getParameterString() {
		StringBuilder builder = new StringBuilder();

		int loopCnt = 1;
		for (String parameter : parameters) {
			builder.append(parameter + "=");
			if (loopCnt < parameters.size()) {
				builder.append("&");
			}

			loopCnt++;
		}

		return builder.toString();
	}

	@Override
	public String toString() {
		return "ServiceLayout [getPattern()=" + getPattern()
				+ ", getMethods()=" + getMethods() + ", getParameters()="
				+ getParameters() + "]";
	}

}
