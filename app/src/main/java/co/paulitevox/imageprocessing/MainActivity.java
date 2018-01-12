package co.paulitevox.imageprocessing;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.kittinunf.fuel.Fuel;
import com.github.kittinunf.fuel.core.FuelError;
import com.github.kittinunf.fuel.core.Handler;
import com.github.kittinunf.fuel.core.Request;
import com.github.kittinunf.fuel.core.Response;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {

    public final static int MY_REQUEST_CODE = 1;
    private static final int PICK_IMAGE_REQUEST = 234;
    HttpURLConnection httpConnection;
    private TextView responsefromServer;
    private String requestCode1;
    String body;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        responsefromServer=(TextView) findViewById(R.id.resultsText);
    }
    /*

    public void takePicture(View view) {
        requestCode1="REQUEST_CAMERA";
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, MY_REQUEST_CODE);
    }

    public void chooseGallery(View view)
    {
        requestCode1="SELECT_FILE";
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode1 == "SELECT_FILE")
                onSelectFromGalleryResult(data);
            else if (requestCode1 == "REQUEST_CAMERA")
                onCaptureImageResult(data);
        }
    }
    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {
        Bitmap picture=null;
        if (data != null) {
            try {
                picture = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
                ((ImageView) findViewById(R.id.previewImage)).setImageBitmap(picture);
                ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                picture.compress(Bitmap.CompressFormat.JPEG, 90, byteStream);

                String base64Data = Base64.encodeToString(byteStream.toByteArray(),
                        Base64.URL_SAFE);

                new FetchResults().execute(base64Data);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
    private void onCaptureImageResult(Intent data) {

        if(data!=null) {

            Bitmap picture = (Bitmap) data.getExtras().get("data");
            ((ImageView) findViewById(R.id.previewImage))
                    .setImageBitmap(picture);
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            picture.compress(Bitmap.CompressFormat.JPEG, 90, byteStream);

            String base64Data = Base64.encodeToString(byteStream.toByteArray(),
                    Base64.URL_SAFE);

            new FetchResults().execute(base64Data);
        }
    }

    public class FetchResults extends AsyncTask<String, Void, String> {

        // COMPLETED (6) Override the doInBackground method to perform your network requests
        @Override
        protected String doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }

            String base64Data = params[0];
            String requestURL =
                    "https://vision.googleapis.com/v1/images:annotate?key=" +
                            getResources().getString(R.string.mykey);
            // Create an array containing
            // the LABEL_DETECTION feature
            JSONArray features = new JSONArray();
            JSONObject feature = new JSONObject();
            try {

                feature.put("type", "LABEL_DETECTION");
                feature.put("type","FACE_DETECTION");
                feature.put("type","WEB_DETECTION");
                features.put(feature);


                JSONObject imageContent = new JSONObject();
                imageContent.put("content", base64Data);

                Log.e("Exception: ", "content");


                // Put the array and object into a single request
                // and then put the request into an array of requests
                JSONArray requests = new JSONArray();
                JSONObject request = new JSONObject();

                request.put("image", imageContent);
                request.put("features", features);

                Log.e("Exception: ", "image and features");
                requests.put(request);
                JSONObject postData = new JSONObject();

                postData.put("requests", requests);
                body = postData.toString();
            } catch (JSONException e) {
                Log.e("Exception: ", "requests");
            }

            URL serverUrl = null;

            try {
                serverUrl = new URL(requestURL);
                URLConnection urlConnection = null;
                urlConnection = serverUrl.openConnection();
                httpConnection = (HttpURLConnection) urlConnection;
                httpConnection.setRequestMethod("POST");
                httpConnection.setRequestProperty("Content-Type", "application/json");
                httpConnection.setDoOutput(true);
                BufferedWriter httpRequestBodyWriter = null;
                httpRequestBodyWriter = new BufferedWriter(new
                        OutputStreamWriter(httpConnection.getOutputStream()));
                httpRequestBodyWriter.write(body);
                httpRequestBodyWriter.close();

                //String response = httpConnection.getResponseMessage();

                if (httpConnection.getInputStream() == null) {
                    System.out.println("No stream");
                    return null;
                }

                Scanner httpResponseScanner = new Scanner(httpConnection.getInputStream());
                String resp = "";
                while (httpResponseScanner.hasNext()) {
                    String line = httpResponseScanner.nextLine();
                    resp += line;
                    Log.e("Line: ",line);
                }
                httpResponseScanner.close();
                return resp;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }

        }

        @Override
        protected void onPostExecute(String response) {
            if (response != null) {
                Log.e("Response is:", response);
                responsefromServer.setText(response);

            }
        }
    }
    */
}

