package cz.cvut.elf.mainapp.update;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

import cz.cvut.elf.R;
import cz.cvut.elf.mainapp.ElfConstants;
import cz.cvut.elf.mainapp.login.LoginActivity;
import cz.cvut.elf.mainapp.planetselection.Planet;

public class CheckUpdateService extends Service {

	public static void notificate(Planet planet, Context context) {
		NotificationCompat.Builder builder = new NotificationCompat.Builder(
				context)
				.setSmallIcon(R.drawable.notification)
				.setContentTitle(
						context.getString(R.string.notification_new_planet_short))
				.setContentText(
						context.getString(R.string.notification_new_planet)
								+ ": " + planet.getName());

		Intent intent = new Intent(context, LoginActivity.class);

		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);

		builder.setContentIntent(pendingIntent);

		int mNotificationId = 001;
		NotificationManager mNotifyMgr = (NotificationManager) context
				.getSystemService(NOTIFICATION_SERVICE);
		mNotifyMgr.notify(mNotificationId, builder.getNotification());
	}


	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		PlanetUpdateTask infoDownloader = new PlanetUpdateTask(this, true);
		infoDownloader.execute();
		return Service.START_NOT_STICKY;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	public static void schedule(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		if (!prefs.getBoolean("update", true) ) {
			return;
		}

		Intent intent = new Intent(context, CheckUpdateService.class);
		PendingIntent pending = PendingIntent.getService(context, 0, intent, 0);

		Calendar c = Calendar.getInstance();
		// c.add(Calendar.DAY_OF_YEAR, 1);
		c.set(Calendar.HOUR_OF_DAY, 14);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);

		final AlarmManager alarm = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		alarm.cancel(pending);

		if (ElfConstants.DEBUG) {
			alarm.setRepeating(AlarmManager.ELAPSED_REALTIME,
					SystemClock.elapsedRealtime(), 30 * 1000, pending);
		} else {
			alarm.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(),
					AlarmManager.INTERVAL_DAY, pending);
		}
	}

}
