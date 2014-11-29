package net.tridentsdk.server;

import com.google.common.collect.LinkedListMultimap;

import java.util.ArrayList;
import java.util.List;

public class Benchmarks {
    public static final String[] TOKENS = {"1", "2", "4", "8", "16", "32", "64", "128", "256", "512", "1024"};
    private static final int TOK_LEN = TOKENS.length;

    // Insertion order required
    public static void chart(LinkedListMultimap<String, Double> data, String what) {
        int entries = data.size();
        String[] keys = convert(data.asMap().keySet().toArray());

        StringBuilder builder = new StringBuilder("http://chart.googleapis.com/chart?")
                .append("cht=lxy")                     // Chart type: Line/xy
                .append("&chtt=Benchmark+Results:+")   // Chart title: Benchmark Results:
                .append(what)                          // specified
                .append(" (ns/op)")
                .append("&chs=900x200")                // Chart size: 900x200
                .append("&chds=a")                     // Chart sizing: automatic
                .append("&chxt=x,y")                   // Chart x/y: visible
                .append("&chxs=0,000000,12,0,lt")      // Chart axis data
                .append("|")
                .append("1,000000,12,1,lt");

        StringBuilder dataBuilder = new StringBuilder("&chd=t:");
        for (int j = 0; j < TOK_LEN; j++) {
            dataBuilder.append(TOKENS[j]);
            if (j != TOK_LEN - 1)
                dataBuilder.append(",");
        }

        dataBuilder.append("|");

        for (int i = 0; i < keys.length; i++) {
            List<Double> doubles = new ArrayList<>(data.get(keys[i]));
            for (int j = 0; j < doubles.size(); j++) {
                dataBuilder.append(roundTo3(doubles.get(j)));
                if (j != doubles.size() - 1)
                    dataBuilder.append(",");
            }

            if (i != keys.length - 1)
                dataBuilder.append("|");
        }
        builder.append(dataBuilder.toString());        // Chart data: specified

        builder.append("&chdl=");                      // Chart keys: specified
        for (int i = 0; i < keys.length; i++) {
            builder.append(keys[i]);
            if (i != keys.length - 1) {
                builder.append(",");
            }
        }

        StringBuilder colors = new StringBuilder("&chco="); // Chart colors: randomized
        for (int i = 0; i < keys.length; i++) {
            colors.append(randomColor());
            if (i != entries - 1)
                colors.append(",");
        }
        builder.append(colors.toString());

        System.out.println(builder.toString());
    }

    private static String randomColor() {
        String[] letters = "0123456789ABCDEF".split("");
        StringBuilder code = new StringBuilder();
        for(int i = 0 ; i < 6; i++) {
            double doub = Math.random() * 15;
            int index = (int) Math.round(doub);
            code.append(letters[index]);
        }
        return code.toString();
    }

    private static String[] convert(Object[] objs) {
        String[] strings = new String[objs.length];
        for (int i = 0; i < objs.length; i++) {
            strings[i] = (String) objs[i];
        }

        return strings;
    }

    private static double roundTo3(double longDouble) {
        return (double) Math.round(longDouble * 1000) / 1000;
    }
}
