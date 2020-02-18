package live.hestia.app.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import live.hestia.app.R;
import live.hestia.app.model.EventBasicModel;

public class EventListAdapter extends RecyclerView.Adapter<EventListAdapter.ViewHolder> {

    ArrayList<EventBasicModel> eventsList;
    Activity context;
    View view;

    public EventListAdapter(ArrayList<EventBasicModel> eventsList, Activity context) {
        this.eventsList = eventsList;
        this.context = context;
    }

    @NonNull
    @Override
    public EventListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_list_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventListAdapter.ViewHolder holder, int position) {
        holder.title.setText(eventsList.get(position).getTitle());
        holder.date.setText(eventsList.get(position).getFile1());
        holder.prize.setText("Prize : " + eventsList.get(position).getPrize());

        Picasso.with(context)
                .load(eventsList.get(position).getImg())
                .error(R.color.colorPrimary)
                .resize(65, 65)
                .centerCrop()
                .into(holder.img);
    }

    @Override
    public int getItemCount() {
        return eventsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private CardView cardView;
        private ImageView img;
        private TextView title;
        private TextView date;
        private TextView prize;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.event_row_card);
            title = itemView.findViewById(R.id.event_name);
            date = itemView.findViewById(R.id.event_date);
            img = itemView.findViewById(R.id.ordered_product_image);
            prize = itemView.findViewById(R.id.event_prize_amt);

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

        }
    }
}
