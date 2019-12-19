package wear.the.weather;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import java.util.zip.Inflater;

public class TemperatureSetting extends AppCompatActivity {

    ImageView EDT_verycold, EDT_cold, EDT_cool, EDT_mild, EDT_littlehot, EDT_hot;
    TextView verycold, cold, cool, mild, littlehot, hot;
    int int_verycold, int_cold, int_cool, int_mild, int_littlehot, int_hot;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    CustomDialog cd;
    LinearLayout ll;
    MyCanvas ov;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temperature_setting);
        EDT_verycold = findViewById(R.id.verycoldedit);
        EDT_cold = findViewById(R.id.coldedit);
        EDT_cool = findViewById(R.id.cooledit);
        EDT_mild = findViewById(R.id.mildedit);
        EDT_littlehot = findViewById(R.id.littlehotedit);
        EDT_hot = findViewById(R.id.hotedit);
        verycold = findViewById(R.id.verycold);
        cold = findViewById(R.id.cold);
        cool = findViewById(R.id.cool);
        mild = findViewById(R.id.mild);
        littlehot = findViewById(R.id.littlehot);
        hot = findViewById(R.id.hot);
        ll = findViewById(R.id.ll);


        prefs = getSharedPreferences("Temperature", MODE_PRIVATE);
        editor = prefs.edit();
        int_verycold = prefs.getInt("verycold", 5);
        int_cold = prefs.getInt("cold", 10);
        int_cool = prefs.getInt("cool", 15);
        int_mild = prefs.getInt("mild", 23);
        int_littlehot = prefs.getInt("littlehot", 25);
        int_hot = prefs.getInt("hot", 50);
        //온도 텍스트 설정
        verycold.setText(int_verycold + "º이하");
        cold.setText(int_verycold + "º~" + int_cold + "º");
        cool.setText(int_cold + "º~" + int_cool + "º");
        mild.setText(int_cool + "º~" + int_mild + "º");
        littlehot.setText(int_mild + "º~" + int_littlehot + "º");
        hot.setText(int_littlehot + "º이상");

        cd = new CustomDialog(this);
        WindowManager.LayoutParams wm = cd.getWindow().getAttributes();  //다이얼로그의 높이 너비 설정하기위해
        wm.copyFrom(cd.getWindow().getAttributes());  //여기서 설정한값을 그대로 다이얼로그에 넣겠다는의미
        DisplayMetrics dm = this.getResources().getDisplayMetrics(); //디바이스 화면크기를 구하기위해
        int width = dm.widthPixels; //디바이스 화면 너비
        int height = dm.heightPixels;
        wm.width = width / 3 * 2;  //화면 너비의 절반
        wm.height = height / 3 * 2;

        ov = new MyCanvas(this);
        ll.addView(ov);

        EDT_verycold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenDialog(-20, int_verycold, "verycold");
            }
        });
        EDT_cold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenDialog(int_verycold, int_cold, "cold");
            }
        });
        EDT_cool.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenDialog(int_cold, int_cool, "cool");
            }
        });
        EDT_mild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenDialog(int_cool, int_mild, "mild");
            }
        });
        EDT_littlehot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenDialog(int_mild, int_littlehot, "littlehot");
            }
        });
        EDT_hot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenDialog(int_littlehot, int_hot, "hot");
            }
        });

    }

    void OpenDialog(int mintmp, int maxtmp, final String target) {
        cd.minPicker.setEnabled(true);
        cd.maxPicker.setEnabled(true);
        if (target.equals("verycold")) {
            cd.minPicker.setEnabled(false);
        } else if (target.equals("hot")) {
            cd.maxPicker.setEnabled(false);
        }
        cd.minPicker.setValue(mintmp + 20);
        cd.maxPicker.setValue(maxtmp + 20);
        cd.int_min = mintmp + 20;
        cd.int_max = maxtmp + 20;
        cd.show();

        cd.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int min = cd.int_min - 20;
                int max = cd.int_max - 20;
                editor.putInt(target, max);
                editor.apply();
                if (target.equals("verycold")) {
                    verycold.setText(max + "º이하");
                    int_verycold = max;
                    cold.setText(int_verycold + "º~" + int_cold + "º");
                } else if (target.equals("cold")) {
                    cold.setText(min + "º~" + max + "º");
                    int_cold = max;
                    cool.setText(int_cold + "º~" + int_cool + "º");
                    int_verycold = min;
                    verycold.setText(int_verycold + "º이하");
                } else if (target.equals("cool")) {
                    cool.setText(min + "º~" + max + "º");
                    int_cool = max;
                    mild.setText(int_cool + "º~" + int_mild + "º");
                    int_cold = min;
                    cold.setText(int_verycold + "º~" + int_cold + "º");
                } else if (target.equals("mild")) {
                    mild.setText(min + "º~" + max + "º");
                    int_mild = max;
                    littlehot.setText(int_mild + "º~" + int_littlehot + "º");
                    int_cool = min;
                    cool.setText(int_cold + "º~" + int_cool + "º");
                } else if (target.equals("littlehot")) {
                    littlehot.setText(min + "º~" + max + "º");
                    int_littlehot = max;
                    hot.setText(int_littlehot + "º이상");
                    int_mild = min;
                    mild.setText(int_cool + "º~" + int_mild + "º");
                } else if (target.equals("hot")) {
                    hot.setText(min + "º이상");
                    int_littlehot = min;
                    littlehot.setText(int_mild + "º~" + int_littlehot + "º");
                }
                ov.invalidate();
                cd.dismiss();
            }
        });
    }

    class CustomDialog extends Dialog {
        Button save, cancel;
        int int_min = 0, int_max = 0;
        NumberPicker minPicker, maxPicker;

        public CustomDialog(Context context) {
            super(context);
            requestWindowFeature(Window.FEATURE_NO_TITLE);   //다이얼로그의 타이틀바를 없애주는 옵션입니다.
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));  //다이얼로그의 배경을 투명으로 만듭니다.
            setContentView(R.layout.customdialog);     //다이얼로그에서 사용할 레이아웃입니다.
            save = findViewById(R.id.save);
            cancel = findViewById(R.id.cancel);
            minPicker = findViewById(R.id.minpicker);
            maxPicker = findViewById(R.id.maxpicker);

            minPicker.setMinValue(0);
            minPicker.setMaxValue(70);
            maxPicker.setMinValue(0);
            maxPicker.setMaxValue(70);
            minPicker.setWrapSelectorWheel(true);
            maxPicker.setWrapSelectorWheel(true);

            minPicker.setFormatter(new NumberPicker.Formatter() {
                @Override
                public String format(int i) {
                    return Integer.toString(i - 20);
                }
            });
            maxPicker.setFormatter(new NumberPicker.Formatter() {
                @Override
                public String format(int i) {
                    return Integer.toString(i - 20);
                }
            });

            minPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                @Override
                public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                    int_min = i1;
                    Log.e("qwe", i1 + "");
                }
            });
            maxPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                @Override
                public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                    int_max = i1;
                    Log.e("qwe1", i1 + "");
                }
            });

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cd.dismiss();
                }
            });

        }

    }

    public class MyCanvas extends View {

        public MyCanvas(Context context) {
            super(context);
        }

        public MyCanvas(Context context, @Nullable AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            Paint paint = new Paint();
            int color_black = 0x000000;
            int colors;
            float x = getWidth();
            float datax = x / 40;
            float curx = 0;
            float y = getHeight();
            colors = Color.parseColor("#0d05ff");//0x0d05ff;//verycolod

            paint.setColor(colors);
            paint.setStrokeWidth(15.0f);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeJoin(Paint.Join.MITER);
            canvas.drawLine(curx, y / 2, datax * (10 + int_verycold), y / 2, paint);
            Log.e("qwe",datax+" "+ (10 + int_verycold));
            curx += datax * (10 + int_verycold);

            colors = Color.parseColor("#0565ff");//cold
            paint.setColor(colors);
            canvas.drawLine(curx, y / 2, curx + datax * (int_cold - int_verycold), y / 2, paint);
            curx += datax * (int_cold - int_verycold);

            colors = Color.parseColor("#00dbcc");//cool
            paint.setColor(colors);
            canvas.drawLine(curx, y / 2, curx + datax * (int_cool - int_cold), y / 2, paint);
            curx += datax * (int_cool - int_cold);

            colors = Color.parseColor("#33db00");//mild
            paint.setColor(colors);
            canvas.drawLine(curx, y / 2, curx + datax * (int_mild - int_cool), y / 2, paint);
            curx += datax * (int_mild - int_cool);
            //점

            colors = Color.parseColor("#e5c700");//littlehot
            paint.setColor(colors);
            canvas.drawLine(curx, y / 2, curx + datax * (int_littlehot - int_mild), y / 2, paint);
            curx += datax * (int_littlehot - int_mild);
            //점

            colors = Color.parseColor("#e55400");//hot
            paint.setColor(colors);
            canvas.drawLine(curx, y / 2, curx + datax * (45 - int_littlehot), y / 2, paint);

            curx = 0;
            paint.setColor(Color.BLACK);
            curx += datax * (10+int_verycold);
            canvas.drawCircle(curx, y / 2, 10.0f, paint);
            curx +=datax * (int_cold - int_verycold);
            canvas.drawCircle(curx, y / 2, 10.0f, paint);
            curx += datax * (int_cool - int_cold);
            canvas.drawCircle(curx, y / 2, 10.0f, paint);
            curx += datax * (int_mild - int_cool);
            canvas.drawCircle(curx, y / 2, 10.0f, paint);
            curx += datax * (int_littlehot - int_mild);
            canvas.drawCircle(curx, y / 2, 10.0f, paint);

        }

    }
