package com.mednote.cwru.base;

import java.util.Map;

public interface PermissionRequestHandler {
    void handlePermissionRequest(Map<String, Boolean> permissions);
}