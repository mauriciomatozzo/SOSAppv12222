package com.example.andr.sosappv1;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.util.List;

public class CadastroDoador extends AppCompatActivity {

    private EditText nomeCad, usuarioCad, senhaCad, confirmsenhaCad, emailCad, telefoneCad;
    private Button btnCad, btnFoto;
    private String HOST = "http://centraldobem.ads.cnecsan.edu.br/painel/paginas/app";
    private ImageView imgFoto;

    File fotoSelecionada;

    ContatosAdapter contatosAdapter;
    List<Contato> lista;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_doador);
        setTitle("Cadastro");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Referenciando
        nomeCad = (EditText)findViewById(R.id.nomeCad);
        usuarioCad = (EditText)findViewById(R.id.usuarioCad);
        senhaCad = (EditText)findViewById(R.id.senhaCad);
        confirmsenhaCad = (EditText)findViewById(R.id.confirmsenhaCad);
        emailCad = (EditText)findViewById(R.id.emailCad);
        telefoneCad = (EditText)findViewById(R.id.telefoneCad);

        btnCad = (Button) findViewById(R.id.btnCad);
        btnFoto = (Button) findViewById(R.id.btnFoto);

        imgFoto = (ImageView) findViewById(R.id.imgFoto);

        btnFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    CropImage.activity()
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setAspectRatio(1, 1)
                            .start(CadastroDoador.this);
                } catch (Exception erro) {
                    Toast.makeText(CadastroDoador.this, "ERRO " + erro, Toast.LENGTH_LONG).show();

                }


            }
        });

        //if (TextUtils.isEmpty(name)) {
            //editTextName.setError("Please enter name");
            //editTextName.requestFocus();
            //return;
        //}

        /*CADASTRO DOADOR*/
        btnCad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String nome = nomeCad.getText().toString();
                String usuario = usuarioCad.getText().toString();
                String senha = senhaCad.getText().toString();
                String confirmsenha = confirmsenhaCad.getText().toString();
                final String email = emailCad.getText().toString();
                final String telefone = telefoneCad.getText().toString();
                String URL = HOST + "/cadastrarDoador.php";

                if (confirmsenha.equals(senha)) {
                    //if (nome.isEmpty() || usuario.isEmpty() || senha.isEmpty() || email.isEmpty() || telefone.isEmpty())
                     if (nome.isEmpty() || telefone.isEmpty() ||  email.isEmpty()) {
                        Toast.makeText(CadastroDoador.this, "Todos os Campos devem ser preenchidos!", Toast.LENGTH_LONG).show();
                    } else {

                        Ion.with(CadastroDoador.this)
                                .load(URL)
                                .setMultipartParameter("nome_app", nome)
                                .setMultipartParameter("usuario_app", usuario)
                                .setMultipartParameter("senha_app", senha)
                                .setMultipartParameter("email_app", email)
                                .setMultipartParameter("telefone_app", telefone)
                                .setMultipartFile("foto", fotoSelecionada)
                                .asJsonObject()
                                .setCallback(new FutureCallback<JsonObject>() {
                                    @Override
                                    public void onCompleted(Exception e, JsonObject result) {
                                        try {
                                            String RETORNO = result.get("CADASTRO").getAsString();
                                            if (RETORNO.equals("USUARIO_ERRO")) {
                                                Toast.makeText(CadastroDoador.this, "Ops! Este usuário já está cadastrado.", Toast.LENGTH_LONG).show();
                                            } else if (RETORNO.equals("SUCESSO")) {
                                                int idretorno = Integer.parseInt(result.get("ID").getAsString());
                                                String fotoRetornada = result.get("FOTO").getAsString();

                                                try {
                                                    //atualizando lista
                                                    Contato c = new Contato(); //cria uma instância da nossa lista

                                                    // e seta ela passsando pra lista os parametros inseridos
                                                    c.setId(idretorno);
                                                    c.setNome(nome);
                                                    c.setTelefone(telefone);
                                                    c.setEmail(email);
                                                    c.setFoto(fotoRetornada);
                                                    lista.add(c); //add na lista

                                                    contatosAdapter.notifyDataSetChanged();

                                                } catch (Exception erro) {}

                                                limpar();
                                                Toast.makeText(CadastroDoador.this, "Cadastro realizado com sucesso!! ID:" + idretorno, Toast.LENGTH_LONG).show();

                                            } else {
                                                Toast.makeText(CadastroDoador.this, "Ops! Ocorreu um erro.!", Toast.LENGTH_LONG).show();
                                            }
                                        } catch (Exception erro) {}
                                    }
                                });
                    }

                } else
                    Toast.makeText(CadastroDoador.this, "As senhas não conferem, Verifique!", Toast.LENGTH_LONG).show();
            }
        });

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();

                imgFoto.setImageURI(resultUri);

                fotoSelecionada = new File(resultUri.getPath());

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private void limpar() {
        nomeCad.setText("");
        usuarioCad.setText("");
        senhaCad.setText("");
        confirmsenhaCad.setText("");
        emailCad.setText("");
        telefoneCad.setText("");
        imgFoto.setImageResource(R.drawable.facebook_avatar);

        nomeCad.requestFocus();
    }
}
