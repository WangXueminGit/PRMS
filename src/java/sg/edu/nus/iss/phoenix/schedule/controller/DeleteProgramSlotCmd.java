package sg.edu.nus.iss.phoenix.schedule.controller;

import at.nocturne.api.Action;
import at.nocturne.api.Perform;
import sg.edu.nus.iss.phoenix.radioprogram.delegate.ProgramDelegate;
import sg.edu.nus.iss.phoenix.radioprogram.delegate.ReviewSelectProgramDelegate;
import sg.edu.nus.iss.phoenix.radioprogram.entity.RadioProgram;
import sg.edu.nus.iss.phoenix.schedule.delegate.ReviewSelectScheduledProgramDelegate;
import sg.edu.nus.iss.phoenix.schedule.delegate.ScheduleDelegate;
import sg.edu.nus.iss.phoenix.schedule.entity.ProgramSlot;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.util.List;

/**
 * Added by Xuemin on 15/09/20.
 */
@Action("deleteps")
public class DeleteProgramSlotCmd implements Perform {
    @Override
    public String perform(String path, HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        ScheduleDelegate del = new ScheduleDelegate();
        String dateOfPr = req.getParameter("dateOfProgram");
        Date dateOfProgram=Date.valueOf(dateOfPr);
        String stat=req.getParameter("startTime");
        Time startTime=Time.valueOf(stat);
        String dur=req.getParameter("duration");
        Time duration=Time.valueOf(dur);
        try {
            del.processDelete(startTime,dateOfProgram);
        }catch (IllegalArgumentException ilm){
            req.setAttribute("err_message",ilm.getMessage());
            return "/pages/error.jsp";
        }


        ReviewSelectScheduledProgramDelegate rsDel = new ReviewSelectScheduledProgramDelegate();
        List<ProgramSlot> data = rsDel.reviewSelectScheduledProgram();
        req.setAttribute("psl", data);
        return "/pages/scheduleProgramList.jsp";
    }
}
