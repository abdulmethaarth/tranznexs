package co.webnexs.tranznexs.beens;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import co.webnexs.tranznexs.R;

public class CabsAdapter extends RecyclerView.Adapter<CabsAdapter.CustomViewHolder> {
    private List<CarType> carTypes;
    private AdapterView.OnItemClickListener listener;
    public Context context;
    public ArrayList<CabsAdapter> dataItems;
    int row_index;


    public CabsAdapter(List<CarType> carTypes) {
        this.carTypes = carTypes;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.car_type, parent, false);

        return new CustomViewHolder(itView);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        CarType gobase = carTypes.get(position);
        holder.Name.setText(gobase.getName());
        Picasso.get().load(carTypes.get(position).getIcon()).into(holder.icon);

        holder.mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                row_index=position;
                notifyDataSetChanged();
            }
        });
        if(row_index==position){
            //holder.icon.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
          //  holder.icon.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(Color.parseColor)));
            holder.mCardView.setCardBackgroundColor(Color.BLACK);
        }
        else
        {
           // holder.icon.setBackgroundColor(Color.parseColor("#ffffff"));
            holder.mCardView.setCardBackgroundColor(Color.WHITE);
            //holder.icon.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context,R.color.white)));

        }
    }

    @Override
    public int getItemCount() {
        return carTypes.size();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        public TextView Name;
        public ImageView icon;
        public CardView mCardView;



        public CustomViewHolder(View itView) {
            super(itView);
            Name = (TextView) itView.findViewById(R.id.name);
            icon = (ImageView) itemView.findViewById(R.id.icon);
            mCardView = (CardView) itemView.findViewById(R.id.card_view_top);

        }
    }
}
