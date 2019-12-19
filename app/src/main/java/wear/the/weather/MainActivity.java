package wear.the.weather;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    final String path = "https://search.naver.com/search.naver?sm=top_hty&fbm=0&ie=utf8&query=%EC%84%9C%EC%9A%B8+%EB%82%A0%EC%94%A8";
    final String path2 = "https://www.google.com/search?q=%EC%84%9C%EC%9A%B8+%EB%82%A0%EC%94%A8&oq=%EC%84%9C%EC%9A%B8+%EB%82%A0%EC%94%A8&aqs=chrome..69i57j69i59j69i60l2j69i61.1506j0j7&sourceid=chrome&ie=UTF-8";
    Handler handler = new Handler();
    String datas;
    String tmpweather;
//    TextView tv;
    TextView date,cur_tmp,sstmp,min_max,talk,uv;
    String str_date,str_curtmp,str_sstmp,str_min_max,str_talk,str_uv;
    ImageView img, setting;
    SwipeRefreshLayout SRL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        tv = findViewById(R.id.text);
        img = findViewById(R.id.img);
        setting = findViewById(R.id.setting);
        date=findViewById(R.id.date);
        cur_tmp=findViewById(R.id.cur_tmp);
        sstmp=findViewById(R.id.sstmp);
        min_max=findViewById(R.id.min_max);
        talk=findViewById(R.id.talk);
        uv=findViewById(R.id.uv);
        SRL = findViewById(R.id.SRL);

        SRL.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
//                tv.setText("Refreshing");
                GetData();
            }
        });
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //셋팅 이벤트
                Intent intent = new Intent(MainActivity.this,SettingActivity.class);
                startActivity(intent);
            }
        });
        getDate();
        GetData();
    }
    private void getDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Date date = new Date();
        str_date = sdf.format(date);
    }

    void GetData() {
        new Thread() {
            public void run() {
                try {
                    Document doc = Jsoup.connect(path).get();
                    Document doc2 = Jsoup.connect(path2).get();

                    Element todayweather = doc2.getElementById("wob_tci");

                    Element tem = doc.getElementsByClass("todaytemp").get(0);
                    Element mintem = doc.getElementsByClass("min").get(0).child(0);
                    Element maxtem = doc.getElementsByClass("max").get(0).child(0);
                    Element sensibletem = doc.getElementsByClass("sensible").get(0).child(0);
                    Element lv1 = doc.getElementsByClass("lv1").get(0);
                    Element cast_txt = doc.getElementsByClass("cast_txt").get(0);//℃

//                    weather = todayweather.attr("alt");
                    tmpweather = todayweather.attr("src");
                    tmpweather = "https:"+tmpweather;
                    datas = "tmp:"+tem.text()+
                            "\nmintmp:"+mintem.text()+
                            "\nmaxtmp:"+maxtem.text()+
                            "\nsstmp:"+sensibletem.text()+
                            "\nlv1:"+lv1.text()+
                            "\ncast_txt:"+cast_txt.text()+
                            "\n"+todayweather.attr("alt")+
                            "\n";
                    str_curtmp = tem.text()+"℃";
                    str_sstmp = "체감온도: "+sensibletem.text();
                    str_min_max = mintem.text()+"º / "+maxtem.text()+"º";
                    str_talk = cast_txt.text().split(",")[1];
                    str_uv = lv1.text().substring(lv1.text().length()-2)+" ("+lv1.text().substring(0,lv1.text().length()-2)+")";
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("asd", e.getMessage());
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
//                        tv.setText(datas);
                        /*switch(weather){
                            case "소나기(약함)":
                            case "소나기": img.setImageResource(R.drawable.rain1); break;
                            case "흐리고 한때 비": img.setImageResource(R.drawable.rain_cloud); break;
                            case "대체로 흐림":
                            case "구름 조금":
                            case "대체로 맑음": img.setImageResource(R.drawable.cloudy_);break;
                            case "맑음" : img.setImageResource(R.drawable.sunny);break;
                            case "맑으나 때때로 구름" : img.setImageResource(R.drawable.sunny_cloudy);break;
                            case "눈": img.setImageResource(R.drawable.snow);break;
                            case "소낙눈" : img.setImageResource(R.drawable.snow2);break;
                            case "비와 눈": img.setImageResource(R.drawable.rain_snow);break;
                            case "광역성 소나기":
                            case "광역성 뇌우": img.setImageResource(R.drawable.rain4); break;
                            case "안개": img.setImageResource(R.drawable.fog);break;
                            case "비":img.setImageResource(R.drawable.rain);break;
                            case "흐리고 때때로 갬":img.setImageResource(R.drawable.cloudy_sunny);break;
                            case "흐림": img.setImageResource(R.drawable.cloudy);break;
                        }*/
                        Glide.with(getApplicationContext()).load(tmpweather).into(img);
                        cur_tmp.setText(str_curtmp);
                        sstmp.setText(str_sstmp);
                        min_max.setText(str_min_max);
                        talk.setText(str_talk);
                        uv.setText(str_uv);
                        date.setText(str_date.substring(0,4)+"년 "+str_date.substring(4,6)+"월 "+str_date.substring(6)+"일");
                        SRL.setRefreshing(false);
                    }
                });
            }
        }.start();
    }

}
