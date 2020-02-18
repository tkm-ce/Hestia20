package live.hestia.app.Adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import live.hestia.app.R;
import live.hestia.app.model.ScheduleEventModel;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.MyViewHolder> {

    private List<ScheduleEventModel> origData;
    private List<ScheduleEventModel> scheduleList = new ArrayList<>();

    private String filterDay;
    private Context context;

    public ScheduleAdapter(List<ScheduleEventModel> scheduleList, Context context) {
        this.origData = scheduleList;
        this.context = context;
        dataSetChanged();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView eve_name, eve_label, eve_venue, eve_time;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            eve_name = itemView.findViewById(R.id.eve_name);
            eve_label = itemView.findViewById(R.id.eve_label);
            eve_time = itemView.findViewById(R.id.eve_time);
            eve_venue = itemView.findViewById(R.id.eve_venue);
        }
    }

    @NonNull
    @Override
    public ScheduleAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.event_schedule_row, viewGroup, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduleAdapter.MyViewHolder holder, int i) {
        ScheduleEventModel one = scheduleList.get(holder.getAdapterPosition());

        holder.eve_name.setText(one.getEvent_name());
        if (TextUtils.isEmpty(one.getEvent_label())) {
            holder.eve_label.setVisibility(View.GONE);
        } else {
            holder.eve_label.setVisibility(View.VISIBLE);
            holder.eve_label.setText(one.getEvent_label());
        }
        holder.eve_time.setText(beautify(one.getEvent_time()));
        if (!TextUtils.isEmpty(one.getEvent_venue())) {
            holder.eve_venue.setText(one.getEvent_venue());
            holder.eve_venue.setVisibility(View.VISIBLE);
        } else {
            holder.eve_venue.setVisibility(View.GONE);
        }
    }

    @NonNull
    private String beautify(String strFrom) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
                context.getResources().getConfiguration().locale);
        try {
            Date from = dateFormat.parse(strFrom);
            SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm aa", context.getResources().getConfiguration().locale);
            return "Starts at " + timeFormat.format(from);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public int getItemCount() {
        return scheduleList.size();
    }

    public void filter(String day) {
        this.filterDay = day;
        dataSetChanged();
    }

    public void dataSetChanged() {
        scheduleList.clear();

        if(TextUtils.isEmpty(filterDay)){
            return;
        }

        Log.d("yoo", "dataSetChanged: ");
        for (ScheduleEventModel item : origData)
            if (item.getEvent_time().contains(filterDay))
            scheduleList.add(item);

        notifyDataSetChanged();
    }
}