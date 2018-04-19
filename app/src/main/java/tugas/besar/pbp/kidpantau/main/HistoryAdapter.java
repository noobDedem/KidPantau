package tugas.besar.pbp.kidpantau.main;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import lab.praktikum.pbp.kidpantau.R;
import tugas.besar.pbp.kidpantau.model.User;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.MyViewHolder> {
    private ArrayList<User> arrayList;
    private static View v;
    private Context context;

    public HistoryAdapter(Context context, ArrayList<User> arrayList) {
        this.arrayList = arrayList;
        this.context = context;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView name;

        public MyViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.history_name);
        }

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_history, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final User user = arrayList.get(position);
        holder.name.setText(user.getNama());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ListHistoryActivity.class);
                intent.putExtra("email", user.getEmail());
                context.startActivity(intent);
            }
        });
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
