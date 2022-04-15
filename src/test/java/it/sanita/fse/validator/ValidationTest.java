package it.sanita.fse.validator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;

import it.sanita.fse.validator.config.Constants;
import it.sanita.fse.validator.dto.CDAValidationDTO;
import it.sanita.fse.validator.enums.CDAValidationStatusEnum;
import it.sanita.fse.validator.exceptions.BusinessException;
import it.sanita.fse.validator.service.facade.IValidationFacadeSRV;
import it.sanita.fse.validator.utility.FileUtility;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ComponentScan(basePackages = { Constants.ComponentScan.BASE })
@ActiveProfiles(Constants.Profile.TEST)
public class ValidationTest {

	@Autowired
	private IValidationFacadeSRV validationSRV;

	@Test
	void shouldReturnValidWhenCDAIsValid() {

		final String cda = new String(FileUtility.getFileFromInternalResources("Files" + File.separator + "cda.xml"), StandardCharsets.UTF_8);
		String version = "1.0.0";
		
		log.info("Testing with version {}", version);
		CDAValidationDTO firstResult = validationSRV.validateSyntactic(cda, version);
		assertEquals(CDAValidationStatusEnum.VALID, firstResult.getStatus(), "The validation should have been completed correctly");
		
		CDAValidationDTO secondResult = validationSRV.validateSyntactic(cda, version);
		assertEquals(firstResult.getStatus(), secondResult.getStatus(), "Repeating validation should have not changed the result");

		version = "2.0.0";
		
		log.info("Testing with version {}", version);
		CDAValidationDTO thirdResult = validationSRV.validateSyntactic(cda, version);
		assertEquals(CDAValidationStatusEnum.VALID, thirdResult.getStatus(), "The validation should have been completed correctly");
	}

	@Test
	void shouldReturnNotValidWhenCDAIsInvalid() {

		final String cda = "<realmCode code=\"1\"/>";
		String version = "1.0.0";
		
		log.info("Testing with version {}", version);
		CDAValidationDTO firstResult = validationSRV.validateSyntactic(cda, version);
		assertEquals(CDAValidationStatusEnum.NOT_VALID, firstResult.getStatus(), "The validation should have been completed correctly and result as Invalid");
	
		CDAValidationDTO secondResult = validationSRV.validateSyntactic(cda, version);
		assertEquals(CDAValidationStatusEnum.NOT_VALID, secondResult.getStatus(), "The validation should have been completed correctly and result as Invalid");
	}

	@Test
	void shouldThrowBusinessExceptionWhenSchemaisNull() {

		final String cda = new String(FileUtility.getFileFromInternalResources("Files" + File.separator + "cda.xml"), StandardCharsets.UTF_8);
		final String version = "3.0.0";
		
		Throwable thrownException = assertThrows(BusinessException.class, () -> validationSRV.validateSyntactic(cda, version));
		assertEquals(String.format("Schema with version %s not found on database.", version), thrownException.getMessage());
	}

	@Test
	void shouldThrowBusinessExceptionWhenCDAIsNotXML() {

		final String cda = "invalid cda";
		final String version = "3.0.0";
		
		assertThrows(BusinessException.class, () -> validationSRV.validateSyntactic(cda, version));
	}
}
