/**
 * Created by Mike Kurdziel on 4/16/14.
 */
package org.bithome.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class BitHomeConfiguration extends Configuration {

    private String databaseFile;

    @JsonProperty
    public String getDatabaseFile() {
        return databaseFile;
    }

    @JsonProperty
    public void setDatabaseFile(String databaseFile) {
        this.databaseFile = databaseFile;
    }
}