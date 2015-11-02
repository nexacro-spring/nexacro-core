package com.nexacro.spring.data.support;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.InvalidPropertyException;
import org.springframework.beans.PropertyAccessException;

import com.nexacro.spring.data.DataSetRowTypeAccessor;
import com.nexacro.spring.data.DataSetSavedDataAccessor;
import com.nexacro.spring.data.convert.NexacroConvertException;
import com.nexacro.spring.util.ReflectionUtil;
import com.nexacro.xapi.data.DataSet;
import com.nexacro.xapi.data.datatype.DataType;
import com.nexacro.xapi.data.datatype.PlatformDataType;

/**
 * <p><code>DataSet</code> 데이터를 POJO 혹은 Map 형태의 데이터로 변환히기 위한 추상 클래스이다. 
 * @author Park SeongMin
 * @since 08.11.2015
 * @version 1.0
 * @see
 */
public class AbstractDataSetConverter extends AbstractListenerHandler {

    
    /***************************************************************************************************/
    /**************************************  Object -> DataSet  ****************************************/
    /***************************************************************************************************/
    
    /**
     * Map에 존재하는 데이터를 <code>DataSet</code>의 행으로 추가한다.
     *
     * @param ds
     * @param map
     * @throws NexacroConvertException
     */
    protected void addRowIntoDataSet(DataSet ds, Map map) throws NexacroConvertException {
        // ignore null data.
        if(map == null) {
            return;
        }

        int newRow = ds.newRow();
        Iterator iterator = map.keySet().iterator();
        while(iterator.hasNext()) {
            Object key = iterator.next();
            Object value = map.get(key);
            
            if(!(key instanceof String)) {
                throw new NexacroConvertException("must be Map<String, Object> if you use List<Map>. target="+ds.getName());
            }
            String columnName = (String) key;
            if(ignoreSpecfiedColumnName(columnName)) {
            	continue;
            }
            
            // Byte[] 변환
            Object object = NexacroConverterHelper.toObject(value);
            
            int columnIndex = ds.indexOfColumn(columnName);
            // fire event
            object = fireDataSetConvertedValue(ds, object, newRow, columnIndex, false, false);
            
            // add data
            ds.set(newRow, columnName, object);
        }
        
    }
    
    /**
     * bean에 존재하는 데이터를 <code>DataSet</code>의 행으로 추가한다.
     * 
     * @param ds
     * @param obj
     */
    protected void addRowIntoDataSet(DataSet ds, Object obj) {
        
        if(obj == null) { // ignore null data
            return;
        }
        
        int newRow = ds.newRow();
        
        NexacroBeanWrapper beanWrapper = NexacroBeanWrapper.createBeanWrapper(obj);
        
        NexacroBeanProperty[] beanProperties = beanWrapper.getProperties();
        for(NexacroBeanProperty property: beanProperties) {
            
            // ignore static. constColumn..
            if(property.isStatic()) {
                continue;
            }
            
            String propertyName = property.getPropertyName();
            
            Object propertyValue = beanWrapper.getPropertyValue(property);
            // Byte[] 변환
            Object object = NexacroConverterHelper.toObject(propertyValue);
            
            int columnIndex = ds.indexOfColumn(propertyName);
            // fire event
            object = fireDataSetConvertedValue(ds, object, newRow, columnIndex, false, false);
            
            // add data
            ds.set(newRow, columnIndex, object);
            
        }
        
    }
    
    /**
     * Map의 식별자(key)들을 <code>DataSet</code>의 컬럼으로 추가한다.
     * <p>단, 식별자에 해당하는 값이 null인 경우 {@link PlatformDataType#UNDEFINED} 타입으로 지정된다.
     * @param ds
     * @param map
     * @throws NexacroConvertException
     */
    protected void addColumnIntoDataSet(DataSet ds, Map map) throws NexacroConvertException {
        Iterator iterator = map.keySet().iterator();
        while(iterator.hasNext()) {
            Object key = iterator.next();
            Object value = map.get(key);
            
            if(!(key instanceof String)) {
                throw new NexacroConvertException("must be Map<String, Object> if you use List<Map>. target="+ds.getName());
            }
            String columnName = (String) key;
            if(ignoreSpecfiedColumnName(columnName)) {
            	continue;
            }
            
            if(value == null) {
                ds.addColumn(columnName, PlatformDataType.UNDEFINED);
                continue;
            }
            
            // add column
            if(!NexacroConverterHelper.isConvertibleType(value.getClass())) {
                continue;
            }
            DataType dataTypeOfValue = NexacroConverterHelper.getDataTypeOfValue(value);
            ds.addColumn(columnName, dataTypeOfValue);
            
        }
    }
    
