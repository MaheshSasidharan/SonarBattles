package iffi.mah.preetha;

import java.util.Random;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.preference.PreferenceManager;

public class Automatic_Enemy_Missile {

	My_Application my_app;

	MediaPlayer soundBombTargetHit, soundBombTargetNotHit;

	boolean missile_not_reached = false;
	Bitmap missile, random_explosion;
	float x1, y1, x2 = -100, y2 = -100; // missiles initial and final
										// coordinates
	float constant, divisionx, m, angle = 0; // for the line equation
	int row, column;
	static int width_explosion, height_explosion;
	static int width_of_screen, height_of_screen;
	Matrix matrix;
	boolean explosion_drawn = false, waiting_to_finish_exploding = false,
			bombsoundonce = true, target_not_hit = true, playsound;
	Random rand;

	public Automatic_Enemy_Missile() {
		my_app = new My_Application();

		SharedPreferences getPrefs = PreferenceManager
				.getDefaultSharedPreferences(my_app.getAppContext());
		playsound = getPrefs.getBoolean("MusicKey", true);

		soundBombTargetNotHit = MediaPlayer.create(my_app.getAppContext(),
				R.raw.blastone);
		soundBombTargetHit = MediaPlayer.create(my_app.getAppContext(),
				R.raw.bombtwo);

		missile = BitmapFactory.decodeResource(my_app.getAppContext()
				.getResources(), R.drawable.enemy_missile);

		rand = new Random();
		int random_num = rand.nextInt(4) % 4;
		if (random_num == 0)
			random_explosion = BitmapFactory.decodeResource(my_app
					.getAppContext().getResources(), R.drawable.explosion1);
		else if (random_num == 1)
			random_explosion = BitmapFactory.decodeResource(my_app
					.getAppContext().getResources(), R.drawable.explosion2);
		else if (random_num == 2)
			random_explosion = BitmapFactory.decodeResource(my_app
					.getAppContext().getResources(), R.drawable.explosion3);
		else
			random_explosion = BitmapFactory.decodeResource(my_app
					.getAppContext().getResources(), R.drawable.explosion4);

		x1 = -100;
		y1 = -100;
		matrix = new Matrix();

		width_of_screen = My_Application.get_width_of_screen();
		height_of_screen = My_Application.get_height_of_screen();

		// both are width as the images width and height are the same
		width_explosion = height_explosion = my_app
				.get_Width_of_Explosion_icon() / 4;
	}

	public void onDraw(Canvas canvas) {
		if (missile_not_reached) {
			if (x1 < x2) {
				x1 = x1 + divisionx; // increment x by divisionx
				y1 = (x1 * m) + constant;
				if (x1 >= x2) {
					missile_not_reached = false; // to check if ball reached its
													// destination
				}
			}
			if (x2 < x1) {
				x1 = x1 - divisionx;
				y1 = (x1 * m) + constant;
				if (x2 >= x1) {
					missile_not_reached = false;
				}
			}
			angle = (float) Math.toDegrees(Math.atan2((y1 - y2), (x1 - x2)));

			matrix.reset();
			matrix.postRotate(angle, (missile.getWidth() / 2),
					(missile.getHeight() / 2)); // rotate it
			matrix.postTranslate(x1 - (missile.getWidth() / 2),
					y1 - (missile.getHeight() / 2)); // move it into x, y
														// position
			canvas.drawBitmap(missile, matrix, null);
			explosion_drawn = false;
		} else {
			if (target_not_hit) {
				if (bombsoundonce) {
					if (playsound)
						try {
							soundBombTargetNotHit.start();
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							System.out.println("MEDIA ERROR rrrrrrrrrrrr automatic_Enemy_missile - targethit");
						}
					bombsoundonce = false;
				}
			} else {
				if (bombsoundonce) {
					if (playsound)
						try {
							soundBombTargetHit.start();
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							System.out.println("MEDIA ERROR rrrrrrrrrrrr automatic_Enemy_missile - targethit");
						}
					bombsoundonce = false;
				}
			}
		}
		if (!missile_not_reached && explosion_drawn == false) {
			waiting_to_finish_exploding = true;
			int srcX = column * width_explosion;
			int srcY = row * height_explosion;
			Rect src = new Rect(srcX, srcY, srcX + width_explosion, srcY
					+ height_explosion);
			Rect dst = new Rect((int) (x1 - width_explosion / 2),
					(int) (y1 - height_explosion / 2),
					(int) (x1 + width_explosion / 2),
					(int) (y1 + height_explosion / 2));
			canvas.drawBitmap(random_explosion, src, dst, null);

			column = ++column % 4;
			if (column == 0) {
				row++;
			}
			if (row == 4) {
				explosion_drawn = true;
				row = 0;
			}
		}
	}

	void myMotionCalc(float sub_x, float sub_y, float clicked_x2,
			float clicked_y2, Boolean missile_clicked) {
		if (!missile_not_reached) {
			missile_not_reached = missile_clicked;
			x1 = sub_x;
			y1 = sub_y;
			x2 = clicked_x2;
			y2 = clicked_y2;
			if ((y2 - y1) != 0)
				m = (y2 - y1) / (x2 - x1);
			else
				m = y2 - y1;

			constant = y1 - (m * x1); // equation of line is y = m + constant;
										// so,
										// after finding slope m, just find y,
										// for
										// every value of x...

			divisionx = x2 - x1;
			divisionx = divisionx / 20;
			divisionx = Math.abs(divisionx);
			divisionx = divisionx + (1 / divisionx); // just to make missile
														// movement more
														// realistic
		}
	}
}