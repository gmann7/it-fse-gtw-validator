package it.sanita.fse.validator.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.helger.schematron.ISchematronResource;

import it.sanita.fse.validator.cda.CDAHelper;
import it.sanita.fse.validator.cda.ValidationResult;
import it.sanita.fse.validator.dto.CDAValidationDTO;
import it.sanita.fse.validator.dto.SchematronValidationResultDTO;
import it.sanita.fse.validator.enums.CDAValidationStatusEnum;
import it.sanita.fse.validator.exceptions.BusinessException;
import it.sanita.fse.validator.repository.entity.SchemaETY;
import it.sanita.fse.validator.repository.entity.SchematronETY;
import it.sanita.fse.validator.repository.mongo.ISchemaRepo;
import it.sanita.fse.validator.repository.mongo.ISchematronRepo;
import it.sanita.fse.validator.service.ISchemaSRV;
import it.sanita.fse.validator.service.IValidationSRV;
import it.sanita.fse.validator.service.IVocabulariesSRV;
import it.sanita.fse.validator.singleton.SchemaValidatorSingleton;
import it.sanita.fse.validator.singleton.SchematronValidatorSingleton;
import it.sanita.fse.validator.utility.StringUtility;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ValidationSRV implements IValidationSRV {

    @Autowired
    private IVocabulariesSRV vocabulariesRV;

    @Autowired
    private ISchematronRepo schematronRepo;
    
    @Autowired
    private ISchemaRepo schemaRepo;
    
    @Autowired
    private ISchemaSRV schemaSRV;
    
    @Override
    public boolean validateVocabularies(final String cda) {
        boolean validationSuccess = true;
        
        try {
            Map<String, List<String>> vocabularies = CDAHelper.extractTerminology(cda);
            log.info("Validating {} systems...", vocabularies.size());
            validationSuccess = vocabulariesRV.vocabulariesExists(vocabularies);
        } catch (Exception e) {
            log.error("Error while executing validation on vocabularies", e);
            throw new BusinessException("Error while executing validation on vocabularies", e);
        }

        return validationSuccess;
    }

	@Override
	public CDAValidationDTO validateSyntactic(final String cda, final String version) {
		CDAValidationDTO out = new CDAValidationDTO(CDAValidationStatusEnum.VALID);
		try {
			SchemaETY schema = null;
			if(StringUtility.isNullOrEmpty(version)) {
				schema = schemaRepo.findFatherLastVersionXsd();
			} else {
				schema = schemaRepo.findFatherXsd(version);
			}
			
			SchemaValidatorSingleton instance = SchemaValidatorSingleton.getInstance(version, schema, schemaRepo);
			ValidationResult validationResult = schemaSRV.validateXsd(instance.getValidator(), cda);
			if(validationResult!=null && !validationResult.isSuccess()) {
				out  = new CDAValidationDTO(validationResult); 
			}
		} catch(Exception ex) {
			log.error("Error while executing validation on xsd schema", ex);
			throw new BusinessException("Error while executing validation on xsd schema", ex);
		}
		return out;
	}

	@Override
	public boolean validateSemantic(final String cda, final String version) {
		boolean output = false;
		try {
			SchematronETY schematronETY = null;
			if(StringUtility.isNullOrEmpty(version)) {
				schematronETY = schematronRepo.findLastVersion();
			} else {
				schematronETY = schematronRepo.findByVersion(version);
			}
			
			SchematronValidatorSingleton schematron = SchematronValidatorSingleton.getInstance(version, schematronETY);
			ISchematronResource resource = schematron.getSchematronResource();
			SchematronValidationResultDTO result = CDAHelper.validateXMLViaXSLTSchematronFull(resource, cda.getBytes());
			System.out.println("Stop");
		} catch(Exception ex) {
			log.error("Error while executing validation on schematron", ex);
			throw new BusinessException("Error while executing validation on schematron", ex);
		}
		return output;
	}
    
}
