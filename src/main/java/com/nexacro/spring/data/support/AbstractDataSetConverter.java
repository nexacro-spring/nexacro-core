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
 * <pre>
 * Statements
 * </pre>
 *
 * @ClassName   : AbstractDataSetConverterHandler.java
 * @Description : 클래스 설명을 기술합니다.
 * @author Park SeongMin
 * @since 2015. 8. 11.
 * @version 1.0
 * @see
 * @Modification Information
 * <pre>
 *     since          author              description
 *  ===========    =============    ===========================
 *  2015. 8. 11.     Park SeongMin     최초 생성
 * </pre>
 */

public class AbstractDataSetConverter extends AbstractListenerHandler {

    
    /***************************************************************************************************/
    /**************************************  Object -> DataSet  ****************************************/
    /***************************************************************************************************/
    
    /**
     * Statements
     *
     * @param ds
     * @param obj
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
            
            // Byte[] 변환
            Object object = NexacroConverterHelper.toObject(value);
            
            int columnIndex = ds.indexOfColumn(columnName);
            // fire event
            object = fireDataSetConvertedValue(ds, object, newRow, columnIndex, false, false);
            
            // add data
            ds.set(newRow, columnName, object);
        }
        
    }
    
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
    
    protected void addColumnIntoDataSet(DataSet ds, Map map) throws NexacroConvertException {
        Iterator iterator = map.keySet().iterator();
        while(iterator.hasNext()) {
            Object key = iterator.next();
            Object value = map.get(key);
            
            if(!(key instanceof String)) {
                throw new NexacroConvertException("must be Map<String, Object> if you use List<Map>. target="+ds.getName());
            }
            String columnName = (String) key;
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
    
}
