package com.example.partial;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    int operatie = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ArrayList<String> arrayList = new ArrayList<>();

        arrayList.add("Plus");
        arrayList.add("Minus");
        arrayList.add("Inmultire");
        arrayList.add("Impartire");

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<String> arrayAdapter= new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, arrayList);


        spinner.setAdapter(arrayAdapter);
    }

    public void calculate(View view){
        EditText editnumar1 = (EditText) findViewById(R.id.editNumar1);
        EditText editnumar2 = (EditText) findViewById(R.id.editNumar2);
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        int nr1 = Integer.parseInt(editnumar1.getText().toString());
        int nr2 = Integer.parseInt(editnumar2.getText().toString());
        int result;
        switch ((int) spinner.getSelectedItemId()){
            case 0:
                result = nr1+nr2;
                break;
            case 1:
                result = nr1- nr2;
                break;
            case 2:
                result = nr1 * nr2;
            case 3:
                if(nr2 == 0){
                    Toast.makeText(this,"Nu se poate imparti la 0",Toast.LENGTH_SHORT).show();
                }
                else{
                    result = nr1 / nr2;
                }
            default:
                result = nr1;
        }
        CalculFragment cf = new CalculFragment(result);


        cf.show(getSupportFragmentManager(),"Rezultat");

    }
}