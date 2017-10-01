package com.kurume_nct.studybattle.view;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

<<<<<<< HEAD
import com.kurume_nct.studybattle.MainActivity;
import com.kurume_nct.studybattle.R;



public class LotteryActivity extends Activity{
=======
import com.kurume_nct.studybattle.Main2Activity;
import com.kurume_nct.studybattle.R;


public class LotteryActivity extends Activity {
>>>>>>> master

    ImageView card1;
    ImageView card2;
    ImageView card3;
<<<<<<< HEAD
    ImageView lotteried;
    ImageView lottery_text;
    TextView text1;
    Bitmap bmp4;
    final int item_sum=5;

=======
    ImageView item;
    final int item_sum = 4;
    TextView result;
    String userName;
>>>>>>> master

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lottery);

        userName = getIntent().getStringExtra("userName");

        Resources resources = getResources();
        card1 = (ImageView) findViewById(R.id.card1);
        card2 = (ImageView) findViewById(R.id.card2);
        card3 = (ImageView) findViewById(R.id.card3);
<<<<<<< HEAD
        text1=(TextView) findViewById(R.id.lottery_text) ;
        lotteried=(ImageView)findViewById(R.id.lotteried_item);
        lottery_text=(ImageView)findViewById(R.id.lottery_item_text);

=======
        item = (ImageView) findViewById(R.id.result_card);
        result = (TextView) findViewById(R.id.result_text);
>>>>>>> master


        Bitmap bmp1 = BitmapFactory.decodeResource(getResources(), R.drawable.lotterycard);
        card1.setImageBitmap(bmp1);
        Bitmap bmp2 = BitmapFactory.decodeResource(getResources(), R.drawable.lotterycard);
        card2.setImageBitmap(bmp2);
        Bitmap bmp3 = BitmapFactory.decodeResource(getResources(), R.drawable.lotterycard);
        card3.setImageBitmap(bmp3);
        bmp4= BitmapFactory.decodeResource(getResources(),R.drawable.text_itemget);

        card1.setVisibility(View.VISIBLE);
        card2.setVisibility(View.VISIBLE);
        card3.setVisibility(View.VISIBLE);
        lotteried.setVisibility(View.INVISIBLE);
        lottery_text.setVisibility(View.INVISIBLE);

        card1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainToReturn(card1);
            }
        });
        card2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainToReturn(card2);
            }
        });
<<<<<<< HEAD
        card3.setOnClickListener(new View.OnClickListener(){
=======

        card3.setOnClickListener(new View.OnClickListener() {
>>>>>>> master
            @Override
            public void onClick(View v) {
                mainToReturn(card3);
            }
        });
    }

<<<<<<< HEAD


    void mainToReturn(ImageView item){
        //一回限りの処理
        card1.setEnabled(false);
        card2.setEnabled(false);
        card3.setEnabled(false);
        TypedArray typedArray = getResources().obtainTypedArray(R.array.item);
        int i = (int) (Math.floor(Math.random() * item_sum));
        Drawable drawable = typedArray.getDrawable(i);
        item.setImageDrawable(drawable);
        item.setVisibility(View.VISIBLE);
        if(i==0) {
            text1.setText("爆弾GET！");
            lotteried.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.bomb));
            lottery_text.setImageBitmap(bmp4);
        }else if(i==1) {
            text1.setText("2倍カードGET!");
            lotteried.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.card));
            lottery_text.setImageBitmap(bmp4);
        }else if(i==2) {
            text1.setText("マジックハンドGET！");
            lotteried.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.magichand));
            lottery_text.setImageBitmap(bmp4);
        }else if(i==3) {
            text1.setText("シールドGET!");
            lotteried.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.shield));
            lottery_text.setImageBitmap(bmp4);
        }else {
            text1.setText("アイテム獲得ならず.....");
            bmp4= BitmapFactory.decodeResource(getResources(),R.drawable.gagan);
            //lotteried.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.gagan));
            lotteried.setImageBitmap( Bitmap.createScaledBitmap(bmp4, 500, 500, false));
=======
    void mainToReturn() {
        //もし表示されてないなら表示する
        if (item.getVisibility() != View.VISIBLE) {
            TypedArray typedArray = getResources().obtainTypedArray(R.array.item);
            int i = (int) (Math.floor(Math.random() * item_sum));
            Drawable drawable = typedArray.getDrawable(i);
            item.setImageDrawable(drawable);
            item.setVisibility(View.VISIBLE);
            if (i == 0) result.setText("爆弾GET！");
            else if (i == 1) result.setText("2倍カードGET!");
            else if (i == 2) result.setText("マジックハンドGET！");
            else result.setText("シールドGET!");

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            }, 2500);
>>>>>>> master
        }
        lotteried.setVisibility(View.VISIBLE);
        lottery_text.setVisibility(View.VISIBLE);
        final Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent=new Intent(getApplication(),MainActivity.class);
                startActivity(intent);
            }
        },2500);



    }

}


