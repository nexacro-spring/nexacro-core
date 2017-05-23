package com.nexacro.spring.data.support;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;

import com.nexacro.spring.data.DataSetRowTypeAccessor;
import com.nexacro.spring.data.DataSetSavedDataAccessor;
import com.nexacro.spring.data.support.bean.DefaultBean;
import com.nexacro.xapi.data.ColumnHeader;
import com.nexacro.xapi.data.DataSet;
import com.nexacro.xapi.data.datatype.DataType;
import com.nexacro.xapi.data.datatype.PlatformDataType;

/**
 * 
 * @author Park SeongMin
 * @since 08.10.2015
 * @version 1.0
 * @see
 */
public abstract class NexacroTestUtil {

    private static String[] dsPropertyNames;
    private static List<Object[]> dsValuesList;
    private static DataType[] dsDataTypes;

    // private static List<Map<String, Object>> dataMapList;
    // private static Map<String, DataType> dataTypeMap;

    private static Map<Class, Object> supportedClassesValue;
    
    static {
        initSupportedClassesValue();
        initDsPropertyNames();
        initDsValueList();
        initDsDataTypes();
    }
    
    static Map<Class, Object> getSupportedClassesValue() {
        return Collections.unmodifiableMap(supportedClassesValue);
    }
    
    private static void initSupportedClassesValue() {
        
        supportedClassesValue = new HashMap<Class, Object>();
        
        Calendar instance = null;
        Date hireDate = null;
        instance = Calendar.getInstance();
        instance.set(2009, Calendar.JANUARY, 1);
        hireDate = new Date(instance.getTimeInMillis());
        
        supportedClassesValue.put(int.class, 11);
        supportedClassesValue.put(Integer.class, new Integer(11));
        supportedClassesValue.put(long.class, 11l);
        supportedClassesValue.put(Long.class, new Long(11l));
        supportedClassesValue.put(float.class, 180.1f);
        supportedClassesValue.put(Float.class, new Float(180.1f));
        supportedClassesValue.put(double.class, 11.1d);
        supportedClassesValue.put(Double.class, new Double(11.1d));
        supportedClassesValue.put(boolean.class, true);
        supportedClassesValue.put(Boolean.class, Boolean.TRUE);
        supportedClassesValue.put(byte[].class, new byte[] { 1, 1 });
        supportedClassesValue.put(Byte[].class, new Byte[] { 1, 1 });
        supportedClassesValue.put(String.class, "seongmin");
        supportedClassesValue.put(Date.class, hireDate);
        supportedClassesValue.put(BigDecimal.class, new BigDecimal("10001"));
        supportedClassesValue.put(Object.class, new Object());
        
    }

    private static void initDsPropertyNames() {
        
        dsPropertyNames = new String[] { "employeeId", "access", "height", "commissionPercent", "male", "image",
                "firstName", "lastName", "email", "hireDate", "salary", "obj" };

    }
    
    private static void initDsValueList() {
        
        dsValuesList = new ArrayList<Object[]>();

        Calendar instance = null;
        Date hireDate = null;
        instance = Calendar.getInstance();
        instance.set(2009, Calendar.JANUARY, 1);
        hireDate = new Date(instance.getTimeInMillis());

        Object[] firstValues = new Object[] { 11, 11l, 180.1f, 11.1d, true, new byte[] { 1, 1 }, "seongmin", "park",
                "seongmin@tobesoft.com", hireDate, new BigDecimal("10001"), new Object() };
        dsValuesList.add(firstValues);

        instance = Calendar.getInstance();
        instance.set(2009, Calendar.JANUARY, 2);
        hireDate = new Date(instance.getTimeInMillis());
        Object[] secondValues = new Object[] { 12, 12l, 180.2f, 11.2d, false, new byte[] { 1, 2 }, "hyena", "lee",
                "hyena@tobesoft.com", hireDate, new BigDecimal("10002"), new Object() };
        dsValuesList.add(secondValues);
        
    }
    
