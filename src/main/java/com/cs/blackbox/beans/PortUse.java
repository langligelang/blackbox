package com.cs.blackbox.beans;

import lombok.Data;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="t_port_use")
@Data
public class PortUse {

    @Id
    private String id;
    private String port;
    private boolean isUser;
    private String wsAddress;



}
