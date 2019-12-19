package wear.the.weather;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.AppWidgetTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Implementation of App Widget functionality.
 */
public class NewAppWidget extends AppWidgetProvider {
    static String date, etc, temp;
    private static final String ACTION_BUTTON1 = "com.example.gyu_won.lunch_for_sunrin.Refresh";
    static String img=null;
    static SharedPreferences prefs;
    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {


        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);

        views.setTextViewText(R.id.widget_date, date);
        views.setTextViewText(R.id.widget_etc, etc);
        views.setTextViewText(R.id.widget_temp, temp);
        AppWidgetTarget target = new AppWidgetTarget(context, R.id.widget_img, views, appWidgetId);
        Glide.with(context).asBitmap().centerCrop().load(img).into(target);
        prefs = context.getSharedPreferences("isBlack", Context.MODE_PRIVATE);
        if(prefs.getBoolean("isBlack",false)){//widget_temp widget_date
            views.setTextColor(R.id.widget_etc, Color.BLACK);
            views.setTextColor(R.id.widget_temp, Color.BLACK);
            views.setTextColor(R.id.widget_date, Color.BLACK);
        }else{
            views.setTextColor(R.id.widget_etc, Color.WHITE);
            views.setTextColor(R.id.widget_temp, Color.WHITE);
            views.setTextColor(R.id.widget_date, Color.WHITE);
        }
        Intent intentSync = new Intent(context, NewAppWidget.class);
        intentSync.setAction(ACTION_BUTTON1); //You need to specify the action for the intent. Right now that intent is doing nothing for there is no action to be broadcasted.

        PendingIntent pendingSync = PendingIntent.getBroadcast(context, 0, intentSync, PendingIntent.FLAG_UPDATE_CURRENT); //You need to specify a proper flag for the intent. Or else the intent will become deleted.
        views.setOnClickPendingIntent(R.id.button, pendingSync);

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName thisAppWidget = new ComponentName(context.getPackageName(), NewAppWidget.class.getName());
        int[] appWidgets = appWidgetManager.getAppWidgetIds(thisAppWidget);

//        String action = intent.getAction();
        if (intent.getAction().equals(ACTION_BUTTON1)) {
            onUpdate(context, appWidgetManager, appWidgets);
        }
        super.onReceive(context, intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        Log.e("1", "1");
        update u = new update(context, appWidgetManager, appWidgetIds);
        u.thread.start();
//        for (int appWidgetId : appWidgetIds) {
//            updateAppWidget(context, appWidgetManager, appWidgetId);
//        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }


    private static class update {
        Context context;
        AppWidgetManager appWidgetManager;
        int[] appWidgetIds;

        update(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
            this.context = context;
            this.appWidgetIds = appWidgetIds;
            this.appWidgetManager = appWidgetManager;
        }

        Handler handler = new Handler();


        Thread thread = new Thread() {
            public void run() {
                for (int appWidgetId : appWidgetIds) {
                    updateAppWidget(context, appWidgetManager, appWidgetId);
                }
                SimpleDateFormat mFormat = new SimpleDateFormat("yyyyMMdd");
                long mNow = System.currentTimeMillis();
                Date mDate = new Date(mNow);
                date = mFormat.format(mDate);

                final String path = "https://search.naver.com/search.naver?sm=top_hty&fbm=0&ie=utf8&query=%EC%84%9C%EC%9A%B8+%EB%82%A0%EC%94%A8";
                final String path2 = "https://www.google.com/search?q=%EC%84%9C%EC%9A%B8+%EB%82%A0%EC%94%A8&oq=%EC%84%9C%EC%9A%B8+%EB%82%A0%EC%94%A8&aqs=chrome..69i57j69i59j69i60l2j69i61.1506j0j7&sourceid=chrome&ie=UTF-8";

                try {
                    Document doc = Jsoup.connect(path).get();
                    Document doc2 = Jsoup.connect(path2).get();

                    Element todayweather = doc2.getElementById("wob_tci");

                    Element tem = doc.getElementsByClass("todaytemp").get(0);
                    Element cast_txt = doc.getElementsByClass("cast_txt").get(0);//℃

                    img = "https:"+todayweather.attr("src");
                    temp = tem.text()+"℃";
//                            str_curtmp = tem.text();
//                            str_talk = cast_txt.text().split(",")[1];
                    etc = cast_txt.text().split(",")[1];//==========================================옷 차림으로 바꾸기
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("asd", e.getMessage());
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
//                        GetData(context, appWidgetManager, appWidgetIds);
                        date = date.substring(0, 4) + "년 " + date.substring(4, 6) + "월 " + date.substring(6) + "일";
                        for (int appWidgetId : appWidgetIds) {
                            updateAppWidget(context, appWidgetManager, appWidgetId);
                        }
                    }
                });
            }
        };
    }
}
