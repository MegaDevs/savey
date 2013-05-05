package com.megadevs.savey.apis;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.almworks.sqlite4java.SQLiteConnection;
import com.almworks.sqlite4java.SQLiteStatement;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.megadevs.savey.apis.SaveyUtils.IDS;

public class GetTaskServlet extends HttpServlet {

	private static final long serialVersionUID = -5676370248121420944L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	
		SQLiteConnection db = null;
		try {
			String request = req.getParameter("value");

			JsonReader jsonReader = new JsonReader(new StringReader(request));
			jsonReader.beginObject();

			System.out.println("Request: " + request);
			
			String machineID = "";
			String userID = "";
			while (jsonReader.hasNext()) {
				String name = jsonReader.nextName();
				System.out.println("Name: " + name);
				if (name.equals(IDS.MACHINE_ID.getID()))
					machineID = jsonReader.nextString();
				else if (name.equals(IDS.USER_ID.getID()))
					userID = jsonReader.nextString();
				else if (name.equals(IDS.TASK_ID.getID()))
					jsonReader.nextString();
				else
					jsonReader.nextString();
			}

			jsonReader.endObject();
			jsonReader.close();

			db = new SQLiteConnection(new File(SaveyUtils.getDatabasePath()));
			db.open();

//			SQLiteStatement query = db.prepare("SELECT id FROM users_to_tasks WHERE id_user = " + userID);  
//			if (query.step())
//			query.reset();

			SQLiteStatement query = db.prepare("SELECT id_task FROM tasks_to_vendings WHERE tasks_to_vendings.id_vending = " + machineID);
			
			int idTask = -1;
			
			if (query.step())
				idTask = query.columnInt(0);
			
			// idTask should now have a value greater than 0 (an actual ID)

			query.reset();
			query = db.prepare("SELECT id,title,type,content,credit FROM tasks WHERE id = " + idTask);
			
			HashMap<Integer, ArrayList<String>> tasks = new HashMap<Integer, ArrayList<String>>();
			
			while (query.step()) {
				int tID = query.columnInt(0);
				String title = query.columnString(1);
				String type = query.columnString(2);
				String content = query.columnString(3);
				double credit = query.columnDouble(4);
				
				ArrayList<String> values = new ArrayList<String>();
				values.add(title);
				values.add(type);
				values.add(content);
				values.add(String.valueOf(credit));
				
				tasks.put(tID, values);
			}

			int random = (int) (new Random(System.currentTimeMillis()).nextInt() % tasks.size());
			System.out.println("Random value: " + random);
			
			Iterator<Integer> it = tasks.keySet().iterator();
			while (it.hasNext() && random > 0) {
				it.next();
				random--;
			}
			
			Integer randomID = it.next();
			ArrayList<String> valuesOfRandom = tasks.get(randomID);
			
			String content = valuesOfRandom.get(2);
			String[] answers = content.split(Pattern.quote("|||"));
			
			JsonWriter writer = new JsonWriter(resp.getWriter());
			writer.beginObject();
			writer.name(IDS.TASK_ID.getID());
			writer.value(randomID);
			writer.name(IDS.MACHINE_ID.getID());
			writer.value(machineID);
			writer.name("title");
			writer.value(valuesOfRandom.get(0));
			writer.name("type");
			writer.value(valuesOfRandom.get(1));
			writer.name("content");
			writer.beginArray();
			for (String answer : answers)
				writer.value(answer);
			writer.endArray();
			writer.name("credit");
			writer.value(valuesOfRandom.get(3));
			writer.endObject();
			
			query.dispose();

		} catch (Throwable e) {
			e.printStackTrace();
		}
		finally {
			db.dispose();
		}
	}
	
}
