package eu.h2020.helios_social.core.context_example1;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import eu.h2020.helios_social.core.context.Context;
import eu.h2020.helios_social.core.context.ContextListener;

/**
 *  MyContextsActivity
 *  - Shows user's contexts and their status in a view
 */
public class MyContextsActivity extends AppCompatActivity implements ContextListener {

    private static final String TAG = MyContextsActivity.class.getSimpleName();

    // UI Widgets
    private RecyclerView mMyContextsView;

    private LinearLayoutManager layoutManager;

    private MyContextsActivity.myContextAdapter mAdapter;

    private ArrayList<Context> mMyContexts;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mycontexts_activity);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowHomeEnabled(true);

        // Locate the UI widgets.
        mMyContextsView = findViewById(R.id.mycontexts_view);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        mMyContextsView.setLayoutManager(layoutManager);

        mMyContexts = MyContextsExample.mMyContexts;

        // specify an adapter
        mAdapter = new myContextAdapter(mMyContexts);
        mMyContextsView.setAdapter(mAdapter);

        for (Context c : mMyContexts) {
            c.registerContextListener(this);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mMyContexts != null) {
            for (Context c : mMyContexts) {
                c.unregisterContextListener(this);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Implements the ContextLister interface contextChanged method.
     * This is called when context is changed.
     *
     * @param active
     * @see eu.h2020.helios_social.core.context.ContextListener
     */
    @Override
    public void contextChanged(boolean active) {
        runOnUiThread(() -> mAdapter.notifyDataSetChanged());
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
            } else {
                holder.contextNameView.setBackgroundColor(Color.WHITE);
            }
        }

        // Return the size of the dataset
        @Override
        public int getItemCount() {
            return dataset.size();
        }
    }


}
