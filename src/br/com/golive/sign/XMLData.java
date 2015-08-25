package br.com.golive.sign;

import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyStoreException;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.util.Collections;

import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.KeyValue;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import br.com.golive.log.LogUtil;
import br.com.golive.sign.ks.core.Keystore;
import br.com.golive.sign.ks.core.KeystoreException;
import br.com.golive.sign.ks.core.KeystoreFactory;
import br.com.golive.sign.ks.core.Keystores;

public class XMLData {
	private static Logger log = LogUtil.getLogInstance(XMLData.class.getName());
	
	private InputStream in;

	private Keystore ks;
	
	public XMLData(InputStream in) {
		this.in = in;
	}


	/**
	 * Not used anymore
	 * 
	 * @param alias
	 * @param os
	 * @return
	 * @throws Exception
	 */
	public boolean sign(String alias, OutputStream os) throws Exception{

		System.out.println("Iniciando assinatura digital");
		
		// obtain keystore implementation and load keys!
		// Fixed keystore type! MSWindows for tokens !!!
		ks = KeystoreFactory.getKeystoreIstance(Keystores.MSWINDOWS);
		ks.load(alias);
		
		// Create a DOM XMLSignatureFactory that will be used to generate the
		// enveloped signature
		XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM");

		// Create a Reference to the enveloped document (in this case we are
		// signing the whole document, so a URI of "" signifies that) and
		// also specify the SHA1 digest algorithm and the ENVELOPED Transform.
		Reference ref = fac.newReference("", fac.newDigestMethod(
				DigestMethod.SHA1, null), 
				Collections.singletonList(fac.newTransform(Transform.ENVELOPED,
				(TransformParameterSpec) null)), null, null);

		// Create the SignedInfo
		SignedInfo si = fac.newSignedInfo(fac.newCanonicalizationMethod(
						CanonicalizationMethod.INCLUSIVE_WITH_COMMENTS,
						(C14NMethodParameterSpec) null), 
						fac.newSignatureMethod(SignatureMethod.DSA_SHA1, null),
						Collections.singletonList(ref)
						);

		// Create a KeyValue containing the DSA PublicKey that was generated
		KeyInfoFactory kif = fac.getKeyInfoFactory();
		KeyValue kv = kif.newKeyValue(ks.getPublicKey());

		// Create a KeyInfo and add the KeyValue to it
		KeyInfo ki = kif.newKeyInfo(Collections.singletonList(kv));

		// Instantiate the document to be signed
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		Document doc = dbf.newDocumentBuilder().parse(in);

		// Create a DOMSignContext and specify the DSA PrivateKey and
		// location of the resulting XMLSignature's parent element
		DOMSignContext dsc = new DOMSignContext(ks.getPrivKey(), doc.getDocumentElement());

		// Create the XMLSignature (but don't sign it yet)
		XMLSignature signature = fac.newXMLSignature(si, ki);

		// Marshal, generate (and sign) the enveloped signature
		signature.sign(dsc);

		// copia stream
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer trans = tf.newTransformer();
		trans.transform(new DOMSource(doc), new StreamResult(os));			
		
		System.out.println("Documento assinado com sucesso");

		return true;
	}
	
	/**
	 * Receive a data stream, sign it and put the result in a defined OutputStream.
	 * 
	 * @param alias
	 * @param os
	 * @return
	 * @throws Exception
	 */
	public boolean sign(Keystores type,String alias, OutputStream os) throws XMLSignatureException{
		log.debug("Inicio metodo sign");
		
		// obtain keystore implementation and load keys!
		// Keytore type passed as parameter!
		try {
			ks = KeystoreFactory.getKeystoreIstance(type);
			ks.load(alias);
		} catch (KeystoreException e) {
			throw new XMLSignatureException(e);
		} catch (KeyStoreException e) {
			throw new XMLSignatureException(e);
		} catch (CertificateException e) {
			throw new XMLSignatureException(e);
		}
		
		try{
		
			// Create a DOM XMLSignatureFactory that will be used to generate the
			// enveloped signature
			XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM");
	
			// Create a Reference to the enveloped document (in this case we are
			// signing the whole document, so a URI of "" signifies that) and
			// also specify the SHA1 digest algorithm and the ENVELOPED Transform.
			Reference ref = fac.newReference("", fac.newDigestMethod(
					DigestMethod.SHA1, null), 
					Collections.singletonList(fac.newTransform(Transform.ENVELOPED,
					(TransformParameterSpec) null)), null, null);
	
			// Create the SignedInfo
			SignedInfo si = fac.newSignedInfo(fac.newCanonicalizationMethod(
							CanonicalizationMethod.INCLUSIVE_WITH_COMMENTS,
							(C14NMethodParameterSpec) null), 
							fac.newSignatureMethod(SignatureMethod.DSA_SHA1, null),
							Collections.singletonList(ref)
							);
	
			// Create a KeyValue containing the DSA PublicKey that was generated
			KeyInfoFactory kif = fac.getKeyInfoFactory();
			KeyValue kv = kif.newKeyValue(ks.getPublicKey());
	
			// Create a KeyInfo and add the KeyValue to it
			KeyInfo ki = kif.newKeyInfo(Collections.singletonList(kv));
	
			// Instantiate the document to be signed
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			Document doc = dbf.newDocumentBuilder().parse(in);
	
			// Create a DOMSignContext and specify the DSA PrivateKey and
			// location of the resulting XMLSignature's parent element
			DOMSignContext dsc = new DOMSignContext(ks.getPrivKey(), doc.getDocumentElement());
	
			// Create the XMLSignature (but don't sign it yet)
			XMLSignature signature = fac.newXMLSignature(si, ki);
	
			// Marshal, generate (and sign) the enveloped signature
			signature.sign(dsc);
	
			// copia stream
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer trans = tf.newTransformer();
			trans.transform(new DOMSource(doc), new StreamResult(os));			
			
			log.debug("Documento assinado com sucesso");
		}catch(Exception e){
			throw new XMLSignatureException("Erro ao assinar documento. " + e + e.getMessage());
		}
		
		log.debug("Fim metodo sign");
		return true;
	}	
	
