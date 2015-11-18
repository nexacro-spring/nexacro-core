package com.nexacro.spring.view;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StopWatch;
import org.springframework.web.servlet.view.AbstractView;

import com.nexacro.spring.NexacroConstants;
import com.nexacro.spring.context.NexacroContext;
import com.nexacro.spring.context.NexacroContextHolder;
import com.nexacro.spring.data.NexacroFirstRowAccessor;
import com.nexacro.spring.data.NexacroFirstRowHandler;
import com.nexacro.spring.util.NexacroUtil;
import com.nexacro.xapi.data.DataSet;
import com.nexacro.xapi.data.DataSetList;
import com.nexacro.xapi.data.Debugger;
import com.nexacro.xapi.data.PlatformData;
import com.nexacro.xapi.data.Variable;
import com.nexacro.xapi.data.VariableList;
import com.nexacro.xapi.tx.HttpPlatformResponse;
import com.nexacro.xapi.tx.PlatformException;
import com.nexacro.xapi.tx.PlatformType;

/**
 * <p>nexacro platform으로 데이터를 송신하기 위한 {@link org.springframework.web.servlet.View}이다.
 * 
 * <p>데이터 분한 전송이 이루어진 후 데이터를 전송하는 경우 기 전송된 데이터는 전송되지 않는다.
 * 또한 DataSet이 전송되었을 경우 Variable은 추가적으로 전송되지 않는다.
 * 
 * @author Park SeongMin
 * @since 07.27.2015
 * @version 1.0
 *
 */
public class NexacroView extends AbstractView {
    
	private Logger logger = LoggerFactory.getLogger(NexacroView.class);
	private Logger performanceLogger = LoggerFactory.getLogger(NexacroConstants.PERFORMANCE_LOGGER);
	
	private String defaultContentType;
	private String defaultCharset;
	
	public NexacroView(){
	}
	
    public String getDefaultContentType() {
        if(defaultContentType == null) {
            return PlatformType.CONTENT_TYPE_XML;
        } else {
            return defaultContentType;
        }
    }

    /**
     * <p>데이터 전송시 사용되는 기본 ContentType을 설정한다.
     * 
     * @param defaultContentType
     * @see PlatformType#CONTENT_TYPE_XML
     * @see PlatformType#CONTENT_TYPE_SSV
     * @see PlatformType#CONTENT_TYPE_BIN
     */
    public void setDefaultContentType(String defaultContentType) {
        this.defaultContentType = defaultContentType;
    }

    public String getDefaultCharset() {
        if(defaultContentType == null) {
            return PlatformType.DEFAULT_CHAR_SET;
        } else {
            return defaultCharset;
        }
    }

    /**
     * <p>데이터 전송 시 사용되는 기본 charset을 설정한다.
     * @param defaultCharset
     */
    public void setDefaultCharset(String defaultCharset) {
        this.defaultCharset = defaultCharset;
    }

    @Override
	protected void renderMergedOutputModel(Map<String, Object> model,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
    	try {
		    Object object = model.get(NexacroConstants.ATTRIBUTE.NEXACRO_PLATFORM_DATA);
		    if(object == null || !(object instanceof PlatformData)) {
		        sendResponse(request, response);
		        return;
		    }
			sendResponse(request, response, (PlatformData) object);
    	} catch(Throwable e) {
         	// ExceptionResolver가 처리되지 않기 때문에 로그를 남기도록 한다.
         	logger.error("an error has occurred during platform data transfer", e);
         	if(e instanceof Exception) {
         		throw (Exception) e;
         	} else {
         		throw new PlatformException("an error has occurred during platform data transfer", e);
         	}
    	}
	}
		
    protected void sendResponse(HttpServletRequest request, HttpServletResponse response) throws PlatformException{
        sendResponse(request, response, generatePlatformData());
	}
    
    protected void sendResponse(HttpServletRequest request, HttpServletResponse response, PlatformData platformData) throws PlatformException{
        
        NexacroContext cachedData = getCachedData(request, response);
        
        HttpPlatformResponse platformResponse = null;
        
        StopWatch sw = new StopWatch(getClass().getSimpleName());
        sw.start("rendering platformdata");
        
        try {
            if(cachedData != null) {
                if(cachedData.isFirstRowFired()) {
                    NexacroFirstRowHandler firstRowHandler = cachedData.getFirstRowHandler();
                    sendFirstRowData(platformData, firstRowHandler, true);
                    
                    return;
                } else {
                    platformResponse = cachedData.getPlatformResponse();
                    platformResponse.setData(platformData);
                    platformResponse.sendData();
                }
            } else {
                // TODO default 혹은 request parsing..
                platformResponse = new HttpPlatformResponse(response);
                platformResponse.setContentType(getDefaultContentType());
                platformResponse.setCharset(getDefaultCharset());
                platformResponse.setData(platformData);
                platformResponse.sendData();
            }
        } finally {
            sw.stop();
            if(performanceLogger.isTraceEnabled()) {
                performanceLogger.trace(sw.prettyPrint());
            }
        }
        
        if(logger.isDebugEnabled()) {
            logger.debug("response platformdata=[{}]", new Debugger().detail(platformData));
        }
        
    }
    
