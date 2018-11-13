package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.form.Validators.*;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@NewPasswordMatches
@ValidCurrentPassword
public class ModifyPasswordForm {

    private String userName;

    @Size(min = 5, max = 100)
    @Pattern(regexp = "[a-zA-Z0-9]+")
    private String oldPassword;


    @Size(min = 5, max = 100)
    @Pattern(regexp = "[a-zA-Z0-9]+")
    private String newPassword;

    @Size(min = 5, max = 100)
    @Pattern(regexp = "[a-zA-Z0-9]+")
    private String repeatNewPassword;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(final String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getRepeatNewPassword() {
        return repeatNewPassword;
    }

    public void setRepeatNewPassword(final String repeatNewPassword) {
        this.repeatNewPassword = repeatNewPassword;
    }

}
