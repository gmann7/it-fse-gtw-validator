package it.sanita.fse.validator.repository.mongo.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import it.sanita.fse.validator.controller.impl.AbstractMongoRepo;
import it.sanita.fse.validator.exceptions.BusinessException;
import it.sanita.fse.validator.repository.entity.SchemaETY;
import it.sanita.fse.validator.repository.mongo.ISchemaRepo;
import lombok.extern.slf4j.Slf4j;

/**
 *	@author vincenzoingenito
 *
 *	Schema repository.
 */
@Slf4j
@Repository
public class SchemaRepo extends AbstractMongoRepo<SchemaETY, String> implements ISchemaRepo {
	
	/**
	 * Serial version uid.
	 */
	private static final long serialVersionUID = -4017623557412046071L;
	
	@Autowired
	private MongoTemplate mongoTemplate;
    

	@Override
	public List<SchemaETY> findChildrenXsd(final String version) {
		List<SchemaETY> output = null;
		try {
			Query query = new Query();
			query.addCriteria(Criteria.where("name_schema").ne("Files\\schema\\CDA.xsd").and("version").is(version));
			output = mongoTemplate.find(query, SchemaETY.class);
		} catch(Exception ex) {
			log.error("Error while searching for child schemes" , ex);
			throw new BusinessException("Error while searching for child schemes" , ex);
		}
		return output;
	}
	
	@Override
	public SchemaETY findFatherXsd(final String version) {
		SchemaETY output = null;
		try {
			Query query = new Query();
			query.addCriteria(Criteria.where("root_schema").is(true).and("version").is(version));
			output = mongoTemplate.findOne(query, SchemaETY.class);
		} catch(Exception ex) {
			log.error("Error while searching for father schema" , ex);
			throw new BusinessException("Error while searching for father schema" , ex);
		}
		return output;
	}
	
	@Override
	public SchemaETY findFatherLastVersionXsd() {
		SchemaETY output = null;
		try {
			Query query = new Query();
			query.addCriteria(Criteria.where("root_schema").is(true));
			query.with(Sort.by(Sort.Direction.DESC, "version"));
			output = mongoTemplate.findOne(query, SchemaETY.class);
		} catch(Exception ex) {
			log.error("Error while searching for father schema" , ex);
			throw new BusinessException("Error while searching for father schema" , ex);
		}
		return output;
	}

	@Override
	public SchemaETY findByNameAndVersion(final String nameSchema, final String version) {
		SchemaETY output = null;
		try {
			Query query = new Query();
			query.addCriteria(Criteria.where("name_schema").is(nameSchema).and("version").is(version));
			output = mongoTemplate.findOne(query, SchemaETY.class);
		} catch(Exception ex) {
			log.error("Error while searching for find by name and version" , ex);
			throw new BusinessException("Error while searching for find by name and version" , ex);
		}
		return output;
	}
	
}
 		
