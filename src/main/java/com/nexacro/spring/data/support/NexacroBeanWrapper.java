package com.nexacro.spring.data.support;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.NotWritablePropertyException;
import org.springframework.util.ReflectionUtils;

import com.nexacro.spring.util.ReflectionFailException;
import com.nexacro.spring.util.ReflectionUtil;

/**
 * <p>Java Beans 의 구조를 변경하기 위한 Wrapper class
 *
 * @author Park SeongMin
 * @since 08.04.2015
 * @version 1.0
 * @see BeanWrapper
 */
public class NexacroBeanWrapper {

/*
	내부적으로 Java의 Introspection 를 통해 MemberField의 정보를 알아내고 값을 설정한다.
	Java의 PropertyDescriptor의 경우 Method 명칭은 MemberField의 명칭으로 get | set 으로 정의된다. 하지만 boolean의 경우 is가 생략되기 때문에 별도로 처리해야 한다. 
	Member Field에 값 할당 시 Spring에서 데이터 변경에대한 Event 처리, 데이터 Type 처리 등의 처리로 속도가 현저히 떨어지기 때문에 값을 설정할 경우에는 reflection을 이용하여 바로 설정하도록 한다.
	Member Field 의 readMethod (getter)가 static일 경우 Spring에서 Method를 찾을수 없는 상태가 된다. Read method가 null 일 경우 static method를 찾아서 설정해야 한다.

*/
	
/* 
  BeanWrapper의 경우 Method 명칭에 해당하는 property 명칭으로 값을 설정한다.
    하지만 field가 boolean일 경우 eclipseis에서 generation 되는 메서드의 명칭은 is가 생략되기 때문에 
    해당부분만을 처리하며, 나머지는 spring으로 위임한다.
 */
    
    private BeanWrapper beanWrapper;
    private CachedBeanMappings cachedMapping;
    
    
    private NexacroBeanWrapper(Object obj) {
        beanWrapper = new BeanWrapperImpl(obj);
    }
    
    private NexacroBeanWrapper(Class<?> clazz) {
        beanWrapper = new BeanWrapperImpl(clazz);
    }
    
    public NexacroBeanProperty[] getProperties() {
        return getCachedBeanMappings().getProperties();
    }
    
    public NexacroBeanProperty getProperty(String propertyName) {
        return getCachedBeanMappings().getProperty(propertyName);
    }

    public void setPropertyValue(NexacroBeanProperty property, Object value) {
        if(property == null) {
            return;
        }
        
        // 데이터 설정 시 beanwrapper를 사용하지 않고 직접 처리 한다.
        // 데이터 변경에 대한 Event 처리, Data Type 처리 등에 따른 속도 저하..
        // 10만건 처리 시 5초 vs 1초 정도 차이.
//        PropertyDescriptor propertyDescriptor = property.getPropertyDescriptor();
//        Method writeMethod = propertyDescriptor.getWriteMethod();
        Method writeMethod = property.getWriteMethod();
        try {
            ReflectionUtil.makeAccessible(writeMethod);
            writeMethod.invoke(getInstance(), value);
        } catch (IllegalArgumentException e) {
            throw new NotWritablePropertyException(getInstance().getClass(), property.getPropertyName(), "Could not set object property", e);
        } catch (IllegalAccessException e) {
            throw new NotWritablePropertyException(getInstance().getClass(), property.getPropertyName(), "Could not set object property", e);
        } catch (InvocationTargetException e) {
            throw new NotWritablePropertyException(getInstance().getClass(), property.getPropertyName(), "Could not set object property", e.getTargetException());
        }
        
//        if(property.getOriginalPropertyName() != null) {
//            beanWrapper.setPropertyValue(property.getOriginalPropertyName(), value);    
//        } else {
//            beanWrapper.setPropertyValue(property.getPropertyName(), value);
//        }
    }
    
    /**
     * 입력받은 명칭(propertyName)에 해당하는 멤버필드에 값(value)를 설정한다.
     * 
     * @param propertyName
     * @param value
     */
    public void setPropertyValue(String propertyName, Object value) {
        NexacroBeanProperty property = getCachedBeanMappings().getProperty(propertyName);
        if(property == null ) {
            throw new NotWritablePropertyException(getInstance().getClass(), propertyName);
        }
        setPropertyValue(property, value);
    }
    
