package test.sales.gen;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DefaultDateFormatter {
    private static SimpleDateFormat formatter;

    public static SimpleDateFormat getFormatter() {
        if (formatter == null)
            formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        return formatter;
    }

    public static String format(Date date) {
        return date==null?null:getFormatter().format(date);
    }

    public static Date parse(String str) {
        Date date = null;
        try {
            date = str==null?null:getFormatter().parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
}