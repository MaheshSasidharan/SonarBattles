package iffi.mah.preetha;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class Score extends Activity {

	My_Application my_app;

	MediaPlayer soundBackground, soundBack;
	TextView heading;
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

		setContentView(R.layout.score);

		SharedPreferences getPrefs = PreferenceManager
				.getDefaultSharedPreferences(my_app.getAppContext());
		playsound = getPrefs.getBoolean("MusicKey", true);

		soundBackground = MediaPlayer.create(Score.this, R.raw.mainscore);
		soundBack = MediaPlayer.create(Score.this, R.raw.clickfour);
		soundBackground.setLooping(true);
		if (playsound)
			try {
				soundBackground.start();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				System.out.println("MEDIA ERROR rrrrrrrrrrrr Score.java");
			}

		font = Typeface.createFromAsset(getAssets(), "earthquake.ttf");

		heading = (TextView) findViewById(R.id.tvInstruction);
		heading.setTypeface(font);
		heading.setTextColor(Color.BLUE);

		TextView tvName1 = (TextView) findViewById(R.id.tvName1);
		TextView tvName2 = (TextView) findViewById(R.id.tvName2);
		TextView tvName3 = (TextView) findViewById(R.id.tvName3);
		TextView tvName4 = (TextView) findViewById(R.id.tvName4);
		TextView tvName5 = (TextView) findViewById(R.id.tvName5);
		
		TextView tvScore1 = (TextView) findViewById(R.id.tvScore1);
		TextView tvScore2 = (TextView) findViewById(R.id.tvScore2);
		TextView tvScore3 = (TextView) findViewById(R.id.tvScore3);
		TextView tvScore4 = (TextView) findViewById(R.id.tvScore4);
		TextView tvScore5 = (TextView) findViewById(R.id.tvScore5);

		RankScore info = new RankScore(this);
		info.open();

		String[] Namedata = info.getNameData();
		String[] Scoredata = info.getScoreData();

		info.close();

		tvName1.setText(Namedata[0]);
		tvName2.setText(Namedata[1]);
		tvName3.setText(Namedata[2]);
		tvName4.setText(Namedata[3]);
		tvName5.setText(Namedata[4]);
		
		tvScore1.setText(Scoredata[0]);
		tvScore2.setText(Scoredata[1]);
		tvScore3.setText(Scoredata[2]);
		tvScore4.setText(Scoredata[3]);
		tvScore5.setText(Scoredata[4]);
	}

	public void onBackPressed() {
		super.onBackPressed();
		if(playsound)
		try {
			soundBack.start();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			System.out.println("MEDIA ERROR rrrrrrrrrrrr - Score - soundBack");
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
