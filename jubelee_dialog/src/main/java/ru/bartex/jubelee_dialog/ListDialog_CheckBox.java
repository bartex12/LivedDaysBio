package ru.bartex.jubelee_dialog;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.bartex.jubelee_dialog.ru.bartex.jubelee_dialog.data.PersonDbHelper;
import ru.bartex.jubelee_dialog.ru.bartex.jubelee_dialog.data.PersonTable;

public class ListDialog_CheckBox extends AppCompatActivity {

    public final String TAG = "33333";
    ListView mListView;
    private SharedPreferences prefSetting;
    int sort = 1;  //Сортировка: 1-поимени возр, 2- по имени убыв,3-по дате возр, 4 - по дате убыв
    boolean isSort = false; //Список отсортирован?
    Button createList;

    ArrayList<Person> mPersonArrayList = new ArrayList<Person>();
    FindAdapterPerson findAdapter;
    ArrayList<Map<String,Object>> data = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_dialog__check_box);
        Log.d(TAG, "ListDialog_CheckBox onCreate");
        //получаем файл с настройками для приложения
        prefSetting = PreferenceManager.getDefaultSharedPreferences(this);
        sort = Integer.parseInt(prefSetting.getString("ListSort", "1"));
        isSort = prefSetting.getBoolean("cbSort", false);

        createList = (Button) findViewById(R.id.toggleButton_createList);
        createList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // http://www.easyinfogeek.com/2014/01/android-tutorial-two-methods-of-passing.html
                ArrayList<Person> pp = findAdapter.getCheckedPersonList();
                //если длина списка = 0, тост, что ничего не выбрано
                if (pp.size() == 0) {
                    myToast("Ничего не выбрано");
                }else {
                    Intent intent = new Intent(ListDialog_CheckBox.this, FindDatesActivity.class);
                    Bundle mChecked = new Bundle();
                    mChecked.putSerializable(FindDatesActivity.LINE_CHECKED, pp);
                    intent.putExtras(mChecked);
                    startActivity(intent);
                }
            }
        });

        mListView = (ListView) findViewById(R.id.listViewDialog_CheckBOx);
        //находим View, которое выводит текст Список пуст
        View empty = findViewById(R.id.emptyList);
        mListView.setEmptyView(empty);

        //получаем экземпляр PersonDbHelper для работы с базой данных
        PersonDbHelper mPersonDbHelper = new PersonDbHelper(this);
        //получаем список объектов
        mPersonArrayList = mPersonDbHelper.getAllContactsChoose();
        //подключаем свой адаптер для отображения на экране
        findAdapter = new FindAdapterPerson(this,mPersonArrayList);
        mListView.setAdapter(findAdapter);

    }

    void myToast (String s){
        Toast mToast = Toast.makeText(ListDialog_CheckBox.this,s, Toast.LENGTH_SHORT);
        mToast.setGravity(Gravity.CENTER,0,0);
        mToast.show();
    }

}
