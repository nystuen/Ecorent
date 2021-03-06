package admin;

import changescene.MainMethods;
import email.SendEmail;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import control.*;
import loginAdm.CurrentAdmin;
import model.AdminModel;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * AdminController.java
 * @author Team 007
 * @version 1.0
 *
 * This class handles displaying information, canging password, deleting admins and creating new admins by using
 * AdminChangePasswordView.fxml, AdminDeleteAdminView.fxml, AdminNewAdminView.fxml and AdminView.fxml.
 */
public class AdminController extends MainMethods {

    AdminModel model = new AdminModel();

    @FXML
    private CheckBox mainAdminCheck;

    @FXML
    private TextField newAdminEmailField;

    @FXML
    private TextField deleteAdminEmailField;

    @FXML
    private TextField newPasswordField2;

    @FXML
    private TextField newPasswordField;

    @FXML
    private TextField oldPasswordField;

    /**
     * Opens the popup window for creating a new admin.
     * @param event         on button click.
     * @throws Exception    if wrong file name.
     */
    @FXML
    void createNewAdmin(ActionEvent event) throws Exception {
        newPopup("/admin/AdminNewAdminView.fxml" ,"Create New Admin");
    }

    /**
     * Opens the popup window for deleting an existing admin.
     * @param event         on button click.
     */
    @FXML
    void deleteAdmin(ActionEvent event){
        newPopup("/admin/AdminDeleteAdminView.fxml", "Delete Admin");

    }

    /**
     * Opens the popup window for changing password.
     * @param event         on button click.
     */
    @FXML
    void changePassword(ActionEvent event) {
        newPopup("/admin/AdminChangePasswordView.fxml", "Change Password");
    }

    /**
     * Confirms the creation of a new admin.
     * @param event         on button click.
     */
    @FXML
    void createNewAdminConfirm(ActionEvent event) {
        if(CurrentAdmin.getInstance().getAdmin().isMainAdmin()) {
            String email = newAdminEmailField.getText();
            boolean main = mainAdminCheck.isSelected();
            String defaultPassword = SendEmail.sendFromGmail(email);
            String hashed = Password.hashPassword(defaultPassword);
            Admin admin = new Admin(email, hashed, main);
            if (factory.addAdmin(admin)) System.out.println(admin);
            closeWindow(event);
        }//end if
        else{
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Permission denied");
            alert.setHeaderText(null);
            alert.setContentText("You do not have main admin privileges and cannot create other admins." +
            " Contact your supervisor to require more privileges");
            alert.showAndWait();
        }//end else
    }//end method


    /**
     * Confirms the deletion of the admin.
     * @param event     on button click
     */
    @FXML
    void deleteAdminConfirm(ActionEvent event) {
        String email = deleteAdminEmailField.getText();
        if(email.equals(CurrentAdmin.getInstance().getAdmin().getEmail())){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Permission denied");
            alert.setHeaderText(null);
            alert.setContentText("You cannot delete yourself");
            alert.showAndWait();
            closeWindow(event);
        }//end if
        if(CurrentAdmin.getInstance().getAdmin().isMainAdmin()) {
            if (model.deleteAdmin(email)) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Delete success");
                alert.setHeaderText(Alert.AlertType.INFORMATION.name());
                alert.setContentText("Admin with email " + email + " is deleted");
                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Failed");
                alert.setHeaderText(Alert.AlertType.WARNING.name());
                alert.setContentText("Something went wrong!");
                alert.showAndWait();
            }//end condition
        }else{
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Failed");
            alert.setHeaderText(Alert.AlertType.WARNING.name());
            alert.setContentText("You do not have the privileges required to delete other admins." +
                    " Contact your supervisor to require more privileges");
            alert.showAndWait();
        }
        closeWindow(event);

    }//end

    /**
     * Confirms the change of password.
     * The password will change if the old password is correct,
     * as well as if the new password meets the requirements.
     * @param event     on button click.
     */
    @FXML
    void changePasswordConfirm(ActionEvent event) {
        boolean continiueBool = true;//Makes it easier to choose which error message to display
        String oldPassword = oldPasswordField.getText();
        String newPassword = newPasswordField.getText();
        String newPassword2 = newPasswordField2.getText();

        if(newPassword.length()<8 || newPassword.length()>30)continiueBool = false;

        if(Password.check(oldPassword,CurrentAdmin.getInstance().getAdmin().getPassword())&&newPassword.equals(newPassword2)&&
                continiueBool){
            String email = CurrentAdmin.getInstance().getAdmin().getEmail();
            boolean main = CurrentAdmin.getInstance().getAdmin().isMainAdmin();
            String password = Password.hashPassword(newPassword);
            model.deleteAdmin(CurrentAdmin.getInstance().getAdmin().getEmail());
            if(factory.addAdmin(new Admin(email,password,main))){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Password changed");
                alert.setHeaderText(null);
                alert.setContentText(email + " has now password " + newPassword);
                alert.showAndWait();
            }//end if
            else{
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Something went wrong!");
                alert.setHeaderText(null);
                alert.setContentText("Operation failed. Please make sure you fill out everything in the correct format and" +
                                " have internet access");
                alert.showAndWait();
                closeWindow(event);
            }
        }//end if

        else{
            if(continiueBool) { //if the passwords do not match
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Something went wrong!");
                alert.setHeaderText(null);
                alert.setContentText("Your passwords do not match!");
                alert.showAndWait();
                closeWindow(event);
            }//end if
            else{
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Something went wrong!");
                alert.setHeaderText(null);
                alert.setContentText("Your password must be between 8 and 30 characters!");
                alert.showAndWait();
                closeWindow(event);
            }//end else
        }//end else

        closeWindow(event);
    }
}
