package com.megadevs.savey.apis;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.almworks.sqlite4java.SQLiteConnection;
import com.almworks.sqlite4java.SQLiteStatement;
import com.google.gson.stream.JsonReader;
import com.megadevs.savey.apis.SaveyUtils.IDS;

public class GetTaskResultServlet extends HttpServlet {

	private static final long serialVersionUID = 508581952289517945L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	
		String callbackMethod = req.getParameter(SaveyUtils.CALLBACK);
		
		String request = req.getParameter("value");

		JsonReader jsonReader = new JsonReader(new StringReader(request));
		jsonReader.beginObject();

		String taskID = "";
		while (jsonReader.hasNext()) {
			String name = jsonReader.nextName();
			if (name.equals(IDS.MACHINE_ID.getID()))
				taskID = jsonReader.nextString();
		}
		
		SQLiteConnection db = null;
		try {
			String dbFile = SaveyUtils.getDatabasePath();

			db = new SQLiteConnection(new File(dbFile));
			db.open(true);

			ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			
			SQLiteStatement query = db.prepare("SELECT * FROM vending_machines");
			while (query.step()) {
				int id = query.columnInt(0);
				double latitude = query.columnDouble(1);
				double longitude = query.columnDouble(2);
				String encodedImage = query.columnString(3);
		
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("id", id);
				map.put("latitude", latitude);
				map.put("longitude", longitude);
				map.put("encoded_image", encodedImage);
				
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
