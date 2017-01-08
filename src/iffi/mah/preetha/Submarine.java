package iffi.mah.preetha;

import java.util.Random;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

public class Submarine implements SensorEventListener {
	My_Application my_app;

	Bitmap submarine_bit, explosion;
	float x = -120, y = 250, touchx, touchy, width_of_screen, height_of_screen,
			sensorX, sensorY;
	float y_increment, y_accel = 0, x_increment, x_accel = 0,
			sub_tilt_angle = 0;

	int x_after_eos = 10;
	int random_explosion_position_x = 0, random_explosion_position_y = 0;
	Rect src, dst;

	Boolean ready_to_move = false, show_score_screen = false,
			sonar_active_screen = false;
	Boolean sub_going_down = true, sub_going_front = true,
			still_touched = false, change_explosion_sprite = false,
			get_random_explosion_position = true;

	static Boolean blasted = false;
	Paint sub_paint;

	int row, column;
	static int width_explosion, height_explosion;

	Random rand;
	SensorManager sm;

	String Final_Message = "GAME OVER";
	Matrix matrix;

	public Submarine() {
		my_app = new My_Application();

		submarine_bit = BitmapFactory.decodeResource(my_app.getAppContext()
				.getResources(), R.drawable.submarine);
		explosion = BitmapFactory.decodeResource(my_app.getAppContext()
				.getResources(), R.drawable.explosion1);
		my_app.set_Width_of_Explosion_icon(explosion.getWidth());

		width_of_screen = My_Application.get_width_of_screen();
		height_of_screen = My_Application.get_height_of_screen();

		rand = new Random();
		y = rand.nextInt((int) (2 * (height_of_screen / 3)));

		sub_paint = new Paint(Paint.ANTI_ALIAS_FLAG);

		width_explosion = height_explosion = my_app
				.get_Width_of_Explosion_icon() / 4;

		sensorX = sensorY = 0;

		my_app.getAppContext();
		sm = (SensorManager) my_app.getAppContext().getSystemService(
				Context.SENSOR_SERVICE);

		if (sm.getSensorList(Sensor.TYPE_ACCELEROMETER).size() != 0) {
			Sensor s = sm.getSensorList(Sensor.TYPE_ACCELEROMETER).get(0);
			sm.registerListener(this, s, SensorManager.SENSOR_DELAY_GAME);
		}
	}

	public float get_submarine_x() {
		return (submarine_bit.getWidth() / 2 + x);
	}

	public float get_submarine_y() {
		return (submarine_bit.getHeight() / 2 + y);
	}

	public void onDraw(Canvas canvas) {

		if (My_Application.get_end_of_game()) {
			// x += x_after_eos;
			// x_after_eos += 1;
			x += 2;
			y += 3;
			matrix = new Matrix();
			matrix.postRotate((sub_tilt_angle = sub_tilt_angle + 2),
					(submarine_bit.getWidth() / 2),
					(submarine_bit.getHeight() / 2)); // rotate it
			matrix.postTranslate(x, y); // move it into x, y position
			canvas.drawBitmap(submarine_bit, matrix, sub_paint);

			/*
			 * if (x > width_of_screen) { show_score_screen = true; }
			 */
		} else {

			// Y Movement
			if (!show_score_screen) {
				y += sensorX * 3;
				if (y < 0) {
					y = 0;
				}

				if (y > (height_of_screen - submarine_bit.getHeight())) {
					y = height_of_screen - submarine_bit.getHeight();
				}
			}

			// Subamrine tilt angle
			sub_tilt_angle = sensorX;
			if (sensorX == 0) {
				sub_tilt_angle = sensorY;
			}
			if (!ready_to_move) {
				x += 4;
				if (x >= submarine_bit.getWidth()) {
					ready_to_move = true;
				}
				Matrix matrix = new Matrix();
				matrix.postRotate((2 * sub_tilt_angle),
						(submarine_bit.getWidth() / 2),
						(submarine_bit.getHeight() / 2)); // rotate it
				matrix.postTranslate(x, y); // move it into x, y position
				canvas.drawBitmap(submarine_bit, matrix, sub_paint);
			} else {
				// X Movement
				if (!show_score_screen) {
					x += sensorY * 3;
					if (x < 0) {
						x = 0;
					}
					if (x > (width_of_screen - submarine_bit.getWidth())) {
						x = width_of_screen - submarine_bit.getWidth();
					}
				} else {
					System.out.println("GAME IS OVER");
				}
				Matrix matrix = new Matrix();
				matrix.postRotate((2 * sub_tilt_angle),
						(submarine_bit.getWidth() / 2),
						(submarine_bit.getHeight() / 2)); // rotate it
				matrix.postTranslate(x, y); // move it into x, y position
				canvas.drawBitmap(submarine_bit, matrix, sub_paint);
			}
		}

		if (blasted) {
			int srcX = column * width_explosion;
			int srcY = row * height_explosion;

			src = new Rect(srcX, srcY, srcX + width_explosion, srcY
					+ height_explosion);

			if (get_random_explosion_position == true) {
				random_explosion_position_x = rand.nextInt(submarine_bit
						.getWidth());
				random_explosion_position_y = rand.nextInt(submarine_bit
						.getHeight());
				get_random_explosion_position = false;
			}
			dst = new Rect((int) x + random_explosion_position_x, (int) (y)
					+ random_explosion_position_y, (int) x
					+ random_explosion_position_x + width_explosion / 2,
					(int) (y) + random_explosion_position_y + height_explosion
							/ 2);

			canvas.drawBitmap(explosion, src, dst, null);

			column = ++column % 4;
			if (column == 0) {
				row++;
			}
			if (row == 4) {
				get_random_explosion_position = true;
				blasted = false;
				row = 0;
			}
		}
	}

	public Boolean check_if_sub_has_been_clicked_in_radar_mode(float touchedx,
			float touchedy) {

		if (touchedx >= x && touchedx < (x + submarine_bit.getWidth())
				&& touchedy >= y && touchedy < (y + submarine_bit.getHeight())) {
			if (!(submarine_bit.getPixel((int) (touchedx - x),
					(int) (touchedy - y)) == Color.TRANSPARENT)) {
				sonar_active_screen = true;
				Log.w("SUBMARINE", "NOT TRANSPARENT");
			}
		} else {
			Log.w("SUBMARINE", "TRANSPARENT");
			sonar_active_screen = false;
		}
		return sonar_active_screen;
	}

	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	public void onPause() {
		sm.unregisterListener(this);
	}

	public void onResume() {
		my_app.getAppContext();
		sm = (SensorManager) my_app.getAppContext().getSystemService(
				Context.SENSOR_SERVICE);

		if (sm.getSensorList(Sensor.TYPE_ACCELEROMETER).size() != 0) {
			Sensor s = sm.getSensorList(Sensor.TYPE_ACCELEROMETER).get(0);
			sm.registerListener(this, s, SensorManager.SENSOR_DELAY_GAME);
		}
	}

	public void onSensorChanged(SensorEvent se) {
		/*
		 * try { Thread.sleep(100); } catch (InterruptedException e) {
		 * e.printStackTrace(); }
		 */

		// se.sensor.getMaximumRange()
		// System.out.println("x= " + se.values[0] + "   y= " + se.values[1]);
		sensorX = se.values[0];
		sensorY = se.values[1];

		// sensorX = (sensorX * 2) / se.sensor.getMaximumRange();
		// sensorY = (sensorY * 2) / se.sensor.getMaximumRange();
	}

	public void onStop() {
		sm.unregisterListener(this);
	}
}