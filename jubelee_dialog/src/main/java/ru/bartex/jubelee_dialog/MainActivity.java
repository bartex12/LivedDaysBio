package ru.bartex.jubelee_dialog;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ru.bartex.jubelee_dialog.ru.bartex.jubelee_dialog.data.P;
import ru.bartex.jubelee_dialog.ru.bartex.jubelee_dialog.data.PersonDbHelper;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public final String TAG = "33333";

    ListView mListView;
    String[] stringListMain;
    String[] stringListSubMain;
    int array_size; //размер массива строк для списка в MainActivity
    int[] idPicture = {R.drawable.tort,R.drawable.two_dates,R.drawable.bioritm};

    SimpleAdapter sara;
    ArrayList<Map<String, Object>> data;
    Map<String,Object> m;

    final String ATTR_PICTURE = "PICTURE";
    final String ATTR_BASE_TEXT = "BASE_TEXT";
    final String ATTR_SUB_TEXT = "SUB_TEXT";

    static final int request_code = 11;// для newActivity от плавающей кнопки

    //создаём базу данных, если ее не было
    PersonDbHelper mDbHelper = new PersonDbHelper(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "MainActivity onCreate");

        //если в базе нет записей, добавляем одну с анжелиной джоли
        mDbHelper.createDefaultPersonIfNeed();
        // Обновляем данные в столбце Количество прожитых дней
        mDbHelper.updatePastDays();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mListView = findViewById(R.id.listView);
        //адаптер  - в onResume
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                // 0-Личные даты
                // 1- Совместные даты
                // 2 - Биоритмы
                // 3 - совместимость биоритмов (резерв)
                if (i == 0) {
                    Intent intent = new Intent(MainActivity.this, PersonsListActivity.class);
                    intent.putExtra(P.FROM_MAIN, P.TO_ONE_DATE);
                    startActivity(intent);
                }else if(i == 1){
                    Intent intent = new Intent(MainActivity.this, ListDialog_CheckBox.class);
                    //intent.putExtra(SingleFragmentActivity.FROM_ACTIVITY,MAIN_ACTIVITY);
                    //intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent);
                }else if(i == 2){
                    //вариант 1, когда просто список, в котором имя, дата и тип а одной строке
                    //Intent intent = new Intent(MainActivity.this, ListOfFilesActivity.class);
                    //Вариант 2 на основе Tab? что устарело, хотя и работает
                    //Intent intent = new Intent(MainActivity.this, TabActivity.class);
                    //Вариант 3 на основе TabBar с ViewPager и фрагментами
                    Log.d(TAG, "MainActivity onCreate onItemClick P.TO_BIORITM = " + P.TO_BIORITM);
                    Intent intent = new Intent(MainActivity.this, PersonsListActivity.class);
                    intent.putExtra(P.FROM_MAIN, P.TO_BIORITM);
                    //intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent);
                }
            }
        });


        FloatingActionButton fab =  findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                 //       .setAction("Action", null).show();

                //создаём новую запись
                Intent intentAdd = new Intent(MainActivity.this, NewActivity.class);
                intentAdd.putExtra(NewActivity.REQUEST_CODE, request_code);
                startActivity(intentAdd);
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //получаем массив строк из строковых ресурсов
        stringListMain =  getResources().getStringArray(R.array.MenuMain);
        //получаем массив строк из строковых ресурсов
        stringListSubMain =  getResources().getStringArray(R.array.MenuSubMain);
        //получаем размер списка
        array_size = stringListMain.length;
        //готовим данные для SimpleAdapter
        data = new ArrayList<Map<String, Object>>(array_size);
        for (int i = 0; i<array_size; i++){

            m = new HashMap<>();
            m.put(ATTR_BASE_TEXT,stringListMain[i]);
            m.put(ATTR_SUB_TEXT,stringListSubMain[i]);
            m.put(ATTR_PICTURE,idPicture[i]);
            data.add(m);
        }
        String[] from = {ATTR_PICTURE, ATTR_BASE_TEXT, ATTR_SUB_TEXT};
        int[] to = {R.id.picture, R.id.base_text,R.id.sub_text};
        sara = new SimpleAdapter(this, data, R.layout.list_item, from, to);
        mListView.setAdapter(sara);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //super.onBackPressed();
            openQuitDialog();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_drower, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            case R.id.action_settings:
                Log.d(TAG, "OptionsItem = action_settings");
                Intent intentSettings = new Intent(this, PrefActivity.class);
                startActivity(intentSettings);
                return true;

            case R.id.action_help_main:
                Log.d(TAG, "OptionsItem = action_help_main");
                Intent intentHelpMain = new Intent(this, HelpActivity.class);
                startActivity(intentHelpMain);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_oneDate) {
            Intent intent = new Intent(MainActivity.this, PersonsListActivity.class);
            intent.putExtra(P.FROM_MAIN, P.TO_ONE_DATE);
            startActivity(intent);

        } else if (id == R.id.nav_twoDate) {
            Intent intent = new Intent(MainActivity.this, ListDialog_CheckBox.class);
            startActivity(intent);

        } else if (id == R.id.nav_bio) {
            Log.d(TAG, "MainActivity onCreate onItemClick P.TO_BIORITM = " + P.TO_BIORITM);
            Intent intent = new Intent(MainActivity.this, PersonsListActivity.class);
            intent.putExtra(P.FROM_MAIN, P.TO_BIORITM);
            startActivity(intent);

        } else if (id == R.id.nav_manage) {
            Intent intentSettings = new Intent(this, PrefActivity.class);
            startActivity(intentSettings);

        } else if (id == R.id.nav_share) {
            //поделиться - передаём ссылку на приложение в маркете
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT,
                    "Приложение для расчёта количества прожитых/совместно прожитых дней: " +
                            "https://play.google.com/store/apps/details?id=" + getPackageName());
            //sendIntent.putExtra(Intent.EXTRA_TEXT,
             //       "Приложение для расчёта количества прожитых/совместно прожитых дней: " +
              //      "https://play.google.com/store/apps/details?id=" +
              //      "ru.bartex.jubelee_dialog_singllist");
            sendIntent.setType("text/plain");
            startActivity(sendIntent);

        } else if (id == R.id.nav_send) {
            //оценить- попадаем на страницу приложения в маркете
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(
                    "http://play.google.com/store/apps/details?id=" + getPackageName()));
            //intent.setData(Uri.parse(
            //        "http://play.google.com/store/apps/details?id=ru.bartex.jubelee_dialog_singllist"));

            startActivity(intent);
            /*
            //оценить- попадаем на страницу приложения в маркете
            Intent intent1 = new Intent(Intent.ACTION_VIEW);
            //intent1.setData(Uri.parse("market://details?id=" + getPackageName()));
            intent1.setData(Uri.parse("market://details?id = ru.bartex.jubelee_dialog_singllist"));
            startActivity(intent1);
*/
        }else if (id == R.id.nav_help){
            Intent intentHelp = new Intent(this, HelpActivity.class);
            intentHelp.putExtra(P.HELP_FROM, P.HELP_ALL);
            startActivity(intentHelp);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //Создать и открыть диалог выхода из программы
    private void openQuitDialog() {
        AlertDialog.Builder quitDialog = new AlertDialog.Builder(this);
        quitDialog.setTitle("Выход: Вы уверены?");

        quitDialog.setPositiveButton("Нет", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        quitDialog.setNegativeButton("Да", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();  //нельзя, так как в стеке возврата BioritmActivity
                //system.exit(0);
                //finishAffinity();  // с API 16
                //System.runFinalizersOnExit(true);
                //System.exit(0);
            }
        });

        quitDialog.show();
    }
}
