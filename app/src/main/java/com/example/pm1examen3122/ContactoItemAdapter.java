package com.example.pm1examen3122;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class ContactoItemAdapter extends ArrayAdapter<ContactoItem> {

    public ContactoItemAdapter(Context context, List<ContactoItem> contactos) {
        super(context, 0, contactos);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ContactoItem contacto = getItem(position);


        if (convertView == null) {

            convertView = LayoutInflater.from(getContext()).inflate(R.layout.contacto, parent, false);
        }


        TextView tvNombre = convertView.findViewById(R.id.tvNombreContacto);
        TextView tvTelefono = convertView.findViewById(R.id.tvTelefonoContacto);


        tvNombre.setText(contacto.getNombre());
        tvTelefono.setText(contacto.getTelefono());


        return convertView;
    }
}