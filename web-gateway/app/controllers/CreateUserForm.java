package controllers;

import play.data.validation.Constraints;

public class CreateUserForm {

    @Constraints.Required
    private String name;
    @Constraints.Required
    private String email;
    @Constraints.Required
    private String password;
    @Constraints.Required
    private String confirmPassword;

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public void setName(String name) {
        this.name = name;
    }
}
