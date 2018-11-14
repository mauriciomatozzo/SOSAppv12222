package com.example.andr.sosappv1;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.theartofdev.edmodo.cropper.CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE;

public class Painel extends AppCompatActivity {

    EditText editNome, editTelefone, editEmail, editId;
    Button btnNovo, btnSalvar, btnExcluir;
    ListView listViewContatos;
    ImageView imgFoto;

    private String HOST = "http://centraldobem.ads.cnecsan.edu.br/painel/paginas/app";

    private int itemClicado;
    private String fotoClicada;

    ContatosAdapter contatosAdapter;
    List<Contato> lista;

    File fotoSelecionada;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_painel);
        setTitle("Painel");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        editNome = (EditText) findViewById(R.id.editNome);
        editTelefone = (EditText) findViewById(R.id.editTelefone);
        editEmail = (EditText) findViewById(R.id.editEmail);
        editId = (EditText) findViewById(R.id.editId);

        btnNovo = (Button) findViewById(R.id.btnNovo);
        btnSalvar = (Button) findViewById(R.id.btnSalvar);
        btnExcluir = (Button) findViewById(R.id.btnExcluir);

        listViewContatos = (ListView) findViewById(R.id.listViewContatos);

        imgFoto = (ImageView) findViewById(R.id.imgFoto);

        lista = new ArrayList<Contato>();
        contatosAdapter = new ContatosAdapter(Painel.this, lista);

        listViewContatos.setAdapter(contatosAdapter);

        try {
            listaContatos();
        } catch (Exception erro) {}


        imgFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1, 1)
                        .start(Painel.this);
            }
        });

        btnNovo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                limpar();
            }
        });


        //Método update
        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //referencia as variaveis
                final String id = editId.getText().toString();
                final String nome = editNome.getText().toString();
                final String telefone = editTelefone.getText().toString();
                final String email = editEmail.getText().toString();

                //dois if, o primeiro para verificar se nenhum campo está vazio
                if(nome.isEmpty() || telefone.isEmpty() || email.isEmpty()) {
                    Toast.makeText(Painel.this, "Todos os Campos devem ser preenchidos!", Toast.LENGTH_LONG).show();
                } else if(id.isEmpty()) { //segundo verifica se o id está vazio, caso for selecionado cai no else
                    Toast.makeText(Painel.this, "Selecione alguém!", Toast.LENGTH_LONG).show();
                } else {
                    //que vai fazer a integração com php, json
                String url = HOST + "/update.php"; //seta o arquivo php que vai ser responsável por atualizar nossos dados
                Ion.with(Painel.this)
                        .load(url)
                        .setMultipartParameter("id_usuario", id) //parametros passados
                        .setMultipartParameter("nome", nome)
                        .setMultipartParameter("telefone", telefone)
                        .setMultipartParameter("email", email)
                        .setMultipartFile("foto", fotoSelecionada)
                        .asJsonObject()
                        .setCallback(new FutureCallback<JsonObject>() {
                            @Override
                            public void onCompleted(Exception e, JsonObject result) {
                                // Aqui dentro é o do Retorno php
                                try { // um try catch pra tratamento de erro, por exemplo para o programa não fechar do nada caso aconteça algum erro inesperado
                                    String RETORNO = result.get("ATUALIZAR").getAsString();
                                    String fotoRetornada = result.get("FOTO").getAsString(); // variavel foto para adicionar na lista

                                    if (RETORNO.equals("SUCESSO")) {

                                        //atualizando lista
                                        Contato c = new Contato(); //instancia a lista

                                        c.setId(Integer.parseInt(id));  //passa pra lista esses parametros retornados do json
                                        c.setNome(nome);
                                        c.setTelefone(telefone);
                                        c.setEmail(email);
                                        c.setFoto(fotoRetornada);

                                        lista.set(itemClicado, c); //então atualiza a lista da activity Tab2 e Painel
                                        Tab2.lista.set(itemClicado, c);

                                        contatosAdapter.notifyDataSetChanged(); //Lista atualizada
                                        Tab2.contatosAdapter.notifyDataSetChanged();

                                        limpar(); // limpa os campos
                                        Toast.makeText(Painel.this, "Usuário atualizado com sucesso!", Toast.LENGTH_LONG).show(); //msg caso de tudo certo
                                    } else {
                                        Toast.makeText(Painel.this, "Ops! Ocorreu um erro.!", Toast.LENGTH_LONG).show(); //msg de erro
                                    }
                                }  catch(Exception ex) {}

                            }
                        });
            }}
        });

        //Método exclusão
        btnExcluir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = editId.getText().toString(); // String id vai receber o valor que está na editID, ou seja, ela va armazenar um valor
                if(id.isEmpty()) { //Verifica se o campo id está vazio
                    Toast.makeText(Painel.this, "Selecione alguém!", Toast.LENGTH_LONG).show();
                } else { //senão
                    //delete
                    String url = HOST + "/delete.php"; //Monta o caminho do arquivo php que está no servidor kraft

                    Ion.with(Painel.this) //passa a url
                            .load(url)
                            .setBodyParameter("id_usuario", id) //passando um corpo de parametro
                            .setBodyParameter("foto", fotoClicada)
                            .asJsonObject()
                            .setCallback(new FutureCallback<JsonObject>() {
                                @Override
                                public void onCompleted(Exception e, JsonObject result) { //método que retorna os dados php
                                    // Retorno php
                                    try { //try catch pra fazer o tratamento de erro do método exlcuir, pra que não trave no momento que estiver excluindo
                                        String RETORNO = result.get("DELETE").getAsString();
                                        if (RETORNO.equals("SUCESSO")) { //caso json retorne msg com sucesso
                                            lista.remove(itemClicado); //vai ser removido o item da lista, que foi clicado na lista
                                            contatosAdapter.notifyDataSetChanged(); //atualiza essa lista
                                            limpar(); // limpa os campos
                                            Toast.makeText(Painel.this, "Usuário deletado com sucesso!", Toast.LENGTH_LONG).show(); //msg caso de tudo certo
                                        } else {
                                            Toast.makeText(Painel.this, "Ops! Ocorreu um erro ao deletar o usuário", Toast.LENGTH_LONG).show(); //msg de erro
                                        }
                                    }  catch(Exception ex) {}

                                }
                            });

                }

            }
        });


        listViewContatos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Contato c = (Contato) adapterView.getAdapter().getItem(position);

                editId.setText(String.valueOf(c.getId()));
                editNome.setText(c.getNome());
                editTelefone.setText(c.getTelefone());
                editEmail.setText(c.getEmail());

                String fotoRecebida = HOST + "/" + c.getFoto();
                Picasso.get().load(fotoRecebida).into(imgFoto); //url recebida e que seja mostrado no itemFoto


                itemClicado = position;
                fotoClicada = c.getFoto();

            }
        });


    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == Activity.RESULT_OK) {
                Uri resultUri = result.getUri();

                imgFoto.setImageURI(resultUri);

                fotoSelecionada = new File(resultUri.getPath());

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }


    public void limpar() {
        editId.setText("");
        editNome.setText("");
        editTelefone.setText("");
        editEmail.setText("");
        imgFoto.setImageResource(R.drawable.facebook_avatar);

        editNome.requestFocus();
    }


    //listar dados do servidor
    private void listaContatos() {

        String url =  HOST + "/read.php";

        Ion.with(getBaseContext())
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
