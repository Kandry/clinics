package com.haulmont.testtask.dao;


import com.haulmont.testtask.entity.Doctor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DoctorDAO implements DAO<Doctor>{
    private Connection connection = null;

    protected DoctorDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Doctor persist(Doctor object) throws DAOException {
        Doctor doctor = new Doctor();
        String sql = "insert into DOCTOR (FIRST_NAME, LAST_NAME, MIDDLE_NAME, SPECIALIZATION) " +
                "values (?, ?, ?, ?);";
        try (PreparedStatement st = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            st.setString(1, object.getFirstName());
            st.setString(2, object.getLastName());
            st.setString(3, object.getMiddleName());
            st.setString(4, object.getSpecialization());
            if (st.executeUpdate() == 1) {
                try (ResultSet rs = st.getGeneratedKeys()) {
                    rs.next();
                    doctor.setId(rs.getLong(1));
                    doctor.setFirstName(object.getFirstName());
                    doctor.setLastName(object.getLastName());
                    doctor.setMiddleName(object.getMiddleName());
                    doctor.setSpecialization(object.getSpecialization());
                } catch (SQLException e) {
                    throw e;
                }
            } else {
                throw new SQLException("Creating Doctor failed, no row inserted.");
            }
        } catch (SQLException e) {
            doctor = null;
            throw new DAOException(e);
        }
        return doctor;
    }

    @Override
    public void update(Doctor object) throws DAOException {
        String sql = "update DOCTOR set FIRST_NAME = ?, LAST_NAME = ?, MIDDLE_NAME = ?, SPECIALIZATION = ?" +
                "where ID = ?;";
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setString(1, object.getFirstName());
            st.setString(2, object.getLastName());
            st.setString(3, object.getMiddleName());
            st.setString(4, object.getSpecialization());
            st.setLong(5, object.getId());
            st.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    @Override
    public void delete(Doctor object) throws DAOException {
        String sql = "delete from DOCTOR where ID = ?;";
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setLong(1, object.getId());
            st.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    @Override
    public Doctor getByPrimaryKey(Long key) throws DAOException {
        Doctor doctor = null;
        String sql ="select FIRST_NAME, LAST_NAME, MIDDLE_NAME, SPECIALIZATION from DOCTOR where ID = ?;";
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setLong(1, key);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                doctor = new Doctor();
                doctor.setId(key);
                doctor.setFirstName(rs.getString("FIRST_NAME"));
                doctor.setLastName(rs.getString("LAST_NAME"));
                doctor.setMiddleName(rs.getString("MIDDLE_NAME"));
                doctor.setSpecialization(rs.getString("SPECIALIZATION"));
            }
        } catch (SQLException e) {
            doctor = null;
            throw new DAOException(e);
        }
        return doctor;
    }

    @Override
    public List<Doctor> getAll() throws DAOException {
        List<Doctor> list = new ArrayList<Doctor>();
        String sql ="select ID, FIRST_NAME, LAST_NAME, MIDDLE_NAME, SPECIALIZATION from DOCTOR;";
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                Doctor doctor = new Doctor();
                doctor.setId(rs.getLong("ID"));
                doctor.setFirstName(rs.getString("FIRST_NAME"));
                doctor.setLastName(rs.getString("LAST_NAME"));
                doctor.setMiddleName(rs.getString("MIDDLE_NAME"));
                doctor.setSpecialization(rs.getString("SPECIALIZATION"));
                list.add(doctor);
            }
        } catch (SQLException e) {
            list = null;
            throw new DAOException(e);
        }
        return list;
    }

    public int countPrescriptions(Doctor object) throws  DAOException {
        int counter;
        String sql ="select count(*) from PRESCRIPTION pr WHERE DOCTOR_ID = ?;";
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setLong(1, object.getId());
            ResultSet rs = st.executeQuery();
            rs.next();
            counter = rs.getInt(1);
        } catch (SQLException e) {
            throw new DAOException(e);
        }
        return counter;
    }
}
