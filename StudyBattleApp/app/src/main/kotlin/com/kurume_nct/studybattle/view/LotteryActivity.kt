package com.kurume_nct.studybattle.view
import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Resources
import android.content.res.TypedArray
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.media.Image
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.kurume_nct.studybattle.R


 class LotteryActivity:Activity() {

internal var card1:ImageView
internal var card2:ImageView
internal var card3:ImageView
internal var item:ImageView
internal val item_sum = 4
internal var result:TextView


protected override fun onCreate(savedInstanceState:Bundle?) {
super.onCreate(savedInstanceState)
setContentView(R.layout.activity_lottery)

val resources = getResources()

card1 = findViewById(R.id.card1) as ImageView
card2 = findViewById(R.id.card2) as ImageView
card3 = findViewById(R.id.card3) as ImageView
item = findViewById(R.id.result_card) as ImageView
result = findViewById(R.id.result_text) as TextView


val bmp1 = BitmapFactory.decodeResource(getResources(), R.drawable.lotterycard)
card1.setImageBitmap(bmp1)
val bmp2 = BitmapFactory.decodeResource(getResources(), R.drawable.lotterycard)
card2.setImageBitmap(bmp2)
val bmp3 = BitmapFactory.decodeResource(getResources(), R.drawable.lotterycard)
card3.setImageBitmap(bmp3)

item.setVisibility(View.INVISIBLE)
card1.setVisibility(View.VISIBLE)
card2.setVisibility(View.VISIBLE)
card3.setVisibility(View.VISIBLE)

card1.setOnClickListener(object:View.OnClickListener {
public override fun onClick(v:View) {
mainToReturn()
}
})
card2.setOnClickListener(object:View.OnClickListener {
public override fun onClick(v:View) {
mainToReturn()
}
})

card3.setOnClickListener(object:View.OnClickListener {
public override fun onClick(v:View) {
mainToReturn()
}
})
}




internal fun mainToReturn() {
 //もし表示されてないなら表示する
        if (item.getVisibility() != View.VISIBLE)
{
val typedArray = getResources().obtainTypedArray(R.array.item)
val i = (Math.floor(Math.random() * item_sum)).toInt()
val drawable = typedArray.getDrawable(i)
item.setImageDrawable(drawable)
item.setVisibility(View.VISIBLE)
if (i == 0)
result.setText("爆弾GET！")
else if (i == 1)
result.setText("2倍カードGET!")
else if (i == 2)
result.setText("マジックハンドGET！")
else
result.setText("シールドGET!")

val handler = Handler()
handler.postDelayed(object:Runnable {
public override fun run() {
 // Intent intent=new Intent(getApplication(),MainActivity.class);
                    //startActivity(intent);
                }
}, 2500)
}


}

}



