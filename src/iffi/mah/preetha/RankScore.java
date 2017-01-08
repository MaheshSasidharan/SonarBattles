package iffi.mah.preetha;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class RankScore {

	public static final String KEY_RANK = "_id";
	public static final String KEY_NAME = "persons_name";
	public static final String KEY_SCORE = "persons_score";

	private static final String DATABASE_NAME = "Scoredb";
	private static final String DATABASE_TABLE = "scoreTables";
	private static final String DATABASE_TABLE2 = "rankTable";
	private static final int DATABASE_VERSION = 2;

	private DbHelper ourHelper;
	private final Context ourContext;
	private SQLiteDatabase ourDatabase;

	private static class DbHelper extends SQLiteOpenHelper {

		public DbHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			db.execSQL("CREATE TABLE " + DATABASE_TABLE + " (" + KEY_NAME
					+ " TEXT NOT NULL, " + KEY_SCORE + " INTEGER NOT NULL);");
			// db.execSQL("CREATE TABLE " + DATABASE_TABLE2 + " (" +
			// KEY_RANK + " INTEGER);");

		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub

			db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
			// db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE2);
			onCreate(db);
		}

	}

	public RankScore(Context c) {
		ourContext = c;
	}

	public RankScore open() {
		ourHelper = new DbHelper(ourContext);
		ourDatabase = ourHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		ourHelper.close();
	}

	public void createEntry(String name, int curScore) {
		// TODO Auto-generated method stub
		ContentValues cv = new ContentValues();
		String[] columns = new String[] { KEY_NAME, KEY_SCORE };
		Cursor c = ourDatabase.query(DATABASE_TABLE, columns, null, null, null,
				null, KEY_SCORE + " DESC;");

		int iScore = c.getColumnIndex(KEY_SCORE);

		cv.put(KEY_NAME, name);
		cv.put(KEY_SCORE, curScore);

		if (c.getCount() == 0)
			ourDatabase.insert(DATABASE_TABLE, null, cv);
		else {

			for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
				if (curScore >= c.getInt(iScore)) {
					ourDatabase.insert(DATABASE_TABLE, null, cv);
					c = ourDatabase.query(DATABASE_TABLE, columns, null, null,
							null, null, KEY_SCORE + " DESC;");
					if (c.getCount() > 5) {

						c.moveToLast();
						ourDatabase.execSQL("DELETE FROM " + DATABASE_TABLE
								+ " WHERE " + KEY_SCORE + "="
								+ c.getInt(iScore) + ";");
					}
					break;
				} else if ((curScore < c.getInt(iScore)) && (c.getCount() <= 4)) {
					c.moveToNext();
					ourDatabase.insert(DATABASE_TABLE, null, cv);
					break;
				}
			}
		}
	}

	public String [] getNameData() {

		String[] columns = new String[] { KEY_NAME, KEY_SCORE };
		Cursor c = ourDatabase.query(DATABASE_TABLE, columns, null, null, null,
				null, KEY_SCORE + " DESC;");

		String result [] = new String[5];
		int i = 0;

		int iName = c.getColumnIndex(KEY_NAME);
		for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
			result[i++] =  c.getString(iName);
		}
		return result;
	}

	public String [] getScoreData() {
		String[] columns = new String[] { KEY_SCORE };
		Cursor c = ourDatabase.query(DATABASE_TABLE, columns, null, null, null,
				null, KEY_SCORE + " DESC;");

		String result [] = new String[5];
		int i = 0;

		int iScore = c.getColumnIndex(KEY_SCORE);
		for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
			result[i++] =  c.getString(iScore);
		}
		return result;
	}

}