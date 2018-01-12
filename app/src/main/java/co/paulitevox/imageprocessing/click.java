package co.paulitevox.imageprocessing;

/**
 * Created by HP on 13-12-2017.
 */
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import java.sql.Timestamp;
import java.util.List;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.ErrorCallback;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.media.Image;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import static android.graphics.Bitmap.CompressFormat.JPEG;
import static android.graphics.Bitmap.CompressFormat.PNG;

public class click extends Fragment implements Callback,OnClickListener {

    private SurfaceView surfaceView;
    private ImageView imageView;
    private SurfaceHolder surfaceHolder;
    private Camera camera;
    private ImageButton flipCamera;
    private ImageButton flashCameraButton;
    private FloatingActionButton captureImage;
    private FloatingActionButton done2;
    private FloatingActionButton cancel;
    private int cameraId;
    private boolean flashmode = false;
    private int rotation;
    private Bitmap rotatedBitmap ;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_click, container, false);
        cameraId = CameraInfo.CAMERA_FACING_BACK;
        flipCamera = (ImageButton) view.findViewById(R.id.flipCamera);
        flashCameraButton = (ImageButton) view.findViewById(R.id.flash);
        captureImage = (FloatingActionButton) view.findViewById(R.id.captureImage);
        done2 = (FloatingActionButton) view.findViewById(R.id.done2);
        cancel = (FloatingActionButton) view.findViewById(R.id.discard);
        surfaceView = (SurfaceView) view.findViewById(R.id.surfaceView);
        imageView = (ImageView) view.findViewById(R.id.camera_image_view);


        imageView.setVisibility(View.GONE);
        done2.setVisibility(View.INVISIBLE);
        cancel.setVisibility(View.INVISIBLE);


        flipCamera.setOnClickListener(this);
        captureImage.setOnClickListener(this);
        flashCameraButton.setOnClickListener(this);
        done2.setOnClickListener(this);
        cancel.setOnClickListener(this);

        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED )
        {
            surfaceHolder = surfaceView.getHolder();
            surfaceHolder.addCallback(this);
        if (Camera.getNumberOfCameras() > 1) {
            flipCamera.setVisibility(View.VISIBLE);
        }
        if (!getActivity().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA_FLASH)) {
            flashCameraButton.setVisibility(View.GONE);
        }
        }
        else
        {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 0);
            surfaceHolder = surfaceView.getHolder();
            surfaceHolder.addCallback(this);
        }
        return view;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!openCamera(CameraInfo.CAMERA_FACING_BACK)) {
            //alertCameraDialog();
            captureImage.performClick();
            cancel.performClick();
            return;
        }

    }

    private boolean openCamera(int id) {
        boolean result = false;
        cameraId = id;
        releaseCamera();
        try {
            camera = Camera.open(cameraId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (camera != null) {
            try {
                setUpCamera(camera);
                camera.setErrorCallback(new ErrorCallback() {

                    @Override
                    public void onError(int error, Camera camera) {

                    }
                });
                camera.setPreviewDisplay(surfaceHolder);
                camera.startPreview();
                result = true;
            } catch (IOException e) {
                e.printStackTrace();
                result = false;
                releaseCamera();
            }
        }
        return result;
    }

    private void setUpCamera(Camera c) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        rotation = getActivity().getWindowManager().getDefaultDisplay().getRotation();
        int degree = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degree = 0;
                break;
            case Surface.ROTATION_90:
                degree = 90;
                break;
            case Surface.ROTATION_180:
                degree = 180;
                break;
            case Surface.ROTATION_270:
                degree = 270;
                break;

            default:
                break;
        }
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            // frontFacing
            rotation = (info.orientation + degree) % 330;
            rotation = (360 - rotation) % 360;
        } else {
            // Back-facing
            rotation = (info.orientation - degree + 360) % 360;
        }

        c.setDisplayOrientation(rotation);
        Parameters params = c.getParameters();

        showFlashButton(params);

        List<String> focusModes = params.getSupportedFlashModes();
        if (focusModes != null) {
            if (focusModes
                    .contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                params.setFlashMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            }
        }

        params.setRotation(rotation);
    }

    private void showFlashButton(Parameters params) {
        boolean showFlash = (getActivity().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA_FLASH) && params.getFlashMode() != null)
                && params.getSupportedFlashModes() != null
                && params.getSupportedFocusModes().size() > 1;

        flashCameraButton.setVisibility(showFlash ? View.VISIBLE
                : View.INVISIBLE);
        flashCameraButton.setImageResource(showFlash ? R.drawable.ic_flash_on_white_18dp : R.drawable.ic_flash_off_white_18dp);
    }

    private void releaseCamera() {
        try {
            if (camera != null) {
                camera.setPreviewCallback(null);
                camera.setErrorCallback(null);
                camera.stopPreview();
                camera.release();
                camera = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("error", e.toString());
            camera = null;
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.flash:
                flashOnButton();
                break;
            case R.id.flipCamera:
                flipCamera();
                break;
            case R.id.captureImage:
                surfaceView.setVisibility(View.GONE);
                imageView.setVisibility(View.VISIBLE);
                flipCamera.setVisibility(View.INVISIBLE);
                flashCameraButton.setVisibility(View.GONE);
                cancel.setVisibility(View.VISIBLE);
                done2.setVisibility(View.VISIBLE);
                captureImage.setVisibility(View.INVISIBLE);
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_GRANTED && camera !=null)
                    takeImage();
                else
                    return;
                break;
            case R.id.discard:
                surfaceView.setVisibility(View.VISIBLE);
                imageView.setVisibility(View.GONE);
                flipCamera.setVisibility(View.VISIBLE);
                flashCameraButton.setVisibility(View.VISIBLE);
                cancel.setVisibility(View.INVISIBLE);
                done2.setVisibility(View.INVISIBLE);
                captureImage.setVisibility(View.VISIBLE);
                break;
            case R.id.done2:
                Intent intent = new Intent(getActivity(), selectOp.class);
                intent.putExtra("BitmapImage", rotatedBitmap);
                flashOffButton();
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    private void takeImage() {
        Camera.Parameters params=camera.getParameters();
        params.setPictureFormat(ImageFormat.JPEG);
        params.setJpegQuality(100);

        camera.takePicture(new Camera.ShutterCallback() {
            @Override
            public void onShutter() {

            }
        }, new PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {

            }
        }, new PictureCallback() {

            private File imageFile;

            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                try {
                    // convert byte array into bitmap
                    Bitmap loadedImage = null;

                    loadedImage = BitmapFactory.decodeByteArray(data, 0,
                            data.length);
                    loadedImage=loadedImage.createBitmap(loadedImage);

                    Matrix rotateMatrix = new Matrix();
                    rotateMatrix.postRotate(rotation);
                    rotatedBitmap = Bitmap.createBitmap(loadedImage, 0, 0,
                            loadedImage.getWidth(), loadedImage.getHeight(),
                            rotateMatrix, false);
                    imageView.setImageBitmap(rotatedBitmap);

                    String state = Environment.getExternalStorageState();
                    File folder = null;
                    if (state.contains(Environment.MEDIA_MOUNTED)) {
                        folder = new File(Environment
                                .getExternalStorageDirectory() + "/ImageProcessing");
                    } else {
                        folder = new File(Environment
                                .getExternalStorageDirectory() + "/ImageProcessing");
                    }

                    boolean success = true;
                    if (!folder.exists()) {
                        success = folder.mkdirs();
                    }
                    if (success) {
                        java.util.Date date = new java.util.Date();
                        imageFile = new File(folder.getAbsolutePath()
                                + File.separator
                                + new Timestamp(date.getTime()).toString()
                                + "Image.jpg");

                        imageFile.createNewFile();
                    } else {
                        Toast.makeText(getActivity(), "Image Not saved",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    ByteArrayOutputStream ostream = new ByteArrayOutputStream();

                    /* save image into gallery
                    rotatedBitmap.compress(JPEG, 10000, ostream);

                    FileOutputStream fout = new FileOutputStream(imageFile);

                    fout.write(ostream.toByteArray());
                    fout.close();
                    ContentValues values = new ContentValues();

                    values.put(Images.Media.DATE_TAKEN,
                            System.currentTimeMillis());
                    values.put(Images.Media.MIME_TYPE, "image/jpeg");
                    values.put(MediaStore.MediaColumns.DATA,
                            imageFile.getAbsolutePath());

                    getActivity().getContentResolver().insert(
                            Images.Media.EXTERNAL_CONTENT_URI, values);
                            */
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private void flipCamera() {
        int id = (cameraId == CameraInfo.CAMERA_FACING_BACK ? CameraInfo.CAMERA_FACING_FRONT
                : CameraInfo.CAMERA_FACING_BACK);
        if (!openCamera(id)) {
            alertCameraDialog();
        }
    }

    private void alertCameraDialog() {
        AlertDialog.Builder dialog = createAlert(getActivity(),
                "Camera info", "error to open camera");
        dialog.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();

            }
        });

        dialog.show();
    }

    private Builder createAlert(Context context, String title, String message) {

        AlertDialog.Builder dialog = new AlertDialog.Builder(
                new ContextThemeWrapper(context,
                        android.R.style.Theme_Holo_Light_Dialog));
        //dialog.setIcon(R.drawable.ic_launcher);
        if (title != null)
            dialog.setTitle(title);
        else
            dialog.setTitle("Information");
        dialog.setMessage(message);
        dialog.setCancelable(false);
        return dialog;

    }

    private void flashOnButton() {
        if (camera != null) {
            try {
                Parameters param = camera.getParameters();
                param.setFlashMode(!flashmode ? Parameters.FLASH_MODE_TORCH
                        : Parameters.FLASH_MODE_OFF);
                camera.setParameters(param);
                flashmode = !flashmode;
            } catch (Exception e) {
                // TODO: handle exception
            }

        }
    }
    private void flashOffButton() {
        if (camera != null) {
            try {
                Parameters param = camera.getParameters();
                param.setFlashMode(!flashmode ? Parameters.FLASH_MODE_OFF
                        : Parameters.FLASH_MODE_OFF);
                camera.setParameters(param);
                flashmode = !flashmode;
            } catch (Exception e) {
                // TODO: handle exception
            }

        }
    }
}

