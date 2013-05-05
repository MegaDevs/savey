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

public class GetVendingMachinesServlet extends HttpServlet {

	private static final long serialVersionUID = 508581952289517945L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	
		String callbackMethod = req.getParameter(SaveyUtils.CALLBACK);
		
		SQLiteConnection db = null;
		try {
			String dbFile = SaveyUtils.getDatabasePath();

			db = new SQLiteConnection(new File(dbFile));
			db.open(true);

//			StringWriter sWriter = new StringWriter();
			
//			JsonWriter writer = new JsonWriter(new BufferedWriter(sWriter));
//			writer.beginArray();

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
				
//				writer.beginObject();
//				
//				writer.name("id");
//				writer.value(id);
//				writer.name("latitude");
//				writer.value(latitude);
//				writer.name("longitude");
//				writer.value(longitude);
//				writer.name("encoded_image");
//				writer.value(encodedImage);
//				
//				writer.endObject();
			}
			
//			writer.endArray();
//			writer.flush();
//			writer.close();
			
//			System.out.println("StringWriter returned: " + sWriter.toString());
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
