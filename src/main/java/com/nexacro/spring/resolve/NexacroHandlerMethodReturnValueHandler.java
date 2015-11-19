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
 * 요청에 대한 응답 데이터를 nexacro platform 데이터 형식으로 데이터 변환을 수행한다.
 * 
 * <p>정의된 형식은 다음과 같다.
 * <blockquote>
 *    <table border="thin">
 *        <tr class="TableSubHeadingColor">
 *            <th>class</th>
 *            <th>description</th>
 *        </tr>
 *        <tr class="TableRowColor">
 *            <td>NexacroResult</td>
 *            <td>DataSet 혹은 Varible로 데이터를 송신하기 위한 정보를 가진다.</td>
 *        </tr>
 *        <tr class="TableRowColor">
 *            <td>NexacroFileResult</td>
 *            <td>파일 데이터를 송신하기 위한 정보를 가진다.</td>
 *        </tr>
 *        <tr class="TableRowColor">
 *            <td>PlatformData</td>
 *            <td>nexacro platform의 데이터 통신의 기본 단위이다.</td>
 *        </tr>
 *    </table>
 * </blockquote>
 *
 * @author Park SeongMin
 * @since 07.27.2015
 * @version 1.0
 * @see NexacroResult
 * @see NexacroFileResule
 * @see PlatformData
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
        
        Map<String, Object> dataSets = nexacroResult.getDataSets();
        Set<String> dataSetKeySet = dataSets.keySet();
        for(String name: dataSetKeySet) {
            Object object = dataSets.get(name);
            if(object == null) {
            	platformData.addDataSet(new DataSet(name));
            } else {
            
	            NexacroConverter dataSetConverter = getDataSetConverter(object.getClass());
	            if(dataSetConverter == null) {
	                logger.debug("not found converter {} to List to DataSet({})" , name);
	                continue;
	            }
	            
	            logger.debug("found a converter({}) for converting the List to DataSet({})"
	                    , dataSetConverter.getClass().getName()
	                    , name);
	            
	            
	            ConvertDefinition definition = new ConvertDefinition(name);
	            Object convert = dataSetConverter.convert(object, definition);
	            
	            if(convert != null && convert instanceof DataSet) {
	                platformData.addDataSet((DataSet) convert);
	            }
            } // end if
            
        } // end for
    }
    
    private void addVariablesIntoPlatformData(PlatformData platformData, NexacroResult nexacroResult) throws NexacroConvertException {
        
        Map<String, Object> variables = nexacroResult.getVariables();
        Set<String> variableKeySets = variables.keySet();
        for(String name: variableKeySets) {
            Object object = variables.get(name);
            if(object == null) {
            	platformData.addVariable(new Variable(name));
            } else {
            
	            NexacroConverter variableConverter = getVariableConverter(object.getClass());
	            if(variableConverter == null) {
	                logger.debug("not found converter {} to Variable({})" , object.getClass(), name);
	                continue;
	            }
	            
	            logger.debug("found a converter({}) for converting the {} to Variable({})"
	                    , variableConverter.getClass().getName()
	                    , object.getClass()
	                    , name);
	            
	            ConvertDefinition definition = new ConvertDefinition(name);
	            Object convert = variableConverter.convert(object, definition);
	            
	            if(convert != null && convert instanceof Variable) {
	                platformData.addVariable((Variable) convert);
	            }
            } // end if
            
        } // end for
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
