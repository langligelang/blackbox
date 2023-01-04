package com.cs.blackbox.beans;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

@Entity
@Table(name="black_scan")
@Data
public class BlackScan {

    @Id
    private String id;
    private Integer port;
    private String jiraId;
    private String proxyIp;
    private String wsAddress;
    private String username;    //谁负责测试
    private Timestamp regTime;

}
