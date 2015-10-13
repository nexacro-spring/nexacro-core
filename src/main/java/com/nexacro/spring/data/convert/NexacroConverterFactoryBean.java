package com.nexacro.spring.data.convert;

import java.util.Set;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * <pre>
 * Statements
 * </pre>
 *
 * @ClassName   : NexacroConverterFactoryBean.java
 * @Description : 클래스 설명을 기술합니다.
 * @author Park SeongMin
 * @since 2015. 8. 9.
 * @version 1.0
 * @see
 * @Modification Information
 * <pre>
 *     since          author              description
 *  ===========    =============    ===========================
 *  2015. 8. 9.     Park SeongMin     최초 생성
 * </pre>
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
