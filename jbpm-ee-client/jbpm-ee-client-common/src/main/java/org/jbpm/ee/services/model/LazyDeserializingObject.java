package org.jbpm.ee.services.model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import javax.xml.bind.DatatypeConverter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Jaxb, serializable map implementation.
 * Lazily deserializes values in order to support server side classloader setup prior to deserialization.
 * 
 * @author bradsdavis
 *
 */
public class LazyDeserializingObject implements LazyDeserializing<Serializable> {

	private static final Logger LOG = LoggerFactory.getLogger(LazyDeserializingObject.class);
	private byte[] lazyObj;
	private String lazyObjType;
	
	private Serializable obj;
	
	public LazyDeserializingObject() throws Exception {

	}

	public LazyDeserializingObject(Serializable obj) throws Exception {
		this.obj = obj;
	}
	
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		try {
			if(lazyObj != null && lazyObj.length > 0) {
				out.writeUTF(lazyObjType);
				String s = DatatypeConverter.printBase64Binary(lazyObj);
				out.writeUTF(s);
			}
			else {
				String objectType = obj.getClass().getName();
				out.writeUTF(objectType);

				ObjectOutputStream os = null;
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				os = new ObjectOutputStream(baos);
				os.writeObject(obj);
				os.close();
				
				byte[] bytes = baos.toByteArray();
				String s = DatatypeConverter.printBase64Binary(bytes);
				out.writeUTF(s);
				LOG.debug("Array on Write Converted: "+s);
				
				String str = new String(bytes);
				LOG.debug("Array on Write: "+str);
			}
		} catch (Exception e) {
			throw new IOException("Exception marshalling map.", e);
		}
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		this.lazyObjType = in.readUTF();
		String utfObj = in.readUTF();
		this.lazyObj = DatatypeConverter.parseBase64Binary(utfObj);
		
		LOG.debug("Array on Read Converted: "+utfObj);
		
		String str = new String(this.lazyObj);
		LOG.debug("Array on Read: "+str);
	}

	public void initializeLazy(ClassLoader classloader) throws IOException {
		LOG.debug("Lazily object: "+this.lazyObjType);
		try {
			ByteArrayInputStream bais = new ByteArrayInputStream(this.lazyObj);
			LazyDeserializingObjectInputStream ldois = new LazyDeserializingObjectInputStream(bais);
			this.obj = (Serializable)ldois.readObject();
		}
		catch(Exception e) {
			throw new IOException("Exception reading class.", e);
		}
	}

	@Override
	public Serializable getDelegate() {
		return this.obj;
	}

}
