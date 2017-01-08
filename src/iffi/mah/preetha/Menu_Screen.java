package iffi.mah.preetha;


import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.BounceInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;

public class Menu_Screen extends Activity implements View.OnClickListener {

	My_Application my_app;
	MediaPlayer soundFalling, soundClick;
	Vibrator vibe;
	Button Play,Option,Instruction,Score;
	boolean mShowing = true;
	boolean playsound;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.w("HELLO", "Entered Menu");
		
		my_app = new My_Application();
		vibe = (Vibrator) my_app.getAppContext().getSystemService(
				Context.VIBRATOR_SERVICE);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		setContentView(R.layout.menu_screen);
		
		SharedPreferences getPrefs = PreferenceManager.getDefaultSharedPreferences(my_app.getAppContext());
		playsound = getPrefs.getBoolean("MusicKey", true);
		
		soundFalling = MediaPlayer.create(Menu_Screen.this, R.raw.menu);
		soundClick = MediaPlayer.create(Menu_Screen.this, R.raw.clickone);
		try {
			if(playsound)
			soundFalling.start();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			System.out.println("MEDIA ERROR rrrrrrrrrrrr - menu - soundFalling");
		}
		
						
		Play = (Button) findViewById(R.id.bPlay);
		Option = (Button) findViewById(R.id.bOptions);
		Instruction = (Button) findViewById(R.id.bIntructions);
		Score = (Button) findViewById(R.id.bScore);

		Play.setOnClickListener(this);
		Option.setOnClickListener(this);
		Instruction.setOnClickListener(this);
		Score.setOnClickListener(this);
		runanimation();
	}
	
	
	private void runanimation(){
		AnimationSet ani_set_1,ani_set_2,ani_set_3,ani_set_4;
		
		Random rand = new Random();
		int randA=rand.nextInt(500);
		int randB=rand.nextInt(500);
		int randC=rand.nextInt(500);
		int randD=rand.nextInt(500);

		//Play Animation
		ani_set_1 = new AnimationSet(true);
		ani_set_1.setFillEnabled(true);
		ani_set_1.setInterpolator(new BounceInterpolator());
		
	    TranslateAnimation trans_ani = new TranslateAnimation(randA-randB,Play.getLeft(), randC-randD,Play.getTop()); 
	    trans_ani.setDuration(1500);
	    
	    AlphaAnimation alpha_ani = new AlphaAnimation(0,1);
	    alpha_ani.setDuration(1500);
	    
	    ani_set_1.addAnimation(alpha_ani);
	    ani_set_1.addAnimation(trans_ani); 
	    
	    Play.startAnimation(ani_set_1);
	 	    
		//Option Animation
	    ani_set_2 = new AnimationSet(true);
		ani_set_2.setFillEnabled(true);
		ani_set_2.setInterpolator(new BounceInterpolator());
	    
	    trans_ani = new TranslateAnimation(randA-randC,Option.getLeft(), randD-randB,Option.getTop());
	    trans_ani.setDuration(1500);
	   
	    ani_set_2.addAnimation(alpha_ani);
	    ani_set_2.addAnimation(trans_ani);

		Option.startAnimation(ani_set_2);
		
		//Option Animation
	    ani_set_3 = new AnimationSet(true);
		ani_set_3.setFillEnabled(true);
		ani_set_3.setInterpolator(new BounceInterpolator());
	    
	    trans_ani = new TranslateAnimation(randA-randD,Play.getLeft(), randB-randC,Play.getTop());
	    trans_ani.setDuration(1500);
	   
	    ani_set_3.addAnimation(alpha_ani);
	    ani_set_3.addAnimation(trans_ani);

		Instruction.startAnimation(ani_set_3);
		
		//Option Animation
	    ani_set_4 = new AnimationSet(true);
		ani_set_4.setFillEnabled(true);
		ani_set_4.setInterpolator(new BounceInterpolator());
	    
	    trans_ani = new TranslateAnimation(randC-randD,Play.getLeft(), randB-randA,Play.getTop());
	    trans_ani.setDuration(1500);
	   
	    ani_set_4.addAnimation(alpha_ani);
	    ani_set_4.addAnimation(trans_ani);

		Score.startAnimation(ani_set_4);	
	}
	
	public void onBackPressed(){
		Bundle my_bundle = new Bundle();
		my_bundle.putString("Objective", "Leave Sonar Battle ??");
		Intent my_intent = new Intent(Menu_Screen.this, Are_you_sure.class);
		my_intent.putExtras(my_bundle);
		startActivityForResult(my_intent, 0);
		soundFalling.release();
		overridePendingTransition(R.anim.push_top_in, R.anim.push_bottom_out);
	}
	
	public void onClick(View v) {
		vibe.vibrate(50);
		if(playsound)
		try {
			soundClick.start();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			System.out.println("MEDIA ERROR rrrrrrrrrrrr - Menu - soundClick");
		}
		
		switch (v.getId()) {
		case R.id.bPlay:
			Log.w("PLAY", "Play Play Play");
			start_this_activity("Play");
			break;
		case R.id.bOptions:
			Log.w("OPTIONS", "Options Options Options");
			start_this_activity("Options");
			break;
		case R.id.bIntructions:
			Log.w("INSTRUCTIONS", "Intructions Intructions Intructions");
			start_this_activity("Instructions");
			break;
		case R.id.bScore:
			Log.w("SCORE", "Score Score Score");
			if(mShowing ){
	            Animation animation = new AlphaAnimation(1.0f, 0.5f);
	            animation.setFillAfter(true);
	            v.startAnimation(animation);
	            } else {
	                Animation animation = new AlphaAnimation(0.5f, 1.0f);
	                animation.setFillAfter(true);
	                v.startAnimation(animation);
	            }

	            mShowing = !mShowing;
			start_this_activity("Score");
			break;
		}
	}
	
	
	
	private void start_this_activity(String open_this_class) {
		// TODO Auto-generated method stub
		Log.w("Menu_Screen", "Before startactivityforresult");
		Class ourClass = null;
		try {
			ourClass = Class.forName("iffi.mah.preetha." + open_this_class);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		Bundle my_bundle = new Bundle();
		Intent my_intent = new Intent(Menu_Screen.this, ourClass);
		my_intent.putExtras(my_bundle);
		startActivityForResult(my_intent,0);
		overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		String gotomenu = "Go To Menu";
		String endwholegame = "Quit The Game ??";
		String quit_the_game = "Leave Sonar Battle ??";
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == RESULT_OK){
			Bundle my_bundle = data.getExtras();
			String s = my_bundle.getString("Wat_To_Do");
			if(s.equals(gotomenu)){
				// do nothing.. ur back in menu screen now
			}
			if(s.equals(endwholegame) || s.equals(quit_the_game)){
				soundClick.release();
				finish();
			}
		}
	}
}
