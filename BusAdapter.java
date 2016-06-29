package itrans.myjsontesting;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class BusAdapter extends RecyclerView.Adapter<BusAdapter.ViewHolder> {

    private ArrayList<Buses> busList = new ArrayList<>();
    private LayoutInflater layoutInflater;

    public BusAdapter(Context context){
        layoutInflater = LayoutInflater.from(context);
    }

    public void setBusList(ArrayList<Buses> busList){
        this.busList = busList;
        notifyItemRangeChanged(0, busList.size());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.custom_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
       // Buses currentBus = busList.get(position);
        holder.tvBusService.setText(busList.get(position).getBusNumber()); //currentBus.getBusNumber()
        holder.btnTimeLeft.setText(String.valueOf(busList.get(position).getNextBusTime()));
    }

    @Override
    public int getItemCount() {
        return busList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvBusService;
        private Button btnTimeLeft;
        private CardView cardView;

        public ViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.cardView);
            tvBusService = (TextView) itemView.findViewById(R.id.tvBusService);
            btnTimeLeft = (Button) itemView.findViewById(R.id.btnTimeLeft);
        }
    }
}
