package com.mednote.cwru.login;

import com.mednote.cwru.serverapi.ServerResult;

public class LoginServerResult extends ServerResult<LoginServerResponse> {
    private LoginServerResponse loginServerResponse;

    public LoginServerResult(LoginServerResponse loginServerResponse) {
        this.loginServerResponse = loginServerResponse;
    }

    @Override
    public LoginServerResponse getResult() {
        return loginServerResponse;
    }
}