/*    public class DrawingView extends View {
        Paint paint;
        private void init(){
            paint = new Paint();
        }
        public DrawingView(Context context) {
            super(context);
            init();
        }

        public DrawingView(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
            init();

        }

        public DrawingView(Context context, AttributeSet attrs) {
            super(context, attrs);
            init();

        }

        @Override
        protected void onDraw(Canvas canvas) {
            int color_black = 0x000000;
            int colors;
            float x = getWidth();
            float datax = x / 70;
            float curx = 0;
            float y = getHeight();
            colors = 0x0d05ff;//verycolod
            paint.setColor(colors);
            paint.setcolor

//            paint.setStrokeWidth(100.0f);
//            paint.setStyle(Paint.Style.STROKE);
//            paint.setStrokeJoin(Paint.Join.MITER);

//            canvas.drawLine(curx,y/2,datax*(20+int_verycold),y/2,paint);
//            canvas.drawLine(0, 50, 500, 50, paint);
            canvas.drawRect(5,5,100,100,paint);
//            colors = 0x0565ff;//cold
//            paint.setColor(colors);
//
//            colors = 0x00dbcc;//cool
//            paint.setColor(colors);
//
//            colors = 0x33db00;//mild
//            paint.setColor(colors);
//
//            colors = 0xe5c700;//littlehot
//            paint.setColor(colors);
//
//            colors = 0xe55400;//hot
//            paint.setColor(colors);


//            paint.setAntiAlias(true);
            //원
//            canvas.drawCircle(240, y / 2, 70, paint);
        }
    }*/
}
