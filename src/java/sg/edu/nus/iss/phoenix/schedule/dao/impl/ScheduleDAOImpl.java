package sg.edu.nus.iss.phoenix.schedule.dao.impl;

import sg.edu.nus.iss.phoenix.authenticate.entity.Role;
import sg.edu.nus.iss.phoenix.core.dao.DBConstants;
import sg.edu.nus.iss.phoenix.core.exceptions.NotFoundException;
import sg.edu.nus.iss.phoenix.radioprogram.entity.RadioProgram;
import sg.edu.nus.iss.phoenix.schedule.dao.ScheduleDAO;
import sg.edu.nus.iss.phoenix.schedule.entity.ProgramSlot;
import sg.edu.nus.iss.phoenix.user.entity.Presenter;
import sg.edu.nus.iss.phoenix.user.entity.Producer;

import java.sql.*;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Created by yao on 15/09/16.
 */
public class ScheduleDAOImpl implements ScheduleDAO{
    Connection connection;

    /* (non-Javadoc)
     * @see sg.edu.nus.iss.phoenix.radioprogram.dao.impl.ProgramDAO#createValueObject()
     */
    @Override
    public ProgramSlot createValueObject() {
        return new ProgramSlot();
    }

    /* (non-Javadoc)
     * @see sg.edu.nus.iss.phoenix.radioprogram.dao.impl.ProgramDAO#getObject(java.lang.String)
     */
    @Override
    public ProgramSlot getObject(Time startTime, Date dateOfProgram) throws NotFoundException,
            SQLException {

        ProgramSlot valueObject = createValueObject();
        valueObject.setStartTime(startTime);
        valueObject.setDateOfProgram(dateOfProgram);
        load(valueObject);
        return valueObject;
    }

    /* (non-Javadoc)
     * @see sg.edu.nus.iss.phoenix.radioprogram.dao.impl.ProgramDAO#load(sg.edu.nus.iss.phoenix.radioprogram.entity.RadioProgram)
     */
    @Override
    public void load(ProgramSlot valueObject) throws NotFoundException,
            SQLException {

        if (valueObject.getStartTime() == null || valueObject.getDateOfProgram() == null) {
            // System.out.println("Can not select without Primary-Key!");
            throw new NotFoundException("Can not select without Primary-Key!");
        }

        String sql = "SELECT * FROM `program-slot` WHERE (`startTime` = ? AND `dateOfProgram` = ?); ";
        PreparedStatement stmt = null;
        openConnection();
        try {
            stmt = connection.prepareStatement(sql);
            stmt.setTime(1, valueObject.getStartTime());
            stmt.setDate(2, valueObject.getDateOfProgram());
            singleQuery(stmt, valueObject);

        } finally {
            if (stmt != null)
                stmt.close();
            closeConnection();
        }
    }

    /* (non-Javadoc)
     * @see sg.edu.nus.iss.phoenix.radioprogram.dao.impl.ProgramDAO#loadAll()
     */
    @Override
    public List<ProgramSlot> loadAll() throws SQLException {
        openConnection();
        String sql = "SELECT * FROM phoenix.`program-slot` ORDER BY `dateOfProgram` ASC; ";
        List<ProgramSlot> searchResults = listQuery(connection
                .prepareStatement(sql));
        closeConnection();
        System.out.println("record size"+searchResults.size());
        return searchResults;
    }

    /* (non-Javadoc)
     * @see sg.edu.nus.iss.phoenix.radioprogram.dao.impl.ProgramDAO#create(sg.edu.nus.iss.phoenix.radioprogram.entity.RadioProgram)
     */
    @Override
    public synchronized void create(ProgramSlot valueObject)
            throws SQLException {

        String sql = " ";
        PreparedStatement stmt = null;
        openConnection();
        try {
            sql = "INSERT INTO `program-slot` (`duration`, `dateOfProgram`, `startTime`, `program-name`, `presenter`, `producer`) VALUES (?,?,?,?,?,?); ";
            stmt = connection.prepareStatement(sql);
            stmt.setTime(1, valueObject.getDuration());
            stmt.setDate(2, valueObject.getDateOfProgram());
            stmt.setTime(3, valueObject.getStartTime());
            stmt.setString(4, valueObject.getRadioProgram().getName());
            stmt.setString(5, valueObject.getPresenter().getId());
            stmt.setString(6, valueObject.getProducer().getId());
            int rowcount = databaseUpdate(stmt);
            if (rowcount != 1) {
                throw new SQLException("PrimaryKey Error when updating DB!");
            }

        } finally {
            if (stmt != null)
                stmt.close();
            closeConnection();
        }

    }

