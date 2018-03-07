package com.abin.lee.debezium.svr.api.client;

import com.abin.lee.debezium.svr.common.util.JsonUtil;
import io.debezium.config.Configuration;
import io.debezium.embedded.EmbeddedEngine;
import io.debezium.util.LoggingContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.connect.errors.ConnectException;
import org.apache.kafka.connect.runtime.standalone.StandaloneConfig;
import org.apache.kafka.connect.source.SourceConnector;
import org.apache.kafka.connect.source.SourceRecord;

import java.nio.file.Path;
import java.util.concurrent.*;
import java.util.function.Predicate;

import static org.junit.Assert.fail;

/**
 * Created by abin on 2018/3/5 14:06.
 * debezium-svr
 * com.abin.lee.debezium.svr.api.client
 */
@Slf4j
public class DebeziumEmbeddedConnector {
    protected static final Path OFFSET_STORE_PATH = Testing.Files.createTestingPath("file-connector-offsets.txt").toAbsolutePath();
    private CountDownLatch latch;
    protected EmbeddedEngine engine;
    private ExecutorService executor;


    public static void main(String[] args) {

    }

    public void connect(){
        // Define the configuration for the embedded and MySQL connector ...
        Configuration config = Configuration.create()
                                    /* begin engine properties */
                .with("connector.class",
                        "io.debezium.connector.mysql.MySqlConnector")
                .with("offset.storage",
                        "org.apache.kafka.connect.storage.FileOffsetBackingStore")
                .with("offset.storage.file.filename",
                        "/path/to/storage/offset.dat")
                .with("offset.flush.interval.ms", 60000)
                                    /* begin connector properties */
                .with("name", "my-sql-connector")
                .with("database.hostname", "localhost")
                .with("database.port", 3306)
                .with("database.user", "debezium")
                .with("database.password", "dbz")
                .with("server.id", 85744)
                .with("database.server.name", "my-app-connector")
                .with("database.history",
                        "io.debezium.relational.history.FileDatabaseHistory")
                .with("database.history.file.filename",
                        "/path/to/storage/dbhistory.dat")
                .build();

// Create the engine with this configuration ...
        EmbeddedEngine engine = EmbeddedEngine.create()
                .using(config)
//                .notifying(this::handleEvent)
                .build();

// Run the engine asynchronously ...
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(engine);

// At some later time ...
        engine.stop();

    }


    protected void start(Class<? extends SourceConnector> connectorClass, Configuration connectorConfig,
                         EmbeddedEngine.CompletionCallback callback, Predicate<SourceRecord> isStopRecord) {
        Configuration config = Configuration.copy(connectorConfig)
                .with(EmbeddedEngine.ENGINE_NAME, "testing-connector")
                .with(EmbeddedEngine.CONNECTOR_CLASS, connectorClass.getName())
                .with(StandaloneConfig.OFFSET_STORAGE_FILE_FILENAME_CONFIG, OFFSET_STORE_PATH)
                .with(EmbeddedEngine.OFFSET_FLUSH_INTERVAL_MS, 0)
                .build();
        latch = new CountDownLatch(1);
        EmbeddedEngine.CompletionCallback wrapperCallback = (success, msg, error) -> {
            try {
                if (callback != null) callback.handle(success, msg, error);
            } finally {
                if (!success) {
                    // we only unblock if there was an error; in all other cases we're unblocking when a task has been started
                    latch.countDown();
                }
            }
        };

        EmbeddedEngine.ConnectorCallback connectorCallback = new EmbeddedEngine.ConnectorCallback() {
            @Override
            public void taskStarted() {
                // if this is called, it means a task has been started successfully so we can continue
                latch.countDown();
            }
        };

        // Create the connector ...
        engine = EmbeddedEngine.create()
                .using(config)
                .notifying((record) -> {
                    if (isStopRecord != null && isStopRecord.test(record)) {
                        log.error("Stopping connector after record as requested");
                        throw new ConnectException("Stopping connector after record as requested");
                    }
                    try {
                        System.out.println("------------"+ JsonUtil.toJson(record));
//                        consumedLines.put(record);
                    } catch (Exception e) {
                        Thread.interrupted();
                    }
                })
                .using(this.getClass().getClassLoader())
                .using(wrapperCallback)
                .using(connectorCallback)
                .build();

        // Submit the connector for asynchronous execution ...
//        assertThat(executor).isNull();
        executor = Executors.newFixedThreadPool(1);
        executor.execute(() -> {
            LoggingContext.forConnector(getClass().getSimpleName(), "", "engine");
            engine.run();
        });
        try {
            if (!latch.await(10, TimeUnit.SECONDS)) {
                // maybe it takes more time to start up, so just log a warning and continue
                log.warn("The connector did not finish starting its task(s) or complete in the expected amount of time");
            }
        } catch (InterruptedException e) {
            if (Thread.interrupted()) {
                fail("Interrupted while waiting for engine startup");
            }
        }
    }



}
