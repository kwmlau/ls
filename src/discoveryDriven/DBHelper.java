package discoveryDriven;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBHelper {	
    static String driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    static String url = "jdbc:sqlserver://localhost;databaseName=ls;user=sa;password=123";
    static Connection conn = null;
    
    /**
     * 与 数据库 建立连接
     */
    public static void getConnect() {
    	try {
    		Class.forName(driver);
    		conn = DriverManager.getConnection(url); 
    	}
    	catch (ClassNotFoundException e) {
    	// TODO Auto-generated catch block
    		System.err.println("装载 JDBC/ODBC驱动程序失败。" );  
    		e.printStackTrace();
        }
    	catch (SQLException e) {
            // TODO Auto-generated catch block
            System.err.println("无法连接数据库" ); 
            e.printStackTrace();
        }
    }

    /**
     * 查询数据表
     * @param sql sql查询语句
     * @param str 参数数组
     * @return 返回查询得到的数据表
     */
    public static ResultSet getResultSet(String sql, String str[]) {
    	getConnect();
    	ResultSet res = null;
        try {
        	/*PreparedStatement pst = conn.prepareStatement(sql   
                    , ResultSet.TYPE_SCROLL_INSENSITIVE  
                    , ResultSet.CONCUR_UPDATABLE);*/
        	//传入控制结果集可滚动  
            PreparedStatement pst = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);            
            if (str != null) {
                for (int i = 0; i < str.length; i++) {
                	//添加参数
                    pst.setString(i + 1, str[i]);
                }
            }
            res = pst.executeQuery();
        } 
        catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return res;
    }

    /**
     * 执行 1条 增删改 语句
     * @param sql sql 增删修改 语句
     * @param str 参数数组
     * @return 返回受影响的行数
     */    
    public static int ExcuteNonQuery(String sql, String str[]) {
        int res = -1;
        getConnect();
        try {
            PreparedStatement pst = conn.prepareStatement(sql);
            if (str != null) {
                for (int i = 0; i < str.length; i++) {
                    pst.setString(i + 1, str[i]);
                }
            }
            res = pst.executeUpdate();
        } 
        catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return res;
    }
}

