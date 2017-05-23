package com.nexacro.spring.data.convert;

import com.nexacro.xapi.data.DataSet;


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
    
    private DataSet schemaDataSet;
    private boolean disallowChangeStructure; // (v1.0.0에서  허용 하게 되어 있음)

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

    /**
     * 
     * @return disallow structure change
     */
	public boolean isDisallowChangeStructure() {
		return disallowChangeStructure;
	}

	/**
	 * 
	 * @param disallow structure change
	 */
	public void setDisallowChangeStructure(boolean disallowChangeStructure) {
		this.disallowChangeStructure = disallowChangeStructure;
	}

	/**
     * 
     * @return the schema dataSet
     */
	public DataSet getSchemaDataSet() {
		return schemaDataSet;
	}

	/**
	 * 
	 * @param schemaDataSet
	 */
	public void setSchemaDataSet(DataSet schemaDataSet) {
		this.schemaDataSet = schemaDataSet;
	}

}
