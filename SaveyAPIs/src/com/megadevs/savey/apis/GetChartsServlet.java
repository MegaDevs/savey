package com.megadevs.savey.apis;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.almworks.sqlite4java.SQLiteConnection;
import com.almworks.sqlite4java.SQLiteStatement;
import com.google.gson.stream.JsonReader;
import com.megadevs.savey.apis.SaveyUtils.IDS;

public class GetChartsServlet extends HttpServlet {

	private static final long serialVersionUID = 8344187986442463022L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	
		String callbackMethod = req.getParameter(SaveyUtils.CALLBACK);
		
		String request = req.getParameter("value");

		JsonReader jsonReader = new JsonReader(new StringReader(request));
		jsonReader.beginObject();

		System.out.println("Request: " + request);
		
		String taskID = "";
		while (jsonReader.hasNext()) {
			String name = jsonReader.nextName();
			if (name.equals(IDS.TASK_ID.getID()))
				taskID = jsonReader.nextString();
			else
				jsonReader.nextString();
		}
		
		SQLiteConnection db = null;
		try {
			String dbFile = SaveyUtils.getDatabasePath();

			db = new SQLiteConnection(new File(dbFile));
			db.open(true);

			ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			
			SQLiteStatement query = db.prepare("SELECT title,content FROM tasks WHERE id = " + taskID);
			
			String title = "";
			String content = "";
			if (query.step()) {
				title = query.columnString(0);
				content = query.columnString(1);
			}
				
			query.reset();
			query = db.prepare("SELECT result FROM users_to_tasks WHERE id_task = " + taskID);
			
			String[] split = content.split(Pattern.quote("|||"));
			System.out.println("Split size: " + split.length);
			System.out.println("Split[0] : " + split[0]);
			System.out.println("Split[1] : " + split[1]);
			
			int[] counter = new int[split.length];
			
			while (query.step()) {
				String result = query.columnString(0);
				System.out.println("Result: " + result);
				for (int i=0; i<split.length; i++) {
					if (split[i].trim().equals(result)) {
						System.out.println("Adding " + i + " to counter");
						counter[i]++;
					}
				}
			}
			
			for (int i : counter)
				System.out.println("entry value : " + i);
			
			ArrayList<Map<String, Object>> array = new ArrayList<Map<String, Object>>();

			HashMap<String, Object> histogram = new HashMap<String, Object>();
			histogram.put("title", title);
			histogram.put("x", split);
			histogram.put("y", counter);
			array.add(histogram);
			
			HashMap<String, Object> pieOne = new HashMap<String, Object>();
			pieOne.put("key", split[0]);
			pieOne.put("categories", new String[] {"Student", "Business", "Travelers"});
			pieOne.put("values", new int[] {11, 22, 33});
			
			array.add(pieOne);
			
			HashMap<String, Object> pieTwo = new HashMap<String, Object>();
			pieTwo.put("key", split[1]);
			pieTwo.put("categories", new String[] {"Student", "Business", "Travelers"});
			pieTwo.put("values", new int[] {11, 22, 33});

			array.add(pieTwo);
			
			System.out.println("Prepared format is: " + SaveyUtils.prepareResponse(list, callbackMethod));
			
			resp.setContentType("application/javascript");
			resp.getWriter().write(SaveyUtils.prepareResponse(array, callbackMethod));
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
