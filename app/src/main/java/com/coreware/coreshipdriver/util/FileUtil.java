package com.coreware.coreshipdriver.util;

import android.net.Uri;
import android.webkit.MimeTypeMap;

import java.io.File;

public final class FileUtil {
    private static final String LOG_TAG = FileUtil.class.getName();

    private static final String FILE_SEPARATOR = "file.separator";
    private static final String FILE_DIRECTORY = "files";
    private static final String IMAGE_DIRECTORY = "iamges";
    private static final String TEMP_DIRECTORY = "temp";

    private FileUtil() {
        // Cannot instantiate class
    }

    public static String getMimeType(File file) {
        String mimeType = null;

        String fileUri = Uri.fromFile(file).toString();
        String fileExtension = MimeTypeMap.getFileExtensionFromUrl(fileUri);

        if (fileExtension == null || fileExtension.length() == 0) {
            int fileExtensionIndex = fileUri.lastIndexOf(".");
            fileExtension = fileUri.substring(fileExtensionIndex+1);
        }

        fileExtension = fileExtension.toLowerCase();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        mimeType = mimeTypeMap.getMimeTypeFromExtension(fileExtension);

        return mimeType;
    }
}
