package com.nexacro.spring.data.support;

import java.lang.reflect.Method;

/**
 * <pre>
 * Statements
 * </pre>
 *
 * @ClassName   : NexacroBeanProperty.java
 * @Description : 클래스 설명을 기술합니다.
 * @author Park SeongMin
 * @since 2015. 8. 10.
 * @version 1.0
 * @see
 * @Modification Information
 * <pre>
 *     since          author              description
 *  ===========    =============    ===========================
 *  2015. 8. 10.     Park SeongMin     최초 생성
 * </pre>
 */

public class NexacroBeanProperty {

    private final String propertyName;
    private final Class<?> propertyType;
    
    private boolean isStatic;
    private String originalPropertyName;
    
    // for performance..
//    private PropertyDescriptor descriptor;
    // descriptor의 getWriteMethod 시 synchoronized
    private Method writeMethod;
    
    public NexacroBeanProperty(String propertyName, Class<?> propertyType) {
        this.propertyName = propertyName;
        this.propertyType = propertyType;
    }
    
    public String getPropertyName() {
        return propertyName;
    }

    public Class<?> getPropertyType() {
        return propertyType;
    }

    void setStatic(boolean isStatic) {
        this.isStatic = isStatic;
    }
    
    public boolean isStatic() {
        return isStatic;
    }

    String getOriginalPropertyName() {
        return originalPropertyName;
    }

    void setOriginalPropertyName(String originalPropertyName) {
        this.originalPropertyName = originalPropertyName;
    }

    Method getWriteMethod() {
        return writeMethod;
    }

    void setWriteMethod(Method writeMethod) {
        this.writeMethod = writeMethod;
    }
    
//    PropertyDescriptor getPropertyDescriptor() {
//        return this.descriptor;
//    }
//    
//    void setPropertyDescriptor(PropertyDescriptor descriptor) {
//        this.descriptor = descriptor;
//    }
    
    
    
}
