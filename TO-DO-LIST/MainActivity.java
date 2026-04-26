package com.example.todolist;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    EditText inputTask, inputDate;
    Button addButton;
    ListView listView;
    TextView tvTaskCount, tvProgressPercent, emptyStateLayout;
    View progressBar;
    LinearLayout emptyState;

    ArrayList<Task> taskList;
    TaskAdapter adapter;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputTask        = findViewById(R.id.inputTask);
        inputDate        = findViewById(R.id.inputDate);
        addButton        = findViewById(R.id.addButton);
        listView         = findViewById(R.id.listView);
        tvTaskCount      = findViewById(R.id.tvTaskCount);
        tvProgressPercent = findViewById(R.id.tvProgressPercent);
        progressBar      = findViewById(R.id.progressBar);
        emptyState       = findViewById(R.id.emptyState);

        dbHelper  = new DBHelper(this);
        taskList  = new ArrayList<>();
        adapter   = new TaskAdapter();

        listView.setAdapter(adapter);
        loadTasks();


        inputDate.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            DatePickerDialog dialog = new DatePickerDialog(
                    MainActivity.this,
                    (view, year, month, day) -> {
                        String date = day + "/" + (month + 1) + "/" + year;
                        inputDate.setText(date);
                    },
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
            );
            dialog.getDatePicker().setMinDate(cal.getTimeInMillis());
            dialog.show();
        });


        addButton.setOnClickListener(v -> {
            String task = inputTask.getText().toString().trim();
            String date = inputDate.getText().toString().trim();

            if (!task.isEmpty() && !date.isEmpty()) {
                dbHelper.insertTask(task, date);
                Toast.makeText(this, "Task added!", Toast.LENGTH_SHORT).show();
                loadTasks();
                inputTask.setText("");
                inputDate.setText("");
                inputTask.requestFocus();
            } else {
                if (task.isEmpty()) {
                    inputTask.setError("Enter a task");
                    inputTask.requestFocus();
                } else {
                    Toast.makeText(this, "Please select a due date", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

 
    public void loadTasks() {
        taskList.clear();

        Cursor cursor = dbHelper.getTasks();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Task t = new Task();
                t.id          = cursor.getInt(0);
                t.title       = cursor.getString(1);
                t.date        = cursor.getString(2);
                t.isCompleted = cursor.getInt(3) == 1;
                taskList.add(t);
            } while (cursor.moveToNext());
            cursor.close();
        }

        autoCheckExpiredTasks();
        taskList.sort((a, b) -> Boolean.compare(a.isCompleted, b.isCompleted));
        updateHeader();
        toggleEmptyState();
        adapter.notifyDataSetChanged();
    }


    private void autoCheckExpiredTasks() {
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

        for (Task task : taskList) {
            if (!task.isCompleted) {
                try {
                    String[] parts = task.date.split("/");
                    int day   = Integer.parseInt(parts[0]);
                    int month = Integer.parseInt(parts[1]) - 1;
                    int year  = Integer.parseInt(parts[2]);

                    Calendar dueDate = Calendar.getInstance();
                    dueDate.set(year, month, day, 0, 0, 0);
                    dueDate.set(Calendar.MILLISECOND, 0);

                    if (dueDate.before(today)) {
                        task.isCompleted = true;
                        dbHelper.updateStatus(task.id, 1);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private void updateHeader() {
        int total     = taskList.size();
        int completed = 0;
        for (Task t : taskList) if (t.isCompleted) completed++;

        tvTaskCount.setText(total + (total == 1 ? " task" : " tasks"));

        int percent = total == 0 ? 0 : (int) ((completed / (float) total) * 100);
        tvProgressPercent.setText(percent + "%");

 
        progressBar.post(() -> {
            int parentWidth = ((View) progressBar.getParent()).getWidth();
            int targetWidth = (int) (parentWidth * (percent / 100f));
            progressBar.getLayoutParams().width = targetWidth;
            progressBar.requestLayout();
        });
    }


    private void toggleEmptyState() {
        if (taskList.isEmpty()) {
            emptyState.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        } else {
            emptyState.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
        }
    }

    class Task {
        int id;
        String title;
        String date;
        boolean isCompleted;
    }


    class TaskAdapter extends BaseAdapter {

        @Override
        public int getCount() { return taskList.size(); }

        @Override
        public Object getItem(int position) { return taskList.get(position); }

        @Override
        public long getItemId(int position) { return position; }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.item_task, parent, false);
            }

            CheckBox checkBox = convertView.findViewById(R.id.checkBox);
            TextView title    = convertView.findViewById(R.id.taskText);
            TextView date     = convertView.findViewById(R.id.taskDate);
            TextView badge    = convertView.findViewById(R.id.tvBadge);
            View     cardRoot = convertView.findViewById(R.id.cardRoot);

            Task task = taskList.get(position);

            title.setText(task.title);
            date.setText("📅 " + task.date);

 
            checkBox.setOnCheckedChangeListener(null);
            checkBox.setChecked(task.isCompleted);

            if (task.isCompleted) {
                title.setPaintFlags(title.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                title.setTextColor(0xFFBDBDBD);
                date.setTextColor(0xFFBDBDBD);
                cardRoot.setAlpha(0.6f);
                badge.setVisibility(View.GONE);
            } else {
        
                title.setPaintFlags(title.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                title.setTextColor(0xFF1A1A2E);
                date.setTextColor(0xFF9098C8);
                cardRoot.setAlpha(1.0f);

          
                if (isOverdue(task.date)) {
                    badge.setVisibility(View.VISIBLE);
                } else {
                    badge.setVisibility(View.GONE);
                }
            }

            checkBox.setOnCheckedChangeListener((btn, isChecked) -> {
                task.isCompleted = isChecked;
                dbHelper.updateStatus(task.id, isChecked ? 1 : 0);
                loadTasks();
            });

            return convertView;
        }

 
        private boolean isOverdue(String dateStr) {
            try {
                String[] parts = dateStr.split("/");
                int day   = Integer.parseInt(parts[0]);
                int month = Integer.parseInt(parts[1]) - 1;
                int year  = Integer.parseInt(parts[2]);

                Calendar today = Calendar.getInstance();
                today.set(Calendar.HOUR_OF_DAY, 0);
                today.set(Calendar.MINUTE, 0);
                today.set(Calendar.SECOND, 0);
                today.set(Calendar.MILLISECOND, 0);

                Calendar due = Calendar.getInstance();
                due.set(year, month, day, 0, 0, 0);
                due.set(Calendar.MILLISECOND, 0);

                return due.before(today);
            } catch (Exception e) {
                return false;
            }
        }
    }
}
