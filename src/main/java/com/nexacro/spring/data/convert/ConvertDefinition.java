package com.nexacro.spring.data.convert;


/**
 * <p>데이터 변환에 대한 정보를 저장한다.
 * <p>DataSet 혹은 Variable의 명칭을 가지며, List 형식의 데이터 변환시  Generic Type 정보를 가진다. 
 * 
 * @author Park SeongMin
 * @since 07.28.2015
 * @version 1.0
 * @see
 */

public class ConvertDefinition {

    private String name;
    private Class genericType; // for generic
    private boolean isIgnoreException = false;

    public ConvertDefinition(String name) {
        setName(name);
    }

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
        if(name == null || name.length() == 0) {
            throw new IllegalArgumentException("converted name must be not null");
        }
        this.name = name;
    }

    /**
     * @return the isIgnoreException
     */
    public boolean isIgnoreException() {
        return isIgnoreException;
    }

    /**
     * @param isIgnoreException
     *            the isIgnoreException to set
     */
    public void setIgnoreException(boolean isIgnoreException) {
        this.isIgnoreException = isIgnoreException;
    }

    /**
     * @return the genericType
     */
    public Class getGenericType() {
        return genericType;
    }

    /**
     * @param genericType the genericType to set
     */
    public void setGenericType(Class genericType) {
        this.genericType = genericType;
    }

}