    public Object getPropertyValue(NexacroBeanProperty property) {
        if(property == null) {
            return null;
        }
        
        String propertyName = property.getPropertyName();
        if(property.getOriginalPropertyName() != null) {
            propertyName = property.getOriginalPropertyName();
        }
        
//        if(beanWrapper.isReadableProperty(propertyName)) {
        return beanWrapper.getPropertyValue(propertyName);
        
    }

    /**
     * 입력받은 명칭(propertyName)에 해당하는 멤버필드에 값(value)를 반환한다.
     * @param propertyName
     * @return value
     */
    public Object getPropertyValue(String propertyName) {
        NexacroBeanProperty property = getCachedBeanMappings().getProperty(propertyName);
        if(property == null) {
            throw new NotWritablePropertyException(getInstance().getClass(), propertyName);
        }
        return getPropertyValue(property);
    }
    
    /**
     * 현재 설정 된 class의 object instance를 반환한다.
     * @return object instance
     */
    public Object getInstance() {
        return beanWrapper.getWrappedInstance();
    }
    
    private CachedBeanMappings getCachedBeanMappings() {
        if(cachedMapping != null) {
            return cachedMapping;
        }
        
        cachedMapping = CachedBeanMappings.beanMappings(beanWrapper);
        return cachedMapping;
    }
    
    /**
     * 입력받은 Object를 통해 {@code NexacroBeanWrapper}를 생성한다.
     * @param clazz
     * @return wrapped class
     */
    public static NexacroBeanWrapper createBeanWrapper(Object obj) {
        return new NexacroBeanWrapper(obj);
    }
    
    /**
     * 입력받은 class를 통해 {@code NexacroBeanWrapper}를 생성한다.
     * @param clazz
     * @return wrapped class
     */
    public static NexacroBeanWrapper createBeanWrapper(Class<?> clazz) {
        return new NexacroBeanWrapper(clazz);
    }
    
    /**
     * <p>Beans의 Property 중 nexacro에서 처리가능한 Field에 대한 정보를 cache 하는 class이다
     * @author Park SeongMin
     *
     */
    private static class CachedBeanMappings {
        
        private static Logger logger = LoggerFactory.getLogger(NexacroBeanWrapper.class);
        private static final String IS = "is";
        private static Map<Class, CachedBeanMappings> classCache = Collections.synchronizedMap(new HashMap<Class, CachedBeanMappings>());
        
        private Map<String, NexacroBeanProperty> propertyCache;  
        
        /* supported all properties */
        private NexacroBeanProperty[] beanProperties;
        
        private CachedBeanMappings(BeanWrapper beanWrapper) {
            initBeanPropertyNames(beanWrapper);
        }
        
        static CachedBeanMappings beanMappings(BeanWrapper beanWrapper) {
            
            Class wrappedClass = beanWrapper.getWrappedClass();
            CachedBeanMappings mapping = classCache.get(wrappedClass);
            if(mapping != null) {
                return mapping;
            }
            mapping = new CachedBeanMappings(beanWrapper);
            classCache.put(wrappedClass, mapping);
            return mapping;
        }
        
        private void initBeanPropertyNames(BeanWrapper beanWrapper) {
            
            propertyCache = new HashMap<String, NexacroBeanProperty>();
            List<NexacroBeanProperty> tmpList = new ArrayList<NexacroBeanProperty>();
            
            Class wrappedClass = beanWrapper.getWrappedClass();
            
            // not ordered.. 
            PropertyDescriptor[] propertyDescriptors = beanWrapper.getPropertyDescriptors();
            
            for(PropertyDescriptor descriptor: propertyDescriptors) {
                
                if(!validateReadAndWriteMethod(wrappedClass, descriptor)) {
                    continue;
                }
                
                // ignore row type
                if("rowType".equalsIgnoreCase(descriptor.getName())) {
                    continue;
                }
                
                String name = descriptor.getName();
                Class<?> propertyType = descriptor.getPropertyType();
                boolean isConverted = false;
                String adjustName = name;
                if(propertyType == boolean.class) {
                    if(!name.startsWith(IS)) {
                        try {
                            // check exist field
                            if(wrappedClass.getField(IS + getBaseName(name)) != null) { 
                                adjustName = IS + getBaseName(name);
                                isConverted = true;
                            }
                        } catch (SecurityException e) {
                        } catch (NoSuchFieldException e) {
                        }
                    }
                }
                
                NexacroBeanProperty beanProperty = new NexacroBeanProperty(adjustName, propertyType);
                if(isConverted) {
                    beanProperty.setOriginalPropertyName(name);
                }
                
                if(isStaticProperty(descriptor)) {
                    beanProperty.setStatic(true);
                }
                
//                beanProperty.setPropertyDescriptor(descriptor);
                beanProperty.setWriteMethod(descriptor.getWriteMethod());
                
                tmpList.add(beanProperty);
                propertyCache.put(adjustName, beanProperty);
            }
            beanProperties = new NexacroBeanProperty[tmpList.size()];
            beanProperties = tmpList.toArray(beanProperties);
        }
        
