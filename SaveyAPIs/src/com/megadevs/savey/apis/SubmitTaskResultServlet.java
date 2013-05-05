package com.megadevs.savey.apis;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.almworks.sqlite4java.SQLiteConnection;
import com.almworks.sqlite4java.SQLiteStatement;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.megadevs.savey.apis.SaveyUtils.IDS;

public class SubmitTaskResultServlet extends HttpServlet {

	private static final long serialVersionUID = 8543233752176482406L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		SQLiteConnection db = null;
		try {
			String request = req.getParameter("value");

			JsonReader jsonReader = new JsonReader(new StringReader(request));
			jsonReader.beginObject();

			System.out.println(request);
			
			String userID = "";
			String taskID = "";
			String machineID = "";
			String result = "";
			while (jsonReader.hasNext()) {
				String name = jsonReader.nextName();
				if (name.equals(IDS.USER_ID.getID()))
					userID = jsonReader.nextString();
				else if (name.equals(IDS.TASK_ID.getID()))
					taskID = jsonReader.nextString();
				else if (name.equals(IDS.MACHINE_ID.getID()))
					machineID = jsonReader.nextString();
				else if (name.equals(IDS.RESULT.getID()))
					result = jsonReader.nextString();
				else
					jsonReader.nextString();

			}

			jsonReader.endObject();
			jsonReader.close();

			db = new SQLiteConnection(new File(SaveyUtils.getDatabasePath()));
			db.open(true);

			db.exec("INSERT INTO users_to_tasks ( id , result , id_user , id_task , id_machine , created_at ) " +
					"VALUES ( NULL , " +
					"'male'" + " , " +
					userID + " , " +
					taskID + " , " +
					machineID + " , " +
					String.valueOf(System.currentTimeMillis()) +
					" ) ");
			
			SQLiteStatement query = db.prepare("SELECT last_insert_rowid()");
			int lastInsertedID = -1;
			if (query.step())
				lastInsertedID = query.columnInt(0);
			
			String qrCode = SaveyUtils.generateQRCode(SaveyUtils.prepareInputForQRCode(lastInsertedID));
			
			JsonWriter writer = new JsonWriter(resp.getWriter());
			writer.beginObject();
			writer.name("qr_code");
			writer.value(qrCode);
			writer.endObject();
			
		} catch (Throwable e) {
			e.printStackTrace();
		}
		finally {
			db.dispose();
		}
		
	}
	
}
