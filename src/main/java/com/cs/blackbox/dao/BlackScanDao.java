package com.cs.blackbox.dao;

import com.cs.blackbox.beans.BlackScan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Component
public interface BlackScanDao extends JpaRepository<BlackScan,String> {



}
