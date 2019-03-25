package com.yafey.diveinspringboot.web.servlet;

import java.io.IOException;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// 要配置asyncSupported = true , 不然会报错。
@WebServlet(urlPatterns="/my/servlet",asyncSupported=true)
public class MyServlet extends HttpServlet{

	private static final long serialVersionUID = -623201157626202603L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//		resp.getWriter().print("Hello,World");
		
		// 异步方式
		AsyncContext asyncContext = req.startAsync();
		asyncContext.start(()->{
			try {
				resp.getWriter().print("Hello,World");
				
				// 异步方式 需要显式地 触发完成。不然，浏览器会一直等待，直到超时。
				asyncContext.complete();
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}

}
