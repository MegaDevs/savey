package com.megadevs.savey.apis;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.zxing.WriterException;

public class SaveyTestServlet extends HttpServlet {

	private static final long serialVersionUID = 5706727396040369493L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	
		resp.getWriter().println("Welcome to Savey!");
		
		try {
			resp.getWriter().println(SaveyUtils.generateQRCode(SaveyUtils.prepareInputForQRCode(1)));
			resp.getWriter().println(SaveyUtils.generateQRCode(SaveyUtils.prepareInputForQRCode(2)));
			resp.getWriter().println(SaveyUtils.generateQRCode(SaveyUtils.prepareInputForQRCode(3)));
		} catch (WriterException e) {
			e.printStackTrace();
		}
		
		resp.getWriter().flush();
		resp.getWriter().close();
		
	}
	
}
