/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.gtw.validator.logging;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import it.finanze.sanita.fse2.ms.gtw.validator.client.IConfigClient;
import it.finanze.sanita.fse2.ms.gtw.validator.dto.LogDTO;
import it.finanze.sanita.fse2.ms.gtw.validator.enums.ErrorLogEnum;
import it.finanze.sanita.fse2.ms.gtw.validator.enums.OperationLogEnum;
import it.finanze.sanita.fse2.ms.gtw.validator.enums.ResultLogEnum;
import it.finanze.sanita.fse2.ms.gtw.validator.enums.WarnLogEnum;
import it.finanze.sanita.fse2.ms.gtw.validator.utility.StringUtility;
import lombok.extern.slf4j.Slf4j;
 
@Service
@Slf4j
public class LoggerHelper {
    
	Logger kafkaLog = LoggerFactory.getLogger("kafka-logger"); 
	
    @Autowired
	private IConfigClient configClient;
	
	private String gatewayName;
	
	@Value("${log.kafka-log.enable}")
	private boolean kafkaLogEnable;

	@Value("${spring.application.name}")
	private String msName;
	
	/* 
	 * Specify here the format for the dates 
	 */
	private DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss.SSS"); 
	
	/* 
	 * Implements structured logs, at all logging levels
	 */
	public void trace(String message, OperationLogEnum operation, ResultLogEnum result, Date startDateOperation) {

		final String gtwName = getGatewayName();
		
		LogDTO logDTO = LogDTO.builder().
				message(message).
				operation(operation.getCode()).
				op_result(result.getCode()).
				op_timestamp_start(dateFormat.format(startDateOperation)).
				op_timestamp_end(dateFormat.format(new Date())).
				gateway_name(gtwName).
				microservice_name(msName).
				build();

		final String logMessage = StringUtility.toJSON(logDTO);
		log.trace(logMessage);

		if (Boolean.TRUE.equals(kafkaLogEnable)) {
			kafkaLog.trace(logMessage);
		}
	}

	public void debug(String message, OperationLogEnum operation, ResultLogEnum result, Date startDateOperation) {
		
		final String gtwName = getGatewayName();

		LogDTO logDTO = LogDTO.builder().
				message(message).
				operation(operation.getCode()).
				op_result(result.getCode()).
				op_timestamp_start(dateFormat.format(startDateOperation)).
				op_timestamp_end(dateFormat.format(new Date())).
				gateway_name(gtwName).
				microservice_name(msName).
				build();
		
		final String logMessage = StringUtility.toJSON(logDTO);
		log.debug(logMessage);
		if (Boolean.TRUE.equals(kafkaLogEnable)) {
			kafkaLog.debug(logMessage);
		}
	} 
	 
	public void info(String message, OperationLogEnum operation, ResultLogEnum result, Date startDateOperation) {

		final String gtwName = getGatewayName();
		
		LogDTO logDTO = LogDTO.builder().
				message(message).
				operation(operation.getCode()).
				op_result(result.getCode()).
				op_timestamp_start(dateFormat.format(startDateOperation)).
				op_timestamp_end(dateFormat.format(new Date())).
				gateway_name(gtwName).
				microservice_name(msName).
				build();
		
		final String logMessage = StringUtility.toJSON(logDTO);
		log.info(logMessage);
		if (Boolean.TRUE.equals(kafkaLogEnable)) {
			kafkaLog.info(logMessage);
			log.info("After send kafka message");
		}
	} 
	
	public void warn(String message, OperationLogEnum operation, ResultLogEnum result, Date startDateOperation, WarnLogEnum warning) {
		
		final String gtwName = getGatewayName();

		LogDTO logDTO = LogDTO.builder().
				message(message).
				operation(operation.getCode()).
				op_result(result.getCode()).
				op_timestamp_start(dateFormat.format(startDateOperation)).
				op_timestamp_end(dateFormat.format(new Date())).
				op_warning(warning.getCode()).
				op_warning_description(warning.getDescription()).
				gateway_name(gtwName).
				microservice_name(msName).
				build();
		
		final String logMessage = StringUtility.toJSON(logDTO);
		log.warn(logMessage);
		if (Boolean.TRUE.equals(kafkaLogEnable)) {
			kafkaLog.warn(logMessage);
		}
 
	} 
	
	public void error(String message, OperationLogEnum operation, ResultLogEnum result, Date startDateOperation, ErrorLogEnum error) {
		
		final String gtwName = getGatewayName();

		LogDTO logDTO = LogDTO.builder().
				message(message).
				operation(operation.getCode()).
				op_result(result.getCode()).
				op_timestamp_start(dateFormat.format(startDateOperation)).
				op_timestamp_end(dateFormat.format(new Date())).
				op_error(error.getCode()).
				op_error_description(error.getDescription()).
				gateway_name(gtwName).
				microservice_name(msName).
				build();
		
		final String logMessage = StringUtility.toJSON(logDTO);
		log.error(logMessage);
		if (Boolean.TRUE.equals(kafkaLogEnable)) {
			kafkaLog.error(logMessage);
		}
		
	}

	/**
	 * Returns the gateway name.
	 * 
	 * @return The GatewayName of the ecosystem.
	 */
	private String getGatewayName() {
		if (gatewayName == null) {
			gatewayName = configClient.getGatewayName();
		}
		return gatewayName;
	}
	
}
