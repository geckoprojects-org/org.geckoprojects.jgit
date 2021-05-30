package org.geckoprojects.jgit.core;

import java.io.IOException;
import java.nio.file.Path;

import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.geckoprojects.jgit.core.GitFileRepository.Config;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.ServiceScope;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@Designate(ocd = Config.class, factory = true)
@Component(service = Repository.class, configurationPid = Constants.PID_REPOSITORY_FILE,scope = ServiceScope.SINGLETON)
public class GitFileRepository extends FileRepository implements Named{

	enum InitType {

		CREATE_NEW_OR_THROW, CREATE_NEW_BARE_OR_THROW, EXISTING_OR_THROW, EXISTING_OR_CREATE_NEW,
		EXISTING_OR_CREATE_NEW_BARE
	}

	@ObjectClassDefinition(description = "Configuration of the InMemory Repositroty")
	@interface Config {

		@AttributeDefinition(description = "repository_name of the Repository")
		String repository_name();

		@AttributeDefinition(description = "name of the InMemory Repositroty")
		String gitDir();

		@AttributeDefinition(description = "true - Require the repository to exist before it can be opened.")
		boolean mustExist();
	}

	private Config config;

	@Activate
	public GitFileRepository(Config config) throws Exception {
		super(to(config));
		this.config=config;
		incrementOpen();

	}

	@Deactivate
	private void deactivate() {
		close();
	}

	private static FileRepositoryBuilder to(Config config) throws IllegalArgumentException, IOException {
		return new FileRepositoryBuilder().setGitDir(Path.of(config.gitDir()).toFile()).setMustExist(config.mustExist())
				.setup();

	}

	@Override
	public String getName() {
		return config.repository_name();
	}

}
