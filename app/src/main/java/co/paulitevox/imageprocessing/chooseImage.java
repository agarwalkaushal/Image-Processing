package co.paulitevox.imageprocessing;

import android.animation.Animator;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static android.app.Activity.RESULT_OK;

/**
 * Created by HP on 13-12-2017.
 */


public class chooseImage extends Fragment {
    private final int PICK_IMAGE_REQUEST = 234;
    private LinearLayout before;
    private LinearLayout after;
    private LinearLayout root;
    private RelativeLayout open;
    private FloatingActionButton done;
    private FloatingActionButton chooseanother;
    private FloatingActionButton edit;
    private FloatingActionButton done1;
    private FloatingActionButton choose;
    private FloatingActionButton crop;
    private ImageView imageView;
    private ImageView imageViewBlur;
    private Animation anim;
    private Uri filePath;
    private Button choosebefore;
    private Uri file;
    private Bitmap bitmap ;
    final int PIC_CROP = 1;
    private String filename = "bitmap.png";

    private final Runnable revealAnimationRunnable = new Runnable() {
        @Override
        public void run() {
            int cx = before.getLeft();
            int cy = before.getBottom();

            int finalRadius = Math.max(root.getWidth(), root.getHeight());
            Animator animator = ViewAnimationUtils.createCircularReveal(after, cx, cy, 0, finalRadius);
            animator.setDuration(300);
            final Handler handler = new Handler();

            animator.start();

            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    before.setVisibility(View.GONE);
                    after.setVisibility(View.VISIBLE);
                    after.startAnimation(anim);
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });

        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_choose, container, false);
        anim = AnimationUtils.loadAnimation(getContext(), R.anim.alpha_anim);
        open = (RelativeLayout) view.findViewById(R.id.open);
        before = (LinearLayout) view.findViewById(R.id.before);
        after = (LinearLayout) view.findViewById(R.id.after);
        root = (LinearLayout) view.findViewById(R.id.root);
        done = (FloatingActionButton) view.findViewById(R.id.done);
        done1 = (FloatingActionButton) view.findViewById(R.id.done1);
        chooseanother = (FloatingActionButton) view.findViewById(R.id.chooseanother);
        choosebefore = (Button) view.findViewById(R.id.choosebefore);
        edit = (FloatingActionButton) view.findViewById(R.id.edit);
        choose = (FloatingActionButton) view.findViewById(R.id.choose);
        crop = (FloatingActionButton) view.findViewById(R.id.crop);
        imageView = (ImageView) view.findViewById(R.id.chooseFromGallery);
        imageViewBlur = (ImageView) view.findViewById(R.id.chooseFromGalleryBlur);
        final FlipAnimation flipAnimation = new FlipAnimation(before, after);
        after.setVisibility(View.GONE);
        done.setVisibility(View.INVISIBLE);
        edit.setVisibility(View.INVISIBLE);
        choose.setVisibility(View.INVISIBLE);
        Intent intent = getActivity().getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        if (Intent.ACTION_SEND.equals(action) && type != null && type.startsWith("image/")) {

            handleSendImage(intent);
        }
        choosebefore.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                showFileChooser();


                                            }
                                        }
        );
        choose.setOnClickListener(new View.OnClickListener() {
                                      @Override
                                      public void onClick(View view) {
                                          showFileChooser();
                                      }
                                  }
        );
        chooseanother.setOnClickListener(new View.OnClickListener() {
                                             @Override
                                             public void onClick(View view) {
                                                 showFileChooser();
                                             }
                                         }
        );
        crop.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                            if(filePath==null) {
                                                Toast.makeText(getActivity(), "Please load a image...", Toast.LENGTH_SHORT);
                                            }
                                            else {
                                                CropImage.activity(filePath)
                                                        .start(getContext(), chooseImage.this);
                                            }
                                    }
                                }
        );
        edit.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        view.post(revealAnimationRunnable);
                                        //root.startAnimation(flipAnimation);
                                    }
                                }
        );
        done.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        try {

                                            Intent in1 = new Intent(getActivity(), selectOp.class);
                                            in1.putExtra("image", filename);
                                            startActivity(in1);
                                            getActivity().overridePendingTransition( R.anim.slide_in_up, R.anim.slide_out_up );
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }

                                    }
                                }
        );
        done1.setOnClickListener(new View.OnClickListener() {
                                     @Override
                                     public void onClick(View view) {
                                         try {
                                             //Write file

                                             //FileOutputStream stream = getActivity().openFileOutput(filename, Context.MODE_PRIVATE);
                                            // bitmap.compress(Bitmap.CompressFormat.JPEG, 20, stream);

                                             //Cleanup
                                           ///  stream.close();
                                            // bitmap.recycle();

                                             //Pop intent
                                             Intent in1 = new Intent(getActivity(), selectOp.class);
                                             in1.putExtra("image", filename);
                                             startActivity(in1);
                                             getActivity().overridePendingTransition( R.anim.slide_in_up, R.anim.slide_out_up );
                                            // getActivity().finish();
                                         } catch (Exception e) {
                                             e.printStackTrace();
                                         }
                                     }
                                 }
        );
        return view;
    }
    private void decode()
    {
        try {
        //Write file
        FileOutputStream stream = getActivity().openFileOutput(filename, Context.MODE_PRIVATE);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 30, stream);

        //Cleanup
        stream.close();
    } catch (Exception e) {
        e.printStackTrace();
    }
    }
    private void handleSendImage(Intent intent) {

        Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);

        if (imageUri != null) {
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
                decode();
                imageView.setImageURI(imageUri);
                imageViewBlur.setImageURI(imageUri);
                open.setVisibility(View.GONE);
                choose.setVisibility(View.VISIBLE);
                done.setVisibility(View.VISIBLE);
                edit.setVisibility(View.VISIBLE);
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }

        } else{

            Toast.makeText(getActivity(), "Error occured, URI is invalid", Toast.LENGTH_LONG).show();

        }
    }
