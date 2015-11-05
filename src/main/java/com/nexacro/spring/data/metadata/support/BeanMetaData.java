package com.nexacro.spring.data.metadata.support;

import com.nexacro.spring.data.metadata.NexacroMetaData;
import com.nexacro.spring.data.support.NexacroBeanWrapper;

/**
 * <p>{@code NexacroMetaData}의 구현체로 java bean의 설정 정보를 가진다.
 *
 * @author Park SeongMin
 * @since 08.06.2015
 * @version 1.0
 * @see
 */
public class BeanMetaData extends NexacroMetaData {

    private Object metaDataObject;
    
    public BeanMetaData(Object obj) {
        setMetaData(obj);
    }
    
    public BeanMetaData(Class<?> genericType) {
        NexacroBeanWrapper beanWrapper = NexacroBeanWrapper.createBeanWrapper(genericType);
        Object insatance = beanWrapper.getInstance();
        setMetaData(insatance);
    }
    
    @Override
    public void setMetaData(Object obj) {
        if(!(obj instanceof NexacroBeanWrapper)) {
            NexacroBeanWrapper beanWrapper = NexacroBeanWrapper.createBeanWrapper(obj);
            this.metaDataObject = beanWrapper.getInstance();
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
