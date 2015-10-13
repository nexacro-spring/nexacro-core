package com.nexacro.spring.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class FileUtils {

    public static void copyFile(File sourceFile, File destFile) throws IOException {
        long writedSize = 0;
        long sourceSize = sourceFile.length();
        if (!destFile.exists()) {
            destFile.createNewFile();
        }

        FileChannel source = null;
        FileChannel destination = null;
        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            while (writedSize < sourceSize) {
                writedSize += destination.transferFrom(source, writedSize, sourceSize - writedSize);
            }
        } finally {
            if (source != null) {
                source.close();
            }
            if (destination != null) {
                destination.close();
            }
        }
    }

    public static boolean isFileUsed(File file) {
        
        if(file == null) { return false; }
        
        if(file.isHidden()) {
            return false;
        }
        else if(!file.exists()) {
            return false;
        }
        else if(!file.canRead()) {
            return false;
        }
        
        return true;
        
    }
    
}
