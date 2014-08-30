package net.tridentsdk.packets.login;

import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;

public class LoginManager {

    private static LoginManager instance = new LoginManager();

    private volatile ConcurrentHashMap<InetSocketAddress, String> loginNames
            = new ConcurrentHashMap<>();

    private LoginManager() {}

    public void initLogin(InetSocketAddress address, String name) {
        loginNames.put(address, name);
    }

    public String getName(InetSocketAddress address) {
        return loginNames.get(address);
    }

    public void finish(InetSocketAddress address) {
        loginNames.remove(address);
    }


    public static LoginManager getInstance() {
        return instance;
    }
}
