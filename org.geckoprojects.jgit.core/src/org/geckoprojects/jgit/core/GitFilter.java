package org.geckoprojects.jgit.core;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.eclipse.jgit.errors.RepositoryNotFoundException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.ServiceMayNotContinueException;
import org.eclipse.jgit.transport.resolver.RepositoryResolver;
import org.eclipse.jgit.transport.resolver.ServiceNotAuthorizedException;
import org.eclipse.jgit.transport.resolver.ServiceNotEnabledException;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;
import org.osgi.service.http.whiteboard.propertytypes.HttpWhiteboardContextSelect;
import org.osgi.service.http.whiteboard.propertytypes.HttpWhiteboardFilterName;
import org.osgi.service.http.whiteboard.propertytypes.HttpWhiteboardFilterPattern;
import org.osgi.service.useradmin.UserAdmin;

@Component(service = javax.servlet.Filter.class)
@HttpWhiteboardFilterName(GitFilter.NAME)
@HttpWhiteboardFilterPattern("/*")
//@HttpWhiteboardFilterServlet(GitServlet.NAME)
@HttpWhiteboardContextSelect("("+ HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME + "=" + AppServletContext.NAME + ")")
public class GitFilter extends org.eclipse.jgit.http.server.GitFilter
		implements RepositoryResolver<HttpServletRequest> {

	public static final String NAME = "GitFilter";
	Map<Repository, Map<String, Object>> repositorys = new ConcurrentHashMap<>();

	@Reference
	UserAdmin userAdmin; 
	
	public GitFilter() {
		setRepositoryResolver(this);
	}

	@Reference(cardinality = ReferenceCardinality.MULTIPLE,policy = ReferencePolicy.DYNAMIC)
	public void bindRepository(Repository repository, Map<String, Object> map) {
		repositorys.compute(repository, (k, v) -> map);
		repositorys.put(repository, map);

	}

	public void updatedRepository(Repository repository, Map<String, Object> map) {
		repositorys.put(repository, map);

	}

	public void unbindRepository(Repository repository) {
		repositorys.remove(repository);
	}

	@Activate
	public void activate() {

	}

	@Modified
	public void modified() {

	}

	@Deactivate
	public void deActivate() {

	}

	Optional<Repository> repositoryByName(String name) {
		return repositorys.entrySet().parallelStream()
				.filter(e -> Objects.equals(name, e.getValue().get(Constants.REPOSITORY_NAME))).map(Entry::getKey)
				.findAny();

	}

	@Override
	public Repository open(HttpServletRequest request, String name) throws RepositoryNotFoundException,
			ServiceNotAuthorizedException, ServiceNotEnabledException, ServiceMayNotContinueException {

		Optional<Repository> oRepository = repositoryByName(name);
		Repository r = oRepository.orElseThrow(() -> new RepositoryNotFoundException(name));
		r.incrementOpen();
		return r;

	}
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		// TODO Auto-generated method stub
		super.doFilter(request, response, chain);
	}

}
