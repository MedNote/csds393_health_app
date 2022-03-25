package com.mednote.cwru.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.util.Log;

import androidx.core.content.FileProvider;


import com.mednote.cwru.util.helpers.ApplicationContextHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class ShareProvider {
    Context appContext;
    public ShareProvider () {
        appContext = ApplicationContextHelper.get();

    }

    public Intent ShareFiles(List<File> files, String[] emails, String header, String body) {

        ArrayList<Uri> uriList = new ArrayList<>();
        if(files != null) {
            for (File f : files) {
                if (f.exists()) {

                    // ?: use cache as shared folder to allow sharing files through FileProvider
                    // File file_in_share = copyFileToCache(f);
                    File file_in_share = f;
                    if(file_in_share == null)
                        continue;

                    // Use the FileProvider to get a content URI
                    try {
                        Uri fileUri = FileProvider.getUriForFile(
                                appContext,
                                "com.cwru.cwru.fileprovider",
                                file_in_share);

                        uriList.add(fileUri);
                    } catch (IllegalArgumentException e) {
                        Log.e("File Selector",
                                "The selected file can't be shared: " + f.toString());
                    }
                }
            }
        }
        if(uriList.size() == 0)
        {
            Log.w("StorageModule", "ShareFiles: no files to share");
            return null;
        }

        Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        // If I want it to be exclusively Gmail:
        // intent.setClassName("com.google.android.gm", "com.google.android.gm.ComposeActivityGmail");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        // set the type to 'email'
        intent.setType("vnd.android.cursor.dir/email");
        intent.putExtra(Intent.EXTRA_EMAIL, emails);
        // the mail subject and body
        intent.putExtra(Intent.EXTRA_SUBJECT, header);
        intent.putExtra(Intent.EXTRA_TEXT, body);
        // the attachment
        intent.setType("text/xml");
        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uriList);


        // To resolve: Permission Denial while sharing file with FileProvider
        // https://stackoverflow.com/questions/57689792/permission-denial-while-sharing-file-with-fileprovider
        Intent chooser = Intent.createChooser(intent, "Share File");

        List<ResolveInfo> resInfoList = appContext.getPackageManager().queryIntentActivities(chooser, PackageManager.MATCH_DEFAULT_ONLY);

        for (ResolveInfo resolveInfo : resInfoList) {
            String packageName = resolveInfo.activityInfo.packageName;
            for (Uri oneUri: uriList) {
                appContext.grantUriPermission(packageName,
                        oneUri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
        }

        return chooser;
    }

    private File copyFileToCache(File file) {
        try {
            InputStream is = new FileInputStream(file);

            File cacheDir = appContext.getCacheDir();
            File outFile = new File(cacheDir, file.getName());

            OutputStream os = new FileOutputStream(outFile.getAbsolutePath());

            byte[] buff = new byte[1024];
            int len;
            while ((len = is.read(buff)) > 0) {
                os.write(buff, 0, len);
            }
            os.flush();
            os.close();
            is.close();
            return outFile;

        } catch (IOException e) {
            // ?: should close streams properly here
            e.printStackTrace();
        }
        return null;
    }

    public void deleteFileFromCache(File file) {
    }

}
