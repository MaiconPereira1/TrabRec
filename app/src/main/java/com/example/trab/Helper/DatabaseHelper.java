package com.example.trab.Helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "CadastroNotas.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE Aluno (" +
                "ra TEXT PRIMARY KEY," +
                "nome TEXT NOT NULL)");

        db.execSQL("CREATE TABLE Disciplina (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nome TEXT NOT NULL UNIQUE)");

        db.execSQL("CREATE TABLE Nota (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "ra TEXT NOT NULL," +
                "disciplina TEXT NOT NULL," +
                "bimestre TEXT NOT NULL," +
                "nota REAL NOT NULL," +
                "FOREIGN KEY (ra) REFERENCES Aluno (ra)," +
                "FOREIGN KEY (disciplina) REFERENCES Disciplina (nome))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Nota");
        db.execSQL("DROP TABLE IF EXISTS Disciplina");
        db.execSQL("DROP TABLE IF EXISTS Aluno");
        onCreate(db);
    }
}