        /**
         * Bean의 Property 중 nexacro platform에서 처리 가능한 필드 정보만을 반환한다.
         * @return
         */
        public NexacroBeanProperty[] getProperties() {
            return beanProperties;
        }

        public NexacroBeanProperty getProperty(String name) {
            return propertyCache.get(name);
        }
        
        private boolean validateReadAndWriteMethod(Class<?> clazz, PropertyDescriptor descriptor) {
            
            String name = descriptor.getName();
            Method readMethod = descriptor.getReadMethod();
            Method writeMethod = descriptor.getWriteMethod();
            
            if(name == null) {
                return false;
            }
            
            if(!NexacroConverterHelper.isConvertibleType(descriptor.getPropertyType())) {
                // unsupported type
                return false;
            }
            
            if(readMethod == null && writeMethod != null) {
                // find static method.. introspection is unsupported static getter.
                String findPropertyName = "get"+getBaseName(name);
                Method findedMethod = ReflectionUtils.findMethod(clazz, findPropertyName);
                if(findedMethod != null) {
                    if(ReflectionUtil.isStaticMethod(findedMethod)) {
                        setStaticReadMethodIntoDescriptor(descriptor, findedMethod);
                    }
                }
                
                readMethod = descriptor.getReadMethod();
                if(readMethod != null) {
                    return true;
                }
                
                if(logger.isDebugEnabled()) {
                    logger.debug("skipped property {} of bean class[{}]:" +
                        " Bean Property {} is not readable or has an invalid getter or setter." +
                        " Does the return type of the getter match the parameter type of the setter"
                        , name, clazz, name);
                    
                }
                
            } else if(readMethod == null || writeMethod == null) {
                return false;
            }
            
            return true;
            
        }
        
        private void setStaticReadMethodIntoDescriptor(PropertyDescriptor descriptor, Method staticMethod) {
            
            // for spring GenericTypeAwarePropertyDescriptor
            Field field = null;
            try {
                field = ReflectionUtil.getField(descriptor.getClass(), "readMethod");
            } catch(ReflectionFailException e) {
                // nothing..
            }
            
            if(field != null) {
                ReflectionUtil.makeAccessible(field);
                try {
                    field.set(descriptor, staticMethod);
                } catch (IllegalArgumentException e) {
                    logger.error("{} finded static gerrer '{}' method setting failed.", descriptor.getName(), staticMethod);
                    return;
                } catch (IllegalAccessException e) {
                    logger.error("{} finded static gerrer '{}' method setting failed.", descriptor.getName(), staticMethod);
                    return;
                }
            } else {
                try {
                    descriptor.setReadMethod(staticMethod);
                } catch (IntrospectionException e) {
                    logger.error("{} finded static gerrer '{}' method setting failed.", descriptor.getName(), staticMethod);
                    return;
                }
            }
                
            
        }
        
        private boolean isStaticProperty(PropertyDescriptor descriptor) {
            
            Method readMethod = descriptor.getReadMethod();
            Method writeMethod = descriptor.getWriteMethod();
            
            if(ReflectionUtil.isStaticMethod(readMethod) && ReflectionUtil.isStaticMethod(writeMethod)) {
                return true;
            }
            return false;
        }
        
        private String getBaseName(String name) {
            if (name == null || name.length() == 0) {
                return name;
            }
            return name.substring(0, 1).toUpperCase(Locale.ENGLISH) + name.substring(1);
        }
        
    }
    
    
//    private static class GenericTypeAwarePropertyDescriptorWrapper extends GenericTypeAwarePropertyDescriptor {
//        
//        private Method staticReadMethod;
//        
//        
//    }
    
}
