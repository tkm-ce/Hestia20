package tkmce.hestia20.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import tkmce.hestia20.R;

public class ParticipantListAdapter extends RecyclerView.Adapter<ParticipantListAdapter.ViewHolder> {
    ArrayList<String> names;
    LayoutInflater inflater;
    ParticipantListAdapter.onCheckedListener listener;

    public ParticipantListAdapter(ArrayList<String> names, Context context,ParticipantListAdapter.onCheckedListener listener) {
        this.names = names;
        this.inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.listener=listener;
    }

    @NonNull
    @Override
    public ParticipantListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view=inflater.inflate(R.layout.participant_list_row,viewGroup,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ParticipantListAdapter.ViewHolder viewHolder, int i) {
        viewHolder.name.setText(names.get(i));
    }

    @Override
    public int getItemCount() {
        return names.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        CheckBox acm_check;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name=itemView.findViewById(R.id.participant_row_name);
            acm_check=itemView.findViewById(R.id.participant_row_check);

            acm_check.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!acm_check.isChecked()) {
                        listener.onChecked(getAdapterPosition());
                    }else {
                        listener.onUnChecked(getAdapterPosition());
                    }
                }
            });
        }
    }

    public interface onCheckedListener{
        void onChecked(int i);
        void onUnChecked(int i);
    }
}
