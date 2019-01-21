package test.sales.gen;

import java.util.ArrayList;
import java.util.List;

public class RangeListExpr {
    public List<RangeExpr> specs = null;

    public RangeListExpr() {
    }

    public RangeListExpr(List<RangeExpr> ranges) {
        this.specs = ranges;
    }

    public static RangeListExpr fromQuery(String s) {
        RangeListExpr expr = new RangeListExpr();
        if (s != null)
        {   String[] ss = s.split(",");
            for (String si : ss)
                expr.append(RangeExpr.fromQuery(si));
            }
        return expr;
    }

    public static String getContentRange(long startZeroBased, long count, long total) {
        return String.format("items %d-%d/%d", startZeroBased, startZeroBased + count - 1, total);
    }

    public static long parseTotal(String contentRange) {
        return Integer.parseInt(contentRange.substring(contentRange.indexOf("/") + 1));
    }

    public RangeListExpr append(RangeExpr o) {
        if (specs == null)
            specs = new ArrayList<>();
        specs.add(o);
        return this;
    }

    public RangeListExpr append(String unit, Long n1, Long n2) {
        return append(new RangeExpr(unit, n1, n2));
    }

    public RangeListExpr append(Long n1, Long n2) {
        return append(new RangeExpr(n1, n2));
    }

    public String toQuery() {
        if (specs == null)
            return null;
        else {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < specs.size(); i++) {
                sb.append(specs.get(i).toQuery());
                if (i < specs.size() - 1)
                    sb.append(",");
            }
            return sb.toString();
        }

    }

    @Override
    public String toString() {
        return toQuery();
    }
}