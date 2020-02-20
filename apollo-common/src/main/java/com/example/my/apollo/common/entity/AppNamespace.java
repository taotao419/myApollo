package com.example.my.apollo.common.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Table(name = "AppNamespace")
@SQLDelete(sql = "Update AppNamespace set isDeleted = 1 where id = ?")
@Where(clause = "isDeleted = 0")
public class AppNamespace extends BaseEntity {

    @NotBlank(message = "AppNamespace Name cannot be blank")
    @Pattern(regexp = InputValidator.CLUSTER_NAMESPACE_VALIDATOR, message = "Invalid Namespace format: "
            + InputValidator.INVALID_CLUSTER_NAMESPACE_MESSAGE + " & "
            + InputValidator.INVALID_NAMESPACE_NAMESPACE_MESSAGE)
    @Column(name = "Name", nullable = false)
    private String name;

    @NotBlank(message = "AppId cannot be blank")
    @Column(name = "AppId", nullable = false)
    private String appId;

    /**
     * 参见ConfigFileFormat
     */
    @Column(name = "Format", nullable = false)
    private String format;

    @Column(name = "IsPublic", columnDefinition = "Bit default '0'")
    private boolean isPublic = false;

    @Column(name = "Comment")
    private String comment;

    public String getAppId() {
        return appId;
    }

    public String getComment() {
        return comment;
    }

    public String getName() {
        return name;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    // public ConfigFileFormat formatAsEnum() {
    // return ConfigFileFormat.fromString(this.format);
    // }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String toString() {
        return toStringHelper().add("name", name).add("appId", appId).add("comment", comment).add("format", format)
                .add("isPublic", isPublic).toString();
    }

    private class InputValidator {
        public static final String CLUSTER_NAMESPACE_VALIDATOR = "[0-9a-zA-Z_.-]+";
        public static final String INVALID_NAMESPACE_NAMESPACE_MESSAGE = "not allowed to end with .json, .yml, .yaml, .xml, .properties";
        public static final String INVALID_CLUSTER_NAMESPACE_MESSAGE = "Only digits, alphabets and symbol - _ . are allowed";
    }
}
