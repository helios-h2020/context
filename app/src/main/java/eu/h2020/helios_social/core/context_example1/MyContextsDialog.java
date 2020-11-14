package eu.h2020.helios_social.core.context_example1;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Iterator;

import eu.h2020.helios_social.core.context.Context;
import eu.h2020.helios_social.core.context.ContextListener;


public class MyContextsDialog extends Dialog implements ContextListener {

    private RecyclerView mMyContextsView;

    private LinearLayoutManager layoutManager;

    private MyContextsDialog.myContextAdapter mAdapter;

    private ArrayList<Context> mMyContexts;

    public MyContextsDialog(Activity activity, ArrayList<Context> myContexts) {
        super(activity);
        this.mMyContexts = myContexts;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        setContentView(R.layout.mycontexts_dialog);

        // Locate the UI widgets.
        mMyContextsView = findViewById(R.id.mycontexts_view);
        // use a linear layout manager
        layoutManager = new LinearLayoutManager(getContext());
        mMyContextsView.setLayoutManager(layoutManager);
    }

    @Override
    public void onStart() {
        super.onStart();
        // specify an adapter
        mAdapter = new MyContextsDialog.myContextAdapter(mMyContexts);
        mMyContextsView.setAdapter(mAdapter);

        Iterator itr = mMyContexts.iterator();
        while (itr.hasNext()) {
            Context c = ((Context) itr.next());
            c.registerContextListener(this);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(mMyContexts != null) {
            Iterator itr = mMyContexts.iterator();
            while (itr.hasNext()) {
                Context c = ((Context) itr.next());
                c.unregisterContextListener(this);
            }
        }
    }

    @Override
    public void contextChanged(boolean active) {
        mAdapter.notifyDataSetChanged();
    }

    public class myContextAdapter extends RecyclerView.Adapter<myContextAdapter.MyViewHolder> {
        private ArrayList<Context> dataset;

        // Reference to the views for each data item
        public class MyViewHolder extends RecyclerView.ViewHolder {
            private TextView contextNameView;
            public MyViewHolder(View itemView) {
                super(itemView);
                this.contextNameView = itemView.findViewById(R.id.contextNameView);
            }
        }

        public myContextAdapter(ArrayList<Context> myDataset) {
            dataset = myDataset;
        }

        // Create new views (invoked by the layout manager)
        public myContextAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View listItem = layoutInflater.inflate(R.layout.mycontexts_item, parent, false);
            myContextAdapter.MyViewHolder viewHolder = new myContextAdapter.MyViewHolder(listItem);
            return viewHolder;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(myContextAdapter.MyViewHolder holder, int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            Context c = (Context)dataset.get(position);
            holder.contextNameView.setText(c.getName());
            if(c.isActive()) {
                holder.contextNameView.setBackgroundColor(Color.GREEN);
            }
        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return dataset.size();
        }
    }

}
