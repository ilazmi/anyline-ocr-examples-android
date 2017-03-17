package io.anyline.examples.ocr;

import android.graphics.PointF;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import java.util.List;

import at.nineyards.anyline.camera.AnylineViewConfig;
import at.nineyards.anyline.modules.ocr.AnylineOcrConfig;
import at.nineyards.anyline.modules.ocr.AnylineOcrResult;
import at.nineyards.anyline.modules.ocr.AnylineOcrResultListener;
import at.nineyards.anyline.modules.ocr.AnylineOcrScanView;
import io.anyline.examples.R;
import io.anyline.examples.SettingsFragment;
import io.anyline.examples.ocr.result.RedBullResultView;

public class ScanRedBullCodeActivity extends AppCompatActivity {

    private static final String TAG = ScanRedBullCodeActivity.class.getSimpleName();
    private AnylineOcrScanView scanView;
    private RedBullResultView redBullResultView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Set the flag to keep the screen on (otherwise the screen may go dark during scanning)
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_anyline_ocr);

        addRedBullResultView();

        String lic = getString(R.string.anyline_license_key);
        scanView = (AnylineOcrScanView) findViewById(R.id.scan_view);

        scanView.copyTrainedData("tessdata/rbf_jan2015_v2.traineddata", "cdb7f35f1af00030178deefc01722f65");

        // see ScanScrabbleActivity for a more detailed description
        AnylineOcrConfig anylineOcrConfig = new AnylineOcrConfig();
        anylineOcrConfig.setTesseractLanguages("rbf_jan2015_v2");
        anylineOcrConfig.setCharWhitelist("2346789ABCDEFGHKLMNPQRTUVWXYZ");
        anylineOcrConfig.setMinCharHeight(15);
        anylineOcrConfig.setMaxCharHeight(30);
        anylineOcrConfig.setMinConfidence(75);
        anylineOcrConfig.setValidationRegex("^[0-9A-Z]{4}\n[0-9A-Z]{4}");
        anylineOcrConfig.setScanMode(AnylineOcrConfig.ScanMode.GRID);
        anylineOcrConfig.setCharCountX(4);
        anylineOcrConfig.setCharCountY(2);
        anylineOcrConfig.setCharPaddingXFactor(0.3);
        anylineOcrConfig.setCharPaddingYFactor(0.5);
        anylineOcrConfig.setIsBrightTextOnDark(true);
        scanView.setAnylineOcrConfig(anylineOcrConfig);

        scanView.setConfig(new AnylineViewConfig(this, "rb_view_config.json"));

        scanView.initAnyline(lic, new AnylineOcrResultListener() {
            @Override
            public void onResult(AnylineOcrResult anylineOcrResult) {
                redBullResultView.setResult(anylineOcrResult.getResult());
                redBullResultView.setVisibility(View.VISIBLE);
            }
        });

        // disable the reporting if set to off in preferences
        scanView.setReportingEnabled(PreferenceManager.getDefaultSharedPreferences(this).getBoolean(SettingsFragment
                .KEY_PREF_REPORTING_ON, true));
        redBullResultView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redBullResultView.setVisibility(View.INVISIBLE);
                if (!scanView.isRunning()) {
                    scanView.startScanning();
                }
            }
        });
    }

    private void addRedBullResultView() {
        RelativeLayout mainLayout = (RelativeLayout) findViewById(R.id.main_layout);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        params.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);

        redBullResultView = new RedBullResultView(this);
        redBullResultView.setVisibility(View.INVISIBLE);

        mainLayout.addView(redBullResultView, params);
    }

    @Override
    protected void onResume() {
        super.onResume();

        scanView.startScanning();
    }

    @Override
    protected void onPause() {
        super.onPause();

        scanView.cancelScanning();
        scanView.releaseCameraInBackground();
    }

    @Override
    public void onBackPressed() {
        if (redBullResultView.getVisibility() == View.VISIBLE) {
            redBullResultView.setVisibility(View.INVISIBLE);
            if (!scanView.isRunning()) {
                scanView.startScanning();
            }
        } else {
            super.onBackPressed();
        }

    }

}
