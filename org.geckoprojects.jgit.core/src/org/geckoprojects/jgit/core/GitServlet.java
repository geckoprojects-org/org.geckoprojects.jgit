package org.geckoprojects.jgit.core;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;
import org.osgi.service.http.whiteboard.propertytypes.HttpWhiteboardContextSelect;
import org.osgi.service.http.whiteboard.propertytypes.HttpWhiteboardServletName;
import org.osgi.service.http.whiteboard.propertytypes.HttpWhiteboardServletPattern;

@Component(service = Servlet.class)
@HttpWhiteboardServletName(GitServlet.NAME)
@HttpWhiteboardServletPattern("/repo/*")
@HttpWhiteboardContextSelect("("+ HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME + "=" + AppServletContext.NAME + ")")
public class GitServlet extends HttpServlet {
 public static final String NAME="GitServlet";
	private static final long serialVersionUID = -4653182235176237L;


	@Override
	protected void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
	System.out.println(1);
	}

}
