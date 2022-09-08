package co.webnexs.tranznexs.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import co.webnexs.tranznexs.R;
import co.webnexs.tranznexs.beens.TripDetails;
import de.hdodenhof.circleimageview.CircleImageView;

public class TripDetailsAdapter extends RecyclerView.Adapter<TripDetailsAdapter.CustomViewHolder>{


    private List<TripDetails> tripDetails;
    android.content.Context mContext;
    private Object Context;

    public TripDetailsAdapter(List<TripDetails> tripDetails ) {
        this.tripDetails = tripDetails;
        this.mContext = mContext;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.trip_list, parent, false);

        return new CustomViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        this.mContext=mContext;
        Picasso.get().load(tripDetails.get(position).getDriver_image()).into(holder.image1);
        Picasso.get().load(tripDetails.get(position).getStatus_stamp()).into(holder.img_status);
        Picasso.get().load(tripDetails.get(position).getIcon()).into(holder.cab_icon);

        TripDetails trips = tripDetails.get(position);
        //holder.date.setText(trips.getAccept_date());
        // holder.bikeNumber.setText(trips.());
        holder.fromLOcation.setText(trips.getPickup_loc());
        holder.toLocation.setText(trips.getDrop_loc());
        holder.bike_number.setText(trips.getNumber_plate());
        holder.date.setText(trips.getRequest_time());
        holder.brand.setText(trips.getBrand());
        holder.bikeTyep.setText(trips.getName());
        holder.driver_id.setText("Trnz-"+trips.getDriver_id());
        holder.driver_name.setText(trips.getFirstname()+" "+trips.getLastname());
        holder.fare.setText("$ "+trips.getFare());
    }

    @Override
    public int getItemCount() {
        return tripDetails.size();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        public TextView date, bike_number, fromLOcation,toLocation,fare,brand,bikeTyep,driver_name,driver_id;
        public CircleImageView image1;
        public ImageView img_status,cab_icon;



        public CustomViewHolder(final View view) {
            super(view);
            date = (TextView) view.findViewById(R.id.dateTime);
            // bikeNumber = (TextView) view.findViewById(R.id.bikeNumber_type);
            fromLOcation = (TextView) view.findViewById(R.id.from_Location);
            toLocation = (TextView) view.findViewById(R.id.to_Location);
            fare = (TextView) view.findViewById(R.id.fareAmount);
            brand = (TextView) view.findViewById(R.id.brand);
            driver_id = (TextView) view.findViewById(R.id.driver_id);
            driver_name = (TextView) view.findViewById(R.id.driver_name);
            bikeTyep = (TextView) view.findViewById(R.id.bikeTyep);
            bike_number = (TextView) view.findViewById(R.id.bike_number);
            image1 = (CircleImageView) itemView.findViewById(R.id.driverImg);
            img_status = (ImageView) itemView.findViewById(R.id.img_status);
            cab_icon = (ImageView) itemView.findViewById(R.id.cab_icon);


            /*view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (getPosition()==0)
                    {
                    Employee employee = employees.get(0);
                    Integer id = employee.getId();
                       // mContext.startActivity(this,SecondActivity.class);
                        //startActivity(new Intent(mContext,SecondActivity.class));
                        Intent intent = new Intent(mContext, SecondActivity.class);
                        intent.putExtra("id", id);
                        mContext.startActivity(intent);

                       // mContext.startActivity(intent);
                        Toast.makeText(v.getContext(), " On CLick one", Toast.LENGTH_SHORT).show();

                    }
                    if (getPosition()==1)
                    {

                        Toast.makeText(v.getContext(), " On CLick two", Toast.LENGTH_SHORT).show();

                    }
                    if (getPosition()==2)
                    {

                        Toast.makeText(v.getContext(), " On CLick three", Toast.LENGTH_SHORT).show();

                    }
                }
            });*/
        }
    }
}
