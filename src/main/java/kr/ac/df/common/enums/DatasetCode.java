package kr.ac.df.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DatasetCode {
    STDNT_BASIC("VW_STDNT_BASIC", new String[]{"KADF_STUDENT_ID", "KADF_UNIV_CD"}),
    COURSE_INFO("VW_COURSE_INFO", new String[]{"KADF_YEAR", "KADF_TERM", "KADF_UNIV_CD", "KADF_COURSE_CD", "KADF_CLASS_DIV"}),
    ENROLL_INFO("VW_ENROLL_INFO", new String[]{"KADF_UNIV_CD", "KADF_STUDENT_ID", "KADF_YEAR", "KADF_TERM", "KADF_COURSE_CD"}),
    GRADE_RESULT("VW_GRADE_RESULT", new String[]{"KADF_STUDENT_ID", "KADF_UNIV_CD", "KADF_YEAR", "KADF_TERM", "KADF_COURSE_CD"});

    private final String viewName;
    private final String[] pkFields;
}
