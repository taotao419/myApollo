package com.example.my.apollo.biz.entity;

import javax.persistence.*;

import com.example.my.apollo.common.entity.BaseEntity;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Table(name = "Item")
@SQLDelete(sql = "Update Item set isDeleted = 1 where id = ?")
@Where(clause = "isDeleted = 0")
public class Item extends BaseEntity {
  @Column(name = "NamespaceId", nullable = false)
  private long namespaceId;

  /**
   * 对于 properties, item的key 对应每条配置项的键 对于yaml等待, item的key = content ,对应整个配置文件.
   */
  @Column(name = "\"key\"", nullable = false)
  private String key;

  @Column(name = "Value")
  @Lob
  private String value;

  @Column(name = "Comment")
  private String comment;

  // 行号, 从一开始. 主要用于properties类型的配置文件.
  @Column(name = "LineNum")
  private Integer lineNum;

  public String getComment() {
  return comment;
  }

  public String getKey() {
    return key;
  }

  public long getNamespaceId() {
    return namespaceId;
  }

  public String getValue() {
  return value;
  }

  public void setComment(String comment) {
  this.comment = comment;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public void setNamespaceId(long namespaceId) {
    this.namespaceId = namespaceId;
  }

  public void setValue(String value) {
  this.value = value;
  }

  public Integer getLineNum() {
  return lineNum;
  }

  public void setLineNum(Integer lineNum) {
  this.lineNum = lineNum;
  }

  public String toString() {
  return toStringHelper().add("namespaceId", namespaceId).add("key",
  key).add("value", value)
  .add("lineNum", lineNum).add("comment", comment).toString();
  }

}