package com.example.pm1examen3122;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class ListaContactos extends AppCompatActivity {

    Spinner spinnerPaises;
    EditText etNombre, etTelefono, etNota;
    Button btnSalvarContacto, btnVerContactos;

    SQLiteConexion db;
    private int contactoIdParaEditar = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_lista_contactos);

        spinnerPaises = findViewById(R.id.spinnerPaises);
        etNombre = findViewById(R.id.etNombre);
        etTelefono = findViewById(R.id.etTelefono);
        etNota = findViewById(R.id.etNota);
        btnSalvarContacto = findViewById(R.id.btnSalvarContacto);

        db = new SQLiteConexion(this);


        Intent intent = getIntent();
        if (intent.hasExtra("contacto_id")) {
            contactoIdParaEditar = intent.getIntExtra("contacto_id", -1);
            if (contactoIdParaEditar != -1) {
                cargarDatosContactoParaEditar(contactoIdParaEditar);
                btnSalvarContacto.setText("Actualizar Contacto");
            }
        }

        btnSalvarContacto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (contactoIdParaEditar != -1) {
                    actualizarContacto();
                } else {
                    salvarContacto();
                }
            }
        });

        if (findViewById(R.id.btnVerContactos) == null) {
            btnVerContactos = new Button(this);
            btnVerContactos.setId(R.id.btnVerContactos); // Asigna un ID
            btnVerContactos.setText("Ver Contactos Salvados");
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.topMargin = (int) getResources().getDisplayMetrics().density * 16;
            btnVerContactos.setLayoutParams(layoutParams);

            ((LinearLayout) findViewById(R.id.activity_main_layout)).addView(btnVerContactos);
        } else {
            btnVerContactos = findViewById(R.id.btnVerContactos);
        }

        btnVerContactos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent listIntent = new Intent(ListaContactos.this, Contactos.class);
                startActivity(listIntent);
                finish();
            }
        });
    }

    private void cargarDatosContactoParaEditar(int id) {
        ContactoItem contacto = db.getContact(id);
        if (contacto != null) {

            ArrayAdapter<CharSequence> adapterSpinner = ArrayAdapter.createFromResource(
                    this, R.array.paises_array, android.R.layout.simple_spinner_item);
            adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerPaises.setAdapter(adapterSpinner);
            int spinnerPosition = adapterSpinner.getPosition(contacto.getPais());
            spinnerPaises.setSelection(spinnerPosition);

            etNombre.setText(contacto.getNombre());
            etTelefono.setText(contacto.getTelefono());
            etNota.setText(contacto.getNota());

        }
    }

    private void salvarContacto() {
        String pais = spinnerPaises.getSelectedItem().toString();
        String nombre = etNombre.getText().toString().trim();
        String telefono = etTelefono.getText().toString().trim();
        String nota = etNota.getText().toString().trim();

        if (nombre.isEmpty()) {
            mostrarAlerta("Debe escribir un nombre");
            return;
        }
        if (telefono.isEmpty()) {
            mostrarAlerta("Debe escribir un telefono");
            return;
        }
        if (nota.isEmpty()) {
            mostrarAlerta("Debe escribir una nota");
            return;
        }

        byte[] imagenBytes = null;

        ContactoItem nuevoContacto = new ContactoItem(pais, nombre, telefono, nota, imagenBytes);
        long resultado = db.insertContact(nuevoContacto);

        if (resultado > 0) {
            Toast.makeText(this, "Contacto guardado exitosamente!", Toast.LENGTH_SHORT).show();
            limpiarCampos();
        } else {
            Toast.makeText(this, "Error al guardar el contacto.", Toast.LENGTH_SHORT).show();
        }
    }

    private void actualizarContacto() {
        String pais = spinnerPaises.getSelectedItem().toString();
        String nombre = etNombre.getText().toString().trim();
        String telefono = etTelefono.getText().toString().trim();
        String nota = etNota.getText().toString().trim();

        if (nombre.isEmpty()) {
            mostrarAlerta("Debe escribir un nombre");
            return;
        }
        if (telefono.isEmpty()) {
            mostrarAlerta("Debe escribir un telefono");
            return;
        }
        if (nota.isEmpty()) {
            mostrarAlerta("Debe escribir una nota");
            return;
        }

        byte[] imagenBytes = null;

        ContactoItem contactoActualizar = new ContactoItem(pais, nombre, telefono, nota, imagenBytes);
        contactoActualizar.setId(contactoIdParaEditar);

        int filasAfectadas = db.updateContact(contactoActualizar);

        if (filasAfectadas > 0) {
            Toast.makeText(this, "Contacto actualizado exitosamente!", Toast.LENGTH_SHORT).show();
            finish(); // Cierra esta actividad y regresa a la lista
        } else {
            Toast.makeText(this, "Error al actualizar el contacto.", Toast.LENGTH_SHORT).show();
        }
    }

    private void mostrarAlerta(String mensaje) {
        new AlertDialog.Builder(this)
                .setTitle("Alerta")
                .setMessage(mensaje)
                .setPositiveButton("OK", null)
                .show();
    }

    private void limpiarCampos() {
        etNombre.setText("");
        etTelefono.setText("");
        etNota.setText("");
        spinnerPaises.setSelection(0);
        contactoIdParaEditar = -1;
        btnSalvarContacto.setText("Salvar Contacto");
    }
}