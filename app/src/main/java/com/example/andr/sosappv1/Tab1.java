package com.example.andr.sosappv1;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.nio.charset.MalformedInputException;

public class Tab1 extends Fragment  {
    ViewPager viewPager;

    private Button btnVisual, btnVisual2;
    private LinearLayout llConteudo, llConteudo2;

    public String categoria;
    EditText editNome, editTelefone, editEmail, editDescricao;
    private Spinner categoriaDoacaoG;
    private Button btnDoacaoG;
    //-----------------------------------------------------------------------------
    public String formadoacao2;
    EditText editNome2, editTelefone2, editEmail2, editDescricao2;
    private Spinner categoriaDoacaoG2;
    private Button btnDoacaoG2;

    private String HOST = "http://centraldobem.ads.cnecsan.edu.br/painel/paginas/app";

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab1_layout, container, false);

        btnVisual = (Button) v.findViewById(R.id.btnVisual);
        btnVisual2 = (Button) v.findViewById(R.id.btnVisual2);
        llConteudo = (LinearLayout) v.findViewById(R.id.llConteudo);
        llConteudo2 = (LinearLayout) v.findViewById(R.id.llConteudo2);

        editNome = (EditText) v.findViewById(R.id.editNome);
        editNome.setText(Login.nomeG);
        editNome.setInputType(0);

        editTelefone = (EditText) v.findViewById(R.id.editTelefone);
        editTelefone.setText(Login.telefoneG);
        editTelefone.setInputType(0);

        editEmail = (EditText)  v.findViewById(R.id.editEmail);
        editEmail.setText(Login.emailG);
        editEmail.setInputType(0);

        editDescricao = (EditText)  v.findViewById(R.id.editDescricao);

        categoriaDoacaoG = (Spinner) v.findViewById(R.id.categoriaDoacaoG);

        btnDoacaoG = (Button) v.findViewById(R.id.btnDoacaoG);
        //-----------------------------------------------------------------------------
        editNome2 = (EditText) v.findViewById(R.id.editNome2);
        editNome2.setText(Login.nomeG);
        editNome2.setInputType(0);

        editTelefone2 = (EditText) v.findViewById(R.id.editTelefone2);
        editTelefone2.setText(Login.telefoneG);
        editTelefone2.setInputType(0);

        editEmail2 = (EditText)  v.findViewById(R.id.editEmail2);
        editEmail2.setText(Login.emailG);
        editEmail2.setInputType(0);

        editDescricao2 = (EditText)  v.findViewById(R.id.editDescricao2);

        categoriaDoacaoG2 = (Spinner) v.findViewById(R.id.categoriaDoacaoG2);

        btnDoacaoG2 = (Button) v.findViewById(R.id.btnDoacaoG2);

        //Método inserir dinheiro
        try {
            btnDoacaoG2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String nome2 = editNome2.getText().toString();
                    String telefone2 = editTelefone2.getText().toString();
                    String email2 = editEmail2.getText().toString();
                    String categoria2 = categoriaDoacaoG2.getSelectedItem().toString();
                    String descricao2 = editDescricao2.getText().toString();

                    if (nome2.isEmpty() || telefone2.isEmpty() || email2.isEmpty() || descricao2.isEmpty() || formadoacao2.isEmpty()) {
                        Toast.makeText(getContext(), "Todos os Campos devem ser preenchidos!", Toast.LENGTH_LONG).show();
                    } else if (categoria2.equals("Selecione")) {
                        Toast.makeText(getContext(), "Os campos devem estar selecionados!", Toast.LENGTH_LONG).show();
                    } else {
                        String url = HOST + "/createbkp.php";
                        Ion.with(getContext()) //Passa a url do arquivo php, create que vai ser responsável por inserir no banco
                                .load(url)
                                .setMultipartParameter("nome", nome2) //passando as váriaveis do ediText como parametro
                                .setMultipartParameter("telefone", telefone2)
                                .setMultipartParameter("email", email2)
                                .setMultipartParameter("descricao", descricao2)
                                .setMultipartParameter("categoria", categoria2)
                                .asJsonObject()
                                .setCallback(new FutureCallback<JsonObject>() {
                                    @Override
                                    public void onCompleted(Exception e, JsonObject result) {
                                        // então aqui é o retorno do php
                                        try {
                                            String RETORNO = result.get("CADASTRO").getAsString(); //caso JSON retorne
                                            if (RETORNO.equals("SUCESSO")) { //MSG sucesso
                                                editDescricao2.setText("");
                                                int idretorno = Integer.parseInt(result.get("ID").getAsString()); // ele pega o id e a foto
                                                Toast.makeText(getContext(), "Doação realizada com sucesso, Obrigado!!", Toast.LENGTH_LONG).show(); //msg dizendo que salvou com sucesso
                                            } else {
                                                Toast.makeText(getContext(), "Ops! Ocorreu um erro.!", Toast.LENGTH_LONG).show(); // em caso de erro irá exibir essa mensagem
                                            }
                                        }  catch(Exception ex) {}

                                    }
                                });



                    }


                }
            });
        } catch (Exception erro) {}


        //Método inserir Produto
        try {
            btnDoacaoG.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                     String nome = editNome.getText().toString();
                     String telefone = editTelefone.getText().toString();
                     String email = editEmail.getText().toString();
                     String categoria = categoriaDoacaoG.getSelectedItem().toString();
                     String descricao = editDescricao.getText().toString();

                     if(nome.isEmpty() || telefone.isEmpty() || email.isEmpty() || descricao.isEmpty() || categoria.isEmpty()) {
                         Toast.makeText(getContext(), "Todos os Campos devem ser preenchidos!", Toast.LENGTH_LONG).show();
                     } else if(categoria.equals("Selecione")) {
                         Toast.makeText(getContext(), "Os campos devem estar selecionados!", Toast.LENGTH_LONG).show();
                     } else {
                         String url = HOST + "/createbkp.php";
                         Ion.with(getContext()) //Passa a url do arquivo php, create que vai ser responsável por inserir no banco
                                 .load(url)
                                 .setMultipartParameter("nome", nome) //passando as váriaveis do ediText como parametro
                                 .setMultipartParameter("telefone", telefone)
                                 .setMultipartParameter("email", email)
                                 .setMultipartParameter("descricao", descricao)
                                 .setMultipartParameter("categoria", categoria)
                                 .asJsonObject()
                                 .setCallback(new FutureCallback<JsonObject>() {
                                     @Override
                                     public void onCompleted(Exception e, JsonObject result) {
                                         // então aqui é o retorno do php
                                         try {
                                             String RETORNO = result.get("CADASTRO").getAsString(); //caso JSON retorne
                                             if (RETORNO.equals("SUCESSO")) { //MSG sucesso
                                                 limpar();
                                                 int idretorno = Integer.parseInt(result.get("ID").getAsString()); // ele pega o id e a foto
                                                 Toast.makeText(getContext(), "Doação realizada com sucesso, Obrigado!!", Toast.LENGTH_LONG).show(); //msg dizendo que salvou com sucesso
                                             } else {
                                                 Toast.makeText(getContext(), "Ops! Ocorreu um erro.!", Toast.LENGTH_LONG).show(); // em caso de erro irá exibir essa mensagem
                                             }
                                         }  catch(Exception ex) {}

                                     }
                                 });
                     }

                }
            });

        } catch (Exception erro) {
            editNome.setError(erro.toString());
            Toast.makeText(getContext(), "ERRO: " + erro, Toast.LENGTH_LONG).show();
        }


            btnVisual.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                            llConteudo.setVisibility(View.VISIBLE);
                            llConteudo2.setVisibility(View.GONE);
                }
            });

        btnVisual2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llConteudo2.setVisibility(View.VISIBLE);
                llConteudo.setVisibility(View.GONE);
            }
        });

        // Spinner Categorias produtos
        Spinner spinner = (Spinner) v.findViewById(R.id.categoriaDoacaoG);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.categoria, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            protected Adapter initializedAdapter = null;

            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                categoria = parentView.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });


        spinner = (Spinner) v.findViewById(R.id.categoriaDoacaoG2);
        adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.formadoacao2, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            protected Adapter initializedAdapter = null;

            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                formadoacao2 = parentView.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });



        return v;
    }

 private void limpar() {
        editDescricao.setText("");
        categoriaDoacaoG.setSelection(0);
 }




}
