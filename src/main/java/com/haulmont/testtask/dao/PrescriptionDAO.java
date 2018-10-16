package com.haulmont.testtask.dao;


import com.haulmont.testtask.entity.Doctor;
import com.haulmont.testtask.entity.Patient;
import com.haulmont.testtask.entity.Prescription;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PrescriptionDAO implements DAO<Prescription>{

    private Connection connection = null;

    protected PrescriptionDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Prescription persist(Prescription object) throws DAOException {
        Prescription prescription = new Prescription();
        String sql = "insert into PRESCRIPTION (DESCRIPTION, PATIENT_ID, DOCTOR_ID, CREATE_DATE, EXPIRATION, PRIORITY) " +
                "values (?, ?, ?, ?, ?, ?);";
        try (PreparedStatement st = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            st.setString(1, object.getDescription());
            Long patientId = null;
            if (object.getPatient() != null) {
                patientId = object.getPatient().getId();
            }
            st.setLong(2, patientId);
            Long doctortId = null;
            if (object.getPatient() != null) {
                doctortId = object.getDoctor().getId();
            }
            st.setLong(3, doctortId);
            Date createDate = null;
            if (object.getCreateDate() != null) {
                createDate = new Date(object.getCreateDate().getTime());
            }
            st.setDate(4, createDate);
            st.setInt(5, object.getExpiration());
            st.setString(6, object.getPriority());
            if (st.executeUpdate() == 1) {
                try (ResultSet rs = st.getGeneratedKeys()) {
                    rs.next();
                    prescription.setId(rs.getLong(1));
                    prescription.setDescription(object.getDescription());
                    prescription.setPatient(object.getPatient());
                    prescription.setDoctor(object.getDoctor());
                    prescription.setCreateDate(object.getCreateDate());
                    prescription.setExpiration(object.getExpiration());
                    prescription.setPriority(object.getPriority());
                } catch (SQLException e) {
                    throw e;
                }
            } else {
                throw new SQLException("Creating Prescription failed, no row inserted.");
            }
        } catch (SQLException e) {
            prescription = null;
            throw new DAOException(e);
        }
        return prescription;
    }

    @Override
    public void update(Prescription object) throws DAOException {
        String sql = "update PRESCRIPTION set DESCRIPTION = ?, PATIENT_ID = ?, DOCTOR_ID = ?, CREATE_DATE = ?, EXPIRATION = ?, PRIORITY = ? " +
                "where ID = ?;";
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setString(1, object.getDescription());
            Long patientId = null;
            if (object.getPatient() != null) {
                patientId = object.getPatient().getId();
            }
            st.setLong(2, patientId);
            Long doctortId = null;
            if (object.getPatient() != null) {
                doctortId = object.getDoctor().getId();
            }
            st.setLong(3, doctortId);
            Date createDate = null;
            if (object.getCreateDate() != null) {
                createDate = new Date(object.getCreateDate().getTime());
            }
            st.setDate(4, createDate);
            st.setInt(5, object.getExpiration());
            st.setString(6, object.getPriority());
            st.setLong(7, object.getId());
            st.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    @Override
    public void delete(Prescription object) throws DAOException {
        String sql = "delete from PRESCRIPTION where ID = ?;";
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setLong(1, object.getId());
            st.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    @Override
    public Prescription getByPrimaryKey(Long key) throws DAOException {
        Prescription prescription = null;
        String sql ="select DESCRIPTION, PATIENT_ID, DOCTOR_ID, CREATE_DATE, EXPIRATION, PRIORITY from PRESCRIPTION where ID = ?;";
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setLong(1, key);
            System.out.println("BEFORE2");
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                prescription = new Prescription();
                prescription.setId(key);
                System.out.println("BEFORE");
                prescription.setDescription(rs.getString("DESCRIPTION"));
                System.out.println("AFTER");
                PatientDAO patientDAO = DAOFactory.getInstance().getPatientDAO();
                Patient patient = patientDAO.getByPrimaryKey(rs.getLong("PATIENT_ID"));
                prescription.setPatient(patient);
                DoctorDAO doctorDAO = DAOFactory.getInstance().getDoctorDAO();
                Doctor doctor = doctorDAO.getByPrimaryKey(rs.getLong("DOCTOR_ID"));
                prescription.setDoctor(doctor);
                prescription.setCreateDate(rs.getDate("CREATE_DATE"));
                prescription.setExpiration(rs.getInt("EXPIRATION"));
                prescription.setPriority(rs.getString("PRIORITY"));
            }
        } catch (SQLException e) {
            prescription = null;
            throw new DAOException(e);
        }
        return prescription;
    }

    @Override
    public List<Prescription> getAll() throws DAOException {
        List<Prescription> list = new ArrayList<Prescription>();
        String sql ="select ID, DESCRIPTION, PATIENT_ID, DOCTOR_ID, CREATE_DATE, EXPIRATION, PRIORITY from PRESCRIPTION;";
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                Prescription prescription = new Prescription();
                prescription.setId(rs.getLong("ID"));
                prescription.setDescription(rs.getString("DESCRIPTION"));
                PatientDAO patientDAO = DAOFactory.getInstance().getPatientDAO();
                Patient patient = patientDAO.getByPrimaryKey(rs.getLong("PATIENT_ID"));
                prescription.setPatient(patient);
                DoctorDAO doctorDAO = DAOFactory.getInstance().getDoctorDAO();
                Doctor doctor = doctorDAO.getByPrimaryKey(rs.getLong("DOCTOR_ID"));
                prescription.setDoctor(doctor);
                prescription.setCreateDate(rs.getDate("CREATE_DATE"));
                prescription.setExpiration(rs.getInt("EXPIRATION"));
                prescription.setPriority(rs.getString("PRIORITY"));
                list.add(prescription);
            }
        } catch (SQLException e) {
            list = null;
            throw new DAOException(e);
        }
        return list;
    }
}
