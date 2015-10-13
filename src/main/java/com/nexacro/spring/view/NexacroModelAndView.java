package com.nexacro.spring.view;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import com.nexacro.spring.NexacroConstants;
import com.nexacro.xapi.data.DataSet;
import com.nexacro.xapi.data.PlatformData;
import com.nexacro.xapi.data.Variable;

public class NexacroModelAndView extends ModelAndView {
	
	private PlatformData platformData = new PlatformData();
	
	public NexacroModelAndView(){
	    setNexacroObject();
	}
	
	public NexacroModelAndView(String viewName){
		super(viewName);
		setNexacroObject();
	}

	public NexacroModelAndView(View view){
        super(view);
        setNexacroObject();
    }
	
    public PlatformData getPlatformData() {
        return platformData;
    }

    public void setPlatformData(PlatformData platformData) {
        this.platformData = platformData;
        setNexacroObject();
    }
    
    public void addVariable(Variable var) {
        this.platformData.addVariable(var);
    }
    
    public void addDataSet(DataSet ds) {
        this.platformData.addDataSet(ds);
    }

	public void setErrorCode(int code){
	    platformData.addVariable(Variable.createVariable(NexacroConstants.ERROR.ERROR_CODE, code));
	    setNexacroObject();
	}
	
	public void setErrorMsg(String msg){
	    platformData.addVariable(Variable.createVariable(NexacroConstants.ERROR.ERROR_MSG, msg));
	    setNexacroObject();
	}
	
	private void setNexacroObject() {
	    super.addObject(NexacroConstants.ATTRIBUTE.NEXACRO_PLATFORM_DATA, platformData);
	}
	
	 
}
