package iffi.mah.preetha;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Bitmap.Config;
import android.graphics.BlurMaskFilter.Blur;
import android.graphics.PorterDuff.Mode;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.os.Vibrator;
import android.preference.PreferenceManager;

public class Background {

	My_Application my_app;

	// double executed_times = 1;

	Rect src, des;
	int width_of_screen, height_of_screen;
	Bitmap crucify, bg_a, bg_b, bg_c, transparent_overlay,
			transparent_overlay_mutable, enemy_sub_bit, submarine_health,
			sonar_indicator_bit, missile_indicator;

	int src_a_X, src_a_Y, src_b_Y, src_c_Y, src_trans_X, src_trans_Y, time = 0;

	float trans_alpha, trans_alpha_increment = (float) 0.2;
	float submarine_x, submarine_y = 0;

	int clicked_number_of_times = 1;
	int sonar_ping_circle_radius = 120, sonar_active_circle_radius = 100;

	Paint trans_paint, trans_surf_paint, dashed_circle, Indicator_Paint;
	Canvas c2;

	Matrix matrix;

	float touchedx = -100, touchedy = -100;

	boolean change_ping_circle_radius = false,
			change_active_circle_radius = false,
			active_radius_end_not_reached = true, playsound;

	boolean draw_first_half = true;

	int far_bg_X, closer_bg_X;

	int missile_x, missile_y;
	Random rand;

	Vector<Enemy_Submarine> enemy_sub_vector;
	Vector<Automatic_Missile> my_missile_vector;
	Vector<Automatic_Enemy_Missile> enemy_missile_vector;

	private static int Action_Down = 1;
	// private static int Action_Move = 2;

	private int Sub_Damage = 0;
	private int Sonar_Indicator = 7;
	private int Missile_Left = 4;
	private int difficulty_level;
	private int rotate_circle = 0;

	Vibrator vibe;

	// TimerTask sonar_indicator_checker;
	Timer sonar_indicator_checker_timer;

	private SoundPool soundPool;
	private int soundMyShipBombed, soundEnemyShipBombed;
	boolean loaded = false;

	public Background() {
		my_app = new My_Application();

		// initially level 1
		my_app.set_level();

		bg_c = BitmapFactory.decodeResource(my_app.getAppContext()
				.getResources(), R.drawable.z_ocean);
		bg_b = BitmapFactory.decodeResource(my_app.getAppContext()
				.getResources(), R.drawable.y_hill);
		bg_a = BitmapFactory.decodeResource(my_app.getAppContext()
				.getResources(), R.drawable.x_grass);
		transparent_overlay = BitmapFactory.decodeResource(my_app
				.getAppContext().getResources(), R.drawable.trans_surface);
		enemy_sub_bit = BitmapFactory.decodeResource(my_app.getAppContext()
				.getResources(), R.drawable.enemy_submarine);
		submarine_health = BitmapFactory.decodeResource(my_app.getAppContext()
				.getResources(), R.drawable.submarine_health);
		sonar_indicator_bit = BitmapFactory.decodeResource(my_app
				.getAppContext().getResources(), R.drawable.sonar_indicator);
		missile_indicator = BitmapFactory.decodeResource(my_app.getAppContext()
				.getResources(), R.drawable.missile_icon_active);

		my_app.set_end_of_game(false);
		width_of_screen = My_Application.get_width_of_screen();
		height_of_screen = My_Application.get_height_of_screen();

		des = new Rect(0, 0, width_of_screen, height_of_screen);

		SharedPreferences getData = PreferenceManager
				.getDefaultSharedPreferences(my_app.getAppContext());
		// if a default value has not yet been mentioned, then keep the level 1
		difficulty_level = Integer.parseInt(getData.getString("difficulty_key",
				"1"));

		initialise_for_sonar_area();

		src_b_Y = height_of_screen - bg_b.getHeight();
		src_a_Y = height_of_screen - bg_a.getHeight();

		rand = new Random();
		enemy_sub_vector = new Vector<Enemy_Submarine>();
		my_missile_vector = new Vector<Automatic_Missile>();
		enemy_missile_vector = new Vector<Automatic_Enemy_Missile>();

		Indicator_Paint = new Paint();
		Indicator_Paint.setAlpha(255);

		matrix = new Matrix();
		vibe = (Vibrator) my_app.getAppContext().getSystemService(
				Context.VIBRATOR_SERVICE);

		SharedPreferences getPrefs = PreferenceManager
				.getDefaultSharedPreferences(my_app.getAppContext());
		playsound = getPrefs.getBoolean("MusicKey", true);

		soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
		soundMyShipBombed = soundPool.load(my_app.getAppContext(),
				R.raw.blastone, 1);
		soundEnemyShipBombed = soundPool.load(my_app.getAppContext(),
				R.raw.blasttwo, 1);

		soundPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {
			public void onLoadComplete(SoundPool soundPool, int sampleId,
					int status) {
				loaded = true;
			}
		});

