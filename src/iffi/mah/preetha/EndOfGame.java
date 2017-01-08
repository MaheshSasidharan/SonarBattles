package iffi.mah.preetha;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class EndOfGame extends Activity implements OnClickListener{
	
	My_Application my_app;
	SharedPreferences getPrefs;
	MediaPlayer sound;
	
	Button But_OK, sqlOK;
	TextView tv_total,tv_enemies_killed, tv_enemies_escaped, tv_level_reached, tv_game_over;
	EditText sqlName;
	Typeface font;
	int curScore;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		int difficulty_level;
		my_app = new My_Application();
		
		SharedPreferences getData = PreferenceManager
				.getDefaultSharedPreferences(my_app.getAppContext());
		difficulty_level = Integer.parseInt(getData.getString("difficulty_key",
				"1"));
		
		int level_reached_score, survived_score, enemies_killed_score, enemies_survived_score,total_score;
		
		level_reached_score = my_app.get_level() * difficulty_level;
		survived_score = my_app.get_time() + (my_app.get_time() * 3); 
		enemies_killed_score = my_app.get_enemies_killed() + (my_app.get_enemies_killed() * difficulty_level); 
		enemies_survived_score = -(my_app.get_enemies_escaped() + (my_app.get_enemies_escaped() * difficulty_level)); //negative
		
		total_score = level_reached_score + survived_score + enemies_killed_score + enemies_survived_score ;
				
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		setContentView(R.layout.game_over);
		
		SharedPreferences getPrefs1 = PreferenceManager.getDefaultSharedPreferences(my_app.getAppContext());
		boolean playsound = getPrefs1.getBoolean("MusicKey", true);
		
		sound = MediaPlayer.create(EndOfGame.this, R.raw.gameover);
		sound.setLooping(true);
		
		if(playsound)
		try {
			sound.start();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			System.out.println("MEDIA ERROR rrrrrrrrrrrr - EndOfGame - gameoversound");
		}
		
		
		font = Typeface.createFromAsset(getAssets(), "earthquake.ttf");
		tv_game_over = (TextView) findViewById(R.id.tvGameOver);
		tv_game_over.setTextSize(20);
		tv_game_over.setTypeface(font);	
		tv_game_over.setText("Game Over \n Difficulty Level " + difficulty_level);
				
		But_OK = (Button) findViewById(R.id.bOK);
		But_OK.startAnimation(AnimationUtils.loadAnimation(this, R.anim.anim_alpha));
		But_OK.setOnClickListener(this);
		But_OK.setTypeface(font);
		
		font = Typeface.createFromAsset(getAssets(), "armystyle.ttf");
		
		tv_level_reached = (TextView) findViewById(R.id.tvLevelReached);
		tv_level_reached.setText("You Reached Level  -- " + my_app.get_level() + "\t\t\t" + level_reached_score + "\n" +
				"You Survived for  --  " + my_app.get_time() + "\t\t" + survived_score + "\n" +
				"And Torched  --  " + my_app.get_enemies_killed() + "\t\t\t\t\t\t" + enemies_killed_score + "\n" +
				"Escaped!!  --  " + my_app.get_enemies_escaped() + "\t\t\t\t\t\t\t\t\t" + enemies_survived_score
				);
		tv_level_reached.setTypeface(font);
		
		tv_total = (TextView) findViewById(R.id.tvTotal);
		tv_total.setText("\nTOTAL \t" + total_score);
		tv_total.setTextSize(25);
		tv_total.setTypeface(font);
		
	
		curScore = total_score;
		
		getPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		sqlName = (EditText) findViewById(R.id.etSqlName);
		//if no name has been given yet, then keep the default name as "Player"
		sqlName.setText(getPrefs.getString("name", "Player"));
	}
	
	public void onBackPressed(){
		Intent Paused_my_intent;
		Bundle Paused_my_bundle;
		String go_to_menu = "Go To Menu ??";
		
		Paused_my_intent = new Intent();
		Paused_my_bundle = new Bundle();
		Paused_my_bundle.putString("Wat_To_Do", go_to_menu);
		Paused_my_intent.putExtras(Paused_my_bundle);
		setResult(RESULT_OK, Paused_my_intent);
		finish();
		
		my_app.reset_values();
		
		super.onBackPressed();
		overridePendingTransition(R.anim.push_top_in, R.anim.push_bottom_out);
	}

	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.bOK:
			String name = sqlName.getText().toString();
			RankScore entry = new RankScore(EndOfGame.this);
			
			entry.open();
			entry.createEntry(name,curScore);
			
			entry.close();
			
			getPrefs.edit().putString("name", name).commit();
			
			this.onBackPressed();	
			break;
		}
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		sound.release();
	}
}
