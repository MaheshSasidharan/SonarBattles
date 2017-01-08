package iffi.mah.preetha;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

public class PausedGameScreen extends Activity implements OnClickListener {
	
	MediaPlayer soundClick1, soundClick2, soundClick3;
	TextView heading;
	Button But_resume,But_GoToMenu,But_Quit;
	Typeface font;
	boolean playsound;

	My_Application my_app;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		my_app = new My_Application();
		
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		setContentView(R.layout.pause_menu);
		
		heading = (TextView) findViewById(R.id.tvHeading);
		font = Typeface.createFromAsset(getAssets(), "earthquake.ttf");
		heading.setTypeface(font);		

		But_resume = (Button) findViewById(R.id.bReturnToGame);
		But_GoToMenu = (Button) findViewById(R.id.bGoToMenu);
		But_Quit = (Button) findViewById(R.id.bQuitGame);
	
		But_resume.startAnimation(AnimationUtils.loadAnimation(this, R.anim.rotation_reverse));
		But_GoToMenu.startAnimation(AnimationUtils.loadAnimation(this, R.anim.anim_alpha));
		But_Quit.startAnimation(AnimationUtils.loadAnimation(this, R.anim.rotation));
		
		font = Typeface.createFromAsset(getAssets(), "handsketch.ttf");
		
		But_resume.setTypeface(font);
		But_GoToMenu.setTypeface(font);
		But_Quit.setTypeface(font);
		
		But_resume.setOnClickListener(this);
		But_GoToMenu.setOnClickListener(this);
		But_Quit.setOnClickListener(this);
		
		SharedPreferences getPrefs = PreferenceManager.getDefaultSharedPreferences(my_app.getAppContext());
		playsound = getPrefs.getBoolean("MusicKey", true);
		
		soundClick1 = MediaPlayer.create(PausedGameScreen.this, R.raw.clicktwo);
		soundClick2 = MediaPlayer.create(PausedGameScreen.this, R.raw.clickthree);
		soundClick3 = MediaPlayer.create(PausedGameScreen.this, R.raw.clickfour);
	}
	
	public void onBackPressed(){
		super.onBackPressed();
		overridePendingTransition(R.anim.push_top_in, R.anim.push_bottom_out);
	}
	
	public void my_onBackPressed(){
		super.onBackPressed();
		overridePendingTransition(R.anim.push_top_in, R.anim.push_bottom_out);
	}
	
	public void onClick(View arg0) {
		
		String Paused_watistobedone;
		Intent Paused_my_intent;
		Bundle Paused_my_bundle;
		
		switch (arg0.getId()) {
		case R.id.bReturnToGame:
			if(playsound)
			try {
				soundClick1.start();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				System.out.println("MEDIA ERROR rrrrrrrrrrrr PAUSEDGAMESCREEN.java soundclick1 - ");
			}
			Paused_watistobedone = "Nothing";
			Paused_my_intent = new Intent();
			Paused_my_bundle = new Bundle();
			Paused_my_bundle.putString("Wat_To_Do", Paused_watistobedone);
			Paused_my_intent.putExtras(Paused_my_bundle);
			setResult(RESULT_OK, Paused_my_intent);
			my_onBackPressed();			
			break;

		case R.id.bGoToMenu:
			if(playsound)
			try {
				soundClick2.start();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				System.out.println("MEDIA ERROR rrrrrrrrrrrr PAUSEDGAMESCREEN.java soundclick2");
			}
			
			Paused_my_bundle = new Bundle();
			Paused_my_bundle.putString("Objective", "Go To Menu ??");
			Paused_my_intent = new Intent(PausedGameScreen.this, Are_you_sure.class);
			Paused_my_intent.putExtras(Paused_my_bundle);
			startActivityForResult(Paused_my_intent, 0);
			overridePendingTransition(R.anim.push_top_in, R.anim.push_bottom_out);
			break;

		case R.id.bQuitGame:
			if(playsound)
				try {
				soundClick3.start();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				System.out.println("MEDIA ERROR rrrrrrrrrrrr - pausedgamescreen - quit.sound");
			}
			
			Paused_my_bundle = new Bundle();
			Paused_my_bundle.putString("Objective", "Quit The Game ??");
			Paused_my_intent = new Intent(PausedGameScreen.this, Are_you_sure.class);
			Paused_my_intent.putExtras(Paused_my_bundle);
			startActivityForResult(Paused_my_intent, 0);
			overridePendingTransition(R.anim.push_top_in, R.anim.push_bottom_out);
			break;
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		Intent Paused_my_intent;
		Bundle Paused_my_bundle;
		
		String go_to_menu = "Go To Menu ??";
		String quit_the_game = "Quit The Game ??";
		String notreally = "do nothing";
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == RESULT_OK){
			Bundle my_bundle = data.getExtras();
			String s = my_bundle.getString("Wat_To_Do");
			if(s.equals(quit_the_game)){
				Paused_my_intent = new Intent();
				Paused_my_bundle = new Bundle();
				Paused_my_bundle.putString("Wat_To_Do", quit_the_game);
				Paused_my_intent.putExtras(Paused_my_bundle);
				setResult(RESULT_OK, Paused_my_intent);
				finish();
			}
			if(s.equals(go_to_menu)){
				Paused_my_intent = new Intent();
				Paused_my_bundle = new Bundle();
				Paused_my_bundle.putString("Wat_To_Do", go_to_menu);
				Paused_my_intent.putExtras(Paused_my_bundle);
				setResult(RESULT_OK, Paused_my_intent);
				finish();
			}
			if(s.equals(notreally)){
				// do nothing..
			}
		}
	}
}