    /* (non-Javadoc)
     * @see sg.edu.nus.iss.phoenix.radioprogram.dao.impl.ProgramDAO#save(sg.edu.nus.iss.phoenix.radioprogram.entity.RadioProgram)
     */
    @Override
    public void save(ProgramSlot valueObject) throws NotFoundException,
            SQLException {

        String sql = "UPDATE `program-slot` SET `program-name` = ?, `presenter` = ?, `producer` = ? WHERE (`startTime` = ? AND `dateOfProgram` = ?); ";
        PreparedStatement stmt = null;
        openConnection();
        try {
            stmt = connection.prepareStatement(sql);
            stmt.setString(1, valueObject.getRadioProgram().getName());
            stmt.setString(2, valueObject.getPresenter().getId());
            stmt.setString(3, valueObject.getProducer().getId());

            stmt.setTime(4, valueObject.getStartTime());
            stmt.setDate(5, valueObject.getDateOfProgram());

            int rowcount = databaseUpdate(stmt);
            if (rowcount == 0) {
                throw new NotFoundException(
                        "Object could not be saved! (PrimaryKey not found)");
            }
            if (rowcount > 1) {
                throw new SQLException(
                        "PrimaryKey Error when updating DB! (Many objects were affected!)");
            }
        } finally {
            if (stmt != null)
                stmt.close();
            closeConnection();
        }
    }

    /* (non-Javadoc)
     * @see sg.edu.nus.iss.phoenix.radioprogram.dao.impl.ProgramDAO#delete(sg.edu.nus.iss.phoenix.radioprogram.entity.RadioProgram)
     */
    @Override
    public void delete(ProgramSlot valueObject) throws NotFoundException,
            SQLException {

        if (valueObject.getDuration() == null || valueObject.getDateOfProgram() == null) {
            throw new NotFoundException("Can not delete without Primary-Key!");
        }

        String sql = "DELETE FROM `program-slot` WHERE (`startTime` = ? AND `dateOfProgram` = ?); ";
        PreparedStatement stmt = null;
        openConnection();
        try {
            stmt = connection.prepareStatement(sql);
            stmt.setTime(1, valueObject.getStartTime());
            stmt.setDate(2, valueObject.getDateOfProgram());

            int rowcount = databaseUpdate(stmt);
            if (rowcount == 0) {
                throw new NotFoundException(
                        "Object could not be deleted! (PrimaryKey not found)");
            }
            if (rowcount > 1) {
                throw new SQLException(
                        "PrimaryKey Error when updating DB! (Many objects were deleted!)");
            }
        } finally {
            if (stmt != null)
                stmt.close();
            closeConnection();
        }
    }

    /* (non-Javadoc)
     * @see sg.edu.nus.iss.phoenix.radioprogram.dao.impl.ProgramDAO#deleteAll(java.sql.Connection)
     */
    @Override
    public void deleteAll(Connection conn) throws SQLException {

        String sql = "DELETE FROM `program-slot`";
        PreparedStatement stmt = null;
        openConnection();
        try {
            stmt = connection.prepareStatement(sql);
            int rowcount = databaseUpdate(stmt);
            System.out.println(""+rowcount);
        } finally {
            if (stmt != null)
                stmt.close();
            closeConnection();
        }
    }

    /* (non-Javadoc)
     * @see sg.edu.nus.iss.phoenix.radioprogram.dao.impl.ProgramDAO#countAll()
     */
    @Override
    public int countAll() throws SQLException {

        String sql = "SELECT count(*) FROM `program-slot`";
        PreparedStatement stmt = null;
        ResultSet result = null;
        int allRows = 0;
        openConnection();
        try {
            stmt = connection.prepareStatement(sql);
            result = stmt.executeQuery();

            if (result.next())
                allRows = result.getInt(1);
        } finally {
            if (result != null)
                result.close();
            if (stmt != null)
                stmt.close();
            closeConnection();
        }
        return allRows;
    }

