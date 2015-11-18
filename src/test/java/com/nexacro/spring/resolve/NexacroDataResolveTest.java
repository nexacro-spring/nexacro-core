package com.nexacro.spring.resolve;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.StringReader;

import junit.framework.Assert;

import org.apache.commons.io.IOUtils;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultHandler;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import com.nexacro.spring.NexacroConstants;
import com.nexacro.spring.servlet.NexacroInterceptor;
import com.nexacro.spring.view.NexacroView;
import com.nexacro.xapi.data.DataSet;
import com.nexacro.xapi.data.PlatformData;
import com.nexacro.xapi.tx.DataDeserializer;
import com.nexacro.xapi.tx.DataSerializerFactory;
import com.nexacro.xapi.tx.PlatformException;
import com.nexacro.xapi.tx.PlatformType;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = { "classpath*:spring/context-servlet.xml" } )
public class NexacroDataResolveTest {

	/*
new test method.

MockMvcBuilders.standaloneMvcSetup(  
  new TestController()).build()
    .perform(get("/form"))
     .andExpect(status().isOk())
     .andExpect(content().type("text/plain"))
     .andExpect(content().string("hello world")
);

vs
old test method.

TestController controller = new TestController();  
MockHttpServletRequest req = new MockHttpRequest();  
MockHttpSerlvetResponse res = new MockHttpResponse();  
ModelAndView mav = controller.form(req, res);  
assertThat(res.getStatus(), is(200));  
assertThat(res.getContentType(), is(“text/plain”));
assertThat(res.getContentAsString (), is(“content”));
	
	*/
	
	@Autowired
	private WebApplicationContext wac;
	
	private MockMvc mockMvc;
	
	@Before
	public void init() {
		
		mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
		
		// 단일 Controller Test
//		mockMvc = MockMvcBuilders.standaloneSetup(new SampleController())
//				.setCustomArgumentResolvers(new NexacroMethodArgumentResolver()).build();
	}

	@Test
	public void testDefaultProcessing() throws Exception {
		
		MvcResult andReturn = mockMvc.perform(get("/default").content("")).andExpect(status().isOk()).andReturn();
		
		HandlerInterceptor[] interceptors = andReturn.getInterceptors();
		Assert.assertEquals("interceptor count does not match..", 1, interceptors.length);
		
		if(!(interceptors[0] instanceof NexacroInterceptor)) {
			Assert.fail(NexacroInterceptor.class+" not defined.");
		}
		
		ModelAndView modelAndView = andReturn.getModelAndView();
		Object platformDataObj = modelAndView.getModelMap().get(NexacroConstants.ATTRIBUTE.NEXACRO_PLATFORM_DATA);
		Assert.assertNotNull(NexacroConstants.ATTRIBUTE.NEXACRO_PLATFORM_DATA +" must be exist in model attribute.", platformDataObj);
		
		if(!(platformDataObj instanceof PlatformData)) {
			Assert.fail(NexacroConstants.ATTRIBUTE.NEXACRO_PLATFORM_DATA +" must be PlatformData instance.");
		}
		
		View view = modelAndView.getView();
		if(!(view instanceof NexacroView)) {
			Assert.fail("result rendering should be "+NexacroView.class);
		}
		
	}
	
	// 데이터셋의 컬럼의 order는 처리하지 않는다.
	@Test
	public void testResolveDataSetToBean() throws Exception {
		
		// dataset row type....
		String requestFileName = "src/test/java/com/nexacro/spring/resolve/httpRequest.xml";
		InputStream requestInputStream = new FileInputStream(new File(requestFileName));
		byte[] byteArray = IOUtils.toByteArray(requestInputStream);
		
		MvcResult andReturn = mockMvc.perform(get("/DataSetToBean").content(byteArray).contentType(MediaType.TEXT_XML))
					.andExpect(status().isOk())
					.andExpect(content().contentType("text/xml;charset=UTF-8"))
					.andReturn();

		MockHttpServletResponse servletResponse = andReturn.getResponse();
		String actualResult = servletResponse.getContentAsString();
		
		String responseFileName = "src/test/java/com/nexacro/spring/resolve/httpResponse.xml";
		InputStream responseInputStream = new FileInputStream(new File(responseFileName));
		String expectedResult = IOUtils.toString(responseInputStream);
		
		Assert.assertEquals("result data has not resolved.", expectedResult, actualResult);
		
	}
	
