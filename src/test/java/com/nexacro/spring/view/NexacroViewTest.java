package com.nexacro.spring.view;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.nexacro.spring.NexacroConstants;
import com.nexacro.xapi.data.PlatformData;
import com.nexacro.xapi.data.Variable;
import com.nexacro.xapi.tx.DataDeserializer;
import com.nexacro.xapi.tx.DataSerializerFactory;
import com.nexacro.xapi.tx.PlatformType;

public class NexacroViewTest {
	
	@Test
	public void testRender() throws Exception {
		
		NexacroView view = new NexacroView();
		
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();
		
		Map<String, Object> model = new HashMap<String, Object>();
		
		// initialize RequestContextHolder. (used NexacroView..)
		ServletRequestAttributes attributes = new ServletRequestAttributes(request);
		RequestContextHolder.setRequestAttributes(attributes);
		
		view.render(model, request, response);
		
		String contentAsString = response.getContentAsString();
		
		DataDeserializer deserializer = DataSerializerFactory.getDeserializer(PlatformType.CONTENT_TYPE_XML);
		PlatformData readData = deserializer.readData(new StringReader(contentAsString), null, PlatformType.DEFAULT_CHAR_SET);
		Variable errorCodeVariable = readData.getVariable(NexacroConstants.ERROR.ERROR_CODE);
		Assert.assertNotNull("Variable 'ErrorCode' should not be null.", errorCodeVariable);
		
		int expectedErrorCode = NexacroConstants.ERROR.DEFAULT_ERROR_CODE;
		int actualErrorCode = errorCodeVariable.getInt();
		
		Assert.assertEquals("successfully ErrorCode must be zero", expectedErrorCode, actualErrorCode);
		
	}

}
