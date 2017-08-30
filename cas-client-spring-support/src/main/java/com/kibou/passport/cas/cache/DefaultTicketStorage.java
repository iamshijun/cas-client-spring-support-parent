package com.kibou.passport.cas.cache;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

import org.springframework.stereotype.Component;

@Component("defaultTicketStorage")
public class DefaultTicketStorage implements TicketStorage{

	private String keyPrefix = "cas_ticket_";
	
	private File tmpDirectory;
	
	public DefaultTicketStorage() {
		String tmpDir = System.getProperty("java.io.tmpdir");
		tmpDirectory = new File(tmpDir);
	}
	
	private String getTicketFileName(String key) {
		return keyPrefix + key;
	}
	
	@Override
	public String get(String key) {
		File ticketFile = new File(tmpDirectory,getTicketFileName(key));
		if(Files.exists(ticketFile.toPath())) {
			try {
				List<String> allLines = Files.readAllLines(ticketFile.toPath(),Charset.defaultCharset());
				if(allLines.size() > 0) {
					return allLines.get(0);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	@Override
	public void put(String key, String value) {
		
		Path ticketFilePath = new File(tmpDirectory,getTicketFileName(key)).toPath();
		
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
			
			ByteBuffer byteBuffer = ByteBuffer.wrap(value.getBytes());
			
			fileChannel.write(byteBuffer);
			
			System.out.println("Write complete. Closing the channel and releasing lock.");
			lock.release();
			//unlock is company with fileChannel.close()
		} catch (IOException e) {
			e.printStackTrace();
		}
		

	}
}
