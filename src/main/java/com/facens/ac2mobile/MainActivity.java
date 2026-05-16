package com.facens.ac2mobile;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facens.ac2mobile.adapter.FilmeAdapter;
import com.facens.ac2mobile.model.Filme;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private EditText edtNome, edtAnoLanc, edtNotaPes;
    private Spinner spnTipo, spnGenero;
    private CheckBox jaAssistiu;
    private RecyclerView recyclerFilmes;
    private List<Filme> listaFilmes = new ArrayList<>();
    private FilmeAdapter filmeAdapter;
    private Filme filmeEditando = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = FirebaseFirestore.getInstance();

        edtNome = findViewById(R.id.edtNome);
        edtAnoLanc = findViewById(R.id.edtAnoLanc);
        edtNotaPes = findViewById(R.id.edtNotaPes);
        spnGenero = findViewById(R.id.spinnerGenero);
        spnTipo = findViewById(R.id.spinnerTipo);
        jaAssistiu = findViewById(R.id.checkBoxAssistiu);
        recyclerFilmes = findViewById(R.id.recyclerFilmes);
        recyclerFilmes.setLayoutManager(new LinearLayoutManager(this));
        filmeAdapter = new FilmeAdapter(listaFilmes);
        recyclerFilmes.setAdapter(filmeAdapter);

        findViewById(R.id.btnSalvar).setOnClickListener(v -> salvarFilme());

        carregarProdutos();

        ArrayAdapter<CharSequence> adaptery = ArrayAdapter.createFromResource(
                this, R.array.tipos, android.R.layout.simple_spinner_item);
        adaptery.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnTipo.setAdapter(adaptery);

        ArrayAdapter<CharSequence> adapterx = ArrayAdapter.createFromResource(
                this, R.array.genero, android.R.layout.simple_spinner_item);
        adapterx.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnGenero.setAdapter(adapterx);

    }

    private void salvarFilme () {
        String nome = edtNome.getText().toString();
        String anoTexto = edtAnoLanc.getText().toString();
        String notaTexto = edtNotaPes.getText().toString();
        String genero = spnGenero.getSelectedItem().toString();
        String tipo = spnTipo.getSelectedItem().toString();
        String assistiu = validaCheckBox(jaAssistiu.isChecked());

        if(nome.isEmpty() || genero.isEmpty() || tipo.isEmpty() ||
                anoTexto.isEmpty() || notaTexto.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos.", Toast.LENGTH_SHORT).show();
            return;
        }

        int anoLanc;
        int notaPes;

        try {
            anoLanc = Integer.parseInt(anoTexto);
            notaPes = Integer.parseInt(notaTexto);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Ano e nota precisam ser números.", Toast.LENGTH_SHORT).show();
            return;
        }

        if(validarNotaPessoa(notaPes)){
            Toast.makeText(this, "Insira uma nota válida", Toast.LENGTH_SHORT).show();
            return;
        }
        if(filmeEditando == null) {
            Filme f = new Filme(null, nome, tipo, genero, anoLanc, notaPes, assistiu);

            db.collection("filmes")
                    .add(f)
                    .addOnSuccessListener(doc -> {
                        f.setId(doc.getId());
                        Toast.makeText(this, "Filme salvo!", Toast.LENGTH_SHORT).show();
                    });
        } else {
            filmeEditando.setNome(nome);
            filmeEditando.setGenero(genero);
            filmeEditando.setTipo(tipo);
            filmeEditando.setAnoLancamento(anoLanc);
            filmeEditando.setNotaPessoal(notaPes);
            filmeEditando.setJaAssistiu(assistiu);

            db.collection("filmes").document(filmeEditando.getId())
                    .set(filmeEditando)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Filme atualizado!", Toast.LENGTH_SHORT).show();
                        limparCampos();
                        carregarProdutos();
                    });
        }
        carregarProdutos();
        limparCampos();
    }

    private void limparCampos() {
        edtNome.setText("");
        spnTipo.setSelection(0);
        spnGenero.setSelection(0);
        edtNotaPes.setText("");
        edtAnoLanc.setText("");
        jaAssistiu.setChecked(false);
        filmeEditando = null;
        ((Button) findViewById(R.id.btnSalvar)).setText("Salvar Produto");
    }

    private void carregarProdutos() {
        db.collection("filmes")
                .get()
                .addOnSuccessListener(query -> {
                    listaFilmes.clear();
                    for (QueryDocumentSnapshot doc : query) {
                        Filme f = doc.toObject(Filme.class);
                        f.setId(doc.getId());
                        listaFilmes.add(f);
                    }
                    filmeAdapter.notifyDataSetChanged();
                });

        filmeAdapter.setOnItemClickListener(f -> {
            edtNome.setText(f.getNome());
            edtAnoLanc.setText(String.valueOf(f.getAnoLancamento()));
            edtNotaPes.setText(String.valueOf(f.getNotaPessoal()));

            spnGenero.setSelection(buscarItemPorPosicao(spnGenero, f.getGenero()));
            spnTipo.setSelection(buscarItemPorPosicao(spnTipo, f.getTipo()));

            jaAssistiu.setChecked(validaCheckBox(f.getJaAssistiu()));
            filmeEditando = f;
            ((Button) findViewById(R.id.btnSalvar)).setText("Atualizar Produto");
        });

    }

    private boolean validarNotaPessoa(int nota) {
        if(nota > 5){
            return true;
        }
        return false;
    }

    private int buscarItemPorPosicao(Spinner spin, String val) {
        for(int i = 0; i < spin.getCount(); i++) {
            String item = spin.getItemAtPosition(i).toString();

            if(item.equalsIgnoreCase(item)) {
                return i;
            }
        }
        return 0;
    }

    private String validaCheckBox(boolean check) {
        if(check){
            return "Assistiu";
        }
        return "Nao assistiu";
    }

    private boolean validaCheckBox(String check) {
        if(check.equalsIgnoreCase("Sim")){
            return true;
        }
        return false;
    }

}