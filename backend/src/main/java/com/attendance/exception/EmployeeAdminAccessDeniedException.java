package com.attendance.exception;

public class EmployeeAdminAccessDeniedException extends RuntimeException {

    public static final String ERROR_CODE = "EMPLOYEE_ACCOUNT_NO_ADMIN_ACCESS";

    public EmployeeAdminAccessDeniedException() {
        super("此帳號為員工帳號，無法管理後台");
    }
}
