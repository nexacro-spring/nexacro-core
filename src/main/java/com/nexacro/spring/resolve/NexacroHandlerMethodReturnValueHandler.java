package com.nexacro.spring.resolve;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.util.StopWatch;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.View;

import com.nexacro.spring.NexacroConstants;
import com.nexacro.spring.data.NexacroFileResult;
import com.nexacro.spring.data.NexacroResult;
import com.nexacro.spring.data.convert.ConvertDefinition;
import com.nexacro.spring.data.convert.NexacroConvertException;
import com.nexacro.spring.data.convert.NexacroConverter;
import com.nexacro.spring.data.convert.NexacroConverterFactory;
import com.nexacro.spring.view.NexacroFileView;
import com.nexacro.spring.view.NexacroView;
import com.nexacro.xapi.data.DataSet;
import com.nexacro.xapi.data.PlatformData;
import com.nexacro.xapi.data.Variable;


/**
 * <pre>
 * Controller에서 반환되는 데이터를 nexacro platform 데이터 형식으로 데이터 변환을 수행한다.
 * 
 * 지원하는 형식은 다음과 같다.
 * 
 * <li>NexacroResult</li>
 * <li>NexacroFileResult</li>
 * <li>PlatformData</li>
 * </pre>
 *
 * @ClassName   : NexacroHandlerMethodReturnValueHandler.java
 * @Description : 클래스 설명을 기술합니다.
 * @author Park SeongMin
 * @since 2015. 7. 27.
 * @version 1.0
 * @see
 * @Modification Information
 * <pre>
 *     since          author              description
 *  ===========    =============    ===========================
 *  2015. 7. 27.     Park SeongMin     최초 생성
 * </pre>
 */

public class NexacroHandlerMethodReturnValueHandler implements HandlerMethodReturnValueHandler {

    private Logger logger = LoggerFactory.getLogger(NexacroHandlerMethodReturnValueHandler.class);
    private Logger performanceLogger = LoggerFactory.getLogger(NexacroConstants.PERFORMANCE_LOGGER);
    
    private View view;
    private View fileView;

    public View getView() {
        if(view == null) {
            return new NexacroView();
        } else {
            return view;
        }
    }

    public void setView(View view) {
        this.view = view;
    }
    
    public View getFileView() {
        if(fileView == null) {
            return new NexacroFileView();
        } else {
            return fileView;
        }
    }

    public void setFileView(View fileView) {
        this.fileView = fileView;
    }

    @Override
    public boolean supportsReturnType(MethodParameter returnType) {

        Class<?> parameterType = returnType.getParameterType();
        if(NexacroResult.class.isAssignableFrom(parameterType)) {
            return true;
        } else if(PlatformData.class.isAssignableFrom(parameterType)) {
            return true;
        } else if(NexacroFileResult.class.isAssignableFrom(parameterType)) {
            return true;
        }
        
        return false;
    }

    @Override
    public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest) throws Exception {

        Class<?> parameterType = returnType.getParameterType();
        
        if(returnValue == null) {
            mavContainer.setView(getView());
            return;
        }
        
        View responseView = null;
        
        StopWatch sw = new StopWatch(getClass().getSimpleName());
        sw.start("resolve return value");
        try {
            if(NexacroResult.class.isAssignableFrom(parameterType)) {
                // first use platform data
                NexacroResult nexacroResult = (NexacroResult) returnValue;
                PlatformData platformData = nexacroResult.getPlatformData();
                
                // add datasets
                addDataSetsIntoPlatformData(platformData, nexacroResult);
                
                // add variables
                addVariablesIntoPlatformData(platformData, nexacroResult);
                
                addErrorInformationIntoPlatformData(platformData, nexacroResult);
                
                // 변환 된 PlatformData 전달.
                mavContainer.addAttribute(NexacroConstants.ATTRIBUTE.NEXACRO_PLATFORM_DATA, platformData);
                responseView = getView();
                
            } else if(PlatformData.class.isAssignableFrom(parameterType)) {
                PlatformData platformData = (PlatformData) returnValue;
                mavContainer.addAttribute(NexacroConstants.ATTRIBUTE.NEXACRO_PLATFORM_DATA, platformData);
                responseView = getView();
            } else if(NexacroFileResult.class.isAssignableFrom(parameterType)) {
                NexacroFileResult fileResult = (NexacroFileResult) returnValue;
                mavContainer.addAttribute(NexacroConstants.ATTRIBUTE.NEXACRO_FILE_DATA, fileResult);
                responseView = getFileView();
            } else {
                // default..
                responseView = getView();
            }
        } catch(Exception e) {
            logger.error("Error handling return value. value"+returnValue+", e="+e +", message="+e.getMessage(), e);
            throw e;
        } finally {
            sw.stop();
            if(performanceLogger.isTraceEnabled()) {
                performanceLogger.trace(sw.prettyPrint());
            }
        }
        
        mavContainer.setView(responseView);
            
    }
    
    private void addDataSetsIntoPlatformData(PlatformData platformData, NexacroResult nexacroResult) throws NexacroConvertException {
        
        Map<String, List> dataSets = nexacroResult.getDataSets();
        Set<String> dataSetKeySet = dataSets.keySet();
        for(String name: dataSetKeySet) {
            List list = dataSets.get(name);
            NexacroConverter dataSetConverter = getDataSetConverter(list.getClass());
            
            ConvertDefinition definition = new ConvertDefinition(name);
            Object convert = dataSetConverter.convert(list, definition);
            
            if(convert != null && convert instanceof DataSet) {
                platformData.addDataSet((DataSet) convert);
            }
        }
    }
    
    private void addVariablesIntoPlatformData(PlatformData platformData, NexacroResult nexacroResult) throws NexacroConvertException {
        
        Map<String, Object> variables = nexacroResult.getVariables();
        Set<String> variableKeySets = variables.keySet();
        for(String name: variableKeySets) {
            Object object = variables.get(name);
            NexacroConverter variableConverter = getVariableConverter(object.getClass());
            
            ConvertDefinition definition = new ConvertDefinition(name);
            Object convert = variableConverter.convert(object, definition);
            
            if(convert != null && convert instanceof Variable) {
                platformData.addVariable((Variable) convert);
            }
        }
    }
    
    /**
     * Statements
     *
     * @param platformData
     * @param nexacroResult
     */
    private void addErrorInformationIntoPlatformData(PlatformData platformData, NexacroResult nexacroResult) {
        
        // result status
        if(nexacroResult.registedErrorCode()) {
            int errorCode = nexacroResult.getErrorCode();
            String errorMsg = nexacroResult.getErrorMsg();
            
            platformData.addVariable(Variable.createVariable(NexacroConstants.ERROR.ERROR_CODE, errorCode));
            platformData.addVariable(Variable.createVariable(NexacroConstants.ERROR.ERROR_MSG, errorMsg));
        } else {
            if(platformData.getVariable(NexacroConstants.ERROR.ERROR_CODE) == null) {
                // binary 통신 시 반드시 필요.
                platformData.addVariable(Variable.createVariable(NexacroConstants.ERROR.ERROR_CODE, NexacroConstants.ERROR.DEFAULT_ERROR_CODE));
            }
        }
        
    }
    
    private NexacroConverter getDataSetConverter(Class source) {
        return getConverter(source, DataSet.class);
    }
    
    private NexacroConverter getVariableConverter(Class source) {
        return getConverter(source, Variable.class);
    }
    
    private NexacroConverter getConverter(Class source, Class target) {
        return NexacroConverterFactory.getConverter(source, target);
    }

}
