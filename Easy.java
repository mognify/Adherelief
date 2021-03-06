// finished: gathering the information from paste
// next: create timers
// will use https://vclock.com/#time=17:59&title=Alarm&sound=glow&loop=1
// for the alarms
// need to create getPaste()
// (need to test still)
// i also want this program to open up other applications
// service-now, remote tool, itsd tools, slack, 
// http://webapps.homedepot.com/itsd/dashboard/
// https://thdengops.slack.com/
// https://workforce.homedepot.com/wfo/control/showadherence
// http://webapps.homedepot.com/itsd/dashboard/
// TODO: SPECIAL CASE: OFF DAYS

import java.io.IOException;
import java.awt.Desktop;
import java.net.URI;
import java.time.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Map;
import java.util.Date;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class Easy {
 public static final boolean debug = true;
 // automatically creates alarms from pasted schedule

 public static void main(String[] args) {
  LocalDate today = LocalDate.now();
  today.plusDay(1);
  String y = "YYYY-MM-DD".split("-")[0]; 
  String d = today.split("-")[2];
  
  final boolean methodA = true;

  // day, lunch time(s), breaks\
  HashMap < String, Integer[] > schedule = new HashMap < String, Integer[] > ();
  String[] input = getPaste();

  String day = "";
  int lunch = 0;
  Integer[] breaks = new Integer[] {
   0,
   0
  };
  outln("Variables initialized: day, lunch, breaks");
  
  // get today's date
  // check if today's date
  // if not, skip
  DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
  Date date = new Date();
  int today = Integer.valueOf(dateFormat.format(date).split("/")[0]).intValue();
  int year = Integer.valueOf(dateFormat.format(date).split("/")[2].split(" ")[0]).intValue();
  outln("Today is " + today);
  outln("Year is " + year);
  boolean yearGood = false, dayGood = false;
  
  for(int i = 0, complete = 3; i < input.length && complete > 0; i++){
   int b = 0;
   outln("Main loop count: " + i);
   
   // check if today's date
   if(input[i].contains("" + year) || (dayGood && yearGood)){ // year check -> get breaks -> create timers
    if(!yearGood){
     if(input[i+1].contains("Off")) continue; // so what happens if the program is tested on an off day?
     int t = input[i].length();
     int d = Integer.valueOf(input[i].substring(t-9).split(",")[0]).intValue();
     dayGood = (d == today);
    }
    yearGood = true; // the key to the kingdom is granted on first access
    
    // get all 3 breaks
    if (input[i].contains("Break")) {
     // 15 minute break
     complete--; // here to make sure only the 3 breaks are checked for
     b++; // break counter, to differentiate break 1 from break 2

     outln("\tBreak " + String.valueOf(b) + " identified: " + input[i]);

     breaks[b] = getBreak(input[i], false);
    } else if (input[i].contains("Lunch")) {
     // lunch break
     complete--; // again, just checking to make sure only the 3 breaks are retrieved
     outln("\tLunch identified: " + input[i]);
     
     // TODO: not everyone's lunch is going to be 60 minutes
     // so check 
     // lunch length, if 2 lunches (if their lunch is at 23:45 and they come back at 00:15, they will have 2 lunch 
     lunch = getBreak(input[i], true);

     outln("Lunch found to be at " +
      String.valueOf(lunch / 60) + ":" + String.valueOf(lunch % 60)); // get lunch break
    } else {
     continue;
    }
   }
   
   outln("complete is 0, schedule identification complete");
    
   schedule.put(day, new Integer[] {
    breaks[0], lunch, breaks[1]
   });

   openBrowserWindows((Iterator<?>)schedule.entrySet().iterator()); // create timers

   outln("Main ends");
   
   outln("Program ends");
   exit(0);
  }
 }

 static private void openBrowserWindows(Iterator<?> itr8r) {
  outln("openBrowserWindows start");
  while (itr8r.hasNext()) {
   @SuppressWarnings (value="unchecked")
   Map.Entry < String, Integer[] > pair = (Map.Entry < String, Integer[] > ) itr8r.next();
   String dayy = pair.getKey(); // CHECK
   if (dayy.contains("[BAD]")) return;
   Integer[] times = pair.getValue(); // break1, lunch, break2
   outln(pair.getKey() + " = " + pair.getValue()[0]);

   for (int j = 0; j < times.length; j++) {
    if (j % 2 == 0) {
     outln("Break" + ((j / 2) + 1) + ": " + times[j]);
     browserTimer("Break" + ((j / 2) + 1), times[j]);
     outln("BrEnd" + ((j / 2) + 1) + ": " + (times[j] + 15));
     browserTimer("BrEnd" + ((j / 2) + 1), times[j] + 15);
    } else {
     outln("Lunch" + j + ": " + times[j]);
     browserTimer("Lunch", times[j]);
     outln("LuEnd" + j + ": " + times[j] + 60);
     browserTimer("LuEnd", (times[j] + 60));
    }
   }
  }
  try {
   TimeUnit.SECONDS.sleep(3);
  } catch (InterruptedException e) {
   e.printStackTrace();
  }

  outln("openBrowserWindows end");
  itr8r.remove();

  return;
 }

 static private String[] getPaste() {
  outln("getPaste() begin");
  Scanner inputScanner = new Scanner(System.in);

  outln("Variable initialized: inputScanner");
  outln("Variable declared: inputScanner as new Scanner object");

  System.out.println("How many lines?");
  String[] s = new String[inputScanner.nextInt()];

  System.out.println("Paste the CtrlA schedule");

  for (int i = 0; i < s.length; i++) {
   s[i] = inputScanner.nextLine();
  }

  inputScanner.close();

  outln("Variable changed: inputScanner closed");
  outln("Finished retrieving lines.");
  outln("getPaste() end");

  return s;
 }

 // time will be stored in minutes
 static private int getBreak(String s, boolean lunch) {
  outln("getBreak() start");
  int breakTime = 0;
  outln("Variable initialized: breakTime");

  //String work = lunch ? s.split("h", 2)[1] : s.split("k", 1)[1];
  String work = lunch ? s.substring(s.indexOf('h') + 1) : s.substring(s.indexOf('k') + 1);

  outln("Variable initialized: work");
  outln("Variable changed: work to " + work);
  String fix1 = work.split(":")[0];
  fix1 = fix1.contains(" ") ? fix1.split(" ")[1] : fix1;
  breakTime += (60 * Integer.valueOf(fix1).intValue()) +
   Integer.valueOf(work.split(":")[1].split(" ")[0]).intValue();
  outln("Variable changed: breakTime to " +
   String.valueOf(breakTime));
  if (s.split(":", 2)[1].contains("PM")) {
   outln("PM identified, adding 12*60 minutes");
   breakTime += 12 * 60;
   outln("Variable changed: breakTime to " + breakTime);
  }
  outln("getBreak() end");
  return breakTime;
 }

 static private void outln(String s) {
  if (debug)
   System.out.println("DEBUG: " + s);
 }

 static public void browserTimer(String name, Integer time) {
  outln("browserTimer start");
  String site = "\"https://vclock.com/#time=[a]&title=" + name + "&sound=glow&loop=1\"";
  String a = String.valueOf(time / 60) + ":" + String.valueOf(time % 60);

  if (a.length() < 5) a = "0" + a;
  if (a.length() < 5) a = a + "0";

  site = site.replace("[a]", a); // add the break time into the alarm
  outln("browserTimer: Trying " + site);

  try {
   Runtime.getRuntime().exec(new String[]{
    "cmd",
    "/c",
    "start chrome " + site
   });
  } catch (IOException e) {
   e.printStackTrace();
  }
  try {
   TimeUnit.SECONDS.sleep(1);
  } catch (InterruptedException e) {
   e.printStackTrace();
  }

  outln("browserTimer end");
  return;
 }
}
