package coms.obir;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class Helpers {
	public static int parseInt(String s) {
		try {
			return new Integer(s);
		} catch (NumberFormatException e) {
			return -1;
		}
	}

	public static String array2string(double[] v) {
		String s = "";
		for (int i = 0; i < v.length; ++i) {
			s += String.format("%5.3f, ", v[i]);
		}
		return s;
	}

	public static Connection openDbConnection() throws SQLException {
		Connection dbConn = null;
		Properties info = new Properties();
		info.put("user", "cuongnx");
		info.put("password", "admin123456");
			dbConn = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/obir", info);

		return dbConn;

	}

	public static void closeDbConnection(Connection dbConn) throws SQLException {
		if (dbConn != null)
				dbConn.close();
	}
}
