package dbConnect;

import java.sql.Connection;
import java.sql.DriverManager;

public class MySqlConnection {
	 public static Connection getCon()
	    {
	       Connection con = null;
	        try {
	            Class.forName("com.mysql.jdbc.Driver");
	            con=DriverManager.getConnection("jdbc:mysql://localhost:3306/demoProject", "root", "password");
	            
	        } catch (Exception ex) {
	            System.out.println("Exception:"+ex);
	        }

	       return con;

	    }
}