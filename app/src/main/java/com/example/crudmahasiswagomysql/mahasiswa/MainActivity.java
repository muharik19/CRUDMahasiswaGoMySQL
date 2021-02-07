package com.example.crudmahasiswagomysql.mahasiswa;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.crudmahasiswagomysql.R;
import com.example.crudmahasiswagomysql.adapter.MahasiswaAdapter;
import com.example.crudmahasiswagomysql.data.DataMahasiswa;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MahasiswaAdapter.ListAdapterListener {

    private static final String TAG = "MainActivity";
    private static final String BASE_URL = "http://192.168.43.237:83/mima/v1/";
    private List<DataMahasiswa> dataMahasiswaList;
    private RecyclerView recyclerView;
    private FloatingActionButton fabAdd;
    private ShimmerFrameLayout shimmerFrameLayout;
    private MahasiswaAdapter adapter;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fabAdd = findViewById(R.id.fab);
        recyclerView = findViewById(R.id.rv_data_all);
        shimmerFrameLayout = findViewById(R.id.shimmer_frame_layout);
        searchView = findViewById(R.id.search_view);

        dataMahasiswaList = new ArrayList<>();
        adapter = new MahasiswaAdapter(this, dataMahasiswaList, this);
        RecyclerView.LayoutManager rLayout = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(rLayout);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        getDataMahasiswaAll();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, CreateActivity.class));
            }
        });
    }

    private void getDataMahasiswaAll() {
        AndroidNetworking.get(BASE_URL+"mahasiswa-all/")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray results = response.getJSONArray("results");
                            shimmerFrameLayout.stopShimmerAnimation();
                            shimmerFrameLayout.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                            for (int i = 0; i < results.length(); i++) {
                                JSONObject data = results.getJSONObject(i);
                                dataMahasiswaList.add(new DataMahasiswa(
                                        data.getInt("id"),
                                        data.getString("photo"),
                                        data.getString("nim"),
                                        data.getString("nama"),
                                        data.getString("jurusan"),
                                        data.getString("hp")
                                ));
                            }
                            adapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onError(ANError anError) {
                        Log.d(TAG, "onError: " + anError);
                    }
                });
    }

    @Override
    public void onMahasiswaSelected(DataMahasiswa dataMahasiswa) {
        Toast.makeText(getApplicationContext(), "Selected: " + dataMahasiswa.getNimMahasiswa() + ", " + dataMahasiswa.getNamaMahasiswa(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        shimmerFrameLayout.startShimmerAnimation();
    }

    @Override
    public void onPause() {
        shimmerFrameLayout.stopShimmerAnimation();
        super.onPause();
    }

}