    private static void initDsDataTypes() {
        dsDataTypes = new DataType[] { PlatformDataType.INT, PlatformDataType.LONG, PlatformDataType.FLOAT,
                PlatformDataType.DOUBLE, PlatformDataType.BOOLEAN, PlatformDataType.BLOB, PlatformDataType.STRING,
                PlatformDataType.STRING, PlatformDataType.STRING, PlatformDataType.DATE_TIME,
                PlatformDataType.BIG_DECIMAL, PlatformDataType.UNDEFINED };
    }
    
    public static List<DefaultBean> createDefaultBeans() {

        List<DefaultBean> beanList = new ArrayList<DefaultBean>();

        for (Object[] value : dsValuesList) {

            NexacroBeanWrapper wrapper = NexacroBeanWrapper.createBeanWrapper(DefaultBean.class);
            for (int i=0; i<dsPropertyNames.length; i++) {
                wrapper.setPropertyValue(dsPropertyNames[i], value[i]);
            }
            Object bean = wrapper.getInstance();
            beanList.add((DefaultBean) bean);
        }

        return beanList;

    }

    public static DataSet createDefaultDataSet() {

        DataSet ds = new DataSet("employee");
        for (int i = 0; i < dsPropertyNames.length; i++) {
            ds.addColumn(dsPropertyNames[i], dsDataTypes[i]);
        }

        for (Object[] value : dsValuesList) {
            int newRow = ds.newRow();
            for (int i = 0; i < dsPropertyNames.length; i++) {
                ds.set(newRow, dsPropertyNames[i], value[i]);
            }
        }

        return ds;
    }

    public static List<Map<String, Object>> createDefaultMaps() {

        List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();

        for (Object[] value : dsValuesList) {

            Map<String, Object> dataMap = new HashMap<String, Object>();
            
            for (int i=0; i<dsPropertyNames.length; i++) {
                dataMap.put(dsPropertyNames[i], value[i]);
            }
            mapList.add(dataMap);
        }

        return mapList;
    }

    public static void compareDefaultBeans(List<DefaultBean> beanList) {

        if (beanList == null) {
            Assert.fail("bean list is null");
        }

        int expectedSize = dsValuesList.size();
        int actualSize = beanList.size();
        Assert.assertEquals("bean list size does not matched. please check converted columns. expected=" + expectedSize
                + ", actual=" + actualSize, expectedSize, actualSize);

        for (int rowIndex = 0; rowIndex < actualSize; rowIndex++) {
            DefaultBean defaultBean = beanList.get(rowIndex);
            NexacroBeanWrapper beanWrapper = NexacroBeanWrapper.createBeanWrapper(defaultBean);

            NexacroBeanProperty[] properties = beanWrapper.getProperties();
            if (dsPropertyNames.length != properties.length) {
                Assert.fail("bean properties does not matched. please check converted columns. expected="
                        + dsPropertyNames.length + ", actual=" + properties.length);
            }

            Object[] values = dsValuesList.get(rowIndex);
            for (int propertyIndex = 0; propertyIndex < dsPropertyNames.length; propertyIndex++) {
                NexacroBeanProperty property = beanWrapper.getProperty(dsPropertyNames[propertyIndex]);

                Object expectedValue = values[propertyIndex];
                Object actualValue = beanWrapper.getPropertyValue(dsPropertyNames[propertyIndex]);

                Assert.assertEquals("row(" + rowIndex + ") property(" + dsPropertyNames[propertyIndex]
                        + ") value does not matched. expected=" + expectedValue + ", actual=" + actualValue,
                        expectedValue, actualValue);
            }
            
        }
        
    }
    
    