		// Volume
		/*
		 * AudioManager audioManager = (AudioManager)
		 * my_app.getSystemService(my_app.AUDIO_SERVICE); float actualVolume =
		 * (float) audioManager .getStreamVolume(AudioManager.STREAM_MUSIC);
		 * float maxVolume = (float) audioManager
		 * .getStreamMaxVolume(AudioManager.STREAM_MUSIC); volume = actualVolume
		 * / maxVolume;
		 */

		// using sound here only when collision of ships occur, odrwise sound
		// not needed, it'll be decided by the respective missile
		// soundMyShipBombed = MediaPlayer.create(my_app.getAppContext(),
		// R.raw.blastone);
		// soundEnemyShipBombed = MediaPlayer.create(my_app.getAppContext(),
		// R.raw.blasttwo);
	}

	public void onStop() {
		// soundMyShipBombed.re();
		//
		// soundEnemyShipBombed.release();
	}

	public void onResume() {
		soundPool.autoResume();

		TimerTask sonar_indicator_checker = new TimerTask() {

			public void run() {
				change_sonar_battery();
			}
		};

		sonar_indicator_checker_timer = new Timer(true);
		sonar_indicator_checker_timer.scheduleAtFixedRate(
				sonar_indicator_checker, 0, 1000);
	}

	public void onPause() {
		soundPool.autoPause();
		sonar_indicator_checker_timer.cancel();
	}

	public void change_sonar_battery() {
		// System.out.println("timer at " + time++ + "  Missile Left="
		// + Missile_Left);
		// Every 4 seconds charge the sonar battery
		time++;
		if (time % 4 == 0) {
			if (Sonar_Indicator < 7) {
				Sonar_Indicator++;
			}
		}
		if (time % 15 == 0){
			rotate_circle = 0;
		}
		// Every second add 2 missiles
		// if ur changing max missiles to x, den also change respective alpha
		// indicator 255/x
		if (Missile_Left == 3) {
			Missile_Left++;
		}
		if (Missile_Left < 4) {
			Missile_Left += 2;
		}
	}

	private void initialise_for_sonar_area() {

		trans_surf_paint = new Paint();
		trans_surf_paint.setColor(Color.RED);
		trans_surf_paint.setAlpha(0);

		dashed_circle = new Paint();
		dashed_circle.setColor(Color.RED);
		DashPathEffect dashPath = new DashPathEffect(new float[] { 5, 5 },
				(float) 1.0);
		dashed_circle.setPathEffect(dashPath);
		dashed_circle.setStrokeWidth(3);
		dashed_circle.setStyle(Paint.Style.STROKE);

		transparent_overlay_mutable = Bitmap.createBitmap(width_of_screen,
				height_of_screen, (Config.ARGB_8888));
		// convert to ARGB_8888 format,only den can it be put on canvas in next
		// line..

		c2 = new Canvas();
		c2.setBitmap(transparent_overlay_mutable);

		trans_paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		trans_paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
		trans_paint.setColor(Color.TRANSPARENT);
		trans_paint.setStrokeWidth(3);
		trans_paint.setMaskFilter(new BlurMaskFilter(15, Blur.NORMAL)); // Blur
	}

	public void sonar_visual_bubble_area() {
		// if (clicked_number_of_times++ == 1) {

		// } else {
		if (Sonar_Indicator > 0) {
			change_active_circle_radius = true;
			active_radius_end_not_reached = true;
			Sonar_Indicator -= 3;
		}
		// }
	}

	public void sonar_active_ping_area(float touchx, float touchy, int Action) {
		if (Sonar_Indicator > 0) {
			// if (clicked_number_of_times++ == 1) {
			// if clicked once on radar, do nothing, from second click
			// onwards,
			// take the touch

			// } else {
			active_radius_end_not_reached = false;
			if (Action == Action_Down) {
				sonar_ping_circle_radius = 120;
				Sonar_Indicator -= 2;
			}

			touchedx = touchx;
			touchedy = touchy;
			change_ping_circle_radius = true;
			// }
		}
	}

	void myMissileMotionCalc(float sub_x, float sub_y, float clicked_x2,
			float clicked_y2, Boolean missile_clicked) {
		if (Missile_Left > 0) {
			Automatic_Missile new_missile = new Automatic_Missile();
			new_missile.myMotionCalc(sub_x, sub_y, clicked_x2, clicked_y2,
					missile_clicked);
			Missile_Left--;
			my_missile_vector.add(new_missile);
		}
	}

	private void create_new_enemy() {
		Enemy_Submarine new_enemy = new Enemy_Submarine();

		new_enemy.x = width_of_screen + 10;
		new_enemy.y = rand.nextInt(height_of_screen);
		new_enemy.speed_of_sub = rand.nextInt(6) + 2;

		enemy_sub_vector.add(new_enemy);
	}

	public void onDraw(Canvas canvas) {
		if (draw_first_half) {
			canvas.drawBitmap(bg_c, null, des, null);

			// generate ENEMY SUBMARINES according to difficulty level
			if (rand.nextInt(100) % (100 / difficulty_level) == 0) {
				create_new_enemy();
			}

			// Calculate wrap FAR background
			far_bg_X = far_bg_X - 1;
			int second_far_bg_X = bg_b.getWidth() - (-far_bg_X);

			// draw FAR BACKGROUND - if we have scrolled all the way, reset to
			// start
			if (second_far_bg_X <= 0) {
				// draw once
				far_bg_X = 0;
				canvas.drawBitmap(bg_b, far_bg_X, src_b_Y, null);
			} else {
				// draw original and wrap
				canvas.drawBitmap(bg_b, far_bg_X, src_b_Y, null);
				canvas.drawBitmap(bg_b, second_far_bg_X, src_b_Y, null);
			}

			boolean next_ship = false;

			// Draw ENEMY SUBMARINES n check if I killed him
			if ((enemy_sub_vector != null || enemy_sub_vector.size() != 0)) {
				for (int i = (enemy_sub_vector.size() - 1); i >= 0; i--) {
					next_ship = false;
					Enemy_Submarine enemy_sub = enemy_sub_vector.elementAt(i);

					if ((my_missile_vector != null || my_missile_vector.size() != 0)) {
						for (int j = (my_missile_vector.size() - 1); j >= 0; j--) {
							Automatic_Missile the_missile = my_missile_vector
									.elementAt(j);

							missile_x = (int) the_missile.x1;// get_missile_x();
							missile_y = (int) the_missile.y1;// get_missile_y();
							if (missile_x >= enemy_sub.x
									&& missile_x <= enemy_sub.x
											+ enemy_sub_bit.getWidth()
									&& missile_y >= enemy_sub.y
									&& missile_y <= enemy_sub.y
											+ enemy_sub_bit.getHeight()) {
								the_missile.missile_not_reached = false;
								the_missile.target_not_hit = false;
								if (the_missile.waiting_to_finish_exploding == false) {
									enemy_sub_vector.removeElementAt(i);
									my_app.set_enemies_killed();
									next_ship = true;
									break;
								}
							}
							if (the_missile.explosion_drawn == true) {
								my_missile_vector.removeElementAt(j);
							}
						}
					}

					if (next_ship)
						continue;

					// Enemy and My Subs Collision detection
					if ((enemy_sub.x >= submarine_x - enemy_sub_bit.getWidth()
							/ 2
							&& enemy_sub.x < submarine_x
									+ enemy_sub_bit.getWidth() / 2
							&& enemy_sub.y >= submarine_y
									- enemy_sub_bit.getHeight() / 2 && enemy_sub.y < submarine_y
							+ enemy_sub_bit.getHeight() / 2)
							|| (enemy_sub.x + enemy_sub_bit.getWidth() >= submarine_x
									- enemy_sub_bit.getWidth() / 2
									&& enemy_sub.x + enemy_sub_bit.getWidth() < submarine_x
											+ enemy_sub_bit.getWidth() / 2
									&& enemy_sub.y + enemy_sub_bit.getHeight() >= submarine_y
											- enemy_sub_bit.getHeight() / 2 && enemy_sub.y
									+ enemy_sub_bit.getHeight() < submarine_y
									+ enemy_sub_bit.getHeight() / 2)) {
						// reduce ship health here
						// System.out.println("I HAVE BEEN KILLED");
						Submarine.blasted = true;

						// kill enemy which touched you
						enemy_sub_vector.removeElementAt(i);
						my_app.set_enemies_killed();
						next_ship = true;

						if (playsound) {
							if (loaded) {
								soundPool.play(soundMyShipBombed, 0.9f, 0.9f,
										1, 0, 1f);
								soundPool.play(soundEnemyShipBombed, 0.9f,
										0.9f, 1, 0, 1f);
							}
						}
						// soundMyShipBombed.start();

						if (playsound)
							// soundEnemyShipBombed.start();

							Sub_Damage++;
						vibe.vibrate(100);
					}

					if (next_ship)
						continue;

					// check if enemies escaped out of the screen
					if (enemy_sub.x < 0 - enemy_sub_bit.getWidth()) {
						enemy_sub_vector.removeElementAt(i);
						my_app.set_enemies_escaped();
					} else {
						enemy_sub.x -= enemy_sub.speed_of_sub;
						canvas.drawBitmap(enemy_sub_bit, enemy_sub.x,
								enemy_sub.y, null);
					}
					// check if ship is in active area
					if (change_active_circle_radius) {
						double dx = enemy_sub.x - submarine_x;
						double dy = enemy_sub.y - submarine_y;
						if (Math.sqrt((dx * dx) + (dy * dy)) <= sonar_active_circle_radius) {
							if (!enemy_sub.launched_a_missile) {
								// Enemy Shoots You according to difficulty
								// level
								if (rand.nextInt(100)
										% (100 / difficulty_level) == 0) {
									Automatic_Enemy_Missile new_missile = new Automatic_Enemy_Missile();
									new_missile.myMotionCalc(enemy_sub.x + 10,
											enemy_sub.y + 19, submarine_x,
											submarine_y, true);
									enemy_missile_vector.add(new_missile);
									enemy_sub.launched_a_missile = true;
								}
							}
							enemy_sub.launched_a_missile = false;
							enemy_sub.in_radar_area = true;
							enemy_sub.visible_in_active = true;
						} else {
							enemy_sub.visible_in_active = false;
							enemy_sub.in_radar_area = false;
						}
					}

					// check if ship is in ping area
					if (change_ping_circle_radius) {
						double dx = (enemy_sub.x + enemy_sub_bit.getWidth() / 2)
								- touchedx;
						double dy = (enemy_sub.y + enemy_sub_bit.getHeight() / 2)
								- touchedy;
						if (Math.sqrt((dx * dx) + (dy * dy)) <= sonar_ping_circle_radius) {
							if (!enemy_sub.launched_a_missile) {
								// Enemy Shoots You according to difficulty
								// level
								if (rand.nextInt(100)
										% (100 / difficulty_level) == 0) {
									Automatic_Enemy_Missile new_missile = new Automatic_Enemy_Missile();
									new_missile.myMotionCalc(enemy_sub.x + 10,
											enemy_sub.y + 19, submarine_x,
											submarine_y, true);
									enemy_missile_vector.add(new_missile);
									enemy_sub.launched_a_missile = true;
								}
							}
							enemy_sub.launched_a_missile = false;
							enemy_sub.in_radar_area = true;
						} else {
							if (!enemy_sub.visible_in_active)
								enemy_sub.in_radar_area = false;
						}
					}

					// Enemy Shoots You according to difficulty level(but less
					// in normal mode)
					if (rand.nextInt(200) % (200 / difficulty_level) == 0) {
						Automatic_Enemy_Missile new_missile = new Automatic_Enemy_Missile();
						new_missile.myMotionCalc(enemy_sub.x + 10,
								enemy_sub.y + 19, submarine_x, submarine_y,
								true);
						enemy_missile_vector.add(new_missile);
						enemy_sub.launched_a_missile = true;
					}
				}
			}
			draw_first_half = !draw_first_half;
		} else {

			// Calculate Ping Radius
			if (change_ping_circle_radius) {
				if (sonar_ping_circle_radius == 0) {
					sonar_ping_circle_radius = 0;
				} else {
					sonar_ping_circle_radius--;
				}
			}

			// Calculate Visual Bubble radius
			if (change_active_circle_radius) {
				if (sonar_active_circle_radius < 100) {
					sonar_active_circle_radius = 100;
					change_active_circle_radius = false;
				} else {
					if (active_radius_end_not_reached
							&& sonar_active_circle_radius < 800) {
						sonar_active_circle_radius += 40;
					} else {
						active_radius_end_not_reached = false;
						sonar_active_circle_radius -= 5;
					}
				}
			}

			c2.drawBitmap(transparent_overlay, null, des, null);
			c2.drawCircle(submarine_x, submarine_y, sonar_active_circle_radius,
					trans_paint);
			c2.drawCircle(touchedx, touchedy, sonar_ping_circle_radius,
					trans_paint);

			if (trans_alpha < 0) {
				trans_alpha_increment = (float) 0.20;
			}
			if (trans_alpha > 255) {
				// after reaching complete darkness, increment level
				my_app.set_level();
				trans_alpha_increment = (float) -0.20;
				System.out.println("LEVEL " + my_app.get_level() + "Time= " + time);
			}

			trans_alpha += trans_alpha_increment;

			trans_surf_paint.setAlpha((int) trans_alpha);
			canvas.drawBitmap(transparent_overlay_mutable, null, des,
					trans_surf_paint);

			// draw my missiles
			if ((my_missile_vector != null || my_missile_vector.size() != 0)) {
				for (int i = (my_missile_vector.size() - 1); i >= 0; i--) {
					Automatic_Missile the_missile = my_missile_vector
							.elementAt(i);
					the_missile.onDraw(canvas);
				}
			}

			// draw enemy missiles and check MySub - Enemy Missile Collision
			if ((enemy_missile_vector != null || enemy_missile_vector.size() != 0)) {
				for (int i = (enemy_missile_vector.size() - 1); i >= 0; i--) {
					Automatic_Enemy_Missile ene_missile = enemy_missile_vector
							.elementAt(i);

					missile_x = (int) ene_missile.x1;
					missile_y = (int) ene_missile.y1;
					if (missile_x >= submarine_x - enemy_sub_bit.getWidth() / 2
							&& missile_x < submarine_x
									+ enemy_sub_bit.getWidth() / 2
							&& missile_y >= submarine_y
									- enemy_sub_bit.getHeight() / 2
							&& missile_y < submarine_y
									+ enemy_sub_bit.getHeight() / 2) {
						ene_missile.missile_not_reached = false;
						ene_missile.target_not_hit = false;
						if (ene_missile.waiting_to_finish_exploding == false) {
							System.out.println("I HAVE BEEN KILLED");
							Submarine.blasted = true;
							Sub_Damage++;
							vibe.vibrate(100);
						}
					}
					ene_missile.onDraw(canvas);
					if (ene_missile.explosion_drawn == true) {
						enemy_missile_vector.removeElementAt(i);
					}
				}
			}

			// Calculate wrap near background
			closer_bg_X = closer_bg_X - 3;
			int second_near_bg_X = bg_a.getWidth() - (-closer_bg_X);
			if (second_near_bg_X <= 0) {
				closer_bg_X = 0;
				canvas.drawBitmap(bg_a, closer_bg_X, src_a_Y, null);
			} else {
				canvas.drawBitmap(bg_a, closer_bg_X, src_a_Y, null);
				canvas.drawBitmap(bg_a, second_near_bg_X, src_a_Y, null);
			}

			// draw ring around enemy ship
			if ((enemy_sub_vector != null || enemy_sub_vector.size() != 0)) {
				for (int i = (enemy_sub_vector.size() - 1); i >= 0; i--) {
					Enemy_Submarine enemy_sub = enemy_sub_vector.elementAt(i);
					if (enemy_sub.in_radar_area) {
						canvas.rotate(-rotate_circle,enemy_sub.x + enemy_sub_bit.getWidth() / 2,
								enemy_sub.y + enemy_sub_bit.getHeight() / 2);
						canvas.drawCircle(
								enemy_sub.x + enemy_sub_bit.getWidth() / 2,
								enemy_sub.y + enemy_sub_bit.getHeight() / 2,
								enemy_sub_bit.getWidth() / 2, dashed_circle);
						canvas.rotate(rotate_circle++,enemy_sub.x + enemy_sub_bit.getWidth() / 2,
								enemy_sub.y + enemy_sub_bit.getHeight() / 2);
					}
				}
			}

			// frames per second
			// executed_times++;

			if (!My_Application.get_end_of_game()) {
				// draw Submarines Health Indicator
				if (Sub_Damage >= 11) {
					my_app.set_end_of_game(true);
					Indicator_Paint.setAlpha(0);
					my_app.set_time(time);
				} else
					Indicator_Paint.setAlpha(255 - (25 * Sub_Damage));
				matrix.reset();
				matrix.postTranslate(width_of_screen / 4, height_of_screen
						- my_app.get_Width_of_Radar_icon());
				canvas.drawBitmap(submarine_health, matrix, Indicator_Paint);

				// draw Sonar Indicator
				if (Sonar_Indicator < 0)
					Indicator_Paint.setAlpha(0);
				else
					Indicator_Paint.setAlpha(36 * Sonar_Indicator);
				matrix.reset();
				matrix.postTranslate(width_of_screen / 2, height_of_screen
						- my_app.get_Width_of_Radar_icon());
				canvas.drawBitmap(sonar_indicator_bit, matrix, Indicator_Paint);

				// draw Missile Indicator
				if (Missile_Left < 0)
					Indicator_Paint.setAlpha(0);
				else
					Indicator_Paint.setAlpha(63 * Missile_Left);
				matrix.reset();
				matrix.postTranslate(
						(width_of_screen - missile_indicator.getWidth() - 10),
						(height_of_screen - missile_indicator.getHeight() - 10));

				canvas.drawBitmap(missile_indicator, matrix, Indicator_Paint);
			} // else
				// System.out.println("Executed times= " + executed_times);
			draw_first_half = !draw_first_half;
		}
	}
}