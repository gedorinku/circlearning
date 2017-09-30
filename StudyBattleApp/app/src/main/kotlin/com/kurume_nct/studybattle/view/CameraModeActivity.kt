package com.kurume_nct.studybattle.view

import com.kurume_nct.studybattle.R
import android.app.Activity
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.content.Intent
import android.widget.ImageView
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.util.Log
import android.widget.TextView
import android.os.Handler
import java.io.FileDescriptor
import java.io.IOException




 class CameraModeActivity:Activity() {

private var imageview:ImageView? = null
private var libraryButton:Button? = null
private var submissionButton:Button? = null
private var passButton:Button? = null
private var cameraButton:Button? = null
private var comment:TextView? = null
private var shock:ImageView? = null
private var flag:Int = 0
private var bitmap:Bitmap? = null
internal val handler = Handler()

 //ギャラリーpath取得関数
    private val galleryPath:String
get() {
return Environment.getExternalStorageDirectory() + "/" + Environment.DIRECTORY_DCIM + "/"
}

protected override fun onCreate(saveInstanceState:Bundle?) {
super.onCreate(saveInstanceState)
setContentView(R.layout.activity_camera_mode)
 // 宣言
        flag = 0
comment = findViewById(R.id.comment) as TextView
imageview = findViewById((R.id.answer)) as ImageView//解答の写真
libraryButton = findViewById(R.id.library_button) as Button
shock = findViewById(R.id.shock_character) as ImageView
submissionButton = findViewById(R.id.submission_button) as Button
passButton = findViewById(R.id.pass_button) as Button
cameraButton = findViewById(R.id.camera_button) as Button


shock!!.setVisibility(View.INVISIBLE)
 //commnent_.setText("ギャラリーのpath: " + getGalleryPath());//ギャラリーのpathを取得すru
        submissionButton!!.setEnabled(true)
cameraButton!!.setEnabled(true)
libraryButton!!.setEnabled(true)
passButton!!.setEnabled(true)


libraryButton!!.setOnClickListener(object:View.OnClickListener {
public override fun onClick(v:View) {
 //ファイルを選択
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
 //開けるものだけ表示
                intent.addCategory(Intent.CATEGORY_OPENABLE)
 //イメージのみを表示するフィルタ
                intent.setType("image/*")
startActivityForResult(intent, RESULT_PICK_IMAGEFILE)
}
})

cameraButton!!.setOnClickListener(object:View.OnClickListener {
public override fun onClick(view:View) {
val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
startActivityForResult(intent, RESULT_CAMERA)
}
})



 //パスするボタン
        passButton!!.setOnClickListener(object:View.OnClickListener {
public override fun onClick(view:View) {

buttonKill()

comment!!.setText("次は頑張ろう.....")
shock!!.setVisibility(View.VISIBLE)
val bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.shock)
shock!!.setImageBitmap(bitmap)



handler.postDelayed(object:Runnable {
public override fun run() {
finish()
}
}, 2500)
}
})

 //提出するボタン
        submissionButton!!.setOnClickListener(object:View.OnClickListener {

public override fun onClick(view:View) {

comment!!.setText("0")
if (flag == 1)
{

 //キャラクターがにっこり
                    comment!!.setText("1")
shock!!.setVisibility(View.VISIBLE)
comment!!.setText("2")
bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.glad)
comment!!.setText("3")
shock!!.setImageBitmap(bitmap)
comment!!.setText("解答の提出に成功しました")
buttonKill()

handler.postDelayed(object:Runnable {
public override fun run() {
val intent1 = Intent(getApplication(), LotteryActivity::class.java)
startActivity(intent1)
}
}, 2500)

}
else
comment!!.setText("解答を提出してください")
}
})
}



protected override fun onActivityResult(requestCode:Int, resultCode:Int, data:Intent?) {
if (requestCode == RESULT_CAMERA)
{
 //カメラ撮影の処理
            val bitmap = data!!.getExtras().get("data") as Bitmap?
imageview!!.setImageBitmap(bitmap)
}
else if (requestCode == RESULT_PICK_IMAGEFILE && resultCode == Activity.RESULT_OK)
{
 //ギャラリーからの写真を選択
            var uri:Uri? = null
if (data != null)
{
uri = data!!.getData()
Log.i("", "Uri: " + uri!!.toString())

try
{
val bmp = getBitmapFromUri(uri)
imageview!!.setImageBitmap(bmp)
}
catch (e:IOException) {
e.printStackTrace()
}

}
}
flag = 1
comment!!.setText("解答を提出してね")
}

 //画像を選んで取得して返す？（多分）
    @Throws(IOException::class)
private fun getBitmapFromUri(uri:Uri):Bitmap {
val parcelFileDescriptor = getContentResolver().openFileDescriptor(uri, "r")
val fileDescriptor = parcelFileDescriptor!!.getFileDescriptor()
val image = BitmapFactory.decodeFileDescriptor(fileDescriptor)
parcelFileDescriptor!!.close()
return image
}

private fun buttonKill() {
 //他の画面に遷移しないようにする
        submissionButton!!.setEnabled(false)
cameraButton!!.setEnabled(false)
libraryButton!!.setEnabled(false)
passButton!!.setEnabled(false)
}

companion object {

private val RESULT_CAMERA = 1001
private val RESULT_PICK_IMAGEFILE = 1000
}

}

