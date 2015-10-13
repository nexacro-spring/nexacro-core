package com.nexacro.spring.data;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletOutputStream;

public class FirstRowTrackableServletOutputStream extends ServletOutputStream {

    ByteArrayOutputStream output = new ByteArrayOutputStream();
    List<byte[]> flushedByteArrayList = new ArrayList<byte[]>();

    Object syncronizeObject = new Object();
    byte[] resultData = null;
    boolean isClose = false;

    @Override
    public void write(int b) throws IOException {
        write(new byte[] { (byte) b });
    }

    public void close() throws IOException {

        // ByteArrayOutputStream 은 close 시 아무런 영향이 없다.
        // 메모리 대상이기 때문에 Garbage Collector 에 의해 소멸된다.
        output.close();
        isClose = true;

    }

    public boolean equals(Object obj) {
        return output.equals(obj);
    }

    public void flush() throws IOException {
        synchronized (syncronizeObject) {

            resultData = output.toByteArray();
            output.reset();

            flushedByteArrayList.add(resultData);

            if (isClose) {
                throw new IOException("connection reset by peer");
            }
        }
    }

    public List<byte[]> getFlushedByteList() {
        return flushedByteArrayList;
    }

    public int hashCode() {
        return output.hashCode();
    }

    public void reset() {
        output.reset();
    }

    public int size() {
        return output.size();
    }

    public byte[] toByteArray() {
        return output.toByteArray();
    }

    public String toString() {
        return output.toString();
    }

    @SuppressWarnings("deprecation")
    public String toString(int hibyte) {
        return output.toString(hibyte);
    }

    public String toString(String enc) throws UnsupportedEncodingException {
        return output.toString(enc);
    }

    public void write(byte[] b, int off, int len) {
        output.write(b, off, len);
    }

    public void write(byte[] b) throws IOException {
        output.write(b);
    }

    public void writeTo(OutputStream out) throws IOException {
        output.writeTo(out);
    }

    public OutputStream getOutputStream() {
        return this.output;
    }

    public byte[] getResultData() {
        return resultData;
    }

    public void setResultData(byte[] resultData) {
        this.resultData = resultData;
    }
}
