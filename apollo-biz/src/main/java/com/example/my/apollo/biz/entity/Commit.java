package com.example.my.apollo.biz.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;

import com.example.my.apollo.common.entity.BaseEntity;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Table(name = "Commit")
@SQLDelete(sql = "Update Commit set isDeleted = 1 where id = ?")
@Where(clause = "isDeleted = 0")
public class Commit extends BaseEntity{
  //变更集合. JSON格式化
  @Lob//存储大量、单字节、字符数据，存储在内部表空间，用于存储字符串类型的Lob，如文本和XML文件等，字符串已数据库字符集编码。
  @Column(name = "ChangeSets", nullable = false)
  private String changeSets;

  @Column(name = "AppId", nullable = false)
  private String appId;

  @Column(name = "ClusterName", nullable = false)
  private String clusterName;

  @Column(name = "NamespaceName", nullable = false)
  private String namespaceName;

  @Column(name = "Comment")
  private String comment;

  public String getChangeSets() {
    return changeSets;
  }

  public void setChangeSets(String changeSets) {
    this.changeSets = changeSets;
  }

  public String getAppId() {
    return appId;
  }

  public void setAppId(String appId) {
    this.appId = appId;
  }

  public String getClusterName() {
    return clusterName;
  }

  public void setClusterName(String clusterName) {
    this.clusterName = clusterName;
  }

  public String getNamespaceName() {
    return namespaceName;
  }

  public void setNamespaceName(String namespaceName) {
    this.namespaceName = namespaceName;
  }

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  @Override
  public String toString() {
    return toStringHelper().add("changeSets", changeSets).add("appId", appId).add("clusterName", clusterName)
        .add("namespaceName", namespaceName).add("comment", comment).toString();
  }
    
}