    /* (non-Javadoc)
     * @see sg.edu.nus.iss.phoenix.radioprogram.dao.impl.ProgramDAO#searchMatching(sg.edu.nus.iss.phoenix.radioprogram.entity.RadioProgram)
     */
    @Override
    public List<ProgramSlot> searchMatching(ProgramSlot valueObject) throws SQLException {

        @SuppressWarnings("UnusedAssignment")
        List<ProgramSlot> searchResults = new ArrayList<>();
        openConnection();
        boolean first = true;
        StringBuilder sql = new StringBuilder(
                "SELECT * FROM `program-slot` WHERE 1=1 ");

        if (valueObject.getDuration() != null) {
            if (first) {
                first = false;
            }
            sql.append("AND `duration` = '").append(valueObject.getDuration())
                    .append("' ");
        }

        if (valueObject.getDateOfProgram() != null) {
            if (first) {
                first =false;
            }
            sql.append("AND `dateOfProgram` = '").append(valueObject.getDateOfProgram()).append("' ");
        }

        if (valueObject.getStartTime() != null) {
            if (first) {
                first = false;
            }
            sql.append("AND `startTime` = '").append(valueObject.getStartTime())
                    .append("' ");
        }

        if (valueObject.getRadioProgram() != null) {
            if (first) {
                first = false;
            }
            sql.append("AND `program-name` LIKE '").append(valueObject.getRadioProgram().getName())
                    .append("%' ");
        }

        if (valueObject.getPresenter() != null) {
            if (first) {
                first = false;
            }
            sql.append("AND `presenter` LIKE '")
                    .append(valueObject.getPresenter().getName()).append("%' ");
        }

        if (valueObject.getProducer() != null) {
            if (first) {
                first = false;
            }
            sql.append("AND `producer` LIKE '").append(valueObject.getProducer().getName()).append("%' ");
        }

        sql.append("ORDER BY `dateOfProgram` ASC ");

        // Prevent accidential full table results.
        // Use loadAll if all rows must be returned.
        if (first)
            searchResults = new ArrayList<>();
        else
            searchResults = listQuery(connection.prepareStatement(sql
                    .toString()));
        closeConnection();
        return searchResults;
    }

    /**
     * databaseUpdate-method. This method is a helper method for internal use.
     * It will execute all database handling that will change the information in
     * tables. SELECT queries will not be executed here however. The return
     * value indicates how many rows were affected. This method will also make
     * sure that if cache is used, it will reset when data changes.
     *
     * @param stmt
     *            This parameter contains the SQL statement to be excuted.
     * @return
     * @throws java.sql.SQLException
     */
    protected int databaseUpdate(PreparedStatement stmt) throws SQLException {

        int result = stmt.executeUpdate();

        return result;
    }

    /**
     * databaseQuery-method. This method is a helper method for internal use. It
     * will execute all database queries that will return only one row. The
     * resultset will be converted to valueObject. If no rows were found,
     * NotFoundException will be thrown.
     *
     * @param stmt
     *            This parameter contains the SQL statement to be excuted.
     * @param valueObject
     *            Class-instance where resulting data will be stored.
     * @throws sg.edu.nus.iss.phoenix.core.exceptions.NotFoundException
     * @throws java.sql.SQLException
     */
    protected void singleQuery(PreparedStatement stmt, ProgramSlot valueObject)
            throws NotFoundException, SQLException {
        RadioProgram radioProgram;
        Presenter presenter;
        Producer producer;
        ResultSet result = null;
        openConnection();
        try {
            result = stmt.executeQuery();

            if (result.next()) {
                valueObject.setDuration(result.getTime("duration"));
                valueObject.setDateOfProgram(result.getDate("dateOfProgram"));
                radioProgram = new RadioProgram(result.getString("program-name"));
                valueObject.setRadioProgram(radioProgram);
                presenter = new Presenter(result.getString("presenter"));
                valueObject.setPresenter(presenter);
                producer = new Producer(result.getString("producer"));
                valueObject.setProducer(producer);
            } else {
                throw new NotFoundException("ProgramSlot Object Not Found!");
            }
        } finally {
            if (result != null)
                result.close();
            if (stmt != null)
                stmt.close();
            closeConnection();
        }
    }

