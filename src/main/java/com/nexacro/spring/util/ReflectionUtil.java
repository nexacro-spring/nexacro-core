package com.nexacro.spring.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReflectionUtil {

    public static Field[] getFields(String className) throws ReflectionFailException {
        Field[] fields = null;

        try {
            Class<?> cls = Class.forName(className);

            fields = cls.getFields();

        } catch (ClassNotFoundException e) {
            throw new ReflectionFailException("class not found.", e);
        }

        return fields;
    }

    public static String[] getPublicFieldNames(String className) throws ReflectionFailException {
        Field[] fields = getFields(className);

        List<String> fieldNameList = new ArrayList<String>();

        for (int fieldsIndex = 0; fieldsIndex < fields.length; fieldsIndex++) {
            if (Modifier.isPublic(fields[fieldsIndex].getModifiers())) {
                fieldNameList.add(fields[fieldsIndex].getName());
            }
        }

        String[] fieldsNames = new String[fieldNameList.size()];

        fieldsNames = fieldNameList.toArray(fieldsNames);

        return fieldsNames;

    }

    public static Method[] getMethods(Class<?> clazz) throws ReflectionFailException {

        Method[] methods = null;
        methods = clazz.getDeclaredMethods();

        return methods;
    }

    public static Method[] getMethods(Class<?> clazz, String methodName) throws ReflectionFailException {

        Method[] methods = getMethods(clazz);
        int foundCount = 0;
        for (int i = 0; i < methods.length; i++) {
            if (methods[i].getName().equals(methodName)) {
                foundCount++;
            }
        }

        Method[] foundMethods = new Method[foundCount];
        int foundMethodIndex = 0;
        for (int i = 0; i < methods.length; i++) {
            if (methods[i].getName().equals(methodName)) {
                foundMethods[foundMethodIndex] = methods[i];
                foundMethodIndex++;
            }
        }

        return foundMethods;

    }

    /**
     * Attempt to find a {@link Method} on the supplied class with the supplied
     * name
     * and parameter types. Searches all superclasses up to {@code Object}.
     * <p>
     * Returns {@code null} if no {@link Method} can be found.
     * 
     * @param clazz
     *            the class to introspect
     * @param name
     *            the name of the method
     * @param paramTypes
     *            the parameter types of the method
     *            (may be {@code null} to indicate any signature)
     * @return the Method object, or {@code null} if none found
     * @throws ReflectionFailException
     */
    public static Method getMethod(Class<?> clazz, String name, Class<?>... paramTypes) {
        if (clazz == null) {
            throw new IllegalArgumentException("Class should not be null");
        }
        if (name == null) {
            throw new IllegalArgumentException("Method name should not be null");
        }
        Class<?> searchType = clazz;
        while (searchType != null) {
            Method[] methods = (searchType.isInterface() ? searchType.getMethods() : searchType.getDeclaredMethods());
            for (Method method : methods) {
                if (name.equals(method.getName())
                        && (paramTypes == null || Arrays.equals(paramTypes, method.getParameterTypes()))) {
                    return method;
                }
            }
            searchType = searchType.getSuperclass();
        }
        return null;
    }

    public static Method[] getMethods(String className) throws ReflectionFailException {

        Class<?> cls = null;
        Method[] methods = null;
        try {
            cls = Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new ReflectionFailException("class not found.", e);
        }

        return getMethods(cls);

    }

    public static Method[] getMethods(Object object) throws ReflectionFailException {
        if (object == null) {
            return null;
        }
        return getMethods(object.getClass());
    }

    public static String[] getPublicMethodNames(String className) throws ReflectionFailException {

        Method[] methods = getMethods(className);

        List<String> methodNameList = new ArrayList<String>();

        for (int i = 0; i < methods.length; i++) {
            if (Modifier.isPublic(methods[i].getModifiers())) {
                methodNameList.add(methods[i].getName());
            }
        }

        String[] methodNames = new String[methodNameList.size()];
        methodNames = methodNameList.toArray(methodNames);

        return methodNames;

    }

    public static Object executeMethod(Method method, Object instanceObject, Object[] parameterValueObject)
            throws ReflectionFailException {
        Object retobj = null;
        try {
            retobj = method.invoke(instanceObject, parameterValueObject);
        } catch (IllegalArgumentException e) {
            throw new ReflectionFailException("execution method failed.", e);
        } catch (IllegalAccessException e) {
            throw new ReflectionFailException("execution method failed.", e);
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            StackTraceElement[] stackTraceElements = cause.getStackTrace();
            StringBuffer stackTraceBuffer = new StringBuffer();
            for (int i = 0; i < stackTraceElements.length; i++) {
                stackTraceBuffer.append("    at ");
                stackTraceBuffer.append(stackTraceElements[i].getClassName()).append(".");
                stackTraceBuffer.append(stackTraceElements[i].getMethodName());
                stackTraceBuffer.append("(").append(stackTraceElements[i].getFileName());
                stackTraceBuffer.append(":").append(stackTraceElements[i].getLineNumber()).append(")");
                stackTraceBuffer.append("\n");
            }
            throw new ReflectionFailException(stackTraceBuffer.toString(), e);
        }

        return retobj;
    }

    public static boolean isImplemented(Class targetClass, Class interfaceClass) {
        Class[] interfaceClasses = targetClass.getInterfaces();
        for (int i = 0; i < interfaceClasses.length; i++) {
            if (interfaceClasses[i] == interfaceClass) {
                return true;
            }
        }

        Class superClass = targetClass.getSuperclass();
        if (superClass != null) {
            return isImplemented(superClass, interfaceClass);
        }
        return false;
    }
    
    public static boolean isStaticField(Field field) {
        if(field == null) {
            return false;
        }
        return isStatic(field.getModifiers());
    }
    
    public static boolean isStaticMethod(Method method) {
        if(method == null) {
            return false;
        }
        return isStatic(method.getModifiers());
    }
    
    public static boolean isStatic(int modifiers) {
        return Modifier.isStatic(modifiers);
    }

    public static Object getFieldInstance(Object object, String fieldName) throws ReflectionFailException {

        if (object == null) {
            return null;
        }
        if (fieldName == null) {
            return null;
        }

        String[] fieldNames = fieldName.split("\\.");
        return getFieldInstance(object, fieldNames);

    }

    public static Field getField(Object object, String fieldName) throws ReflectionFailException {

        if (object == null) {
            return null;
        }
        if (fieldName == null) {
            return null;
        }

        Field field = null;
        Class clazz = null;

        clazz = object.getClass();

        return getField(clazz, fieldName);

    }

    public static Field getField(Class clazz, String fieldName) throws ReflectionFailException {

        if (clazz == null) {
            return null;
        }
        if (fieldName == null) {
            return null;
        }

        Field field = null;

        Exception rootException = null;
        try {
            field = clazz.getDeclaredField(fieldName);
            return field;
        } catch (SecurityException e) {
            rootException = e;
        } catch (NoSuchFieldException e) {
            rootException = e;
        }

        Class superClass = clazz.getSuperclass();
        while (superClass != null) {
            try {
                field = superClass.getDeclaredField(fieldName);
                return field;
            } catch (SecurityException e) {
            } catch (NoSuchFieldException e) {
            }
            superClass = superClass.getSuperclass();
        }

        throw new ReflectionFailException("getting field " + fieldName + " failed.", rootException);
    }

    public static Object getFieldInstance(Object object, String[] fieldNames) throws ReflectionFailException {

        if (object == null) {
            return null;
        }
        if (fieldNames == null) {
            return null;
        }
        if (fieldNames.length == 0) {
            return null;
        }

        try {
            Field field = getField(object, fieldNames[0]);
            field.setAccessible(true);

            if (fieldNames.length == 1) {
                Object value = field.get(object);
                return value;
            } else {
                Object subObject = field.get(object);
                if (subObject == null) {
                    return null;
                }

                String[] subFieldNames = new String[fieldNames.length - 1];
                String subFieldName = "";
                for (int i = 1; i < fieldNames.length; i++) {
                    subFieldNames[i - 1] = fieldNames[i];
                }
                return getFieldInstance(subObject, subFieldNames);
            }

        } catch (SecurityException e) {
            throw new ReflectionFailException("getting field value failed.", e);
        } catch (IllegalArgumentException e) {
            throw new ReflectionFailException("getting field value failed.", e);
        } catch (IllegalAccessException e) {
            throw new ReflectionFailException("getting field value failed.", e);
        }

    }

    public static Method[] getAllDeclaredMethods(Class<?> leafClass) {
        final List<Method> methods = new ArrayList<Method>(32);

        doWithMethods(leafClass, methods);

        return methods.toArray(new Method[methods.size()]);
    }

    private static void doWithMethods(Class clazz, List<Method> methodList) {
        Method[] methods = clazz.getDeclaredMethods();
        for (int i = 0; i < methods.length; i++) {
            methodList.add(methods[i]);
        }

        if (clazz.getSuperclass() != null) {
            doWithMethods(clazz.getSuperclass(), methodList);
        } else if (clazz.isInterface()) {
            for (Class<?> superIfc : clazz.getInterfaces()) {
                doWithMethods(superIfc, methodList);
            }
        }
    }

    public static void makeAccessible(Field field) {
        if ((!Modifier.isPublic(field.getModifiers()) || !Modifier.isPublic(field.getDeclaringClass().getModifiers()) || Modifier
                .isFinal(field.getModifiers())) && !field.isAccessible()) {
            field.setAccessible(true);
        }
    }

    public static void makeAccessible(Method method) {
        if ((!Modifier.isPublic(method.getModifiers()) || !Modifier.isPublic(method.getDeclaringClass().getModifiers()))
                && !method.isAccessible()) {
            method.setAccessible(true);
        }
    }

    public static void makeAccessible(Constructor<?> ctor) {
        if ((!Modifier.isPublic(ctor.getModifiers()) || !Modifier.isPublic(ctor.getDeclaringClass().getModifiers()))
                && !ctor.isAccessible()) {
            ctor.setAccessible(true);
        }
    }
    
    public static <T> T instantiateClass(Class<T> clazz) throws ReflectionFailException {
        if(clazz == null) {
            throw new ReflectionFailException("Class must not be null");
        }
        if (clazz.isInterface()) {
            throw new ReflectionFailException(clazz+ " specified class is an interface");
        }
        try {
            return instantiateClass(clazz.getDeclaredConstructor());
        } catch (NoSuchMethodException ex) {
            throw new ReflectionFailException(clazz+ " no default constructor found", ex);
        }
    }
    
    public static <T> T instantiateClass(Constructor<T> ctor, Object... args) throws ReflectionFailException {
        if(ctor == null) {
            throw new ReflectionFailException("Constructor must not be null");
        }
        try {
            makeAccessible(ctor);
            return ctor.newInstance(args);
        } catch (InstantiationException ex) {
            throw new ReflectionFailException(ctor.getDeclaringClass()+ " is it an abstract class?", ex);
        } catch (IllegalAccessException ex) {
            throw new ReflectionFailException(ctor.getDeclaringClass() + " is the constructor accessible?", ex);
        } catch (IllegalArgumentException ex) {
            throw new ReflectionFailException(ctor.getDeclaringClass() + " illegal arguments for constructor", ex);
        } catch (InvocationTargetException ex) {
            throw new ReflectionFailException(ctor.getDeclaringClass() + " constructor threw exception", ex.getTargetException());
        }
    }

}
