package org.bithome.core.services.storage;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.bithome.api.Version;
import org.bithome.api.node.nodes.Node;
import org.mapdb.Atomic;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Set;
import java.util.concurrent.ConcurrentNavigableMap;

/**
 * Created by Mike Kurdziel on 5/20/14.
 */
public class StorageServiceMapDb implements StorageService {
    private final static Logger LOGGER = LoggerFactory.getLogger(StorageServiceMapDb.class);

    private final static String KEY_VERSION = "version";
    private final static String KEY_NODE_LIST = "nodeList";

    private final Optional<String> dbFileName;
    private DB db;

    private Version dbVersion = new Version(1,0);

    private ConcurrentNavigableMap<Long,Node> nodeMap;


    /**
     * Create a MapDB storage service in-memory
     */
    public StorageServiceMapDb() {
        this.dbFileName = Optional.absent();
    }

    /**
     * Create a MapDB storage service with a backing file storage
     *
     * @param dbFileName name of the database file
     */
    public StorageServiceMapDb(final Optional<String> dbFileName) {
        this.dbFileName = dbFileName;
    }

    @Override
    public void start() throws Exception {
        LOGGER.info("Starting StorageServiceMapDb...");
        // configure and open database using builder pattern.
        // all options are available with code auto-completion.

        // If a filename is present, use that file. Otherwise use in-memory db.
        if (dbFileName.isPresent()) {
            LOGGER.info("Loading data from file: {}", this.dbFileName.get());
            db = DBMaker.newFileDB(new File(dbFileName.get()))
                    .transactionDisable()
                    .closeOnJvmShutdown()
                    .make();
        } else {
            LOGGER.info("No database file given. Creating in-memory database");
            db = DBMaker.newMemoryDB()
                    .closeOnJvmShutdown()
                    .make();
        }

        // Handle the db versioning
        if (db.exists(KEY_VERSION)) {
            Atomic.String versionString = db.getAtomicString(KEY_VERSION);
            LOGGER.info("Database version is at {}", versionString);
        } else {
            LOGGER.info("Database is empty, setting to version {}", dbVersion.toString());
            db.createAtomicString(KEY_VERSION, dbVersion.toString());
            db.commit();
        }

        nodeMap =  db.getTreeMap("nodeList");

        LOGGER.info("Started StorageServiceMapDb");
    }

    @Override
    public void stop() throws Exception {
        LOGGER.info("Stopping StorageServiceMapDb...");
        if (!db.isClosed()) {
            db.close();
        }
        LOGGER.info("Stopped StorageServiceMapDb");
    }

    public Version getDbVersion() {
        return dbVersion;
    }

    @Override
    public void saveNode(Node node) {
        Preconditions.checkNotNull(node);
        Preconditions.checkNotNull(node.getId());

        Long nodeId = node.getId();
        if (nodeMap.containsKey(nodeId)) {
            nodeMap.replace(nodeId, node.clone());
        } else {
            nodeMap.put(nodeId, node.clone());
        }

        db.commit();
    }

    @Override
    public void unsaveNode(Node node) {
        Preconditions.checkNotNull(node);
        Preconditions.checkNotNull(node.getId());

        Long nodeId = node.getId();
        if (nodeMap.containsKey(nodeId)) {
            nodeMap.remove(nodeId);
        }

        db.commit();
    }

    @Override
    public ImmutableSet<Node> getNodes() {
        Set<Node> nodes = Sets.newHashSet();

        for (Node node : nodeMap.values()) {
            nodes.add(node.clone());
        }

        return ImmutableSet.copyOf(nodes);
    }
}
