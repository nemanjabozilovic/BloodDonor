package com.example.blooddonor.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.webkit.MimeTypeMap;

import com.example.blooddonor.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public class ImageUploadHelper {

    private static final String IMAGE_DIR = "/images/";
    private static final long MAX_IMAGE_SIZE_MB = 2 * 1024 * 1024;

    public static String saveImageToAppStorage(Context context, Uri imageUri, String existingFilePath) throws Exception {
        Bitmap bitmap = null;
        FileOutputStream fos = null;
        try {
            if (!validateImage(context, imageUri)) {
                throw new IOException(String.valueOf(R.string.error_invalid_image));
            }

            if (existingFilePath != null && !existingFilePath.isEmpty()) {
                deleteImage(existingFilePath);
            }

            bitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.getContentResolver(), imageUri));

            String fileName = generateGUID() + ".jpg";
            File storageDir = new File(context.getFilesDir() + IMAGE_DIR);

            if (!storageDir.exists() && !storageDir.mkdirs()) {
                throw new IOException(String.valueOf(R.string.error_creating_directory));
            }

            File imageFile = new File(storageDir, fileName);
            fos = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fos);
            fos.flush();
            return imageFile.getAbsolutePath();
        } catch (Exception e) {
            throw new Exception(String.valueOf(R.string.error_saving_image));
        } finally {
            if (fos != null) fos.close();
            if (bitmap != null) bitmap.recycle();
        }
    }

    public static boolean validateImage(Context context, Uri imageUri) throws Exception {
        String fileExtension = getFileExtension(context, imageUri);
        if (!isValidImageFormat(fileExtension)) return false;
        long fileSize = getFileSize(context, imageUri);
        return fileSize <= MAX_IMAGE_SIZE_MB;
    }

    public static String generateGUID() {
        return UUID.randomUUID().toString();
    }

    public static boolean deleteImage(String filePath) {
        File file = new File(filePath);
        return file.exists() && file.delete();
    }

    public static Bitmap getImageFromPath(String imagePath) throws Exception {
        File imageFile = new File(imagePath);
        if (!imageFile.exists()) {
            throw new IOException(R.string.error_image_not_found + imagePath);
        }
        try (FileInputStream fis = new FileInputStream(imageFile)) {
            return BitmapFactory.decodeStream(fis);
        } catch (Exception e) {
            throw new Exception(e.getLocalizedMessage());
        }
    }

    private static String getFileExtension(Context context, Uri uri) {
        String mimeType = context.getContentResolver().getType(uri);
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType);
    }

    private static boolean isValidImageFormat(String fileExtension) {
        return "jpg".equalsIgnoreCase(fileExtension)
                || "jpeg".equalsIgnoreCase(fileExtension)
                || "png".equalsIgnoreCase(fileExtension);
    }

    private static long getFileSize(Context context, Uri uri) throws Exception {
        try (InputStream inputStream = context.getContentResolver().openInputStream(uri)) {
            if (inputStream == null) {
                throw new IOException(String.valueOf(R.string.failed_to_open_input_stream) + uri);
            }
            return inputStream.available();
        }
    }
}