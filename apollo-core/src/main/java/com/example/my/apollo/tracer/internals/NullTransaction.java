package com.example.my.apollo.tracer.internals;

import com.example.my.apollo.tracer.spi.Transaction;

public class NullTransaction implements Transaction {
    @Override
    public void setStatus(String status) {
    }
  
    @Override
    public void setStatus(Throwable e) {
    }
  
    @Override
    public void addData(String key, Object value) {
    }
  
    @Override
    public void complete() {
    }
    
}