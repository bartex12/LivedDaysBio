package ru.bartex.jubelee_dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;


//свой адаптер пришлось сделать для того, чтобы можно было отмечать чекбоксы выбора строк в списке,
// поскольку слушатель чекбокса находится в адаптере

public class FindAdapterPerson extends BaseAdapter {
    public static final String TAG = "33333";
    Context ctx;
    LayoutInflater mLayoutInflater;
    ArrayList<Person> mPersonList;

    TextView name;
    TextView past_days;
    CheckBox cb_Find;

    public FindAdapterPerson(Context context, ArrayList<Person> personArrayList){

        ctx = context;
        mPersonList = personArrayList;
        mLayoutInflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mPersonList.size();
    }

    @Override
    public Object getItem(int position) {
        return mPersonList.get(position);
    }

    @Override
    public long getItemId(int position) {
        //получаем id - он нужен при удалении из контекстного меню - идёт обращение к адаптеру
        return mPersonList.get(position).getPerson_id();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Person p = getPerson(position);

        View v = convertView;
        if (v == null) {
            v = mLayoutInflater.inflate(R.layout.list_name_choose, parent, false);
        }

        name = (TextView)v.findViewById(R.id.name_list_test);
        cb_Find = (CheckBox) v.findViewById(R.id.checkBox_test);

        name.setText(p.getPerson_name());
        cb_Find.setText("");
        //записываем тэг для того, чтобы потом получить позицию списка в обработчике чекбокса
        cb_Find.setTag(position);
        //cb_Find.setChecked(false); //нельзя, иначе уход списка за экран снимает галку
        //присваиваем слушатель чекбоксу
        cb_Find.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //позицию списка получаем из тэга, преобразовав его в (Integer)
                Person p1 = (Person)getItem((Integer) buttonView.getTag());
                // здесь присваиваем Person.person_choose значение isChecked
                p1.setPerson_choose(isChecked);
            }
        });
        return v;
    }

    // Метод getCheckedPersonList проверяет, какие персоны отмечены галками
    // и формирует из них коллекцию-список.
    //помещаем в ArrayList только те персоны, которые имеют установленную галку
    //галка выбора пперсоны устанавливается в методе обработки слушателя галки,
    // который прописан в методе View getView()  адаптера FindAdapter в слушателе cb_Find

    public  ArrayList<Person> getCheckedPersonList() {

        ArrayList<Person> checkedList = new ArrayList<Person>();

        for (Person p : mPersonList) {
            // если отмечен
            if (p.isPerson_choose())
                checkedList.add(p);
        }
        return checkedList;
    }

    //метод-оболочка для получения объекта по его позиции в списке
    private Person getPerson(int position){
        return ((Person)getItem(position));
    }
}
