package live.hestia.app.Adapter;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import live.hestia.app.R;
import live.hestia.app.model.EventBasicModel;

public class CategoryEventAdapter extends RecyclerView.Adapter<CategoryEventAdapter.ViewHolder> {
    private View view;
    private List<EventBasicModel> events;
    private Activity context;
    private OnRowClickedListener listener;

    public CategoryEventAdapter(List<EventBasicModel> events, Activity context, OnRowClickedListener listener) {
        this.events = events;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CategoryEventAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_list_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CategoryEventAdapter.ViewHolder holder, final int position) {
        holder.title.setText(events.get(position).getTitle());
        holder.date.setText("Reg Fees: " + events.get(position).getReg_fee());
        holder.event_prize_amt.setText("Prizes: " + events.get(position).getPrize());

        if (TextUtils.isEmpty(events.get(position).getPrize())) {
            holder.event_prize_amt.setVisibility(View.GONE);
        }

        Log.d("yoo", "onBindViewHolder: " + "https://www.hestia.live/assets/uploads/event_images/" + events.get(position).getImg());

        Picasso.with(context)
                .load("https://www.hestia.live/assets/uploads/event_images/" + events.get(position).getImg())
                .resize(700, 700)
                .centerCrop()
                .placeholder(R.drawable.landing_placeholder)
                .into(holder.ordered_product_image);

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
        private ImageView ordered_product_image;
        private TextView title;
        private TextView date, event_prize_amt;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ordered_product_image = itemView.findViewById(R.id.ordered_product_image);
            title = itemView.findViewById(R.id.event_name);
            date = itemView.findViewById(R.id.event_date);
            event_prize_amt = itemView.findViewById(R.id.event_prize_amt);
        }

    }

    @Keep
    public interface OnRowClickedListener {
        void onRowClicked(String event_id);
    }
}
