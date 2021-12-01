package top.codexvn.client;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.StringJoiner;

public class Util {
    public static URL bindArgs(String targetURL, Map<String,String> args) throws MalformedURLException {
        StringJoiner sj = new StringJoiner("&");
        for (var i :args.entrySet()) {
            sj.add(String.format("%s=%s",i.getKey(),i.getValue()));
        }
        return new URL(targetURL+"?"+sj.toString());
    }
}