    private void sendFirstRowData(PlatformData platformData, NexacroFirstRowHandler firstRowHandler, boolean isCallEndMethod)
            throws PlatformException {
        
    	setFirstRowStatusDataSet(platformData);
    	
        removeTransferData(firstRowHandler, platformData);
        
        if(logger.isDebugEnabled()) {
            logger.debug("response platformdata=[{}]", new Debugger().detail(platformData));
        }
        firstRowHandler.sendPlatformData(platformData);
        if(isCallEndMethod) {
            NexacroFirstRowAccessor.end(firstRowHandler);
        }
    }
    
    private void setFirstRowStatusDataSet(PlatformData platformData){
    	
    	Variable errorCodeVariable = platformData.getVariable(NexacroConstants.ERROR.ERROR_CODE);
    	if(errorCodeVariable != null && errorCodeVariable.getInt() < 0) {
    		// error status
    		Variable errorMsgVariable = platformData.getVariable(NexacroConstants.ERROR.ERROR_MSG);
    		platformData.addDataSet(NexacroUtil.createFirstRowStatusDataSet(errorCodeVariable.getInt(), errorMsgVariable != null? errorMsgVariable.getString(): null));
    	} else {
    		// success status
    		platformData.addDataSet(NexacroUtil.createFirstRowStatusDataSet(NexacroConstants.ERROR.DEFAULT_ERROR_CODE, null));
    	}
    	
    }
    

    /**
     * Statements
     *
     * @param firstRowHandler
     * @param variableList
     */
    private void removeTransferData(NexacroFirstRowHandler firstRowHandler, PlatformData platformData) {
        
        VariableList variableList = platformData.getVariableList();
        
        if(NexacroFirstRowAccessor.getSendOutDataSetCount(firstRowHandler) > 0) {
            // dataset already sended.. 
            int size = variableList.size();
            for(int i=0; i<size; i++) {
                if (logger.isInfoEnabled()) {
                    logger.info("DataSet aleady sended. ignore variable="+variableList.get(i).getName());
                }
            }
            platformData.setVariableList(new VariableList());
            
        } else {
            removeTransferVariables(firstRowHandler, variableList);
        }
        
        DataSetList dataSetList = platformData.getDataSetList();
        removeTransferDataSets(firstRowHandler, dataSetList);
        
    }
    
    private void removeTransferVariables(NexacroFirstRowHandler firstRowHandler, VariableList variableList) {
        
        String[] sendOutVariableNames = NexacroFirstRowAccessor.getSendOutVariableNames(firstRowHandler);
        Variable var = null;
        int variableListSize = variableList.size();
        for(int variableListIndex = variableListSize-1 ; variableListIndex>=0 ; variableListIndex--) {
            var = variableList.get(variableListIndex);
            if(var == null) {
                continue;
            }
            boolean isSended = false;
            for(int sendedVariableIndex = 0; sendedVariableIndex<sendOutVariableNames.length; sendedVariableIndex++) {
                if(var.getName().equals(sendOutVariableNames[sendedVariableIndex])) {
                    isSended = true;
                    break;
                }
            }
            if(isSended) {
                variableList.remove(variableListIndex);
            }
        }
    }
    
    private void removeTransferDataSets(NexacroFirstRowHandler firstRowHandler, DataSetList dataSetList) {
        
        String[] sendOutDataSetNames = NexacroFirstRowAccessor.getSendOutDataSetNames(firstRowHandler);
        DataSet dataSet = null;
        int dataSetListSize = dataSetList.size();
        for(int datasetListIndex = dataSetListSize - 1 ; datasetListIndex >= 0; datasetListIndex--) {
            dataSet = dataSetList.get(datasetListIndex);
            if(dataSet == null) {
                continue;
            }
            boolean isSended = false;
            for(int sendedDataSetIndex = 0; sendedDataSetIndex<sendOutDataSetNames.length; sendedDataSetIndex++) {
                if(dataSet.getName().equals(sendOutDataSetNames[sendedDataSetIndex])) {
                    isSended = true;
                    break;
                }
            }
            if(isSended) {
                dataSetList.remove(datasetListIndex);
            }
        
        }
    }
    
    private PlatformData generatePlatformData() {
        PlatformData platformData = new PlatformData();
        platformData.addVariable(Variable.createVariable(NexacroConstants.ERROR.ERROR_CODE, NexacroConstants.ERROR.DEFAULT_ERROR_CODE));
        return platformData;
    }
    
    private NexacroContext getCachedData(HttpServletRequest request, HttpServletResponse response) {
        
        // get already parsed request
        NexacroContext nexacroContext = NexacroContextHolder.getNexacroContext();
        if(nexacroContext != null) {
            return nexacroContext;
        }
        
        return nexacroContext;
    }
	
}
