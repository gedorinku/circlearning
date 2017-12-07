package com.kurume_nct.studybattle.view

import android.app.Activity
import android.databinding.DataBindingUtil
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.ImageView

import com.bumptech.glide.Glide
import com.kurume_nct.studybattle.R
import com.kurume_nct.studybattle.databinding.ActivityLotteryBinding
import com.kurume_nct.studybattle.model.*

class LotteryActivity : Activity() {

    private lateinit var binding: ActivityLotteryBinding
    /*当たるアイテム*/
    private var itemNumber = 0
    /*アイテムの番号*/
    private val Bomb = 1
    private val Shield = 2
    private val DoubleScoreCard = 3
    private val MagicHand = 4


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_lottery)

        itemNumber = intent.getIntExtra("item", 278)

        viewInitSetting()

    }


    private fun viewInitSetting() {

        /*カードをかぶせる*/
        Glide.with(this).run {
            load(R.drawable.lotterycard).into(binding.cardLeft)
            load(R.drawable.lotterycard).into(binding.cardCenter)
            load(R.drawable.lotterycard).into(binding.cardRight)
            load(R.drawable.abc_list_selector_background_transition_holo_light).into(binding.electedItemImage)
        }

        binding.apply {
            cardLeft.setOnClickListener { onOpeningCard(binding.cardLeft) }
            cardCenter.setOnClickListener { onOpeningCard(binding.cardCenter) }
            cardLeft.setOnClickListener { onOpeningCard(binding.cardRight) }
            electedItemImage.setOnClickListener { setResult(0); finish() }
        }

    }


    /*Userがアイテムを選択した後の処理*/
    private fun onOpeningCard(chooseImageView: ImageView) {

        binding.apply {
            cardLeft.isClickable = false
            cardCenter.isClickable = false
            cardRight.isClickable = false
        }

        when (itemNumber) {
            Bomb -> {
                binding.apply {
                    lotteryText.text = "爆弾GET！"
                    chooseImageView.setImageResource(R.drawable.bomb)
                    electedItemImage.setImageResource(R.drawable.bomb)
                }
            }
            DoubleScoreCard -> {
                binding.apply {
                    lotteryText.text = "2倍カードGET!"
                    chooseImageView.setImageResource(R.drawable.card)
                    electedItemImage.setImageResource(R.drawable.card)
                }
            }
            MagicHand -> {
                binding.apply {
                    lotteryText.text = "マジックハンドGET！"
                    chooseImageView.setImageResource(R.drawable.magichand)
                    electedItemImage.setImageResource(R.drawable.magichand)
                }
            }
            Shield -> {
                binding.apply {
                    lotteryText.text = "シールドGET!"
                    chooseImageView.setImageResource(R.drawable.shield)
                    electedItemImage.setImageResource(R.drawable.shield)
                }
            }
        /*itemNumber == Air*/
            else -> {
                binding.apply {
                    lotteryText.text = "アイテム獲得ならず....."
                    chooseImageView.setImageResource(R.drawable.hazure)
                    electedItemImage.setImageResource(R.drawable.gagan)
                }
            }
        }

        chooseImageView.visibility = View.VISIBLE
        if (itemNumber > 0) binding.electedItemTextImage.visibility = View.VISIBLE

    }
}


