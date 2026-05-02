package com.example.isisgamecollector.UI;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.isisgamecollector.R;
import com.example.isisgamecollector.UI.entities.Console;

import java.util.List;

public class ConsoleAdapter extends RecyclerView.Adapter<ConsoleAdapter.ConsoleViewHolder> {
    private List<Console> mConsoles;

    private final Context context;

    private final LayoutInflater mInflater;

    public ConsoleAdapter(Context context) {
        mInflater= LayoutInflater.from(context);
        this.context = context;
    }


    public class ConsoleViewHolder extends RecyclerView.ViewHolder {
        private final TextView consoleItemView;

        public ConsoleViewHolder(@NonNull View itemView) {
            super(itemView);
            consoleItemView = itemView.findViewById(R.id.textView2);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    final Console current=mConsoles.get(position);
                    Intent intent=new Intent(context, ConsoleDetails.class);
                    intent.putExtra("id",current.getConsoleID());
                    intent.putExtra("consoleName",current.getConsoleName());
                    intent.putExtra("consoleBrand",current.getConsoleBrand());
                    intent.putExtra("consoleReleaseDate",current.getConsoleReleaseDate());
                    intent.putExtra("acquisitionDate",current.getAcquisitionDate());
                    context.startActivity(intent);
                }
            });
        }
    }


    @NonNull
    @Override
    public ConsoleAdapter.ConsoleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView=mInflater.inflate(R.layout.console_list_item,parent,false);
        return new ConsoleViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ConsoleAdapter.ConsoleViewHolder holder, int position) {
        if(mConsoles!=null){
            Console current=mConsoles.get(position);
            String name=current.getConsoleName();
            holder.consoleItemView.setText(name);
        }
        else{
            holder.consoleItemView.setText("No console name");
        }

    }

    @Override
    public int getItemCount() {
        if (mConsoles!=null) {
            return mConsoles.size();
        }
        else return 0;
    }

    public void setConsoles(List<Console> consoles) {
        mConsoles=consoles;
        notifyDataSetChanged();
    }


}
