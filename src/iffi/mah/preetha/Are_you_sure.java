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
import android.widget.TextView;

public class Are_you_sure extends Activity implements OnClickListener {
	
	MediaPlayer soundClick1, soundClick2;
	Button But_Absolutely,But_NotReally;
	TextView myText;
	Typeface font;
	boolean playsound;

	My_Application my_app;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		my_app = new My_Application();
		
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		setContentView(R.layout.are_you_sure);
		
		font = Typeface.createFromAsset(getAssets(), "handsketch.ttf");
		
		But_Absolutely = (Button) findViewById(R.id.bAbsolutely);
		But_NotReally = (Button) findViewById(R.id.bNotReally);
		
		But_Absolutely.setTypeface(font);
		But_NotReally.setTypeface(font);

		But_Absolutely.setOnClickListener(this);
		But_NotReally.setOnClickListener(this);
		
		But_Absolutely.startAnimation(AnimationUtils.loadAnimation(this, R.anim.rotation));
		But_NotReally.startAnimation(AnimationUtils.loadAnimation(this, R.anim.rotation_reverse));
		
		font = Typeface.createFromAsset(getAssets(), "earthquake.ttf");
		myText = (TextView) findViewById(R.id.tvMytext);
		myText.setTypeface(font);
		
		Bundle gotBasket = getIntent().getExtras();
		myText.setText(gotBasket.getString("Objective"));
		
		SharedPreferences getPrefs = PreferenceManager.getDefaultSharedPreferences(my_app.getAppContext());
		playsound = getPrefs.getBoolean("MusicKey", true);
		
		soundClick1 = MediaPlayer.create(Are_you_sure.this, R.raw.clickone);
		soundClick2 = MediaPlayer.create(Are_you_sure.this, R.raw.clicktwo);
	}
	
	public void onBackPressed(){
		super.onBackPressed();
		overridePendingTransition(R.anim.push_bottom_in, R.anim.push_top_out);
	}
	
	public void onClick(View arg0) {
		String watistobedone;
		Intent intent;
		Bundle bundle;
		
		switch (arg0.getId()) {
		case R.id.bAbsolutely:
			if(playsound)
				try {
				soundClick1.start();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				System.out.println("MEDIA ERROR rrrrrrrrrrrr AreYouSure.java soundclick1");
			}
			
			watistobedone = (String) myText.getText();
			intent = new Intent();
			bundle = new Bundle();
			bundle.putString("Wat_To_Do", watistobedone);
			intent.putExtras(bundle);
			setResult(RESULT_OK, intent);
			finish();
			break;

		case R.id.bNotReally:
			if(playsound)
				try {
				soundClick2.start();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				System.out.println("MEDIA ERROR rrrrrrrrrrrr AreYouSure.java soundclick2");
			}
			watistobedone = "Not Really";
			intent = new Intent();
			bundle = new Bundle();
			bundle.putString("Wat_To_Do", watistobedone);
			intent.putExtras(bundle);
			setResult(RESULT_OK, intent);
			onBackPressed();
			break;
		}
	}
}
