package com.norden.warehousemanagement;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;
import java.util.Calendar;

import static android.content.ContentValues.TAG;

public class itemRegistryActivity extends AppCompatActivity {

    private warehouseManagementDataSource bd;
    private adapterWarehouseManagementItemsRegistry scMovements;

    EditText edtInitialDate;
    EditText edtFinalDate;

    ImageView ivInitialDate;
    ImageView ivFinalDate;

    String id_article;

    private DatePickerDialog.OnDateSetListener mDateSetListener;

    private static String[] from = new String[]{warehouseManagementDataSource.MOVEMENT_DATE,
            warehouseManagementDataSource.MOVEMENT_QUANTITY,
            warehouseManagementDataSource.MOVEMENT_TYPE};
    private static int[] to = new int[]{R.id.tvDate_R2, R.id.tvQuantity_R2, R.id.tvType_R2};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_registry);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        id_article = intent.getStringExtra("id_article");
        String codi_article = intent.getStringExtra("codi_article");

        setTitle("E/S del article " + codi_article);

        bd = new warehouseManagementDataSource(this);
        loadMovements(Integer.parseInt(id_article));

        edtInitialDate = (EditText) findViewById(R.id.edtInitialDate);
        edtInitialDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAndGetDateCalendar(edtInitialDate);
            }
        });

        ivInitialDate = (ImageView) findViewById(R.id.ivInitialDate);
        ivInitialDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAndGetDateCalendar(edtInitialDate);
            }
        });


        edtFinalDate = (EditText) findViewById(R.id.edtFinalDate);
        edtFinalDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAndGetDateCalendar(edtFinalDate);
            }
        });

        ivFinalDate = (ImageView) findViewById(R.id.ivFinalDate);
        ivFinalDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAndGetDateCalendar(edtFinalDate);
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    loadMovementsDates();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

        FloatingActionButton fab2 = findViewById(R.id.fab2);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edtInitialDate.setText("");
                edtFinalDate.setText("");
                loadMovements(Long.parseLong(id_article));
            }
        });
    }

    public void loadMovements(long id_article) {
        // Crido a la quaery que em retorna tots els articles
        Cursor cursorMovements = bd.movement(id_article);

        // Now create a simple cursor adapter and set it to display
        scMovements = new adapterWarehouseManagementItemsRegistry(this, R.layout.registry_item, cursorMovements, from, to, 1);

        ListView lv = (ListView) findViewById(R.id.lvMovements);

        lv.setAdapter(scMovements);
    }

    public void loadMovementsDates() throws ParseException {
        Cursor _cursor;

        if(!edtInitialDate.getText().toString().isEmpty() && !edtFinalDate.getText().toString().isEmpty()){
            _cursor = bd.movementsBetweenDates(dateFormatChanger.ChangeFormatDate(edtInitialDate.getText().toString(), "dd/MM/yyyy","yyyy/MM/dd"), dateFormatChanger.ChangeFormatDate(edtFinalDate.getText().toString(), "dd/MM/yyyy","yyyy/MM/dd"),Integer.parseInt(id_article));
        }
        else if(!edtInitialDate.getText().toString().isEmpty()){
            _cursor = bd.movementsInitialDate(dateFormatChanger.ChangeFormatDate(edtInitialDate.getText().toString(), "dd/MM/yyyy","yyyy/MM/dd"),Integer.parseInt(id_article));
        }
        else if(!edtFinalDate.getText().toString().isEmpty()){
            _cursor = bd.movementsFinalDate(dateFormatChanger.ChangeFormatDate(edtFinalDate.getText().toString(), "dd/MM/yyyy","yyyy/MM/dd"),Integer.parseInt(id_article));
        }
        else {
            _cursor = bd.movement(Integer.parseInt(id_article));
        }

        scMovements = new adapterWarehouseManagementItemsRegistry(this, R.layout.registry_item, _cursor, from, to, 1);

        ListView lv = (ListView) findViewById(R.id.lvMovements);

        lv.setAdapter(scMovements);

        if (scMovements.isEmpty()) {
            myDialogs.showShortSnackbar(findViewById(R.id.coordinatorLayout), "No s'ha trobat cap moviment");
        }
    }

    public void showAndGetDateCalendar(final EditText editText) {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                Log.d(TAG, "onDateSet: yyyy/mm/dd: " + year + "/" + month + "/" + day);

                String sDay = String.valueOf(day);
                String sMonth = String.valueOf(month);

                if (sDay.length() != 2) {
                    sDay = "0" + sDay;
                }
                if (sMonth.length() != 2) {
                    sMonth = "0" + sMonth;
                }

                String date = sDay + "/" + sMonth + "/" + year;
                editText.setText(date);
            }
        };

        DatePickerDialog dialog = new DatePickerDialog(
                itemRegistryActivity.this,
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                mDateSetListener,
                year,month,day);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}

class adapterWarehouseManagementItemsRegistry extends android.widget.SimpleCursorAdapter {

    public adapterWarehouseManagementItemsRegistry(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final View view = super.getView(position, convertView, parent);

        // Agafem l'objecte de la view que es una LINEA DEL CURSOR
        final Cursor linia = (Cursor) getItem(position);

        TextView tv = view.findViewById(R.id.tvDate_R2);
        try {
            tv.setText(dateFormatChanger.ChangeFormatDate(tv.getText().toString(), "yyyy/MM/dd", "dd/MM/yyyy"));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return view;
    }
}
