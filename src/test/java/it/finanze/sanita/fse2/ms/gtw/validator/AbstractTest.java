package it.finanze.sanita.fse2.ms.gtw.validator;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.finanze.sanita.fse2.ms.gtw.validator.config.Constants;
import it.finanze.sanita.fse2.ms.gtw.validator.utility.ProfileUtility;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.bson.BsonBinarySubType;
import org.bson.Document;
import org.bson.types.Binary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;

import it.finanze.sanita.fse2.ms.gtw.validator.exceptions.BusinessException;
import it.finanze.sanita.fse2.ms.gtw.validator.repository.entity.DictionaryETY;
import it.finanze.sanita.fse2.ms.gtw.validator.repository.entity.SchemaETY;
import it.finanze.sanita.fse2.ms.gtw.validator.repository.entity.SchematronETY;
import it.finanze.sanita.fse2.ms.gtw.validator.repository.entity.TerminologyETY;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractTest {
    
    @Autowired
    protected ServletWebServerApplicationContext context;

    @Autowired
	protected MongoTemplate mongoTemplate;

	@Autowired
	protected ProfileUtility profileUtility;

    protected void clearConfigurationItems() {
        mongoTemplate.dropCollection(SchemaETY.class);
        mongoTemplate.dropCollection(SchematronETY.class);
        mongoTemplate.dropCollection(TerminologyETY.class);
    }

    protected void insertSchema() {
        insertConfigurationItems("schema");
    }

    protected void insertSchematron() {
        insertConfigurationItems("schematron");
    }


    private void insertConfigurationItems(final String item) {
			
		try {
			final File folder = context.getResource("classpath:Files/" + item).getFile();

			for (File file : folder.listFiles()) {
				final String schemaJson = new String(Files.readAllBytes(Paths.get(file.getCanonicalPath())), StandardCharsets.UTF_8);
				final Document schema = Document.parse(schemaJson);
				String targetCollection = item;
				if (profileUtility.isTestProfile()) {
					targetCollection = Constants.Profile.TEST_PREFIX + item;
				}
				mongoTemplate.insert(schema, targetCollection);

			}
		} catch(Exception e) {
			log.error(ExceptionUtils.getStackTrace(e));
			throw new BusinessException(e);
		}
	}
    
    protected Map<String, byte[]> getSchematronFiles(final String directoryPath) {
    	Map<String, byte[]> map = new HashMap<>();
		try {
			File directory = new File(directoryPath);
			
			//only first level files.
			String[] actualFiles = directory.list();
			
			if (actualFiles!=null && actualFiles.length>0) {
				for (String namefile : actualFiles) {
					File file = new File(directoryPath+ File.separator + namefile);
					map.put(namefile, Files.readAllBytes(file.toPath()));
				}
			}
		} catch(Exception ex) {
			log.error("Error while get schematron files : " + ex);
			throw new BusinessException("Error while get schematron files : " + ex);
		}
		return map;
	}

    protected void deleteDictionary() {
    	mongoTemplate.dropCollection(DictionaryETY.class);
    }
    
	protected void saveDictionaryFiles() {
		try {
			File directory = new File("src\\test\\resources\\Files\\dictionary");
			
			//only first level files.
			String[] actualFiles = directory.list();
			List<DictionaryETY> dictionaryList = new ArrayList<>();
			if (actualFiles!=null && actualFiles.length>0) {
				for (String namefile : actualFiles) {
					File file = new File("src\\test\\resources\\Files\\dictionary\\"+namefile);
					byte[] content = Files.readAllBytes(file.toPath());
					DictionaryETY dic = new DictionaryETY();
					dic.setContentFile(new Binary(BsonBinarySubType.BINARY, content));
					dic.setFileName(namefile);
					dictionaryList.add(dic);
				}
				insertAllDictionary(dictionaryList);
			}
			
			
		} catch(Exception ex) {
			log.error("Error while save dictionary file : " + ex);
			throw new BusinessException("Error while save dictionary file : " + ex);
		}
	}
    
    private void insertAllDictionary(List<DictionaryETY> list) {
    	mongoTemplate.insertAll(list);
    }
}
