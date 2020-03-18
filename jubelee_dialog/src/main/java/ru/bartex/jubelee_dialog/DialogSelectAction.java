package ru.bartex.jubelee_dialog;

import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;

public class DialogSelectAction extends DialogFragment {

    static String TAG = "33333";
    private static final String ARG_VALUE = "ValueOfId";
    int selectNumber;

    public DialogSelectAction(){}

    public interface SelectAction {
        void NumberOfAction(int number, long id);
    }
    SelectAction mSelectAction;

    public static DialogSelectAction newInstance(long id){
        Bundle args = new Bundle();
        args.putLong(ARG_VALUE, id);
        DialogSelectAction fragment = new DialogSelectAction();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mSelectAction = (SelectAction)context;
    }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            //принудительно вызываем клавиатуру - повторный вызов ее скроет
            //takeOnAndOffSoftInput();

            AlertDialog.Builder bilder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View view = inflater.inflate(R.layout.fragment_dialog_select_action, null);
            final RadioGroup radioGroup = view.findViewById(R.id.radioGroupSelect);
            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int i) {
                    switch (i){
                        case R.id.radioButtonOneDates:
                            selectNumber = 0;
                            break;
                        case R.id.radioButtonTwoDates:
                            selectNumber = 1;
                            break;
                        case R.id.radioButtonBioritm:
                            selectNumber = 2;
                            break;
                    }
                }
            });
            final long valueOfId = (Long) getArguments().get(ARG_VALUE);
            bilder.setView(view);
            bilder.setTitle("Выберите дальнейшее действие");
            bilder.setPositiveButton("Готово", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    //Вызываем метод интерфейса и передаём выбранное действие и id
                    mSelectAction.NumberOfAction(selectNumber,valueOfId );
                    Log.d(TAG, "DialogSelectAction NumberOfAction = " +
                            selectNumber + "  id = " +valueOfId);

                    //принудительно прячем  клавиатуру - повторный вызов ее покажет
                    //takeOnAndOffSoftInput();
                }
            });

            bilder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    //принудительно прячем  клавиатуру - повторный вызов ее покажет
                    //takeOnAndOffSoftInput();

                }
            });
            //если не делать запрет на закрытие окна при щелчке за пределами окна, то можно так
            //return bilder.create();
            //А если делать запрет, то так
            AlertDialog  dialog = bilder.create();
            //запрет на закрытие окна при щелчке за пределами окна
            dialog.setCanceledOnTouchOutside(false);
            return dialog;
        }
    }
