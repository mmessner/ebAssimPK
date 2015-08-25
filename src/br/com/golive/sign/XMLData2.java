package br.com.golive.sign;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Collections;

import javax.xml.crypto.KeySelector;
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

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.sun.corba.se.impl.orbutil.graph.Node;

public class XMLData2 {
	private InputStream in;

	private PrivateKey privKey;
	
	private PublicKey dsaPublicKey;
	
	public XMLData2(InputStream in) {
		this.in = in;
	}

	public boolean sign(String alias, OutputStream os) throws Exception{

		System.out.println("Iniciando assinatura digital");

		// get windows key store, its will be used to cert public key
		KeyStore ks = null;
		try {
			ks = KeyStore.getInstance("Windows-MY");
			// Note: When a security manager is installed,
			// the following call requires SecurityPermission
			// "authProvider.SunMSCAPI".
			ks.load(null, null);
		} catch (KeyStoreException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (CertificateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (ks == null) {
			System.out.println("key store nula.");
			return false;
		}

		// get private key from keystore
		privKey = (PrivateKey) ks.getKey(alias, null);

		// get certificate from keystore
		Certificate cert = null;
		try {
			cert = ks.getCertificate(alias);
			if (cert == null)
				throw new NullPointerException("Certificado não encontrado!");
		} catch (KeyStoreException e) {
			e.printStackTrace();
		}

		// get public key from certificate
		dsaPublicKey = cert.getPublicKey();

		// Deamon apenas para teste
		// Gera keys
		if (privKey == null || dsaPublicKey == null) {
			System.out.println("Private ou Public key nulas! Criando chaves DEAMON!");
			// Create a DSA KeyPair
			KeyPairGenerator kpg = KeyPairGenerator.getInstance("DSA");
			kpg.initialize(1024);
			KeyPair kp = kpg.generateKeyPair();

			privKey = kp.getPrivate();
			dsaPublicKey = kp.getPublic();
		}

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
		KeyValue kv = kif.newKeyValue(dsaPublicKey);

		// Create a KeyInfo and add the KeyValue to it
		KeyInfo ki = kif.newKeyInfo(Collections.singletonList(kv));

		// Instantiate the document to be signed
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		Document doc = dbf.newDocumentBuilder().parse(in);

		// Create a DOMSignContext and specify the DSA PrivateKey and
		// location of the resulting XMLSignature's parent element
		DOMSignContext dsc = new DOMSignContext(privKey, doc.getDocumentElement());

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

		// Validate
		DOMValidateContext valContext = new DOMValidateContext(dsaPublicKey,nl.item(0));		
		
		// Unmarshal the XMLSignature.
		XMLSignature signature = fac.unmarshalXMLSignature(valContext);	
		
		boolean isValid = signature.validate(valContext);
		return isValid;
	}
}