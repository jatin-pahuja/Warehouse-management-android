package com.norden.warehousemanagement;

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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;
import java.util.Calendar;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity {

    private static int ACTIVITY_ITEM_ADD = 1;
    private static int ACTIVITY_ITEM_UPDATE = 2;

    private long idActual;
    private int firstTimeApp = 0;

    private warehouseManagementDataSource bd;
    private adapterWarehouseManagementItems scItems;

    LayoutInflater mInflater;
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    private static String[] from = new String[]{warehouseManagementDataSource.WAREHOUSEMANAGEMENT_ITEMCODE,
            warehouseManagementDataSource.WAREHOUSEMANAGEMENT_DESCRIPTION,
            warehouseManagementDataSource.WAREHOUSEMANAGEMENT_STOCK};
    private static int[] to = new int[]{R.id.tvItemCode2, R.id.tvDescription2, R.id.tvStock2};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Management");

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addItem();
            }
        });

        bd = new warehouseManagementDataSource(this);
        loadItems();
    }

    private void addItem() {
        // Cridem a l'activity del detall de l'article enviant com a id -1
        Bundle bundle = new Bundle();
        bundle.putLong("id",-1);

        idActual = -1;

        Intent i = new Intent(this, itemActivity.class );
        i.putExtras(bundle);
        startActivityForResult(i,ACTIVITY_ITEM_ADD);
    }

    private void loadItems() {
        // Crido a la quaery que em retorna tots els articles
        Cursor cursorTasks = bd.warehouseManagement();

        // Now create a simple cursor adapter and set it to display
        scItems = new adapterWarehouseManagementItems(this, R.layout.warehouse_item, cursorTasks, from, to, 1);

        ListView lv = (ListView) findViewById(R.id.listView);

        lv.setAdapter(scItems);

        if (scItems.isEmpty() && firstTimeApp != 0) {
            myDialogs.showShortSnackbar(findViewById(R.id.activity_main), "No s'ha trobat cap article");
        }

        firstTimeApp++;

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                updateItem(id);
            }
        });
    }

    private void refreshItems() {
        Cursor cursorTasks = bd.warehouseManagement();

        // Notifiquem al adapter que les dades han canviat i que refresqui
        scItems.changeCursor(cursorTasks);
        scItems.notifyDataSetChanged();
    }

    private void updateItem(long id) {
        // Cridem a l'activity del detall de l'article enviant com a id -1
        Bundle bundle = new Bundle();
        bundle.putLong("id",id);

        idActual = id;

        Intent i = new Intent(this, itemActivity.class );
        i.putExtras(bundle);
        startActivityForResult(i,ACTIVITY_ITEM_UPDATE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ACTIVITY_ITEM_ADD) {
            if (resultCode == RESULT_OK) {
                // Carreguem tots els articles a lo bestia
                refreshItems();
            }
        }

        if (requestCode == ACTIVITY_ITEM_UPDATE) {
            if (resultCode == RESULT_OK) {
                refreshItems();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.filter_items_menu, menu);
        return true;
    }

    // Capturar pulsacions en el menú de la barra superior.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
            case R.id.allMovements:
                showAllMovementsActivity();
                return true;
            case R.id.allItems:
                loadItems();
                return true;
            case R.id.stockItems:
                loadStockItems();
                return true;
            case R.id.noStockItems:
                loadNoStockItems();
                return true;
            case R.id.weatherIcon:
                showWeatherActivity();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadStockItems() {
        // Cridem la query que ens retorna tots els articles amb stock
        Cursor cursorTasks = bd.stockIems();

        // Now create a simple cursor adapter and set it to display
        scItems = new adapterWarehouseManagementItems(this, R.layout.warehouse_item, cursorTasks, from, to, 1);

        ListView lv = (ListView) findViewById(R.id.listView);

        lv.setAdapter(scItems);

        if (scItems.isEmpty()) {
            myDialogs.showShortSnackbar(findViewById(R.id.activity_main), "No items were found in stock");
        }

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                updateItem(id);
            }
        });
    }

    private void loadNoStockItems() {
        // Cridem a la query que ens retorna els articles que NO tenen stock
        Cursor cursorTasks = bd.noStockIems();

        // Now create a simple cursor adapter and set it to display
        scItems = new adapterWarehouseManagementItems(this, R.layout.warehouse_item, cursorTasks, from, to, 1);

        ListView lv = (ListView) findViewById(R.id.listView);

        lv.setAdapter(scItems);

        if (scItems.isEmpty()) {
            myDialogs.showShortSnackbar(findViewById(R.id.activity_main), "No items were found in stock");
        }

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                updateItem(id);
            }
        });
    }

    public void deleteItem(final int idItem) {
        // Pedimos confirmación
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Do you want to delete the article?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                bd.itemDelete(idItem);

                Intent mIntent = new Intent();
                mIntent.putExtra("id", -1);  // Devolvemos -1 indicant que s'ha eliminat
                setResult(RESULT_OK, mIntent);

                refreshItems();
            }
        });

        builder.setNegativeButton("No", null);

        builder.show();

    }

    public void showAlertDialogButtonClicked(View view, String title, final int idLinia, final boolean addingStock) {
        // create an alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);

        final Context context = this.getApplicationContext();
        mInflater = LayoutInflater.from(context);

        // set the custom layout
        final View customLayout = mInflater.inflate(R.layout.dialog_stock, null);
        builder.setView(customLayout);

        final String[] _Date = {""};

        final TextView tvDatePicker = (TextView) customLayout.findViewById(R.id.tvDatePicker);
        tvDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        MainActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        ImageView ivDatePicker = (ImageView) customLayout.findViewById(R.id.ivDatePicker);
        ivDatePicker.setImageResource(R.drawable.calendar_icon);
        ivDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        MainActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        //final TextView tvDatePicker = (TextView) customLayout.findViewById(R.id.tvDatePicker);

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
                tvDatePicker.setText(date);
                _Date[0] = date;
            }
        };

        // add a button
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                EditText editText = customLayout.findViewById(R.id.edtNum);
                if (editText.getText().toString().length() > 5) {
                    myDialogs.showShortSnackbar(findViewById(R.id.activity_main), "The number is too long!");
                }
                else {
                    try {
                        sendDialogDataToActivity(editText.getText().toString(), _Date[0], addingStock, idLinia);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void sendDialogDataToActivity(String stock, String date, boolean addingStock, int idLinia) throws ParseException {
        Cursor item = bd.item(idLinia);
        item.moveToFirst();

        int stockUpdate;

        if (addingStock) {
            // ADD STOCK
            bd.movementAdd(
                    item.getString(item.getColumnIndexOrThrow(warehouseManagementDataSource.WAREHOUSEMANAGEMENT_ITEMCODE)),
                    dateFormatChanger.ChangeFormatDate(date, "dd/MM/yyyy","yyyy/MM/dd"),
                    Integer.parseInt(stock),
                    "Entry",
                    item.getInt(item.getColumnIndexOrThrow(warehouseManagementDataSource.WAREHOUSEMANAGEMENT_ID))
            );
            stockUpdate = item.getInt(item.getColumnIndexOrThrow(warehouseManagementDataSource.WAREHOUSEMANAGEMENT_STOCK)) + Integer.parseInt(stock);
        }
        else {
            // Remove STOCK
            bd.movementAdd(
                    item.getString(item.getColumnIndexOrThrow(warehouseManagementDataSource.WAREHOUSEMANAGEMENT_ITEMCODE)),
                    dateFormatChanger.ChangeFormatDate(date, "dd/MM/yyyy","yyyy/MM/dd"),
                    -Integer.parseInt(stock),
                    "Exit",
                    item.getInt(item.getColumnIndexOrThrow(warehouseManagementDataSource.WAREHOUSEMANAGEMENT_ID))
            );
            stockUpdate = item.getInt(item.getColumnIndexOrThrow(warehouseManagementDataSource.WAREHOUSEMANAGEMENT_STOCK)) - Integer.parseInt(stock);
        }

        bd.itemUpdate(
                item.getInt(item.getColumnIndexOrThrow(warehouseManagementDataSource.WAREHOUSEMANAGEMENT_ID)),
                item.getString(item.getColumnIndexOrThrow(warehouseManagementDataSource.WAREHOUSEMANAGEMENT_ITEMCODE)),
                item.getString(item.getColumnIndexOrThrow(warehouseManagementDataSource.WAREHOUSEMANAGEMENT_DESCRIPTION)),
                item.getInt(item.getColumnIndexOrThrow(warehouseManagementDataSource.WAREHOUSEMANAGEMENT_PVP)),
                stockUpdate);

        if (addingStock) {
            myDialogs.showShortSnackbar(findViewById(R.id.activity_main), "It has been added " + stock + " de stock");
        }
        else {
            myDialogs.showShortSnackbar(findViewById(R.id.activity_main), "It has been added " + stock + " de stock");
        }

        refreshItems();
    }

    public void showAllMovementsActivity() {
        Intent myIntent = new Intent(this, allMovementsActivity.class);
        this.startActivity(myIntent);
    }

    public void showWeatherActivity() {
        Intent myIntent = new Intent(this, weatherActivity.class);
        this.startActivity(myIntent);
    }

}

class adapterWarehouseManagementItems extends android.widget.SimpleCursorAdapter {

    public MainActivity mainActivity;

    public adapterWarehouseManagementItems(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
        mainActivity = (MainActivity) context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final View view = super.getView(position, convertView, parent);

        // Agafem l'objecte de la view que es una LINEA DEL CURSOR
        final Cursor linia = (Cursor) getItem(position);

        final int idLinia = linia.getInt(linia.getColumnIndexOrThrow(warehouseManagementDataSource.WAREHOUSEMANAGEMENT_ID));

        int stock = linia.getInt(
                linia.getColumnIndexOrThrow(warehouseManagementDataSource.WAREHOUSEMANAGEMENT_STOCK)
        );

        // Si el estoc actual es 0 o negatiu la row s'ha de mostrar en color vermell de fons, si té estoc caldrà que aparegui en color blanc de fons.
        if (stock <= 0) {
            view.setBackgroundColor(Color.parseColor("#e53935"));
        }
        else if (stock > 0) {
            view.setBackgroundColor(0x00000000);
        }

        // Botó d'eliminar un article
        ImageView ivDelete = (ImageView) view.findViewById(R.id.ivDelete);
        ivDelete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                View row = (View) v.getParent();
                // Busco el ListView
                ListView lv = (ListView) row.getParent();
                // Busco quina posicio ocupa la Row dins de la ListView
                int position = lv.getPositionForView(row);

                // Carrego la linia del cursor de la posició.
                Cursor linia = (Cursor) getItem(position);

                mainActivity.deleteItem(linia.getInt(linia.getColumnIndexOrThrow(warehouseManagementDataSource.WAREHOUSEMANAGEMENT_ID)));
            }
        });

        // Botó de treure stock
        ImageView ivStockQuit = (ImageView) view.findViewById(R.id.ivStockQuit);
        ivStockQuit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mainActivity.showAlertDialogButtonClicked(view, "Remove stock", idLinia, false);
            }
        });

        // Botó d'afegir stock
        ImageView ivStockAdd = (ImageView) view.findViewById(R.id.ivStockAdd);
        ivStockAdd.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mainActivity.showAlertDialogButtonClicked(view, "Add stock", idLinia, true);
            }
        });

        ImageView ivRegistry = (ImageView) view.findViewById(R.id.ivRegistry);
        ivRegistry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View row = (View) v.getParent();
                // Busco el ListView
                ListView lv = (ListView) row.getParent();
                // Busco quina posicio ocupa la Row dins de la ListView
                int position = lv.getPositionForView(row);

                // Carrego la linia del cursor de la posició.
                Cursor linia = (Cursor) getItem(position);

                Intent myIntent = new Intent(mainActivity, itemRegistryActivity.class);
                myIntent.putExtra("id_article", linia.getString(linia.getColumnIndexOrThrow(warehouseManagementDataSource.WAREHOUSEMANAGEMENT_ID)));
                myIntent.putExtra("codi_article", linia.getString(linia.getColumnIndexOrThrow(warehouseManagementDataSource.WAREHOUSEMANAGEMENT_ITEMCODE)));
                mainActivity.startActivity(myIntent);
            }
        });

        return view;
    }
}
