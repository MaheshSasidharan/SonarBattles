package iffi.mah.preetha;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;

public class Missile_button {

	My_Application my_app;

	Bitmap missile_icon;
	int width_of_screen, height_of_screen;
	Matrix transform;
	Boolean game_over = false, missile_icon_clicked = false,
			missile_mode = false;
	float touchedx, touchedy = 0;

	public static int in_radar_mode_clicked_once = 1;

	public Missile_button() {
		my_app = new My_Application();
		missile_icon = BitmapFactory.decodeResource(my_app.getAppContext()
				.getResources(), R.drawable.missile_icon_inactive);

		width_of_screen = My_Application.get_width_of_screen();
		height_of_screen = My_Application.get_height_of_screen();
		transform = new Matrix();
	}

	public void gettouchxy(float touchx, float touchy) {
		touchedx = touchx;
		touchedy = touchy;
		//checkifmissileiconisclicked();
	}
	
	public void onStop() {
		// nothing to do
	}

	/*public void checkifmissileiconisclicked() {
		if (touchedx >= (width_of_screen - missile_icon.getWidth() - 10)
				&& touchedx < (width_of_screen - 10)
				&& touchedy >= (height_of_screen - missile_icon.getHeight() - 10)
				&& touchedy < (height_of_screen - 10)) {
			if (!(missile_icon.getPixel(
					((int) touchedx - (width_of_screen
							- missile_icon.getWidth() - 10)),
					((int) touchedy - (height_of_screen
							- missile_icon.getHeight() - 10))) == Color.TRANSPARENT)) {

				missile_mode = !missile_mode;
				missile_icon_clicked = true;

				if (missile_mode) {
					missile_icon = BitmapFactory.decodeResource(my_app
							.getAppContext().getResources(),
							R.drawable.missile_icon_active);
				} else {
					missile_icon = BitmapFactory.decodeResource(my_app
							.getAppContext().getResources(),
							R.drawable.missile_icon_inactive);
				}
				Radar.in_missile_mode_clicked_once = 1;
			}

		}
	}

	public void change_missile_mode() {
		if (in_radar_mode_clicked_once++ == 1) {
			//missile_mode = false;
			missile_icon = BitmapFactory.decodeResource(my_app.getAppContext()
					.getResources(), R.drawable.missile_icon_inactive);
		}
	}*/

	public void onDraw(Canvas canvas) {

		if (!My_Application.get_end_of_game()) {
			if (!game_over) {
				if (missile_icon_clicked) {
					transform.reset();
					transform.postScale(0.95f, 0.95f);
					transform.postTranslate(
							(width_of_screen - missile_icon.getWidth() - 10),
							(height_of_screen - missile_icon.getHeight() - 10));

					canvas.drawBitmap(missile_icon, transform, null);
					missile_icon_clicked = false;
				} else {
					transform.setTranslate(
							(width_of_screen - missile_icon.getWidth() - 10),
							(height_of_screen - missile_icon.getHeight() - 10));
					canvas.drawBitmap(missile_icon, transform, null);
				}
			}
		}
	}
}
