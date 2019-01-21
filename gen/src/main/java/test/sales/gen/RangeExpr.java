package test.sales.gen;

public class RangeExpr {
    public String unit = null;
    public Long n1 = null;
    public Long n2 = null;

    public RangeExpr(String unit, Long n1, Long n2) {
        this.unit = unit;
        this.n1 = n1;
        this.n2 = n2;
    }

    public RangeExpr(Long n1, Long n2) {
        this("items", n1, n2);
    }

    public static RangeExpr fromQuery(String s) {
        if (s == null)
            return null;
        int iEqual = s.indexOf("=");
        int iDash = s.indexOf("-");
        String unit = s.substring(0, iEqual);
        Long n1 = iDash - iEqual > 1 ? Long.parseLong(s.substring(iEqual + 1, iDash)) : null;
        Long n2 = iDash < s.length() - 1 ? Long.parseLong(s.substring(iDash + 1)) : null;
        return new RangeExpr(unit, n1, n2);
    }

    public long getStart(long total) {
        if (n1 != null && n2 != null) return n1; //return n1<=total-1?n1:total>0?total-1:0;
        else if (n1 != null && n2 == null) return n1; //return n1>total-1?total-1:n1;
        else if (n1 == null && n2 != null) return n2 > total ? 0 : total - n2;
        else return 0;
    }

    public long getStop(long total) {
        if (n1 != null && n2 != null) return n2;
        else if (n1 != null && n2 == null) return total - 1;
        else if (n1 == null && n2 != null) return total - 1;
        else return total - 1;
    }

    public long getCount(long total) {
        if (n1 != null && n2 != null) return n2 - n1 + 1; //n2>total-1?total-n1:n2-n1+1;
        else if (n1 != null && n2 == null) return total - n1; //n1>total-1?0:total-n1;
        else if (n1 == null && n2 != null) return n2; //n2>total?total:n2;
        else return total;
    }

    public String toQuery() {
        StringBuffer sb = new StringBuffer();
        sb.append(unit);
        sb.append("=");
        sb.append(n1 != null ? n1 : "");
        sb.append("-");
        sb.append(n2 != null ? n2 : "");
        return sb.toString();
    }

    @Override
    public String toString() {
        return toQuery();
    }
}