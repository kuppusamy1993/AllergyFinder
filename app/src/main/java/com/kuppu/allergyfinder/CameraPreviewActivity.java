package com.kuppu.allergyfinder;

import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetector;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata;
import com.kuppu.allergyfinder.Viewmodel.AllergyViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Camera Preview Activity
 * control preview screen and overlays
 */
public class CameraPreviewActivity extends AppCompatActivity {

    private Camera mCamera;
    private CameraView camView;
    private OverlayView overlay;
    private double overlayScale = -1;

    private AllergyViewModel allergyViewModel;
    private List<AllergyNote>allergylist;

    int valueType;
    private interface OnBarcodeListener {
        void onIsbnDetected(FirebaseVisionBarcode barcode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Full Screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Fix orientation : portrait
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Set layout
        setContentView(R.layout.activity_camera_preview);

        // Set ui button actions
        findViewById(R.id.btn_finish_preview).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               CameraPreviewActivity.this.finish();

            }
        });

        // Initialize viewmodel
        allergyViewModel= ViewModelProviders.of(this).get(AllergyViewModel.class);

        // Initialize List
        allergylist=new ArrayList<>();

        allergyViewModel.getAllItem().observe(this, new Observer<List<AllergyNote>>() {
            @Override
            public void onChanged(List<AllergyNote> allergyNotes) {
                allergylist=allergyNotes;
            }
        });
        // Initialize Camera
        mCamera = getCameraInstance();

        // Set-up preview screen
        if(mCamera != null) {
            // Create overlay view
            overlay = new OverlayView(this);

            // Create barcode processor for ISBN
            CustomPreviewCallback camCallback = new CustomPreviewCallback(CameraView.PREVIEW_WIDTH, CameraView.PREVIEW_HEIGHT);
            camCallback.setBarcodeDetectedListener(new OnBarcodeListener() {
                @Override
                public void onIsbnDetected(FirebaseVisionBarcode barcode) {
                    overlay.setOverlay(fitOverlayRect(barcode.getBoundingBox()), barcode.getRawValue());
                    overlay.invalidate();
                }
            });

            // Create camera preview
            camView = new CameraView(this, mCamera);
            camView.setPreviewCallback(camCallback);

            // Add view to UI
            FrameLayout preview = findViewById(R.id.frm_preview);
            preview.addView(camView);
            preview.addView(overlay);
        }
    }

    private void ShowDialog(){
         final AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setMessage("This food item contain Allergy incredients");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }

    @Override
    protected void onDestroy() {
        try{
            if(mCamera != null) mCamera.release();
        }catch (Exception e){
            e.printStackTrace();
        }

        super.onDestroy();
    }

    /** Get facing back camera instance */
    public static Camera getCameraInstance()
    {
        int camId = -1;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        for (int i = 0; i < Camera.getNumberOfCameras(); ++i) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                camId = i;
                break;
            }
        }

        if(camId == -1) return null;

        Camera c=null;
        try{
            c= Camera.open(camId);
        }catch(Exception e){
            e.printStackTrace();
        }
        return c;
    }

    /** Calculate overlay scale factor */
    private Rect fitOverlayRect(Rect r) {
        if(overlayScale <= 0) {
            Camera.Size prevSize = camView.getPreviewSize();
            overlayScale = (double) overlay.getWidth()/(double)prevSize.height;
        }

        return new Rect((int)(r.left*overlayScale), (int)(r.top*overlayScale), (int)(r.right*overlayScale), (int)(r.bottom*overlayScale));
    }

    /** Post-processor for preview image streams */
    private class CustomPreviewCallback implements Camera.PreviewCallback, OnSuccessListener<List<FirebaseVisionBarcode>>, OnFailureListener {

        public void setBarcodeDetectedListener(OnBarcodeListener mBarcodeDetectedListener) {
            this.mBarcodeDetectedListener = mBarcodeDetectedListener;
        }

        // ML Kit instances
        private FirebaseVisionBarcodeDetectorOptions options;
        private FirebaseVisionBarcodeDetector detector;
        private FirebaseVisionImageMetadata metadata;

        /**
         * Event Listener for post processing
         *
         * We'll set up the detector only for EAN-13 barcode format and ISBN barcode type.
         * This OnBarcodeListener aims of notifying 'ISBN barcode is detected' to other class.
         */
        private OnBarcodeListener mBarcodeDetectedListener = null;

        /** size of input image */
        private int mImageWidth, mImageHeight;

        /**
         * Constructor
         * @param imageWidth preview image width (px)
         * @param imageHeight preview image height (px)
         */
        CustomPreviewCallback(int imageWidth, int imageHeight){
            mImageWidth = imageWidth;
            mImageHeight = imageHeight;

            options =new FirebaseVisionBarcodeDetectorOptions.Builder()
                    .setBarcodeFormats(FirebaseVisionBarcode.FORMAT_CODE_128,FirebaseVisionBarcode.FORMAT_QR_CODE,FirebaseVisionBarcode.FORMAT_ALL_FORMATS)
                    .build();

            detector = FirebaseVision.getInstance().getVisionBarcodeDetector(options);

            // build detector
            metadata = new FirebaseVisionImageMetadata.Builder()
                    .setFormat(ImageFormat.NV21)
                    .setWidth(mImageWidth)
                    .setHeight(mImageHeight)
                    .setRotation(FirebaseVisionImageMetadata.ROTATION_90)
                    .build();
        }

        /** Start detector if camera preview shows */
        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {
            try {
                detector.detectInImage(FirebaseVisionImage.fromByteArray(data, metadata))
                        .addOnSuccessListener(this)
                        .addOnFailureListener(this);
            } catch (Exception e) {
                Log.d("CameraView", "parse error");
            }
        }

        /** Barcode is detected successfully */
        @Override
        public void onSuccess(List<FirebaseVisionBarcode> barcodes) {
            // Task completed successfully
            String url="";
            for (FirebaseVisionBarcode barcode: barcodes) {

                 valueType = barcode.getValueType();
                switch (valueType) {
                    case FirebaseVisionBarcode.TYPE_WIFI:
                        String ssid = barcode.getWifi().getSsid();
                        String password = barcode.getWifi().getPassword();
                        int type = barcode.getWifi().getEncryptionType();
                        break;
                    case FirebaseVisionBarcode.TYPE_URL:
                        String title = barcode.getUrl().getTitle();
                        url = barcode.getUrl().getUrl();
                        Log.e("Barcode", "value : "+url+",,"+title);
                          mBarcodeDetectedListener.onIsbnDetected(barcode);

                        for(int i=0;i<allergylist.size();i++) {
                            String itemname = "http://" + allergylist.get(i).getName();
                            if (url.equals(itemname)) {
                                // Toast.makeText(getApplicationContext(),"This food item contain Allergy incredients",Toast.LENGTH_LONG).show();
                                Toast toast = Toast.makeText(getApplicationContext(), "This food item contain Allergy incredients", Toast.LENGTH_SHORT);
                                TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                                v.setTextColor(Color.RED);
                                toast.show();

                                //ShowDialog();

                            }
                        }
                            break;
                            case FirebaseVisionBarcode.TYPE_TEXT:
                                String data =barcode.getRawValue();
                                String databarcode=barcode.getDisplayValue();
                                mBarcodeDetectedListener.onIsbnDetected(barcode);
                                for(int i=0;i<allergylist.size();i++) {
                                    String itemname = allergylist.get(i).getName();
                                    if (data.equals(itemname)) {
                                        Toast toast = Toast.makeText(getApplicationContext(), "This food item contain Allergy incredients", Toast.LENGTH_SHORT);
                                        TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                                        v.setTextColor(Color.RED);
                                        toast.show();

                                    }
                                    if(databarcode.equals(itemname)){
                                        Toast toast = Toast.makeText(getApplicationContext(), "This food item contain Allergy incredients", Toast.LENGTH_SHORT);
                                        TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                                        v.setTextColor(Color.RED);
                                        toast.show();

                                    }

                                }
                                break;
                    case FirebaseVisionBarcode.TYPE_UNKNOWN:
                        String datas =barcode.getRawValue();
                        mBarcodeDetectedListener.onIsbnDetected(barcode);

                        for(int i=0;i<allergylist.size();i++) {
                            String itemname = allergylist.get(i).getName();
                            if (datas.equals(itemname)) {
                                Toast toast = Toast.makeText(getApplicationContext(), "This food item contain Allergy incredients", Toast.LENGTH_SHORT);
                                TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                                v.setTextColor(Color.RED);
                                toast.show();

                                //ShowDialog();

                            }
                        }
                        break;
                }

                }







            }


        /** Barcode is not recognized */
        @Override
        public void onFailure(@NonNull Exception e) {
            // Task failed with an exception
            Log.i("Barcode", "read fail");
        }
    }
}
