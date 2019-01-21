package test.sales.gen;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FilterLogicExpr implements FilterExpr {
    public static final String OP_AND = "and";
    public static final String OP_OR = "or";
    public static final String OP_NOT = "not";

    public String op = null;
    public List<FilterExpr> data = null;

    public FilterLogicExpr() {
        op = OP_AND;
        data = new ArrayList<FilterExpr>();
    }

    public FilterLogicExpr(FilterExpr... data) {
        this(OP_AND, data);
    }

    public FilterLogicExpr(String op, FilterExpr... data) {
        this.op = op;
        this.data = new ArrayList<FilterExpr>();
        for (FilterExpr datum : data)
            if (datum != null)
                this.data.add(datum);
    }

    public FilterLogicExpr not() {
        return new FilterLogicExpr(FilterLogicExpr.OP_NOT, this);
    }

    public FilterLogicExpr append(FilterExpr e) {
        data.add(e);
        return this;
    }

    public FilterLogicExpr equalsString(String field, String value) {
        append(FilterConditionExpr.equalsString(field, value));
        return this;
    }

    public FilterLogicExpr containsString(String field, String value) {
        append(FilterConditionExpr.containsString(field, value));
        return this;
    }

    public FilterLogicExpr startString(String field, String value) {
        append(FilterConditionExpr.startString(field, value));
        return this;
    }

    public FilterLogicExpr endString(String field, String value) {
        append(FilterConditionExpr.endString(field, value));
        return this;
    }

    public FilterLogicExpr gtDate(String field, Date value) {
        append(FilterConditionExpr.gtDate(field, value));
        return this;
    }

    public FilterLogicExpr geDate(String field, Date value) {
        append(FilterConditionExpr.geDate(field, value));
        return this;
    }

    public FilterLogicExpr ltDate(String field, Date value) {
        append(FilterConditionExpr.ltDate(field, value));
        return this;
    }

    public FilterLogicExpr leDate(String field, Date value) {
        append(FilterConditionExpr.leDate(field, value));
        return this;
    }

    public FilterLogicExpr equalsNumber(String field, Integer value) {
        append(FilterConditionExpr.equalsNumber(field, value));
        return this;
    }

    public FilterLogicExpr gtNumber(String field, Integer value) {
        append(FilterConditionExpr.gtNumber(field, value));
        return this;
    }

    public FilterLogicExpr geNumber(String field, Integer value) {
        append(FilterConditionExpr.geNumber(field, value));
        return this;
    }

    public FilterLogicExpr ltNumber(String field, Integer value) {
        append(FilterConditionExpr.ltNumber(field, value));
        return this;
    }

    public FilterLogicExpr leNumber(String field, Integer value) {
        append(FilterConditionExpr.leNumber(field, value));
        return this;
    }

    public FilterLogicExpr equalsField(String field1, String field2) {
        append(FilterConditionExpr.equalsField(field1, field2));
        return this;
    }

    public FilterLogicExpr existsObject(String obj, FilterLogicExpr expr) {
        append(FilterConditionExpr.existsObject(obj, expr));
        return this;
    }
}