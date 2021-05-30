package org.geckoprojects.jgit.core.http;

import java.io.IOException;
import java.security.Principal;
import java.util.Collection;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.jgit.errors.CorruptObjectException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.http.server.resolver.DefaultReceivePackFactory;
import org.eclipse.jgit.lib.AnyObjectId;
import org.eclipse.jgit.lib.ObjectChecker;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.transport.PostReceiveHook;
import org.eclipse.jgit.transport.PreReceiveHook;
import org.eclipse.jgit.transport.ReceiveCommand;
import org.eclipse.jgit.transport.ReceiveCommand.Result;
import org.eclipse.jgit.transport.ReceiveCommand.Type;
import org.eclipse.jgit.transport.ReceivePack;
import org.eclipse.jgit.transport.resolver.ServiceNotAuthorizedException;
import org.eclipse.jgit.transport.resolver.ServiceNotEnabledException;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.geckoprojects.jgit.core.Named;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.useradmin.Authorization;
import org.osgi.service.useradmin.User;
import org.osgi.service.useradmin.UserAdmin;

@Component(service = HttpReceivePackFactory.class)
public class HttpReceivePackFactoryImpl implements HttpReceivePackFactory {

//	@Reference
	UserAdmin userAdmin;
	DefaultReceivePackFactory f;

	@Override
	public ReceivePack create(HttpServletRequest req, Repository db)
			throws ServiceNotEnabledException, ServiceNotAuthorizedException {

		if (!(db instanceof Named)) {
			new ServiceNotEnabledException();
		}
		String repoName = ((Named) db).getName();
		Principal principal = req.getUserPrincipal();
		String userName = principal == null ? "stbischof" : principal.getName();

		if (false) {
			User user = userAdmin.getUser("username", userName);
			Authorization authorization = userAdmin.getAuthorization(user);
			String targetRole = "repo:" + repoName + ":";

			long matchedRoles = Stream.of(authorization.getRoles()).filter(role -> targetRole.matches(targetRole))
					.count();

			if (matchedRoles < 1) {
				new ServiceNotAuthorizedException("wrong role");
			}
		}
		// https://mincong.io/2018/07/30/jgit-protect-master-branch-on-git-server/
		final ReceivePack rp = new ReceivePack(db);

		rp.setPreReceiveHook(new PreReceiveHook() {

			@Override
			public void onPreReceive(ReceivePack rp, Collection<ReceiveCommand> commands) {
				System.out.println(commands);

				try (RevWalk walk = new RevWalk(rp.getRepository())) {
					for (ReceiveCommand cmd : commands) {

						// just on my branche
						if (!("refs/heads/" + userName).equals(cmd.getRefName())) {
							cmd.setResult(Result.REJECTED_CURRENT_BRANCH,
									"push only on your branche <" + userName + ">");
							continue;
						}
						// do not remove you own branche
						if (cmd.getType() == Type.DELETE) {
							cmd.setResult(Result.REJECTED_NODELETE, "you will need your branche");

						}

						// FileSearch
						RevCommit commitnew = walk.parseCommit(cmd.getNewId());
						ObjectId treeIdNew = commitnew.getTree().getId();
						try (ObjectReader reader = rp.getRepository().newObjectReader()) {
							CanonicalTreeParser newP = new CanonicalTreeParser(null, reader, treeIdNew);

							//
							boolean b = newP.findFile("readme.md2");

							walk.reset();
							if (b) {

								cmd.setResult(Result.REJECTED_OTHER_REASON, "unallowed file manipulation");
							}
						}

					}
				} catch (MissingObjectException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IncorrectObjectTypeException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		});
		rp.setObjectChecker(new ObjectChecker() {

			@Override
			public void check(AnyObjectId id, int objType, byte[] raw) throws CorruptObjectException {
				System.out.println(id.getName());
				super.check(id, objType, raw);
			}

			@Override
			public void checkPath(String path) throws CorruptObjectException {
				System.out.println(path);
				super.checkPath(path);
			}
		});
//		rp.setAdvertiseRefsHook(null);
		rp.setPostReceiveHook(new PostReceiveHook() {

			@Override
			public void onPostReceive(ReceivePack rp, Collection<ReceiveCommand> commands) {
				// Push to real repo and handle ci build, formar, codeanalysis
			}
		});
//		rp.setPreReceiveHook(null);
		rp.setRefLogIdent(new PersonIdent("name", "email@email.de"));
		return rp;
	}

}
