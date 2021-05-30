package org.geckoprojects.jgit.core.http;

import org.osgi.service.http.context.ServletContextHelper;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;
import org.osgi.service.component.annotations.Component;


@Component(service = ServletContextHelper.class, property = {
		HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME + "=" + AppServletContext.NAME,
		HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_PATH + "=/training/git" })
public class AppServletContext extends ServletContextHelper {
	public final static String NAME = "GitTraining";

	// TODO: use config
}