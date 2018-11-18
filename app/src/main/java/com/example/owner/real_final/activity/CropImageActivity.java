package com.example.owner.real_final.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.system.ErrnoException;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.owner.real_final.R;
import com.example.owner.real_final.database.MenuData;
import com.example.owner.real_final.database.RestaurantData;
import com.example.owner.real_final.other.CheckResult;
import com.example.owner.real_final.other.TaskforMenu;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class CropImageActivity extends AppCompatActivity {
    private CropImageView mCropImageView;
    private Uri mCropImageUri;
    private Uri ImageUri;
    String Ocresult = "";
    String temp = "";
    Bitmap cropped;
    Toolbar toolbar;
    String name, money;
    int count = 0;
    String[] temp2;
    CheckResult.NaverTranslateTask.TranslatedItem items;
    String searchString = "";
    ImageButton finish;
    String menu_name, menu_money;
    AlertDialog.Builder alt_bld;
    String travels_key, restaurants_key;

    private DatabaseReference database;
    FirebaseAuth mAuth;
    String uid;
    RestaurantData rd;
    MenuData md;

    String sourceLang = "en";

    static {
        System.loadLibrary("opencv_java3");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cropimage);
        mCropImageView = (CropImageView) findViewById(R.id.cropImageView);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setToolbarTitle();

        final String travels = (String) getIntent().getStringExtra("tdata");
        travels_key = travels;

        restaurants_key = (String) getIntent().getStringExtra("rdata");

        database = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        uid = currentUser.getUid();
        show();
    }

    public void show()
    {
        final List<String> ListItems = new ArrayList<>();
        ListItems.add("영어");
        ListItems.add("중국어 간체");
        ListItems.add("중국어 번체");
        ListItems.add("베트남어");
        ListItems.add("스페인어");
        ListItems.add("인도네시아어");
        ListItems.add("태국어");
        ListItems.add("프랑스어");
        final CharSequence[] items =  ListItems.toArray(new String[ ListItems.size()]);

        final List SelectedItems  = new ArrayList();
        int defaultItem = 0;
        SelectedItems.add(defaultItem);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Setting Language");
        builder.setSingleChoiceItems(items, defaultItem,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SelectedItems.clear();
                        SelectedItems.add(which);
                    }
                });
        builder.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String msg="";

                        if (!SelectedItems.isEmpty()) {
                            int index = (int) SelectedItems.get(0);
                            msg = ListItems.get(index);
                            if(msg.equals("영어")){
                                sourceLang = "en";
                            }else if(msg.equals("중국어 간체")){
                                sourceLang = "zh-CN";
                            }else if(msg.equals("중국어 번체")){
                                sourceLang = "zh-TW";
                            }else if(msg.equals("베트남어")){
                                sourceLang = "vi";
                            }else if(msg.equals("스페인어")){
                                sourceLang = "es";
                            }else if(msg.equals("태국어")){
                                sourceLang = "th";
                            }else if(msg.equals("프랑스어")){
                                sourceLang = "fr";
                            }
                            msg = msg + "(" + sourceLang + ")";
                        }
                        Toast.makeText(getApplicationContext(),
                                msg+" 이 선택되었습니다." , Toast.LENGTH_LONG)
                                .show();
                    }
                });
        builder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.show();
    }

    private void setToolbarTitle() {
        getSupportActionBar().setTitle("Menu Helper");
    }

    /**
     * On load image button click, start pick  image chooser activity.
     */
    public void onLoadImageClick(View view) {
        startActivityForResult(getPickImageChooserIntent(), 200);
    }

    /**
     * Crop the image and set it back to the  cropping view.
     */
    public void onCropImageClick(View view) {
        android.graphics.Rect rect = mCropImageView.getCropRect();
        int x = rect.width();
        int y = rect.height();
        cropped = mCropImageView.getCroppedImage(x, y);

        if (cropped != null) {
            Mat contour = ImageContour(cropped, getApplicationContext());
            for (int i = 0; i < stream.length; ++i) {
                if (stream[i].length() != 0) {
                    count++;
                }
            }

            name = "";
            money = "";

            for (int i = 0; i < stream.length; ++i) {
                if (stream[i].length() != 0) {
                    count++;
                    temp2 = stream[i].split("\n");
                    for (int j = 0; j < temp2.length; ++j) {
                    }
                    name = temp2[0];
                    money = temp2[temp2.length - 1];
                }
            }

            String menu = name + "\n" + money;

            NaverTranslateTask asyncTask = new NaverTranslateTask();
            asyncTask.execute(name.toLowerCase());
            mCropImageView.setImageUriAsync(ImageUri);
        }
    }

    //ASYNCTASK
    public class NaverTranslateTask extends AsyncTask<String, Void, String> {

        //Naver
        String clientId = "";//애플리케이션 클라이언트 아이디값";
        String clientSecret = "";//애플리케이션 클라이언트 시크릿값";
        //언어선택도 나중에 사용자가 선택할 수 있게 옵션 처리해 주면 된다.
        String targetLang = "ko";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        //AsyncTask 메인처리
        @Override
        protected String doInBackground(String... strings) {
            String sourceText = strings[0];
            try {
                String text = URLEncoder.encode(sourceText, "UTF-8");
                String apiURL = "https://openapi.naver.com/v1/papago/n2mt";
                URL url = new URL(apiURL);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("X-Naver-Client-Id", clientId);
                con.setRequestProperty("X-Naver-Client-Secret", clientSecret);
                // post request
                String postParams = "source=" + sourceLang + "&target=" + targetLang + "&text=" + text;
                con.setDoOutput(true);
                DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                wr.writeBytes(postParams);
                wr.flush();
                wr.close();
                int responseCode = con.getResponseCode();
                BufferedReader br;
                if (responseCode == 200) { // 정상 호출
                    br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                } else { // 에러 발생
                    br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                }
                String inputLine;
                StringBuffer response = new StringBuffer();
                while ((inputLine = br.readLine()) != null) {
                    response.append(inputLine);
                }
                br.close();
                return response.toString();
            } catch (Exception e) {
                Log.d("error", e.getMessage());
                return null;
            }
        }

        //번역된 결과를 받아서 처리
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        //최종 결과 처리부
        //JSON데이터를 자바객체로 변환해야 한다.
        //Gson을 사용할 것이다.
            Gson gson = new GsonBuilder().create();
            JsonParser parser = new JsonParser();
            JsonElement rootObj = parser.parse(s.toString())
            //원하는 데이터 까지 찾아 들어간다.
                    .getAsJsonObject().get("message")
                    .getAsJsonObject().get("result");
            //안드로이드 객체에 담기
            items = gson.fromJson(rootObj.toString(), CheckResult.NaverTranslateTask.TranslatedItem.class);
        }

        //자바용 그릇
        public class TranslatedItem {
            String translatedText;
            public String getTranslatedText() {
                return translatedText;
            }
        }
    }

    public void finish(View view) {
        DialogSimple();
    }

    private void submitMenuData(MenuData menus, String restaurants_key) {
        String key = database.child("member").child(uid).child("travels").child(travels_key).child("restaurants").child(restaurants_key).child("menus").push().getKey();
        menus = new MenuData(key, menu_name, menu_money,
                "1");
        menus.setmKey(key);

        database.child("member").child(uid).child("travels").child(travels_key)
                .child("restaurants").child(restaurants_key).child("menus").child(key).setValue(menus).addOnSuccessListener(
                this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                });
        finish();
    }

    public static boolean isStringDouble(String s) {
        try {
            Double.parseDouble(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void DialogSimple() {
        android.app.AlertDialog.Builder alt_bld = new android.app.AlertDialog.Builder(this);
        if (money == null)
            money = "0";
        alt_bld.setMessage(items.getTranslatedText() + ", " + money).setCancelable(
                false).setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        menu_name = name;
                        if (isStringDouble(money))
                            menu_money = money;
                        else
                            menu_money = "0";

                        submitMenuData(md, restaurants_key);

                        Toast.makeText(getApplicationContext(), "Yes", Toast.LENGTH_LONG).show();
                    }
                }).setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Action for 'NO' Button
                        Toast.makeText(getApplicationContext(), "No", Toast.LENGTH_LONG).show();
                        dialog.cancel();
                    }
                }).setNeutralButton("Search",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Action for 'NO' Button
                        Toast.makeText(getApplicationContext(), "Search", Toast.LENGTH_LONG).show();
                        searchString = name;
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_WEB_SEARCH);

                        intent.putExtra(SearchManager.QUERY, searchString);
                        startActivity(intent);
                        dialog.cancel();
                    }
                });
        android.app.AlertDialog alert = alt_bld.create();
        // Title for AlertDialog
        alert.setTitle("Title");
        // Icon for AlertDialog
        alert.show();
    }



    /*
    public void TestImageMorph(View view){
        Intent intent = new Intent(MainActivity.this, Morph.class);
        intent.putExtra("cropped", cropped);
        startActivity(intent);
        finish();
    }
    */

    //Matrixes
    Mat imageRGBA, imageGray, imageCanny, imageinRange, imageTMP, imageContours, imageToBmpTMP;
    String[] stream;
    Bitmap[] result;
    Scalar contourColor = new Scalar(255, 0, 0, 255);

    public Mat ImageContour(Bitmap bitmap, Context context) {

        int tresh1 = 50, tresh2 = 150;
        double erosionSize = 0.5;
        double dilatationSize = 0.5;

        imageRGBA = new Mat();
        Utils.bitmapToMat(bitmap, imageRGBA);

        Mat image3 = new Mat(imageRGBA.size(), CvType.CV_8UC1);
        image3 = imageRGBA;

        imageGray = new Mat(imageRGBA.size(), CvType.CV_8UC1);
        imageCanny = new Mat(imageRGBA.size(), CvType.CV_8UC1);
        imageinRange = new Mat(imageRGBA.size(), CvType.CV_8UC1);
        imageTMP = new Mat(imageRGBA.size(), CvType.CV_8UC1);
        imageToBmpTMP = new Mat(imageRGBA.size(), CvType.CV_8UC4);
        Mat adaptiveThresh = new Mat(imageRGBA.size(), CvType.CV_8UC1);

        List<MatOfPoint> contours = new ArrayList<>();
        //Mat hierarchy = new Mat();
        Mat hierarchy = new Mat();
        imageContours = new Mat(imageRGBA.size(), CvType.CV_8UC1);

        Imgproc.cvtColor(imageRGBA, imageGray, Imgproc.COLOR_RGBA2GRAY);

        //Imgproc.Canny(imageGray, imageCanny, tresh1, tresh2);    //only for test

        //Core.inRange(imageGray, new Scalar(tresh1, tresh1, tresh1), new Scalar(tresh2, tresh2, tresh2), imageinRange);
        Imgproc.adaptiveThreshold(imageGray, adaptiveThresh, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C,
                Imgproc.THRESH_BINARY_INV, 15, 12);

        //openinng image (erode+dilate)
        Mat element1 = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(2 * erosionSize + 1, 2 * erosionSize + 1));
        //Imgproc.erode(imageinRange, imageTMP, element1);
        Imgproc.erode(adaptiveThresh, imageTMP, element1);
        Mat element2 = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(2 * dilatationSize + 1, 2 * dilatationSize + 1));
        //Imgproc.dilate(imageinRange, imageTMP, element2);
        Imgproc.dilate(adaptiveThresh, imageTMP, element2);

        Mat gradient = new Mat();
        Imgproc.morphologyEx(imageGray, gradient, Imgproc.MORPH_GRADIENT, element2);
        Imgproc.adaptiveThreshold(gradient, adaptiveThresh, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C,
                Imgproc.THRESH_BINARY_INV, 15, 12);
        // ****Morph Opening
        Mat opening = new Mat();

        //Imgproc.morphologyEx(adaptiveThresh, opening, Imgproc.MORPH_OPEN, element2);
        // ****Morph Closing
        Mat closing = new Mat();
        Mat element3 = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(2 * 0.8 + 1, 2 * 0.8 + 1));
        Imgproc.morphologyEx(adaptiveThresh, closing, Imgproc.MORPH_CLOSE, element3);

        //finding countours
        //Imgproc.findContours(imageTMP,contours,hierarchy,Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
        Imgproc.findContours(closing, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

        RotatedRect[] minRect = new RotatedRect[contours.size()];
        double[] height = new double[contours.size()];
        double[] width = new double[contours.size()];
        int count = 0;
        result = new Bitmap[contours.size()];

        for (int i = 0; i < contours.size(); i++) {
            Rect rect = Imgproc.boundingRect(contours.get(i));
            MatOfPoint matOfPoint = contours.get(i);
            MatOfPoint2f matOfPoint2f = new MatOfPoint2f(matOfPoint.toArray());
            minRect[i] = Imgproc.minAreaRect(matOfPoint2f);
            Point[] rectPoints = new Point[4];
            minRect[i].points(rectPoints);
            for (int j = 0; j < 4; j++) {
                //Imgproc.line(imageRGBA, rectPoints[j], rectPoints[(j + 1) % 4], contourColor);
            }
            /*
            if (minRect[i].size.height > 100 && minRect[i].size.height < 600 || minRect[i].size.width > 100 && minRect[i].size.width < 600) {
                height[i] = Math.round(minRect[i].size.height);
                width[i] = Math.round(minRect[i].size.width);
                stream = String.valueOf(i) + ": " + String.valueOf(height[i]) + "x" + String.valueOf(width[i]);
                Imgproc.putText(imageRGBA, stream, new Point(minRect[i].center.x - minRect[i].size.width / 2,
                               minRect[i].center.y - minRect[i].size.height / 2),
                        Core.FONT_ITALIC, 1.0, contourColor);
            }
            */
            int[] current_hierarchy = new int[4];
            hierarchy.get(0, i, current_hierarchy);
            if (current_hierarchy[3] == -1) {
                Imgproc.rectangle(imageRGBA, rect.br(), rect.tl(), new Scalar(0, 255, 0), 1);
                Mat image_roi = new Mat(imageRGBA, rect);
                result[i] = Bitmap.createBitmap(image_roi.width(), image_roi.height(), Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(image_roi, result[i]);
            }
        }
/*
        result = new Bitmap[count];
        for (int i = 0; i < contours.size(); i++) {
            Rect rect = Imgproc.boundingRect(contours.get(i));
            MatOfPoint matOfPoint = contours.get(i);
            MatOfPoint2f matOfPoint2f = new MatOfPoint2f(matOfPoint.toArray());
            minRect[i] = Imgproc.minAreaRect(matOfPoint2f);
            Imgproc.rectangle(imageRGBA, rect.br(), rect.tl(), new Scalar(0, 255, 0), 1);
            if (rect.height > 20 && rect.width > 20 && !(rect.width >= 512 - 5 && rect.height >= 512 - 5)) {
                Mat image_roi = new Mat(imageRGBA, rect);
                result[i] = Bitmap.createBitmap(image_roi.width(), image_roi.height(), Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(image_roi, result[i]);
            }
        }
        */

        stream = new String[result.length];
        for (int i = 0; i < result.length; ++i) {
            stream[i] = ocr(result[i], context);
            // Ocresult += ocr(result[i], context);
        }

        return closing;

    }

    // Google Ocr
    public String ocr(Bitmap bitmap, Context context) {
        StringBuilder ocrResult = new StringBuilder();

        try {
            //for(int j=0; j<bitmap.length; i++){
            TextRecognizer textRecognizer = new TextRecognizer.Builder(context).build();
            if (textRecognizer.isOperational()) {
                Log.i("Text Recog", "is Operational");

                Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                SparseArray<TextBlock> items = textRecognizer.detect(frame);
                StringBuilder stringBuilder = new StringBuilder();
                for (int k = 0; k < items.size(); k++) {
                    TextBlock item = items.valueAt(k);
                    stringBuilder.append(item.getValue());
                    stringBuilder.append("\n");
                }
                ocrResult.append(stringBuilder.toString());
            } else {
                Log.i("Text Recog", "is not Operational");
            }
            //}

        } catch (Exception e) {
            e.printStackTrace();
        }

        return ocrResult.toString();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            Uri imageUri = getPickImageResultUri(data);
            ImageUri = imageUri;

            // For API >= 23 we need to check specifically that we have permissions to read external storage,
            // but we don't know if we need to for the URI so the simplest is to try open the stream and see if we get error.
            boolean requirePermissions = false;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                    checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                    isUriRequiresPermissions(imageUri)) {

                // request permissions and handle the result in onRequestPermissionsResult()
                requirePermissions = true;
                mCropImageUri = imageUri;
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
            }

            if (!requirePermissions) {
                mCropImageView.setImageUriAsync(imageUri);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (mCropImageUri != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            mCropImageView.setImageUriAsync(mCropImageUri);
        } else {
            Toast.makeText(this, "Required permissions are not granted", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Create a chooser intent to select the  source to get image from.<br/>
     * The source can be camera's  (ACTION_IMAGE_CAPTURE) or gallery's (ACTION_GET_CONTENT).<br/>
     * All possible sources are added to the  intent chooser.
     */
    public Intent getPickImageChooserIntent() {

// Determine Uri of camera image to  save.
        Uri outputFileUri = getCaptureImageOutputUri();

        List<Intent> allIntents = new ArrayList<>();
        PackageManager packageManager = getPackageManager();

// collect all camera intents
        Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            if (outputFileUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            }
            allIntents.add(intent);
        }

// collect all gallery intents
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        List<ResolveInfo> listGallery = packageManager.queryIntentActivities(galleryIntent, 0);
        for (ResolveInfo res : listGallery) {
            Intent intent = new Intent(galleryIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            allIntents.add(intent);
        }

// the main intent is the last in the  list (fucking android) so pickup the useless one
        Intent mainIntent = allIntents.get(allIntents.size() - 1);
        for (Intent intent : allIntents) {
            if (intent.getComponent().getClassName().equals("com.android.documentsui.DocumentsActivity")) {
                mainIntent = intent;
                break;
            }
        }
        allIntents.remove(mainIntent);

// Create a chooser from the main  intent
        Intent chooserIntent = Intent.createChooser(mainIntent, "Select source");

// Add all other intents
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, allIntents.toArray(new Parcelable[allIntents.size()]));

        return chooserIntent;
    }

    /**
     * Get URI to image received from capture  by camera.
     */
    private Uri getCaptureImageOutputUri() {
        Uri outputFileUri = null;
        File getImage = getExternalCacheDir();
        if (getImage != null) {
            outputFileUri = Uri.fromFile(new File(getImage.getPath(), "pickImageResult.jpeg"));
        }
        return outputFileUri;
    }

    /**
     * Get the URI of the selected image from  {@link #getPickImageChooserIntent()}.<br/>
     * Will return the correct URI for camera  and gallery image.
     *
     * @param data the returned data of the  activity result
     */
    public Uri getPickImageResultUri(Intent data) {
        boolean isCamera = true;
        if (data != null && data.getData() != null) {
            String action = data.getAction();
            isCamera = action != null && action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
        }
        return isCamera ? getCaptureImageOutputUri() : data.getData();
    }

    /**
     * Test if we can open the given Android URI to test if permission required error is thrown.<br>
     */
    public boolean isUriRequiresPermissions(Uri uri) {
        try {
            ContentResolver resolver = getContentResolver();
            InputStream stream = resolver.openInputStream(uri);
            stream.close();
            return false;
        } catch (FileNotFoundException e) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (e.getCause() instanceof ErrnoException) {
                    return true;
                }
            }
        } catch (Exception e) {
        }
        return false;
    }

}
