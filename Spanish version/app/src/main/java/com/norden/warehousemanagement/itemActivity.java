package com.norden.warehousemanagement;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class itemActivity extends AppCompatActivity {

    private long idItem;
    private warehouseManagementDataSource bd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);

        // Busquem el id que estem modificant
        // si el el id es -1 vol dir que s'està creant
        idItem = this.getIntent().getExtras().getLong("id");

        if (idItem != -1) {
            setTitle("Editar article");
        }
        else {
            setTitle("Afegir nou article");
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        bd = new warehouseManagementDataSource(this);

        /*Botons d'acceptar i cancelar*/

        // Botó d'acceptar
        Button btnOk = (Button) findViewById(R.id.btnOk);
        btnOk.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                acceptChanges();
            }
        });

        // Botó de cancelar
        Button  btnCancel = (Button) findViewById(R.id.btnCancelar);
        btnCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                cancelChanges();
            }
        });

        if (idItem != -1) {
            // Si estem modificant carreguem les dades en pantalla
            loadData();
        }
    }

    private void loadData() {
        // Demanem un cursor que retorna un sol registre amb les dades de l'article
        // Això es podria fer amb un classe pero...
        Cursor datos = bd.item(idItem);
        datos.moveToFirst();

        // Carreguem les dades en la interfície
        TextView tv;

        tv = (TextView) findViewById(R.id.edtItemCode);
        tv.setEnabled(false);
        tv.setText(datos.getString(datos.getColumnIndex(warehouseManagementDataSource.WAREHOUSEMANAGEMENT_ITEMCODE)));

        tv = (TextView) findViewById(R.id.edtDescription);
        tv.setText(datos.getString(datos.getColumnIndex(warehouseManagementDataSource.WAREHOUSEMANAGEMENT_DESCRIPTION)));

        tv = (TextView) findViewById(R.id.edtPvp);
        tv.setText(datos.getString(datos.getColumnIndex(warehouseManagementDataSource.WAREHOUSEMANAGEMENT_PVP)));

        tv = (TextView) findViewById(R.id.edtStock);
        tv.setEnabled(false);
        tv.setText(datos.getString(datos.getColumnIndex(warehouseManagementDataSource.WAREHOUSEMANAGEMENT_STOCK)));
    }

    private void acceptChanges() {
        // Validem les dades
        TextView tv;

        // El codi de l'article ha d'estar informat
        tv = (TextView) findViewById(R.id.edtItemCode);
        String itemCode = tv.getText().toString();
        if (itemCode.trim().equals("")) {
            myDialogs.showShortSnackbar(findViewById(R.id.activity_main), "El codi de l'article ha d'estar informat");
            return;
        }

        // La descripció ha d'estar informada
        tv = (TextView) findViewById(R.id.edtDescription);
        String description = tv.getText().toString();
        if (description.trim().equals("")) {
            myDialogs.showShortSnackbar(findViewById(R.id.activity_main), "La descripció ha d'estar informada");
            return;
        }

        // El PVP ha de ser minim 0
        tv = (TextView) findViewById(R.id.edtPvp);
        double iPvp;
        try {
            iPvp = Double.valueOf(tv.getText().toString().replaceAll(",", "."));
        }
        catch (Exception e) {
            myDialogs.showShortSnackbar(findViewById(R.id.activity_main), "El PVP ha de ser un numero");
            return;
        }

        if ((iPvp < 0)) {
            myDialogs.showShortSnackbar(findViewById(R.id.activity_main), "El PVP ha de ser mínim 0");
            return;
        }

        // El stock ha de ser un numero enter
        tv = (TextView) findViewById(R.id.edtStock);
        //tv.setEnabled(false);

        int iStock;

        if (tv.getText().toString().equals("")) {
            iStock = 0;
        }
        else {
            try {
                iStock = Integer.valueOf(tv.getText().toString());
            }
            catch (Exception e) {
                myDialogs.showShortSnackbar(findViewById(R.id.activity_main), "El stock ha de ser un numero enter");
                return;
            }
        }

        tv = (TextView) findViewById(R.id.edtItemCode);

        // Mirem si estem creant o estem guardant
        if (idItem == -1) {
            if (bd.itemCodeExists(tv.getText().toString())) {
                myDialogs.showShortSnackbar(findViewById(R.id.activity_main), "Ja existeix un article amb aquest codi");
                return;
            }
            // El estoc ha de ser mínim 0 si estem creant
            if ((iStock < 0)) {
                myDialogs.showShortSnackbar(findViewById(R.id.activity_main), "El stock ha de ser mínim 0");
                return;
            }

            idItem = bd.itemAdd(tv.getText().toString(), description, iPvp, iStock);
        }
        else {
            bd.itemUpdate(idItem,tv.getText().toString(), description, iPvp, iStock);
        }

        Intent mIntent = new Intent();
        mIntent.putExtra("id", idItem);
        setResult(RESULT_OK, mIntent);

        finish();
    }

    private void cancelChanges() {
        Intent mIntent = new Intent();
        mIntent.putExtra("id", idItem);
        setResult(RESULT_CANCELED, mIntent);

        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.item_activity_menu, menu);
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
        }
        return super.onOptionsItemSelected(item);
    }

}

