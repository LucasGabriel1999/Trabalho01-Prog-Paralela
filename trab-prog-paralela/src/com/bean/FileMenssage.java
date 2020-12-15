package com.bean;

import java.io.File;
import java.io.Serializable;

/**
 *
 * @author lucas
 */
public class FileMenssage implements Serializable{
    private  String cliente;
    private File file;

    public FileMenssage(String cliente, File file) {
        this.cliente = cliente;
        this.file = file;
    }

    public FileMenssage(String cliente) {
        this.cliente = cliente;
    }

    public FileMenssage() {
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
    
    
}
