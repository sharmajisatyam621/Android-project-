package com.example.todolist;

import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    EditText inputTask;
    Button addButton;
    ListView listView;

    ArrayList<Task> taskList;
    TaskAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputTask = findViewById(R.id.inputTask);
        addButton = findViewById(R.id.addButton);
        listView = findViewById(R.id.listView);

        taskList = new ArrayList<>();
        adapter = new TaskAdapter();
        listView.setAdapter(adapter);

        addButton.setOnClickListener(v -> {
            String taskText = inputTask.getText().toString();
            if (!taskText.isEmpty()) {
                taskList.add(new Task(taskText, false));
                adapter.notifyDataSetChanged();
                inputTask.setText("");
            }
        });
    }

    // Task Model Class
  static  class Task {
        String title;
        boolean isCompleted;

        Task(String title, boolean isCompleted) {
            this.title = title;
            this.isCompleted = isCompleted;
        }
    }

    // Custom Adapter
    class TaskAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return taskList.size();
        }

        @Override
        public Object getItem(int position) {
            return taskList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, android.view.ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.item_task, parent, false);
            }

            CheckBox checkBox = convertView.findViewById(R.id.checkBox);
            TextView textView = convertView.findViewById(R.id.taskText);

            Task task = taskList.get(position);

            textView.setText(task.title);
            checkBox.setChecked(task.isCompleted);

            // Strike-through if completed
            if (task.isCompleted) {
                textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                textView.setPaintFlags(textView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            }

            checkBox.setOnClickListener(v -> {
                task.isCompleted = !task.isCompleted;
                notifyDataSetChanged();
            });

            return convertView;
        }
    }
}
