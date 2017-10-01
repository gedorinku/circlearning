package com.kurume_nct.studybattle.view;

import com.kurume_nct.studybattle.R;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.widget.Button;
import android.content.Intent;
import android.widget.ImageView;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.widget.TextView;
import android.os.Handler;
import android.widget.Toast;

import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;



public class CameraModeActivity extends Activity {
    private static CameraModeActivity instance = null;

    private final static int RESULT_CAMERA = 1001;
    private static final int RESULT_PICK_IMAGEFILE = 1000;
    private final static int REQUEST_PERMISSION = 1002;

    private ImageView imageview;
    private Button libraryButton;
<<<<<<< HEAD
    private TextView comment;
    private TextView experiment;//これで実験試してる
    private int flag = 0;
    private String userName;
    private Bitmap bmp1;
    private File cameraFile;
    private Uri cameraUri;
    private String filePath;

=======
    private TextView commnent_;
    private ImageView shock;
    private int flag=0;
    private String userName;
>>>>>>> master

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        instance = this;

        setContentView(R.layout.activity_camera_mode);
        userName = getIntent().getStringExtra("userName");
        // 宣言
        comment = (TextView) findViewById(R.id.comment);
        imageview = (ImageView) findViewById((R.id.answer));
        libraryButton = (Button) findViewById(R.id.library_button);
        experiment = (TextView) findViewById(R.id.experiment);
        final Button submissionButton = (Button) findViewById(R.id.submission_button);
        Button passButton = (Button) findViewById(R.id.pass_button);


        //(uriについての実験機能)
        if (savedInstanceState != null) {
            cameraUri = savedInstanceState.getParcelable("CaptureUri");
        }


        libraryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ファイルを選択
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                //開けるものだけ表示
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                //イメージのみを表示するフィルタ
                intent.setType("image/*");
                startActivityForResult(intent, RESULT_PICK_IMAGEFILE);
            }
        });

        //カメラ撮影ボタン
        final Button cameraButton = (Button) findViewById(R.id.camera_button);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Android 6, API 23以上でパーミッシンの確認
                if (Build.VERSION.SDK_INT >= 23) {
                    checkPermission();
                } else {
                    cameraIntent();
                }
            }
        });

        //パスするボタン
        passButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //他の画面に遷移しないようにする
                submissionButton.setEnabled(false);
                cameraButton.setEnabled(false);
                libraryButton.setEnabled(false);


                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, 2500);
            }
        });

        //提出するボタン
        submissionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (flag == 1) {
                    Intent intent1 = new Intent(getApplication(), LotteryActivity.class);
<<<<<<< HEAD
                    intent1.putExtra("userName", userName);
                    startActivity(intent1);
                    finish();
                } else comment.setText("解答を提出してください");
=======
                    intent1.putExtra("userName",userName);
                    startActivity(intent1);
                    finish();
                }else commnent_.setText("解答を提出してください");
>>>>>>> master
            }
        });
    }


    public static CameraModeActivity getInstance() {
        return instance;
    }


    //キャプチャーパス取得関数
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable("CaptureUri", cameraUri);
    }

    //画像関連の処理関数
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RESULT_CAMERA) {
            //カメラ撮影の処理
            if (cameraUri != null) {
                imageview.setImageURI(cameraUri);
                registerDatabase(filePath);

            } else {
                Log.d("debug", "cameraUri == null");
            }

        } else if (requestCode == RESULT_PICK_IMAGEFILE && resultCode == Activity.RESULT_OK) {
            //ギャラリーからの写真を選択
            Uri uri = null;
            if (data != null) {
                uri = data.getData();
                Log.i("", "Uri: " + uri.toString());

                try {
                    bmp1 = getBitmapFromUri(uri);
                    imageview.setImageBitmap(bmp1);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

        }
        flag = 1;
        comment.setText("解答を提出してね");
        // experiment.setText(filePath);
    }


    //ギャラリーpath取得関数
    private String getGalleryPath() {
        return Environment.getExternalStorageDirectory() + "/" + Environment.DIRECTORY_DCIM + "/";
    }

    //カメラ撮影した際の画像を保存するフォルダ作成関数
    private void cameraIntent() {
        // 保存先のフォルダーを作成
        File cameraFolder = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), ""
        );
        cameraFolder.mkdirs();

        // 保存ファイル名
        String fileName = new SimpleDateFormat("ddHHmmss").format(new Date());
        filePath = cameraFolder.getPath() + "/" + fileName + ".jpg";
        Log.d("debug", "filePath:" + filePath);


        // capture画像のファイルパス
        cameraFile = new File(filePath);
//        cameraUri = Uri.fromFile(cameraFile);
        cameraUri = FileProvider.getUriForFile(CameraModeActivity.this, getApplicationContext().getPackageName() + ".provider", cameraFile);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri);
        startActivityForResult(intent, RESULT_CAMERA);
    }

    //画像をギャラリーから選んで取得して返す？（多分）
    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }

    //パーミッション確認関数
    private void checkPermission() {
        // 既に許可している
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            cameraIntent();
        }
        // 拒否していた場合
        else {
            requestLocationPermission();
        }
    }

    // 許可を求める関数
    private void requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            ActivityCompat.requestPermissions(CameraModeActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION);

        } else {
            Toast toast = Toast.makeText(this, "許可されないとアプリが実行できません", Toast.LENGTH_SHORT);
            toast.show();

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,}, REQUEST_PERMISSION);

        }
    }


    // 結果の受け取りする関数
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION) {
            // 使用が許可された
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                cameraIntent();
                return;

            } else {
                // それでも拒否された時の対応
                Toast toast = Toast.makeText(this, "これ以上なにもできません", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    // アンドロイドのデータベースへ登録する関数
    private void registerDatabase(String file) {
        ContentValues contentValues = new ContentValues();
        ContentResolver contentResolver = CameraModeActivity.this.getContentResolver();
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        contentValues.put("_data", file);
        contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
    }

    //uriからpath取得
    public static String getPath(Context context, Uri uri) {
        ContentResolver contentResolver = context.getContentResolver();
        String[] columns = {MediaStore.Images.Media.DATA};
        Cursor cursor = contentResolver.query(uri, columns, null, null, null);
        cursor.moveToFirst();
        String path = cursor.getString(0);
        cursor.close();
        return path;
    }

}



