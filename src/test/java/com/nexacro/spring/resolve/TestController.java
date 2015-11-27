package com.nexacro.spring.resolve;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.nexacro.spring.NexacroException;
import com.nexacro.spring.annotation.ParamDataSet;
import com.nexacro.spring.annotation.ParamVariable;
import com.nexacro.spring.data.NexacroFileResult;
import com.nexacro.spring.data.NexacroFirstRowHandler;
import com.nexacro.spring.data.NexacroResult;
import com.nexacro.spring.data.support.bean.DefaultBean;
import com.nexacro.xapi.data.DataSet;
import com.nexacro.xapi.data.DataSetList;
import com.nexacro.xapi.data.PlatformData;
import com.nexacro.xapi.data.VariableList;
import com.nexacro.xapi.data.datatype.PlatformDataType;
import com.nexacro.xapi.tx.HttpPlatformRequest;
import com.nexacro.xapi.tx.HttpPlatformResponse;
import com.nexacro.xapi.tx.PlatformException;

@Controller
public class TestController {

	@RequestMapping(value="/default", method={RequestMethod.GET})
	public NexacroResult methodDefaultProcessing() throws NexacroException {
		return new NexacroResult();
	}
	
	@RequestMapping(value="/DataSetToBean", method={RequestMethod.GET})
	public NexacroResult methodDataSetToBean(@ParamDataSet(name="ds") List<DefaultBean> dsList
											, @ParamDataSet(name="ds") DataSet ds) throws NexacroException {
		if(dsList == null || ds == null) {
			throw new NexacroException("DataSet 'ds' have not been resolved");
		}
		
		if(dsList.size() != 2 || ds.getRowCount() != 2) {
			throw new NexacroException("DataSet 'ds' data have not been resolved. expectedRowCount="+2+", actual="+ds.getRowCount());
		}
		
		NexacroResult result = new NexacroResult();
		result.addDataSet("dsResult", dsList);
		
		return result;
	}
	
	@RequestMapping(value="/DataSetToMap", method={RequestMethod.GET})
	public NexacroResult methodDataSetToMap(@ParamDataSet(name="ds") List<Map<String, Object>> dsList
											, @ParamDataSet(name="ds") DataSet ds) throws NexacroException {
		
		if(dsList == null || ds == null) {
			throw new NexacroException("DataSet 'ds' have not been resolved");
		}
		
		if(dsList.size() != 2 || ds.getRowCount() != 2) {
			throw new NexacroException("DataSet 'ds' data have not been resolved. expectedRowCount="+2+", actual="+ds.getRowCount());
		}
		
		NexacroResult result = new NexacroResult();
		result.addDataSet("dsResult", dsList);
		
		return result;
	}
	
	@RequestMapping(value="/DataSetWithASingleRowToMap", method={RequestMethod.GET})
	public NexacroResult methodDataSetWithOneRowToMap(@ParamDataSet(name="ds") Map<String, Object> map) throws NexacroException {
		
		if(map == null) {
			throw new NexacroException("DataSet 'ds' have not been resolved");
		}
		
		NexacroResult result = new NexacroResult();
		result.addDataSet("dsResult", map);
		
		return result;
	}
	
	@RequestMapping(value="/DataSetWithASingleRowToObject", method={RequestMethod.GET})
	public NexacroResult methodDataSetWithOneRowToObject(@ParamDataSet(name="ds") DefaultBean defaultBean) throws NexacroException {
		
		if(defaultBean == null) {
			throw new NexacroException("DataSet 'ds' have not been resolved");
		}
		
		NexacroResult result = new NexacroResult();
		result.addDataSet("dsResult", defaultBean);
		
		return result;
	}
	
	@RequestMapping(value="/Variable", method={RequestMethod.GET})
	public NexacroResult methodVariable(@ParamVariable(name="varInt") int varInt, @ParamVariable(name="varString") String varString) throws NexacroException {
		if(varInt != 1) {
			throw new NexacroException("Variable 'varInt' must be '1'. input variable have not been resolved.");
		}
		
		if(!"park".equals(varString)) {
			throw new NexacroException("Variable 'varString' must be 'park'. input variable have not been resolved.");
		}
		
		NexacroResult result = new NexacroResult();
		result.addVariable("varResultInt", varInt);
		result.addVariable("varResultString", varString);
		
		return result;
	}
	
	@RequestMapping(value="/SupportedParameter")
	public NexacroResult methodSupportedParam(
							DataSetList dsList
					          , VariableList varList
					          , PlatformData platformData
					          , HttpPlatformRequest platformRequest
					          , HttpPlatformResponse platformResponse
					          , NexacroFirstRowHandler firstRowHandler
							) throws NexacroException {
		
		if(dsList == null) {
			throw new NexacroException("VariableList have not bean resolved.");
		}
		if(varList == null) {
			throw new NexacroException("DataSetList have not bean resolved.");
		}
		if(platformData == null) {
			throw new NexacroException("PlatformData have not bean resolved.");
		}
		if(platformRequest == null) {
			throw new NexacroException("HttpPlatformRequest have not bean resolved.");
		}
		if(platformResponse == null) {
			throw new NexacroException("HttpPlatformResponse have not bean resolved.");
		}
		if(firstRowHandler == null) {
			throw new NexacroException("NexacroFirstRowHandler have not bean resolved.");
		}

		return new NexacroResult();
		
	}
	
//	@RequestMapping(value="/UnsupportedParameter")
//	public NexacroResult methodSupportedParam(@ParamDataSet(name = "" )  Map map) throws NexacroException {
//		
//
//		return new NexacroResult();
//	}
	
	@RequestMapping(value="/NotEnterRequiredDataSet")
	public NexacroResult methodNotEnterRequiredDataSet(@ParamDataSet(name="required", required=true) List<Map> required) throws NexacroException {
		
		if(required != null) {
			throw new NexacroException("'required' data should be null with null input.");
		}
			
		return new NexacroResult();
	}
	
	@RequestMapping(value="/NotEnterRequiredVariable")
	public NexacroResult methodNotEnterRequiredVariable(@ParamVariable(name="required", required=true) int required) throws NexacroException {
		
		if(required != 0) {
			throw new NexacroException("'required' data should be zero with null input.");
		}
		
		return new NexacroResult();
	}
	
	@RequestMapping(value="/OptionalDataSet")
	public NexacroResult methodOptionalDataSet(@ParamDataSet(name="optional", required=false) List<Map> missing) throws NexacroException {
		return new NexacroResult();
	}
	
	@RequestMapping(value="/OptionalVariable")
	public NexacroResult methodOptionalVariable(@ParamDataSet(name="optional", required=false) String optional) throws NexacroException {
		return new NexacroResult();
	}
	
	@RequestMapping(value="/NexacroFirstRowStatus")
	public NexacroResult methodNexacroFirstRowStatus(NexacroFirstRowHandler firstRowHandler) throws PlatformException {
		
		DataSet ds = new DataSet("dummy");
		ds.addColumn("dummy", PlatformDataType.STRING);
		ds.newRow();
		ds.set(0, "dummy", "dummydata");
		
		firstRowHandler.sendDataSet(ds);
		
		return new NexacroResult();
		
	}
	
	@RequestMapping(value="/NexacroFileResult")
	public NexacroFileResult methodNexacroFileResult() {
		
		File file = null;
		
		NexacroFileResult fileResult = new NexacroFileResult(file);
		
		return fileResult;
	}
	
	@RequestMapping(value="/Void")
	public void methodVoid() {
		
		// nothing
	}
	
}
