package com.example.my.apollo.common.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Table(name = "App")//catalog="apolloportaldb"  不能写这个 这样写死在portalDB取
@SQLDelete(sql = "Update App set isDeleted = 1 where id = ?")
@Where(clause = "isDeleted = 0")
public class App extends BaseEntity {

    @NotBlank(message = "Name cannot be blank")
    @Column(name = "Name", nullable = false)
    private String name;
  
    @NotBlank(message = "AppId cannot be blank")
    @Column(name = "AppId", nullable = false)
    private String appId;
  
    @Column(name = "OrgId", nullable = false)
    private String orgId;
  
    @Column(name = "OrgName", nullable = false)
    private String orgName;
  
    @NotBlank(message = "OwnerName cannot be blank")
    @Column(name = "OwnerName", nullable = false)
    private String ownerName;
  
    @NotBlank(message = "OwnerEmail cannot be blank")
    @Column(name = "OwnerEmail", nullable = false)
    private String ownerEmail;
  
    public String getAppId() {
      return appId;
    }
  
    public String getName() {
      return name;
    }
  
    public String getOrgId() {
      return orgId;
    }
  
    public String getOrgName() {
      return orgName;
    }
  
    public String getOwnerEmail() {
      return ownerEmail;
    }
  
    public String getOwnerName() {
      return ownerName;
    }
  
    public void setAppId(String appId) {
      this.appId = appId;
    }
  
    public void setName(String name) {
      this.name = name;
    }
  
    public void setOrgId(String orgId) {
      this.orgId = orgId;
    }
  
    public void setOrgName(String orgName) {
      this.orgName = orgName;
    }
  
    public void setOwnerEmail(String ownerEmail) {
      this.ownerEmail = ownerEmail;
    }
  
    public void setOwnerName(String ownerName) {
      this.ownerName = ownerName;
    }
  
    @Override
    public String toString() {
      return toStringHelper().add("name", name).add("appId", appId)
          .add("orgId", orgId)
          .add("orgName", orgName)
          .add("ownerName", ownerName)
          .add("ownerEmail", ownerEmail).toString();
    }
  
    public static class Builder {
  
      public Builder() {
      }
  
      private App app = new App();
  
      public Builder name(String name) {
        app.setName(name);
        return this;
      }
  
      public Builder appId(String appId) {
        app.setAppId(appId);
        return this;
      }
  
      public Builder orgId(String orgId) {
        app.setOrgId(orgId);
        return this;
      }
  
      public Builder orgName(String orgName) {
        app.setOrgName(orgName);
        return this;
      }
  
      public Builder ownerName(String ownerName) {
        app.setOwnerName(ownerName);
        return this;
      }
  
      public Builder ownerEmail(String ownerEmail) {
        app.setOwnerEmail(ownerEmail);
        return this;
      }
  
      public App build() {
        return app;
      }
  
    }
  
    public static Builder builder() {
      return new Builder();
    }
  
  
  }