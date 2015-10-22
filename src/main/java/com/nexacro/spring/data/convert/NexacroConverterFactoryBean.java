package com.nexacro.spring.data.convert;

import java.util.Set;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * <p>spring의 FactoryBean으로 등록가능한 factory 클래스이다.
 *
 * @author Park SeongMin
 * @since 08.09.2015
 * @version 1.0
 * @see
 */

public class NexacroConverterFactoryBean implements FactoryBean<NexacroConverterFactory>, InitializingBean {

    private Set<NexacroConverter> converters;
    
    private NexacroConverterFactory converterFactory;
    
    /**
     * @param converters the converters to set
     */
    public void setConverters(Set<NexacroConverter> converters) {
        this.converters = converters;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.converterFactory = NexacroConverterFactory.getInstance();
        converterFactory.register(converters);
    }

    @Override
    public NexacroConverterFactory getObject() throws Exception {
        return converterFactory;
    }

    @Override
    public Class<NexacroConverterFactory> getObjectType() {
        return NexacroConverterFactory.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

}