	/**
	 * Not used anymore
	 * 
	 * @param in
	 * @param alias
	 * @return
	 * @throws Exception
	 */
	public boolean verify(InputStream in, String alias)throws Exception{
		this.in = in;	
		
		// Create a DOM XMLSignatureFactory that will be used to generate the
		// enveloped signature
		XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM");

		// Instantiate the document to be signed
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		Document doc = dbf.newDocumentBuilder().parse(in);	
		
		// FROM  http://www.j2ee.me/webservices/docs/2.0/tutorial/doc/XMLDigitalSignatureAPI8.html
		
		// Get element contening signature
		NodeList nl = doc.getElementsByTagNameNS(XMLSignature.XMLNS, "Signature");
		if (nl.getLength() == 0) {
		  throw new Exception("Cannot find Signature element");
		} 

		// Get public key. Used to validate signature
		ks = KeystoreFactory.getKeystoreIstance(Keystores.MSWINDOWS);
		ks.load(alias);
		
		PublicKey dsaPublicKey = ks.getPublicKey();
		
		// Create validation context
		DOMValidateContext valContext = new DOMValidateContext(dsaPublicKey,nl.item(0));		
		
		// Unmarshal the XMLSignature.
		XMLSignature signature = fac.unmarshalXMLSignature(valContext);	
		
		// Validade signature.
		boolean isValid = signature.validate(valContext);
		return isValid;
	}	
	
	/**
	 * Verify Signature.
	 * 
	 * @param type
	 * @param alias
	 * @param in
	 * @return
	 * @throws XMLSignatureException
	 */
	public boolean verify(Keystores type, String alias, InputStream in) throws XMLSignatureException{
		log.debug("Inicio metodo verify");
		
		boolean isValid = false;
		
		// obtain keystore implementation and load keys!
		// Keytore type passed as parameter!
		try {
			ks = KeystoreFactory.getKeystoreIstance(type);
			ks.load(alias);
		} catch (KeystoreException e) {
			throw new XMLSignatureException(e);
		} catch (KeyStoreException e) {
			throw new XMLSignatureException(e);
		} catch (CertificateException e) {
			throw new XMLSignatureException(e);
		}		

		this.in = in;	
		try{
			// Create a DOM XMLSignatureFactory that will be used to generate the
			// enveloped signature
			XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM");
	
			// Instantiate the document to be signed
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			Document doc = dbf.newDocumentBuilder().parse(in);	
			
			// FROM  http://www.j2ee.me/webservices/docs/2.0/tutorial/doc/XMLDigitalSignatureAPI8.html
			
			// Get element contening signature
			NodeList nl = doc.getElementsByTagNameNS(XMLSignature.XMLNS, "Signature");
			if (nl.getLength() == 0) {
			  throw new Exception("Cannot find Signature element");
			} 
			
			PublicKey dsaPublicKey = ks.getPublicKey();
			
			// Create validation context
			DOMValidateContext valContext = new DOMValidateContext(dsaPublicKey,nl.item(0));		
			
			// Unmarshal the XMLSignature.
			XMLSignature signature = fac.unmarshalXMLSignature(valContext);	
			
			// Validade signature.
			isValid = signature.validate(valContext);
			
			log.debug("Resultado=" + isValid);
		}catch(Exception e){
			throw new XMLSignatureException("Erro ao verificar documento. " + e + e.getMessage());
		}
		
		log.debug("Fim metodo verify");
		return isValid;
	}		
	
}