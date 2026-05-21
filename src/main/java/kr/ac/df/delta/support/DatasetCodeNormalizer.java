package kr.ac.df.delta.support;

import java.util.Locale;

public final class DatasetCodeNormalizer {

    private DatasetCodeNormalizer() {
    }

    public static String normalize(String datasetCode) {
        if (datasetCode == null) {
            return null;
        }

        String normalized = datasetCode.trim().toUpperCase(Locale.ROOT);
        return switch (normalized) {
            case "STUDENT", "STUDENT_BASIC" -> "STDNT_BASIC";
            case "GRADE" -> "GRADE_RESULT";
            case "ENROLLMENT" -> "ENROLL_INFO";
            case "COURSE" -> "COURSE_INFO";
            default -> normalized;
        };
    }
}
