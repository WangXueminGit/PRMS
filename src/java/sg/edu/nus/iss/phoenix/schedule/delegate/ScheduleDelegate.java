package sg.edu.nus.iss.phoenix.schedule.delegate;

import sg.edu.nus.iss.phoenix.authenticate.entity.User;
import sg.edu.nus.iss.phoenix.schedule.entity.AnnualSchedule;
import sg.edu.nus.iss.phoenix.schedule.service.ScheduleService;

import java.util.List;

/**
 * Created by yangzai on 10/9/16.
 */
public class ScheduleDelegate {
    private ScheduleService scheduleService;

    public ScheduleDelegate() {
        scheduleService = new ScheduleService();
    }

    public List<AnnualSchedule> processRetrieveAllAnnualSchedule() {
        return scheduleService.processRetrieveAllAnnualSchedule();
    }

    public void processCreateAnnualWeeklySchedule(int year, User user) {
        scheduleService.processCreateAnnualWeeklySchedule(year, user);
    }
}