package cn.vove7.cthelper;

import android.app.Application;

import org.litepal.LitePal;

import cn.vove7.cthelper.openct.adapter.SchoolAdapter;

public class VApplication extends Application {
   static SchoolAdapter schoolAdapter;
   static VApplication vApplication;

   public static VApplication getInstance() {
      return vApplication;
   }

   public static SchoolAdapter getSchoolAdapter() {
      return schoolAdapter;
   }

   public void setSchoolAdapter(SchoolAdapter schoolAdapter) {
      this.schoolAdapter = schoolAdapter;
   }

   @Override
   public void onCreate() {
      vApplication = this;
      LitePal.initialize(this);
      super.onCreate();
   }
}
