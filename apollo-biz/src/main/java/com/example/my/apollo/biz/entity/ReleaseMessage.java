package com.example.my.apollo.biz.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;

import com.google.common.base.MoreObjects;

@Entity
@Table(name = "ReleaseMessage")
public class ReleaseMessage {
  // 对于同一个Namespace, 生成的消息内容是相同的.通过这样的方式
  // 我们可以使用最新的ReleaseMessage的id属性,作为Namespace
  // 是否发生变更的标识.Client通过不断使用或得到的ReleaseMessage
  // 的id属性作为版本号,请求ConfigService判断是否配置发生了变化
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "Id")
  private long id;

  // ex: "test+default+application"
  @Column(name = "Message", nullable = false)
  private String message;

  @Column(name = "DataChange_LastTime")
  private Date dataChangeLastModifiedTime;

  @PrePersist
  protected void prePersist() {
    // 如保存时, 未设置该字段, 进行补全.
    if (this.dataChangeLastModifiedTime == null) {
      dataChangeLastModifiedTime = new Date();
    }
  }

  public ReleaseMessage() {
  }

  public ReleaseMessage(String message) {
    this.message = message;
  }

  public ReleaseMessage(long id, String message) {
    this.id = id;
    this.message = message;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this).omitNullValues().add("id", id).add("message", message)
        .add("dataChangeLastModifiedTime", dataChangeLastModifiedTime).toString();
  }

}