    /**
     * 입력받은 bean의 멤버필드 정보를 토대로 데이터셋의  <code>DataSet</code>의 컬럼으로 추가한다.
     * @param ds
     * @param availableFirstData
     */
    protected void addColumnIntoDataSet(DataSet ds, Object availableFirstData) {
     
        NexacroBeanWrapper beanWrapper = NexacroBeanWrapper.createBeanWrapper(availableFirstData);
        
        NexacroBeanProperty[] beanProperties = beanWrapper.getProperties();
        for(NexacroBeanProperty property: beanProperties) {
            
            String propertyName = property.getPropertyName();
            Class<?> propertyType = property.getPropertyType();
            
            if(!NexacroConverterHelper.isConvertibleType(propertyType)) {
                continue;
            }
            DataType dataTypeOfValue = NexacroConverterHelper.getDataType(propertyType);
            
            if(property.isStatic()) {
                Object staticValue = beanWrapper.getPropertyValue(property);
                // Byte[] 변환
                staticValue = NexacroConverterHelper.toObject(staticValue);
                int columnIndex = ds.indexOfColumn(propertyName);
                // fire event
                staticValue = fireDataSetConvertedValue(ds, staticValue, -1, columnIndex, false, false);
                ds.addConstantColumn(propertyName, dataTypeOfValue, staticValue);
            } else {
                ds.addColumn(propertyName, dataTypeOfValue);
            }
        }
        
    }
    
    /**
     * <code>DataSet</code>의 컬럼이름들을 반환한다.
     * @param ds
     * @return
     */
    protected String[] getDataSetColumnNames(DataSet ds) {
        int columnCount = ds.getColumnCount();
        String[] columnNames = new String[columnCount];
        for(int i=0; i<columnNames.length; i++) {
            columnNames[i] = ds.getColumn(i).getName();
        }
        return columnNames;
    }
    
    
    /***************************************************************************************************/
    /**************************************  DataSet -> Object  ****************************************/
    /***************************************************************************************************/
    /**
     * 입력받은 <code>DataSet</code>의 행의 위치(rowIndex)에 해당하는 값을 Map에 저장한다.
     * @param dataMap
     * @param ds
     * @param rowIndex
     * @param columnNames
     */
    protected void addRowIntoMap(Map<String, Object> dataMap, DataSet ds, int rowIndex, String[] columnNames) {
        
        int rowType = ds.getRowType(rowIndex);
        for(int columnIndex=0; columnIndex<columnNames.length; columnIndex++) {
            Object object = ds.getObject(rowIndex, columnIndex);
            
            // fire event
            object = fireDataSetConvertedValue(ds, object, rowIndex, columnIndex, false, false);
            
            dataMap.put(columnNames[columnIndex], object);
        }
        
        // saved data
        if(ds.hasSavedRow(rowIndex)) {
            Map<String, Object> savedDataRow = new HashMap<String, Object>();
            for(int columnIndex=0; columnIndex<columnNames.length; columnIndex++) {
                Object object = ds.getSavedData(rowIndex, columnIndex);
                
                // fire event
                object = fireDataSetConvertedValue(ds, object, rowIndex, columnIndex, true, false);
                
                savedDataRow.put(columnNames[columnIndex], object);
            }
            
            // set saved data
            dataMap.put(DataSetSavedDataAccessor.NAME, savedDataRow);
        }
        
        // row type
        dataMap.put(DataSetRowTypeAccessor.NAME, rowType);
        
    }
    
