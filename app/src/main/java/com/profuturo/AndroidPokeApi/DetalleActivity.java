package com.profuturo.AndroidPokeApi;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.profuturo.AndroidPokeApi.model.Pokemon;

public class DetalleActivity extends AppCompatActivity {

    Intent mIntent;
    TextView tvNombrePokemon;
    ImageView ivExpandedFotoPokemon;
    Pokemon pokemon;
    CollapsingToolbarLayout collapsingToolbarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle);
        tvNombrePokemon = findViewById(R.id.tvDetailNombrePokemon);
        ivExpandedFotoPokemon = findViewById(R.id.ivExpandedFotoPokemon);

        mIntent = getIntent();
        pokemon = (Pokemon) mIntent.getSerializableExtra("POKEMON");
        tvNombrePokemon.setText(pokemon.getName());
        Glide.with(this)
                .load("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/" + pokemon.getNumber() + ".png")
                .into(ivExpandedFotoPokemon);
        final Toolbar mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        collapsingToolbarLayout = findViewById(R.id.collapsingToolbar);
        collapsingToolbarLayout.setTitle(pokemon.getName());
        collapsingToolbarLayout.setExpandedTitleColor(getColor(R.color.colorPrimaryDark));
    }
}
