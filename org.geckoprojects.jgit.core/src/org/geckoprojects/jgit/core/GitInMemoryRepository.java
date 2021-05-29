package org.geckoprojects.jgit.core;

import org.eclipse.jgit.internal.storage.dfs.DfsRepositoryDescription;
import org.eclipse.jgit.internal.storage.dfs.InMemoryRepository;
import org.eclipse.jgit.lib.Repository;
import org.geckoprojects.jgit.core.GitInMemoryRepository.Config;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.ServiceScope;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@Designate(ocd = Config.class, factory = true)
@Component(service = Repository.class, configurationPid = Constants.PID_REPOSITORY_INMEMORY, scope = ServiceScope.SINGLETON)
public class GitInMemoryRepository extends InMemoryRepository {

	enum InitType {

		CREATE_NEW_OR_THROW, CREATE_NEW_BARE_OR_THROW, EXISTING_OR_THROW, EXISTING_OR_CREATE_NEW,
		EXISTING_OR_CREATE_NEW_BARE
	}

	@ObjectClassDefinition(description = "Configuration of the InMemory Repositroty")
	@interface Config {
		@AttributeDefinition(description = "repository_name of the InMemory Repository")
		String repository_name();

		@AttributeDefinition(description = "name of the InMemory Repositroty")
		InitType initType() default InitType.EXISTING_OR_CREATE_NEW;
	}

	@Activate
	public GitInMemoryRepository(Config config) throws Exception {
		super(to(config));
		switch (config.initType()) {
		case CREATE_NEW_OR_THROW:
			if (exists()) {
				throw new Exception("CREATE_NEW_OR_THROW");
			}
			create();
			break;

		case CREATE_NEW_BARE_OR_THROW:
			if (exists()) {
				throw new Exception("CREATE_NEW_OR_THROW");
			}
			create(true);
			break;

		case EXISTING_OR_THROW:
			if (!exists()) {
				throw new Exception("CREATE_NEW_OR_THROW");
			}

			break;

		case EXISTING_OR_CREATE_NEW:
			if (exists()) {
				create();
			}
			break;

		case EXISTING_OR_CREATE_NEW_BARE:
			if (exists()) {
				create(true);
			}
			break;

		}

	}

	@Deactivate
	private void deactivate() {
		close();
	}

	private static DfsRepositoryDescription to(Config config) {
		return new DfsRepositoryDescription(config.repository_name());

	}

}
