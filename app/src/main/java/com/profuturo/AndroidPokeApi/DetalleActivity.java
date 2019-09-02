package com.profuturo.AndroidPokeApi;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.profuturo.AndroidPokeApi.model.Move;
import com.profuturo.AndroidPokeApi.model.Pokemon;
import com.profuturo.AndroidPokeApi.model.PokemonsMoves;
import com.profuturo.AndroidPokeApi.model.PokemonsStats;
import com.profuturo.AndroidPokeApi.retrofit.PokemonApi;
import com.profuturo.AndroidPokeApi.retrofit.PokemonService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressLint("PrivateResource")
public class DetalleActivity extends AppCompatActivity {

    private static final String DETALLE_TAG = "DETAIL_ACTIVITY";
    Intent mIntent;
    Toolbar mToolbar;
    TextView tvNombrePokemon, tvDetalleAtaque, tvPower, tvAccuracy, tvWeight, tvHeight, tvBaseExperience;
    ImageView ivExpandedFotoPokemon;
    Pokemon pokemon;
    CollapsingToolbarLayout collapsingToolbarLayout;
    private PokemonService pokemonService;
    private ArrayList<Move> moves;
    private int power, accuracy, baseExperience, height, weight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle);
        pokemonService = PokemonApi.getClient(this).create(PokemonService.class);
        moves = new ArrayList<>();

        inicializaVistas();
        obtenerDatos();
        setSupportActionBar(mToolbar);
        setToolbarMethods();
    }

    private void setMovesInfo() {
        tvDetalleAtaque.setText(moves.get(4).getFlavor_text());
        tvPower.setText(String.valueOf(power));
        tvAccuracy.setText(String.valueOf(accuracy));
    }

    private void setStatsInfo() {
        tvHeight.setText(String.valueOf(height));
        tvWeight.setText(String.valueOf(weight));
        tvBaseExperience.setText(String.valueOf(baseExperience));
    }

    private void setToolbarMethods() {
        mToolbar.setNavigationIcon(android.support.v7.appcompat.R.drawable.abc_ic_ab_back_material);
        mToolbar.setNavigationOnClickListener(
                view -> finish()
        );
        collapsingToolbarLayout.setTitle(pokemon.getName().toUpperCase());
        collapsingToolbarLayout.setExpandedTitleColor(getColor(R.color.black));
    }

    private void obtenerDatos() {
        mIntent = getIntent();
        pokemon = (Pokemon) mIntent.getSerializableExtra("POKEMON");
        tvNombrePokemon.setText(pokemon.getName());
        Glide.with(this)
                .load("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/" + pokemon.getNumber() + ".png")
                .into(ivExpandedFotoPokemon);
        cargaEstadisticas();
    }

    private void inicializaVistas() {
        tvNombrePokemon = findViewById(R.id.tvDetailNombrePokemon);
        tvDetalleAtaque = findViewById(R.id.tvDetalleAtaque);
        ivExpandedFotoPokemon = findViewById(R.id.ivExpandedFotoPokemon);
        tvPower = findViewById(R.id.tvPower);
        tvAccuracy = findViewById(R.id.tvAccuracy);
        tvWeight = findViewById(R.id.tvWeight);
        tvHeight = findViewById(R.id.tvHeight);
        tvBaseExperience = findViewById(R.id.tvBaseExperience);
        mToolbar = findViewById(R.id.toolbar);
        collapsingToolbarLayout = findViewById(R.id.collapsingToolbar);
    }

    private Call<PokemonsMoves> callPokemonMovesService() {
        return pokemonService.obtenerMovimientos(pokemon.getNumber());
    }

    private Call<PokemonsStats> callPokemonStatsService() {
        return pokemonService.obtenerStats(pokemon.getNumber());
    }

    public void cargaEstadisticas() {
//        callPokemonMovesService().enqueue(new Callback<PokemonsMoves>() {
//            @Override
//            public void onResponse(Call<PokemonsMoves> call, Response<PokemonsMoves> response) {
//                PokemonsMoves pokemons = response.body();
//                List<Move> results = pokemons.getflavor_text_entries();
//                moves.addAll(results);
//                power = pokemons.getPower();
//                accuracy = pokemons.getAccuracy();
//                setMovesInfo();
//            }
//
//            @Override
//            public void onFailure(Call<PokemonsMoves> call, Throwable t) {
//                t.printStackTrace();
//                Log.e(DETALLE_TAG.concat("Moves"), t.getMessage());
//            }
//        });

        callPokemonStatsService().enqueue(new Callback<PokemonsStats>() {
            @Override
            public void onResponse(Call<PokemonsStats> call, Response<PokemonsStats> response) {
                PokemonsStats pokemonsStats = response.body();
                height = pokemonsStats.getHeight();
                weight = pokemonsStats.getWeight();
                baseExperience = pokemonsStats.getBase_experience();
                setStatsInfo();
            }

            @Override
            public void onFailure(Call<PokemonsStats> call, Throwable t) {
                t.printStackTrace();
                Log.e(DETALLE_TAG.concat("Stats"), t.getMessage());
            }
        });
    }
}
