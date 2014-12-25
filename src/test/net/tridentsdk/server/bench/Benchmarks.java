package net.tridentsdk.server.bench;

import com.google.common.collect.LinkedListMultimap;
import org.openjdk.jmh.results.BenchmarkResult;
import org.openjdk.jmh.results.RunResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Benchmarks {
    public static final String[] TOKENS = { "1", "2", "4", "8", "16", "32", "64", "128", "256", "512", "1024" };
    private static final int TOK_LEN = TOKENS.length;

    public static void main(String[] args) {
        // Used for conversion
        chart(parse("eventManagerRegister 2839.479\n" +
                            "eventManagerRegister 2951.136\n" +
                            "eventManagerRegister 3281.786\n" +
                            "eventManagerRegister 2256.583\n" +
                            "eventManagerRegister 2418.254\n" +
                            "eventManagerRegister 2873.758\n" +
                            "eventManagerRegister 3198.369\n" +
                            "eventManagerRegister 2627.253\n" +
                            "eventManagerRegister 3021.304\n" +
                            "eventManagerRegister 3523.214\n" +
                            "eventManagerRegister 4625.676\n" +
                            "control 4.024\n" +
                            "control 4.929\n" +
                            "control 9.571\n" +
                            "control 18.557\n" +
                            "control 38.897\n" +
                            "control 80.024\n" +
                            "control 161.235\n" +
                            "control 333.729\n" +
                            "control 644.482\n" +
                            "control 1278.642\n" +
                            "control 2916.530\n" +
                            "eventBusDispatch 2770.647\n" +
                            "eventBusDispatch 2628.087\n" +
                            "eventBusDispatch 2751.062\n" +
                            "eventBusDispatch 2715.058\n" +
                            "eventBusDispatch 2623.678\n" +
                            "eventBusDispatch 2608.599\n" +
                            "eventBusDispatch 2588.978\n" +
                            "eventBusDispatch 2699.354\n" +
                            "eventBusDispatch 2871.476\n" +
                            "eventBusDispatch 2870.639\n" +
                            "eventBusDispatch 3997.102\n" +
                            "eventBusRegister 1725.076\n" +
                            "eventBusRegister 1649.689\n" +
                            "eventBusRegister 1770.917\n" +
                            "eventBusRegister 1610.475\n" +
                            "eventBusRegister 1803.798\n" +
                            "eventBusRegister 1870.417\n" +
                            "eventBusRegister 2196.282\n" +
                            "eventBusRegister 2790.529\n" +
                            "eventBusRegister 3936.786\n" +
                            "eventBusRegister 6145.257\n" +
                            "eventBusRegister 4808.295\n" +
                            "eventManagerDispatch 41.650\n" +
                            "eventManagerDispatch 44.365\n" +
                            "eventManagerDispatch 45.381\n" +
                            "eventManagerDispatch 56.980\n" +
                            "eventManagerDispatch 81.285\n" +
                            "eventManagerDispatch 134.914\n" +
                            "eventManagerDispatch 210.851\n" +
                            "eventManagerDispatch 389.052\n" +
                            "eventManagerDispatch 696.638\n" +
                            "eventManagerDispatch 1340.717\n" +
                            "eventManagerDispatch 2596.675"), "Event+Facilities+performance");
    }

    // Insertion order required
    public static void chart(LinkedListMultimap<String, Double> data, String what) {
        String[] keys = toString(data.asMap().keySet().toArray());
        double[] base = toDouble(data.get("control").toArray());

        StringBuilder builder = new StringBuilder("http://chart.googleapis.com/chart?").append(
                "cht=lxy")                             // Chart type: Line/xy
                .append("&chtt=Benchmark+Results:+")           // Chart title: Benchmark Results:
                .append(what)                                  // specified
                .append(" (ns/op)").append("&chs=900x300")                        // Chart size: 900x300
                .append("&chxt=x,x,y,y")                       // Chart x/y: visible
                .append("&chxl=1:|CPU+Backoff|3:|Nanoseconds") // Axis labels
                .append("&chds=a").append("&chxl=1:|CPU+Backoff|3:|Nanoseconds") // Axis labels
                .append("&chxs=0,000000,12,0,lt")              // Chart axis data
                .append("|").append("1,000000,12,1,lt");

        StringBuilder dataBuilder = new StringBuilder("&chd=t:");
        for (int i = 0; i < keys.length; i++) {
            for (int j = 0; j < TOK_LEN; j++) {
                dataBuilder.append(TOKENS[j]);
                if (j != TOK_LEN - 1) dataBuilder.append(",");
            }

            dataBuilder.append("|");

            String label = keys[i];
            List<Double> doubles = new ArrayList<>(data.get(label));
            for (int j = 0; j < doubles.size(); j++) {
                dataBuilder.append(roundTo3(doubles.get(j) - base[j]));
                if (j != doubles.size() - 1) dataBuilder.append(",");
            }

            if (i != keys.length - 1) dataBuilder.append("|");
        }
        builder.append(dataBuilder.toString());        // Chart data: specified

        builder.append("&chdl=");                      // Chart keys: specified
        for (int i = 0; i < keys.length; i++) {
            builder.append(keys[i]);
            if (i != keys.length - 1) builder.append("|");
        }

        StringBuilder colors = new StringBuilder("&chco="); // Chart colors: randomized
        for (int i = 0; i < keys.length; i++) {
            String rand = randomColor();
            colors.append(rand);
            if (rand.length() != 6) { // This happens for no reason sometimes
                for (int j = 0; j < 6 - rand.length(); j++) {
                    colors.append(randomChar());
                }
            }

            if (i != keys.length - 1) colors.append(",");
        }
        builder.append(colors.toString());

        System.out.println(builder.toString());
    }

    private static String randomColor() {
        String[] letters = "0123456789ABCDEF".split("");
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            double doub = Math.random() * 15;
            int index = (int) Math.round(doub);
            code.append(letters[index]);
        }
        return code.toString();
    }

    private static String randomChar() {
        String[] letters = "0123456789ABCDEF".split("");
        double doub = Math.random() * 15;
        int index = (int) Math.round(doub);
        return letters[index];
    }

    private static String[] toString(Object[] objs) {
        String[] strings = new String[objs.length];
        for (int i = 0; i < objs.length; i++) {
            strings[i] = (String) objs[i];
        }

        return strings;
    }

    private static double[] toDouble(Object[] objs) {
        double[] doubles = new double[objs.length];
        for (int i = 0; i < objs.length; i++) {
            doubles[i] = (double) objs[i];
        }

        return doubles;
    }

    private static double roundTo3(double longDouble) {
        return (double) Math.round(longDouble * 1000) / 1000;
    }

    public static LinkedListMultimap<String, Double> parse(String lines) {
        LinkedListMultimap<String, Double> list = LinkedListMultimap.create();

        for (String s : lines.split("\n")) {
            String[] split = s.split(" ");
            list.put(split[0], Double.parseDouble(split[1]));
        }

        return list;
    }

    public static LinkedListMultimap<String, Double> parse(Collection<RunResult> results) {
        LinkedListMultimap<String, Double> data = LinkedListMultimap.create();
        for (RunResult result : results) {
            for (BenchmarkResult result0 : result.getBenchmarkResults()) {
                System.out.println(result0.getPrimaryResult().getLabel() + " " + result0.getPrimaryResult().getScore());
                data.put(result0.getPrimaryResult().getLabel(), result0.getPrimaryResult().getScore());
            }
        }

        return data;
    }
}
