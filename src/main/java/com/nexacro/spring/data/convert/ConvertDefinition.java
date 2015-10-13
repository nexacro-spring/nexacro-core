package com.nexacro.spring.data.convert;


/**
 * <pre>
 * Statements
 * </pre>
 * 
 * @ClassName : NexacroConvertDefinition.java
 * @Description : 클래스 설명을 기술합니다.
 * @author Park SeongMin
 * @since 2015. 7. 28.
 * @version 1.0
 * @see
 * @Modification Information
 * 
 *               <pre>
 *     since          author              description
 *  ===========    =============    ===========================
 *  2015. 7. 28.     Park SeongMin     최초 생성
 * </pre>
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
