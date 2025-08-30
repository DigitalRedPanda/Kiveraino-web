package com.digiunion.kick.model;

import java.util.Set;

import com.digiunion.model.Client;

public record KickChatroom(Channel channel,
                        Set<Client> clients) {
            public boolean addClient(Client client) {
                        return clients.add(client);
            }
}