    /**
     * databaseQuery-method. This method is a helper method for internal use. It
     * will execute all database queries that will return multiple rows. The
     * resultset will be converted to the List of valueObjects. If no rows were
     * found, an empty List will be returned.
     *
     * @param stmt
     *            This parameter contains the SQL statement to be excuted.
     * @return
     * @throws java.sql.SQLException
     */
    protected List<ProgramSlot> listQuery(PreparedStatement stmt) throws SQLException {
        ArrayList<ProgramSlot> searchResults = new ArrayList<>();
        RadioProgram radioProgram;
        Presenter presenter;
        Producer producer;
        ResultSet result = null;
        openConnection();
        try {
            result = stmt.executeQuery();

            while (result.next()) {
                ProgramSlot temp = createValueObject();

                temp.setDuration(result.getTime("duration"));
                temp.setDateOfProgram(result.getDate("dateOfProgram"));

                radioProgram = new RadioProgram(result.getString("program-name"));
                temp.setRadioProgram(radioProgram);
                presenter = new Presenter(result.getString("presenter"));
                temp.setPresenter(presenter);
                producer = new Producer(result.getString("producer"));
                temp.setProducer(producer);

                searchResults.add(temp);
            }

        } finally {
            if (result != null)
                result.close();
            if (stmt != null)
                stmt.close();
            closeConnection();
        }
       return (List<ProgramSlot>) searchResults;
    }

//    private RadioProgram createRP(PreparedStatement stmt, String program_name) throws SQLException {
//        openConnection();
//        ResultSet result = null;
//        String sql = "SELECT * FROM phoenix.`radio-program` WHERE `name` = ?";
//        RadioProgram program = new RadioProgram();
//        try {
//            stmt = connection.prepareStatement(sql);
//            stmt.setString(1, program_name);
//            result = stmt.executeQuery();
//            while (result.next()) {
//                program.setDescription(result.getString("desc"));
//                program.setName(result.getString("name"));
//                program.setTypicalDuration(result.getTime("typicalDuration"));
//            }
//        } finally {
//            result.close();
//            closeConnection();
//        }
//        return program;
//    }
//
//    private Presenter setPresenterValue(PreparedStatement stmt, String presenter_name) throws SQLException {
//        openConnection();
//        ResultSet result = null;
//        String sql = "SELECT * FROM phoenix.`user` WHERE `id` = ?";
//        Presenter presenter = new Presenter();
//        try {
//            stmt = connection.prepareStatement(sql);
//            stmt.setString(1, presenter_name);
//            result = stmt.executeQuery();
//            while (result.next()) {
//                presenter.setName(result.getString("name"));
//                presenter.setPassword(result.getString("password"));
//                String[] array = result.getString("role").split(":");
//                List<Role> roleList = Arrays.stream(array)
//                        .map(Role::new)
//                        .collect(Collectors.toList());
//                presenter.setRoles((ArrayList<Role>) roleList);
//            }
//        } finally {
//            result.close();
//            closeConnection();
//        }
//        return presenter;
//    }
//
//    private Producer setProducerValue(PreparedStatement stmt, String producer_name) throws SQLException {
//        openConnection();
//        ResultSet result = null;
//        String sql = "SELECT * FROM phoenix.`user` WHERE `id` = ?";
//        Producer producer = new Producer();
//        try {
//            stmt = connection.prepareStatement(sql);
//            stmt.setString(1, producer_name);
//            result = stmt.executeQuery();
//            while (result.next()) {
//                producer.setName(result.getString("name"));
//                producer.setPassword(result.getString("password"));
//                String[] array = result.getString("role").split(":");
//                List<Role> roleList = Arrays.stream(array)
//                        .map(Role::new)
//                        .collect(Collectors.toList());
//                producer.setRoles((ArrayList<Role>) roleList);
//            }
//        } finally {
//            result.close();
//            closeConnection();
//        }
//        return producer;
//    }

    private void openConnection() {
        try {
            Class.forName(DBConstants.COM_MYSQL_JDBC_DRIVER);
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        try {
            this.connection = DriverManager.getConnection(DBConstants.dbUrl,
                    DBConstants.dbUserName, DBConstants.dbPassword);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private void closeConnection() {
        try {
            this.connection.close();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