	@Test
	public void testResolveDataSetToMap() throws Exception {
		
		String requestFileName = "src/test/java/com/nexacro/spring/resolve/httpRequest.xml";
		InputStream requestInputStream = new FileInputStream(new File(requestFileName));
		byte[] byteArray = IOUtils.toByteArray(requestInputStream);
		
		MvcResult andReturn = mockMvc.perform(get("/DataSetToMap").content(byteArray).contentType(MediaType.TEXT_XML))
					.andExpect(status().isOk())
					.andExpect(content().contentType("text/xml;charset=UTF-8"))
					.andReturn();
		
		MockHttpServletResponse servletResponse = andReturn.getResponse();
		String actualResult = servletResponse.getContentAsString();
		
		String responseFileName = "src/test/java/com/nexacro/spring/resolve/httpResponseMap.xml";
		InputStream responseInputStream = new FileInputStream(new File(responseFileName));
		String expectedResult = IOUtils.toString(responseInputStream);

		Assert.assertEquals("result data has not resolved.", expectedResult, actualResult);
		
//		DataDeserializer deserializer = DataSerializerFactory.getDeserializer(PlatformType.CONTENT_TYPE_XML);
//		PlatformData readData = deserializer.readData(new FileReader(new File(responseFileName)), null, PlatformType.DEFAULT_CHAR_SET);
//		DataSet expectedDataSet = readData.getDataSet("dsResult");
//		
//		// Map 변환 시 column의 order를 처리하지 않기 때문에 데이터셋으로 비교한다.
//		readData = deserializer.readData(new StringReader(actualResult), null, PlatformType.DEFAULT_CHAR_SET);
//		DataSet actualDataSet = readData.getDataSet("dsResult");
//		
//		Assert.assertTrue("Result 'DataSet' structure not same.", expectedDataSet.equalsStructure(actualDataSet));
//		Assert.assertTrue("Result 'DataSet' data should be same.", expectedDataSet.equalsData(actualDataSet));
		
	}
	
	@Test
	public void testResolveVariable() throws Exception {
		
		String requestFileName = "src/test/java/com/nexacro/spring/resolve/httpRequest.xml";
		InputStream requestInputStream = new FileInputStream(new File(requestFileName));
		byte[] byteArray = IOUtils.toByteArray(requestInputStream);
		
		MvcResult andReturn = mockMvc.perform(get("/Variable").content(byteArray).contentType(MediaType.TEXT_XML))
					.andExpect(status().isOk())
					.andExpect(content().contentType("text/xml;charset=UTF-8"))
					.andReturn();
		
		MockHttpServletResponse servletResponse = andReturn.getResponse();
		String actualResult = servletResponse.getContentAsString();
		
		String responseFileName = "src/test/java/com/nexacro/spring/resolve/httpResponseVariable.xml";
		InputStream responseInputStream = new FileInputStream(new File(responseFileName));
		String expectedResult = IOUtils.toString(responseInputStream);

		Assert.assertEquals("result data has not resolved.", expectedResult, actualResult);
		
	}
	
	@Test
	public void testSupportedParameter() throws Exception {
		
		mockMvc.perform(get("/SupportedParameter").content("").contentType(MediaType.TEXT_XML))
				.andExpect(status().isOk())
				.andExpect(content().contentType("text/xml;charset=UTF-8"));
		
	}
	
	@Test
	public void testFirstRowStatus() throws Exception {
		
		MvcResult result = mockMvc.perform(get("/NexacroFirstRowStatus").content("").contentType(MediaType.TEXT_XML))
				.andExpect(status().isOk())
				.andExpect(content().contentType("text/xml;charset=UTF-8"))
				.andReturn();
	
		// check response
		String responseString = result.getResponse().getContentAsString();
		
		PlatformData readData = readData(responseString);
		
		DataSet dummy = readData.getDataSet("dummy");
		Assert.assertNotNull("DataSet 'dummy' should be sended.", dummy);
		
		DataSet firstRowStatusDs = readData.getDataSet(NexacroConstants.ERROR_FIRST_ROW.ERROR_DATASET);
		Assert.assertNotNull("FirstRow status must be transmitted.", firstRowStatusDs);
		
		// row count
		int actualRowCount = firstRowStatusDs.getRowCount();
		int expectedRowCount = 1;
		Assert.assertEquals(expectedRowCount, actualRowCount);
		
		// default error code
		int actualErrorCode = 0;
		int expectedErrorCode = firstRowStatusDs.getInt(0, NexacroConstants.ERROR_FIRST_ROW.ERROR_CODE);
		Assert.assertEquals(expectedErrorCode, actualErrorCode);
		
		// default error msg
		String actualErrorMsg = null;
		String expectedErrorMsg = firstRowStatusDs.getString(0, NexacroConstants.ERROR_FIRST_ROW.ERROR_MSG);
		Assert.assertEquals(expectedErrorMsg, actualErrorMsg);
		
	}
	
