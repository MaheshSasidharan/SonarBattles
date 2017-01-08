package iffi.mah.preetha;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class Instructions extends Activity {

	My_Application my_app;
	
	MediaPlayer soundBackground, soundBack;
	TextView heading, text1, text2;
	Typeface font;
	boolean playsound;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		my_app = new My_Application();

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.instructions);

		font = Typeface.createFromAsset(getAssets(), "earthquake.ttf");

		heading = (TextView) findViewById(R.id.tvHeading);
		heading.setTypeface(font);
		heading.setTextColor(Color.DKGRAY);

		font = Typeface.createFromAsset(getAssets(), "armystyle.ttf");

		text1 = (TextView) findViewById(R.id.tvInstruction1);
		text2 = (TextView) findViewById(R.id.tvInstruction2);

		text1.setTypeface(font);
		text2.setTypeface(font);
		
		SharedPreferences getPrefs = PreferenceManager.getDefaultSharedPreferences(my_app.getAppContext());
		playsound = getPrefs.getBoolean("MusicKey", true);
		
		soundBackground = MediaPlayer.create(Instructions.this, R.raw.instruction);
		soundBack = MediaPlayer.create(Instructions.this, R.raw.clickfour);
		soundBackground.setLooping(true);
		
		if(playsound)
			try {
				soundBackground.start();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				System.out.println("MEDIA ERROR rrrrrrrrrrrr - instruction - soundbackground");
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
			System.out.println("MEDIA ERROR rrrrrrrrrrrr instruction - back");
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