/*
    private void performCrop(Uri picUri) {
        try {
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            // indicate image type and Uri
            cropIntent.setDataAndType(picUri, "image/*");
            // set crop properties here
            cropIntent.putExtra("crop", true);
            // indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            // indicate output X and Y
            cropIntent.putExtra("outputX", 128);
            cropIntent.putExtra("outputY", 128);
            // retrieve data on return
            cropIntent.putExtra("return-data", true);
            // start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, PIC_CROP);
        }
        // respond to users whose devices do not support the crop action
        catch (ActivityNotFoundException anfe) {
            // display an error message
            String errorMessage = "Whoops - your device doesn't support the crop action!";
            Toast toast = Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }
    private void viewMenu() {

        int x = before.getLeft();
        int y = before.getBottom();

        int startRadius = 0;
        int endRadius = (int) Math.hypot(root.getWidth(), root.getHeight());

        Animator anim = ViewAnimationUtils.createCircularReveal(after, x, y, startRadius, endRadius);
        after.setVisibility(View.VISIBLE);
        before.setVisibility(View.GONE);

        anim.start();
    }
    */

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);

    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

        @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            int dataSize=0;
            filePath = data.getData();
            try {
                InputStream fileInputStream=getActivity().getApplicationContext().getContentResolver().openInputStream(filePath);
                dataSize = fileInputStream.available();
                if(dataSize>10485760)
                {
                    Toast toast = Toast.makeText(getActivity(), "File too large!!!", Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);
                imageViewBlur.setImageBitmap(bitmap);
                decode();
                open.setVisibility(View.GONE);
                choose.setVisibility(View.VISIBLE);
                done.setVisibility(View.VISIBLE);
                edit.setVisibility(View.VISIBLE);
                return;

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {
                    Uri resultUri = result.getUri();
                    try {
                        bitmap = BitmapFactory.decodeStream(getActivity().
                                getContentResolver().openInputStream(resultUri));
                        decode();
                        imageView.setImageBitmap(bitmap);
                        imageViewBlur.setImageBitmap(bitmap);
                        return;

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                }
            }


    }

}
