package com.cs.blackbox.service;

import com.cs.blackbox.beans.BlackScan;
import com.cs.blackbox.dao.BlackScanDao;
import org.springframework.beans.factory.annotation.Autowired;

public class BlackScanService {

    @Autowired
    BlackScanDao blackScanDao;

    public void save(BlackScan blackScan){
        blackScanDao.save(blackScan);
    }


}
