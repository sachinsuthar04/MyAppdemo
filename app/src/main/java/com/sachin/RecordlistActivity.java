package com.sachin;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.sachin.adapter.RecordAdapter;
import com.sachin.databinding.ActivityMainBinding;
import com.sachin.roomdb.RecordDatabase;
import com.sachin.roomdb.model.Recorduser;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class RecordlistActivity extends AppCompatActivity {

    private RecordDatabase recordDatabase;
    private List<Recorduser> recorduser;
    private RecordAdapter recordAdapter;
    ActivityMainBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        initializeVies();
        displayList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.btnadd:
                startActivityForResult(new Intent(RecordlistActivity.this, AddRecordActivity.class), 100);
                break;
            default:
                break;
        }
        return true;
    }

    private void displayList() {
        recordDatabase = RecordDatabase.getInstance(RecordlistActivity.this);
        new RetrieveTask(this).execute();
    }

    private static class RetrieveTask extends AsyncTask<Void, Void, List<Recorduser>> {

        private WeakReference<RecordlistActivity> activityReference;

        // only retain a weak reference to the activity
        RetrieveTask(RecordlistActivity context) {
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected List<Recorduser> doInBackground(Void... voids) {
            if (activityReference.get() != null)
                return activityReference.get().recordDatabase.getNoteDao().getrecords();
            else
                return null;
        }

        @Override
        protected void onPostExecute(List<Recorduser> notes) {
            if (notes != null && notes.size() > 0) {
                activityReference.get().recorduser.clear();
                activityReference.get().recorduser.addAll(notes);
                // hides empty text view
                activityReference.get().mBinding.recyclerView.setVisibility(View.VISIBLE);
                activityReference.get().mBinding.tvEmpty.setVisibility(View.GONE);
                activityReference.get().recordAdapter.notifyDataSetChanged();
            } else {
                activityReference.get().mBinding.tvEmpty.setVisibility(View.VISIBLE);
                activityReference.get().mBinding.recyclerView.setVisibility(View.GONE);
            }
        }
    }

    private void initializeVies() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mBinding.recyclerView.setLayoutManager(new LinearLayoutManager(RecordlistActivity.this));
        recorduser = new ArrayList<>();
        recordAdapter = new RecordAdapter(recorduser, RecordlistActivity.this);
        mBinding.recyclerView.setAdapter(recordAdapter);

        mBinding.edtsearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });
    }

    private void filter(String text) {
        ArrayList<Recorduser> filteredList = new ArrayList<>();
        for (Recorduser item : recorduser) {
            if (item.getTitle().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        recordAdapter.filterList(filteredList);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode > 0) {
            if (resultCode == 1) {
                recorduser.add((Recorduser) data.getSerializableExtra("record"));
            }
            listVisibility();
        }
    }


    private void listVisibility() {
        int emptyMsgVisibility = View.GONE;
        if (recorduser.size() == 0) { // no item to display
            if (mBinding.tvEmpty.getVisibility() == View.GONE) {
                emptyMsgVisibility = View.VISIBLE;
                mBinding.recyclerView.setVisibility(View.GONE);
            }
        }
        mBinding.recyclerView.setVisibility(View.VISIBLE);
        mBinding.tvEmpty.setVisibility(emptyMsgVisibility);
        recordAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        recordDatabase.cleanUp();
        super.onDestroy();
    }
}
