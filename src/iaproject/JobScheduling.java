package iaproject;

import com.google.common.collect.ArrayListMultimap;
import java.io.*;
import java.util.*;
import javafx.util.Pair;
import com.google.common.collect.Multimap;
import java.io.File;


public class JobScheduling 
{
   private static final Comparator<Pair> COMPJOBS;
   private List<Pair<Integer,Integer>> alStartOrg;       

//implementing custom comparator.
    static
    {
        COMPJOBS = new Comparator<Pair>()
        { 
            @Override
            public int compare(Pair o1, Pair o2) 
            {
                if(o1.getValue()==o2.getValue())                                                            // if the finish times are similar sort on the basis of start time
                {
                    int compKey = (Integer)o2.getKey();
                    return (Integer)o1.getKey() - compKey;                                                  // sort in ascending order by comparing start times
                }
                else
                {
                    return (Integer)o1.getValue() - (Integer)o2.getValue();                                 // sort in ascending order by comparing finish times
                }
            }
        };
    }
     
    JobScheduling()
    {
              alStartOrg =new ArrayList<>();
    }
//Reading input.txt file    
    public void readInput(String filename)
    { 
       String strLine ="";
       String tokens[] = null;                                                                              // store the tokenized lined from the file
       int jobs=0 ;                                                                                         // store no. of jobs read from the file      
       ArrayList<Pair<Integer,Integer>> alStart = new ArrayList();                                          // store jobs as <key,Value> pair (Key: Start time, Value: finish time)
       
       int machines=0;                                                                                      // store no. of machines read from the file.
       int lineno = 0;                                                                                      // count line no. of the file to be read.
       
       try 
        {
            FileReader fileRead = new FileReader(filename);                                             // read the file with name "filename"
            BufferedReader br=new BufferedReader(fileRead);                                             // store the contents of the file in bufffer
            
            while((strLine = br.readLine())!=null)                                                      // read each line from buffer and stop if it is null
            {
               tokens = strLine.split(" ");                                                             // split the line and store it in tokens array
               if(lineno!=0)
               {
                  if(Integer.parseInt(tokens[0]) < Integer.parseInt(tokens[1]))                         // check for eligible jobs i.e. job.start_time < job.finish_time
                  {
                    alStart.add(new Pair<>(Integer.parseInt(tokens[0]),Integer.parseInt(tokens[1])));   // add the job to the arraylist as new <key,value> pair
                  } 
                  else
                  {
                    alStart.add(new Pair<>(Integer.MIN_VALUE,Integer.MAX_VALUE));                      // add job with key as least int value and value as max int value
                    jobs--;
                  }  
               }
               else                                                                                   // for first line of file.
               {
                    jobs = Integer.parseInt(tokens[0]);                 
                    machines = Integer.parseInt(tokens[1]); 
                    if(jobs<machines)                                                                // check if jobs > machines
                    {
                        System.out.println("Exiting...");
                        System.exit(0);                                                             // exit if true.
                    }
                    lineno++;
               }
            }
            fileRead.close();
            br.close();
        } 
        catch (IOException ex) 
        {
            ex.printStackTrace();
        } 
        alStartOrg.addAll(alStart);                                                                 // storing original job order in a seperate arraylist
        Collections.sort(alStart, COMPJOBS);                                                        //Sorting the jobs according to their fisnish times usig custom comparator.
        System.out.println(alStart);
        this.findOptJobs(jobs,machines,alStart);                                                    // call method to find optimal jobs that can be assigned.
    }
    
//For finding optimal jobs
    public void findOptJobs(int n,int m,ArrayList<Pair<Integer,Integer>> alJobs)
    { 
        //i jobs 
        //j machines
        
        Multimap<Integer,Integer> jobList = ArrayListMultimap.create();                             // Multimap to store which machine has which job no.
        int jobsallocated=0;                                                                        // to count total no. of jobs allocated.
        int[] mac = new int[m];
        int job_no=0;                                                                               // array to store the machines.   
        boolean processed;
        Pair p;
        int idle_time = 0;
        int mac_index=0;
        for(int i=0;i<n;i++)
        {
            mac_index=0;
            idle_time=Integer.MAX_VALUE;
            processed = false;
            for(int j=0;j<m;j++)
            {  //System.out.println("J:"+j);    
                if(mac[j] <= alJobs.get(i).getKey())                                                // Check if finish time of job in machine mj < start time of the next job ni.
                {
                    //System.out.println("J1:"+j);
                    if(idle_time > Math.abs(mac[j] - alJobs.get(i).getKey() ))
                    {
                        idle_time = Math.abs(mac[j] - alJobs.get(i).getKey());
                        mac_index=j;
                    }   
                    processed = true;
                }
            }
            //System.out.println("After Index:"+mac_index);
            if(processed)
            {
                mac[mac_index]= alJobs.get(i).getValue();
                System.out.println(mac_index+":"+alJobs.get(i));// store the finish time of job ni assigned to machine mj. 
                p=alJobs.get(i);
                job_no=alStartOrg.indexOf(p);                                                   // take the index value of assigned job from original array list of jobs
                jobList.put((mac_index+1),(job_no+1));                                                  // store the machine no. and job no. assigned to it. 
                jobsallocated++;
            }    
        }
       this.writeOPFile(jobsallocated,m,jobList);                                                   // call method to write to file, the 
    } 

//Writing O/P to file Output.txt
    public void writeOPFile(int noofJobs,int m,Multimap jobMap) 
    {
        String str="";
        File file = new File ("C:/Users/Sunit/Desktop/output.txt");                                 // Create new output file
        PrintWriter pw;
        try 
        {
            pw=new PrintWriter(file);           
            pw.println("Total no. of jobs allocated: "+noofJobs);                                   // Print total no. of jobs allocated on the first line of O/P file.
            pw.flush();
            for(int j=0;j<m;j++)                                                                    // Loop through the multimap to get the jobs nos allocated to machine mj.
                {
                    str = jobMap.get(j+1).toString();                                               // get the job nos assigned to machine mj and store it in str.
                    pw.println("Machine:"+(j+1)+" allocated jobs: "+str);                           // Print the jobs allocated to machine mj in the O/P file.
                    pw.flush();                                         
                }
            pw.close();
        } 
        catch (IOException ex) 
        {
            ex.printStackTrace();
        }   
        
    }
    
    //Main method
    public static void main(String[] args) 
    {
        new JobScheduling().readInput("input4.txt");                                        // create object of class and call readInput method to read contents from file input.txt
    } 
}
