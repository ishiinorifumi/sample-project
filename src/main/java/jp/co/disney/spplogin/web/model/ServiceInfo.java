package jp.co.disney.spplogin.web.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class ServiceInfo implements Serializable {
	
	private static final long serialVersionUID = 4147377399322143011L;

	private String dspp;
	
	private String serviceName;
	
}
