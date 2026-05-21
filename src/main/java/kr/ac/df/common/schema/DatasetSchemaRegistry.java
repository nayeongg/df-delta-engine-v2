package kr.ac.df.common.schema;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import kr.ac.df.common.enums.DatasetCode;

public final class DatasetSchemaRegistry {

    private static final Map<DatasetCode, DatasetSchema> SCHEMA_BY_DATASET = new EnumMap<>(DatasetCode.class);

    static {
        SCHEMA_BY_DATASET.put(
                DatasetCode.STDNT_BASIC,
                new DatasetSchema(
                        List.of("KADF_STUDENT_ID", "KADF_UNIV_CD"),
                        List.of(
                                "KADF_STUDENT_ID", "KADF_UNIV_CD", "KADF_KOR_NM", "KADF_ENG_NM",
                                "KADF_GENDER", "KADF_BIRTH_DT", "KADF_MOBILE_NO", "KADF_USER_DIV",
                                "KADF_REG_STAT", "KADF_COLLEGE_NM", "KADF_DEPT_NM", "KADF_EMAIL",
                                "KADF_STD_GRADE", "KADF_SEMESTER_CNT", "KADF_DISABLED_YN", "KADF_EXCHANGE_YN"
                        )
                )
        );

        SCHEMA_BY_DATASET.put(
                DatasetCode.COURSE_INFO,
                new DatasetSchema(
                        List.of("KADF_YEAR", "KADF_TERM", "KADF_UNIV_CD", "KADF_COURSE_CD", "KADF_CLASS_DIV"),
                        List.of(
                                "KADF_YEAR", "KADF_TERM", "KADF_UNIV_CD", "KADF_COURSE_CD", "KADF_CLASS_DIV",
                                "KADF_COURSE_NM_KO", "KADF_COURSE_NM_EN", "KADF_DEPT_NM", "KADF_CREDIT",
                                "KADF_COMP_DIV_NM", "KADF_PROF_NM", "KADF_PROF_ORD", "KADF_DEGREE_DIV"
                        )
                )
        );

        SCHEMA_BY_DATASET.put(
                DatasetCode.ENROLL_INFO,
                new DatasetSchema(
                        List.of("KADF_UNIV_CD", "KADF_STUDENT_ID", "KADF_YEAR", "KADF_TERM", "KADF_COURSE_CD"),
                        List.of(
                                "KADF_UNIV_CD", "KADF_STUDENT_ID", "KADF_YEAR", "KADF_TERM", "KADF_COURSE_CD",
                                "KADF_DEGREE_DIV", "KADF_CLASS_DIV", "KADF_COURSE_NM_KO", "KADF_COURSE_NM_EN",
                                "KADF_COMP_DIV_NM", "KADF_CREDIT", "KADF_RETAKE_YN"
                        )
                )
        );

        SCHEMA_BY_DATASET.put(
                DatasetCode.GRADE_RESULT,
                new DatasetSchema(
                        List.of("KADF_STUDENT_ID", "KADF_UNIV_CD", "KADF_YEAR", "KADF_TERM", "KADF_COURSE_CD"),
                        List.of(
                                "KADF_STUDENT_ID", "KADF_UNIV_CD", "KADF_YEAR", "KADF_TERM", "KADF_COURSE_CD",
                                "KADF_DEGREE_DIV", "KADF_CLASS_DIV", "KADF_COURSE_NM_KO", "KADF_COURSE_NM_EN",
                                "KADF_SCORE", "KADF_GRADE_CD", "KADF_COMP_DIV_NM", "KADF_CREDIT", "KADF_RETAKE_YN"
                        )
                )
        );
    }

    private DatasetSchemaRegistry() {
    }

    public static DatasetSchema getSchema(DatasetCode datasetCode) {
        DatasetSchema schema = SCHEMA_BY_DATASET.get(datasetCode);
        if (schema == null) {
            throw new IllegalArgumentException("No schema registered for dataset: " + datasetCode);
        }
        return schema;
    }

    public record DatasetSchema(List<String> pkFields, List<String> allowedFields) {
        public List<String> requiredFields() {
            return pkFields;
        }

        public List<String> optionalFields() {
            return allowedFields.stream().filter(field -> !pkFields.contains(field)).toList();
        }
    }
}
