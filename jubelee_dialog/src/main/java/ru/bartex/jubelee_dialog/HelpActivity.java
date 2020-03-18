package ru.bartex.jubelee_dialog;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import ru.bartex.jubelee_dialog.ru.bartex.jubelee_dialog.data.P;

public class HelpActivity extends AppCompatActivity {


    private static final String TAG = "33333";
    TextView tvHelp;
    ImageView left;
    ImageView right;

    int from;  //откуда пришел запрос на справку
    int resurs;  //ресурс справки

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_main);

        Intent intent = getIntent();
        from = intent.getIntExtra(P.HELP_FROM, P.HELP_FROM_MAIN);

        ActionBar acBar = getSupportActionBar();
        acBar.setTitle("");
        //показать стрелку Назад
        acBar.setDisplayHomeAsUpEnabled(true );
        acBar.setHomeButtonEnabled(true);

        tvHelp = findViewById(R.id.textViewHelpMain);

        //получаем ссылку на файл справки в зависимости от того, откуда пришёл запрос
        switch (from){
            case P.HELP_FROM_MAIN:
                resurs = R.raw.help_main_activity;
               break;
            case P.HELP_FROM_LIST_PERSONS:
                resurs = R.raw.help_personal_list_activity;
                break;
            case P.HELP_FROM_BIORITM:
                resurs = R.raw.help_bioritm_activity;
                break;
            case P.HELP_FROM_TIME:
                resurs = R.raw.help_time_activity;
                break;
            case P.HELP_FROM_TABLE:
                resurs = R.raw.help_table_activity;
                break;
            case P.HELP_FROM_JOINT:
                resurs = R.raw.help_joint_activity;
                break;
            case P.HELP_FROM_FIND_DATE:
                resurs = R.raw.help_find_date_activity;
                break;
            case P.HELP_FROM_NEW_PERSON:
                resurs = R.raw.help_new_person_activity;
                break;
            case P.HELP_ALL:
                resurs = R.raw.help_main_all;
                break;
        }

        //используем файл справки для вывода на экран
        InputStream iFile = getResources().openRawResource(resurs);
        StringBuilder strFile = inputStreamToString(iFile);
        tvHelp.setText(strFile);

        left = findViewById(R.id.imageView2);
        left.setImageResource(R.drawable.help_magistr);

        right = findViewById(R.id.imageView3);
        right.setImageResource(R.drawable.help_magistr);
    }

    private StringBuilder inputStreamToString(InputStream iFile) {
        StringBuilder strFull = new StringBuilder();
        String str;
        try {
            // открываем поток для чтения
            InputStreamReader ir = new InputStreamReader(iFile);
            BufferedReader br = new BufferedReader(ir);
            // читаем содержимое
            while ((str = br.readLine()) != null) {
                //Log.d(TAG, str);
                //Чтобы не было в одну строку, ставим символ новой строки
                strFull.append(str + "\n");
            }
            //закрываем потоки
            iFile.close();
            ir.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return strFull;
    }

    //отслеживание нажатия кнопки HOME
    @Override
    protected void onUserLeaveHint() {

        //Toast toast = Toast.makeText(getApplicationContext(), "onUserLeaveHint", Toast.LENGTH_SHORT);
        //toast.show();
        //включаем звук
        AudioManager audioManager =(AudioManager)getSystemService(Context.AUDIO_SERVICE);
        if (audioManager!=null){
            audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
        }


        super.onUserLeaveHint();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_help_main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){

            //чтобы работала стрелка Назад, а не происходил крах приложения
            case android.R.id.home:
                Log.d(TAG, "Домой");
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
