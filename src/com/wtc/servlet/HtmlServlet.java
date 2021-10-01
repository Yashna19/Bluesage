package com.wtc.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;

import com.wtc.Database.DatabaseConnection;

/**
 * Servlet implementation class HtmlServlet
 */
public class HtmlServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	DatabaseConnection obj = new DatabaseConnection();

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public HtmlServlet() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doProcess(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doProcess(request, response);
	}

	@SuppressWarnings("unchecked")
	protected void doProcess(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		JSONObject json = new JSONObject();
		try {
			json = obj.getErrorLogData();
		} catch (Exception e) {
			json.put("success", false);
			json.put("message", "Somewhere went wrong");
			e.printStackTrace();
		}
		out.print(json);

	}
}
