package service;

import javafx.stage.Stage;
import net.sf.jasperreports.engine.*;
import org.fxutils.viewer.JasperViewerFX;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

class JasperReportService {

    public static final String JASPER_EXTENSION = ".jasper";
    public static final String JRXML_EXTENSION = ".jrxml";
    public static final String PDF_EXTENSION = ".pdf";

    public static void compileJasper(String jasperFileName) throws JRException {
        JasperCompileManager.compileReportToFile(jasperFileName + JRXML_EXTENSION,
                jasperFileName + JASPER_EXTENSION);
    }

    public static void showJasperReport(Stage primaryStage, String windowTitle,
                                        String inputFile, HashMap<String, Object> parametersMap,
                                        Connection DBConnection) {
        JasperViewerFX viewer = new JasperViewerFX(primaryStage,
                windowTitle, inputFile, parametersMap, DBConnection);
        viewer.show();
    }

    public static void printReport(Connection DBConnection, String reportTitle, String jasperFileName, String outputFile) {


        //------------------Jasper testing is here.--------------------------------------------
        Map<String, Object> mapParameters = new HashMap<>();
        try {

            System.out.println("Generating report...");

            mapParameters.put("ReportTitle", reportTitle);

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperFileName,
                    mapParameters, DBConnection);

            // Export pdf file
            JasperExportManager.exportReportToPdfFile(jasperPrint, outputFile);

            System.out.println("Done exporting reports to pdf.");

        } catch (Exception e) {
            System.out.print("Exception" + e);
        }
        //--------------------------------------------------------------------------------------------
    }

    public static void main(String[] args) {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("services.dbuser");
        String user = resourceBundle.getString("user");
        String password = resourceBundle.getString("password");
        String connectionUrl = "jdbc:mysql://localhost:3306/BOOKSTORE?useUnicode=true&" +
                "characterEncoding=UTF-8";
        Connection DBConnection = null;
        try {
            DBConnection = DriverManager.getConnection(connectionUrl, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String reportTopFiveFileName = "/home/mohammed/workspace/Database/bookstore/jasper/" +
                "report-top-five-customers";
        String reportTopFiveTitle = "Top Five Users in The Past Three Months";

        String reportLastMonthFileName = "/home/mohammed/workspace/Database/bookstore/jasper/" +
                "report-last-month-sales";
        String reportLastMonthTitle = "Last Month Sales";

        String reportTopTenBooksFileName = "/home/mohammed/workspace/Database/bookstore/jasper/" +
                "report-top-ten-selling-books";
        String reportTopTenBooks = "Top Ten Selling Books in The Past Three Months";

//        try {
//            compileJasper(reportTopTenBooksFileName);
//        } catch (JRException e) {
//            e.printStackTrace();
//        }
//
//        try {
//            compileJasper(reportLastMonthFileName);
//        } catch (JRException e) {
//            e.printStackTrace();
//        }
//
//        try {
//            compileJasper(reportTopFiveFileName);
//        } catch (JRException e) {
//            e.printStackTrace();
//        }

        printReport(DBConnection, reportTopFiveTitle, reportTopFiveFileName + JASPER_EXTENSION,
                reportTopFiveFileName + PDF_EXTENSION);

        printReport(DBConnection, reportTopTenBooks,
                reportTopTenBooksFileName + JASPER_EXTENSION,
                reportTopTenBooksFileName + PDF_EXTENSION);

        printReport(DBConnection, reportLastMonthTitle,
                reportLastMonthFileName + JASPER_EXTENSION,
                reportLastMonthFileName + PDF_EXTENSION);



    }

}
