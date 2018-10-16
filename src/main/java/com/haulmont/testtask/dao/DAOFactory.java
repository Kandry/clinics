package com.haulmont.testtask.dao;


import java.sql.SQLException;

public class DAOFactory {
    private static DAOFactory instance = null;

    private DAOFactory() {
    }

    public static synchronized DAOFactory getInstance() {
        if (instance == null) {
            instance = new DAOFactory();
        }
        return instance;
    }

    public PatientDAO getPatientDAO() throws DAOException {
        try {
            return new PatientDAO(DBConnection.getConnection());
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }

    public DoctorDAO getDoctorDAO() throws DAOException {
        try {
            return new DoctorDAO(DBConnection.getConnection());
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }

    public PrescriptionDAO getPrescriptionDAO() throws DAOException {
        try {
            return new PrescriptionDAO(DBConnection.getConnection());
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }

    public void releaseResources() throws DAOException {
        try {
            DBConnection.closeConnection();
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }
}
