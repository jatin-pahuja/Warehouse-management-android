package com.norden.warehousemanagement;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;
import java.util.Calendar;

import static android.content.ContentValues.TAG;
import static android.util.TypedValue.TYPE_NULL;

public class allMovementsActivity extends AppCompatActivity {

    private warehouseManagementDataSource bd;
    private adapterAllMovementsActivity scMovements;

    private static String[] from = new String[]{
            warehouseManagementDataSource.MOVEMENT_ITEMCODE,
            warehouseManagementDataSource.MOVEMENT_DATE,
            warehouseManagementDataSource.MOVEMENT_QUANTITY,
            warehouseManagementDataSource.MOVEMENT_TYPE};
    private static int[] to = new int[]{R.id.tvItemCode_R2, R.id.tvDate_R2, R.id.tvQuantity_R2, R.id.tvType_R2};

    private EditText edtDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_movements);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Check Activity");

        bd = new warehouseManagementDataSource(this);
        loadMovements();

        edtDate = (EditText) findViewById(R.id.edtDate);
        edtDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAndGetDateCalendar(edtDate);
            }
        });

        ImageView ivDate = (ImageView) findViewById(R.id.ivDate);
        ivDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAndGetDateCalendar(edtDate);
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    loadMovementsDate();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

        FloatingActionButton fab2 = findViewById(R.id.fab2);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edtDate.setText("");
                loadMovements();
            }
        });
    }

    public void loadMovements() {
        // Crido a la quaery que em retorna tots els articles
        Cursor cursorMovements = bd.movements();

        // Now create a simple cursor adapter and set it to display
        scMovements = new adapterAllMovementsActivity(this, R.layout.all_movements_item, cursorMovements, from, to, 1);

        ListView lv = (ListView) findViewById(R.id.lvAllMovements);

        lv.setAdapter(scMovements);
    }

    public void loadMovementsDate() throws ParseException {
        Cursor _cursor;

        if(!edtDate.getText().toString().isEmpty()){
            _cursor = bd.movementsEqualDate(dateFormatChanger.ChangeFormatDate(edtDate.getText().toString(), "dd/MM/yyyy","yyyy/MM/dd"));
        }
        else {
            _cursor = bd.movements();
        }

        scMovements = new adapterAllMovementsActivity(this, R.layout.all_movements_item, _cursor, from, to, 1);

        ListView lv = (ListView) findViewById(R.id.lvAllMovements);

        lv.setAdapter(scMovements);

        if (scMovements.isEmpty()) {
            myDialogs.showShortSnackbar(findViewById(R.id.coordinator2), "No movement found");
        }
    }

    public void showAndGetDateCalendar(final EditText editText) {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
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
                allMovementsActivity.this,
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

class adapterAllMovementsActivity extends android.widget.SimpleCursorAdapter {

    public adapterAllMovementsActivity(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
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
