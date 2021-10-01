package com.wtc.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;

/**
 * Servlet implementation class LoginServlet
 */
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String USER_NAME = "BlueSageDev";
	private static final String PASSWORD = "Blu3D3v13";
	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public LoginServlet() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String userName = request.getParameter("username");
		String password = request.getParameter("password");
		JSONObject responseObj = new JSONObject();

		if (userName.equalsIgnoreCase(LoginServlet.USER_NAME) && password.equalsIgnoreCase(LoginServlet.PASSWORD)){
			response.setHeader("success", "true");
			responseObj.put("success", "true");
			responseObj.put("message", "Authentication is Successful..!!");
			response.getWriter().print(responseObj.toString());
			//				response.setHeader("message", "Authentication is Successful..!!");
			//				response.getWriter().print("Authentication is Successful..!!");

		} else {
			responseObj.put("success", "false");
			responseObj.put("message", "Authentication Failed,Please enter proper User Name,Password and try again");
			response.setHeader("success", "false");
			//				response.setHeader("message", "Authentication Failed,Please enter proper User Name,Password and try again");
			response.getWriter().print(responseObj.toString());

		}


	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);

	}

}
