package sg.edu.nus.iss.phoenix.user.controller;

import at.nocturne.api.*;
import sg.edu.nus.iss.phoenix.authenticate.entity.Role;
import sg.edu.nus.iss.phoenix.user.delegate.UserDelegate;
import sg.edu.nus.iss.phoenix.user.service.UserService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by NguyenTrung on 9/9/16.
 */

@Action("addedituser")
public class AddEditUserCmd implements Perform {
    @Override
    public String perform(String s, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException, ServletException {
        UserDelegate delegate = new UserDelegate();
        String selectedId = httpServletRequest.getParameter("id");
        List<Role> roleList = delegate.getAllRoles();
        httpServletRequest.setAttribute("roles",roleList);
        return "/pages/setupUserPage.jsp";
    }
}
