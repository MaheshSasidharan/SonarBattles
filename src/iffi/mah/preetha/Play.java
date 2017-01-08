package iffi.mah.preetha;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.view.WindowManager.LayoutParams;

public class Play extends Activity implements OnTouchListener {

	My_Application my_app;

	MediaPlayer soundOcean;
	Play_Sub_Class play_sub_class_object; // a view of base class
	private static int Action_Down = 1;
	private static int Action_Move = 2;

	boolean back_has_been_pressed = false, first_radar_click = true;
	float x1, y1, x2, y2; // missiles initial and final coordinates
	float constant, divisionx, m; // for the line equation for missiles
									// trajectory

	Background our_background; // sub-class object
	Submarine submarine;
	Radar radar;
	Missile_button missile_button;

	boolean game_end = false, once = true, playsound;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		my_app = new My_Application();
		my_app.set_end_of_game(false);

		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		getWindow().addFlags(LayoutParams.FLAG_KEEP_SCREEN_ON);

		play_sub_class_object = new Play_Sub_Class(this);
		play_sub_class_object.setOnTouchListener(this);

		x1 = y1 = 0;

		setContentView(play_sub_class_object);

		SharedPreferences getPrefs = PreferenceManager
				.getDefaultSharedPreferences(my_app.getAppContext());
		playsound = getPrefs.getBoolean("MusicKey", true);

