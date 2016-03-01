/**
 *
 */
package by.pvt.kish.aircompany.pojos;

import by.pvt.kish.aircompany.enums.*;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * This class represents the User model.
 * User could be two types: administrator and dispatcher
 * This model class can be used throughout all
 * layers, the data layer, the controller layer and the view layer.
 *
 * @author Kish Alexey
 */
@Entity
public class User implements Serializable {

    @Id
    @GeneratedValue
    public Long getUid() {
        return uid;
    }
    public void setUid(Long uid) {
        this.uid = uid;
    }
    private Long uid;

    @Column(nullable = false)
    @NotEmpty
    @Size(min=2, max=15)
    @Pattern(regexp="^[A-Z]+[a-z]+$")
    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    private String firstName;

    @Column(nullable = false)
    @NotEmpty
    @Size(min=2, max=15)
    @Pattern(regexp="^[A-Z]+[a-z]+$")
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    private String lastName;

    @Column(nullable = false, unique = true)
    @NotEmpty
    @Size(min=3, max=10)
    @Pattern(regexp="[a-zA-Z0-9_]+$")
    public String getLogin() {
        return login;
    }
    public void setLogin(String login) {
        this.login = login;
    }
    private String login;

    @Column(nullable = false)
    @NotEmpty
    @Size(min=6, max=15)
    @Pattern(regexp="[a-zA-Z0-9_]+$")
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    private String password;

    @Column(nullable = false)
    @NotEmpty
    @Email
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "enum('ROLE_ADMIN','ROLE_DISPATCHER','ROLE_ANONYMOUS')")
    public UserRole getRole() {
        return role;
    }
    public void setRole(UserRole userType) {
        this.role = userType;
    }
    private UserRole role;

    @Column(nullable = false)
    public boolean isEnabled() {
        return enabled;
    }
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    private boolean enabled = true;

    public User() {
    }

    /**
     * @param uid       - user id
     * @param firstName - user firstname
     * @param lastName  - user lastname
     * @param login     - user login
     * @param password  - user password
     * @param role  - user type (Administrator or dispatcher)
     * @param enabled    - Specify whether the user's account is active or not
     */
    public User(Long uid, String firstName, String lastName, String login, String password, UserRole role, boolean enabled) {
        super();
        this.uid = uid;
        this.firstName = firstName;
        this.lastName = lastName;
        this.login = login;
        this.password = password;
        this.role = role;
        this.enabled = enabled;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (enabled != user.enabled) return false;
        if (uid != null ? !uid.equals(user.uid) : user.uid != null) return false;
        if (firstName != null ? !firstName.equals(user.firstName) : user.firstName != null) return false;
        if (lastName != null ? !lastName.equals(user.lastName) : user.lastName != null) return false;
        if (login != null ? !login.equals(user.login) : user.login != null) return false;
        if (password != null ? !password.equals(user.password) : user.password != null) return false;
        if (email != null ? !email.equals(user.email) : user.email != null) return false;
        return role == user.role;

    }

    @Override
    public int hashCode() {
        int result = uid != null ? uid.hashCode() : 0;
        result = 31 * result + (firstName != null ? firstName.hashCode() : 0);
        result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
        result = 31 * result + (login != null ? login.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (role != null ? role.hashCode() : 0);
        result = 31 * result + (enabled ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "User{" +
                "uid=" + uid +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", login='" + login + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", userType=" + role +
                ", enabled=" + enabled +
                '}';
    }
}
