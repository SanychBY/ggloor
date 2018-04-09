package com.bitloor.ggloor.settings;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ssaan on 09.05.2017.
 */
public class RegExHelper {
    public boolean Test(String data, String regex){
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(data);
        return m.matches();
    }
}
