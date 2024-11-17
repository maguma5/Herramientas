package com.herramientas;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class WorkerAdapter extends RecyclerView.Adapter<WorkerAdapter.WorkerViewHolder> {

    private List<Worker> workerList;

    public WorkerAdapter(List<Worker> workerList) {
        this.workerList = workerList; }
    @NonNull
    @Override
    public WorkerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout,
                parent, false); return new WorkerViewHolder(itemView); }
    @Override
    public void onBindViewHolder(@NonNull WorkerViewHolder holder, int position) {
        Worker worker = workerList.get(position);
        holder.workerTextView.setText(worker.getWorkerName());
        holder.timeTextView.setText(worker.getTime());
        holder.machineTextView.setText(worker.getMachine());
        holder.statusTextView.setText(worker.getStatus());
    }
    @Override
    public int getItemCount() {
        return workerList.size();
    }
    static class WorkerViewHolder extends RecyclerView.ViewHolder {
        TextView workerTextView, timeTextView, machineTextView, statusTextView;
        public WorkerViewHolder(@NonNull View itemView) {
            super(itemView);
            workerTextView = itemView.findViewById(R.id.text_worker);
            timeTextView = itemView.findViewById(R.id.text_time);
            machineTextView = itemView.findViewById(R.id.text_machine);
            statusTextView = itemView.findViewById(R.id.text_status);
        }
    }
}