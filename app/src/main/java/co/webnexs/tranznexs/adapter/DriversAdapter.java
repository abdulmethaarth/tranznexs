package co.webnexs.tranznexs.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import co.webnexs.tranznexs.R;
import co.webnexs.tranznexs.beens.Drivers;

public class DriversAdapter extends RecyclerView.Adapter<DriversAdapter.CustomViewHolder> {
    private List<Drivers.DriverDetails> employees;

    public DriversAdapter(List<Drivers.DriverDetails> employees) {
        this.employees = employees;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_home, parent, false);

        return new CustomViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        Drivers.DriverDetails employee = employees.get(position);
        holder.lat =  employee.getLatitude();
        holder.log = employee.getLongitude();

    }

    @Override
    public int getItemCount() {
        return employees.size();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        public String lat,log;

        public CustomViewHolder(View view) {
            super(view);

        }
    }
}