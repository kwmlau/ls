package discoveryDriven;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Iterator;

public class discDriven {
	// ------------------------------------------------------------------------------------------------------
	/*// 查找某一节点的邻节点
	public ArrayList<Long> getAdjacentUser(int uid1){
		ArrayList<Long> resAdjUser = new ArrayList<Long>();// 暂存邻节点
		ResultSet res = DBHelper.getResultSet("SELECT uid2 FROM coauthor WHERE uid1 = " + uid1, null);
		try {
			while(res.next()){
				long uid = res.getLong(1);
				resAdjUser.add(uid);
			}
		} catch (SQLException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		
		return resAdjUser;
	}*/
	
	// 查找某一节点的某层属性值	
	public static String getProperty(long uid, int level){		
		String property = null;
		ResultSet res = DBHelper.getResultSet("SELECT p" +  level + " FROM author2 WHERE uid = " + uid, null);
		try {
			while(res.next()){
				property = res.getString(1);			
			}
		} catch (SQLException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		
		return property;
	}
	
	// 文件读入合作关系，构建Hashtable
	public static Hashtable<Long, ArrayList<Long>> constructHtable(){
		Hashtable<Long, ArrayList<Long>> coauthor = new Hashtable<Long, ArrayList<Long>>();
		
		try {
			FileReader fin = new FileReader("D:/Data/coauthor");
			BufferedReader br = new BufferedReader(fin);
			String currentLine = "";			
			
			while((currentLine = br.readLine()) != null){
				StringTokenizer t = new StringTokenizer(currentLine, "\t");
				long uid1 = Long.parseLong(t.nextToken());
				long uid2 = Long.parseLong(t.nextToken());
				//System.out.println(uid1 + "\t" + uid2);
				ArrayList<Long> tmp = new ArrayList<Long>();			
				if(coauthor.get(uid1) == null){
					tmp.add(uid2);
					coauthor.put(uid1, tmp);
				}
				else {
					coauthor.get(uid1).add(uid2);
				}
			}			
			br.close();
			fin.close();
			System.out.println("从文件读取co-author信息：over~");
		} catch (FileNotFoundException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		
		return coauthor;
	}
	
	// 文件读入author信息，构建Hashtable
	public static Hashtable<Long, ArrayList<String>> constructAuthors(){
		Hashtable<Long, ArrayList<String>> authors = new Hashtable<Long, ArrayList<String>>();
		
		try {
			FileReader fin = new FileReader("D:/Data/author");
			BufferedReader br = new BufferedReader(fin);
			String currentLine = "";			
			
			while((currentLine = br.readLine()) != null){
				StringTokenizer t = new StringTokenizer(currentLine, "\t");
				long uid = Long.parseLong(t.nextToken());
				ArrayList<String> tmp = new ArrayList<String>();
				String p1 = t.nextToken();
				tmp.add(p1);
				String p2 = t.nextToken();
				tmp.add(p2);
				String p3 = t.nextToken();
				tmp.add(p3);
				//System.out.println(uid + "\t" + p1 + "\t" + p2 + "\t" + p3);				
				authors.put(uid, tmp);				
			}
			br.close();
			fin.close();
			System.out.println("从文件读取authors信息：over~");
		} catch (FileNotFoundException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		
		return authors;
	}
	
	// ------------------------------------------------------------------------------------------------------
	public static void discoverydrivenQuery(long uid, int level){
		ArrayList<Long> queCand = new ArrayList<Long>();// 候选节点队列
		ArrayList<Long> uVisited = new ArrayList<Long>();// 已访问过的节点列表
		ArrayList<String> pVisited = new ArrayList<String>();// 已构建的属性层次列表		
		Hashtable<Long, ArrayList<String>> authors = constructAuthors();// 文件读入author信息，构建Hashtable	
		Hashtable<Long, ArrayList<Long>> coauthor = constructHtable();// 文件读入合作关系，构建Hashtable		
		queCand.add(uid);
		
		try {
			PrintWriter out = new PrintWriter("D:\\Data\\author_authority");
			uVisited.add(queCand.get(0));
			while(queCand.size() != 0){
				long queFront = queCand.get(0);// 队首元素
				queCand.remove(0);				
				ArrayList<Long> res = coauthor.get(queFront);
				if(res != null){
					for(int i = 0; i < res.size(); i++){
						long uAdj = res.get(i); // queFront的邻节点
						//queCand.remove(0);
						if(!uVisited.contains(uAdj)){
							uVisited.add(uAdj);						
							String p = authors.get(uAdj).get(level - 1);// 邻节点属性层次
							// 写入结果
							if(queFront == uid){
								if(!pVisited.contains(p)){
									pVisited.add(p);
									System.out.println(queFront + "\t" + p);
									out.println(queFront + "\t" + p);
								}								
							}
							else{
								if(!pVisited.contains(p)){
									pVisited.add(p);
									//String preP = getProperty(queFront, level);
									String preP = authors.get(queFront).get(level - 1);
									System.out.println(preP + "\t" + p);
									out.println(preP + "\t" + p);
								}
							}							
							queCand.add(uAdj);
						}//if
					}//for
				}				
			}
			out.close();
		} catch (FileNotFoundException e1) {
			// TODO 自动生成的 catch 块
			e1.printStackTrace();
		}
	}
	
	// ------------------------------------------------------------------------------------------------------
	public static void db_discoverydrivenQuery(long uid, int level){
		ArrayList<Long> queCand = new ArrayList<Long>();// 候选节点队列
		ArrayList<Long> uVisited = new ArrayList<Long>();// 已访问过的节点列表
		ArrayList<String> pVisited = new ArrayList<String>();// 已构建的属性层次列表		
		queCand.add(uid);
		//int depth = 0;// 记录BFS的深度
		try {
			PrintWriter out = new PrintWriter("D:\\Data\\author_authority");
			uVisited.add(queCand.get(0));
			while(queCand.size() != 0){
				long queFront = queCand.get(0);// 队首元素
				queCand.remove(0);
				ResultSet res = DBHelper.getResultSet("SELECT uid2 FROM coauthor2 WHERE uid1 = " + queFront + " ORDER BY uid2 asc", null);// uid的邻居				
				try {
					while(res.next()){// 循环处理队首元素的邻居节点					
						long uAdj = res.getLong(1);// uid的邻节点						
						//queCand.remove(0);
						if(!uVisited.contains(uAdj)){
							uVisited.add(uAdj);
							String p = getProperty(uAdj, level);// 邻节点属性层次							
							// 写入结果
							if(queFront == uid){
								if(!pVisited.contains(p)){
									pVisited.add(p);
									System.out.println(queFront + "\t" + p);
									out.println(queFront + "\t" + p);
								}								
							}
							else{
								if(!pVisited.contains(p)){
									pVisited.add(p);
									String preP = getProperty(queFront, level);									
									System.out.println(preP + "\t" + p);
									out.println(preP + "\t" + p);
								}
							}							
							queCand.add(uAdj);
						}//if
					}//while
				} catch (SQLException e) {
					// TODO 自动生成的 catch 块
					e.printStackTrace();
				} 
			}
			out.close();
		} catch (FileNotFoundException e1) {
			// TODO 自动生成的 catch 块
			e1.printStackTrace();
		}
	}
		
	
	
	
	
	
	
	
	
	
	
	
}
