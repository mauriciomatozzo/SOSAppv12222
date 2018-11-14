package com.example.andr.sosappv1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

public class Login extends AppCompatActivity {

    private EditText editUsuario, editSenha;
    private Button btnlogin;
    public static String nomeG, telefoneG, emailG;
    public static JsonArray entidadesG;
    private String HOST = "http://centraldobem.ads.cnecsan.edu.br/painel/paginas/app";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle("Login");

        editUsuario = (EditText)findViewById(R.id.usuarioLogin);
        editSenha = (EditText)findViewById(R.id.senhaLogin);

        btnlogin = (Button) findViewById(R.id.btnlogin);

        /*LOGIN USUÁRIO*/
        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String usuario = editUsuario.getText().toString();
                String senha = editSenha.getText().toString();
                String URL = HOST + "/login.php";

                if (usuario.isEmpty() || senha.isEmpty()) {
                    Toast.makeText(Login.this, "Todos os Campos devem ser preenchidos!", Toast.LENGTH_LONG).show();
                } else {
                    Ion.with(Login.this)
                            .load(URL)
                            .setBodyParameter("usuario_app", usuario)
                            .setBodyParameter("senha_app", senha)
                            .asJsonObject()
                            .setCallback(new FutureCallback<JsonObject>() {
                                @Override
                                public void onCompleted(Exception e, JsonObject result) {

                                    try {
                                        String RETORNO = result.get("LOGIN").getAsString();
                                        if (RETORNO.equals("ERRO")) {
                                            Toast.makeText(Login.this, "Ops! Usuário ou senha incorreto! Verifique..", Toast.LENGTH_LONG).show();
                                        } else if (RETORNO.equals("SUCESSO")) {
                                            nomeG = result.get("Nome").getAsString();
                                            telefoneG = result.get("Telefone").getAsString();
                                            emailG = result.get("Email").getAsString();

                                            try {
                                                Intent abrirDashboard = new Intent(Login.this, MainActivity.class);
                                                startActivity(abrirDashboard);
                                            } catch (Exception erro) {
                                                Toast.makeText(Login.this, "Ops! Ocorreu um erro.!" ,Toast.LENGTH_LONG).show();
                                            }

                                        } else {
                                            Toast.makeText(Login.this, "Ops! Ocorreu um erro.!", Toast.LENGTH_LONG).show();
                                        }
                                    } catch (Exception erro){ Toast.makeText(Login.this, "Cadastro indisponível no momento.!", Toast.LENGTH_LONG).show(); }

                                }
                            });
                }
            }
        });
    }

    public void CadastrarDoador(View v){
        Intent intent = new Intent(this, CadastroDoador.class);
        startActivity(intent);
    }

}
