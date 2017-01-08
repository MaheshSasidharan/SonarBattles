package iffi.mah.preetha;

import android.app.Application;
import android.content.Context;

public class My_Application extends Application {

	private static Context context;
	private static int width_of_screen, height_of_screen;
	private static Boolean game_over;
	private static Boolean game_sound = true;
	private static int Player_Score;
	private static int Player_Sonar_Battery_Left;
	private static int Player_Missiles_Left;
	private static int radar_icon_width, explosion_width;
	private static int time = 0;
	private static int number_of_enemies_killed = 0;
	private static int number_of_enemies_escaped = 0;
	private static int number_of_levels = 0;
	
	public void onCreate() {
		super.onCreate();
		My_Application.context = getApplicationContext();
	}
	
	public void reset_values(){
		number_of_enemies_killed = 0;
		number_of_enemies_escaped = 0;
		number_of_levels = 0;
	}
	
	public void set_game_sound(Boolean sound){
		game_sound = sound;
	}
	
	public static Boolean get_game_sound(){
		return game_sound;
	}
	
	public void set_level(){
		My_Application.number_of_levels++;;
	}
	
	public int get_level(){
		return number_of_levels;
	}
	
	public void set_enemies_escaped(){
		My_Application.number_of_enemies_escaped++;;
	}
	
	public int get_enemies_escaped(){
		return number_of_enemies_escaped;
	}
	
	public void set_enemies_killed(){
		My_Application.number_of_enemies_killed++;;
	}
	
	public int get_enemies_killed(){
		return number_of_enemies_killed;
	}
	
	public void set_time(int time){
		My_Application.time = time;
	}
	
	public int get_time(){
		return time;
	}
	
	public int get_Width_of_Radar_icon(){
		return radar_icon_width;
	}
	
	public void set_Width_of_Explosion_icon(int width){
		explosion_width = width;
	}
	
	public int get_Width_of_Explosion_icon(){
		return explosion_width;
	}
	
	public void set_Width_of_Radar_icon(int width){
		radar_icon_width = width;
	}

	public void Set_Player_Score(int score){
		Player_Score = score;
	}
	
	public int Get_Player_Score(){
		return Player_Score;
	}
	
	public void Set_Player_Sonar_Battery(int battery){
		Player_Sonar_Battery_Left = battery;
	}
	
	public int Get_Player_Sonar_Battery(){
		return Player_Sonar_Battery_Left;
	}
	
	public void Set_Player_Missiles_Left(int missiles_left){
		Player_Missiles_Left = missiles_left;
	}
	
	public int Get_Player_Missiles_Left(){
		return Player_Missiles_Left;
	}
	
	public Context getAppContext() {
		return My_Application.context;
	}
	
	public void set_end_of_game(Boolean eog){
		game_over = eog;
	}
	
	public static Boolean get_end_of_game(){
		return game_over;
	}
	
	public void set_width_of_screen(int wos){
		width_of_screen = wos;
	}
	
	public static int get_width_of_screen(){
		return width_of_screen;
	}
	
	public void set_height_of_screen(int hos){
		height_of_screen = hos;
	}
	
	public static int get_height_of_screen(){
		return height_of_screen;
	}
}