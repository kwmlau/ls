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
     * �� ���ݿ� ��������
     */
    public static void getConnect() {
    	try {
    		Class.forName(driver);
    		conn = DriverManager.getConnection(url); 
    	}
    	catch (ClassNotFoundException e) {
    	// TODO Auto-generated catch block
    		System.err.println("װ�� JDBC/ODBC��������ʧ�ܡ�" );  
    		e.printStackTrace();
        }
    	catch (SQLException e) {
            // TODO Auto-generated catch block
            System.err.println("�޷��������ݿ�" ); 
            e.printStackTrace();
        }
    }

    /**
     * ��ѯ���ݱ�
     * @param sql sql��ѯ���
     * @param str ��������
     * @return ���ز�ѯ�õ������ݱ�
     */
    public static ResultSet getResultSet(String sql, String str[]) {
    	getConnect();
    	ResultSet res = null;
        try {
        	/*PreparedStatement pst = conn.prepareStatement(sql   
                    , ResultSet.TYPE_SCROLL_INSENSITIVE  
                    , ResultSet.CONCUR_UPDATABLE);*/
        	//������ƽ�����ɹ���  
            PreparedStatement pst = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);            
            if (str != null) {
                for (int i = 0; i < str.length; i++) {
                	//��Ӳ���
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
     * ִ�� 1�� ��ɾ�� ���
     * @param sql sql ��ɾ�޸� ���
     * @param str ��������
     * @return ������Ӱ�������
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

