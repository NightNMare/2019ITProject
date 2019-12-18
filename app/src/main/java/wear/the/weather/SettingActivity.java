package wear.the.weather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

public class SettingActivity extends AppCompatActivity {

    Switch swtich_textcolor;
    TextView cur_color,tem_setting;
    boolean widget_color;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        swtich_textcolor=findViewById(R.id.widget_color_switch);
        cur_color=findViewById(R.id.current_color);
        tem_setting=findViewById(R.id.tem_setting);

        prefs = getSharedPreferences("isBlack",MODE_PRIVATE);
        editor= prefs.edit();
        widget_color=prefs.getBoolean("isBlack",false);

        //쉐어드에서 widget color 가져오기
        if(widget_color){
            cur_color.setText("현재: Black");
            swtich_textcolor.setChecked(true);
        } else{
            cur_color.setText("현재: White");
            swtich_textcolor.setChecked(false);
        }
        swtich_textcolor.setChecked(widget_color);

        swtich_textcolor.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                widget_color = b;
                if(widget_color){
                    cur_color.setText("현재: Black");
                    editor.putBoolean("isBlack",true);
                } else{
                    cur_color.setText("현재: White");
                    editor.putBoolean("isBlack",false);
                }
                editor.apply();
                //쉐어드에 저장하기
            }
        });
        tem_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingActivity.this,TemperatureSetting.class);
                startActivity(intent);
            }
        });
    }
}
