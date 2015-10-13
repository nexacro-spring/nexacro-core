package com.nexacro.spring.data.metadata.support;

import com.nexacro.spring.data.metadata.NexacroMetaData;
import com.nexacro.spring.data.support.NexacroBeanWrapper;

/**
 * <pre>
 * Statements
 * </pre>
 *
 * @ClassName   : NexacroBeanMetaData.java
 * @Description : 클래스 설명을 기술합니다.
 * @author Park SeongMin
 * @since 2015. 8. 6.
 * @version 1.0
 * @see
 * @Modification Information
 * <pre>
 *     since          author              description
 *  ===========    =============    ===========================
 *  2015. 8. 6.     Park SeongMin     최초 생성
 * </pre>
 */

public class BeanMetaData extends NexacroMetaData {

    private Object metaDataObject;
    
    public BeanMetaData(Object obj) {
        setMetaData(obj);
    }
    
    public BeanMetaData(Class<?> genericType) {
        NexacroBeanWrapper beanWrapper = NexacroBeanWrapper.createBeanWrapper(genericType);
        Object insatance = beanWrapper.getInsatance();
        setMetaData(insatance);
    }
    
    @Override
    public void setMetaData(Object obj) {
        if(!(obj instanceof NexacroBeanWrapper)) {
            NexacroBeanWrapper beanWrapper = NexacroBeanWrapper.createBeanWrapper(obj);
            this.metaDataObject = beanWrapper.getInsatance();
        } else {
            this.metaDataObject = obj;
        }
    }

    @Override
    public Object getMetaData() {
        return this.metaDataObject;
    }

    @Override
    public String toString() {
        return "BeanMetaData [class=" + this.metaDataObject.getClass() + "]";
    }

}
