package com.nexacro.spring.data.convert;


/**
 * <code>PlatformData</code>(<code>DataSet</code>|<code>Variable</code>)의 데이터 변환을 위한 인터페이스이다.
 * 
 * @author Park SeongMin
 * @since 07.28.2015
 * @version 1.0
 * @see
 */

public interface NexacroConverter<S, T> {

    /**
     * 
     * Convert the source of type S to target type T.
     * 
     * @param Source
     * @return
     * @throws NexacroConvertException
     */
    T convert(S source, ConvertDefinition definition) throws NexacroConvertException;
    
    /**
     * source에서 target으로 데이터 변환 가능여부를 반환한다.
     * @param source 대상 객체
     * @param target 변환되는 객체
     * @return can convertiable
     */
    boolean canConvert(Class source, Class target);
    
    /**
     * {@code NexacroConvertListener}를 등록한다.
     * @param listener
     * @see #removeListener(NexacroConvertListener)
     */
    void addListener(NexacroConvertListener listener);
    
    /**
     * {@code NexacroConvertListener}를 제거한다.
     * @param listener
     * @see #addListener(NexacroConvertListener)
     */
    void removeListener(NexacroConvertListener listener);
    
    public static final class ConvertiblePair {

        private final Class<?> sourceType;
        private final Class<?> targetType;

        public ConvertiblePair(Class<?> sourceType, Class<?> targetType) {
            this.sourceType = sourceType;
            this.targetType = targetType;
        }

        public Class<?> getSourceType() {
            return this.sourceType;
        }

        public Class<?> getTargetType() {
            return this.targetType;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || obj.getClass() != ConvertiblePair.class) {
                return false;
            }
            ConvertiblePair other = (ConvertiblePair) obj;
//            return this.sourceType.equals(other.sourceType) && this.targetType.equals(other.targetType);
            // support sub class
            return this.sourceType.equals(other.sourceType) && this.targetType.equals(other.targetType);

        }

        @Override
        public int hashCode() {
            return this.sourceType.hashCode() * 31 + this.targetType.hashCode();
        }

        @Override
        public String toString() {
            return this.sourceType.getName() + "->" + this.targetType.getName();
        }
        
        
    }

}