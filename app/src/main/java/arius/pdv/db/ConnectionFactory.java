package arius.pdv.db;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.SQLException;

import arius.pdv.core.AriusConnection;
import arius.pdv.core.DbType;
import arius.pdv.core.Metadata;

public final class ConnectionFactory extends SQLiteOpenHelper {

	private static final String Nome_Banco = "pdv.db";

	private static Context context;
	private static ConnectionFactory connectionFactory;

	public static final DbType DB_TYPE = DbType.SQLITE;

	protected static SQLiteDatabase db;

	public ConnectionFactory(){
		super(context,Nome_Banco,null,Metadata.ULTIMA_VERSAO_METADATA);
		db = getWritableDatabase();
	}

	public static AriusConnection createConnection() throws SQLException {
		try {
			Application app = getApplicationUsingReflection();
			context = app.getApplicationContext();
		}catch (Exception e) {
			e.printStackTrace();
		}
		if (connectionFactory == null)
			connectionFactory = new ConnectionFactory();

		return new AndroidConnection(db);
	}

	private void executeMetadata(){
		try {
			if (db.getVersion() != Metadata.ULTIMA_VERSAO_METADATA) {
				for (int i = 0; Metadata.metadata_execute().length > i; i++)
					db.execSQL(Metadata.metadata_execute()[i]);
			}
		}catch (android.database.SQLException e){
			e.printStackTrace();
		}

	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		this.db = db;
		executeMetadata();
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		this.db = db;
		executeMetadata();
	}

	public static Application getApplicationUsingReflection() throws Exception {
		return (Application) Class.forName("android.app.AppGlobals")
				.getMethod("getInitialApplication").invoke(null, (Object[]) null);
	}
}
