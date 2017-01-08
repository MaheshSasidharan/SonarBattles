package iffi.mah.preetha;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

public class Our_Splash extends Activity {

	My_Application my_app;
	MediaPlayer ourSong;
	boolean playsound;
	Vibrator vibe;

	@Override
	protected void onCreate(Bundle mah_variable) {

		super.onCreate(mah_variable);

		my_app = new My_Application();
		this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.our_splash);
		final int SPLASH_TOTAL_DISPLAY_TIME = 6000;
		final int SPLASH_DISPLAY_TIME = 3000;

		final ImageView iv;
		iv = (ImageView) findViewById(R.id.ivMyIv);
		iv.setScaleType(ImageView.ScaleType.FIT_XY);
		
		vibe = (Vibrator) my_app.getAppContext().getSystemService(
				Context.VIBRATOR_SERVICE);
		
		SharedPreferences getPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		playsound = getPrefs.getBoolean("MusicKey", true);
		
		ourSong = MediaPlayer.create(Our_Splash.this, R.raw.firstscene);
		if(playsound)
			try {
				ourSong.start();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				System.out.println("MEDIA ERROR rrrrrrrrrrrr our_splash - oursong");
			}
			
		vibe.vibrate(75);

		new Handler().postDelayed(new Runnable() {
			public void run() {
				vibe.vibrate(75);
				ourSong = MediaPlayer.create(Our_Splash.this, R.raw.secondscene);
				if(playsound)
					try {
						ourSong.start();
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						System.out.println("MEDIA ERROR rrrrrrrrrrrr our_splash - oursong");
					}
					
				iv.setImageResource(R.drawable.gameintro);
			}
		}, SPLASH_DISPLAY_TIME);
		/*
		 * Create a new handler with which to start the main activity and close
		 * this splash activity after SPLASH_DISPLAY_TIME has elapsed.
		 */
		new Handler().postDelayed(new Runnable() {
			public void run() {
				/* Create an intent that will start the main activity. */
				Intent mainIntent = new Intent(Our_Splash.this,
						Menu_Screen.class);
				Our_Splash.this.startActivity(mainIntent);
				Our_Splash.this.finish();
				if(playsound)
					ourSong.release();
			}
		}, SPLASH_TOTAL_DISPLAY_TIME);
	}
	
	public void onBackPressed(){
		//do nothing, continue with splash screen
	}
}