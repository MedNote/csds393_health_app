package com.mednote.cwru.login;

import com.mednote.cwru.serverapi.ServerResult;

public class SignUpServerResult extends ServerResult<SignUpServerResponse> {
    SignUpServerResponse signUpServerResponse;

    public SignUpServerResult(SignUpServerResponse signUpServerResponse) {
        this.signUpServerResponse = signUpServerResponse;
    }

    @Override
    public SignUpServerResponse getResult() {
        return null;
    }
}
