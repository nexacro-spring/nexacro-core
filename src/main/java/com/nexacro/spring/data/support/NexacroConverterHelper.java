package com.nexacro.spring.data.support;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.nexacro.spring.data.convert.NexacroConverter.ConvertiblePair;
import com.nexacro.spring.util.ReflectionUtil;
import com.nexacro.xapi.data.ColumnHeader;
import com.nexacro.xapi.data.DataSet;
import com.nexacro.xapi.data.DataTypes;
import com.nexacro.xapi.data.Variable;
import com.nexacro.xapi.data.datatype.DataType;
import com.nexacro.xapi.data.datatype.DataTypeFactory;
import com.nexacro.xapi.data.datatype.PlatformDataType;

/**
 * <p>DataSet혹은 Variable의 데이터 변환을 위한 helper class
 *
 * @author Park SeongMin
 * @since 2015. 7. 28.
 * @version 1.0
 * @see
 */
public abstract class NexacroConverterHelper {

    private static final Set<ConvertiblePair> listToDataSetConvertibleSets = Collections.singleton(new ConvertiblePair(List.class, DataSet.class));
    private static final Set<ConvertiblePair> dataSetToListConvertibleSets = Collections.singleton(new ConvertiblePair(DataSet.class, List.class));
    
    private static final Set<ConvertiblePair> objectToVariableConvertibleSets = new HashSet<ConvertiblePair>();
    private static final Set<ConvertiblePair> variableToObjectConvertibleSets = new HashSet<ConvertiblePair>();
    
    private static final Map<Class<?>, Class<?>> primitiveTypeWrapperMap = new HashMap<Class<?>, Class<?>>(8);
    private static final Map<Class<?>, Class<?>> nonPrimitiveTypeMap = new HashMap<Class<?>, Class<?>>(4);
    
    
    static {
        // byte[], int, long, float, double, boolean, Object, String, BigDecimal, Date
        // byte, char, short는 지원하지 않는다.
        primitiveTypeWrapperMap.put(byte[].class, Byte[].class);
        // 아래 항목 지원 시 데이터 유실이 발생할 수 있다.
//        primitiveWrapperTypeMap.put(byte.class, Byte.class);
//        primitiveWrapperTypeMap.put(char.class, Character.class);
//        primitiveWrapperTypeMap.put(short.class, Short.class);
        primitiveTypeWrapperMap.put(int.class, Integer.class);
        primitiveTypeWrapperMap.put(long.class, Long.class);
        primitiveTypeWrapperMap.put(float.class, Float.class);
        primitiveTypeWrapperMap.put(double.class, Double.class);
        primitiveTypeWrapperMap.put(boolean.class, Boolean.class);
        
        nonPrimitiveTypeMap.put(Object.class, Object.class);
        nonPrimitiveTypeMap.put(String.class, String.class);
        nonPrimitiveTypeMap.put(BigDecimal.class, BigDecimal.class);
        nonPrimitiveTypeMap.put(Date.class, Date.class);
        nonPrimitiveTypeMap.put(java.sql.Date.class, java.sql.Date.class); // used java.util.map in ibatis
        
        Set<Class<?>> keySet = primitiveTypeWrapperMap.keySet();
        for(Class<?> clazz: keySet) {
            objectToVariableConvertibleSets.add(new ConvertiblePair(clazz, Variable.class));
            objectToVariableConvertibleSets.add(new ConvertiblePair(primitiveTypeWrapperMap.get(clazz), Variable.class));
        }
        for(Class<?> clazz: keySet) {
            variableToObjectConvertibleSets.add(new ConvertiblePair(Variable.class, clazz));
            variableToObjectConvertibleSets.add(new ConvertiblePair(Variable.class, primitiveTypeWrapperMap.get(clazz)));
        }
        
        Set<Class<?>> nonKeySet = nonPrimitiveTypeMap.keySet();
        for(Class<?> clazz: nonKeySet) {
            objectToVariableConvertibleSets.add(new ConvertiblePair(clazz, Variable.class));
        }
        for(Class<?> clazz: nonKeySet) {
            variableToObjectConvertibleSets.add(new ConvertiblePair(Variable.class, clazz));
        }
        
    }
    
    static Set<ConvertiblePair> getObjectToVariableConvertibleTypes() {
        return objectToVariableConvertibleSets;
    }
    
    static Set<ConvertiblePair> getVariableToObjectConvertibleTypes() {
        return variableToObjectConvertibleSets;
    }
    
    static Set<ConvertiblePair> getListToDataSetConvertibleTypes() {
        return listToDataSetConvertibleSets;
    }
    
