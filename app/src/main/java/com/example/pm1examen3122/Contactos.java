package com.example.pm1examen3122;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class Contactos extends AppCompatActivity {

    private static final int PERMISSION_CALL_PHONE = 1;
    EditText etBuscar;
    ListView listViewContactos;
    Button btnCompartirContacto, btnEliminarContacto, btnActualizarContacto, btnVerImagen;

    SQLiteConexion db;
    List<ContactoItem> listaContactos;
    ContactoItemAdapter adapter;
    ContactoItem contactoSeleccionado = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_contactos);

        etBuscar = findViewById(R.id.etBuscar);
        listViewContactos = findViewById(R.id.listViewContactos);
        btnCompartirContacto = findViewById(R.id.btnCompartirContacto);
        btnEliminarContacto = findViewById(R.id.btnEliminarContacto);
        btnActualizarContacto = findViewById(R.id.btnActualizarContacto);
        btnVerImagen = findViewById(R.id.btnVerImagen);

        db = new SQLiteConexion(this);
        cargarContactos();


        listViewContactos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                contactoSeleccionado = (ContactoItem) parent.getItemAtPosition(position);
                mostrarDialogoLlamada(contactoSeleccionado);
            }
        });


        btnCompartirContacto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (contactoSeleccionado != null) {
                    compartirContacto(contactoSeleccionado);
                } else {
                    Toast.makeText(Contactos.this, "Selecciona un contacto para compartir.", Toast.LENGTH_SHORT).show();
                }
            }
        });


        btnEliminarContacto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (contactoSeleccionado != null) {
                    confirmarEliminarContacto(contactoSeleccionado);
                } else {
                    Toast.makeText(Contactos.this, "Selecciona un contacto para eliminar.", Toast.LENGTH_SHORT).show();
                }
            }
        });


        btnActualizarContacto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (contactoSeleccionado != null) {
                    Intent intent = new Intent(Contactos.this, ListaContactos.class);
                    intent.putExtra("contacto_id", contactoSeleccionado.getId());
                    intent.putExtra("pais", contactoSeleccionado.getPais());
                    intent.putExtra("nombre", contactoSeleccionado.getNombre());
                    intent.putExtra("telefono", contactoSeleccionado.getTelefono());
                    intent.putExtra("nota", contactoSeleccionado.getNota());

                    startActivity(intent);
                } else {
                    Toast.makeText(Contactos.this, "Selecciona un contacto para actualizar.", Toast.LENGTH_SHORT).show();
                }
            }
        });


        btnVerImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (contactoSeleccionado != null) {

                    Toast.makeText(Contactos.this, "Funcionalidad 'Ver Imagen' pendiente de implementar.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Contactos.this, "Selecciona un contacto para ver la imagen.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        etBuscar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filtrarContactos(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarContactos();
    }

    private void cargarContactos() {
        listaContactos = db.getAllContacts();
        adapter = new ContactoItemAdapter(this, listaContactos);
        listViewContactos.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void filtrarContactos(String texto) {
        List<ContactoItem> contactosFiltrados = new ArrayList<>();
        for (ContactoItem contacto : listaContactos) {
            if (contacto.getNombre().toLowerCase().contains(texto.toLowerCase()) ||
                    contacto.getTelefono().toLowerCase().contains(texto.toLowerCase())) {
                contactosFiltrados.add(contacto);
            }
        }
        adapter = new ContactoItemAdapter(this, contactosFiltrados);
        listViewContactos.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void compartirContacto(ContactoItem contacto) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        String detallesContacto = "Contacto: " + contacto.getNombre() + "\nTeléfono: " + contacto.getTelefono() + "\nNota: " + contacto.getNota();
        shareIntent.putExtra(Intent.EXTRA_TEXT, detallesContacto);
        startActivity(Intent.createChooser(shareIntent, "Compartir contacto vía"));
    }

    private void confirmarEliminarContacto(final ContactoItem contacto) {
        new AlertDialog.Builder(this)
                .setTitle("Acción")
                .setMessage("¿Desea eliminar a " + contacto.getNombre() + "?")
                .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        db.deleteContact(contacto);
                        Toast.makeText(Contactos.this, "Contacto eliminado.", Toast.LENGTH_SHORT).show();
                        cargarContactos();
                        contactoSeleccionado = null;
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }


    private void mostrarDialogoLlamada(final ContactoItem contacto) {
        new AlertDialog.Builder(this)
                .setTitle("Acción")
                .setMessage("¿Desea llamar a " + contacto.getNombre() + "?")
                .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        realizarLlamada(contacto.getTelefono());
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void realizarLlamada(String numeroTelefono) {
        String uri = "tel:" + numeroTelefono;
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse(uri));

        if (ContextCompat.checkSelfPermission(Contactos.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Contactos.this, new String[]{Manifest.permission.CALL_PHONE}, PERMISSION_CALL_PHONE);
        } else {
            try {
                startActivity(callIntent);
            } catch (SecurityException e) {
                Toast.makeText(this, "Permiso de llamada denegado.", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CALL_PHONE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (contactoSeleccionado != null) {
                    realizarLlamada(contactoSeleccionado.getTelefono());
                }
            } else {
                Toast.makeText(this, "Permiso de llamada denegado. No se puede realizar la llamada.", Toast.LENGTH_LONG).show();
            }
        }
    }
}