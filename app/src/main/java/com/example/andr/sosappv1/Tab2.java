package com.example.andr.sosappv1;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;
import java.util.List;

public class Tab2 extends Fragment {

    private String HOST = "http://centraldobem.ads.cnecsan.edu.br/painel/paginas/app";

     ListView listViewContatos;
    ImageView imgFoto;

    static ContatosAdapter contatosAdapter;
    static List<Contato> lista;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab2_layout, container, false);

        listViewContatos = (ListView) v.findViewById(R.id.listViewContatos);

        imgFoto = (ImageView) v.findViewById(R.id.imgFoto);

        lista = new ArrayList<Contato>();
        contatosAdapter = new ContatosAdapter(getContext(), lista);

       listViewContatos.setAdapter(contatosAdapter);

        try {
            listaContatos();
        } catch (Exception error) {}

        return v;
    }


    private void listaContatos() {
        String url =  HOST + "/read.php";

        Ion.with(getContext())
                .load(url)
                .asJsonArray()
                .setCallback(new FutureCallback<JsonArray>() {
                    @Override
                    public void onCompleted(Exception e, JsonArray result) {
                        for(int i = 0; i < result.size(); i++) {
                            JsonObject obj = result.get(i).getAsJsonObject();

                            Contato c = new Contato();
                            //add objetos no contato
                            c.setId(obj.get("id_usuario").getAsInt());
                            c.setNome(obj.get("nome").getAsString());
                            c.setTelefone(obj.get("telefone").getAsString());
                            c.setEmail(obj.get("email").getAsString());
                            c.setFoto(obj.get("foto").getAsString());

                            //add na lista
                            lista.add(c);
                        }
                        contatosAdapter.notifyDataSetChanged();
                    }
                });

    }




}
