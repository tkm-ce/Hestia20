package tkmce.hestia20.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import tkmce.hestia20.R;
import tkmce.hestia20.model.ResultModel;

public class ResultsAdapter extends RecyclerView.Adapter<ResultsAdapter.MyViewHolder> {
    private ArrayList<ResultModel> results;

    public ResultsAdapter(ArrayList<ResultModel> results) {
        this.results = results;
    }

    @NonNull
    @Override
    public ResultsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_results_row, viewGroup, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ResultsAdapter.MyViewHolder myViewHolder, int i) {
        String label = results.get(i).getLabel() + " : " + results.get(i).getName() + " (" + results.get(i).getCollege() + ")";
        myViewHolder.name.setText(label);
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.row_result_name);
        }
    }
}