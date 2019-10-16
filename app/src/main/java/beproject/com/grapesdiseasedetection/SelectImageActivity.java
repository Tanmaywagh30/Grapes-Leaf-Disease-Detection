package beproject.com.grapesdiseasedetection;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import beproject.com.Network.NetworkClient;
import beproject.com.constant.Utils;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SelectImageActivity extends Activity implements View.OnClickListener {

    Button gallery, camera,next;
    File imageFile;
    ImageView chosenImage;
    Bitmap imageBitmap;
    String currentPhotoPath,finalURL,hostIP,Port,imageurl;
    Retrofit retrofit;
    Dialog mDialog;
    ImageView bg;
    ContentValues values;
    Uri imageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selectimage);

        gallery = findViewById(R.id.selectGallery);
        camera = findViewById(R.id.selectCamera);
        chosenImage = findViewById(R.id.selectedImage);
        next = findViewById(R.id.next);
        bg = findViewById(R.id.background);

        Picasso.with(this).load(R.mipmap.bg).into(bg);
        gallery.setOnClickListener(this);
        camera.setOnClickListener(this);
        next.setOnClickListener(this);

        hostIP = Utils.getSharedPreferences(this,"ipdata","ipaddress");
        Port = Utils.getSharedPreferences(this,"ipdata","port");
        finalURL = "http://"+hostIP+":"+Port;

        retrofit = NetworkClient.getRetrofitClient(SelectImageActivity.this,finalURL);

        Log.d("tag","URL = "+finalURL);
        mDialog = Utils.getDialogBox(this,R.layout.progress_bar);
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.selectGallery:
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setAction(Intent.ACTION_PICK);
                galleryIntent.setType("image/*");
                startActivityForResult(Intent.createChooser(galleryIntent, "Select Picture"), 1);
                break;

            case R.id.selectCamera:
                values = new ContentValues();
                values.put(MediaStore.Images.Media.TITLE, "New Picture");
                values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
                imageUri = getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, 2);
                break;


            case R.id.next:
                mDialog.show();
                NetworkClient.sendDataToServer sendDataToServerAPI= retrofit.create(NetworkClient.sendDataToServer.class);
                String extension = imageFile.getName().substring(imageFile.getName().lastIndexOf("."));
                String name = ""+System.currentTimeMillis();

                Utils.saveToSharedPreferences(this,"user_data","file_name",name+extension);
                Log.d("Tag","Image = "+name+extension);
                RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"),imageFile);
                MultipartBody.Part part = MultipartBody.Part.createFormData("upload",name+extension,requestBody);
                RequestBody description = RequestBody.create(MediaType.parse("text/plain"), "image-type");

                Call call = sendDataToServerAPI.uploadImage(part,description);
                call.enqueue(new Callback() {
                    @Override
                    public void onResponse(Call call, Response response) {
                        mDialog.dismiss();
                        Toast.makeText(SelectImageActivity.this, "Uploaded Successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(SelectImageActivity.this,PredictActivity.class));
                    }

                    @Override
                    public void onFailure(Call call, Throwable t) {
                        Log.d("Tag","Fail");
                    }
                });
                break;

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {

            case 1:
                Uri selectedImage = data.getData();
                String imagepath = getPath(selectedImage);
                imageFile = new File(imagepath);
                break;

            case 2:
                if (resultCode == Activity.RESULT_OK) {

                    try{
                    imageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                    chosenImage.setImageBitmap(imageBitmap);
                    imageurl = getRealPathFromURI(imageUri);
                    imageFile = new File(imageurl);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;

        }

    }

    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(projection[0]);
        String filePath = cursor.getString(columnIndex);
        //cursor.close();
        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
        chosenImage.setImageBitmap(bitmap);
        return cursor.getString(column_index);
    }

    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }
}
