package iffi.mah.preetha;

import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.WindowManager;

public class Options extends PreferenceActivity {

	MediaPlayer soundBackground, soundBack;
	boolean playsound;
	
	My_Application my_app;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		my_app = new My_Application();

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		addPreferencesFromResource(R.xml.options);
		
		SharedPreferences getPrefs = PreferenceManager.getDefaultSharedPreferences(my_app.getAppContext());
		playsound = getPrefs.getBoolean("MusicKey", true);

		soundBackground = MediaPlayer.create(Options.this, R.raw.option);
		soundBack = MediaPlayer.create(Options.this, R.raw.clickfour);
		soundBackground.setLooping(true);
		
		if(playsound)
			try {
				soundBackground.start();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				System.out.println("MEDIA ERROR rrrrrrrrrrrr - Options soundbackground");
			}
			
	}

	public void onBackPressed() {
		super.onBackPressed();
		if(playsound)
		try {
			soundBack.start();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			System.out.println("MEDIA ERROR rrrrrrrrrrrr-  options - soundback");
		}
		
		overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
	}

	@Override
	protected void onStop() {
		super.onStop();
		soundBackground.release();
		soundBack.release();
	}
}
