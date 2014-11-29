package net.tridentsdk.server;

import com.google.common.collect.LinkedListMultimap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Benchmarks {
    public static final String[] TOKENS = {"1", "2", "4", "8", "16", "32", "64", "128", "256", "512", "1024"};
    private static final int TOK_LEN = TOKENS.length;

    public static void main(String[] args) {
        // Used for conversion
        chart(parse(new String[]{"asm 2.8265354476738818",
                "asm 3.4782545492869135",
                "asm 5.306493102791721",
                "asm 11.952136886062913",
                "asm 24.663348031286848",
                "asm 57.69224286352492",
                "asm 126.11686595996926",
                "asm 259.33904622430396",
                "asm 527.0768911850773",
                "asm 1063.9458075576702",
                "asm 2128.7852327343962",
                "normal 6.834032423558203",
                "normal 8.684707668148432",
                "normal 9.721253821803177",
                "normal 18.565880619071233",
                "normal 30.430924841877903",
                "normal 63.43434991460381",
                "normal 132.55096123340317",
                "normal 263.8858792989287",
                "normal 532.5148233084855",
                "normal 1072.2434637406645",
                "normal 2136.478236348935",
                "sun 4.783822630624206",
                "sun 7.97313300552714",
                "sun 10.986937604903295",
                "sun 17.258207376280268",
                "sun 29.315675821973503",
                "sun 65.09054102410745",
                "sun 132.0019388198805",
                "sun 273.4339470869758",
                "sun 535.0946857089384",
                "sun 1073.2342493979588",
                "sun 2175.9071477704956"}), "Reflection");
    }

    // http://chart.googleapis.com/chart?cht=lxy&chtt=Benchmark+Results:+Reflection+methods(ns/op)&chs=900x300&chxt=x,x,y,y&chds=0,7&chxl=1:|CPU+Backoff|3:|Nanoseconds&chxs=0,000000,12,0,lt|1,000000,12,0,lt&chd=t:1,2,4,8,16,32,64,128,256,512,1024|2,3,5,11,24,57,126,259,527,1063,2128|1,2,4,8,16,32,64,128,256,512,1024|6,8,9,18,30,63,132,263,532,1072,2136|1,2,4,8,16,32,64,128,256,512,1024|4,7,10,17,29,65,132,273,535,1073,2175&chdl=asm|normal|sun&chco=29181B,795BDE,5AC5E7

    // Insertion order required
    public static void chart(LinkedListMultimap<String, Double> data, String what) {
        String[] keys = convert(data.asMap().keySet().toArray());
        double max = Collections.max(data.values());

        StringBuilder builder = new StringBuilder("http://chart.googleapis.com/chart?")
                .append("cht=lxy")                            // Chart type: Line/xy
                .append("&chtt=Benchmark+Results:+")          // Chart title: Benchmark Results:
                .append(what)                                 // specified
                .append(" (ns/op)")
                .append("chxr=0,0,8|2,0,1")                   // x = 0-8, y = 0-1
                .append("chds=0,8")                           // scaled from x/y min/max
                .append("&chs=900x300")                       // Chart size: 900x300
                .append("&chds=a")                            // Chart sizing: automatic
                .append("&chxt=x,x,y,y")                      // Chart x/y: visible
                .append("chxl=1:|CPU+Backoff|3:|Nanoseconds") // Axis labels
                .append("&chxs=0,000000,12,0,lt")             // Chart axis data
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
            if (i != keys.length - 1)
                builder.append("|");
        }

        StringBuilder colors = new StringBuilder("&chco="); // Chart colors: randomized
        for (int i = 0; i < keys.length; i++) {
            colors.append(randomColor());
            if (i != keys.length - 1)
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

    private static LinkedListMultimap<String, Double> parse(String[] lines) {
        LinkedListMultimap<String, Double> list = LinkedListMultimap.create();

        for (String s : lines) {
            String[] split = s.split(" ");
            list.put(split[0], Double.parseDouble(split[1]));
        }

        return list;
    }
}
