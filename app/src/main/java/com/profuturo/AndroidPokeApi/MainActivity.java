package com.profuturo.AndroidPokeApi;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.profuturo.AndroidPokeApi.adapters.ListPokemonAdapter;
import com.profuturo.AndroidPokeApi.adapters.ListPokemonAdapterCallback;
import com.profuturo.AndroidPokeApi.adapters.PaginationScrollListener;
import com.profuturo.AndroidPokeApi.model.Pokemon;
import com.profuturo.AndroidPokeApi.model.PokemonRespuesta;
import com.profuturo.AndroidPokeApi.retrofit.PokemonApi;
import com.profuturo.AndroidPokeApi.retrofit.PokemonService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity implements ListPokemonAdapterCallback {

    private static final String TAG = "AndroidPokeApi";
    private static final int TOTAL_PAGES = 10; //20 pokemones por página, 40 para 800. etc.
    private static final int PAGE_START = 1;
    private Retrofit retrofit;
    private ListPokemonAdapter listPokemonAdapter;
    private ArrayList<Pokemon> finalArrayList;
    private RecyclerView rvPokemons;
    private ProgressBar progressBar;
    private LinearLayout errorLayout;
    private Button btnRetry;
    private TextView txtError;
    private SwipeRefreshLayout swipeRefreshLayout;
    //Ejemplo pagination
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int currentPage = PAGE_START;

    private int offset = 0;
    private PokemonService pokemonService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pokemonService = PokemonApi.getClient(this).create(PokemonService.class);

        rvPokemons = findViewById(R.id.rvPokemones);
        progressBar = findViewById(R.id.main_progress);
        errorLayout = findViewById(R.id.error_layout);
        btnRetry = findViewById(R.id.error_btn_retry);
        txtError = findViewById(R.id.error_txt_cause);
        swipeRefreshLayout = findViewById(R.id.main_swiperefresh);

        listPokemonAdapter = new ListPokemonAdapter(this);
        final GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        rvPokemons.setLayoutManager(gridLayoutManager);
        rvPokemons.setAdapter(listPokemonAdapter);
        rvPokemons.setHasFixedSize(true);
        rvPokemons.setItemAnimator(new DefaultItemAnimator());

        rvPokemons.addOnScrollListener(new PaginationScrollListener(gridLayoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage += 1;
                loadNextPage();
            }

            @Override
            public int getTotalPageCount() {
                return TOTAL_PAGES;
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });

        loadFirstPage();

        btnRetry.setOnClickListener(view -> loadFirstPage());

        swipeRefreshLayout.setOnRefreshListener(this::doRefresh);

    }

    private void doRefresh() {
        progressBar.setVisibility(View.VISIBLE);
        if (callPokemonService().isExecuted())
            callPokemonService().cancel();

        listPokemonAdapter.getPokemon().clear();
        listPokemonAdapter.notifyDataSetChanged();
        loadFirstPage();
        swipeRefreshLayout.setRefreshing(false);
    }

    private Call<PokemonRespuesta> callPokemonService() {
        return pokemonService.obtenerListaPokemon(20, offset);
    }

    private void loadFirstPage() {
        offset = 0;
        Log.d(TAG, "loadFirstPage: ");
        // To ensure list is visible when retry button in error view is clicked
        hideErrorView();
        currentPage = PAGE_START;

        callPokemonService().enqueue(new Callback<PokemonRespuesta>() {
            @Override
            public void onResponse(Call<PokemonRespuesta> call, Response<PokemonRespuesta> response) {
                hideErrorView();

//                Log.i(TAG, "onResponse: " + (response.raw().cacheResponse() != null ? "Cache" : "Network"));

                // Got data. Send it to adapter
                PokemonRespuesta pokemonRespuesta = response.body();
                List<Pokemon> results = pokemonRespuesta.getResults();

                progressBar.setVisibility(View.GONE);
                listPokemonAdapter.addAll(results);

                if (currentPage <= TOTAL_PAGES) listPokemonAdapter.addLoadingFooter();
                else isLastPage = true;
            }

            @Override
            public void onFailure(Call<PokemonRespuesta> call, Throwable t) {
                t.printStackTrace();
                showErrorView(t);
            }
        });
    }

    private void loadNextPage() {
        offset += 20;
        Log.d(TAG, "loadNextPage: " + currentPage);

        callPokemonService().enqueue(new Callback<PokemonRespuesta>() {
            @Override
            public void onResponse(Call<PokemonRespuesta> call, Response<PokemonRespuesta> response) {
                if (response.isSuccessful()) {
                    listPokemonAdapter.removeLoadingFooter();
                    isLoading = false;

                    PokemonRespuesta pokemonRespuesta = response.body();
                    List<Pokemon> results = pokemonRespuesta.getResults();
                    listPokemonAdapter.addAll(results);

                    if (currentPage != TOTAL_PAGES) listPokemonAdapter.addLoadingFooter();
                    else isLastPage = true;
                } else
                    Log.d(TAG, "onResponseFail" + response.errorBody());
            }

            @Override
            public void onFailure(Call<PokemonRespuesta> call, Throwable t) {
                Log.d(TAG, "onFailure Retrofit" + t.getMessage());
                listPokemonAdapter.showRetry(true, fetchErrorMessage(t));
            }
        });
    }

