package cn.vove7.cthelper;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;


import cn.vove7.cthelper.openct.utils.JsonUtils;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
   @Test
   public void addition_isCorrect() {
      assertEquals(4, 2 + 2);

      //String data="[{'haust': '河南科技大学'}]";
      //Map<String,String> m=JsonUtils.json2Map(data);
      //System.out.println(m);

      Map<String,String> m=new HashMap<>();
      m.put("1","2");
      m.put("3","4");
      System.out.println(JsonUtils.INSTANCE.toJson(m));

   }
}