package com.mednote.cwru.datasources;

import java.io.File;

public interface DataSource {
    void defaultDir();
    File getDefaultDir();
}
