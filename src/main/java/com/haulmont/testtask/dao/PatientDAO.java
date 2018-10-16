package com.haulmont.testtask.dao;


import com.haulmont.testtask.entity.Patient;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PatientDAO implements DAO<Patient>{

    private Connection connection = null;

    protected PatientDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Patient persist(Patient object) throws DAOException {
        Patient student = new Patient();
        String sql = "insert into PATIENT (FIRST_NAME, LAST_NAME, MIDDLE_NAME, PHONE_NUMBER) " +
                "values (?, ?, ?, ?);";
        try (PreparedStatement st = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            st.setString(1, object.getFirstName());
            st.setString(2, object.getLastName());
            st.setString(3, object.getMiddleName());
            st.setString(4, object.getPhoneNumber());
            if (st.executeUpdate() == 1) {
                try (ResultSet rs = st.getGeneratedKeys()) {
                    rs.next();
                    student.setId(rs.getLong(1));
                    student.setFirstName(object.getFirstName());
                    student.setLastName(object.getLastName());
                    student.setMiddleName(object.getMiddleName());
                    student.setPhoneNumber(object.getPhoneNumber());
                } catch (SQLException e) {
                    throw e;
                }
            } else {
                throw new SQLException("Creating Patient failed, no row inserted.");
            }
        } catch (SQLException e) {
            student = null;
            throw new DAOException(e);
        }
        return student;
    }

    @Override
    public void update(Patient object) throws DAOException {
        String sql = "update PATIENT set FIRST_NAME = ?, LAST_NAME = ?, MIDDLE_NAME = ?, PHONE_NUMBER = ?" +
                "where ID = ?;";
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setString(1, object.getFirstName());
            st.setString(2, object.getLastName());
            st.setString(3, object.getMiddleName());
            st.setString(4, object.getPhoneNumber());
            st.setLong(5, object.getId());
            st.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    @Override
    public void delete(Patient object) throws DAOException {
        String sql = "delete from PATIENT where ID = ?;";
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setLong(1, object.getId());
            st.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    @Override
    public Patient getByPrimaryKey(Long key) throws DAOException {
        Patient patient = null;
        String sql ="select FIRST_NAME, LAST_NAME, MIDDLE_NAME, PHONE_NUMBER from PATIENT where ID = ?;";
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setLong(1, key);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                patient = new Patient();
                patient.setId(key);
                patient.setFirstName(rs.getString("FIRST_NAME"));
                patient.setLastName(rs.getString("LAST_NAME"));
                patient.setMiddleName(rs.getString("MIDDLE_NAME"));
                patient.setPhoneNumber(rs.getString("PHONE_NUMBER"));
            }
        } catch (SQLException e) {
            patient = null;
            throw new DAOException(e);
        }
        return patient;
    }

    @Override
    public List<Patient> getAll() throws DAOException {
        List<Patient> list = new ArrayList<Patient>();
        String sql ="select ID, FIRST_NAME, LAST_NAME, MIDDLE_NAME, PHONE_NUMBER from PATIENT;";
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                Patient patient = new Patient();
                patient.setId(rs.getLong("ID"));
                patient.setFirstName(rs.getString("FIRST_NAME"));
                patient.setLastName(rs.getString("LAST_NAME"));
                patient.setMiddleName(rs.getString("MIDDLE_NAME"));
                patient.setPhoneNumber(rs.getString("PHONE_NUMBER"));
                list.add(patient);
            }
        } catch (SQLException e) {
            list = null;
            throw new DAOException(e);
        }
        return list;
    }
}
