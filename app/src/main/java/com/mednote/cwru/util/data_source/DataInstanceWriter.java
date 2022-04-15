package com.mednote.cwru.util.data_source;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.LongSerializationPolicy;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;

public class DataInstanceWriter<T> {
    protected Gson gson;
    private final DataSource dataSource;
    private final File baseDir;
    public DataInstanceWriter(DataSource dataSource, File baseDir) {
        this.dataSource = dataSource;
        this.baseDir = baseDir;
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setLongSerializationPolicy( LongSerializationPolicy.STRING );
        this.gson = gsonBuilder.create();
    }

    private void initiateDir(File directory) {
        dataSource.defaultDir();
        dataSource.moveDirectory(directory);
    }

    synchronized public void saveDataInstance(T dataInstance, String filename) {
        initiateDir(baseDir);
        JsonObject jsonObject = gson.fromJson(gson.toJson(dataInstance), JsonObject.class);
        dataSource.writeToFile(filename, gson.toJson(jsonObject));
    }

    public T retrieveDataInstance(String filename) {
        initiateDir(baseDir);
        String fileData = dataSource.readFile(filename);
        if (fileData != null) {
            // data was saved as the JsonArray
            JsonObject jsonElement = gson.fromJson(fileData, JsonObject.class);
            Type empMapType = new TypeToken<T>() {}.getType();
            return (gson.fromJson(jsonElement, empMapType));
        }
        return  null;
    }

    public T retrieveDataInstance(String filename, Type typeToken) {
        initiateDir(baseDir);
        String fileData = dataSource.readFile(filename);
        if (fileData != null) {
            // data was saved as the JsonArray
            JsonObject jsonElement = gson.fromJson(fileData, JsonObject.class);
            return (gson.fromJson(jsonElement, typeToken));
        }
        return  null;
    }
}
