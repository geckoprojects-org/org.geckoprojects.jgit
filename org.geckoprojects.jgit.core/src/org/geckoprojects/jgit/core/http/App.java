package org.geckoprojects.jgit.core.http;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

import org.geckoprojects.jgit.core.Constants;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

@Component(immediate = true)
public class App {

	@Reference
	ConfigurationAdmin configAdmin;

	@Activate
	public void activate() throws IOException {

		Configuration c = configAdmin.getFactoryConfiguration(Constants.PID_REPOSITORY_INMEMORY ,"foo", "?");
		c.update(new Hashtable<>(Map.of(Constants.REPOSITORY_NAME, "foo")));
		
	}
}