    static Set<ConvertiblePair> getDataSetToListConvertibleTypes() {
        return dataSetToListConvertibleSets;
    }
    
    public static Object getDefaultValue(DataType dataType) {
        
        int type = dataType.getType();
        
        if(type == DataTypes.STRING) {
            return DataTypes.DEFAULT_VALUE_STRING;
        } else if(type == DataTypes.INT) {
            return DataTypes.DEFAULT_VALUE_INT;
        } else if(type == DataTypes.LONG) {
            return DataTypes.DEFAULT_VALUE_LONG;
        } else if(type == DataTypes.FLOAT) {
            return DataTypes.DEFAULT_VALUE_FLOAT;
        } else if(type == DataTypes.DOUBLE) {
            return DataTypes.DEFAULT_VALUE_DOUBLE;
        } else if(type == DataTypes.BOOLEAN) {
            return DataTypes.DEFAULT_VALUE_BOOLEAN;
        } else if(type == DataTypes.DATE) {
            return DataTypes.DEFAULT_VALUE_DATE;
        } else if(type == DataTypes.DATE_TIME) {
            return DataTypes.DEFAULT_VALUE_DATE_TIME;
        } else if(type == DataTypes.TIME) {
            return DataTypes.DEFAULT_VALUE_TIME;
        } else if(type == DataTypes.BIG_DECIMAL) {
            return DataTypes.DEFAULT_VALUE_BIG_DECIMAL;
        } else if(type == DataTypes.BLOB) {
            return DataTypes.DEFAULT_VALUE_BLOB;
        }
        return DataTypes.DEFAULT_VALUE_OBJECT;
    }
    
    public static Object getDefaultMetaDataValue(DataType dataType) {
        
        int type = dataType.getType();
        
        if(type == DataTypes.STRING) {
            return "";
        } else if(type == DataTypes.INT) {
            return DataTypes.DEFAULT_VALUE_INT;
        } else if(type == DataTypes.LONG) {
            return DataTypes.DEFAULT_VALUE_LONG;
        } else if(type == DataTypes.FLOAT) {
            return DataTypes.DEFAULT_VALUE_FLOAT;
        } else if(type == DataTypes.DOUBLE) {
            return DataTypes.DEFAULT_VALUE_DOUBLE;
        } else if(type == DataTypes.BOOLEAN) {
            return DataTypes.DEFAULT_VALUE_BOOLEAN;
        } else if(type == DataTypes.DATE) {
            return new Date();
        } else if(type == DataTypes.DATE_TIME) {
            return new Date();
        } else if(type == DataTypes.TIME) {
            return new Date();
        } else if(type == DataTypes.BIG_DECIMAL) {
            return new BigDecimal(0);
        } else if(type == DataTypes.BLOB) {
            return new byte[0];
        }
        
        return DataTypes.DEFAULT_VALUE_OBJECT;
    }
    
    public static DataType getDataType(Class targetClass) {
        
        // DataTypeFactory의 byte[] 문제.
        if(String.class.equals(targetClass)) {
            return PlatformDataType.STRING;
        } else if(int.class.equals(targetClass) || Integer.class.equals(targetClass)) {
            return PlatformDataType.INT;
        } else if(long.class.equals(targetClass) || Long.class.equals(targetClass)) {
            return PlatformDataType.LONG;
        } else if(float.class.equals(targetClass) || Float.class.equals(targetClass)) {
            return PlatformDataType.FLOAT;
        } else if(double.class.equals(targetClass) || Double.class.equals(targetClass)) {
            return PlatformDataType.DOUBLE;
        } else if(boolean.class.equals(targetClass) || Boolean.class.equals(targetClass)) {
            return PlatformDataType.BOOLEAN;
        } else if(Date.class.equals(targetClass)) {
            return PlatformDataType.DATE_TIME;
        } else if(BigDecimal.class.equals(targetClass)) {
            return PlatformDataType.BIG_DECIMAL;
        } else if(Object.class.equals(targetClass)) {
            return PlatformDataType.UNDEFINED;
        } else if(targetClass.isArray() && (byte[].class.equals(targetClass) || Byte[].class.equals(targetClass))) {
            return PlatformDataType.BLOB;
        }
        
        return PlatformDataType.UNDEFINED;
    }
    
