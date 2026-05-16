package com.facens.ac2mobile.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import android.widget.Toast;


import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import androidx.recyclerview.widget.RecyclerView;

import com.facens.ac2mobile.R;
import com.facens.ac2mobile.model.Filme;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class FilmeAdapter extends RecyclerView.Adapter<FilmeAdapter.ViewHolder> {

    private List<Filme> filmes;

    public interface OnItemClickListener {
        void onItemClick(Filme filme);
    }

    private OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public FilmeAdapter(List<Filme> produtos) {
        this.filmes = produtos;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_2, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int pos) {
        Filme f = filmes.get(pos);
        holder.txt1.setText(f.getNome());
        holder.txt2.setText(f.getTipo()+" - "+f.getGenero()+" - "+f.getAnoLancamento()+" - "+f.getNotaPessoal()+" - "+f.getJaAssistiu());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(f);
            }
        });

        holder.itemView.setOnTouchListener(new View.OnTouchListener() {
            private long lastClickTime = 0;

            @Override
            public boolean onTouch(View v, android.view.MotionEvent event) {
                if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {
                    long currentTime = System.currentTimeMillis();
                    if (currentTime - lastClickTime < 300) {
                        deletarProduto(f.getId(), holder.getAdapterPosition(), v);
                    }
                    lastClickTime = currentTime;
                }
                return false;
            }
        });
    }

    private void deletarProduto(String idDocumento, int position, View view) {

        if (idDocumento == null || idDocumento.isEmpty()) {
            Toast.makeText(view.getContext(), "ID do filme inválido", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseFirestore.getInstance().collection("filmes")
                .document(idDocumento)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    filmes.remove(position);
                    notifyItemRemoved(position);
                    Toast.makeText(view.getContext(), "Filme deletado!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(
                            view.getContext(),
                            "Erro ao deletar: " + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }


    @Override
    public int getItemCount() {
        return filmes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txt1, txt2;
        public ViewHolder(View itemView) {
            super(itemView);
            txt1 = itemView.findViewById(android.R.id.text1);
            txt2 = itemView.findViewById(android.R.id.text2);
        }
    }
}
