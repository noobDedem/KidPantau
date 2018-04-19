package tugas.besar.pbp.kidpantau.main;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import lab.praktikum.pbp.kidpantau.R;
import tugas.besar.pbp.kidpantau.model.Location;

public class ListHistoryAdapter extends RecyclerView.Adapter<ListHistoryAdapter.MyViewHolder> {
    private ArrayList<Location> arrayList;
    private static View v;

    public ListHistoryAdapter(Context context, ArrayList<Location> arrayList) {
        this.arrayList = arrayList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView namaTempat;
        private TextView waktu;

        public MyViewHolder(View itemView) {
            super(itemView);
            namaTempat = itemView.findViewById(R.id.nama_tempat);
            waktu = itemView.findViewById(R.id.waktu_list);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_list_history, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Location location = arrayList.get(position);
        holder.namaTempat.setText(location.getNamaTempat());
        holder.waktu.setText(location.getWaktu());
    }

    @Override
    public int getItemCount() {
        try {
            return arrayList.size();
        } catch (Exception e) {
            e.getStackTrace();
        }
        return 0;
    }
}
