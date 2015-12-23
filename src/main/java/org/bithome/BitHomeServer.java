/**
 * Created by Mike Kurdziel on 4/16/14.
 */
package org.bithome;

import com.google.common.base.Optional;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.jdbi.bundles.DBIExceptionsBundle;
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.bithome.config.BitHomeConfiguration;
import org.bithome.core.exception.BitHomeExceptionMapper;
import org.bithome.core.services.node.NodeService;
import org.bithome.core.services.storage.StorageService;
import org.bithome.core.services.storage.StorageServiceMapDb;
import org.bithome.resources.NodeWebResource;
import org.skife.jdbi.v2.DBI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BitHomeServer extends Application<BitHomeConfiguration> {

    final static Logger LOGGER = LoggerFactory.getLogger(BitHomeServer.class);

//    private UserService userService;
//    private AthleteService athleteService;
//    private AuthService authService;
    private DBI database;
//    private TeamService teamService;
//    private MeetService meetService;

    public static void main(String[] args) throws Exception {
        new BitHomeServer().run(args);
    }

    @Override
    public String getName() {
        return "BitHome";
    }

    @Override
    public void initialize(Bootstrap<BitHomeConfiguration> bootstrap) {
        // Set up the asset bundle so we can both serve assets and the API
        bootstrap.addBundle(new AssetsBundle("/assets/", "/", "index.html"));
    }

    @Override
    public void run(final BitHomeConfiguration configuration,
                    final Environment environment)
            throws ClassNotFoundException {

        Optional<String> databaseFile = Optional.fromNullable(configuration.getDatabaseFile());

        StorageService storageService = new StorageServiceMapDb(databaseFile);

        NodeService nodeService = new NodeService(storageService);

        environment.lifecycle().manage(storageService);
        environment.lifecycle().manage(nodeService);
//
//        final AthleteDao athleteDao = this.database.onDemand(AthleteDao.class);
//        final UserDao userDao = this.database.onDemand(UserDao.class);
//        final SessionDao sessionDao = this.database.onDemand(SessionDao.class);
//        final TeamDao teamDao = this.database.onDemand(TeamDao.class);
//        final MeetDao meetDao = this.database.onDemand(MeetDao.class);
//
//        this.athleteService = new AthleteService(athleteDao);
//        this.userService = new UserService(userDao);
//        this.authService = new AuthService(userDao, sessionDao);
//        this.teamService = new TeamService(teamDao);
//        this.meetService = new MeetService(meetDao);
//
            final NodeWebResource nodeWebResource = new NodeWebResource();
//        final AuthWebService authWebService = new AuthWebService(authService);
//        final UserWebService userWebService = new UserWebService(new UserAccessService(userService));
//        final TeamWebService teamWebService = new TeamWebService(new TeamAccessService(teamService));
//        final AthleteWebService athleteWebService = new AthleteWebService(
//                new AthleteAccessService(athleteService, teamService));
//        final MeetWebService meetWebService = new MeetWebService(new MeetAccessService(meetService, teamService));

        // Put the API on /api so the base page can server the UI
        environment.jersey().setUrlPattern("/api/*");

//        // Register the services
        environment.jersey().register(nodeWebResource);
//        environment.jersey().register(userWebService);
//        environment.jersey().register(authWebService);
//        environment.jersey().register(teamWebService);
//        environment.jersey().register(athleteWebService);
//        environment.jersey().register(meetWebService);
//
//        // Register the authentication handler
//        environment.jersey().register(new SessionAuthProvider<Session>(authService));
        environment.jersey().register(new BitHomeExceptionMapper());
    }
}