    static DataType getDataTypeOfValue(Object value) {
        if(value == null) {
            return PlatformDataType.UNDEFINED;
        }
        DataType dataTypeOfValue = DataTypeFactory.getDataTypeOfValue(value);
        if(dataTypeOfValue.getType() == PlatformDataType.UNDEFINED.getType()) {
            if(value instanceof Byte[]) {
                dataTypeOfValue = PlatformDataType.BLOB;
            }
        } else if(dataTypeOfValue.getType() == PlatformDataType.DATE.getType()) {
            // data는 DATE_TIME으로 변경.
            dataTypeOfValue = PlatformDataType.DATE_TIME;
        }
        return dataTypeOfValue;
    }
    
    
    static Variable toVariable(String name, Object value) {
        Variable var = new Variable(name);
        if(value == null) {
            return var;
        }
        // 최초 type 설정이 안되어 있을 경우 설정된 값에 따라 Type이 설정 된다.
        if(value instanceof Byte[]) {
            var.set(toPrimitive((Byte[]) value));
        } else {
            var.set(value);
        }
        return var;
    }
    
    static Object toObject(Variable variable, Class<?> targetClass) {
        
        // 직접 변환할까..
        if(String.class.equals(targetClass)) {
            return variable.getString();
        } else if(int.class.equals(targetClass) || Integer.class.equals(targetClass)) {
            return variable.getInt();
        } else if(long.class.equals(targetClass) || Long.class.equals(targetClass)) {
            return variable.getLong();
        } else if(float.class.equals(targetClass) || Float.class.equals(targetClass)) {
            return variable.getFloat();
        } else if(double.class.equals(targetClass) || Double.class.equals(targetClass)) {
            return variable.getDouble();
        } else if(boolean.class.equals(targetClass) || Boolean.class.equals(targetClass)) {
            return variable.getBoolean();
        } else if(Date.class.equals(targetClass)) {
            return variable.getDateTime();
        } else if(BigDecimal.class.equals(targetClass)) {
            return variable.getBigDecimal();
        } else if(Object.class.equals(targetClass)) {
            return variable.getObject();
        } else if(targetClass.isArray()) {
            if(byte[].class.equals(targetClass)) {
                return variable.getBlob();    
            } else if(Byte[].class.equals(targetClass)) {
                byte[] blob = variable.getBlob();
                return toObject(blob);
            }
        }
        
        return variable.getObject();
    }
    
    static Object toObject(Object obj) {
        if(obj == null) {
            return null;
        }
        if(obj instanceof Byte[]) {
            return toPrimitive((Byte[])obj);
        }
        return obj;
    }
    
    static Object toObjectFromDataSetValue(DataSet ds, int rowIndex, int colIndex, Class<?> targetClass, boolean isSavedData, boolean isRemovedData) {

        if(Object.class.equals(targetClass)) {
            if(isSavedData) {
                return ds.getSavedData(rowIndex, colIndex);
            } else if(isRemovedData) {
                return ds.getRemovedData(rowIndex, colIndex);
            } else {
                return ds.getObject(rowIndex, colIndex);
            }
        } else if(String.class.equals(targetClass)) {
            if(isSavedData) {
                return ds.getSavedStringData(rowIndex, colIndex);
            } else if(isRemovedData) {
                return ds.getRemovedStringData(rowIndex, colIndex);
            } else {
                return ds.getString(rowIndex, colIndex);
            }
        } else if(int.class.equals(targetClass) || Integer.class.equals(targetClass)) {
            if(isSavedData) {
                return ds.getSavedIntData(rowIndex, colIndex);
            } else if(isRemovedData) {
                return ds.getRemovedIntData(rowIndex, colIndex);
            } else {
                return ds.getInt(rowIndex, colIndex);
            }
        } else if(long.class.equals(targetClass) || Long.class.equals(targetClass)) {
            if(isSavedData) {
                return ds.getSavedLongData(rowIndex, colIndex);
            } else if(isRemovedData) {
                return ds.getRemovedLongData(rowIndex, colIndex);
            } else {
                return ds.getLong(rowIndex, colIndex);
            }
        } else if(float.class.equals(targetClass) || Float.class.equals(targetClass)) {
            if(isSavedData) {
                return ds.getSavedFloatData(rowIndex, colIndex);
            } else if(isRemovedData) {
                return ds.getRemovedFloatData(rowIndex, colIndex);
            } else {
                return ds.getFloat(rowIndex, colIndex);
            }
        } else if(double.class.equals(targetClass) || Double.class.equals(targetClass)) {
            if(isSavedData) {
                return ds.getSavedDoubleData(rowIndex, colIndex);
            } else if(isRemovedData) {
                return ds.getRemovedDoubleData(rowIndex, colIndex);
            } else {
                return ds.getDouble(rowIndex, colIndex);
            }
        } else if(boolean.class.equals(targetClass) || Boolean.class.equals(targetClass)) {
            if(isSavedData) {
                return ds.getSavedBooleanData(rowIndex, colIndex);
            } else if(isRemovedData) {
                return ds.getRemovedBooleanData(rowIndex, colIndex);
            } else {
                return ds.getBoolean(rowIndex, colIndex);
            }
        } else if(Date.class.equals(targetClass)) {
            if(isSavedData) {
                return ds.getSavedDateTimeData(rowIndex, colIndex);
            } else if(isRemovedData) {
                return ds.getRemovedDateTimeData(rowIndex, colIndex);
            } else {
                return ds.getDateTime(rowIndex, colIndex);
            }
        } else if(BigDecimal.class.equals(targetClass)) {
            if(isSavedData) {
                return ds.getSavedBigDecimalData(rowIndex, colIndex);
            } else if(isRemovedData) {
                return ds.getRemovedBigDecimalData(rowIndex, colIndex);
            } else {
                return ds.getBigDecimal(rowIndex, colIndex);
            }
        } else if(targetClass.isArray()) {
            if(byte[].class.equals(targetClass)) {
                if(isSavedData) {
                    return ds.getSavedBlobData(rowIndex, colIndex);
                } else if(isRemovedData) {
                    return ds.getRemovedBlobData(rowIndex, colIndex);
                } else {
                    return ds.getBlob(rowIndex, colIndex);
                }
            } else if(Byte[].class.equals(targetClass)) {
                byte[] blob;
                if(isSavedData) {
                    blob = ds.getSavedBlobData(rowIndex, colIndex);
                    return toObject(blob);
                } else if(isRemovedData) {
                    blob = ds.getRemovedBlobData(rowIndex, colIndex);
                    return toObject(blob);
                } else {
                    blob = ds.getBlob(rowIndex, colIndex);
                    return toObject(blob);
                }
            }
        }
        
        // return object
        if(isSavedData) {
            return ds.getSavedData(rowIndex, colIndex);
        } else if(isRemovedData) {
            return ds.getRemovedData(rowIndex, colIndex);
        } else {
            return ds.getObject(rowIndex, colIndex);
        }
    }
    
