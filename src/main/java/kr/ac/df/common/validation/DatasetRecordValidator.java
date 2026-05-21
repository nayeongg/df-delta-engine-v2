package kr.ac.df.common.validation;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import kr.ac.df.common.enums.DatasetCode;
import kr.ac.df.common.schema.DatasetSchemaRegistry;
import kr.ac.df.common.schema.DatasetSchemaRegistry.DatasetSchema;

public final class DatasetRecordValidator {

    private DatasetRecordValidator() {
    }

    public static void validateRecord(DatasetCode datasetCode, Map<String, Object> record, boolean rejectUnknownFields) {
        DatasetSchema schema = DatasetSchemaRegistry.getSchema(datasetCode);

        for (String requiredField : schema.requiredFields()) {
            Object value = record.get(requiredField);
            if (value == null) {
                throw new DatasetValidationException(
                        "Missing PK field '" + requiredField + "' for dataset " + datasetCode
                                + ". record keys=" + sortedKeys(record),
                        datasetCode,
                        schema.pkFields()
                );
            }
        }

        if (rejectUnknownFields) {
            Set<String> unknown = new LinkedHashSet<>(record.keySet());
            unknown.removeAll(schema.allowedFields());
            if (!unknown.isEmpty()) {
                throw new DatasetValidationException(
                        "Unknown fields for dataset " + datasetCode + ": " + new ArrayList<>(unknown),
                        datasetCode,
                        schema.pkFields()
                );
            }
        }
    }

    private static List<String> sortedKeys(Map<String, Object> record) {
        List<String> keys = new ArrayList<>(record.keySet());
        keys.sort(String::compareTo);
        return keys;
    }
}
