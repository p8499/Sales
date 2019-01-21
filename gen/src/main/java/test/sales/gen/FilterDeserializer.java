package test.sales.gen;

import java.io.IOException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

public class FilterDeserializer extends JsonDeserializer<FilterExpr> {
    @Override
    public FilterExpr deserialize(JsonParser parser, DeserializationContext context) throws IOException, JsonProcessingException {
        JsonNode node = parser.getCodec().readTree(parser);
        return recursiveDeserialize(node);
    }

    protected FilterExpr recursiveDeserialize(JsonNode node) {
        JsonNode opNode = node.get("op");
        JsonNode dataNode = node.get("data");
        JsonNode isColNode = node.get("isCol");
        if ("and".equals(opNode.asText()) || "or".equals(opNode.asText()) || "not".equals(opNode.asText())) {
            FilterLogicExpr expr = new FilterLogicExpr();
            expr.op = opNode.asText();
            for (JsonNode datumNode : dataNode) {
                expr.data.add(recursiveDeserialize(datumNode));
            }
            return expr;
        } else if ("equal".equals(opNode.asText()) || "greater".equals(opNode.asText()) || "less".equals(opNode.asText()) || "greaterEqual".equals(opNode.asText()) || "lessEqual".equals(opNode.asText()) || "isEmpty".equals(opNode.asText()) || "contain".equals(opNode.asText()) || "startWith".equals(opNode.asText()) || "endWith".equals(opNode.asText()) || "exists".equals(opNode.asText())) {
            FilterConditionExpr expr = new FilterConditionExpr();
            expr.op = opNode.asText();
            for (JsonNode datumNode : dataNode) {
                expr.data.add(recursiveDeserialize(datumNode));
            }
            return expr;
        } else {
            FilterOperandExpr expr = new FilterOperandExpr();
            expr.op = opNode.asText();
            expr.data = dataNode.asText();
            expr.isCol = isColNode == null ? false : isColNode.asBoolean(false);
            return expr;
        }
    }
}