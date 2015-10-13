package com.nexacro.spring.data;

import java.io.File;

import com.nexacro.spring.resolve.NexacroHandlerMethodReturnValueHandler;

/**
 * <pre>
 * nexacro platform으로 File 데이터를 전송하기 위한 정보를 가진다.
 * </pre>
 * 
 * @ClassName : NexacroFileResult.java
 * @Description : 클래스 설명을 기술합니다.
 * @author Park SeongMin
 * @since 2015. 8. 18.
 * @version 1.0
 * @see NexacroHandlerMethodReturnValueHandler
 * @Modification Information
 * 
 *               <pre>
 *     since          author              description
 *  ===========    =============    ===========================
 *  2015. 8. 18.     Park SeongMin     최초 생성
 * </pre>
 */

public class NexacroFileResult {

    private File file;

    private String contentType;
    private String charset;
    private String originalName;

    public NexacroFileResult(File file) {
        if(file == null) {
            throw new IllegalArgumentException("file must not be null.");
        }
        this.file = file;
    }
    
    /**
     * @return the file
     */
    public File getFile() {
        return file;
    }

    /**
     * @param file
     *            the file to set
     */
    public void setFile(File file) {
        this.file = file;
    }

    /**
     * @return the contentType
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * @param contentType
     *            the contentType to set
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    /**
     * @return the charset
     */
    public String getCharset() {
        return charset;
    }

    /**
     * @param charset
     *            the charset to set
     */
    public void setCharset(String charset) {
        this.charset = charset;
    }

    /**
     * @return the originalName
     */
    public String getOriginalName() {
        return originalName;
    }

    /**
     * @param originalName
     *            the originalName to set
     */
    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

}
