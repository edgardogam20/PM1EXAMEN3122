package com.example.pm1examen3122;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class SQLiteConexion extends SQLiteOpenHelper {

    // Nombre de la base de datos
    private static final String DATABASE_NAME = "contactosdb.db";
    // Versión de la base de datos
    private static final int DATABASE_VERSION = 1;

    // Nombre de la tabla
    private static final String TABLE_CONTACTOS = "contactos";

    // Columnas de la tabla
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_PAIS = "pais";
    private static final String COLUMN_NOMBRE = "nombre";
    private static final String COLUMN_TELEFONO = "telefono";
    private static final String COLUMN_NOTA = "nota";
    private static final String COLUMN_IMAGEN = "imagen"; // Para la imagen

    // Sentencia SQL para crear la tabla
    private static final String CREATE_TABLE_CONTACTOS = "CREATE TABLE " + TABLE_CONTACTOS + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_PAIS + " TEXT,"
            + COLUMN_NOMBRE + " TEXT,"
            + COLUMN_TELEFONO + " TEXT,"
            + COLUMN_NOTA + " TEXT,"
            + COLUMN_IMAGEN + " BLOB"
            + ")";

    public SQLiteConexion(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_TABLE_CONTACTOS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTOS);
        onCreate(db);
    }

    public long insertContact(ContactoItem contacto) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PAIS, contacto.getPais());
        values.put(COLUMN_NOMBRE, contacto.getNombre());
        values.put(COLUMN_TELEFONO, contacto.getTelefono());
        values.put(COLUMN_NOTA, contacto.getNota());
        values.put(COLUMN_IMAGEN, contacto.getImagen());


        long id = db.insert(TABLE_CONTACTOS, null, values);
        db.close();
        return id;
    }

    public ContactoItem getContact(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CONTACTOS,
                new String[]{COLUMN_ID, COLUMN_PAIS, COLUMN_NOMBRE, COLUMN_TELEFONO, COLUMN_NOTA, COLUMN_IMAGEN},
                COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        ContactoItem contacto = new ContactoItem(
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PAIS)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOMBRE)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TELEFONO)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTA)),
                cursor.getBlob(cursor.getColumnIndexOrThrow(COLUMN_IMAGEN)));
        contacto.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));

        cursor.close();
        db.close();
        return contacto;
    }

    // aqui vamos a obtener todos los contactos
    public List<ContactoItem> getAllContacts() {
        List<ContactoItem> contactList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_CONTACTOS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);


        if (cursor.moveToFirst()) {
            do {
                ContactoItem contacto = new ContactoItem();
                contacto.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                contacto.setPais(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PAIS)));
                contacto.setNombre(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOMBRE)));
                contacto.setTelefono(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TELEFONO)));
                contacto.setNota(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTA)));
                contacto.setImagen(cursor.getBlob(cursor.getColumnIndexOrThrow(COLUMN_IMAGEN)));
                contactList.add(contacto);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return contactList;
    }

    public int updateContact(ContactoItem contacto) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PAIS, contacto.getPais());
        values.put(COLUMN_NOMBRE, contacto.getNombre());
        values.put(COLUMN_TELEFONO, contacto.getTelefono());
        values.put(COLUMN_NOTA, contacto.getNota());
        values.put(COLUMN_IMAGEN, contacto.getImagen());


        return db.update(TABLE_CONTACTOS, values, COLUMN_ID + " = ?",
                new String[]{String.valueOf(contacto.getId())});
    }


    public void deleteContact(ContactoItem contacto) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CONTACTOS, COLUMN_ID + " = ?",
                new String[]{String.valueOf(contacto.getId())});
        db.close();
    }


    public int getContactsCount() {
        String countQuery = "SELECT * FROM " + TABLE_CONTACTOS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        return count;
    }
}