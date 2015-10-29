package com.nexacro.spring.data;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Locale;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

public class FirstRowHttpResponse implements HttpServletResponse {

    ServletOutputStream outputStream = null;
    String charsetEncoding = null;
    String contentType = null;

    public void setContentType(String arg0) {
        this.contentType = arg0;
    }

    public String getContentType() {
        return this.contentType;
    }

    public String getCharacterEncoding() {
        return this.charsetEncoding;
    }

    public void setCharacterEncoding(String charsetEncoding) {
        this.charsetEncoding = charsetEncoding;
    }

    public ServletOutputStream getOutputStream() throws IOException {
        return this.outputStream;
    }

    public void setOutputStream(ServletOutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void addCookie(Cookie arg0) {
    }

    public void addDateHeader(String arg0, long arg1) {
    }

    public void addHeader(String arg0, String arg1) {
    }

    public void addIntHeader(String arg0, int arg1) {
    }

    public boolean containsHeader(String arg0) {
        return false;
    }

    public String encodeRedirectURL(String arg0) {
        return null;
    }

    public String encodeRedirectUrl(String arg0) {
        return null;
    }

    public String encodeURL(String arg0) {
        return null;
    }

    public String encodeUrl(String arg0) {
        return null;
    }

    public void sendError(int arg0) throws IOException {
    }

    public void sendError(int arg0, String arg1) throws IOException {
    }

    public void sendRedirect(String arg0) throws IOException {
    }

    public void setDateHeader(String arg0, long arg1) {
    }

    public void setHeader(String arg0, String arg1) {
    }

    public void setIntHeader(String arg0, int arg1) {
    }

    public void setStatus(int arg0) {
    }

    public void setStatus(int arg0, String arg1) {
    }

    public void flushBuffer() throws IOException {

    }

    public int getBufferSize() {
        return 0;
    }

    public Locale getLocale() {
        return null;
    }

    public PrintWriter getWriter() throws IOException {
        return new PrintWriter(new OutputStreamWriter(outputStream, "UTF-8"));
    }

    public boolean isCommitted() {
        return false;
    }

    public void reset() {
    }

    public void resetBuffer() {
    }

    public void setBufferSize(int arg0) {
    }

    public void setContentLength(int arg0) {
    }

    public void setLocale(Locale arg0) {
    }

    /****************************************************************************************/
    /***********   Servlet Spec 3.0   *******************************************************/
    /****************************************************************************************/
    public String getHeader(String name) {
        return null;
    }

    public Collection<String> getHeaderNames() {
        return null;
    }

    public Collection<String> getHeaders(String headerName) {
        return null;
    }

    public int getStatus() {
        return 0;
    }

}
