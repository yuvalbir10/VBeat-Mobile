package com.example.vbeat_mobile.utility;

import android.content.ContentUris;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.UUID;

public class URIUtils {
    private static final String TAG = "URIUtils";

    public static String copyAndGetPath(Uri localPath, Context context)
            throws IOException
    {
        InputStream is = null;
        OutputStream os = null;
        BufferedReader reader = null;
        byte[] tempBuffer = new byte[1024];

        try {
            // open input stream to target file
            is = context.getContentResolver().openInputStream(localPath);
            if(is == null){
                throw new IOException("unable to open input stream");
            }
            reader = new BufferedReader(new InputStreamReader(is));

            // open temporary output file
            File tmpFile;
            tmpFile = new File(getDataDir(context) + "/" + UUID.randomUUID().toString());
            tmpFile.deleteOnExit();

            // copying the new file
            os = new FileOutputStream(tmpFile);
            int length;
            while((length = is.read(tempBuffer)) != -1) {
                os.write(tempBuffer, 0, length);
            }

            // returning the path we created
            return tmpFile.getAbsolutePath();
        } catch(Exception e){
            Log.e(TAG, "unable to copy file", e);
            throw e;
        } finally {
            // cleaning up on finish
            if(reader != null) {
                reader.close();
            }
            if(is != null){
                is.close();
            }

            if(os != null){
                os.close();
            }
        }
    }

    public static String getDataDir(Context context){
        PackageManager pm = context.getPackageManager();
        String pkgName = context.getPackageName();
        try {
            PackageInfo pi = pm.getPackageInfo(pkgName, 0);
            return pi.applicationInfo.dataDir;
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    /**
     * Method for return file path of Gallery image/ Document / Video / Audio
     *
     * @param context - context of the application or class
     * @param uri - uri to get the path
     * @return          - path of the selected image file from gallery
     */
    public static String getPath(final Context context, final Uri uri)
    {
        // check here to KITKAT or new version
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri))
        {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri))
            {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type))
                {
                    return Environment.getExternalStorageDirectory() + "/"
                            + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri))
            {
                final String id = DocumentsContract.getDocumentId(uri);

                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri))
            {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;

                if ("image".equals(type))
                {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                }
                else if ("video".equals(type))
                {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                }
                else if ("audio".equals(type))
                {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] { split[1] };

                return getDataColumn(context, contentUri, selection,
                        selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme()))
        {
            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme()))
        {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context           - The context.
     * @param uri               - The Uri to query.
     * @param selection         - (Optional) Filter used in the query.
     * @param selectionArgs     - (Optional) Selection arguments used in the query.
     * @return                  - The value of the _data column, which is typically a file path.
     */
    private static String getDataColumn(Context context, Uri uri,
                                        String selection, String[] selectionArgs)
    {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = { column };

        try
        {
            cursor = context.getContentResolver().query(uri, projection,
                    selection, selectionArgs, null);

            if (cursor != null && cursor.moveToFirst())
            {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        }
        finally
        {
            if (cursor != null)
                cursor.close();
        }

        return null;
    }

    /**
     * @param uri      - The Uri to check.
     * @return         - Whether the Uri authority is ExternalStorageProvider.
     */
    private static boolean isExternalStorageDocument(Uri uri)
    {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri       - The Uri to check.
     * @return          - Whether the Uri authority is DownloadsProvider.
     */
    private static boolean isDownloadsDocument(Uri uri)
    {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri       - The Uri to check.
     * @return          - Whether the Uri authority is MediaProvider.
     */
    private static boolean isMediaDocument(Uri uri)
    {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri       - The Uri to check.
     * @return          - Whether the Uri authority is Google Photos.
     */
    private static boolean isGooglePhotosUri(Uri uri)
    {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }
}
