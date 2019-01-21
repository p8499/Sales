package test.sales.gen;

public class OrderByExpr {
    public String field = null;
    public boolean asc;

    public OrderByExpr(String field, boolean asc) {
        this.field = field;
        this.asc = asc;
    }

    public static OrderByExpr fromQuery(String s) {
        return s == null ? null : new OrderByExpr(s.substring(1).trim(), s.charAt(0) == '-' ? false : true);
    }

    public static OrderByExpr fromSql(String s) {
        String s2 = s.trim();
        int iBlank = s2.indexOf(" ");
        return s == null ? null : new OrderByExpr(s2.substring(0, iBlank).trim(), s2.substring(iBlank + 1).trim().toUpperCase().equals("DESC") ? false : true);
    }

    public OrderByListExpr only() {
        return new OrderByListExpr().append(this);
    }

    public String toSql() {
        return String.format("%s %s", field, asc ? "ASC" : "DESC");
    }

    public String toQuery() {
        return String.format("%s%s", asc ? "+" : "-", field);
    }

    @Override
    public String toString() {
        return toSql();
    }
}