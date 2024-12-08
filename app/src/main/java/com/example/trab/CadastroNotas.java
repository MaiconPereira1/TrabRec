package com.example.trab;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.trab.Helper.DatabaseHelper;
import java.util.ArrayList;
import java.util.Arrays;

public class CadastroNotas extends AppCompatActivity {
    private EditText etRa, etNome, etNota;
    private Spinner spinnerDisciplina, spinnerBimestre;
    private Button btnAdicionar, btnVerNotasAluno, btnVerNotasDisciplina;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_notas);

            etRa = findViewById(R.id.etRa);
            etNome = findViewById(R.id.etNome);
            etNota = findViewById(R.id.etNota);
            spinnerDisciplina = findViewById(R.id.spinnerDisciplina);
            spinnerBimestre = findViewById(R.id.spinnerBimestre);
            btnAdicionar = findViewById(R.id.btnAdicionar);
            btnVerNotasAluno = findViewById(R.id.btnVerNotasAluno);
            btnVerNotasDisciplina = findViewById(R.id.btnVerNotasDisciplina);

            dbHelper = new DatabaseHelper(this);


            configureSpinners();

            btnAdicionar.setOnClickListener(v -> adicionarNota());

            btnVerNotasAluno.setOnClickListener(v -> {
                Intent intent = new Intent(CadastroNotas.this, RelacaoNotas.class);
                startActivity(intent);
            });


            btnVerNotasDisciplina.setOnClickListener(v -> {
                Intent intent = new Intent(CadastroNotas.this, RelacaoNotas.class);
                startActivity(intent);
            });
        }

        private void configureSpinners() {
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT nome FROM Disciplina", null);

            ArrayList<String> disciplinas = new ArrayList<>();
            while (cursor.moveToNext()) {
                disciplinas.add(cursor.getString(0));
            }
            cursor.close();

            ArrayAdapter<String> adapterDisciplina = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, disciplinas);
            adapterDisciplina.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerDisciplina.setAdapter(adapterDisciplina);

            ArrayAdapter<String> adapterBimestre = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
                    Arrays.asList("1º Bimestre", "2º Bimestre", "3º Bimestre", "4º Bimestre"));
            adapterBimestre.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerBimestre.setAdapter(adapterBimestre);
        }

        private void adicionarNota() {
            String ra = etRa.getText().toString().trim();
            String nome = etNome.getText().toString().trim();
            String disciplina = spinnerDisciplina.getSelectedItem().toString();
            String bimestre = spinnerBimestre.getSelectedItem().toString();
            String notaStr = etNota.getText().toString().trim();

            if (ra.isEmpty() || nome.isEmpty() || notaStr.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                return;
            }

            double nota;
            try {
                nota = Double.parseDouble(notaStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Nota inválida", Toast.LENGTH_SHORT).show();
                return;
            }

            SQLiteDatabase db = dbHelper.getWritableDatabase();
            db.execSQL("INSERT OR IGNORE INTO Aluno (ra, nome) VALUES (?, ?)", new String[]{ra, nome});
            db.execSQL("INSERT INTO Nota (ra, disciplina, bimestre, nota) VALUES (?, ?, ?, ?)",
                    new Object[]{ra, disciplina, bimestre, nota});

            Toast.makeText(this, "Nota adicionada com sucesso!", Toast.LENGTH_SHORT).show();

            etRa.setText("");
            etNome.setText("");
            etNota.setText("");
        }
    }
