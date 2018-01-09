package com.kurume_nct.studybattle.view

import android.app.Activity
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.View
import android.widget.ImageView

import com.bumptech.glide.Glide
import com.kurume_nct.studybattle.R
import com.kurume_nct.studybattle.databinding.ActivityLotteryBinding

class LotteryActivity : Activity() {

    private lateinit var binding: ActivityLotteryBinding
    /*当たるアイテム*/
    private var itemNumber = 0
    /*アイテムごとのView*/
    private val itemResource = listOf(R.drawable.gagan, R.drawable.bomb, R.drawable.shield, R.drawable.card, R.drawable.magichand)
    private val itemSubText = listOf("アイテム獲得ならず.....", "爆弾GET！", "2倍カードGET!", "マジックハンドGET！", "シールドGET!")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_lottery)
        itemNumber = intent.getIntExtra("item", 278)

        viewInitSetting()

    }


    private fun viewInitSetting() {

        /*カードをかぶせる*/
        Glide.with(this).load(R.drawable.lotterycard).run {
            into(binding.cardLeft)
            into(binding.cardCenter)
            into(binding.cardRight)
        }

        binding.apply {
            cardLeft.setOnClickListener { onOpeningCard(binding.cardLeft) }
            cardCenter.setOnClickListener { onOpeningCard(binding.cardCenter) }
            cardRight.setOnClickListener { onOpeningCard(binding.cardRight) }
            electedItemImage.setOnClickListener { setResult(0); finish() }
        }

    }


    /*Userがアイテムを選択した後の処理*/
    private fun onOpeningCard(chooseImageView: ImageView) {

        binding.apply {

            cardLeft.isClickable = false
            cardCenter.isClickable = false
            cardRight.isClickable = false

            lotteryText.text = itemSubText[itemNumber]
            electedItemImage.setImageResource(itemResource[itemNumber])
            if (itemNumber > 0) electedItemTextImage.visibility = View.VISIBLE

        }

        chooseImageView.setImageResource(itemResource[itemNumber])
        chooseImageView.visibility = View.VISIBLE

    }
}


