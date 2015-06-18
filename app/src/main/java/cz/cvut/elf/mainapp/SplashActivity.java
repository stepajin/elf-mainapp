package cz.cvut.elf.mainapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import cz.cvut.elf.R;
import cz.cvut.elf.mainapp.login.LoginActivity;
import cz.cvut.elf.mainapp.update.CheckUpdateService;
import cz.cvut.elf.mainapp.update.PlanetUpdateTask;

public class SplashActivity extends Activity {
	private static final int SPLASH_TIME_OUT = 3000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		
		CheckUpdateService.schedule(this);
		PlanetUpdateTask infoDownloader = new PlanetUpdateTask(this, false);
		infoDownloader.execute();

		
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				Intent i = new Intent(SplashActivity.this, LoginActivity.class);
				startActivity(i);
				finish();
			}
		}, SPLASH_TIME_OUT);
	}
}