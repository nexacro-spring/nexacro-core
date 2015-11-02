package com.nexacro.spring.resolve;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.stereotype.Controller;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.view.JstlView;
import org.springframework.web.servlet.view.UrlBasedViewResolver;

import com.nexacro.spring.NexacroConstants;
import com.nexacro.spring.data.NexacroResult;
import com.nexacro.spring.view.NexacroView;

public class NexacroHandlerMethodReturnValueHandlerTest {

	@Test
	public void testNexacroResult() throws Exception {
		
		UrlBasedViewResolver viewResolver = new UrlBasedViewResolver();
		viewResolver.setViewClass(JstlView.class);

		NexacroView view = new NexacroView();
		
		NexacroHandlerMethodReturnValueHandler returnValueHandler = new NexacroHandlerMethodReturnValueHandler();
		returnValueHandler.setView(view);
		
		standaloneSetup(new PersonController()).setViewResolvers(viewResolver).setCustomReturnValueHandlers(returnValueHandler).build()
			.perform(get("/resolveView").content(""))
				.andExpect(status().isOk())
				.andExpect(model().size(1)) // platformData
				.andExpect(model().attributeExists(NexacroConstants.ATTRIBUTE.NEXACRO_PLATFORM_DATA))
				.andDo(new ResultHandler() {
					@Override
					public void handle(MvcResult result) throws Exception {
						if(!(result.getModelAndView().getView() instanceof NexacroView)) {
							Assert.fail("Redering View expected<"+NexacroView.class+">. but was<"+ result.getModelAndView().getView()+">");
						}
					}
				});
	}
	
	@Controller
	private static class PersonController {

		@RequestMapping(value="/resolveView", method=RequestMethod.GET)
		public NexacroResult resolveView() {
			NexacroResult view = new NexacroResult();
			return view;
		}
	}
}
