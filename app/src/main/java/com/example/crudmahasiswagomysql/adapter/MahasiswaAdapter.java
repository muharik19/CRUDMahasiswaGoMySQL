package com.example.crudmahasiswagomysql.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.crudmahasiswagomysql.R;
import com.example.crudmahasiswagomysql.data.DataMahasiswa;
import com.example.crudmahasiswagomysql.mahasiswa.EditActivity;
import com.example.crudmahasiswagomysql.mahasiswa.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MahasiswaAdapter extends RecyclerView.Adapter<MahasiswaAdapter.ViewHolder> implements Filterable {

    private static String BASE_URL = "http://192.168.43.237:83/mima/v1/";
    private Context context;
    private List<DataMahasiswa> dataMahasiswaList;
    private List<DataMahasiswa> dataMahasiswaListFiltered;
    private ListAdapterListener listAdapterListener;
    private Intent checkid, checknim, checknama, checkjurusan, checkhp, checkphoto;

    public MahasiswaAdapter(Context context, List<DataMahasiswa> dataMahasiswa, ListAdapterListener listAdapterListener) {
        this.dataMahasiswaList = dataMahasiswa;
        this.context = context;
        this.dataMahasiswaListFiltered = dataMahasiswaList;
        this.listAdapterListener = listAdapterListener;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.mahasiswa_row, parent, false);
        ViewHolder holder = new ViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DataMahasiswa data = dataMahasiswaListFiltered.get(position);
        Glide.with(context)
                .load(data.getPhotoMahasiswa())
                .apply(new RequestOptions().override(350, 550))
                .into(holder.mPhoto);
        holder.mNim.setText(data.getNimMahasiswa());
        holder.mNama.setText(data.getNamaMahasiswa());
        holder.mJurusan.setText(data.getJurusanMahasiswa());
        holder.mHp.setText(data.getHpMahasiswa());
        holder.btnEdit.setOnClickListener(new CustomOnItemClickListener(position, new CustomOnItemClickListener.OnItemClickCallback() {
            @Override
            public void onItemClicked(View v, int position) {
                Intent intent = new Intent(context, EditActivity.class);
                DataMahasiswa listmhs = dataMahasiswaListFiltered.get(position);
                checkid = intent.putExtra("id", String.valueOf(listmhs.getIDMahasiswa()));
                checknim = intent.putExtra("nim", listmhs.getNimMahasiswa());
                checknama = intent.putExtra("nama", listmhs.getNamaMahasiswa());
                checkjurusan = intent.putExtra("jurusan", listmhs.getJurusanMahasiswa());
                checkhp = intent.putExtra("hp", listmhs.getHpMahasiswa());
                checkphoto = intent.putExtra("photo", listmhs.getPhotoMahasiswa());
                context.startActivity(intent);
            }
        }));
        holder.btnDelete.setOnClickListener(new CustomOnItemClickListener(position, new CustomOnItemClickListener.OnItemClickCallback() {
            @Override
            public void onItemClicked(View v, int position) {
                final DataMahasiswa listmhsID = dataMahasiswaListFiltered.get(position);
                final int id = listmhsID.getIDMahasiswa();
                new AlertDialog.Builder(v.getContext())
                        .setTitle("Delete Mahasiswa")
                        .setMessage("Are you sure want to delete ? " + listmhsID.getNamaMahasiswa())
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                AndroidNetworking.post(BASE_URL+"mahasiswa-delete/")
                                        .addBodyParameter("id", String.valueOf(id))
                                        .setPriority(Priority.LOW)
                                        .build()
                                        .getAsJSONObject(new JSONObjectRequestListener() {
                                            @Override
                                            public void onResponse(JSONObject response) {
                                                try {
                                                    String message = response.getString("message");
                                                    Intent intent = new Intent(context, MainActivity.class);
                                                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                                                    context.startActivity(intent);
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                    Toast.makeText(context, "Parsing Error", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                            @Override
                                            public void onError(ANError anError) {
                                                anError.printStackTrace();
                                                Toast.makeText(context, "Error delete mahasiswa", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        }).setNegativeButton("No", null).show();
            }
        }));
    }

    @Override
    public int getItemCount() {
        return dataMahasiswaListFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    dataMahasiswaListFiltered = dataMahasiswaList;
                } else {
                    List<DataMahasiswa> filteredList = new ArrayList<>();
                    for (DataMahasiswa row : dataMahasiswaList) {
                        if (row.getNamaMahasiswa().toLowerCase().contains(charString.toLowerCase()) || row.getNimMahasiswa().contains(charSequence) || row.getHpMahasiswa().contains(charSequence) || row.getJurusanMahasiswa().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }
                    dataMahasiswaListFiltered = filteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = dataMahasiswaListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                dataMahasiswaListFiltered = (ArrayList<DataMahasiswa>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView mPhoto;
        TextView mNim, mNama, mJurusan, mHp;
        Button btnEdit, btnDelete;

        public ViewHolder(View v) {
            super(v);
            mPhoto = v.findViewById(R.id.iv_item_photo);
            mNim = v.findViewById(R.id.tv_nim);
            mNama = v.findViewById(R.id.tv_nama);
            mJurusan = v.findViewById(R.id.tv_jurusan);
            mHp = v.findViewById(R.id.tv_hp);
            btnEdit = v.findViewById(R.id.btn_edit);
            btnDelete = v.findViewById(R.id.btn_delete);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listAdapterListener.onMahasiswaSelected(dataMahasiswaListFiltered.get(getAdapterPosition()));
                }
            });
        }
    }

    public interface ListAdapterListener {
        void onMahasiswaSelected(DataMahasiswa dataMahasiswa);
    }
}
