package test.sales.gen;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class FilterOperandExpr implements FilterExpr {
    public static final String OP_STRING = "string";
    public static final String OP_NUMBER = "number";
    public static final String OP_DATE = "date";

    public String op = null;
    public String data = null;
    public Boolean isCol = null;

    public FilterOperandExpr() {
    }

    public FilterOperandExpr(String data, String op, Boolean isCol) {
        this.op = op;
        this.data = data;
        this.isCol = isCol;
    }
}