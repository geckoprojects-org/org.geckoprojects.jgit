package org.geckoprojects.jgit.core.http;

import java.security.Principal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.RefFilter;
import org.eclipse.jgit.transport.UploadPack;
import org.eclipse.jgit.transport.resolver.ServiceNotAuthorizedException;
import org.eclipse.jgit.transport.resolver.ServiceNotEnabledException;
import org.osgi.service.component.annotations.Component;

@Component(service = HttpUploadPackFactory.class)
public class HttpUploadPackFactoryImpl implements HttpUploadPackFactory {

	/** {@inheritDoc} */
	@Override
	public UploadPack create(HttpServletRequest req, Repository db)
			throws ServiceNotEnabledException, ServiceNotAuthorizedException {

		Principal principal = req.getUserPrincipal();
		String userName=principal==null?"stbischof":principal.getName();
		UploadPack up = new UploadPack(db);

		String header = req.getHeader("Git-Protocol"); //$NON-NLS-1$
		if (header != null) {
			String[] params = header.split(":"); //$NON-NLS-1$
			up.setExtraParameters(Arrays.asList(params));
		}

		up.setRefFilter(new RefFilter() {

			@Override
			public Map<String, Ref> filter(Map<String, Ref> refs) {

				Map<String, Ref> newRefs = new HashMap<>();

				for (Entry<String, Ref> entry : refs.entrySet()) {
					if ("refs/heads/main".equals(entry.getKey())) {
						newRefs.put(entry.getKey(), entry.getValue());
					}
					if (("refs/heads/" + userName).equals(entry.getKey())) {
						newRefs.put(entry.getKey(), entry.getValue());
					}
				}
				return newRefs;
			}
		});
		;

		return up;

	}

}