		soundOcean = MediaPlayer.create(this, R.raw.ocean);
		soundOcean.setLooping(true);
	}

	@Override
	protected void onStop() {
		super.onStop();
		play_sub_class_object.onStop();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// this.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		// submarines onPause to unRegister accelerometer Sensor
		submarine.onPause();
		// play_sub_class onPause to pause thread
		play_sub_class_object.my_pause();
		// background onPause to pause Timer
		our_background.onPause();

		
		if (once) {
			if (!back_has_been_pressed) {
				Log.w("Play", "Back Not Pressed");
				Bundle my_bundle = new Bundle();
				Intent my_intent = new Intent(Play.this, PausedGameScreen.class);
				my_intent.putExtras(my_bundle);
				startActivityForResult(my_intent, 0);

				overridePendingTransition(R.anim.push_bottom_in,
						R.anim.push_top_out);
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		submarine.onResume();
		play_sub_class_object.my_resume();
		our_background.onResume();
		if (playsound)
			try {
				soundOcean.start();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				System.out.println("MEDIA ERROR rrrrrrrrrrrr - Play.java - Ocean");
			}
			
	}

	public class Play_Sub_Class extends SurfaceView implements Runnable {

		Thread t = null;
		SurfaceHolder holder;
		boolean isitOK = false;
		final ScheduledExecutorService worker;

		public Play_Sub_Class(Context context) {
			super(context);
			holder = getHolder();
			worker = Executors.newSingleThreadScheduledExecutor();

			Display display = getWindowManager().getDefaultDisplay();
			my_app.set_width_of_screen(display.getWidth());
			my_app.set_height_of_screen(display.getHeight());

			our_background = new Background();
			submarine = new Submarine();
			radar = new Radar();
			missile_button = new Missile_button();
		}

		public void onStop() {
			our_background.onStop();
			submarine.onStop();
			radar.onStop();
			missile_button.onStop();
		}

		public void run() {
			while (isitOK == true) {
				// performs canvas drawing
				if (!holder.getSurface().isValid()) { // check if holder is
														// available, if its not
														// der, keep looping n
														// looking for holder!!
					continue;
				}

				Canvas c = holder.lockCanvas();
				onDraw(c);
				holder.unlockCanvasAndPost(c);

				/*
				 * if(My_Application.get_game_sound())
				 * System.out.println("NO SOUND"); else
				 * System.out.println("THERE is SOUND");
				 */

				if (My_Application.get_end_of_game()) {
					if (once)
						end_game_method();
					once = false;
				}
			}
		}

		void end_game_method() {
			Runnable task = new Runnable() {
				public void run() {
					Bundle my_bundle = new Bundle();
					Intent my_intent = new Intent(Play.this, EndOfGame.class);
					my_intent.putExtras(my_bundle);
					startActivityForResult(my_intent, 0);
					overridePendingTransition(R.anim.push_bottom_in,
							R.anim.push_top_out);
				}
			};
			worker.schedule(task, 5, TimeUnit.SECONDS);
		}

		protected void onDraw(Canvas canvas) {
			canvas.drawARGB(256, 0, 0, 0);

			our_background.submarine_x = submarine.get_submarine_x();
			our_background.submarine_y = submarine.get_submarine_y();

			// draw first half of background, den the submarine and missile and
			// den again second half of the background
			our_background.onDraw(canvas);
			submarine.onDraw(canvas);
			our_background.onDraw(canvas);
			radar.onDraw(canvas);
			// missile_button.onDraw(canvas);

			try {
				Thread.sleep(25);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		public void my_pause() {
			isitOK = false;
			while (true) {
				try {
					t.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				break;
			}
			t = null;
		}

		public void my_resume() {
			isitOK = true;
			t = new Thread(this);
			t.start();
		}
	}

	public boolean onTouch(View v, MotionEvent me) {

		if (!My_Application.get_end_of_game()) {
			try {
				Thread.sleep(50); // to improve performance,else more
									// computations
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			switch (me.getAction()) {
			case MotionEvent.ACTION_MOVE:
				if (radar.radar_mode) {
					// missile_button.change_missile_mode();
					our_background.sonar_active_ping_area(me.getX(), me.getY(),
							Action_Move);
				}
				break;

			case MotionEvent.ACTION_DOWN:
				radar.gettouchxy(me.getX(), me.getY());

				if (radar.radar_mode) {
					if (!first_radar_click) {
						if (submarine
								.check_if_sub_has_been_clicked_in_radar_mode(
										me.getX(), me.getY())) {
							our_background.sonar_visual_bubble_area();
						} else {
							our_background.sonar_active_ping_area(me.getX(),
									me.getY(), Action_Down);
						}
					}
					first_radar_click = !first_radar_click;
				} else {
					// when radar is clicked dont ping there at first click
					// our_background.clicked_number_of_times = 1;
					if (first_radar_click) {
						our_background
								.myMissileMotionCalc(
										(submarine.x
												+ submarine.submarine_bit
														.getWidth() - 10),
										(submarine.y + submarine.submarine_bit
												.getHeight() / 2), me.getX(),
										me.getY(), true);
					}
					first_radar_click = true;
				}
				break;

			case MotionEvent.ACTION_UP:
				if (radar.radar_mode) {
					if (first_radar_click) {
						radar.change_radar_mode();
					}
				}
			}
		}
		return true;
	}

	public void onBackPressed() {
		back_has_been_pressed = true;
		Bundle my_bundle = new Bundle();
		Intent my_intent = new Intent(Play.this, PausedGameScreen.class);
		my_intent.putExtras(my_bundle);
		startActivityForResult(my_intent, 0);
		overridePendingTransition(R.anim.push_bottom_in, R.anim.push_top_out);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		back_has_been_pressed = false;

		Intent Play_my_intent;
		Bundle Play_my_bundle;

		String nothing = "Nothing";
		String gotomenu = "Go To Menu ??";
		String endwholegame = "Quit The Game ??";

		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			Bundle my_bundle = data.getExtras();
			String s = my_bundle.getString("Wat_To_Do");

			if (s.equals(nothing)) {
				// Do nothing - Resume Game
			}
			if (s.equals(gotomenu)) {
				Play_my_intent = new Intent();
				Play_my_bundle = new Bundle();
				Play_my_bundle.putString("Wat_To_Do", gotomenu);
				Play_my_intent.putExtras(Play_my_bundle);
				setResult(RESULT_OK, Play_my_intent);
				soundOcean.stop();
				finish();
			}
			if (s.equals(endwholegame)) {
				Play_my_intent = new Intent();
				Play_my_bundle = new Bundle();
				Play_my_bundle.putString("Wat_To_Do", endwholegame);
				Play_my_intent.putExtras(Play_my_bundle);
				setResult(RESULT_OK, Play_my_intent);
				soundOcean.stop();
				finish();
			}
		}
	}
}