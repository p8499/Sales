package test.sales.gen;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class FilterSerializer extends JsonSerializer<FilterExpr> {
    @Override
    public void serialize(FilterExpr expr, JsonGenerator generator, SerializerProvider provider) throws IOException, JsonProcessingException {
        if (expr != null)
            recursiveSerialize(expr, generator);
    }

    private void recursiveSerialize(FilterExpr expr, JsonGenerator generator) throws IOException {
        generator.writeStartObject();
        if (expr instanceof FilterLogicExpr) {
            FilterLogicExpr logic = (FilterLogicExpr) expr;
            generator.writeFieldName("op");
            generator.writeString(logic.op);
            generator.writeFieldName("data");
            generator.writeStartArray();
            for (FilterExpr datum : logic.data) {
                recursiveSerialize(datum, generator);
            }
            generator.writeEndArray();
        } else if (expr instanceof FilterConditionExpr) {
            FilterConditionExpr condition = (FilterConditionExpr) expr;
            generator.writeFieldName("op");
            generator.writeString(condition.op);
            generator.writeFieldName("data");
            generator.writeStartArray();
            for (FilterExpr datum : condition.data) {
                recursiveSerialize(datum, generator);
            }
            generator.writeEndArray();
        } else if (expr instanceof FilterOperandExpr) {
            FilterOperandExpr operand = (FilterOperandExpr) expr;
            generator.writeFieldName("op");
            generator.writeString(operand.op);
            generator.writeFieldName("data");
            generator.writeString(operand.data);
            generator.writeFieldName("isCol");
            generator.writeBoolean(operand.isCol);
        }
        generator.writeEndObject();
    }
}