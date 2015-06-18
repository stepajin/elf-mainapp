package cz.cvut.elf.mainapp.planetselection;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;

import cz.cvut.elf.mainapp.R;
import cz.cvut.elf.mainapp.login.User;

public class TaskStatsFragment extends SherlockFragment {

	TextView taskNameView;
	Task task;
	ListView listView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.fragment_planet_task_stats,
				container, false);

		taskNameView = (TextView) v.findViewById(R.id.task_name_text);

		if (task == null) {
			try {
				task = (Task) savedInstanceState.getSerializable("Task");
			} catch (Exception e) {
			}
		}

		if (task != null) {
			taskNameView.setText(task.getName());
		}

		try {
			PlanetStatisticsActivity activity = (PlanetStatisticsActivity) getActivity();
			ContentResolver cr = activity.getContentResolver();
			Cursor c = cr.query(
					Uri.parse("content://cz.cvut.elf."
							+ activity.getPlanetShortcut()
							+ ".provider/results"), null, "task_id = ?",
					new String[] { "" + task.getId() }, null);

			Task.Result data[] = new Task.Result[c.getCount()];
			int i = 0;
			c.moveToFirst();

			while (!c.isAfterLast()) {
				Task.Result result = new Task.Result();
				result.accuracy = c.getInt(c.getColumnIndex("accuracy"));
				result.time = c.getInt(c.getColumnIndex("time"));
				result.difficulty = c.getInt(c.getColumnIndex("difficulty"));
				data[i++] = result;
				c.moveToNext();
			}
			c.close();

			YourAdapter adapter = new YourAdapter(activity, data);
			listView = (ListView) v.findViewById(R.id.stats_list);
			listView.setAdapter(adapter);

		} catch (Exception e) {
			Log.e("TaskStatsFragment", "used wrongly " + e.getMessage());
		}
		return v;
	}

	@Override
	public void onSaveInstanceState(Bundle bundle) {
		if (task != null) {
			bundle.putSerializable("Task", task);
		}
		super.onSaveInstanceState(bundle);
	}

	public void setTask(Task task) {
		this.task = task;
	}

	static class YourAdapter extends BaseAdapter {

		Context context;
		Task.Result[] data;
		private static LayoutInflater inflater = null;

		public YourAdapter(Context context, Task.Result[] data) {
			this.context = context;
			this.data = data;
			inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			return data.length;
		}

		@Override
		public Object getItem(int position) {
			return data[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View vi = convertView;
			if (vi == null) {
				vi = inflater.inflate(R.layout.list_item_stats, null);
			}
			((ImageView) vi.findViewById(R.id.task_stats_image))
					.setImageResource(User.getBitmap(User.selectedUser
							.getIcon()));

			((TextView) vi.findViewById(R.id.task_stats_accuracy))
					.setText("Přesnost " + data[position].accuracy);
			((TextView) vi.findViewById(R.id.task_stats_time)).setText("Čas "
					+ data[position].time);

			return vi;
		}
	}
}
