package com.nexacro.spring.resolve;

import java.io.StringReader;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.WebApplicationContext;

import com.nexacro.spring.NexacroConstants;
import com.nexacro.spring.NexacroException;
import com.nexacro.spring.data.NexacroResult;
import com.nexacro.xapi.data.PlatformData;
import com.nexacro.xapi.data.Variable;
import com.nexacro.xapi.tx.DataDeserializer;
import com.nexacro.xapi.tx.DataSerializerFactory;
import com.nexacro.xapi.tx.PlatformException;
import com.nexacro.xapi.tx.PlatformType;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = { "classpath*:spring/context-servlet.xml" } )
public class NexacroExceptionResolveTest {

	@Autowired
	private WebApplicationContext wac;
	
	private MockMvc mockMvc;
	
	@Before
	public void init() {
		mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
	}

	@Test
	public void testNexacroException() throws Exception {

		MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/NexacroException").content(""))
									// 200 ok
									.andExpect(MockMvcResultMatchers.status().isOk())
									.andReturn();
		
		// resolved exception..
		Exception resolvedException = result.getResolvedException();
		Assert.assertNotNull("Exception should be resolved.", resolvedException);
		
		// check response
		String responseString = result.getResponse().getContentAsString();
		
		PlatformData readData = readData(responseString);
		
		// check errorcode
		Variable errorCodeVariable = readData.getVariable(NexacroConstants.ERROR.ERROR_CODE);
		Assert.assertNotNull("ErrorCode must be not null. an exception occurs should be the exception information(nexacro ErrorCode, ErrorMsg) is returned", errorCodeVariable);
		// defined ErrorCode
		int expectedErrorCode = -88853;
		int actualErrorCode = errorCodeVariable.getInt();
		Assert.assertEquals("Variable 'ErrorCode' must be '-88853'.", expectedErrorCode, actualErrorCode); 
		
		// check errormsg
		Variable errorMsgVariable = readData.getVariable(NexacroConstants.ERROR.ERROR_MSG);
		Assert.assertNotNull("ErrorMsg must be not null. an exception occurs should be the exception information(nexacro ErrorCode, ErrorMsg) is returned", errorMsgVariable);
		String expectedErrorMsg = "errorMsg=user message, stackMessage=nexacro exception";
		String actualErrorMsg = errorMsgVariable.getString();
		Assert.assertEquals("Variable 'ErrorMsg' must be transfered defined message.", expectedErrorMsg, actualErrorMsg); 
		
	}

	@Test
	public void testAnotherException() throws Exception {

		MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/AnotherException").content(""))
										// 200 ok
										.andExpect(MockMvcResultMatchers.status().isOk())
										.andReturn();

		// resolved exception..
		Exception resolvedException = result.getResolvedException();
		Assert.assertNotNull("Exception should be resolved.", resolvedException);
		
		// check response
		String responseString = result.getResponse().getContentAsString();
		
		PlatformData readData = readData(responseString);
		
		// check errorcode
		Variable errorCodeVariable = readData.getVariable(NexacroConstants.ERROR.ERROR_CODE);
		Assert.assertNotNull("ErrorCode must be not null. an exception occurs should be the exception information(nexacro ErrorCode, ErrorMsg) is returned", errorCodeVariable);
		// default errorCode
		int expectedErrorCode = -1;
		int actualErrorCode = errorCodeVariable.getInt();
		Assert.assertEquals("Variable 'ErrorCode' must be '-1'.", expectedErrorCode, actualErrorCode); 
		
		// check errormsg
		Variable errorMsgVariable = readData.getVariable(NexacroConstants.ERROR.ERROR_MSG);
		Assert.assertNotNull("ErrorMsg must be not null. an exception occurs should be the exception information(nexacro ErrorCode, ErrorMsg) is returned", errorMsgVariable);
		String expectedErrorMsg = "another exception";
		String actualErrorMsg = errorMsgVariable.getString();
		Assert.assertEquals("Variable 'ErrorMsg' must be transfered defined message.", expectedErrorMsg, actualErrorMsg);
		
	}

	@Test
	public void testSendUserExceptionMessage() {

	}

	@Test
	public void testSendStackMessage() {
		// ExceptionResolver의 설정값을 변경해서 Test. 별도 Config를 설정하자.
	}
	
	private PlatformData readData(String responseString) {
		PlatformData readData = null;
		DataDeserializer deserializer = DataSerializerFactory.getDeserializer(PlatformType.CONTENT_TYPE_XML);
		try {
			readData = deserializer.readData(new StringReader(responseString), null, PlatformType.DEFAULT_CHAR_SET);
		} catch (PlatformException e) {
			Assert.fail("response string deserialize failed. e=" + e.getMessage());
		}
		
		return readData;
	}
	
	
	@Controller
	public static class ExceptionController {
		
		@RequestMapping("/NexacroException")
		public NexacroResult throwNexacroException() throws NexacroException {
			
			boolean occuredException = true;
			if(occuredException) {
				throw new NexacroException("nexacro exception", -88853, "user message");
			}
			
			return new NexacroResult();
		}
		
		@RequestMapping("/AnotherException")
		public NexacroResult throwAnotherException() throws Exception {
			
			boolean occuredException = true;
			if(occuredException) {
				throw new IllegalAccessException("another exception");
			}
			
			return new NexacroResult();
		}
		
	}

}
