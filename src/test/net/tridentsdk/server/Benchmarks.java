package net.tridentsdk.server;

import com.google.common.collect.LinkedListMultimap;
import org.openjdk.jmh.results.BenchmarkResult;
import org.openjdk.jmh.results.RunResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Benchmarks {
    public static final String[] TOKENS = {"1", "2", "4", "8", "16", "32", "64", "128", "256", "512", "1024"};
    private static final int TOK_LEN = TOKENS.length;

    public static void main(String[] args) {
        // Used for conversion
        chart(parse("eventManagerRegister 819.725814993991\n" +
                "eventManagerRegister 819.7716393886067\n" +
                "eventManagerRegister 900.4173726148301\n" +
                "eventManagerRegister 808.9506525327429\n" +
                "eventManagerRegister 871.4493851680412\n" +
                "eventManagerRegister 870.8841183058485\n" +
                "eventManagerRegister 986.1459181678067\n" +
                "eventManagerRegister 1161.3012526324733\n" +
                "eventManagerRegister 1339.4179146410627\n" +
                "eventManagerRegister 2155.0835816456324\n" +
                "eventManagerRegister 3255.1289833815845\n" +
                "control 3.0563752522396896\n" +
                "control 4.858000765510459\n" +
                "control 8.754803637812916\n" +
                "control 17.62116829192222\n" +
                "control 36.6637301630973\n" +
                "control 80.64998764139224\n" +
                "control 159.29802810211496\n" +
                "control 317.84067080316265\n" +
                "control 604.4722867379808\n" +
                "control 1229.1015203632219\n" +
                "control 2438.4067537554483\n" +
                "eventBusDispatch 2634.0172621305564\n" +
                "eventBusDispatch 2367.2204021884513\n" +
                "eventBusDispatch 2758.167994277101\n" +
                "eventBusDispatch 2429.86571028309\n" +
                "eventBusDispatch 2739.0328112853776\n" +
                "eventBusDispatch 2644.5405530575954\n" +
                "eventBusDispatch 2569.30430563922\n" +
                "eventBusDispatch 2604.4984638653027\n" +
                "eventBusDispatch 2516.417413929624\n" +
                "eventBusDispatch 2748.7969212941725\n" +
                "eventBusDispatch 3796.988680089802\n" +
                "eventBusRegister 1804.8599982032617\n" +
                "eventBusRegister 1781.1831930272704\n" +
                "eventBusRegister 1876.5939591018257\n" +
                "eventBusRegister 1875.800880989313\n" +
                "eventBusRegister 2002.4119094557268\n" +
                "eventBusRegister 2060.5012920137196\n" +
                "eventBusRegister 2310.212881832179\n" +
                "eventBusRegister 3057.3334045627107\n" +
                "eventBusRegister 3855.548157015138\n" +
                "eventBusRegister 5429.437159213199\n" +
                "eventBusRegister 4378.197201192301\n" +
                "eventManagerDispatch 14.227984141474053\n" +
                "eventManagerDispatch 16.448196908479083\n" +
                "eventManagerDispatch 22.709589607791873\n" +
                "eventManagerDispatch 31.433138184416862\n" +
                "eventManagerDispatch 57.82842572283695\n" +
                "eventManagerDispatch 103.33618694585797\n" +
                "eventManagerDispatch 178.8228942946658\n" +
                "eventManagerDispatch 333.0840272351006\n" +
                "eventManagerDispatch 618.4609425982228\n" +
                "eventManagerDispatch 1280.6936879058728\n" +
                "eventManagerDispatch 2459.4746458453874"), "Scheduling+performance");
    }

    // Insertion order required
    public static void chart(LinkedListMultimap<String, Double> data, String what) {
        String[] keys = toString(data.asMap().keySet().toArray());
        double[] base = toDouble(data.get("control").toArray());

        StringBuilder builder = new StringBuilder("http://chart.googleapis.com/chart?")
                .append("cht=lxy")                             // Chart type: Line/xy
                .append("&chtt=Benchmark+Results:+")           // Chart title: Benchmark Results:
                .append(what)                                  // specified
                .append(" (ns/op)")
                .append("&chs=900x300")                        // Chart size: 900x300
                .append("&chxt=x,x,y,y")                       // Chart x/y: visible
                .append("&chxl=1:|CPU+Backoff|3:|Nanoseconds") // Axis labels
                .append("&chds=a")
                .append("&chxl=1:|CPU+Backoff|3:|Nanoseconds") // Axis labels
                .append("&chxs=0,000000,12,0,lt")              // Chart axis data
                .append("|")
                .append("1,000000,12,1,lt");

        StringBuilder dataBuilder = new StringBuilder("&chd=t:");
        for (int i = 0; i < keys.length; i++) {
            for (int j = 0; j < TOK_LEN; j++) {
                dataBuilder.append(TOKENS[j]);
                if (j != TOK_LEN - 1)
                    dataBuilder.append(",");
            }

            dataBuilder.append("|");

            String label = keys[i];
            List<Double> doubles = new ArrayList<>(data.get(label));
            for (int j = 0; j < doubles.size(); j++) {
                dataBuilder.append(roundTo3(doubles.get(j) - base[j]));
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
            if (i != keys.length - 1)
                builder.append("|");
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

            if (i != keys.length - 1)
                colors.append(",");
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
