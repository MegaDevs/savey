package com.megadevs.savey.apis;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.almworks.sqlite4java.SQLiteConnection;
import com.almworks.sqlite4java.SQLiteStatement;

public class GetTasksListServlet extends HttpServlet {

	private static final long serialVersionUID = 5951128793074514267L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		String callbackMethod = req.getParameter(SaveyUtils.CALLBACK);
		
		SQLiteConnection db = null;
		try {
			String dbFile = SaveyUtils.getDatabasePath();

			db = new SQLiteConnection(new File(dbFile));
			db.open(true);

			ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			
			SQLiteStatement query = db.prepare("SELECT id,title FROM tasks WHERE type = " + "'survey'");
			
			while (query.step()) {
				int id = query.columnInt(0);
				String title = query.columnString(1);
		
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("id", id);
				map.put("title", title);
				
				list.add(map);
			}
			
			System.out.println("Prepared format is: " + SaveyUtils.prepareResponse(list, callbackMethod));
			
			resp.setContentType("application/javascript");
			resp.getWriter().write(SaveyUtils.prepareResponse(list, callbackMethod));
			resp.getWriter().flush();
			resp.getWriter().close();
			
		} catch (Throwable e) {
			e.printStackTrace();
		}
		finally {
			db.dispose();
		}	
	}
}
