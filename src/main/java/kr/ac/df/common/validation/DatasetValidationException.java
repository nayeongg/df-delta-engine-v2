package kr.ac.df.common.validation;

import java.util.List;
import kr.ac.df.common.enums.DatasetCode;

public class DatasetValidationException extends RuntimeException {

    private final DatasetCode datasetCode;
    private final List<String> requiredPkFields;

    public DatasetValidationException(String message, DatasetCode datasetCode, List<String> requiredPkFields) {
        super(message);
        this.datasetCode = datasetCode;
        this.requiredPkFields = requiredPkFields;
    }

    public DatasetCode getDatasetCode() {
        return datasetCode;
    }

    public List<String> getRequiredPkFields() {
        return requiredPkFields;
    }
}
