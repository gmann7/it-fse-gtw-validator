package it.sanita.fse.validator.cda;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.transform.stream.StreamSource;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.helger.schematron.ISchematronResource;
import com.helger.schematron.pure.SchematronResourcePure;
import com.helger.schematron.svrl.jaxb.FailedAssert;
import com.helger.schematron.svrl.jaxb.SchematronOutputType;

import it.sanita.fse.validator.dto.CDAValidationDTO;
import it.sanita.fse.validator.dto.SchematronFailedAssertionDTO;
import it.sanita.fse.validator.dto.SchematronValidationResultDTO;
import it.sanita.fse.validator.enums.CDAValidationStatusEnum;

public class CDAHelper {

	public static Map<String, List<String>> extractTerminology(String cda) {
        org.jsoup.nodes.Document docT = Jsoup.parse(cda);
        Elements terms = docT.select("[codeSystem]");

        Map<String, List<String>> terminology = new HashMap<>();
        
        for (Element t:terms) {
        	String system = t.attr("codeSystem");
        	List<String> codes = terminology.get(system);
        	if (codes == null) {
        		codes = new ArrayList<>();
        	}
        	String code = t.attr("code");
        	codes.add(code);
        	terminology.put(system, codes);
        }
        return terminology;
	}
	
//	public static CDAValidationDTO validate(String content) throws Exception {
//		CDAValidationDTO out = new CDAValidationDTO(CDAValidationStatusEnum.NOT_VALID);
//		if(content != null && content.length() >= 20) {
//			ICDAValidator validator = new CDAValidator();
//			ValidationResult result = validator.validate(content.getBytes());
//			if (!result.isSuccess()) {
//				out = new CDAValidationDTO(CDAValidationStatusEnum.VALID);
//			} else {
//				out = new CDAValidationDTO(result);
//			}
//		}
//		return out;
//	}

	public static SchematronValidationResultDTO validateXMLViaXSLTSchematronFull(ISchematronResource aResSCH , final byte[] xml) throws Exception{
//		final ISchematronResource aResSCH = SchematronResourceSCH.fromClassPath(schematronInternalPath);
//		final ISchematronResource aResSCH = SchematronResourcePure.fromByteArray(buf);
		boolean validST = aResSCH.isValidSchematron();
		boolean validXML = true;
		List<SchematronFailedAssertionDTO> failedAssertions = new ArrayList<>();
		if (validST) {

			Long start = new Date().getTime();


			SchematronOutputType type = aResSCH.applySchematronValidationToSVRL(new StreamSource(new ByteArrayInputStream(xml)));
			List<Object> failedAsserts = type.getActivePatternAndFiredRuleAndFailedAssert();

			Long delta = new Date().getTime() - start;
			System.out.println("TIME" + delta);        

			for (Object object : failedAsserts) {
				if (object instanceof FailedAssert) {
					validXML = false;
					FailedAssert failedAssert = (FailedAssert) object;
					SchematronFailedAssertionDTO failedAssertion = SchematronFailedAssertionDTO.builder().location(failedAssert.getLocation()).test(failedAssert.getTest()).text(failedAssert.getText().getContent().toString()).build();
					failedAssertions.add(failedAssertion);
				}
			}
		}
		return SchematronValidationResultDTO.builder().validSchematron(validST).validXML(validXML).failedAssertions(failedAssertions).build();
	}

}
