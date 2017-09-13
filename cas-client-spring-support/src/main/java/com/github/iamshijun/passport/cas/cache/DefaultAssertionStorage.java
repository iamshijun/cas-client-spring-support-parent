package com.github.iamshijun.passport.cas.cache;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import org.jasig.cas.client.validation.Assertion;
import org.springframework.stereotype.Component;

import com.github.iamshijun.passport.serializer.JdkObjectSerializer;
import com.github.iamshijun.passport.serializer.ObjectSerializer;

/**
 * File based assertion storage
 * @author aimysaber@gmail.com
 *
 */
@Component("defaultTicketStorage")
public class DefaultAssertionStorage implements AssertionStorage{

	private String keyPrefix = "cas_assertion_";
	
	private ObjectSerializer<Object> serializer;
	
	public void setSerializer(ObjectSerializer<Object> serializer) {
		this.serializer = serializer;
	}
	
	private File tmpDirectory;
	
	public DefaultAssertionStorage() {
		String tmpDir = System.getProperty("java.io.tmpdir");
		tmpDirectory = new File(tmpDir);
		serializer = new JdkObjectSerializer();
	}
	
	private String getStoreFileName(String key) {
		return keyPrefix + key;
	}
	
	@Override
	public Assertion get(String key) {
		File ticketFile = new File(tmpDirectory,getStoreFileName(key));
		if(Files.exists(ticketFile.toPath())) {
			try(FileInputStream fis = new FileInputStream(ticketFile);){
				
				byte[] buffer = new byte[fis.available()];
				fis.read(buffer);
				
				return (Assertion) serializer.deserialize(buffer);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return null;
	}
	
	@Override
	public void put(String key, Assertion assertion) {
		
		Path ticketFilePath = new File(tmpDirectory,getStoreFileName(key)).toPath();
		
		//create if not exists and lock when we do write op
		if(!Files.exists(ticketFilePath)) {
			try {
				Files.createFile(ticketFilePath);
			} catch (OverlappingFileLockException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		try(FileChannel fileChannel = FileChannel.open(ticketFilePath, StandardOpenOption.WRITE/*, StandardOpenOption.APPEND*/);) {
			FileLock lock = fileChannel.lock();
			
			byte[] serialize = serializer.serialize(assertion);
			
			ByteBuffer byteBuffer = ByteBuffer.wrap(serialize);
			
			fileChannel.write(byteBuffer);
			
			System.out.println("Write complete. Closing the channel and releasing lock.");
			lock.release();
			//unlock is company with fileChannel.close()
		} catch (IOException e) {
			e.printStackTrace();
		}
		

	}
}
