package com.nexacro.spring.data.support;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nexacro.spring.data.DataSetRowTypeAccessor;
import com.nexacro.spring.data.convert.ConvertDefinition;
import com.nexacro.spring.data.convert.NexacroConvertException;
import com.nexacro.spring.data.convert.NexacroConverter;
import com.nexacro.xapi.data.DataSet;

/**
 * <p><code>DataSet</code>에서 List 형태의 데이터로 변환을 수행한다. 
 *
 * @author Park SeongMin
 * @since 07.28.2015
 * @version 1.0
 * @see
 */
public class DataSetToListConverter extends AbstractDataSetConverter implements NexacroConverter<DataSet, List> {

    @Override
    public boolean canConvert(Class source, Class target) {
        if(source == null || target == null) {
            return false;
        }
        
        // support type
        if(DataSet.class.equals(source) && List.class.equals(target)) {
            return true;
        }
        
        return false;
    }
    
    /**
     * @see com.nexacro.spring.data.NexacroConverter#convert(java.lang.Object)
     */
    @Override
    public List convert(DataSet ds, ConvertDefinition definition) throws NexacroConvertException {

        if(definition ==null) {
            throw new NexacroConvertException(ConvertDefinition.class.getSimpleName()+" must not be null.");
        }
        
        // bean 혹은 Map만을 대상으로 한다.
        Class genericType = definition.getGenericType();
        
        if(ds == null) {
//            return ReflectionUtil.instantiateClass(genericType);
            return new ArrayList();
        }
        
        if(genericType == null) {
            throw new NexacroConvertException("List<> generic type must be declared.");
        }
        
        List dataList = null;
        if(Map.class.equals(genericType)) {
            dataList = convertDataSetToListMap(ds, definition);
        } else {
            dataList = convertDataSetToListBean(ds, definition);
        }
        
        return dataList;
        
    }
    
    private List<Map<String, Object>> convertDataSetToListMap(DataSet ds, ConvertDefinition definition) {
        
        String[] columnNames = getDataSetColumnNames(ds);
        
        // default row
        List<Map<String, Object>> dataListMap = new ArrayList<Map<String, Object>>();
        int rowCount = ds.getRowCount();
        for(int rowIndex=0; rowIndex<rowCount; rowIndex++) {
            addRowIntoListMap(dataListMap, ds, rowIndex, columnNames);
        }
        
        // removed row
        int removedRowCount = ds.getRemovedRowCount();
        for(int removedIndex=0; removedIndex<removedRowCount; removedIndex++) {
            addRemovedRowIntoListMap(dataListMap, ds, removedIndex, columnNames);
        }
    
        return dataListMap;
    }
    
    /**
     * Statements
     *
     * @param ds
     * @param columnCount
     * @param columnNames
     * @param hasSavedData
     * @param dataListMap
     * @param rowIndex
     */
    private void addRowIntoListMap(List<Map<String, Object>> dataListMap, DataSet ds, int rowIndex, String[] columnNames) {
        
        // default row
        Map<String, Object> dataRow = new HashMap<String, Object>();
        addRowIntoMap(dataRow, ds, rowIndex, columnNames);
        
        // set data
        dataListMap.add(dataRow);
    }

    /**
     * Statements
     *
     * @param ds
     * @param columnCount
     * @param columnNames
     * @param dataList
     * @param removedIndex
     */
    private void addRemovedRowIntoListMap(List<Map<String, Object>> dataList, DataSet ds, int removedIndex, String[] columnNames) {
        
        // removed data
        Map<String, Object> dataRow = new HashMap<String, Object>();
        for(int columnIndex=0; columnIndex<columnNames.length; columnIndex++) {
            Object object = ds.getRemovedData(removedIndex, columnIndex);
            
            // fire event
            object = fireDataSetConvertedValue(ds, object, removedIndex, columnIndex, false, true);
            
            dataRow.put(columnNames[columnIndex], object);
        }
        
        // row type
        dataRow.put(DataSetRowTypeAccessor.NAME, DataSet.ROW_TYPE_DELETED);
        
        // set removed data
        dataList.add(dataRow);
    }
    
    
    private List<?> convertDataSetToListBean(DataSet ds, ConvertDefinition definition) throws NexacroConvertException {
        
        Class genericType = definition.getGenericType();
        
        // 지원하는 형식인지 확인 한다.
        if(!NexacroConverterHelper.isSupportedBean(genericType)) {
            throw new NexacroConvertException("unsupported source type. type="+genericType);
        }
        
        // 데이터 변환이 가능한 field 목록을 획득한다.
//        Map<String, Field> adjustConvertibleFields = NexacroConverterHelper.getAdjustConvertibleFields(genericType, ds);
        
        List dataList = new ArrayList();
        
        boolean isOrgData = false;
        boolean isRemovedData = false;
        
        // default row
        int rowCount = ds.getRowCount();
        for(int rowIndex=0; rowIndex<rowCount; rowIndex++) {
            addRowIntoListBean(dataList, genericType, ds, rowIndex);
        }
        
        // removed row
        int removedRowCount = ds.getRemovedRowCount();
        for(int removedIndex=0; removedIndex<removedRowCount; removedIndex++) {
            addRemovedRowIntoListBean(dataList, genericType, ds, removedIndex);
        }
        
        return dataList;
    }
    
    
    /**
     * Statements
     *
     * @param ds
     * @param beanType
     * @param adjustConvertibleFields
     * @param keySet
     * @param dataList
     * @param rowIndex
     * @throws NexacroConvertException
     */
    private void addRowIntoListBean(List dataList, Class beanType, DataSet ds, int rowIndex) throws NexacroConvertException {
        
//        Object bean = ReflectionUtil.instantiateClass(beanType);
        
        NexacroBeanWrapper beanWrapper = NexacroBeanWrapper.createBeanWrapper(beanType);
        addRowAndOrgRowIntoBean(beanWrapper, ds, rowIndex);
        
        Object bean = beanWrapper.getInstance();
        dataList.add(bean);
    }
    
    private void addRemovedRowIntoListBean(List dataList, Class beanType, DataSet ds, int removedIndex)
            throws NexacroConvertException {

        // Object bean = ReflectionUtil.instantiateClass(beanType);
        NexacroBeanWrapper beanWrapper = NexacroBeanWrapper.createBeanWrapper(beanType);
        Object bean = beanWrapper.getInstance();
        boolean isSavedData = false;
        boolean isRemovedData = true;
        addRowIntoBean(beanWrapper, ds, removedIndex, isSavedData, isRemovedData);

        dataList.add(bean);

    }
    
}