    static boolean isSupportedBean(Class clazz) {
        if(!clazz.isInterface() && !clazz.isPrimitive() && !clazz.isEnum() && !clazz.isArray()) {
            return true;
        }
        return false;
    }
    
    static Map<String, Field> getAdjustConvertibleFields(Class clazz, DataSet ds) {
        
        Map<String, Field> accessibleFields = getAccessibleFields(clazz);
        
        Map<String, Field> adjustConvertibleFields = new HashMap<String, Field>();
        // 획득한 field와 dataset field를 검사하여 실제로 사용될 field 들을 추려둔다.
        int columnCount = ds.getColumnCount();
        for(int i=0; i<columnCount; i++) {
            ColumnHeader column = ds.getColumn(i);
            String columnName = column.getName();
            
            // 대소문자를 구별한다!
            Field field = accessibleFields.get(columnName);
            if(field != null) {
                adjustConvertibleFields.put(columnName, field);
            }
        }        
        
        return adjustConvertibleFields;
        
    }
    
    static Map<String, Field> getAccessibleFields(Class clazz) {
        
        Map<String, Field> accessibleFields = new HashMap<String, Field>();
        
        Class<?> searchType = clazz;
        while (!Object.class.equals(searchType) && searchType != null) {
            Field[] fields = searchType.getDeclaredFields();
            for (Field field : fields) {
                Class<?> type = field.getType();
                if(isConvertibleType(type)) {
                    ReflectionUtil.makeAccessible(field);
                    accessibleFields.put(field.getName(), field);
                }
            }
            searchType = searchType.getSuperclass();
        }
        
        return accessibleFields;
    }
    
    static boolean isConvertibleType(Class<?> type) {
        if(primitiveTypeWrapperMap.get(type) != null) {
            return true;
        } else if(nonPrimitiveTypeMap.get(type) != null) {
            return true;
        } else if(primitiveTypeWrapperMap.containsValue(type)) {
            return true;
        } else if(nonPrimitiveTypeMap.containsValue(type)) {
            return true;
        } 
        
        return false;
    }
    
    /* commons.lang.ArrayUtils source*/
    private static Byte[] toObject(byte[] array) {
        if (array == null) {
            return null;
        } else if (array.length == 0) {
            return new Byte[0];
        }
        final Byte[] result = new Byte[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = new Byte(array[i]);
        }
        return result;
    }
    
    private static byte[] toPrimitive(Byte[] array) {
        if (array == null) {
            return null;
        } else if (array.length == 0) {
            return new byte[0];
        }
        final byte[] result = new byte[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = array[i].byteValue();
        }
        return result;
    }
    
}
