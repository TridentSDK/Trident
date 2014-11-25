package net.tridentsdk.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/*
1
1
 */
public class PassByValue {
    private static final Map<String, ArrayList<String>> MAP = new HashMap<>();

    public static void main(String[] args) {
        MAP.put("lol", new ArrayList<String>());
        ArrayList<String> temp = MAP.get("lol");
        temp.add("lel");
        System.out.println(MAP.get("lol").size());
        MAP.put("lol", temp);
        System.out.println(MAP.get("lol").size());
    }
}
