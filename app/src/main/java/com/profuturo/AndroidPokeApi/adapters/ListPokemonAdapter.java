package com.profuturo.AndroidPokeApi.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.profuturo.AndroidPokeApi.DetalleActivity;
import com.profuturo.AndroidPokeApi.R;
import com.profuturo.AndroidPokeApi.model.Pokemon;

import java.util.ArrayList;
import java.util.List;

public class ListPokemonAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int ITEM = 0;
    private final int LOADING = 1;
    private ArrayList<Pokemon> pokemonArrayList;
    private Context mContext;

    private boolean isLoadingAdded = false;
    private boolean retryPageLoad = false;

    private ListPokemonAdapterCallback mCallback;
    private String errorMsg;

    public ListPokemonAdapter(Context mContext) {
        this.mContext = mContext;
        this.mCallback = (ListPokemonAdapterCallback) mContext;
        pokemonArrayList = new ArrayList<>();
    }

    public List<Pokemon> getPokemon() {
        return pokemonArrayList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

        switch (viewType) {
            case ITEM:
                View viewItem = inflater.inflate(R.layout.item_pokemon, viewGroup, false);
                viewHolder = new ViewHolderPokemon(viewItem);
                break;
            case LOADING:
                View viewLoading = inflater.inflate(R.layout.item_progress, viewGroup, false);
                viewHolder = new LoadingVH(viewLoading);
                break;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        switch (getItemViewType(i)) {
            case ITEM:
                ViewHolderPokemon viewHolderPokemon = (ViewHolderPokemon) viewHolder;
                viewHolderPokemon.nombrePokemon.setText(pokemonArrayList.get(i).getName());
                Glide.with(mContext)
                        .load("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/" + pokemonArrayList.get(i).getNumber() + ".png")
                        .into(viewHolderPokemon.ivFotoPokemon);
                break;
            case LOADING:
                LoadingVH loadingVH = (LoadingVH) viewHolder;
                if (retryPageLoad) {
                    loadingVH.mErrorLayout.setVisibility(View.VISIBLE);
                    loadingVH.mProgressBar.setVisibility(View.GONE);

                    loadingVH.mErrorTxt.setText(
                            errorMsg != null ?
                                    errorMsg :
                                    mContext.getString(R.string.error_msg));

                } else {
                    loadingVH.mErrorLayout.setVisibility(View.GONE);
                    loadingVH.mProgressBar.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    @Override
    public int getItemCount() {
        return pokemonArrayList == null ? 0 : pokemonArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (position == pokemonArrayList.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }

    public void agregarPokemones(ArrayList<Pokemon> pokemonArrayList) {
        this.pokemonArrayList.addAll(pokemonArrayList);
        notifyDataSetChanged();
    }

    public void addLoadingFooter() {
        isLoadingAdded = true;
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;
    }

    private Pokemon getItem(int position) {
        return pokemonArrayList.get(position);
    }

    public void showRetry(boolean show, String errorMsg) {
        retryPageLoad = show;
        notifyItemChanged(pokemonArrayList.size() - 1);

        if (errorMsg != null) this.errorMsg = errorMsg;
    }

    public void addAll(List<Pokemon> pokemonResults) {
        for (Pokemon result : pokemonResults) {
            add(result);
        }
    }

    private void add(Pokemon result) {
        pokemonArrayList.add(result);
        notifyItemInserted(pokemonArrayList.size() - 1);
    }

    public void clear() {
        isLoadingAdded = false;
        while (getItemCount() > 0) {
            remove(getItem(0));
        }
    }

    private void remove(Pokemon r) {
        int position = pokemonArrayList.indexOf(r);
        if (position > -1) {
            pokemonArrayList.remove(position);
            notifyItemRemoved(position);
        }
    }

    class LoadingVH extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ProgressBar mProgressBar;
        private ImageButton mRetryBtn;
        private TextView mErrorTxt;
        private LinearLayout mErrorLayout;

        LoadingVH(View itemView) {
            super(itemView);

            mProgressBar = itemView.findViewById(R.id.loadmore_progress);
            mRetryBtn = itemView.findViewById(R.id.loadmore_retry);
            mErrorTxt = itemView.findViewById(R.id.loadmore_errortxt);
            mErrorLayout = itemView.findViewById(R.id.loadmore_errorlayout);

            mRetryBtn.setOnClickListener(this);
            mErrorLayout.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.loadmore_retry:
                case R.id.loadmore_errorlayout:

                    showRetry(false, null);
                    mCallback.recargarPagina();

                    break;
            }
        }
    }

    class ViewHolderPokemon extends RecyclerView.ViewHolder {
        private TextView nombrePokemon;
        private CardView cvTarjetas;
        private ImageView ivFotoPokemon;

        ViewHolderPokemon(@NonNull final View itemView) {
            super(itemView);
            nombrePokemon = itemView.findViewById(R.id.tvNombrePokemon);
            ivFotoPokemon = itemView.findViewById(R.id.ivFotoPokemon);
            cvTarjetas = itemView.findViewById(R.id.cvTarjetas);

            cvTarjetas.setOnClickListener(v -> {
                Intent intent = new Intent(mContext, DetalleActivity.class);
                intent.putExtra("POKEMON", pokemonArrayList.get(getAdapterPosition()));
                mContext.startActivity(intent);
            });
        }
    }
}
