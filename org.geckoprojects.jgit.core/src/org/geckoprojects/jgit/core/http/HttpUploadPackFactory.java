package org.geckoprojects.jgit.core.http;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.jgit.transport.resolver.UploadPackFactory;

public interface HttpUploadPackFactory extends UploadPackFactory<HttpServletRequest>{


}
