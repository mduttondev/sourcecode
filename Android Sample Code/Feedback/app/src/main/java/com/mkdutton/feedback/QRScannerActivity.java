package com.mkdutton.feedback;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;


public class QRScannerActivity extends Activity implements ZBarScannerView.ResultHandler{

    private ZBarScannerView mScannerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mScannerView = new ZBarScannerView(this);
        setContentView(mScannerView);
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result rawResult) {

        Intent result = new Intent();
        result.putExtra(LvFeedbackActivity.EXTRA_SCAN_CONTENTS, rawResult.getContents());
        setResult(RESULT_OK, result);
        finish();

    }
}
