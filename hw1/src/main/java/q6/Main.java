package q6;

import org.json.*;

import java.io.*;

public class Main {

    public static void main(String[] args){

        JSONObject wrapper = new JSONObject();

        for(int i = 1; i<=8; i++){

            JSONObject inner = new JSONObject();
            long startTime=System.currentTimeMillis();
            q6.Bakery.PIncrement.parallelIncrement(0, i);
            long endTime=System.currentTimeMillis();
            inner.put("Bakery", (endTime-startTime));

            startTime=System.currentTimeMillis();
            q6.AtomicInteger.PIncrement.parallelIncrement(0, i);
            endTime=System.currentTimeMillis();
            inner.put("AtomicInteger", (endTime-startTime));

            startTime=System.currentTimeMillis();
            q6.Synchronized.PIncrement.parallelIncrement(0, i);
            endTime=System.currentTimeMillis();
            inner.put("synchronized", (endTime-startTime));

            startTime=System.currentTimeMillis();
            q6.ReentrantLock.PIncrement.parallelIncrement(0, i);
            endTime=System.currentTimeMillis();
            inner.put("Reentrant Lock", (endTime-startTime));

            wrapper.put(i+" numThreads", inner);
        }
        System.out.println(wrapper.toString());

        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream("timeplot.json"), "utf-8"))) {
            writer.write(wrapper.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
