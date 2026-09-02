package com.blockvoice.app;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends Activity {

    private WebView webView;
    private PermissionRequest pendingRequest;

    private static final int MIC_REQUEST = 100;

    private static final String BLOCKVOICE_URL =
            "https://hanifekorkmaz1315-arch.github.io/BlockVoice/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        webView = new WebView(this);
        setContentView(webView);

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setMediaPlaybackRequiresUserGesture(false);

        webView.setWebViewClient(new WebViewClient());

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onPermissionRequest(PermissionRequest request) {

                if (!"https".equals(request.getOrigin().getScheme())
                        || !"hanifekorkmaz1315-arch.github.io"
                        .equals(request.getOrigin().getHost())) {
                    request.deny();
                    return;
                }

                boolean audioRequested = false;

                for (String resource : request.getResources()) {
                    if (PermissionRequest.RESOURCE_AUDIO_CAPTURE
                            .equals(resource)) {
                        audioRequested = true;
                    }
                }

                if (!audioRequested) {
                    request.deny();
                    return;
                }

                if (checkSelfPermission(
                        Manifest.permission.RECORD_AUDIO)
                        == PackageManager.PERMISSION_GRANTED) {

                    request.grant(new String[]{
                            PermissionRequest.RESOURCE_AUDIO_CAPTURE
                    });

                } else {

                    pendingRequest = request;

                    requestPermissions(
                            new String[]{
                                    Manifest.permission.RECORD_AUDIO
                            },
                            MIC_REQUEST
                    );
                }
            }
        });

        webView.loadUrl(BLOCKVOICE_URL);
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            String[] permissions,
            int[] grantResults) {

        super.onRequestPermissionsResult(
                requestCode,
                permissions,
                grantResults
        );

        if (requestCode == MIC_REQUEST
                && pendingRequest != null) {

            if (grantResults.length > 0
                    && grantResults[0]
                    == PackageManager.PERMISSION_GRANTED) {

                pendingRequest.grant(new String[]{
                        PermissionRequest.RESOURCE_AUDIO_CAPTURE
                });

            } else {
                pendingRequest.deny();
            }

            pendingRequest = null;
        }
    }

    @Override
    public void onBackPressed() {

        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
