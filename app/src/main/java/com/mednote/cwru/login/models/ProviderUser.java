package com.mednote.cwru.login.models;

import com.mednote.cwru.login.models.AccountCredentials;
import com.mednote.cwru.login.models.LoggedInUser;

import java.util.ArrayList;
import java.util.List;

public class ProviderUser extends LoggedInUser {
    private List<AccountCredentials> clients;

    public ProviderUser(AccountCredentials accountCredentials) {
        super(accountCredentials);
        clients = new ArrayList<>();
    }

    public List<AccountCredentials> getClients() {
        return clients;
    }

    public void setClients(List<AccountCredentials> clients) {
        this.clients = clients;
    }

    public void addClient(AccountCredentials client) {
        clients.add(client);
    }
}
