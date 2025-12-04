package com.example.ftp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.DocumentsContract;

/**
 * Android-specific utilities for SAF and storage handling.
 */
public class AndroidUtils {

    /**
     * Launch SAF folder picker intent.
     */
    public static Intent requestSAFRootFolder() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        intent.addFlags(
            Intent.FLAG_GRANT_READ_URI_PERMISSION |
            Intent.FLAG_GRANT_WRITE_URI_PERMISSION |
            Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
        );
        return intent;
    }

    /**
     * Take persistable URI permission for SAF folder.
     */
    public static void takePersistablePermission(Activity activity, Uri treeUri) {
        activity.getContentResolver().takePersistableUriPermission(
            treeUri,
            Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION
        );
    }

    /**
     * Optional helper to build a Document URI for a file/folder.
     */
    public static Uri buildDocumentUri(String documentId) {
        return DocumentsContract.buildDocumentUri(
            "com.android.externalstorage.documents", documentId
        );
    }
}