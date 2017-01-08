package iffi.mah.preetha;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.MediaPlayer;
import android.preference.PreferenceManager;

public class Radar {

	My_Application my_app;

	MediaPlayer soundRadar;

	Bitmap radar_bit;
	int width_of_screen, height_of_screen;
	Matrix transform;
	Boolean game_over = false, radar_clicked = false, radar_mode = false,
			playsound;
	int angle = 10;
	float touchedx, touchedy = 0;
	public static int in_missile_mode_clicked_once = 1;

	public Radar() {
		my_app = new My_Application();

		radar_bit = BitmapFactory.decodeResource(my_app.getAppContext()
				.getResources(), R.drawable.radar_off);
		my_app.set_Width_of_Radar_icon(radar_bit.getWidth());
		width_of_screen = My_Application.get_width_of_screen();
		height_of_screen = My_Application.get_height_of_screen();
		transform = new Matrix();

		SharedPreferences getPrefs = PreferenceManager
				.getDefaultSharedPreferences(my_app.getAppContext());
		playsound = getPrefs.getBoolean("MusicKey", true);
		soundRadar = MediaPlayer.create(my_app.getAppContext(),
				R.raw.radarsound);

	}

	public void onStop() {
	
	}

	public void gettouchxy(float touchx, float touchy) {
		touchedx = touchx;
		touchedy = touchy;
		checkifradarisclicked();
	}

	public void checkifradarisclicked() {
		if (touchedx >= 10 && touchedx < (10 + radar_bit.getWidth())
				&& touchedy >= (height_of_screen - radar_bit.getHeight() - 10)
				&& touchedy < (height_of_screen - 10)) {
			if (!(radar_bit
					.getPixel(
							((int) touchedx - 10),
							((int) touchedy - (height_of_screen
									- radar_bit.getHeight() - 10))) == Color.TRANSPARENT)) {

				change_radar_mode();
				radar_clicked = true;
			}
		}
	}

	void change_radar_mode() {
		radar_mode = !radar_mode;
		if (radar_mode) {
			radar_bit = BitmapFactory.decodeResource(my_app.getAppContext()
					.getResources(), R.drawable.radar_on);
		} else {
			radar_bit = BitmapFactory.decodeResource(my_app.getAppContext()
					.getResources(), R.drawable.radar_off);
		}
	}

	public void onDraw(Canvas canvas) {
		if (!My_Application.get_end_of_game()) {
			if (!game_over) {
				angle += 10;
				if (radar_clicked) {
					transform.reset();
					transform.postScale(0.95f, 0.95f);
					transform.postTranslate(10,
							height_of_screen - radar_bit.getHeight() - 10);

					canvas.drawBitmap(radar_bit, transform, null);
					radar_clicked = false;
				} else {
					transform.setTranslate(10,
							height_of_screen - radar_bit.getHeight() - 10);
					transform.preRotate(angle, radar_bit.getWidth() / 2,
							radar_bit.getHeight() / 2);
					canvas.drawBitmap(radar_bit, transform, null);
				}
			}
		}
		if (radar_mode) {
			if (!soundRadar.isPlaying())
				if (playsound)
					try {
						soundRadar.start();
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						System.out.println("MEDIA ERROR rrrrrrrrrrrr - Radar.java - soundRadar");
					}
					
		} else {
			if (soundRadar.isPlaying())
				if (playsound)
					soundRadar.pause();
		}
	}
}