    /**
     * 입력받은 <code>DataSet</code>의 행의 위치(rowIndex)에 해당하는 값을 bean에 저장한다.
     * <p>원본데이터와 행의 타입도 저장된다.
     * @param beanWrapper
     * @param ds
     * @param rowIndex
     * @throws NexacroConvertException
     * @see DataSetRowTypeAccessor
     * @see DataSetSavedDataAccessor
     */
    protected void addRowAndOrgRowIntoBean(NexacroBeanWrapper beanWrapper, DataSet ds, int rowIndex) throws NexacroConvertException {
        
        boolean isSavedData = false;
        boolean isRemovedData = false;
        addRowIntoBean(beanWrapper, ds, rowIndex, isSavedData, isRemovedData);
        
        // set saved data
        if(ds.hasSavedRow(rowIndex)) {
            Object bean = beanWrapper.getInsatance();
            Class<?> beanType = bean.getClass();
            if(ReflectionUtil.isImplemented(beanType, DataSetSavedDataAccessor.class)) {
                isSavedData = true;
                NexacroBeanWrapper savedBeanWrapper = NexacroBeanWrapper.createBeanWrapper(beanType);
                addRowIntoBean(savedBeanWrapper, ds, rowIndex, isSavedData, isRemovedData);
                
                DataSetSavedDataAccessor accessor = (DataSetSavedDataAccessor) bean;
                accessor.setData(savedBeanWrapper.getInsatance());
            }
        }
        
    }
    
    /**
     * 입력받은 <code>DataSet</code>의 행의 위치(rowIndex)에 해당하는 값을 bean에 저장한다.
     * <p>행의 타입(rowType)이 저장된다.
     * @param beanWrapper
     * @param ds
     * @param rowIndex
     * @param isSavedData
     * @param isRemovedData
     * @throws NexacroConvertException
     */
    protected void addRowIntoBean(NexacroBeanWrapper beanWrapper,
            DataSet ds, int rowIndex, boolean isSavedData, boolean isRemovedData) throws NexacroConvertException {
        
        NexacroBeanProperty[] beanProperties = beanWrapper.getProperties();
        for(NexacroBeanProperty property: beanProperties) {
            
            String propertyName = property.getPropertyName();
            int columnIndex = ds.indexOfColumn(propertyName);
            if(columnIndex == -1) {
                continue;
            }
            
            Class<?> propertyType = property.getPropertyType();
            
            Object convertDataSetValue = 
                NexacroConverterHelper.toObjectFromDataSetValue(ds, rowIndex, columnIndex, propertyType, isSavedData, isRemovedData);
            
            // fire event
            convertDataSetValue = fireDataSetConvertedValue(ds, convertDataSetValue, rowIndex, columnIndex, isSavedData, isRemovedData);
            
            try {
                beanWrapper.setPropertyValue(property, convertDataSetValue);
            } catch (InvalidPropertyException e) {
                throw new NexacroConvertException(e.getMessage(), e);
            } catch (PropertyAccessException e) {
                throw new NexacroConvertException(e.getMessage(), e);
            } catch (BeansException e) {
                throw new NexacroConvertException(e.getMessage(), e);
            }
        }
        
        Object bean = beanWrapper.getInsatance();
        Class beanType = bean.getClass();
        
        // row type
        if(ReflectionUtil.isImplemented(beanType, DataSetRowTypeAccessor.class)) {
            
            int rowType;
            if(isRemovedData) {
                rowType = DataSet.ROW_TYPE_DELETED;
            } else if(isSavedData){
                rowType = DataSet.ROW_TYPE_NORMAL;
            } else {
                rowType = ds.getRowType(rowIndex);
            }
            
            DataSetRowTypeAccessor accessor = (DataSetRowTypeAccessor) bean;
            accessor.setRowType(rowType);
        }
        
    }
    
    protected boolean ignoreSpecfiedColumnName(String columnName) {
    	
    	if(DataSetRowTypeAccessor.NAME.equals(columnName) || DataSetSavedDataAccessor.NAME.equals(columnName)) {
        	// DataSetRowType, DataSetSavedData는 무시한다.
        	return true;
        }
    	
    	return false;
    }
    
}
