package cn.vove7.cthelper;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import cn.vove7.cthelper.fragments.MainFragment;

public class MessageHandler extends Handler {

   public static final String KEY_CONTENT="content";
   public static final String KEY_TEXTVIEW_ID="tv_id";
   public static final int WHAT_UPGRADE_TEXTVIEW = 0x10000000;
   public static final int WHAT_CLOSE_PROCESS = 0x20000000;
   private Context context;
   private MainFragment fragment;
   public MessageHandler(Context context) {

      this.context = context;
   }

   public MessageHandler(MainFragment fragment) {
      this.fragment = fragment;
   }

   @Override
   public void handleMessage(Message msg) {
      switch (msg.what){

         case WHAT_UPGRADE_TEXTVIEW:
            upgradeUI(msg);
            break;
         case WHAT_CLOSE_PROCESS:
            fragment.closeProgressDialog();
            break;
      }

   }
   private void upgradeUI(Message msg) {
      Bundle bundle=msg.getData();
      TextView textView=((AppCompatActivity)context).findViewById(bundle.getInt(KEY_TEXTVIEW_ID));
      textView.setText(bundle.getString(KEY_CONTENT));
   }
}