	@Test
	public void testNotEnterRequiredDataSet() throws Exception {
		
		mockMvc.perform(get("/NotEnterRequiredDataSet").content("").contentType(MediaType.TEXT_XML))
				.andExpect(status().isOk())
				.andExpect(content().contentType("text/xml;charset=UTF-8"))
				.andDo(new ResultHandler() {
					@Override
					public void handle(MvcResult result) throws Exception {
						Exception resolvedException = result.getResolvedException();
						if(resolvedException == null) {
							Assert.fail("if you do not enter the mandatory parameters should result in an exception."); 
						}
						if(!(resolvedException instanceof MissingNexacroParameterException)) {
							Assert.fail("if you do not enter a mandatory parameter 'MissingNexacroParameterException' exceptions should be occured."); 
						}
					}
				});
		
	}
	
	@Test
	public void testNotEnterRequiredVariable() throws Exception {
		
		mockMvc.perform(get("/NotEnterRequiredVariable").content("").contentType(MediaType.TEXT_XML))
		.andExpect(status().isOk())
		.andExpect(content().contentType("text/xml;charset=UTF-8"))
		.andDo(new ResultHandler() {
			@Override
			public void handle(MvcResult result) throws Exception {
				Exception resolvedException = result.getResolvedException();
				if(resolvedException == null) {
					Assert.fail("if you do not enter the mandatory parameters should result in an exception."); 
				}
				if(!(resolvedException instanceof MissingNexacroParameterException)) {
					Assert.fail("if you do not enter a mandatory parameter 'MissingNexacroParameterException' exceptions should be occured."); 
				}
			}
		});

	}
	
	@Test
	public void testOptionalDataSet() throws Exception {
		
		mockMvc.perform(get("/OptionalDataSet").content("").contentType(MediaType.TEXT_XML))
				.andExpect(status().isOk())
				.andExpect(content().contentType("text/xml;charset=UTF-8"))
				.andDo(new ResultHandler() {
					@Override
					public void handle(MvcResult result) throws Exception {
						Exception resolvedException = result.getResolvedException();
						if(resolvedException != null) {
							Assert.fail("parameter is not mandatory"); 
						}
					}
				});
		
	}
	
	@Test
	public void testOptionalVariable() throws Exception {
		
		mockMvc.perform(get("/OptionalVariable").content("").contentType(MediaType.TEXT_XML))
				.andExpect(status().isOk())
				.andExpect(content().contentType("text/xml;charset=UTF-8"))
				.andDo(new ResultHandler() {
					@Override
					public void handle(MvcResult result) throws Exception {
						Exception resolvedException = result.getResolvedException();
						if(resolvedException != null) {
							Assert.fail("parameter is not mandatory"); 
						}
					}
				});
		
	}
	
	@Test
	public void testUnsupportedVoidMethod() throws Exception {
		
		// void 메서드의 경우 NexacroView에서 처리 하지 않는다.
		
		mockMvc.perform(get("/Void").content("").contentType(MediaType.TEXT_XML))
			.andExpect(status().isOk()).andDo(new ResultHandler() {
				@Override
				public void handle(MvcResult result) throws Exception {
					// nothing
				}
			})
			.andExpect(content().string(new BaseMatcher<String>() {
				@Override
				public boolean matches(Object response) {
					String responseString = (String) response;
					if(responseString.equals("")) {
						return true;
					}
					return false;
				}
				@Override
				public void describeTo(Description desc) {
					desc.appendText("must be empty response. NexacroView should not be processed.");
				}
			}));
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
	
}