//    public void loadMore() {
//        if (finalArrayList.size() <= 20) {
//            finalArrayList.add(null);
//            listPokemonAdapter.notifyItemInserted(finalArrayList.size() - 1);
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    finalArrayList.remove(finalArrayList.size() - 1);
//                    listPokemonAdapter.notifyItemRemoved(finalArrayList.size());
//
//                    //Generating more data
//                    int index = finalArrayList.size();
//                    int end = index + 20;
//                    for (int i = index; i < end; i++) {
//                        offset += 20;
//                        listPokemonAdapter.agregarPokemones(obtenerDatos(offset));
//                    }
//                    listPokemonAdapter.notifyDataSetChanged();
//                    isLoading = false;
//                }
//            }, 3000);
//        } else {
//            Toast.makeText(MainActivity.this, "Loading data completed", Toast.LENGTH_SHORT).show();
//        }
//    }

//    private ArrayList<Pokemon> obtenerDatos(int offset) {
//        PokemonService pokemonService = retrofit.create(PokemonService.class);
//        Call<PokemonRespuesta> pokemonRespuestaCall = pokemonService.obtenerListaPokemon(20, offset);
//        final ArrayList<Pokemon> pokemons = new ArrayList<>();
//        pokemonRespuestaCall.enqueue(new Callback<PokemonRespuesta>() {
//            @Override
//            public void onResponse(Call<PokemonRespuesta> call, Response<PokemonRespuesta> response) {
//                if (response.isSuccessful()) {
//                    PokemonRespuesta pokemonRespuesta = response.body();
//                    pokemons.addAll(pokemonRespuesta.getResults());
//                } else
//                    Log.d(TAG, "onResponseFail" + response.errorBody());
//            }
//
//            @Override
//            public void onFailure(Call<PokemonRespuesta> call, Throwable t) {
//                Log.d(TAG, "onFailure Retrofit" + t.getMessage());
//            }
//        });
//        return pokemons;
//    }

    @Override
    public void recargarPagina() {
        loadNextPage();
    }

    private void showErrorView(Throwable throwable) {
        if (errorLayout.getVisibility() == View.GONE) {
            errorLayout.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            txtError.setText(fetchErrorMessage(throwable));
        }
    }

    private void hideErrorView() {
        if (errorLayout.getVisibility() == View.VISIBLE) {
            errorLayout.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    private String fetchErrorMessage(Throwable throwable) {
        String errorMsg = getResources().getString(R.string.error_msg_unknown);

        if (!isNetworkConnected()) {
            errorMsg = getResources().getString(R.string.error_msg_no_internet);
        } else if (throwable instanceof TimeoutException) {
            errorMsg = getResources().getString(R.string.error_msg_timeout);
        }
        return errorMsg;
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }
}
