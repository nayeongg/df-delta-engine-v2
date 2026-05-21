package kr.ac.df.common.hash;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import kr.ac.df.common.enums.DatasetCode;
import kr.ac.df.common.schema.DatasetSchemaRegistry;
import kr.ac.df.common.schema.DatasetSchemaRegistry.DatasetSchema;
import kr.ac.df.common.validation.DatasetRecordValidator;
import kr.ac.df.common.validation.DatasetValidationException;
import org.apache.commons.codec.digest.DigestUtils;

public final class RecordHashUtil {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true)
            .configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);

    private RecordHashUtil() {
    }

    public static String generateHash(Object record) {
        if (record == null) {
            throw new IllegalArgumentException("record must not be null");
        }
        try {
            JsonNode jsonNode = OBJECT_MAPPER.valueToTree(record);
            JsonNode canonicalNode = sortJsonNode(jsonNode);
            String canonicalJson = OBJECT_MAPPER.writeValueAsString(canonicalNode);
            return DigestUtils.sha256Hex(canonicalJson);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Failed to generate hash", e);
        }
    }

    public static String generateHash(Object record, DatasetCode datasetCode) {
        Objects.requireNonNull(record, "record must not be null");
        Objects.requireNonNull(datasetCode, "datasetCode must not be null");

        Map<String, Object> recordMap = toMap(record);
        DatasetSchema schema = DatasetSchemaRegistry.getSchema(datasetCode);

        DatasetRecordValidator.validateRecord(datasetCode, recordMap, false);

        String canonicalString = schema.allowedFields().stream()
                .map(field -> normalizeValue(recordMap.get(field)))
                .collect(Collectors.joining("|"));

        return DigestUtils.sha256Hex(canonicalString);
    }

    public static String generateRecordKey(Object record, DatasetCode datasetCode) {
        Objects.requireNonNull(record, "record must not be null");
        Objects.requireNonNull(datasetCode, "datasetCode must not be null");

        Map<String, Object> recordMap = toMap(record);
        DatasetSchema schema = DatasetSchemaRegistry.getSchema(datasetCode);

        DatasetRecordValidator.validateRecord(datasetCode, recordMap, false);

        List<String> keyValues = new ArrayList<>(schema.pkFields().size());
        for (String pkField : schema.pkFields()) {
            Object value = recordMap.get(pkField);
            if (value == null) {
                throw new DatasetValidationException(
                        "Missing PK field '" + pkField + "' for dataset " + datasetCode
                                + ". record keys=" + recordMap.keySet(),
                        datasetCode,
                        schema.pkFields()
                );
            }
            keyValues.add(String.valueOf(value));
        }

        return keyValues.stream().collect(Collectors.joining("|"));
    }

    private static Map<String, Object> toMap(Object record) {
        if (record instanceof Map<?, ?>) {
            @SuppressWarnings("unchecked")
            Map<String, Object> typedMap = (Map<String, Object>) record;
            return typedMap;
        }
        return OBJECT_MAPPER.convertValue(record, new TypeReference<Map<String, Object>>() {
        });
    }

    private static String normalizeValue(Object value) {
        if (value == null) {
            return "";
        }

        if (value instanceof String str) {
            return str.trim();
        }

        if (value instanceof Number num) {
            return new BigDecimal(num.toString()).toPlainString();
        }

        return value.toString().trim();
    }

    private static JsonNode sortJsonNode(JsonNode node) {
        if (node == null || node.isNull() || node.isValueNode()) {
            return node;
        }

        if (node.isObject()) {
            ObjectNode sorted = JsonNodeFactory.instance.objectNode();
            List<String> fieldNames = new ArrayList<>();
            Iterator<String> iterator = node.fieldNames();
            iterator.forEachRemaining(fieldNames::add);
            fieldNames.sort(Comparator.naturalOrder());
            for (String fieldName : fieldNames) {
                sorted.set(fieldName, sortJsonNode(node.get(fieldName)));
            }
            return sorted;
        }

        if (node.isArray()) {
            ArrayNode arrayNode = JsonNodeFactory.instance.arrayNode();
            for (JsonNode item : node) {
                arrayNode.add(sortJsonNode(item));
            }
            return arrayNode;
        }

        return node;
    }
}
