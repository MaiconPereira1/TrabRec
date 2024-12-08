package com.example.trab;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.trab.Helper.DatabaseHelper;
import java.util.ArrayList;

public class RelacaoNotas extends AppCompatActivity {
    private Spinner spinnerAlunos;
    private ListView listViewNotas;
    private TextView tvTopicosEspeciais, tvMedia, tv1Bim, tv2Bim, tv3Bim, tv4Bim;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relacao_notas);

        // Corrigido: Agora os IDs correspondem aos elementos do layout XML
        spinnerAlunos = findViewById(R.id.spinnerAlunos);
        listViewNotas = findViewById(R.id.listViewNotas);
        tvTopicosEspeciais = findViewById(R.id.tvTopicosEspeciais);
        tvMedia = findViewById(R.id.tvMedia);
        tv1Bim = findViewById(R.id.tv1Bim);
        tv2Bim = findViewById(R.id.tv2Bim);
        tv3Bim = findViewById(R.id.tv3Bim);
        tv4Bim = findViewById(R.id.tv4Bim);

        dbHelper = new DatabaseHelper(this);

        populateSpinner();

        spinnerAlunos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedRa = spinnerAlunos.getSelectedItem().toString();
                populateListView(selectedRa);
                updateTopicosEspeciais(selectedRa);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {}
        });
    }

    private void populateSpinner() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT ra FROM Aluno", null);

        ArrayList<String> alunos = new ArrayList<>();
        while (cursor.moveToNext()) {
            alunos.add(cursor.getString(0));
        }
        cursor.close();

        if (alunos.isEmpty()) {
            Toast.makeText(this, "Nenhum aluno encontrado!", Toast.LENGTH_SHORT).show();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, alunos);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAlunos.setAdapter(adapter);
    }

    private void populateListView(String ra) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT disciplina, bimestre, nota FROM Nota WHERE ra = ?", new String[]{ra});

        ArrayList<String> notas = new ArrayList<>();
        while (cursor.moveToNext()) {
            String disciplina = cursor.getString(0);
            String bimestre = cursor.getString(1);
            double nota = cursor.getDouble(2);

            notas.add("Disciplina: " + disciplina + "\nBimestre: " + bimestre + "\nNota: " + nota);
        }
        cursor.close();

        if (notas.isEmpty()) {
            notas.add("Nenhuma nota encontrada para este aluno.");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, notas);
        listViewNotas.setAdapter(adapter);
    }

    private void updateTopicosEspeciais(String ra) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT AVG(nota) FROM Nota WHERE ra = ?", new String[]{ra});

        if (cursor.moveToFirst()) {
            double media = cursor.getDouble(0);
            tvMedia.setText("Média: " + media);
        }
        cursor.close();

        tvTopicosEspeciais.setText("Tópicos Especiais: Algoritmos, Lógica de Programação");

        cursor = db.rawQuery("SELECT bimestre, nota FROM Nota WHERE ra = ? ORDER BY bimestre", new String[]{ra});

        while (cursor.moveToNext()) {
            String bimestre = cursor.getString(0);
            double nota = cursor.getDouble(1);

            if (bimestre.equals("1º Bimestre")) {
                tv1Bim.setText("1º Bim: " + nota);
            } else if (bimestre.equals("2º Bimestre")) {
                tv2Bim.setText("2º Bim: " + nota);
            } else if (bimestre.equals("3º Bimestre")) {
                tv3Bim.setText("3º Bim: " + nota);
            } else if (bimestre.equals("4º Bimestre")) {
                tv4Bim.setText("4º Bim: " + nota);
            }
        }
        cursor.close();
    }
}
