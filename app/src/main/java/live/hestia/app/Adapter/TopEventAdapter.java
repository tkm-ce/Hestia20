package live.hestia.app.Adapter;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import live.hestia.app.R;
import live.hestia.app.model.EventBasicModel;

public class TopEventAdapter extends RecyclerView.Adapter<TopEventAdapter.ViewHolder> {
    private View view;
    private List<EventBasicModel> events;
    private Activity context;
    private OnRowClickedListener listener;

    public TopEventAdapter(List<EventBasicModel> events, Activity context, OnRowClickedListener listener) {
        this.events = events;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TopEventAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_list_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final TopEventAdapter.ViewHolder holder, final int position) {
        holder.title.setText(events.get(position).getTitle());
        holder.date.setText(events.get(position).getFile1());

        Log.d("yoo", "onBindViewHolder: " + "https://www.hestia.live/assets/uploads/event_images/" + events.get(position).getImg());

        Picasso.with(context)
                .load("https://www.hestia.live/assets/uploads/event_images/" + events.get(position).getImg())
                .resize(300,300)
                .centerCrop()
                .placeholder(R.drawable.landing_placeholder)
                .into(holder.img);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onRowClicked(events.get(position).getEvent_id());
            }
        });

    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;
        private ImageView img;
        private TextView title;
        private TextView date;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.user_event_row_card);
            img = itemView.findViewById(R.id.user_event_row_img);
            title = itemView.findViewById(R.id.user_event_row_title);
            date = itemView.findViewById(R.id.user_event_row_date);
        }

    }

    @Keep
    public interface OnRowClickedListener {
        void onRowClicked(String event_id);
    }
}
