package sg.edu.nus.iss.phoenix.user.controller;

/**
 * Created by NguyenTrung on 20/9/16.
 */
public class ReturnCode {
    public static final int SUCCESS = 1;
    public static final int FAIL = 0;
    public static final int USER_DUPLICATED = -1001;
    public static final int USER_NOT_FOUND = -1000;
    public static final int USER_HAS_NO_ROLE = -1002;
}