    public static void compareDefaultDataSet(DataSet ds, int addedColumns) {

        if (ds == null) {
            Assert.fail("compare DataSet is null");
        }

        int expectedSize = dsPropertyNames.length + addedColumns;
        int actualSize = ds.getColumnCount();
        Assert.assertEquals("DataSet column does not matched. please check converted columns. expected=" + expectedSize
                + ", actual=" + actualSize, expectedSize, actualSize);

        // check column
        for (int propertyIndex = 0; propertyIndex < dsPropertyNames.length; propertyIndex++) {

            String propertyName = dsPropertyNames[propertyIndex];
            ColumnHeader column = ds.getColumn(propertyName);

            if (!propertyName.equals(column.getName())) {
                Assert.fail("column name does not matched. expected=" + dsPropertyNames[propertyIndex] + ", autual="
                        + column.getName());
            }
            if (!dsDataTypes[propertyIndex].equals(column.getPlatformDataType())) {
                Assert.fail(column.getName() + " data type does not matched. expected=" + dsDataTypes[propertyIndex] + ", autual="
                        + column.getPlatformDataType());
            }
        }

        // check values
        int rowCount = ds.getRowCount();
        if (dsValuesList.size() != rowCount) {
            Assert.fail("DataSet rows does not matched. expected=" + dsValuesList.size() + ", actual=" + rowCount);
        }

        for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {

            Object[] values = dsValuesList.get(rowIndex);
            for (int propertyIndex = 0; propertyIndex < dsPropertyNames.length; propertyIndex++) {
                String propertyName = dsPropertyNames[propertyIndex];

                Object expectedValue = values[propertyIndex];
                Object actualValue = ds.getObject(rowIndex, propertyName);

                Assert.assertEquals("row(" + rowIndex + ") property(" + dsPropertyNames[propertyIndex]
                        + ") value does not matched. expected=" + expectedValue + ", actual=" + actualValue,
                        expectedValue, actualValue);
            }
        }

    }

    public static void compareDefaultMaps(List<Map<String, Object>> mapList) {

        if (mapList == null) {
            Assert.fail("map list is null");
        }

        int size = mapList.size();
        if (dsValuesList.size() != size) {
            Assert.fail("map list size does not matched. expected=" + dsValuesList.size() + ", actual=" + size);
        }

        for (int rowIndex = 0; rowIndex < size; rowIndex++) {
            Map<String, Object> dataMap = mapList.get(rowIndex);

            Set<String> keySet = dataMap.keySet();

            // row type이 추가되어 있다.
            int expectedSize = dsPropertyNames.length + 1;
            int actualSize = keySet.size();
            Assert.assertEquals("map properties does not matched. please check converted columns. expected="
                    + expectedSize + ", actual=" + actualSize, expectedSize, actualSize);

            Object[] values = dsValuesList.get(rowIndex);
            for (int propertyIndex = 0; propertyIndex < dsPropertyNames.length; propertyIndex++) {

                String propertyName = dsPropertyNames[propertyIndex];

                Object expectedValue = values[propertyIndex];
                Object actualValue = dataMap.get(propertyName);

                Assert.assertEquals("row(" + rowIndex + ") property(" + dsPropertyNames[propertyIndex]
                        + ") value does not matched. expected=" + expectedValue + ", actual=" + actualValue,
                        expectedValue, actualValue);
            }
            
        }

    }
    
    public static class StaticPropertyBean {

        private String name;
        private static double commissionPercent;

        /**
         * @return the name
         */
        public String getName() {
            return name;
        }

        /**
         * @param name
         *            the name to set
         */
        public void setName(String name) {
            this.name = name;
        }

        /**
         * @return the commissionPercent
         */
        public static double getCommissionPercent() {
            return commissionPercent;
        }

        /**
         * @param commissionPercent the commissionPercent to set
         */
        public static void setCommissionPercent(double commissionPercent) {
            StaticPropertyBean.commissionPercent = commissionPercent;
        }

    }

    public static class DataSetRowTypeBean implements DataSetRowTypeAccessor {

        private String name;
        private int rowType;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public int getRowType() {
            return this.rowType;
        }

        @Override
        public void setRowType(int rowType) {
            this.rowType = rowType;
        }

    }
    
    public static class DataSetSavedDataBean implements DataSetSavedDataAccessor<DataSetSavedDataBean>, DataSetRowTypeAccessor {

        private String name;
        private int rowType;
        
        private DataSetSavedDataBean savedData;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public DataSetSavedDataBean getData() {
            return this.savedData;
        }

        @Override
        public void setData(DataSetSavedDataBean savedData) {
            this.savedData = savedData;
        }

        @Override
        public int getRowType() {
            return this.rowType;
        }

        @Override
        public void setRowType(int rowType) {
            this.rowType = rowType;
        }

    }

}
