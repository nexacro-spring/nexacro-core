package com.nexacro.spring.view;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.Map;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.util.FileCopyUtils;

import com.nexacro.spring.NexacroConstants;
import com.nexacro.spring.NexacroException;
import com.nexacro.spring.data.NexacroFileResult;
import com.nexacro.spring.util.CharsetUtil;
import com.nexacro.spring.util.FileUtils;
import com.nexacro.xapi.data.PlatformData;
import com.nexacro.xapi.data.Variable;
import com.nexacro.xapi.tx.PlatformException;

/**
 * <p>nexacro platform으로 파일 데이터를 송신하기 위한 {@link org.springframework.web.servlet.View}이다.
 * 
 * <p>파일데이터 전송 시 사용되는 MIME TYPE의 경우 {@link javax.activation.MimetypesFileTypeMap}을 이용하여 처리된다.
 * 
 * @author Park SeongMin
 * @since 07.27.2015
 * @version 1.0
 *
 */
public class NexacroFileView extends NexacroView {

	public  NexacroFileView() {
	}
	
	@Override
	protected void renderMergedOutputModel(Map<String, Object> model,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
	    
	    Object object = model.get(NexacroConstants.ATTRIBUTE.NEXACRO_FILE_DATA);
        if(object == null || !(object instanceof NexacroFileResult)) {
            sendResponse(request, response);
            return;
        }
	    
        NexacroFileResult fileResult = (NexacroFileResult) object;
        
        String charset = fileResult.getCharset();
        String contentType = fileResult.getContentType();
        String originalName = fileResult.getOriginalName();
        File file = fileResult.getFile();
        if(file == null) {
            sendFailResponse(request, response, "send response failed. file is null.");
            return;
        }
        if(!FileUtils.isFileUsed(file)) {
            sendFailResponse(request, response, "send response failed. '" + file.getName() + "' can not be used.");
            return;
        }
        
        if(contentType == null) {
            contentType = MimetypesFileTypeMap.getDefaultFileTypeMap().getContentType(file);
        }
        
        charset = charset != null? charset: CharsetUtil.getCharsetOfRequest(request, "utf-8");
        contentType = contentType != null? contentType: "application/octet-stream";
        originalName = originalName != null? originalName: file.getName(); 
            
        String enName = URLEncoder.encode(originalName, "utf-8");
        
        response.setContentType(contentType + "; charset=" + charset);
        response.setHeader("Content-Transfer-Encoding", "binary");
        response.setHeader("Content-Disposition", "attachment; filename=" + enName + "; size=" + file.length());
        response.setHeader("Content-Description", "...");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "-1");
         
        OutputStream out = response.getOutputStream();
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            FileCopyUtils.copy(fis, out);
        } catch (Exception e) {
            sendFailResponse(request, response, e.getMessage());
        } finally {
            if (fis != null) { try { fis.close(); } catch (Exception e) {}}
        }
        
	}

    private void sendFailResponse(HttpServletRequest request, HttpServletResponse response, String errorMsg) throws PlatformException {

        PlatformData platformData = new PlatformData();
        platformData.addVariable(Variable.createVariable(NexacroConstants.ERROR.ERROR_CODE, NexacroException.DEFAULT_ERROR_CODE));
        platformData.addVariable(Variable.createVariable(NexacroConstants.ERROR.ERROR_MSG, errorMsg));
        
        sendResponse(request, response, platformData);
        
    }
}
