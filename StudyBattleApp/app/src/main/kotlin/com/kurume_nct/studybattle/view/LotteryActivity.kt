package com.kurume_nct.studybattle.view

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView

import com.bumptech.glide.Glide
import com.kurume_nct.studybattle.R
import com.kurume_nct.studybattle.model.UsersObject

//by kirby, converted to kotlin by huna

class LotteryActivity : Activity() {

    private lateinit var card1: ImageView
    private lateinit var card2: ImageView
    private lateinit var card3: ImageView
    private lateinit var lotteried: ImageView
    private lateinit var lottery_text: ImageView
    private lateinit var text1: TextView
    private lateinit var bmp4: Bitmap
    private lateinit var usersObject: UsersObject
    private var itemNumber = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lottery)

        itemNumber = intent.getIntExtra("item", 278)
        Log.d("item", itemNumber.toString())

        card1 = findViewById(R.id.card1) as ImageView
        card2 = findViewById(R.id.card2) as ImageView
        card3 = findViewById(R.id.card3) as ImageView
        text1 = findViewById(R.id.lottery_text) as TextView
        lotteried = findViewById(R.id.lotteried_item) as ImageView
        lottery_text = findViewById(R.id.lottery_item_text) as ImageView
        usersObject = application as UsersObject

        Glide.with(this).load(R.drawable.lotterycard).into(card1)
        Glide.with(this).load(R.drawable.lotterycard).into(card2)
        Glide.with(this).load(R.drawable.lotterycard).into(card3)
        bmp4 = BitmapFactory.decodeResource(resources, R.drawable.text_itemget)

        card1.visibility = View.VISIBLE
        card2.visibility = View.VISIBLE
        card3.visibility = View.VISIBLE
        lotteried.visibility = View.INVISIBLE
        lottery_text.visibility = View.INVISIBLE

        card1.setOnClickListener { mainToReturn(card1) }
        card2.setOnClickListener { mainToReturn(card2) }
        card3.setOnClickListener { mainToReturn(card3) }
    }


    private fun mainToReturn(item: ImageView) {
        //一回限りの処理
        card1.isEnabled = false
        card2.isEnabled = false
        card3.isEnabled = false
        val typedArray = resources.obtainTypedArray(R.array.item)
        val i = itemNumber
        val drawable = typedArray.getDrawable(i)
        item.setImageDrawable(drawable)
        item.visibility = View.VISIBLE
        when (i) {
            1 -> {
                text1.text = "爆弾GET！"
                lotteried.setImageBitmap(BitmapFactory.decodeResource(resources, R.drawable.bomb))
                lottery_text.setImageBitmap(bmp4)
            }
            3 -> {
                text1.text = "2倍カードGET!"
                lotteried.setImageBitmap(BitmapFactory.decodeResource(resources, R.drawable.card))
                lottery_text.setImageBitmap(bmp4)
            }
            4 -> {
                text1.text = "マジックハンドGET！"
                lotteried.setImageBitmap(BitmapFactory.decodeResource(resources, R.drawable.magichand))
                lottery_text.setImageBitmap(bmp4)
            }
            2 -> {
                text1.text = "シールドGET!"
                lotteried.setImageBitmap(BitmapFactory.decodeResource(resources, R.drawable.shield))
                lottery_text.setImageBitmap(bmp4)
            }
            else -> {
                text1.text = "アイテム獲得ならず....."
                bmp4 = BitmapFactory.decodeResource(resources, R.drawable.gagan)
                lotteried.setImageBitmap(Bitmap.createScaledBitmap(bmp4, 500, 500, false))
            }
        }
        lotteried.visibility = View.VISIBLE
        lottery_text.visibility = View.VISIBLE
        val handler = Handler()
        handler.postDelayed({
            setResult(0)
            finish()
        }, 2500)
    }

}


