package com.nexacro.spring.data.convert;

import java.util.Collection;
import java.util.List;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.nexacro.xapi.data.DataSet;
import com.nexacro.xapi.data.Variable;

/**
 * 
 * @author Park SeongMin
 * @since 08.04.2015
 * @version 1.0
 * @see
 */

public class NexacroConverterFactoryTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testDefaultConverter() {

        Class<?> source, target;
        NexacroConverter converter = null;

        source = Object.class;
        target = Variable.class;
        converter = NexacroConverterFactory.getConverter(source, target);
        Assert.assertNotNull(source + " -> " + target + " converter not registed.", converter);

        source = Variable.class;
        target = Object.class;
        converter = NexacroConverterFactory.getConverter(source, target);
        Assert.assertNotNull(source + " -> " + target + " converter not registed.", converter);

        source = List.class;
        target = DataSet.class;
        converter = NexacroConverterFactory.getConverter(source, target);
        Assert.assertNotNull(source + " -> " + target + " converter not registed.", converter);

        source = DataSet.class;
        target = List.class;
        converter = NexacroConverterFactory.getConverter(source, target);
        Assert.assertNotNull(source + " -> " + target + " converter not registed.", converter);

        source = DataSet.class;
        target = Object.class;
        converter = NexacroConverterFactory.getConverter(source, target);
        Assert.assertNotNull(source + " -> " + target + " converter not registed.", converter);
        
        source = Object.class;
        target = DataSet.class;
        converter = NexacroConverterFactory.getConverter(source, target);
        Assert.assertNotNull(source + " -> " + target + " converter not registed.", converter);
        
    }

    @Test
    public void testRegistConverter() {

        Class<?> source, target;
        source = Collection.class;
        target = DataSet.class;

        UndefinedConverter undefinedConverter = new UndefinedConverter(source, target);
        NexacroConverterFactory.register(undefinedConverter);
        NexacroConverter converter = NexacroConverterFactory.getConverter(source, target);
        Assert.assertNotNull(source + " -> " + target + " converter not registed.", converter);

    }

    private static class UndefinedConverter implements NexacroConverter {

        private Class<?> sourceType;
        private Class<?> targetType;

        UndefinedConverter(Class<?> sourceType, Class<?> targetType) {
            this.sourceType = sourceType;
            this.targetType = targetType;
        }

        @Override
        public Object convert(Object source, ConvertDefinition definition) throws NexacroConvertException {
            return null;
        }

        @Override
        public boolean canConvert(Class source, Class target) {
            if (sourceType.equals(source) && targetType.equals(target)) {
                return true;
            }
            return false;
        }

        @Override
        public void addListener(NexacroConvertListener listener) {
        }

        @Override
        public void removeListener(NexacroConvertListener listener) {

        }

    }

}
