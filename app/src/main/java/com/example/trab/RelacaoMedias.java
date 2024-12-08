package com.example.trab;

import android.annotation.SuppressLint;
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

public class RelacaoMedias extends AppCompatActivity {
    private Spinner spinnerDisciplina;
    private ListView listViewMedias;
    private TextView tvRa, tvMedia, tvNomeAluno, tvStatus;
    private DatabaseHelper dbHelper;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relacao_notas);

        spinnerDisciplina = findViewById(R.id.spinnerDisciplina);
        listViewMedias = findViewById(R.id.listViewMedias);
        tvRa = findViewById(R.id.tvRa);
        tvMedia = findViewById(R.id.tvMedia);
        tvNomeAluno = findViewById(R.id.tvNomeAluno);
        tvStatus = findViewById(R.id.tvStatus);

        dbHelper = new DatabaseHelper(this);

        populateSpinner();

        spinnerDisciplina.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedDisciplina = spinnerDisciplina.getSelectedItem().toString();
                populateMedias(selectedDisciplina);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {}
        });
    }

    private void populateSpinner() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT nome FROM Disciplina", null);

        ArrayList<String> disciplinas = new ArrayList<>();
        while (cursor.moveToNext()) {
            disciplinas.add(cursor.getString(0));
        }
        cursor.close();

        if (disciplinas.isEmpty()) {
            Toast.makeText(this, "Nenhuma disciplina encontrada!", Toast.LENGTH_SHORT).show();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, disciplinas);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDisciplina.setAdapter(adapter);
    }

    private void populateMedias(String disciplina) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT ra, nome, AVG(nota) FROM Nota INNER JOIN Aluno ON Nota.ra = Aluno.ra WHERE disciplina = ? GROUP BY ra",
                new String[]{disciplina});

        if (cursor.moveToFirst()) {
            String ra = cursor.getString(0);
            String nomeAluno = cursor.getString(1);
            double media = cursor.getDouble(2);

            tvRa.setText("RA: " + ra);
            tvNomeAluno.setText(nomeAluno);
            tvMedia.setText("Média: " + media);

            if (media >= 6) {
                tvStatus.setText("Aprovado");
            } else {
                tvStatus.setText("Reprovado");
            }
        }
        cursor.close();

        ArrayList<String> medias = new ArrayList<>();
        medias.add("Disciplina: " + disciplina + " | Média: " + tvMedia.getText().toString());

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, medias);
        listViewMedias.setAdapter(adapter);
    }
}
