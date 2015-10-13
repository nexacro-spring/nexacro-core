package com.nexacro.spring.data.convert;


/**
 * <pre>
 * Statements
 * </pre>
 * 
 * @ClassName : NexacroConverter.java
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
    
    boolean canConvert(Class source, Class target);
    
    void addListener(NexacroConvertListener listener);
    
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