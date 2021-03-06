package org.simpleframework.http.core;

import java.io.IOException;

import org.simpleframework.http.core.FixedProducer;

import junit.framework.TestCase;

public class FixedProducerTest extends TestCase {

   public void testContent() throws IOException {
      testContent(1024, 1);
      testContent(1024, 2);
      testContent(512, 20);
      testContent(64, 64);
   }
   
   public void testContent(int chunkSize, int count) throws IOException {
      MockSender sender = new MockSender((chunkSize * count) + chunkSize);
      MockMonitor monitor = new MockMonitor();
      FixedProducer producer = new FixedProducer(sender, monitor, chunkSize * count);
      byte[] chunk = new byte[chunkSize];
      
      for(int i = 0; i < chunk.length; i++) {
         chunk[i] = (byte)String.valueOf(i).charAt(0);
      }
      for(int i = 0; i < count; i++) {
         producer.produce(chunk, 0, chunkSize);
      }
      producer.close();
      
      System.err.println(sender.getBuffer().encode());

      assertTrue(monitor.isReady());
      assertFalse(monitor.isError());
      assertFalse(monitor.isClose());
      
      sender = new MockSender((chunkSize * count) + chunkSize);
      monitor = new MockMonitor();
      producer = new FixedProducer(sender, monitor, chunkSize * count);
      
      for(int i = 0; i < count; i++) {
         producer.produce(chunk, 0, chunkSize);
      }
      producer.close();
      
      assertFalse(monitor.isError());
      assertTrue(monitor.isReady());
   }
}