package it.finanze.sanita.fse2.ms.gtw.validator.utility;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.Base64;

import org.apache.commons.codec.binary.Hex;

import it.finanze.sanita.fse2.ms.gtw.validator.exceptions.BusinessException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class StringUtility {

	/**
	 * Private constructor to avoid instantiation.
	 */
	private StringUtility() {
		// Constructor intentionally empty.
	}

	/**
	 * Returns {@code true} if the String passed as parameter is null or empty.
	 * 
	 * @param str	String to validate.
	 * @return		{@code true} if the String passed as parameter is null or empty.
	 */
	public static boolean isNullOrEmpty(final String str) {
	    boolean out = false;
		if (str == null || str.isEmpty()) {
			out = true;
		}
		return out;
	}

	/**
	 * Returns the encoded String of the SHA-256 algorithm represented in base 64.
	 * 
	 * @param objectToEncode String to encode.
	 * @return String Encoded.
	 */
	public static String encodeSHA256B64(String objectToEncode) {
		try {
		    final MessageDigest digest = MessageDigest.getInstance("SHA-256");
		    final byte[] hash = digest.digest(objectToEncode.getBytes());
		    return encodeBase64(hash);
		} catch (Exception e) {
			log.error("Errore in fase di calcolo sha", e);
			throw new BusinessException("Errore in fase di calcolo SHA-256", e);
		}
	}
	
	/**
	 * Returns the encoded String of the SHA-256 algorithm encoded represented in base hex.
	 * 
	 * @param objectToEncode String to encode.
	 * @return String Encoded.
	 */
	public static String encodeSHA256Hex(String objectToEncode) {
		try {
		    final MessageDigest digest = MessageDigest.getInstance("SHA-256");
		    final byte[] hash = digest.digest(objectToEncode.getBytes());
		    return encodeHex(hash);
		} catch (Exception e) {
			log.error("Errore in fase di calcolo sha", e);
			throw new BusinessException("Errore in fase di calcolo SHA-256", e);
		}
	}

	/**
	 * Encode in Base64 the byte array passed as parameter.
	 * 
	 * @param input	The byte array to encode.
	 * @return		The encoded byte array to String.
	 */
	public static String encodeBase64(final byte[] input) {
		return Base64.getEncoder().encodeToString(input);
	}

	/**
	 * Encodes the byte array passed as parameter in hexadecimal.
	 * 
	 * @param input	The byte array to encode.
	 * @return		The encoded byte array to String.
	 */
	public static String encodeHex(final byte[] input) {
		return Hex.encodeHexString(input);
	}
	
	/**
	 * Get filename from complete path.
	 * 
	 * @param completePath	path
	 * @return				filename
	 */
	public static String getFilename(final String completePath) {
		String output = "";
		try {
			Path path = Paths.get(completePath);
			output = path.getFileName().toString(); 
		} catch(Exception ex) {
			log.error("Error to get filename from complete path " , ex);
			throw new BusinessException("Error to get filename from complete path " , ex);
		}
		return output;
	}
	
	public static String sanitizeCDA(String cda) {
		return cda.replaceAll("<!DOCTYPE[^<>]*(?:<!ENTITY[^<>]*>[^<>]*)+>", "");
	}
	
	/**
	 * Metodo che permette data l'uri definita nelle prop di avere il nome del db
	 * 
	 * @param uri
	 * @return string
	 */
	public static String getDatabaseName(final String uri) { 
		int indexDBName = uri.lastIndexOf("/");
		String nameWithReplica = uri.substring(indexDBName+1, uri.length()).trim();
		if(nameWithReplica.contains("?")) {
			nameWithReplica = nameWithReplica.substring(0, nameWithReplica.indexOf('?')).trim();
		}
		return nameWithReplica;
	}
 }
