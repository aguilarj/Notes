package io.github.aguilarj.notes;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.common.base.Preconditions;

import java.util.ArrayList;

public class ShowNotesActivity extends AppCompatActivity {
    final private int GRID = 0;
    final private int LIST = 1;
    private int CURRENT_VIEW;
    private RecyclerView rvNotes;
    private int notebookId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_notes);

        Intent intent = getIntent();

        Toolbar addNotebookToolbar = (Toolbar) findViewById(R.id.show_notes_toolbar);
        setSupportActionBar(addNotebookToolbar);

        ActionBar actionBar = getSupportActionBar();
        Preconditions.checkArgument(actionBar != null);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_back);

        RelativeLayout showNotesLayout = (RelativeLayout) findViewById(R.id.show_notes_layout);
        TextView noNotes = (TextView) findViewById(R.id.no_notes);
        rvNotes = (RecyclerView) findViewById(R.id.notes_list);

        Data data = Data.getInstance(ShowNotesActivity.this);
        notebookId = intent.getIntExtra("notebookId", -1);

        actionBar.setTitle(data.getNotebook(notebookId).getTitle());

        Preconditions.checkArgument(notebookId != -1);
        ArrayList<Note> notes = data.getNotes(notebookId);

        Preconditions.checkArgument(rvNotes != null && showNotesLayout != null && noNotes != null);

        if (!notes.isEmpty()) {
            noNotes.setVisibility(View.INVISIBLE);
            NotesAdapter adapter = new NotesAdapter(notes, notebookId);
            rvNotes.setAdapter(adapter);

            CURRENT_VIEW = data.getNotebook(notebookId).getDisplayMode();

            if (CURRENT_VIEW == GRID) {
                rvNotes.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
            } else {
                rvNotes.setLayoutManager(new LinearLayoutManager(this));
            }


        } else {
            rvNotes.setVisibility(View.INVISIBLE);
            noNotes.setText(R.string.no_notes_message);
        }

        FloatingActionButton add_notebook_FAB = (FloatingActionButton) findViewById(R.id.add_note_FAB);
        Preconditions.checkArgument(add_notebook_FAB != null);

        add_notebook_FAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ShowNotesActivity.this, NoteActivity.class);
                intent.putExtra("notebookId", notebookId);
                intent.putExtra("REQUEST", 0); // ADD
                intent.putExtra("FROM_SHOW_NOTES", true);
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.action_switch_view:
                Data data = Data.getInstance(this);
                if (CURRENT_VIEW == GRID) {
                    rvNotes.setLayoutManager(new LinearLayoutManager(this));
                    CURRENT_VIEW = LIST;
                    data.setNotebookDisplayMode(notebookId, LIST);
                } else {
                    rvNotes.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
                    CURRENT_VIEW = GRID;
                    data.setNotebookDisplayMode(notebookId, GRID);
                }
                invalidateOptionsMenu();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_show_notes, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu (Menu menu) {
        if (CURRENT_VIEW == GRID) {
            menu.findItem(R.id.action_switch_view).setIcon(R.drawable.ic_list);
        } else {
            menu.findItem(R.id.action_switch_view).setIcon(R.drawable.ic_grid);
        }
        return true;
    }
}
