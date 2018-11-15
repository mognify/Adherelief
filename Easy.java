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

import java.util.Map;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class Easy {
 public static final boolean debug = true;
 // automatically creates alarms from pasted schedule

 public static void main(String[] args) {
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
  boolean yearGood = false;
  
  for(int i = 0; i < input.length(); i++){
   int b = 0;
   outln("Main loop count: " + i);
   
   
   // check if today's date
   if(input[i].contains("" + year) || yearGood){ // year check -> get breaks -> create timers
    yearGood = true; // the key to the kingdom is granted on first access
    
    if (input[i].contains("Break")) {
     b++;

     outln("\tBreak " + String.valueOf(b) + " identified: " + input[i]);

     breaks[b] = getBreak(input[i], false);
    } else if (input[i].contains("Lunch")) {
     outln("\tLunch identified: " + input[i]);
     lunch = getBreak(input[i], true);

     outln("Lunch found to be at " +
      String.valueOf(lunch / 60) + ":" + String.valueOf(lunch % 60));
    } else {
     continue;
    }
    
    schedule.put(day, new Integer[] {
     breaks[0], lunch,
      breaks[1]
    });

    openBrowserWindows(schedule.entrySet().iterator());
   }

   // UNNECESSARY
   /*String dayTemp = getDay(input[i]);
   if (!dayTemp.equals("[BAD]")) {
    day = dayTemp;
    outln("Variable changed: day to " + day);
    continue;
   }*/



  }
 }

 static private void openBrowserWindows(Iterator < ? > itr8r) {
  outln("openBrowserWindows start");
  while (itr8r.hasNext()) {
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

    /*
     * out of the sample, this produced
     * 12:00 AM Break1
     * 12:15 AM BrEnd1
     * 12:00 AM Lunch
     * 1:00AM LuEnd
     * 7:45PM Break2
     * 2:00AM BrEnd2
     *
     * Should be
     * 2:30 AM Break1
     * 12:15 AM BrEnd1
     * 12:00 AM Lunch
     * 1:00AM LuEnd
     * 7:45PM Break2
     * 2:00AM BrEnd2
     *
     * "Extends to next day"
     *
     * Get today's date
     * find matching day in schedule
     * get break lunch break (get starting time, start there)
     */
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

 // UNNECESSARY
 /*static private String getDay(String input) {
  if (input.contains("Monday|Tuesday|Wednesday|Thursday|Friday")) {
   String day = input.trim().substring(0, 6);

   outln(day + " identified.");

   return day;
  }

  return "[BAD]";
 }*/

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

 static public void browserTimer(String name, Integer time /*, boolean x*/ ) {
  outln("browserTimer start");
  String site = "\"https://vclock.com/#time=[a]&title=" + name + "&sound=glow&loop=1\"";
  String a = String.valueOf(time / 60) + ":" + String.valueOf(time % 60);

  if (a.length() < 5) a = "0" + a;
  if (a.length() < 5) a = a + "0";

  site = site.replace("[a]", a); // add the break time into the alarm
  outln("browserTimer: Trying " + site);

  try {
   Runtime.getRuntime().exec(new String[] {
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
