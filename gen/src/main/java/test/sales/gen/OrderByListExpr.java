package test.sales.gen;

import java.util.ArrayList;
import java.util.List;

public class OrderByListExpr {
    public List<OrderByExpr> specs = null;

    public OrderByListExpr() {
    }

    public OrderByListExpr(List<OrderByExpr> orderBys) {
        this.specs = orderBys;
    }

    public static OrderByListExpr fromSql(String s) {
        OrderByListExpr expr = new OrderByListExpr();
        if (s != null) {
            String[] ss = s.split(",");
            for (String si : ss)
                expr.append(OrderByExpr.fromSql(si));
        }
        return expr;
    }

    public static OrderByListExpr fromQuery(String s) {
        OrderByListExpr expr = new OrderByListExpr();
        if (s != null) {
            String[] ss = s.split(",");
            for (String si : ss)
                expr.append(OrderByExpr.fromQuery(si));
        }
        return expr;
    }

    public OrderByListExpr append(OrderByExpr o) {
        if (specs == null)
            specs = new ArrayList<>();
        specs.add(o);
        return this;
    }

    public OrderByListExpr append(String field, boolean asc) {
        return append(new OrderByExpr(field, asc));
    }

    public String toSql() {
        if (specs == null)
            return null;
        else {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < specs.size(); i++) {
                sb.append(specs.get(i).toSql());
                if (i < specs.size() - 1)
                    sb.append(",");
            }
            return sb.toString();
        }
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
        return toSql();
    }
}