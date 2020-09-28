package com.profuturo.AndroidPokeApi.retrofit;

import com.profuturo.AndroidPokeApi.model.Pokemons;
import com.profuturo.AndroidPokeApi.model.PokemonsMoves;
import com.profuturo.AndroidPokeApi.model.PokemonsStats;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface PokemonService {
    @GET("pokemon")
    Call<Pokemons> obtenerListaPokemon(@Query("limit") int limit, @Query("offset") int offset);

    @GET("move/{number}/")
    Call<PokemonsMoves> obtenerMovimientos(@Path("number") int number);

    @GET("pokemon/{number}/")
    Call<PokemonsStats> obtenerStats(@Path("number") int number);
}
