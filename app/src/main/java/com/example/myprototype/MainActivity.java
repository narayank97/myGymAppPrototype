package com.example.myprototype;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final int ADD_NOTE_REQUEST = 1;
    public static final int EDIT_NOTE_REQUEST = 2;
    private NoteViewModel noteViewModel;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        FloatingActionButton buttonAddNote = findViewById(R.id.button_add_note);
        buttonAddNote.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v){
                Intent intent = new Intent(MainActivity.this, AddEditNoteActivitiy.class);
                startActivityForResult(intent,ADD_NOTE_REQUEST);
            }

        });

        RecyclerView recyclerView= findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        final NoteAdapter adapter = new NoteAdapter();
        recyclerView.setAdapter(adapter);
        
        noteViewModel = ViewModelProviders.of(this).get(NoteViewModel.class);
        noteViewModel.getAllNotes().observe(this, new Observer<List<Note>>() {
            @Override
            public void onChanged(List<Note> notes) {
                adapter.setNotes(notes);
            }
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                noteViewModel.delete(adapter.getNoteAt(viewHolder.getAdapterPosition()));
                Toast.makeText(MainActivity.this,"Note has been deleted",Toast.LENGTH_SHORT).show();
            }
        }).attachToRecyclerView(recyclerView);

        adapter.setOnItemClickListener(new NoteAdapter.OnItemCLickListener() {
            @Override
            public void onItemClick(Note note) {
                Intent intent = new Intent(MainActivity.this, AddEditNoteActivitiy.class);
                intent.putExtra(AddEditNoteActivitiy.EXTRA_ID,note.getId());
                intent.putExtra(AddEditNoteActivitiy.EXTRA_TITLE,note.getTitle());
                intent.putExtra(AddEditNoteActivitiy.EXTRA_DESCRIPTION,note.getDescription());
                intent.putExtra(AddEditNoteActivitiy.EXTRA_PRIORITY,note.getPriority());
                startActivityForResult(intent,EDIT_NOTE_REQUEST);
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode == ADD_NOTE_REQUEST && resultCode == RESULT_OK){
            String title = data.getStringExtra(AddEditNoteActivitiy.EXTRA_TITLE);
            String description = data.getStringExtra(AddEditNoteActivitiy.EXTRA_DESCRIPTION);
            int priority = data.getIntExtra(AddEditNoteActivitiy.EXTRA_PRIORITY,1);

            Note note = new Note(title,description,priority);
            noteViewModel.insert(note);
            Toast.makeText(this,"Note Saved", Toast.LENGTH_SHORT).show();
        }
        else if(requestCode == EDIT_NOTE_REQUEST && resultCode == RESULT_OK) {
            int id = data.getIntExtra(AddEditNoteActivitiy.EXTRA_ID,1);
            if(id == -1) {
                Toast.makeText(this,"Note was cant be Updated",Toast.LENGTH_SHORT).show();
                return;
            }

            String title = data.getStringExtra(AddEditNoteActivitiy.EXTRA_TITLE);
            String description = data.getStringExtra(AddEditNoteActivitiy.EXTRA_DESCRIPTION);
            int priority = data.getIntExtra(AddEditNoteActivitiy.EXTRA_PRIORITY,1);

            Note note = new Note(title,description,priority);
            note.setId(id); // choose specific note with same id
            noteViewModel.update(note);
            Toast.makeText(this,"Note was EDITED",Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(this,"Note was NOT saved",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.delete_all_notes:
                noteViewModel.deleteAllNotes();
                Toast.makeText(this,"All notes deleted",Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }


}
