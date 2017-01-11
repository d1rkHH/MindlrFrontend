package de.gamedots.mindlr.mindlrfrontend.model;

import android.net.Uri;

import org.json.JSONObject;

public class ImageUploadResult {
    public boolean successful;
    public String link;
    public JSONObject content;
    public Uri draftUri;

    public ImageUploadResult(boolean successful, String link, JSONObject content, Uri draftUri) {
        this.successful = successful;
        this.link = link;
        this.content = content;
        this.draftUri = draftUri;
    }
}
